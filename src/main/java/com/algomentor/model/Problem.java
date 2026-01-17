package com.algomentor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Platform is required")
    @Column(nullable = false)
    private String platform;
    
    @NotBlank(message = "Difficulty is required")
    @Column(nullable = false)
    private String difficulty; // Easy, Medium, Hard
    
    @Column(nullable = false)
    private String status = "solved"; // solved, in_progress, failed
    
    @Column(name = "solved_at")
    private LocalDateTime solvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @PrePersist
    protected void onCreate() {
        if (solvedAt == null) {
            solvedAt = LocalDateTime.now();
        }
    }
    
    // Constructors
    public Problem() {}
    
    public Problem(String title, String platform, String difficulty, String status, Student student) {
        this.title = title;
        this.platform = platform;
        this.difficulty = difficulty;
        this.status = status;
        this.student = student;
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
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }
}
