package com.algomentor.dto;

import java.time.LocalDateTime;

public class StudentDTO {
    private Long id;
    private String name;
    private String email;
    private String rollNumber;
    private String section;
    private String hackerrankProfile;
    private String leetcodeProfile;
    private int problemCount;
    private LocalDateTime createdAt;
    
    public StudentDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRollNumber() {
        return rollNumber;
    }
    
    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public String getHackerrankProfile() {
        return hackerrankProfile;
    }
    
    public void setHackerrankProfile(String hackerrankProfile) {
        this.hackerrankProfile = hackerrankProfile;
    }
    
    public String getLeetcodeProfile() {
        return leetcodeProfile;
    }
    
    public void setLeetcodeProfile(String leetcodeProfile) {
        this.leetcodeProfile = leetcodeProfile;
    }
    
    public int getProblemCount() {
        return problemCount;
    }
    
    public void setProblemCount(int problemCount) {
        this.problemCount = problemCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
