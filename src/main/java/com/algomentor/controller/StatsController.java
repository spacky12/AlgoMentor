package com.algomentor.controller;

import com.algomentor.dto.StudentDTO;
import com.algomentor.repository.ProblemRepository;
import com.algomentor.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        List<StudentDTO> students = studentService.getAllStudents();
        long totalProblems = problemRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", students.size());
        stats.put("totalProblems", totalProblems);
        stats.put("students", students);
        
        return ResponseEntity.ok(stats);
    }
}
