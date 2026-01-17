package com.algomentor.dto;

public class HackerRankProblemDTO {
    private String title;
    private String platform;
    private String difficulty;
    private String status;
    private String solvedDate;
    
    public HackerRankProblemDTO() {}
    
    public HackerRankProblemDTO(String title, String difficulty, String status, String solvedDate) {
        this.title = title;
        this.difficulty = difficulty;
        this.status = status;
        this.solvedDate = solvedDate;
    }
    
    // Getters and Setters
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
    
    public String getSolvedDate() {
        return solvedDate;
    }
    
    public void setSolvedDate(String solvedDate) {
        this.solvedDate = solvedDate;
    }
}
