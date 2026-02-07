package com.algomentor.service;

import com.algomentor.dto.HackerRankProfileDTO;
import com.algomentor.dto.HackerRankProblemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LeetCodeService {
    
    private static final Logger logger = LoggerFactory.getLogger(LeetCodeService.class);
    
    /**
     * Fetches LeetCode profile data for a given username
     * 
     * @param username LeetCode username
     * @return HackerRankProfileDTO (reusing the same DTO structure)
     */
    public HackerRankProfileDTO fetchProfileData(String username) {
        try {
            String cleanUsername = extractUsername(username);
            String url = "https://leetcode.com/graphql";
            
            String query = "{\"query\":\"query userPublicProfile($username: String!) { matchedUser(username: $username) { username submitStats: submitStatsGlobal { acSubmissionNum { difficulty count } } } recentSubmissionList(username: $username, limit: 20) { title titleSlug timestamp statusDisplay lang } }\",\"variables\":{\"username\":\"" + cleanUsername + "\"}}";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(query, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                
                if (data != null) {
                    HackerRankProfileDTO profile = null;

                    if (data.containsKey("matchedUser")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> matchedUser = (Map<String, Object>) data.get("matchedUser");
                        
                        if (matchedUser != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> submitStats = (Map<String, Object>) matchedUser.get("submitStats");
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> acSubmissionNum = (List<Map<String, Object>>) submitStats.get("acSubmissionNum");
                            
                            int totalSolved = 0;
                            int easySolved = 0;
                            int mediumSolved = 0;
                            int hardSolved = 0;
                            
                            for (Map<String, Object> counts : acSubmissionNum) {
                                String difficulty = (String) counts.get("difficulty");
                                int count = (Integer) counts.get("count");
                                
                                if ("All".equalsIgnoreCase(difficulty)) totalSolved = count;
                                if ("Easy".equalsIgnoreCase(difficulty)) easySolved = count;
                                if ("Medium".equalsIgnoreCase(difficulty)) mediumSolved = count;
                                if ("Hard".equalsIgnoreCase(difficulty)) hardSolved = count;
                            }
                            
                            profile = new HackerRankProfileDTO(
                                    cleanUsername, totalSolved, easySolved, mediumSolved, hardSolved
                            );
                        }
                    }

                    if (profile != null && data.containsKey("recentSubmissionList")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> submissions = (List<Map<String, Object>>) data.get("recentSubmissionList");
                        List<HackerRankProblemDTO> problems = new ArrayList<>();
                        
                        for (Map<String, Object> sub : submissions) {
                            String status = (String) sub.get("statusDisplay");
                            if ("Accepted".equals(status)) {
                                long timestamp = Long.parseLong(sub.get("timestamp").toString());
                                String solvedDate = java.time.Instant.ofEpochSecond(timestamp).toString();
                                
                                HackerRankProblemDTO p = new HackerRankProblemDTO();
                                p.setTitle((String) sub.get("title"));
                                p.setPlatform("LeetCode");
                                p.setDifficulty("Medium"); // LeetCode recentSubmissionList doesn't provide difficulty unfortunately without more queries
                                p.setStatus("solved");
                                p.setSolvedDate(solvedDate);
                                problems.add(p);
                            }
                        }
                        profile.setRecentProblems(problems);
                        
                        logger.info("Successfully fetched LeetCode profile and {} recent problems for {}", problems.size(), cleanUsername);
                        return profile;
                    } else if (profile != null) {
                        profile.setRecentProblems(new ArrayList<>());
                        return profile;
                    }
                }
            }
            
            return HackerRankProfileDTO.error("User not found or structure changed");
            
        } catch (Exception e) {
            logger.error("Error fetching LeetCode profile for {}: {}", username, e.getMessage());
            return HackerRankProfileDTO.error("Failed to fetch profile: " + e.getMessage());
        }
    }
    
    /**
     * Extracts the username from a string that might be a full URL or just a username
     */
    private String extractUsername(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        input = input.trim();
        
        if (!input.contains("/") && !input.contains("http")) {
            return input;
        }
        
        input = input.replaceFirst("^https?://(www\\.)?leetcode\\.com/?", "");
        input = input.replaceAll("^/+|/+$", "");
        input = input.replaceFirst("^profile/", "");
        
        if (input.contains("/")) {
            String[] parts = input.split("/");
            input = parts[parts.length - 1];
        }
        
        return input;
    }
}
