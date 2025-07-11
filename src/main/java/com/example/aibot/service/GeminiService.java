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
            String prompt = """
            당신은 숙련된 시니어 개발자입니다.
            다음은 PR의 코드 변경 내용(diff)입니다:

            %s

            이 변경사항을 검토하고 코드 품질, 가독성, 성능, 버그 가능성에 대해 리뷰를 작성해 주세요.
            """.formatted(diff);

            // ✅ escapeJson 제거: Gemini API는 그대로 text로 받음
            String requestBody = """
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """.formatted(prompt); // escapeJson() 필요 없음

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // ✅ BearerAuth 사용 ❌ → API 키는 query param으로만!
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Gemini 호출 중 오류 발생: " + e.getMessage();
        }
    }

    private String escapeJson(String s) {
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }

}
