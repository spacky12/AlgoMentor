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
    public ResponseEntity<?> syncAllProgress(@PathVariable Long studentId) {
        try {
            progressTrackingService.syncHackerRankProgress(studentId);
            progressTrackingService.syncLeetCodeProgress(studentId);
            StudentProgressDTO progress = progressTrackingService.getAllStudentsProgress().stream()
                    .filter(p -> p.getStudentId().equals(studentId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(progress);
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
