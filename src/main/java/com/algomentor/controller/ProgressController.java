package com.algomentor.controller;

import com.algomentor.dto.StudentProgressDTO;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import com.algomentor.service.ProgressTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {
    
    @Autowired
    private ProgressTrackingService progressTrackingService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Get progress summary for all students (TEACHER ONLY)
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<StudentProgressDTO>> getAllStudentsProgress() {
        List<StudentProgressDTO> progress = progressTrackingService.getAllStudentsProgress();
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Get progress for the logged-in student (STUDENT ONLY)
     */
    @GetMapping("/my-progress")
    public ResponseEntity<StudentProgressDTO> getMyProgress(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        String userRole = (String) request.getAttribute("userRole");
        
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        
        if ("TEACHER".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
        
        // Find student by email
        Student student = studentRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        StudentProgressDTO progress = progressTrackingService.getStudentProgress(student);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Sync HackerRank progress for a student
     * Students can only sync their own progress
     */
    @PostMapping("/hackerrank/{studentId}")
    public ResponseEntity<?> syncHackerRankProgress(
            @PathVariable Long studentId,
            HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            String userRole = (String) request.getAttribute("userRole");
            
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }
            
            // Students can only sync their own progress
            if ("STUDENT".equals(userRole)) {
                Student student = studentRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("Student not found"));
                if (!student.getId().equals(studentId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only sync your own progress"));
                }
            }
            
            StudentProgressDTO progress = progressTrackingService.syncHackerRankProgress(studentId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error syncing HackerRank progress: " + e.getMessage()));
        }
    }
    
    /**
     * Sync LeetCode progress for a student
     * Students can only sync their own progress
     */
    @PostMapping("/leetcode/{studentId}")
    public ResponseEntity<?> syncLeetCodeProgress(
            @PathVariable Long studentId,
            HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            String userRole = (String) request.getAttribute("userRole");
            
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }
            
            // Students can only sync their own progress
            if ("STUDENT".equals(userRole)) {
                Student student = studentRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("Student not found"));
                if (!student.getId().equals(studentId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only sync your own progress"));
                }
            }
            
            StudentProgressDTO progress = progressTrackingService.syncLeetCodeProgress(studentId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error syncing LeetCode progress: " + e.getMessage()));
        }
    }
    
    /**
     * Sync both HackerRank and LeetCode progress for a student
     */
    @PostMapping("/sync-all/{studentId}")
    public ResponseEntity<?> syncAllProgress(@PathVariable Long studentId, HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            String userRole = (String) request.getAttribute("userRole");
            
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }
            
            // Students can only sync their own progress
            if ("STUDENT".equals(userRole)) {
                Student student = studentRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("Student not found"));
                if (!student.getId().equals(studentId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only sync your own progress"));
                }
            }
            
            // Get student to check profiles
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            boolean hasHackerRank = student.getHackerrankProfile() != null && !student.getHackerrankProfile().trim().isEmpty();
            boolean hasLeetCode = student.getLeetcodeProfile() != null && !student.getLeetcodeProfile().trim().isEmpty();
            
            if (!hasHackerRank && !hasLeetCode) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Please configure your HackerRank and/or LeetCode username in your profile first"));
            }
            
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();
            
            // Try HackerRank if configured
            if (hasHackerRank) {
                try {
                    progressTrackingService.syncHackerRankProgress(studentId);
                    successMsg.append("HackerRank synced successfully. ");
                } catch (Exception e) {
                    errorMsg.append("HackerRank: ").append(e.getMessage()).append(". ");
                }
            }
            
            // Try LeetCode if configured
            if (hasLeetCode) {
                try {
                    progressTrackingService.syncLeetCodeProgress(studentId);
                    successMsg.append("LeetCode synced successfully. ");
                } catch (Exception e) {
                    errorMsg.append("LeetCode: ").append(e.getMessage()).append(". ");
                }
            }
            
            // Get updated progress
            StudentProgressDTO progress = progressTrackingService.getAllStudentsProgress().stream()
                    .filter(p -> p.getStudentId().equals(studentId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // If we have any success, return the progress
            if (successMsg.length() > 0) {
                return ResponseEntity.ok(progress);
            } else {
                // All failed
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Sync failed: " + errorMsg.toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error syncing progress: " + e.getMessage()));
        }
    }
    
    private static class ErrorResponse {
        @SuppressWarnings("unused")
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
    }
}
