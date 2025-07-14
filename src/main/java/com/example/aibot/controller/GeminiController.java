package com.example.aibot.controller;


import com.example.aibot.dto.GeminiReview;
import com.example.aibot.repository.GeminiReviewRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GeminiController {

    private final GeminiReviewRepository geminiReviewRepository;

    public GeminiController(GeminiReviewRepository geminiReviewRepository) {
        this.geminiReviewRepository = geminiReviewRepository;
    }

    @GetMapping("/reviews")
    public List<GeminiReview> getAllReviews() {
        return geminiReviewRepository.findAll();
    }
}
