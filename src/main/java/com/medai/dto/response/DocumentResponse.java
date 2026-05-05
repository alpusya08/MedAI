package com.medai.dto.response;

import com.medai.model.enums.DocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private DocumentType documentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}