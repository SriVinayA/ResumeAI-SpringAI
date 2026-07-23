package com.vinayappari.resumespringai.infrastructure;

import com.vinayappari.resumespringai.domain.AiAnalyzer;
import com.vinayappari.resumespringai.dto.JobDescriptionData;
import com.vinayappari.resumespringai.dto.OptimizationResponse;
import com.vinayappari.resumespringai.dto.ResumeData;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AIService implements AiAnalyzer {

    private final ChatClient chatClient;

    @Value("classpath:prompts/parse-resume.st")
    private Resource parseResumePrompt;

    @Value("classpath:prompts/parse-jd.st")
    private Resource parseJdPrompt;

    @Value("classpath:prompts/optimize-resume-system.st")
    private Resource optimizeResumeSystemPrompt;

    @Value("classpath:prompts/optimize-resume-user.st")
    private Resource optimizeResumeUserPrompt;

    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .build();
    }

    @Override
    public ResumeData parseResume(String rawText) {
        return chatClient.prompt()
                .system(parseResumePrompt)
                .user(rawText)
                .call()
                .entity(ResumeData.class);
    }

    @Override
    public JobDescriptionData parseJobDescription(String rawText) {
        return chatClient.prompt()
                .system(parseJdPrompt)
                .user(rawText)
                .call()
                .entity(JobDescriptionData.class);
    }

    @Override
    public OptimizationResponse optimizeResume(ResumeData resumeData, JobDescriptionData jdData) {
        return chatClient.prompt()
                .system(optimizeResumeSystemPrompt)
                .user(u -> u.text(optimizeResumeUserPrompt)
                        .param("resume", resumeData.toString())
                        .param("jd", jdData.toString()))
                .call()
                .entity(OptimizationResponse.class);
    }
}