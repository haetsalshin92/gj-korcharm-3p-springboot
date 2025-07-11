package com.example.aibot.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getCodeReview(String diff) {
        try {
            String question = "이 PR의 변경사항을 코드 품질, 가독성, 성능, 버그 가능성 측면에서 리뷰해 주세요.";

            String requestBody = String.format("""
        {
          "questions": ["%s"],
          "contexts": ["%s"],
          "temperature": 0.2
        }
        """, question, diff.replace("\"", "\\\""));  // diff 내부 따옴표 escape

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/aqa:generateAnswer?key=" + geminiApiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("answers").get(0).path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Gemini 호출 중 오류 발생: " + e.getMessage();
        }
    }


    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }

}
