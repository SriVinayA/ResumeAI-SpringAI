package com.vinayappari.resumespringai.application;

import com.vinayappari.resumespringai.domain.AiAnalyzer;
import com.vinayappari.resumespringai.domain.DocumentExtractor;
import com.vinayappari.resumespringai.domain.FileStorage;
import com.vinayappari.resumespringai.domain.PdfGenerator;
import com.vinayappari.resumespringai.dto.ExtractionResponse;
import com.vinayappari.resumespringai.dto.JobDescriptionData;
import com.vinayappari.resumespringai.dto.OptimizationResponse;
import com.vinayappari.resumespringai.dto.ResumeData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Service
public class ResumeOrchestratorService {

    private final DocumentExtractor documentExtractor;
    private final AiAnalyzer aiAnalyzer;
    private final PdfGenerator pdfGenerator;
    private final FileStorage fileStorage;

    public ResumeOrchestratorService(DocumentExtractor documentExtractor, AiAnalyzer aiAnalyzer, PdfGenerator pdfGenerator, FileStorage fileStorage) {
        this.documentExtractor = documentExtractor;
        this.aiAnalyzer = aiAnalyzer;
        this.pdfGenerator = pdfGenerator;
        this.fileStorage = fileStorage;
    }

    public Map<String, Object> analyzeAndGenerateResume(MultipartFile resumeFile, String jobDescriptionText) {
        // 1. Extract Text
        ExtractionResponse extracted = documentExtractor.extract(resumeFile);
        
        // 2. Parse into structured domain objects
        ResumeData parsedResume = aiAnalyzer.parseResume(extracted.text());
        JobDescriptionData parsedJd = aiAnalyzer.parseJobDescription(jobDescriptionText);
        
        // 3. Optimize
        OptimizationResponse optimized = aiAnalyzer.optimizeResume(parsedResume, parsedJd);
        
        // 4. Generate PDF
        PdfGenerator.PdfResult generatedPdfResult = pdfGenerator.generatePdf(optimized.tailoredResume());
        
        // 5. Store File
        String fileId = fileStorage.storeFile(generatedPdfResult.pdf());
        
        // 6. Return response
        java.util.List<Map<String, Object>> mappedExperience = optimized.tailoredResume().experience().stream()
            .map(exp -> Map.of(
                "title", exp.title(),
                "company", exp.company(),
                "duration", exp.duration(),
                "bullets", exp.responsibilities()
            ))
            .toList();

        return java.util.Map.ofEntries(
                java.util.Map.entry("fileId", fileId),
                java.util.Map.entry("summary", optimized.tailoredResume().summary()),
                java.util.Map.entry("experience", mappedExperience),
                java.util.Map.entry("skills", optimized.tailoredResume().skills()),
                java.util.Map.entry("oldScore", optimized.oldAtsScore()),
                java.util.Map.entry("newScore", optimized.newAtsScore()),
                java.util.Map.entry("matchedKeywords", optimized.matchedKeywords()),
                java.util.Map.entry("missingKeywords", optimized.missingKeywords()),
                java.util.Map.entry("recommendations", optimized.recommendations()),
                java.util.Map.entry("downloadUrl", "/api/v1/resume/download/" + fileId),
                java.util.Map.entry("overleafZipBase64", generatedPdfResult.overleafZipBase64())
        );
    }

    public Optional<File> getGeneratedPdf(String fileId) {
        return fileStorage.getFile(fileId);
    }
}
