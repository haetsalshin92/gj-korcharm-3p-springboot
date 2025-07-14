package com.example.aibot.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "gemini_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiReview {

    @Id
    private String id;

    private int prNumber;
    private String diff;
    private String review;
    private LocalDateTime createdAt = LocalDateTime.now();

    public GeminiReview(String diff, String reviewText) {
        this.diff = diff;
        this.review = reviewText;
    }
}
