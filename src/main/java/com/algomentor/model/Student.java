package com.algomentor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Roll number is required")
    @Column(name = "roll_number", unique = true, nullable = false)
    private String rollNumber;
    
    @Column(name = "hackerrank_profile")
    private String hackerrankProfile;
    
    @Column(name = "leetcode_profile")
    private String leetcodeProfile;
    
    @NotBlank(message = "Section is required")
    @Column(name = "section", nullable = false)
    private String section;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // HackerRank Stats
    @Column(name = "hr_total")
    private int hackerRankTotal = 0;
    
    @Column(name = "hr_easy")
    private int hackerRankEasy = 0;
    
    @Column(name = "hr_medium")
    private int hackerRankMedium = 0;
    
    @Column(name = "hr_hard")
    private int hackerRankHard = 0;
    
    // LeetCode Stats
    @Column(name = "lc_total")
    private int leetCodeTotal = 0;
    
    @Column(name = "lc_easy")
    private int leetCodeEasy = 0;
    
    @Column(name = "lc_medium")
    private int leetCodeMedium = 0;
    
    @Column(name = "lc_hard")
    private int leetCodeHard = 0;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Problem> problems = new ArrayList<>();
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Student() {}
    
    public Student(String name, String email, String rollNumber, String hackerrankProfile, String leetcodeProfile, String section) {
        this.name = name;
        this.email = email;
        this.rollNumber = rollNumber;
        this.hackerrankProfile = hackerrankProfile;
        this.leetcodeProfile = leetcodeProfile;
        this.section = section;
    }
    
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
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Problem> getProblems() {
        return problems;
    }
    
    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public int getProblemCount() {
        return problems != null ? problems.size() : 0;
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
}
