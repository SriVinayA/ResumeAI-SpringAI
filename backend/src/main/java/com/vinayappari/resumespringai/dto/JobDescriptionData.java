package com.vinayappari.resumespringai.dto;

import java.util.List;

public record JobDescriptionData(
        String jobTitle,
        List<String> requiredSkills,
        List<String> preferredSkills,
        List<String> responsibilities,
        List<String> atsKeywords
) {}