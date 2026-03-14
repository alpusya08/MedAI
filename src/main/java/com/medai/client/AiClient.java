package com.medai.client;

import com.medai.dto.request.AiAnalysisRequest;
import com.medai.dto.response.AiAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public AiAnalysisResponse analyze(AiAnalysisRequest request) {
        String url = aiServiceUrl + "/predict";

        // Формируем тело в формате который ожидает Python сервис: { "features": {...} }
        Map<String, Object> features = new HashMap<>();
        features.put("age", request.getAge());
        features.put("sex", request.getSex());
        features.put("dataset", request.getDataset() != null ? request.getDataset() : "Cleveland");
        features.put("cp", request.getCp());
        features.put("trestbps", request.getTrestbps());
        features.put("chol", request.getChol());
        features.put("fbs", request.getFbs());
        features.put("restecg", request.getRestecg());
        features.put("thalch", request.getThalch());
        features.put("exang", request.getExang());
        features.put("oldpeak", request.getOldpeak());
        features.put("slope", request.getSlope());
        features.put("ca", request.getCa());
        features.put("thal", request.getThal());

        Map<String, Object> body = new HashMap<>();
        body.put("features", features);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        log.info("Calling AI service: {}", url);
        ResponseEntity<AiAnalysisResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, AiAnalysisResponse.class
        );

        return response.getBody();
    }
}