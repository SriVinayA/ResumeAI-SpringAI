package com.vinayappari.resumespringai.dto;

import java.util.List;

public record OptimizationResponse(
        ResumeData tailoredResume,
        int oldAtsScore,
        int newAtsScore,
        List<String> matchedKeywords,
        List<String> missingKeywords,
        List<String> recommendations
) {}