package com.medai.service;

import com.medai.dto.response.DocumentResponse;
import com.medai.exception.BadRequestException;
import com.medai.exception.ResourceNotFoundException;
import com.medai.exception.UnauthorizedException;
import com.medai.model.entity.Document;
import com.medai.model.entity.Patient;
import com.medai.model.enums.DocumentType;
import com.medai.repository.DocumentRepository;
import com.medai.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    public DocumentResponse upload(Long userId, MultipartFile file, String documentType) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Unsupported file type. Allowed: PDF, JPEG, PNG");
        }

        DocumentType docType;
        try {
            docType = DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            docType = DocumentType.OTHER;
        }

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path patientDir = Paths.get(uploadDir, "documents", patient.getId().toString());

        try {
            Files.createDirectories(patientDir);
            Path filePath = patientDir.resolve(storedFileName);
            Files.write(filePath, file.getBytes());

            Document document = new Document();
            document.setPatient(patient);
            document.setFileName(file.getOriginalFilename());
            document.setStoredFileName(storedFileName);
            document.setContentType(file.getContentType());
            document.setDocumentType(docType);
            document.setFilePath(filePath.toString());
            document.setFileSize(file.getSize());
            documentRepository.save(document);

            log.info("Document uploaded: {} for patient {}", storedFileName, patient.getId());
            return mapToResponse(document);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BadRequestException("Failed to save file: " + e.getMessage());
        }
    }

    public List<DocumentResponse> getMyDocuments(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        return documentRepository.findByPatientIdOrderByUploadedAtDesc(patient.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteDocument(Long userId, Long documentId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));

        Document document = documentRepository.findByIdAndPatientId(documentId, patient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            log.warn("Could not delete file from disk: {}", document.getFilePath());
        }

        documentRepository.delete(document);
        log.info("Document {} deleted by patient {}", documentId, patient.getId());
    }

    private DocumentResponse mapToResponse(Document doc) {
        DocumentResponse resp = new DocumentResponse();
        resp.setId(doc.getId());
        resp.setFileName(doc.getFileName());
        resp.setContentType(doc.getContentType());
        resp.setDocumentType(doc.getDocumentType());
        resp.setFileSize(doc.getFileSize());
        resp.setUploadedAt(doc.getUploadedAt());
        return resp;
    }
}
