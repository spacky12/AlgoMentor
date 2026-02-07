package com.algomentor.dto;

public class StudentProgressDTO {
    private Long studentId;
    private String studentName;
    private String email;
    private String rollNumber;
    private String section;
    private String group;
    private String hackerrankProfile;
    private String leetcodeProfile;
    
    // HackerRank stats
    private int hackerRankTotal;
    private int hackerRankEasy;
    private int hackerRankMedium;
    private int hackerRankHard;
    
    // LeetCode stats
    private int leetCodeTotal;
    private int leetCodeEasy;
    private int leetCodeMedium;
    private int leetCodeHard;
    
    // Overall stats (from database)
    private int totalProblems;
    private int easyProblems;
    private int mediumProblems;
    private int hardProblems;
    
    public StudentProgressDTO() {}
    
    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
    
    public int getHackerRankTotal() {
        return hackerRankTotal;
    }
    
    public void setHackerRankTotal(int hackerRankTotal) {
        this.hackerRankTotal = hackerRankTotal;
    }
    
    public int getHackerRankEasy() {
        return hackerRankEasy;
    }
    
    public void setHackerRankEasy(int hackerRankEasy) {
        this.hackerRankEasy = hackerRankEasy;
    }
    
    public int getHackerRankMedium() {
        return hackerRankMedium;
    }
    
    public void setHackerRankMedium(int hackerRankMedium) {
        this.hackerRankMedium = hackerRankMedium;
    }
    
    public int getHackerRankHard() {
        return hackerRankHard;
    }
    
    public void setHackerRankHard(int hackerRankHard) {
        this.hackerRankHard = hackerRankHard;
    }
    
    public int getLeetCodeTotal() {
        return leetCodeTotal;
    }
    
    public void setLeetCodeTotal(int leetCodeTotal) {
        this.leetCodeTotal = leetCodeTotal;
    }
    
    public int getLeetCodeEasy() {
        return leetCodeEasy;
    }
    
    public void setLeetCodeEasy(int leetCodeEasy) {
        this.leetCodeEasy = leetCodeEasy;
    }
    
    public int getLeetCodeMedium() {
        return leetCodeMedium;
    }
    
    public void setLeetCodeMedium(int leetCodeMedium) {
        this.leetCodeMedium = leetCodeMedium;
    }
    
    public int getLeetCodeHard() {
        return leetCodeHard;
    }
    
    public void setLeetCodeHard(int leetCodeHard) {
        this.leetCodeHard = leetCodeHard;
    }
    
    public int getTotalProblems() {
        return totalProblems;
    }
    
    public void setTotalProblems(int totalProblems) {
        this.totalProblems = totalProblems;
    }
    
    public int getEasyProblems() {
        return easyProblems;
    }
    
    public void setEasyProblems(int easyProblems) {
        this.easyProblems = easyProblems;
    }
    
    public int getMediumProblems() {
        return mediumProblems;
    }
    
    public void setMediumProblems(int mediumProblems) {
        this.mediumProblems = mediumProblems;
    }
    
    public int getHardProblems() {
        return hardProblems;
    }
    
    public void setHardProblems(int hardProblems) {
        this.hardProblems = hardProblems;
    }
    
    public int getGrandTotal() {
        return hackerRankTotal + leetCodeTotal;
    }
}
