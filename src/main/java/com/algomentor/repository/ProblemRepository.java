package com.algomentor.repository;

import com.algomentor.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    @Query("SELECT p FROM Problem p WHERE p.student.id = :studentId")
    List<Problem> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT p FROM Problem p WHERE p.student.id = :studentId AND p.status = :status")
    List<Problem> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);
}
