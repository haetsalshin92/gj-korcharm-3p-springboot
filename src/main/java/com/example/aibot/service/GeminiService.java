package com.example.aibot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getCodeReview(String diff) {
        try {
            // gemini-pro 모델과 generateContent 엔드포인트 사용
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;

            // 코드 리뷰를 위한 프롬프트 구성
            // diff 내용을 프롬프트에 직접 포함하여 Gemini가 변경사항을 분석하도록 유도
            String prompt = "당신은 숙련된 개발자이자 코드 리뷰 전문가입니다. 아래 Pull Request(PR)의 변경사항을 제공된 코드 diff를 기반으로 다음 측면에서 상세히 리뷰해주세요:\n" +
                    "1. 코드 품질 (Clean Code, 디자인 패턴 등)\n" +
                    "2. 가독성 및 유지보수성\n" +
                    "3. 성능 최적화 가능성\n" +
                    "4. 잠재적인 버그 또는 예외 처리 문제\n" +
                    "5. 보안 취약점 (SQL Injection, XSS 등) 가능성\n" +
                    "각 항목에 대해 구체적인 설명과 개선 방안을 제시하고, 가능하다면 수정된 코드 예시도 포함해주세요. 답변은 한국어로 명확하고 간결하게 작성해주세요.\n\n" +
                    "--- PR 변경사항 (Diff) ---\n" + diff;

            // Gemini API의 generateContent 요청 본문 구조에 맞게 Map 구성
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(textPart);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", parts);

            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", contents);
            // temperature 설정은 여기에 직접 넣을 수 있습니다.
            // requestBody.put("temperature", 0.2); // 필요시 추가

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            // 응답에서 텍스트 추출 (generateContent 응답 구조에 맞게 변경)
            // candidates -> content -> parts -> [0] -> text
            if (root.has("candidates") && root.get("candidates").isArray() && root.get("candidates").size() > 0) {
                JsonNode firstCandidate = root.get("candidates").get(0);
                if (firstCandidate.has("content") && firstCandidate.get("content").has("parts") && firstCandidate.get("content").get("parts").isArray() && firstCandidate.get("content").get("parts").size() > 0) {
                    return firstCandidate.get("content").get("parts").get(0).path("text").asText();
                }
            }
            return "❌ Gemini 응답에서 결과를 찾을 수 없습니다.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Gemini 호출 중 오류 발생: " + e.getMessage();
        }
    }

    // 이 메서드는 더 이상 필요하지 않습니다. 프롬프트 내에서 diff를 직접 처리합니다.
    // private String escapeJson(String s) {
    //     return s.replace("\"", "\\\"").replace("\n", "\\n");
    // }
}