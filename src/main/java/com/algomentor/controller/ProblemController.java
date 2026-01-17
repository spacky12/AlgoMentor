package com.algomentor.controller;

import com.algomentor.dto.ProblemDTO;
import com.algomentor.model.Problem;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import com.algomentor.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/problems")
@CrossOrigin(origins = "*")
public class ProblemController {
    
    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Get student problems - students can only see their own, teachers can see any
     */
    @GetMapping
    public ResponseEntity<List<ProblemDTO>> getStudentProblems(
            @PathVariable Long studentId,
            HttpServletRequest request) {
        
        String userEmail = (String) request.getAttribute("userEmail");
        String userRole = (String) request.getAttribute("userRole");
        
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Students can only see their own problems
        if ("STUDENT".equals(userRole)) {
            Student student = studentRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            if (!student.getId().equals(studentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        List<ProblemDTO> problems = problemService.getProblemsByStudentId(studentId);
        return ResponseEntity.ok(problems);
    }
    
    /**
     * Create problem - students can only create for themselves, teachers can create for any
     */
    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(
            @PathVariable Long studentId,
            @Valid @RequestBody Problem problem,
            HttpServletRequest request) {
        
        String userEmail = (String) request.getAttribute("userEmail");
        String userRole = (String) request.getAttribute("userRole");
        
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Students can only create problems for themselves
        if ("STUDENT".equals(userRole)) {
            Student student = studentRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            if (!student.getId().equals(studentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        ProblemDTO createdProblem = problemService.createProblem(studentId, problem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProblem);
    }
}
