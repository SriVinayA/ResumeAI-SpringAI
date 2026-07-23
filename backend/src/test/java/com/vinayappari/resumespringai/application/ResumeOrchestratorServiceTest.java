package com.vinayappari.resumespringai.application;

import com.vinayappari.resumespringai.domain.AiAnalyzer;
import com.vinayappari.resumespringai.domain.DocumentExtractor;
import com.vinayappari.resumespringai.domain.FileStorage;
import com.vinayappari.resumespringai.domain.PdfGenerator;
import com.vinayappari.resumespringai.dto.ExtractionResponse;
import com.vinayappari.resumespringai.dto.JobDescriptionData;
import com.vinayappari.resumespringai.dto.OptimizationResponse;
import com.vinayappari.resumespringai.dto.ResumeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResumeOrchestratorServiceTest {

    @Mock
    private DocumentExtractor documentExtractor;

    @Mock
    private AiAnalyzer aiAnalyzer;

    @Mock
    private PdfGenerator pdfGenerator;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private ResumeOrchestratorService orchestratorService;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = mock(MultipartFile.class);
    }

    @Test
    void testAnalyzeAndGenerateResume() {
        // Arrange
        String jdText = "Looking for a Java Developer";
        String extractedText = "Raw resume text";
        
        ExtractionResponse extractionResponse = new ExtractionResponse("application/pdf", extractedText);
        ResumeData parsedResume = new ResumeData("John Doe", "john@test.com", "123", "Summary", List.of(), List.of(), List.of(), List.of(), List.of());
        JobDescriptionData parsedJd = new JobDescriptionData("Java Dev", List.of("Java"), List.of(), List.of(), List.of());
        OptimizationResponse optimizedResponse = new OptimizationResponse(parsedResume, 90, List.of("Java"), List.of("Spring"), List.of("Add Spring"));
        File mockPdf = new File("test.pdf");
        String mockFileId = "12345-abcde";

        when(documentExtractor.extract(mockFile)).thenReturn(extractionResponse);
        when(aiAnalyzer.parseResume(extractedText)).thenReturn(parsedResume);
        when(aiAnalyzer.parseJobDescription(jdText)).thenReturn(parsedJd);
        when(aiAnalyzer.optimizeResume(parsedResume, parsedJd)).thenReturn(optimizedResponse);
        when(pdfGenerator.generatePdf(parsedResume)).thenReturn(mockPdf);
        when(fileStorage.storeFile(mockPdf)).thenReturn(mockFileId);

        // Act
        Map<String, Object> result = orchestratorService.analyzeAndGenerateResume(mockFile, jdText);

        // Assert
        assertNotNull(result);
        assertEquals(90, result.get("atsScore"));
        assertEquals(List.of("Java"), result.get("matchedKeywords"));
        assertEquals(List.of("Spring"), result.get("missingKeywords"));
        assertEquals("/api/v1/resume/download/" + mockFileId, result.get("downloadUrl"));

        verify(documentExtractor, times(1)).extract(mockFile);
        verify(aiAnalyzer, times(1)).parseResume(extractedText);
        verify(aiAnalyzer, times(1)).parseJobDescription(jdText);
        verify(aiAnalyzer, times(1)).optimizeResume(parsedResume, parsedJd);
        verify(pdfGenerator, times(1)).generatePdf(parsedResume);
        verify(fileStorage, times(1)).storeFile(mockPdf);
    }

    @Test
    void testGetGeneratedPdf() {
        // Arrange
        String fileId = "12345-abcde";
        File mockFile = new File("test.pdf");
        when(fileStorage.getFile(fileId)).thenReturn(Optional.of(mockFile));

        // Act
        Optional<File> result = orchestratorService.getGeneratedPdf(fileId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockFile, result.get());
        verify(fileStorage, times(1)).getFile(fileId);
    }
}
