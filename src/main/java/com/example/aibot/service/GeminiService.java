package com.example.aibot.service;

import com.example.aibot.dto.GeminiReview;
import com.example.aibot.repository.GeminiReviewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GeminiReviewRepository codeReviewRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(RestTemplate restTemplate,
                         ObjectMapper objectMapper,
                         GeminiReviewRepository codeReviewRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.codeReviewRepository = codeReviewRepository;
    }

    public String getCodeReview(int prNumber, String diff) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-8b-latest:generateContent?key=" + geminiApiKey;

            String prompt = """
                당신은 숙련된 개발자이자 코드 리뷰 전문가입니다.
                아래 Pull Request(PR)의 변경사항을 기반으로 다음을 검토해주세요:

                1. 코드 품질 (Clean Code, 디자인 패턴 등)
                2. 가독성 및 유지보수성
                3. 성능 최적화 가능성
                4. 잠재적 버그 또는 예외 처리 문제
                5. 보안 취약점 가능성 (SQL Injection, XSS 등)

                각 항목에 대해 구체적 설명과 개선방안을 제시하고,
                가능하다면 수정된 코드 예시도 포함해주세요.

                --- PR 변경사항 (Diff) ---
                %s
                """.formatted(diff);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode textNode = objectMapper.readTree(response.getBody())
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            String reviewText = textNode.isMissingNode() ? "❌ Gemini 응답에서 결과를 찾을 수 없습니다." : textNode.asText();

            // ✅ MongoDB에 저장
            GeminiReview review = new GeminiReview(prNumber, diff, reviewText);
            codeReviewRepository.save(review);

            return reviewText;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Gemini 호출 중 오류 발생: " + e.getMessage();
        }
    }

}