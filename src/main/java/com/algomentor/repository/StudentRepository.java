package com.algomentor.repository;

import com.algomentor.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByRollNumber(String rollNumber);
    List<Student> findBySection(String section);
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
}
