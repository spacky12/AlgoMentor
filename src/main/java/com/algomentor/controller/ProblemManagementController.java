package com.algomentor.controller;

import com.algomentor.dto.ProblemDTO;
import com.algomentor.model.Problem;
import com.algomentor.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "*")
public class ProblemManagementController {
    
    @Autowired
    private ProblemService problemService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable Long id) {
        ProblemDTO problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProblemDTO> updateProblem(@PathVariable Long id, @Valid @RequestBody Problem problem) {
        ProblemDTO updatedProblem = problemService.updateProblem(id, problem);
        return ResponseEntity.ok(updatedProblem);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
