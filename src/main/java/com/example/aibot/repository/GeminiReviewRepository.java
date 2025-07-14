package com.example.aibot.repository;
import com.example.aibot.dto.GeminiReview;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GeminiReviewRepository extends MongoRepository<GeminiReview, String> {
}
