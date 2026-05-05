package com.medai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medai.client.AiClient;
import com.medai.dto.request.AiAnalysisRequest;
import com.medai.dto.request.QuestionnaireRequest;
import com.medai.dto.response.AiAnalysisResponse;
import com.medai.dto.response.AiAnalysisResultResponse;
import com.medai.dto.response.DoctorShortResponse;
import com.medai.dto.response.QuestionnaireResponse;
import com.medai.exception.ResourceNotFoundException;
import com.medai.exception.UnauthorizedException;
import com.medai.model.entity.AiAnalysis;
import com.medai.model.entity.Doctor;
import com.medai.model.entity.Patient;
import com.medai.model.entity.Questionnaire;
import com.medai.repository.AiAnalysisRepository;
import com.medai.repository.DoctorRepository;
import com.medai.repository.PatientRepository;
import com.medai.repository.QuestionnaireRepository;
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
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    public QuestionnaireResponse submit(Long userId, QuestionnaireRequest request) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        // Подставляем age и sex из профиля если не переданы
        if (request.getAge() == null && patient.getDateOfBirth() != null) {
            request.setAge(Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears());
        }
        if (request.getSex() == null && patient.getGender() != null) {
            request.setSex(patient.getGender());
        }

        Questionnaire questionnaire = buildQuestionnaire(patient, request);
        questionnaireRepository.save(questionnaire);

        // Автоматически запускаем AI анализ
        AiAnalysisResultResponse aiResult = runAiAnalysis(patient, questionnaire, request);

        QuestionnaireResponse response = mapToResponse(questionnaire);
        response.setAiAnalysis(aiResult);

        log.info("Questionnaire {} submitted, AI analysis id={}", questionnaire.getId(),
                aiResult != null ? aiResult.getId() : "failed");
        return response;
    }

    public List<QuestionnaireResponse> getMyQuestionnaires(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return questionnaireRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream()
                .map(q -> {
                    QuestionnaireResponse resp = mapToResponse(q);
                    if (q.getAiAnalysis() != null) {
                        resp.setAiAnalysis(mapAiToResult(q.getAiAnalysis()));
                    }
                    return resp;
                })
                .toList();
    }

    public QuestionnaireResponse getById(Long userId, Long questionnaireId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionnaire not found"));

        if (!questionnaire.getPatient().getId().equals(patient.getId())) {
            throw new UnauthorizedException("Access denied");
        }

        QuestionnaireResponse response = mapToResponse(questionnaire);
        if (questionnaire.getAiAnalysis() != null) {
            response.setAiAnalysis(mapAiToResult(questionnaire.getAiAnalysis()));
        }
        return response;
    }

    private AiAnalysisResultResponse runAiAnalysis(Patient patient, Questionnaire questionnaire, QuestionnaireRequest request) {
        try {
            AiAnalysisRequest aiRequest = buildAiRequest(request);
            AiAnalysisResponse aiResponse = aiClient.analyze(aiRequest);

            AiAnalysis analysis = new AiAnalysis();
            analysis.setPatient(patient);
            analysis.setQuestionnaire(questionnaire);
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

            AiAnalysisResultResponse result = mapAiToResult(analysis);
            result.setDisclaimer(aiResponse.getDisclaimer());
            result.setSuggestedDoctors(findSuggestedDoctors(aiResponse.getRecommendedSpecialists()));
            return result;
        } catch (Exception e) {
            log.error("AI analysis failed for questionnaire {}: {}", questionnaire.getId(), e.getMessage());
            return null;
        }
    }

    private AiAnalysisRequest buildAiRequest(QuestionnaireRequest req) {
        AiAnalysisRequest aiReq = new AiAnalysisRequest();
        aiReq.setAge(req.getAge());
        aiReq.setSex(req.getSex());
        aiReq.setDataset(req.getDataset());
        aiReq.setCp(req.getCp());
        aiReq.setTrestbps(req.getTrestbps());
        aiReq.setChol(req.getChol());
        aiReq.setFbs(req.getFbs());
        aiReq.setRestecg(req.getRestecg());
        aiReq.setThalch(req.getThalch());
        aiReq.setExang(req.getExang());
        aiReq.setOldpeak(req.getOldpeak());
        aiReq.setSlope(req.getSlope());
        aiReq.setCa(req.getCa());
        aiReq.setThal(req.getThal());
        return aiReq;
    }

    private Questionnaire buildQuestionnaire(Patient patient, QuestionnaireRequest req) {
        Questionnaire q = new Questionnaire();
        q.setPatient(patient);
        q.setAge(req.getAge());
        q.setSex(req.getSex());
        q.setDataset(req.getDataset());
        q.setCp(req.getCp());
        q.setTrestbps(req.getTrestbps());
        q.setChol(req.getChol());
        q.setFbs(req.getFbs());
        q.setRestecg(req.getRestecg());
        q.setThalch(req.getThalch());
        q.setExang(req.getExang());
        q.setOldpeak(req.getOldpeak());
        q.setSlope(req.getSlope());
        q.setCa(req.getCa());
        q.setThal(req.getThal());
        q.setShortnessOfBreath(req.getShortnessOfBreath());
        q.setShortnessAtRest(req.getShortnessAtRest());
        q.setShortnessAtActivity(req.getShortnessAtActivity());
        q.setPalpitations(req.getPalpitations());
        q.setDizziness(req.getDizziness());
        q.setSwelling(req.getSwelling());
        q.setSymptomDuration(req.getSymptomDuration());
        q.setSymptomFrequency(req.getSymptomFrequency());
        q.setTriggers(req.getTriggers());
        q.setPreviousCvd(req.getPreviousCvd());
        q.setCvdHistory(req.getCvdHistory());
        q.setFamilyHistoryCvd(req.getFamilyHistoryCvd());
        q.setChronicDiseases(req.getChronicDiseases());
        q.setMedications(req.getMedications());
        q.setSmoking(req.getSmoking());
        q.setPhysicalActivity(req.getPhysicalActivity());
        q.setBmi(req.getBmi());
        q.setAdditionalNotes(req.getAdditionalNotes());
        return q;
    }

    private QuestionnaireResponse mapToResponse(Questionnaire q) {
        QuestionnaireResponse resp = new QuestionnaireResponse();
        resp.setId(q.getId());
        resp.setPatientId(q.getPatient().getId());
        resp.setAge(q.getAge());
        resp.setSex(q.getSex());
        resp.setCp(q.getCp());
        resp.setTrestbps(q.getTrestbps());
        resp.setChol(q.getChol());
        resp.setFbs(q.getFbs());
        resp.setRestecg(q.getRestecg());
        resp.setThalch(q.getThalch());
        resp.setExang(q.getExang());
        resp.setOldpeak(q.getOldpeak());
        resp.setSlope(q.getSlope());
        resp.setCa(q.getCa());
        resp.setThal(q.getThal());
        resp.setShortnessOfBreath(q.getShortnessOfBreath());
        resp.setShortnessAtRest(q.getShortnessAtRest());
        resp.setShortnessAtActivity(q.getShortnessAtActivity());
        resp.setPalpitations(q.getPalpitations());
        resp.setDizziness(q.getDizziness());
        resp.setSwelling(q.getSwelling());
        resp.setSymptomDuration(q.getSymptomDuration());
        resp.setSymptomFrequency(q.getSymptomFrequency());
        resp.setTriggers(q.getTriggers());
        resp.setPreviousCvd(q.getPreviousCvd());
        resp.setCvdHistory(q.getCvdHistory());
        resp.setFamilyHistoryCvd(q.getFamilyHistoryCvd());
        resp.setChronicDiseases(q.getChronicDiseases());
        resp.setMedications(q.getMedications());
        resp.setSmoking(q.getSmoking());
        resp.setPhysicalActivity(q.getPhysicalActivity());
        resp.setBmi(q.getBmi());
        resp.setAdditionalNotes(q.getAdditionalNotes());
        resp.setCreatedAt(q.getCreatedAt());
        return resp;
    }

    private AiAnalysisResultResponse mapAiToResult(AiAnalysis a) {
        AiAnalysisResultResponse r = new AiAnalysisResultResponse();
        r.setId(a.getId());
        r.setRiskLevel(a.getRiskLevel());
        r.setConfidenceLevel(a.getConfidenceLevel());
        r.setPrediction(a.getPrediction());
        r.setUrgency(a.getUrgency());
        r.setExplanation(a.getExplanation());
        r.setPossibleConditions(fromJson(a.getPossibleConditions()));
        r.setRecommendedSpecialists(fromJson(a.getRecommendedSpecialists()));
        r.setRecommendedTests(fromJson(a.getRecommendedTests()));
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    private List<DoctorShortResponse> findSuggestedDoctors(List<String> specialists) {
        if (specialists == null || specialists.isEmpty()) return List.of();
        List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCaseAndVerifiedTrue(specialists.get(0));
        if (doctors.isEmpty()) {
            doctors = doctorRepository.findByVerifiedTrueOrderByRatingDesc().stream().limit(5).toList();
        }
        return doctors.stream().map(d -> new DoctorShortResponse(
                d.getId(), d.getUser().getFirstName(), d.getUser().getLastName(),
                d.getSpecialization(), d.getClinicName(), d.getYearsOfExperience(),
                d.getRating(), d.getConsultationFee(),
                d.getAcceptsOnlineAppointments(), d.getAcceptsOfflineAppointments(), d.getVerified()
        )).toList();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error", e);
            return "[]";
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> fromJson(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
