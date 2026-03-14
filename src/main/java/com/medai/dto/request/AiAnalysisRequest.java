package com.medai.dto.request;

import lombok.Data;

@Data
public class AiAnalysisRequest {
    // Эти поля подставляются автоматически из профиля пациента если не переданы
    private Integer age;
    private String sex;           // "Male" / "Female"

    // Клинические показатели — пациент заполняет вручную
    private String dataset;       // по умолчанию "Cleveland"
    private String cp;            // боль в груди: "typical angina", "atypical angina", "non-anginal", "asymptomatic"
    private Integer trestbps;     // давление в покое (мм рт. ст.)
    private Integer chol;         // холестерин (мг/дл)
    private Boolean fbs;          // сахар натощак > 120 мг/дл
    private String restecg;       // ЭКГ: "normal", "st-t abnormality", "lv hypertrophy"
    private Integer thalch;       // макс. ЧСС
    private Boolean exang;        // стенокардия при нагрузке
    private Double oldpeak;       // депрессия ST
    private String slope;         // наклон ST: "upsloping", "flat", "downsloping"
    private Integer ca;           // кол-во крупных сосудов (0-3)
    private String thal;          // "normal", "fixed defect", "reversable defect"
}