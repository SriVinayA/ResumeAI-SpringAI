package com.vinayappari.resumespringai.dto;

import java.util.List;

public record ResumeData(
        String name,
        String email,
        String phone,
        String location,
        String linkedin,
        String github,
        String portfolio,
        String summary,
        List<String> otherLinks,
        List<SkillCategory> skills,
        List<Experience> experience,
        List<Education> education,
        List<Project> projects,
        List<String> certifications
) {
    public record SkillCategory(String category, List<String> skills) {}
    public record Experience(String company, String title, String duration, List<String> responsibilities) {}
    public record Education(String institution, String degree, String year) {}
    public record Project(String name, List<String> description, List<String> technologies) {}
}