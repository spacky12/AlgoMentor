package com.algomentor.service;

import com.algomentor.dto.ProblemDTO;
import com.algomentor.model.Problem;
import com.algomentor.model.Student;
import com.algomentor.repository.ProblemRepository;
import com.algomentor.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemService {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public List<ProblemDTO> getProblemsByStudentId(Long studentId) {
        return problemRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ProblemDTO getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        return convertToDTO(problem);
    }
    
    public ProblemDTO createProblem(Long studentId, Problem problem) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        problem.setStudent(student);
        Problem savedProblem = problemRepository.save(problem);
        return convertToDTO(savedProblem);
    }
    
    public ProblemDTO updateProblem(Long id, Problem problemDetails) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        
        problem.setTitle(problemDetails.getTitle());
        problem.setPlatform(problemDetails.getPlatform());
        problem.setDifficulty(problemDetails.getDifficulty());
        problem.setStatus(problemDetails.getStatus());
        
        Problem updatedProblem = problemRepository.save(problem);
        return convertToDTO(updatedProblem);
    }
    
    public void deleteProblem(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problemRepository.delete(problem);
    }
    
    private ProblemDTO convertToDTO(Problem problem) {
        return new ProblemDTO(
                problem.getId(),
                problem.getTitle(),
                problem.getPlatform(),
                problem.getDifficulty(),
                problem.getStatus(),
                problem.getSolvedAt(),
                problem.getStudentId()
        );
    }
}
