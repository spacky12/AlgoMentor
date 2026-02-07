package com.algomentor.service;

import com.algomentor.dto.HackerRankProfileDTO;
import com.algomentor.dto.StudentProgressDTO;
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
public class ProgressTrackingService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private HackerRankService hackerRankService;
    
    @Autowired
    private LeetCodeService leetCodeService;
    
    /**
     * Get progress summary for all students
     */
    public List<StudentProgressDTO> getAllStudentsProgress() {
        List<Student> students = studentRepository.findAll();
        
        return students.stream()
                .map(this::getStudentProgress)
                .collect(Collectors.toList());
    }
    
    /**
     * Get progress for a specific student
     */
    public StudentProgressDTO getStudentProgress(Student student) {
        StudentProgressDTO progress = new StudentProgressDTO();
        progress.setStudentId(student.getId());
        progress.setStudentName(student.getName());
        progress.setEmail(student.getEmail());
        progress.setRollNumber(student.getRollNumber());
        progress.setSection(student.getSection());
        progress.setGroup(student.getGroup());
        progress.setHackerrankProfile(student.getHackerrankProfile());
        progress.setLeetcodeProfile(student.getLeetcodeProfile());
        
        // Use the direct stats stored in the student entity
        int hackerRankTotal = student.getHackerRankTotal();
        int hackerRankEasy = student.getHackerRankEasy();
        int hackerRankMedium = student.getHackerRankMedium();
        int hackerRankHard = student.getHackerRankHard();
        
        int leetCodeTotal = student.getLeetCodeTotal();
        int leetCodeEasy = student.getLeetCodeEasy();
        int leetCodeMedium = student.getLeetCodeMedium();
        int leetCodeHard = student.getLeetCodeHard();
        
        // Calculate totals
        int totalProblems = hackerRankTotal + leetCodeTotal;
        int easyProblems = hackerRankEasy + leetCodeEasy;
        int mediumProblems = hackerRankMedium + leetCodeMedium;
        int hardProblems = hackerRankHard + leetCodeHard;
        
        progress.setTotalProblems(totalProblems);
        progress.setEasyProblems(easyProblems);
        progress.setMediumProblems(mediumProblems);
        progress.setHardProblems(hardProblems);
        
        progress.setHackerRankTotal(hackerRankTotal);
        progress.setHackerRankEasy(hackerRankEasy);
        progress.setHackerRankMedium(hackerRankMedium);
        progress.setHackerRankHard(hackerRankHard);
        
        progress.setLeetCodeTotal(leetCodeTotal);
        progress.setLeetCodeEasy(leetCodeEasy);
        progress.setLeetCodeMedium(leetCodeMedium);
        progress.setLeetCodeHard(leetCodeHard);
        
        return progress;
    }
    
    /**
     * Sync progress from HackerRank for a student
     */
    public StudentProgressDTO syncHackerRankProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        String hackerProfile = student.getHackerrankProfile();
        if (hackerProfile == null || hackerProfile.isEmpty()) {
            throw new RuntimeException("Student does not have a HackerRank profile configured");
        }
        
        // Fetch from HackerRank
        HackerRankProfileDTO profile = hackerRankService.fetchProfileData(hackerProfile);
        
        if (!profile.isSuccess()) {
            throw new RuntimeException("Failed to fetch HackerRank data: " + profile.getErrorMessage());
        }
        
        // Update problems in database
        // Also update the direct stats
        student.setHackerRankTotal(profile.getTotalProblemsSolved());
        student.setHackerRankEasy(profile.getEasyProblemsSolved());
        student.setHackerRankMedium(profile.getMediumProblemsSolved());
        student.setHackerRankHard(profile.getHardProblemsSolved());
        studentRepository.save(student);

        if (profile.getRecentProblems() != null) {
            List<Problem> existingProblems = problemRepository.findByStudentId(studentId);
            var existingTitles = existingProblems.stream()
                    .filter(p -> "HackerRank".equalsIgnoreCase(p.getPlatform()))
                    .map(Problem::getTitle)
                    .collect(Collectors.toSet());
            
            for (var hrProblem : profile.getRecentProblems()) {
                if (!existingTitles.contains(hrProblem.getTitle())) {
                    Problem problem = new Problem();
                    problem.setTitle(hrProblem.getTitle());
                    problem.setPlatform("HackerRank");
                    problem.setDifficulty(hrProblem.getDifficulty() != null ? hrProblem.getDifficulty() : "Unknown");
                    problem.setStatus("solved");
                    problem.setStudent(student);
                    
                    if (hrProblem.getSolvedDate() != null) {
                        try {
                            problem.setSolvedAt(java.time.OffsetDateTime.parse(hrProblem.getSolvedDate()).toLocalDateTime());
                        } catch (Exception e) {
                            problem.setSolvedAt(java.time.LocalDateTime.now());
                        }
                    }
                    
                    problemRepository.save(problem);
                    existingTitles.add(hrProblem.getTitle()); // Track added titles in same session
                }
            }
        }
        
        return getStudentProgress(student);
    }
    
    /**
     * Sync progress from LeetCode for a student
     */
    public StudentProgressDTO syncLeetCodeProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        String leetcodeProfile = student.getLeetcodeProfile();
        if (leetcodeProfile == null || leetcodeProfile.isEmpty()) {
            throw new RuntimeException("Student does not have a LeetCode profile configured");
        }
        
        // Fetch from LeetCode
        HackerRankProfileDTO profile = leetCodeService.fetchProfileData(leetcodeProfile);
        
        if (!profile.isSuccess()) {
            throw new RuntimeException("Failed to fetch LeetCode data: " + profile.getErrorMessage());
        }
        
        // Update problems in database
        // Also update the direct stats
        student.setLeetCodeTotal(profile.getTotalProblemsSolved());
        student.setLeetCodeEasy(profile.getEasyProblemsSolved());
        student.setLeetCodeMedium(profile.getMediumProblemsSolved());
        student.setLeetCodeHard(profile.getHardProblemsSolved());
        studentRepository.save(student);

        if (profile.getRecentProblems() != null) {
            List<Problem> existingProblems = problemRepository.findByStudentId(studentId);
            var existingTitles = existingProblems.stream()
                    .filter(p -> "LeetCode".equalsIgnoreCase(p.getPlatform()))
                    .map(Problem::getTitle)
                    .collect(Collectors.toSet());
            
            for (var lcProblem : profile.getRecentProblems()) {
                if (!existingTitles.contains(lcProblem.getTitle())) {
                    Problem problem = new Problem();
                    problem.setTitle(lcProblem.getTitle());
                    problem.setPlatform("LeetCode");
                    problem.setDifficulty(lcProblem.getDifficulty() != null ? lcProblem.getDifficulty() : "Unknown");
                    problem.setStatus("solved");
                    problem.setStudent(student);
                    
                    if (lcProblem.getSolvedDate() != null) {
                        try {
                            problem.setSolvedAt(java.time.OffsetDateTime.parse(lcProblem.getSolvedDate()).toLocalDateTime());
                        } catch (Exception e) {
                            problem.setSolvedAt(java.time.LocalDateTime.now());
                        }
                    }
                    
                    problemRepository.save(problem);
                    existingTitles.add(lcProblem.getTitle()); // Track added titles in same session
                }
            }
        }
        
        return getStudentProgress(student);
    }
}
