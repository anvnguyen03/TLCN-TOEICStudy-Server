package com.toeic.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.response.*;
import com.toeic.entity.*;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.exception.AnalysisLimitExceededException;
import com.toeic.repository.*;
import com.toeic.service.AITestAnalysisService;
import com.toeic.utils.OpenAIUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AITestAnalysisServiceImpl implements AITestAnalysisService {

    private final UserRepository userRepository;
    private final AITestAnalysisRepository aiTestAnalysisRepository;
    private final OpenAIUtils openAIUtils;

    private static final int MAX_ANALYSIS_PER_USER = 5;

    @Override
    @Transactional
    public AITestAnalysisDTO analyzeTestResult(Long userId, TestAnalyticsDTO analytics) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user has exceeded analysis limit
        long analysisCount = aiTestAnalysisRepository.countByUser(user);
        if (analysisCount >= MAX_ANALYSIS_PER_USER) {
            throw new AnalysisLimitExceededException("You have reached the maximum limit of AI analysis (5 times)");
        }

        AITestAnalysisDTO analysis = new AITestAnalysisDTO();
        
        // Generate concise feedback based on overall statistics
        String overallFeedback = generateOverallFeedback(analytics);
        analysis.setOverallFeedback(overallFeedback);
        
        // Generate concise part analysis
        String partAnalysis = generatePartAnalysis(analytics);
        analysis.setPartAnalysis(partAnalysis);
        
        // Generate concise skill analysis
        String skillAnalysis = generateSkillAnalysis(analytics);
        analysis.setSkillAnalysis(skillAnalysis);
        
        // Generate concise improvement suggestions
        String improvementSuggestions = generateImprovementSuggestions(analytics);
        analysis.setImprovementSuggestions(improvementSuggestions);
        
        // Identify strengths and weaknesses
        List<String> strengths = identifyStrengths(analytics);
        List<String> weaknesses = identifyWeaknesses(analytics);
        analysis.setStrengths(strengths);
        analysis.setWeaknesses(weaknesses);

        // Save the analysis
        saveAnalysis(user, analysis);
        
        return analysis;
    }

    @Override
    @Transactional(readOnly = true)
    public AITestAnalysisDTO getLatestAnalysis(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AITestAnalysis analysis = aiTestAnalysisRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("No AI analysis found for this user"));

        return convertToDTO(analysis);
    }

    private String generateOverallFeedback(TestAnalyticsDTO analytics) {
        Map<String, Object> data = new HashMap<>();
        data.put("currentScore", analytics.getCurrentScore());
        data.put("previousScore", analytics.getPreviousScore());
        data.put("improvement", analytics.getImprovementStats().getTotalImprovement());
        data.put("testsTaken", analytics.getTestsTaken());
        
        return callOpenAI("Generate concise TOEIC test feedback", data);
    }

    private String generatePartAnalysis(TestAnalyticsDTO analytics) {
        Map<String, Object> data = new HashMap<>();
        data.put("partPerformance", analytics.getPartPerformance());
        data.put("strongestPart", analytics.getStrongestPart());
        data.put("weakestPart", analytics.getWeakestPart());
        
        return callOpenAI("Generate concise part analysis", data);
    }

    private String generateSkillAnalysis(TestAnalyticsDTO analytics) {
        Map<String, Object> data = new HashMap<>();
        data.put("listeningAccuracy", analytics.getOverallStats().getListeningAccuracy());
        data.put("readingAccuracy", analytics.getOverallStats().getReadingAccuracy());
        
        return callOpenAI("Generate concise skill analysis", data);
    }

    private String generateImprovementSuggestions(TestAnalyticsDTO analytics) {
        Map<String, Object> data = new HashMap<>();
        data.put("weakestPart", analytics.getWeakestPart());
        data.put("listeningAccuracy", analytics.getOverallStats().getListeningAccuracy());
        data.put("readingAccuracy", analytics.getOverallStats().getReadingAccuracy());
        
        return callOpenAI("Generate concise improvement suggestions", data);
    }

    private List<String> identifyStrengths(TestAnalyticsDTO analytics) {
        List<String> strengths = new ArrayList<>();
        
        // Add strongest part if accuracy is good
        if (analytics.getPartPerformance().stream()
                .anyMatch(part -> part.getPart() == getPartNumber(analytics.getStrongestPart()) 
                    && part.getUserAccuracy() > 70)) {
            strengths.add("Strong performance in " + analytics.getStrongestPart());
        }
        
        // Add listening strength if good
        if (analytics.getOverallStats().getListeningAccuracy() > 70) {
            strengths.add("Strong listening skills");
        }
        
        // Add reading strength if good
        if (analytics.getOverallStats().getReadingAccuracy() > 70) {
            strengths.add("Strong reading skills");
        }
        
        return strengths;
    }

    private List<String> identifyWeaknesses(TestAnalyticsDTO analytics) {
        List<String> weaknesses = new ArrayList<>();
        
        // Add weakest part if accuracy is poor
        if (analytics.getPartPerformance().stream()
                .anyMatch(part -> part.getPart() == getPartNumber(analytics.getWeakestPart()) 
                    && part.getUserAccuracy() < 50)) {
            weaknesses.add("Needs improvement in " + analytics.getWeakestPart());
        }
        
        // Add listening weakness if poor
        if (analytics.getOverallStats().getListeningAccuracy() < 50) {
            weaknesses.add("Needs improvement in listening skills");
        }
        
        // Add reading weakness if poor
        if (analytics.getOverallStats().getReadingAccuracy() < 50) {
            weaknesses.add("Needs improvement in reading skills");
        }
        
        return weaknesses;
    }

    private int getPartNumber(String partName) {
        switch (partName.toLowerCase()) {
            case "photographs": return 1;
            case "question-response": return 2;
            case "conversations": return 3;
            case "talks": return 4;
            case "incomplete sentences": return 5;
            case "text completion": return 6;
            case "reading comprehension": return 7;
            default: return 0;
        }
    }

    private String callOpenAI(String prompt, Object data) {
        // Define system prompts for different types of analysis
        Map<String, String> systemPrompts = Map.of(
            "Generate concise TOEIC test feedback", """
                You are a TOEIC test expert. Provide a brief, focused analysis of the test results.
                Keep feedback under 100 words. Focus on:
                1. Overall performance
                2. Key improvement areas
                3. One specific recommendation
                """,
                
            "Generate concise part analysis", """
                You are a TOEIC test expert. Analyze the performance for each part (1-7) of the TOEIC test.
                For each part, provide:
                1. A brief assessment of performance (strengths/weaknesses)
                2. One specific improvement tip
                Keep each part's analysis under 50 words.
                Format the response as:
                Part 1: [Name]
                [Assessment]
                [Improvement tip]

                Part 2: [Name]
                [Assessment]
                [Improvement tip]

                [Continue for all parts 1-7]
                """,
                
            "Generate concise skill analysis", """
                You are a TOEIC test expert. Provide a brief analysis of listening and reading skills.
                Keep feedback under 100 words per skill. Focus on:
                1. Current proficiency level
                2. One specific improvement area
                """,
                
            "Generate concise improvement suggestions", """
                You are a TOEIC test expert. Provide 3-4 specific, actionable suggestions for improvement.
                Keep each suggestion under 50 words. Focus on practical, achievable steps.
                """
        );

        String systemPrompt = systemPrompts.getOrDefault(prompt, 
            "You are a TOEIC test expert. Provide concise, focused feedback.");

        return openAIUtils.generateResponse(systemPrompt, data);
    }

    private void saveAnalysis(User user, AITestAnalysisDTO analysis) {
        AITestAnalysis aiAnalysis = new AITestAnalysis();
        aiAnalysis.setUser(user);
        aiAnalysis.setOverallFeedback(analysis.getOverallFeedback());
        aiAnalysis.setPartAnalysis(analysis.getPartAnalysis());
        aiAnalysis.setSkillAnalysis(analysis.getSkillAnalysis());
        aiAnalysis.setImprovementSuggestions(analysis.getImprovementSuggestions());
        aiAnalysis.setStrengths(String.join(", ", analysis.getStrengths()));
        aiAnalysis.setWeaknesses(String.join(", ", analysis.getWeaknesses()));
        
        aiTestAnalysisRepository.save(aiAnalysis);
    }

    private AITestAnalysisDTO convertToDTO(AITestAnalysis analysis) {
        AITestAnalysisDTO dto = new AITestAnalysisDTO();
        dto.setOverallFeedback(analysis.getOverallFeedback());
        dto.setPartAnalysis(analysis.getPartAnalysis());
        dto.setSkillAnalysis(analysis.getSkillAnalysis());
        dto.setImprovementSuggestions(analysis.getImprovementSuggestions());
        dto.setStrengths(Arrays.asList(analysis.getStrengths().split(", ")));
        dto.setWeaknesses(Arrays.asList(analysis.getWeaknesses().split(", ")));
        return dto;
    }
} 