package com.medai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medai.client.AiClient;
import com.medai.dto.request.AiAnalysisRequest;
import com.medai.dto.response.AiAnalysisResponse;
import com.medai.dto.response.AiAnalysisResultResponse;
import com.medai.exception.ResourceNotFoundException;
import com.medai.model.entity.AiAnalysis;
import com.medai.model.entity.Patient;
import com.medai.repository.AiAnalysisRepository;
import com.medai.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AiAnalysisService {

    private final AiClient aiClient;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;

    public AiAnalysisResultResponse analyze(Long userId, AiAnalysisRequest request) {
        // 1. Находим пациента
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        // 2. Автоматически подставляем age и sex из профиля если не переданы
        if (request.getAge() == null && patient.getDateOfBirth() != null) {
            int age = Period.between(patient.getDateOfBirth().toLocalDate(), LocalDate.now()).getYears();
            request.setAge(age);
        }
        if (request.getSex() == null && patient.getGender() != null) {
            request.setSex(patient.getGender());
        }

        // 3. Сохраняем входные данные в БД
        AiAnalysis analysis = new AiAnalysis();
        analysis.setPatient(patient);
        analysis.setAge(request.getAge());
        analysis.setSex(request.getSex());
        analysis.setDataset(request.getDataset());
        analysis.setCp(request.getCp());
        analysis.setTrestbps(request.getTrestbps());
        analysis.setChol(request.getChol());
        analysis.setFbs(request.getFbs());
        analysis.setRestecg(request.getRestecg());
        analysis.setThalch(request.getThalch());
        analysis.setExang(request.getExang());
        analysis.setOldpeak(request.getOldpeak());
        analysis.setSlope(request.getSlope());
        analysis.setCa(request.getCa());
        analysis.setThal(request.getThal());
        aiAnalysisRepository.save(analysis);

        // 4. Вызываем Python AI сервис
        AiAnalysisResponse aiResponse = aiClient.analyze(request);

        // 5. Сохраняем результат в ту же запись
        analysis.setRiskLevel(aiResponse.getRiskLevel());
        analysis.setConfidenceLevel(aiResponse.getConfidenceLevel());
        analysis.setPrediction(aiResponse.getPrediction());
        analysis.setThresholdUsed(aiResponse.getThresholdUsed());
        analysis.setModelVersion(aiResponse.getModelVersion());
        analysis.setUrgency(aiResponse.getUrgency());
        analysis.setExplanation(aiResponse.getExplanation());
        analysis.setPossibleConditions(toJson(aiResponse.getPossibleConditions()));
        analysis.setRecommendedSpecialists(toJson(aiResponse.getRecommendedSpecialists()));
        analysis.setRecommendedTests(toJson(aiResponse.getRecommendedTests()));
        aiAnalysisRepository.save(analysis);

        log.info("AI analysis saved with id: {} for patient: {}", analysis.getId(), patient.getId());

        return mapToResultResponse(analysis, aiResponse);
    }

    public List<AiAnalysisResultResponse> getHistory(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return aiAnalysisRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream()
                .map(a -> mapToResultResponse(a, null))
                .toList();
    }

    public AiAnalysisResultResponse getById(Long analysisId) {
        AiAnalysis analysis = aiAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found"));
        return mapToResultResponse(analysis, null);
    }

    private AiAnalysisResultResponse mapToResultResponse(AiAnalysis analysis, AiAnalysisResponse aiResponse) {
        AiAnalysisResultResponse response = new AiAnalysisResultResponse();
        response.setId(analysis.getId());
        response.setRiskLevel(analysis.getRiskLevel());
        response.setConfidenceLevel(analysis.getConfidenceLevel());
        response.setPrediction(analysis.getPrediction());
        response.setUrgency(analysis.getUrgency());
        response.setExplanation(analysis.getExplanation());
        response.setPossibleConditions(fromJson(analysis.getPossibleConditions()));
        response.setRecommendedSpecialists(fromJson(analysis.getRecommendedSpecialists()));
        response.setRecommendedTests(fromJson(analysis.getRecommendedTests()));
        response.setCreatedAt(analysis.getCreatedAt());

        if (aiResponse != null) {
            response.setDisclaimer(aiResponse.getDisclaimer());
        }
        return response;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error serializing to JSON", e);
            return "[]";
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> fromJson(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing from JSON", e);
            return List.of();
        }
    }
}