package com.medai.dto.request;

import com.medai.model.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiFeedbackRequest {

    @NotNull
    private FeedbackType feedbackType;

    private String notes;

    private String finalDiagnosis;
}