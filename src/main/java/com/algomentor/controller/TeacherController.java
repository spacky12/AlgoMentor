package com.algomentor.controller;

import com.algomentor.dto.StudentProgressDTO;
import com.algomentor.model.Student;
import com.algomentor.repository.StudentRepository;
import com.algomentor.service.ProgressTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProgressTrackingService progressTrackingService;
    
    /**
     * Get all unique sections
     */
    @GetMapping("/sections")
    public ResponseEntity<Set<String>> getAllSections() {
        List<Student> students = studentRepository.findAll();
        Set<String> sections = students.stream()
                .map(Student::getSection)
                .filter(section -> section != null && !section.isEmpty())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(sections);
    }
    
    /**
     * Get all students in a specific section
     */
    @GetMapping("/section/{section}/students")
    public ResponseEntity<List<StudentProgressDTO>> getStudentsBySection(@PathVariable String section) {
        List<Student> students = studentRepository.findBySection(section);
        List<StudentProgressDTO> progressList = students.stream()
                .map(progressTrackingService::getStudentProgress)
                .collect(Collectors.toList());
        return ResponseEntity.ok(progressList);
    }
    
    /**
     * Sync all students' progress in a section
     */
    @PostMapping("/section/{section}/sync-all")
    public ResponseEntity<?> syncAllStudentsInSection(@PathVariable String section) {
        List<Student> students = studentRepository.findBySection(section);
        int synced = 0;
        int failed = 0;
        
        for (Student student : students) {
            try {
                if (student.getHackerrankProfile() != null && !student.getHackerrankProfile().isEmpty()) {
                    progressTrackingService.syncHackerRankProgress(student.getId());
                }
                if (student.getLeetcodeProfile() != null && !student.getLeetcodeProfile().isEmpty()) {
                    progressTrackingService.syncLeetCodeProgress(student.getId());
                }
                synced++;
            } catch (Exception e) {
                failed++;
                System.err.println("Failed to sync student " + student.getId() + ": " + e.getMessage());
            }
        }
        
        return ResponseEntity.ok(new SyncResult(synced, failed, "Synced " + synced + " students, " + failed + " failed"));
    }
    
    @Autowired
    private com.algomentor.service.ReportService reportService;

    /**
     * Export section data as CSV
     */
    @GetMapping("/section/{section}/export/csv")
    public ResponseEntity<byte[]> exportSectionCSV(@PathVariable String section) {
        try {
            List<Student> students = studentRepository.findBySection(section);
            List<com.algomentor.dto.StudentProgressDTO> studentDTOs = students.stream()
                    .map(progressTrackingService::getStudentProgress)
                    .collect(Collectors.toList());
            
            byte[] csvBytes = reportService.generateStudentProgressCSV(studentDTOs);
            
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=section_" + section + "_progress.csv")
                    .contentType(org.springframework.http.MediaType.parseMediaType("text/csv"))
                    .body(csvBytes);
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Export section data as PDF
     */
    @GetMapping("/section/{section}/export/pdf")
    public ResponseEntity<byte[]> exportSectionPDF(@PathVariable String section) {
        try {
            List<Student> students = studentRepository.findBySection(section);
            List<com.algomentor.dto.StudentProgressDTO> studentDTOs = students.stream()
                    .map(progressTrackingService::getStudentProgress)
                    .collect(Collectors.toList());
            
            byte[] pdfBytes = reportService.generateStudentProgressPDF(studentDTOs, section);
            
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=section_" + section + "_progress.pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private static class SyncResult {
        @SuppressWarnings("unused")
        private int synced;
        @SuppressWarnings("unused")
        private int failed;
        @SuppressWarnings("unused")
        private String message;
        
        public SyncResult(int synced, int failed, String message) {
            this.synced = synced;
            this.failed = failed;
            this.message = message;
        }
        
        @SuppressWarnings("unused")
        public int getSynced() {
            return synced;
        }
        
        @SuppressWarnings("unused")
        public int getFailed() {
            return failed;
        }
        
        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
    }
}
