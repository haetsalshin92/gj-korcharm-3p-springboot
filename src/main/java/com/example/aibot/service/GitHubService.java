package com.example.aibot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.repo-owner}")
    private String repoOwner;

    @Value("${github.repo-name}")
    private String repoName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GitHubService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getPullRequestDiff(String repoFullName, int prNumber) {
        try {
            String url = "https://api.github.com/repos/" + repoFullName + "/pulls/" + prNumber;

            HttpHeaders headers = new HttpHeaders(); //
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3.diff");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
