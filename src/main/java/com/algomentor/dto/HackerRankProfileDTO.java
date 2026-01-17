package com.algomentor.dto;

import java.util.List;

public class HackerRankProfileDTO {
    private String username;
    private int totalProblemsSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private List<HackerRankProblemDTO> recentProblems;
    private boolean success;
    private String errorMessage;
    
    public HackerRankProfileDTO() {}
    
    public HackerRankProfileDTO(String username, int totalProblemsSolved, 
                                int easySolved, int mediumSolved, int hardSolved) {
        this.username = username;
        this.totalProblemsSolved = totalProblemsSolved;
        this.easySolved = easySolved;
        this.mediumSolved = mediumSolved;
        this.hardSolved = hardSolved;
        this.success = true;
    }
    
    public static HackerRankProfileDTO error(String errorMessage) {
        HackerRankProfileDTO dto = new HackerRankProfileDTO();
        dto.success = false;
        dto.errorMessage = errorMessage;
        return dto;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getTotalProblemsSolved() {
        return totalProblemsSolved;
    }
    
    public void setTotalProblemsSolved(int totalProblemsSolved) {
        this.totalProblemsSolved = totalProblemsSolved;
    }
    
    public int getEasyProblemsSolved() {
        return easySolved;
    }

    public int getMediumProblemsSolved() {
        return mediumSolved;
    }

    public int getHardProblemsSolved() {
        return hardSolved;
    }
    
    public int getEasySolved() {
        return easySolved;
    }
    
    public void setEasySolved(int easySolved) {
        this.easySolved = easySolved;
    }
    
    public int getMediumSolved() {
        return mediumSolved;
    }
    
    public void setMediumSolved(int mediumSolved) {
        this.mediumSolved = mediumSolved;
    }
    
    public int getHardSolved() {
        return hardSolved;
    }
    
    public void setHardSolved(int hardSolved) {
        this.hardSolved = hardSolved;
    }
    
    public List<HackerRankProblemDTO> getRecentProblems() {
        return recentProblems;
    }
    
    public void setRecentProblems(List<HackerRankProblemDTO> recentProblems) {
        this.recentProblems = recentProblems;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
