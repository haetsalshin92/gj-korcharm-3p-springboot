package com.example.aibot.controller;

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

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody JsonNode payload,
                                                @RequestHeader("X-GitHub-Event") String eventType) {
        if ("pull_request".equals(eventType)) {
            int prNumber = payload.get("number").asInt();
            System.out.println("🔔 PR 이벤트 수신!! 번호: " + prNumber);

            String diff = gitHubService.getPullRequestDiff(prNumber);
            System.out.println("📄 PR Diff 내용:\n" + diff);

            // TODO: Gemini API 호출 및 리뷰 코멘트 작성

            return ResponseEntity.ok("Diff fetched");
        }
        return ResponseEntity.ok("Event ignored");
    }
}
