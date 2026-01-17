package com.algomentor.controller;

import com.algomentor.dto.HackerRankProfileDTO;
import com.algomentor.model.Problem;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import com.algomentor.service.HackerRankService;
import com.algomentor.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackerrank")
@CrossOrigin(origins = "*")
public class HackerRankController {
    
    @Autowired
    private HackerRankService hackerRankService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProblemService problemService;
    
    /**
     * Fetch HackerRank profile data for a username
     */
    @GetMapping("/profile/{username}")
    public ResponseEntity<HackerRankProfileDTO> getProfile(@PathVariable String username) {
        HackerRankProfileDTO profile = hackerRankService.fetchProfileData(username);
        
        if (profile.isSuccess()) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(profile);
        }
    }
    
    /**
     * Sync HackerRank data for a student
     * This will fetch the profile data and update/create problems in the database
     * Students can only sync their own data
     */
    @PostMapping("/sync/{studentId}")
    public ResponseEntity<?> syncStudentData(
            @PathVariable Long studentId,
            HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("userEmail");
            String userRole = (String) request.getAttribute("userRole");
            
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }
            
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
            
            // Students can only sync their own data
            if ("STUDENT".equals(userRole)) {
                Student loggedInStudent = studentRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("Student not found"));
                if (!student.getId().equals(loggedInStudent.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("You can only sync your own data"));
                }
            }
            
            String hackerProfile = student.getHackerrankProfile();
            if (hackerProfile == null || hackerProfile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Student does not have a HackerRank profile configured"));
            }
            
            // Fetch data from HackerRank
            HackerRankProfileDTO profile = hackerRankService.fetchProfileData(hackerProfile);
            
            if (!profile.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Failed to fetch HackerRank data: " + profile.getErrorMessage()));
            }
            
            // Update Student stats
            student.setHackerRankTotal(profile.getTotalProblemsSolved());
            student.setHackerRankEasy(profile.getEasyProblemsSolved());
            student.setHackerRankMedium(profile.getMediumProblemsSolved());
            student.setHackerRankHard(profile.getHardProblemsSolved());
            studentRepository.save(student); // Save the stats immediately

            // Create problems based on fetched data
            int problemsCreated = 0;
            if (profile.getRecentProblems() != null) {
                for (com.algomentor.dto.HackerRankProblemDTO hrProblem : profile.getRecentProblems()) {
                    // Check if problem already exists
                    boolean exists = problemService.getProblemsByStudentId(studentId).stream()
                            .anyMatch(p -> p.getTitle().equals(hrProblem.getTitle()) 
                                    && p.getPlatform().equals("HackerRank"));
                    
                    if (!exists) {
                        Problem problem = new Problem();
                        problem.setTitle(hrProblem.getTitle());
                        problem.setPlatform("HackerRank");
                        problem.setDifficulty(hrProblem.getDifficulty() != null 
                                ? hrProblem.getDifficulty() : "Unknown");
                        problem.setStatus("solved");
                        problem.setStudent(student); // Explicitly set student
                        problemService.createProblem(studentId, problem);
                        problemsCreated++;
                    }
                }
            }
            
            return ResponseEntity.ok(new SyncResponse(
                    "Successfully synced HackerRank data",
                    profile.getTotalProblemsSolved(),
                    problemsCreated
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error syncing data: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * Test endpoint to verify the controller is accessible
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new ErrorResponse("HackerRank controller is working!"));
    }
    
    /**
     * Response DTO for sync operation
     */
    private static class SyncResponse {
        @SuppressWarnings("unused")
        private String message;
        @SuppressWarnings("unused")
        private int totalProblemsOnHackerRank;
        @SuppressWarnings("unused")
        private int problemsCreated;
        
        public SyncResponse(String message, int totalProblemsOnHackerRank, int problemsCreated) {
            this.message = message;
            this.totalProblemsOnHackerRank = totalProblemsOnHackerRank;
            this.problemsCreated = problemsCreated;
        }
        
        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
        
        @SuppressWarnings("unused")
        public int getTotalProblemsOnHackerRank() {
            return totalProblemsOnHackerRank;
        }
        
        @SuppressWarnings("unused")
        public int getProblemsCreated() {
            return problemsCreated;
        }
    }
    
    /**
     * Error response DTO
     */
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
