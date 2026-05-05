package com.medai.controller;

import com.medai.dto.response.DocumentResponse;
import com.medai.service.DocumentService;
import com.medai.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", defaultValue = "OTHER") String documentType
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        log.info("Document upload by userId: {}, type: {}", userId, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.upload(userId, file, documentType));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return ResponseEntity.ok(documentService.getMyDocuments(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        documentService.deleteDocument(userId, id);
        return ResponseEntity.noContent().build();
    }
}
