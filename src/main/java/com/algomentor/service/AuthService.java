package com.algomentor.service;

import com.algomentor.dto.AuthResponse;
import com.algomentor.dto.LoginRequest;
import com.algomentor.dto.SignupRequest;
import com.algomentor.model.Student;
import com.algomentor.model.User;
import com.algomentor.repository.StudentRepository;
import com.algomentor.repository.UserRepository;
import com.algomentor.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse signup(SignupRequest request) {
        try {
            logger.info("Attempting signup for email: {}", request.getEmail());
            
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Signup failed: Email already exists - {}", request.getEmail());
                return new AuthResponse("Email already exists");
            }

            // Force role to STUDENT
            String requestedRole = "STUDENT";
            
            if (request.getRollNumber() != null && !request.getRollNumber().trim().isEmpty() && studentRepository.existsByRollNumber(request.getRollNumber())) {
                logger.warn("Signup failed: Roll number already exists - {}", request.getRollNumber());
                return new AuthResponse("Roll number already exists");
            }
            
            // Create user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(requestedRole);
            user = userRepository.save(user);
            logger.info("User created with ID: {} and Role: {}", user.getId(), requestedRole);
            
            if ("STUDENT".equals(requestedRole)) {
                // Create student
                Student student = new Student();
                student.setName(request.getName());
                student.setEmail(request.getEmail());
                student.setRollNumber(request.getRollNumber());
                student.setHackerrankProfile(request.getHackerrankProfile() != null && !request.getHackerrankProfile().trim().isEmpty() 
                    ? request.getHackerrankProfile() : null);
                student.setLeetcodeProfile(request.getLeetcodeProfile() != null && !request.getLeetcodeProfile().trim().isEmpty() 
                    ? request.getLeetcodeProfile() : null);
                student.setSection(request.getSection());
                student.setGroup(request.getGroup());
                student.setUser(user);
                student = studentRepository.save(student);
                logger.info("Student created with ID: {}", student.getId());
                
                // Update user with student reference
                user.setStudent(student);
                userRepository.save(user);
            }
            
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
            logger.info("Signup successful for email: {}", request.getEmail());
            return new AuthResponse(token, user.getRole(), user.getId(), user.getEmail());
            
        } catch (Exception e) {
            logger.error("Error during signup: ", e);
            return new AuthResponse("Signup failed: " + e.getMessage());
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getRole(), user.getId(), user.getEmail());
    }
}
