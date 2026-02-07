package com.algomentor.service;

import com.algomentor.dto.HackerRankProfileDTO;
import com.algomentor.dto.HackerRankProblemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HackerRankService {
    
    private static final Logger logger = LoggerFactory.getLogger(HackerRankService.class);
    
    /**
     * Fetches HackerRank profile data for a given username using the /badges endpoint.
     * 
     * Note: This endpoint provides "solved" counts per badge (e.g., Python, Problem Solving).
     * It does NOT provide a breakdown of Easy/Medium/Hard problems. 
     * Therefore, only the total solved count is accurate. Breakdown fields are set to 0.
     * 
     * @param username HackerRank username (can be just username or full URL)
     * @return HackerRankProfileDTO containing profile statistics
     */
    public HackerRankProfileDTO fetchProfileData(String username) {
        try {
            // Clean the username - extract just the username part if a full URL is provided
            String cleanUsername = extractUsername(username);
            // using badges endpoint as it's the most reliable source for "total solved" without auth
            String url = "https://www.hackerrank.com/rest/hackers/" + cleanUsername + "/badges";
            
            logger.info("Fetching HackerRank profile: {} (cleaned from: {})", url, username);
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Use ParameterizedTypeReference to avoid raw Map warning, or just Map<String, Object>
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            
            int totalSolved = 0;
            // Breakdown is not available from this endpoint
            int easySolved = 0;
            int mediumSolved = 0;
            int hardSolved = 0;
            
            if (responseBody != null && responseBody.containsKey("models")) {
                Object modelsObj = responseBody.get("models");
                if (modelsObj instanceof List<?>) {
                    List<?> models = (List<?>) modelsObj;
                    
                    for (Object modelObj : models) {
                        if (modelObj instanceof Map<?, ?>) {
                           Map<?, ?> model = (Map<?, ?>) modelObj;
                           if (model.containsKey("solved")) {
                                Object solvedObj = model.get("solved");
                                int solved = 0;
                                if (solvedObj instanceof Integer) {
                                    solved = (Integer) solvedObj;
                                }
                                totalSolved += solved;
                           }
                        }
                    }
                }
            }
            
            HackerRankProfileDTO profile = new HackerRankProfileDTO(
                    cleanUsername, totalSolved, easySolved, mediumSolved, hardSolved
            );
            
            // Fetch recent challenges
            String recentUrl = "https://www.hackerrank.com/rest/hackers/" + cleanUsername + "/recent_challenges?limit=20";
            try {
                @SuppressWarnings("unchecked")
                ResponseEntity<Map<String, Object>> recentResponse = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.exchange(
                    recentUrl, 
                    HttpMethod.GET, 
                    entity, 
                    Map.class
                );
                
                Map<String, Object> recentBody = recentResponse.getBody();
                List<HackerRankProblemDTO> recentProblems = new ArrayList<>();
                
                if (recentBody != null && recentBody.containsKey("models")) {
                    Object recentModelsObj = recentBody.get("models");
                    if (recentModelsObj instanceof List<?>) {
                        for (Object mObj : (List<?>) recentModelsObj) {
                            if (mObj instanceof Map<?, ?>) {
                                Map<?, ?> m = (Map<?, ?>) mObj;
                                HackerRankProblemDTO p = new HackerRankProblemDTO();
                                p.setTitle((String) m.get("challengetitle"));
                                p.setPlatform("HackerRank");
                                p.setDifficulty("Medium"); // Scraper doesn't give difficulty easily
                                p.setStatus("solved");
                                // created_at is usually the timestamp
                                Object createdAt = m.get("created_at");
                                if (createdAt != null) {
                                    p.setSolvedDate(createdAt.toString());
                                }
                                recentProblems.add(p);
                            }
                        }
                    }
                }
                profile.setRecentProblems(recentProblems);
            } catch (Exception e) {
                logger.warn("Failed to fetch recent challenges for {}: {}", cleanUsername, e.getMessage());
                profile.setRecentProblems(new ArrayList<>());
            }
            
            logger.info("Successfully fetched profile for {}: {} problems solved", cleanUsername, totalSolved);
            return profile;
            
        } catch (Exception e) {
            logger.error("Error fetching HackerRank profile for {}: {}", username, e.getMessage());
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
        
        // If it's already just a username (no slashes or URLs), return as is
        if (!input.contains("/") && !input.contains("http")) {
            return input;
        }
        
        // Remove protocol and domain
        input = input.replaceFirst("^https?://(www\\.)?hackerrank\\.com/?", "");
        
        // Remove leading/trailing slashes
        input = input.replaceAll("^/+|/+$", "");
        
        // Remove /profile/ prefix if present
        input = input.replaceFirst("^profile/", "");
        
        // Get the last part after any remaining slashes (in case of nested paths)
        if (input.contains("/")) {
            String[] parts = input.split("/");
            input = parts[parts.length - 1];
        }
        
        return input;
    }
    
    /**
     * Alternative method: Fetch data using HackerRank's public API endpoints if available
     * This method can be extended to use official APIs when they become available
     */
    public HackerRankProfileDTO fetchProfileDataViaAPI(String username) {
        // Placeholder for future API integration
        // If HackerRank releases a public API, implement it here
        logger.warn("API method not yet implemented. Using web scraping instead.");
        return fetchProfileData(username);
    }
}
