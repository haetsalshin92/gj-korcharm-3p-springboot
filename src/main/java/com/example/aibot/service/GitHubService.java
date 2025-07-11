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

    public String getPullRequestDiff(int prNumber) {
        try {
            // PR 정보 요청 URL
            String prUrl = String.format("https://api.github.com/repos/%s/%s/pulls/%d", repoOwner, repoName, prNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // PR 정보 요청
            ResponseEntity<String> prResponse = restTemplate.exchange(prUrl, HttpMethod.GET, entity, String.class);

            JsonNode prJson = objectMapper.readTree(prResponse.getBody());
            String diffUrl = prJson.get("diff_url").asText();

            // diff 내용 요청
            headers.set("Accept", "application/vnd.github.v3.diff"); // diff 포맷 요청
            entity = new HttpEntity<>(headers);

            ResponseEntity<String> diffResponse = restTemplate.exchange(diffUrl, HttpMethod.GET, entity, String.class);

            return diffResponse.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
