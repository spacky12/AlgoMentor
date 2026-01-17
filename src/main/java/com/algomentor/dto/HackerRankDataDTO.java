package com.algomentor.dto;

import java.util.List;

public class HackerRankDataDTO {
    private String username;
    private int solvedProblems;
    private int badges;
    private String rank;
    private List<String> recentProblems;
    private boolean success;
    private String message;
    
    public HackerRankDataDTO() {}
    
    public HackerRankDataDTO(String username, int solvedProblems, int badges, String rank, List<String> recentProblems) {
        this.username = username;
        this.solvedProblems = solvedProblems;
        this.badges = badges;
        this.rank = rank;
        this.recentProblems = recentProblems;
        this.success = true;
    }
    
    public HackerRankDataDTO(String username, String message, boolean success) {
        this.username = username;
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getSolvedProblems() {
        return solvedProblems;
    }
    
    public void setSolvedProblems(int solvedProblems) {
        this.solvedProblems = solvedProblems;
    }
    
    public int getBadges() {
        return badges;
    }
    
    public void setBadges(int badges) {
        this.badges = badges;
    }
    
    public String getRank() {
        return rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public List<String> getRecentProblems() {
        return recentProblems;
    }
    
    public void setRecentProblems(List<String> recentProblems) {
        this.recentProblems = recentProblems;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
