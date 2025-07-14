package com.example.aibot.controller;

import com.example.aibot.service.GeminiService;
import com.example.aibot.service.GitHubService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody JsonNode payload,
                                                @RequestHeader("X-GitHub-Event") String eventType) {
        if ("pull_request".equals(eventType)) {
            int prNumber = payload.get("number").asInt();
            System.out.println("🔔 PR 이벤트 수신!! 번호: " + prNumber);

            String diff = gitHubService.getPullRequestDiff(prNumber);
            System.out.println("📄 PR Diff 내용:\n" + diff);

            // ➕ Gemini 코드 리뷰 생성
            String review = geminiService.getCodeReview(prNumber, diff);
            System.out.println("🧠 AI 코드 리뷰 결과:\n" + review);

            return ResponseEntity.ok("리뷰 완료!");
        }
        return ResponseEntity.ok("처리되지 않은 이벤트");

    }
}
