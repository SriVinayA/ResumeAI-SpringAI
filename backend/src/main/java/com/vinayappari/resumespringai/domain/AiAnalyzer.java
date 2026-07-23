package com.vinayappari.resumespringai.domain;

import com.vinayappari.resumespringai.dto.JobDescriptionData;
import com.vinayappari.resumespringai.dto.OptimizationResponse;
import com.vinayappari.resumespringai.dto.ResumeData;

public interface AiAnalyzer {
    ResumeData parseResume(String rawText);
    JobDescriptionData parseJobDescription(String rawText);
    OptimizationResponse optimizeResume(ResumeData resumeData, JobDescriptionData jdData);
}
