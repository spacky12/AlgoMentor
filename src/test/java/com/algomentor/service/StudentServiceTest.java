package com.algomentor.service;

import com.algomentor.dto.StudentDTO;
import com.algomentor.exception.ResourceNotFoundException;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @InjectMocks
    private StudentService studentService;
    
    private Student testStudent;
    
    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("Test Student");
        testStudent.setEmail("test@example.com");
        testStudent.setRollNumber("123");
        testStudent.setSection("A");
        testStudent.setHackerrankProfile("testuser");
    }
    
    @Test
    void getAllStudents_ShouldReturnListOfStudents() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent));
        
        // Act
        List<StudentDTO> result = studentService.getAllStudents();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Student", result.get(0).getName());
        verify(studentRepository, times(1)).findAll();
    }
    
    @Test
    void getStudentById_WhenExists_ShouldReturnStudent() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        
        // Act
        StudentDTO result = studentService.getStudentById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Student", result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(studentRepository, times(1)).findById(1L);
    }
    
    @Test
    void getStudentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(999L);
        });
        verify(studentRepository, times(1)).findById(999L);
    }
    
    @Test
    void createStudent_WithValidData_ShouldCreateStudent() {
        // Arrange
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByRollNumber(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        
        // Act
        StudentDTO result = studentService.createStudent(testStudent);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Student", result.getName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }
    
    @Test
    void createStudent_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(studentRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            studentService.createStudent(testStudent);
        });
        verify(studentRepository, never()).save(any(Student.class));
    }
    
    @Test
    void deleteStudent_WhenExists_ShouldDeleteStudent() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        doNothing().when(studentRepository).delete(any(Student.class));
        
        // Act
        studentService.deleteStudent(1L);
        
        // Assert
        verify(studentRepository, times(1)).delete(testStudent);
    }
}
