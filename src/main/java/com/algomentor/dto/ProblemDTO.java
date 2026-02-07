package com.algomentor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class ProblemDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "^(HackerRank|LeetCode|CodeForces|CodeChef|AtCoder|Other)$", 
             message = "Platform must be one of: HackerRank, LeetCode, CodeForces, CodeChef, AtCoder, Other")
    private String platform;
    
    @NotBlank(message = "Difficulty is required")
    @Pattern(regexp = "^(EASY|MEDIUM|HARD)$", 
             message = "Difficulty must be one of: EASY, MEDIUM, HARD")
    private String difficulty;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SOLVED|IN_PROGRESS|FAILED)$", 
             message = "Status must be one of: SOLVED, IN_PROGRESS, FAILED")
    private String status;
    private LocalDateTime solvedAt;
    private Long studentId;
    
    public ProblemDTO() {}
    
    public ProblemDTO(Long id, String title, String platform, String difficulty, 
                     String status, LocalDateTime solvedAt, Long studentId) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.difficulty = difficulty;
        this.status = status;
        this.solvedAt = solvedAt;
        this.studentId = studentId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getSolvedAt() {
        return solvedAt;
    }
    
    public void setSolvedAt(LocalDateTime solvedAt) {
        this.solvedAt = solvedAt;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
