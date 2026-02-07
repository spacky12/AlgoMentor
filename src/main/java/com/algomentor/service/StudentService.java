package com.algomentor.service;

import com.algomentor.dto.StudentDTO;
import com.algomentor.exception.ResourceNotFoundException;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<StudentDTO> searchStudents(String search, Pageable pageable) {
        return studentRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
                search, search, search, pageable)
                .map(this::convertToDTO);
    }
    
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDTO(student);
    }
    
    public StudentDTO createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists: " + student.getEmail());
        }
        if (studentRepository.existsByRollNumber(student.getRollNumber())) {
            throw new RuntimeException("Roll number already exists: " + student.getRollNumber());
        }
        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }
    
    public StudentDTO updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        if (!student.getEmail().equals(studentDetails.getEmail()) && 
            studentRepository.existsByEmail(studentDetails.getEmail())) {
            throw new RuntimeException("Email already exists: " + studentDetails.getEmail());
        }
        if (!student.getRollNumber().equals(studentDetails.getRollNumber()) && 
            studentRepository.existsByRollNumber(studentDetails.getRollNumber())) {
            throw new RuntimeException("Roll number already exists: " + studentDetails.getRollNumber());
        }
        
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setRollNumber(studentDetails.getRollNumber());
        student.setHackerrankProfile(studentDetails.getHackerrankProfile());
        student.setLeetcodeProfile(studentDetails.getLeetcodeProfile());
        student.setSection(studentDetails.getSection());
        
        Student updatedStudent = studentRepository.save(student);
        return convertToDTO(updatedStudent);
    }
    
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
    
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setRollNumber(student.getRollNumber());
        dto.setSection(student.getSection());
        dto.setHackerrankProfile(student.getHackerrankProfile());
        dto.setLeetcodeProfile(student.getLeetcodeProfile());
        dto.setProblemCount(student.getProblemCount());
        dto.setCreatedAt(student.getCreatedAt());
        return dto;
    }
}
