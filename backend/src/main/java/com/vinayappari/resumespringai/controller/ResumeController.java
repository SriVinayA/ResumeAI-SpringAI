package com.vinayappari.resumespringai.controller;

import com.vinayappari.resumespringai.application.ResumeOrchestratorService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/resume")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Validated
public class ResumeController {

    private final ResumeOrchestratorService orchestratorService;

    public ResumeController(ResumeOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("jobDescription") @NotBlank String jobDescriptionText) {

        Map<String, Object> response = orchestratorService.analyzeAndGenerateResume(resumeFile, jobDescriptionText);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String fileId) {
        Optional<File> pdfFileOpt = orchestratorService.getGeneratedPdf(fileId);
        
        if (pdfFileOpt.isEmpty() || !pdfFileOpt.get().exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(pdfFileOpt.get());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"optimized_resume.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}