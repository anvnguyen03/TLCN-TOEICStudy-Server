package com.toeic.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.TestAnalyticsDTO;
import com.toeic.dto.TestAnalyticsDTO.ImprovementStatsDTO;
import com.toeic.dto.TestAnalyticsDTO.OverallStatsDTO;
import com.toeic.dto.TestAnalyticsDTO.ScoreHistoryDTO;
import com.toeic.dto.TestAnalyticsDTO.PartPerformanceDTO;
import com.toeic.dto.TestAnalyticsDTO.recentTestsDTO;
import com.toeic.entity.Part;
import com.toeic.entity.UserAnswer;
import com.toeic.entity.UserResult;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.PartRepository;
import com.toeic.repository.UserAnswerRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.AnalyticService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {

    private final UserResultRepository userResultRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final PartRepository partRepository;

    @Override
    @Transactional(readOnly = true)
    public TestAnalyticsDTO getUserTestAnalytics(Long userId) {
        // Get all user results ordered by completion date
        List<UserResult> userResults = userResultRepository.findByUserId(userId);
        if (userResults.isEmpty()) {
            throw new ResourceNotFoundException("No test results found for user");
        }

        // Sort results by completion date
        userResults.sort(Comparator.comparing(UserResult::getCompleted_at));

        TestAnalyticsDTO analytics = new TestAnalyticsDTO();
        
        // Get latest and previous results
        UserResult latestResult = userResults.get(userResults.size() - 1);
        UserResult previousResult = userResults.size() > 1 ? userResults.get(userResults.size() - 2) : latestResult;
        
        // Basic scores
        analytics.setCurrentScore(latestResult.getTotal_score());
        analytics.setPreviousScore(previousResult.getTotal_score());
        analytics.setMaxPossibleScore(990); // TOEIC max score
        analytics.setTestsTaken(userResults.size());
        
        // Calculate part-wise performance using all user results
        List<PartPerformanceDTO> partPerformance = calculatePartPerformance(userResults);
        analytics.setPartPerformance(partPerformance);
        
        // Find strongest and weakest parts
        PartPerformanceDTO strongestPart = partPerformance.stream()
            .max(Comparator.comparing(PartPerformanceDTO::getUserAccuracy))
            .orElseThrow();
        PartPerformanceDTO weakestPart = partPerformance.stream()
            .min(Comparator.comparing(PartPerformanceDTO::getUserAccuracy))
            .orElseThrow();
        
        analytics.setStrongestPart("Part " + strongestPart.getPart() + " - " + strongestPart.getName());
        analytics.setWeakestPart("Part " + weakestPart.getPart() + " - " + weakestPart.getName());
        
        // Improvement stats
        ImprovementStatsDTO improvementStats = new ImprovementStatsDTO();
        improvementStats.setTotalImprovement((int) (latestResult.getTotal_score() - userResults.get(0).getTotal_score()));
        improvementStats.setImprovementTrend(determineImprovementTrend(userResults));
        analytics.setImprovementStats(improvementStats);
        
        // Overall stats
        OverallStatsDTO overallStats = new OverallStatsDTO();
        overallStats.setTotalTestsCompleted(userResults.size());
        overallStats.setTotalQuestionsAnswered(userResults.stream()
            .mapToInt(r -> r.getCorrect_answers() + r.getIncorrect_answers() + r.getSkipped_answers())
            .sum());
        overallStats.setAverageAccuracy((float) Math.round(userResults.stream()
            .mapToDouble(UserResult::getAccuracy)
            .average()
            .orElse(0) * 100) / 100.0);
        overallStats.setTotalStudyTime(userResults.stream()
            .mapToInt(UserResult::getCompletion_time)
            .sum() / 60); // Convert to minutes
        overallStats.setListeningAccuracy((float) Math.round(userResults.stream()
            .mapToDouble(r -> (double) r.getListening_corrects() / 100 * 100)
            .average()
            .orElse(0) * 100) / 100.0);
        overallStats.setReadingAccuracy((float) Math.round(userResults.stream()
            .mapToDouble(r -> (double) r.getReading_corrects() / 100 * 100)
            .average()
            .orElse(0) * 100) / 100.0);
        overallStats.setBestScore(userResults.stream()
            .mapToInt(UserResult::getTotal_score)
            .max()
            .orElse(0));
        overallStats.setAverageScore((int) userResults.stream()
            .mapToInt(UserResult::getTotal_score)
            .average()
            .orElse(0));
        analytics.setOverallStats(overallStats);
        
        // Score history - group and average by month
        List<ScoreHistoryDTO> scoreHistory = userResults.stream()
            .collect(Collectors.groupingBy(
                result -> result.getCompleted_at().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    monthResults -> {
                        ScoreHistoryDTO history = new ScoreHistoryDTO();
                        history.setDate(monthResults.get(0).getCompleted_at().format(DateTimeFormatter.ofPattern("yyyy-MM")));
                        history.setListening((int) monthResults.stream()
                            .mapToInt(UserResult::getListening_score)
                            .average()
                            .orElse(0));
                        history.setReading((int) monthResults.stream()
                            .mapToInt(UserResult::getReading_score)
                            .average()
                            .orElse(0));
                        history.setTotal((int) monthResults.stream()
                            .mapToInt(UserResult::getTotal_score)
                            .average()
                            .orElse(0));
                        return history;
                    }
                )))
            .values()
            .stream()
            .collect(Collectors.toList());
        analytics.setScoreHistory(scoreHistory);

        // Recent tests (last 4 tests)
        List<recentTestsDTO> recentTests = userResults.stream()
            .limit(Math.min(4, userResults.size()))
            .map(result -> {
                recentTestsDTO test = new recentTestsDTO();
                test.setId(result.getId());
                test.setTitle(result.getTest().getTitle());
                test.setDate(result.getCompleted_at().toLocalDate());
                test.setListening(result.getListening_score());
                test.setReading(result.getReading_score());
                test.setTotal(result.getTotal_score());
                test.setTimeToComplete(result.getCompletion_time());
                return test;
            })
            .collect(Collectors.toList());

        analytics.setRecentTests(recentTests);
        
        // Sort recent tests by date
        recentTests.sort(Comparator.comparing(recentTestsDTO::getDate));
        
        return analytics;
    }
    
    private List<PartPerformanceDTO> calculatePartPerformance(List<UserResult> userResults) {
        List<PartPerformanceDTO> partPerformance = new ArrayList<>();
        
        // Get all user answers from all results
        List<UserAnswer> allUserAnswers = userResults.stream()
            .flatMap(result -> userAnswerRepository.findByUserResultId(result.getId()).stream())
            .collect(Collectors.toList());
        
        // Group answers by part number
        Map<Integer, List<UserAnswer>> answersByPart = allUserAnswers.stream()
            .collect(Collectors.groupingBy(answer -> answer.getQuestion().getPart().getPart_num()));
        
        // Calculate accuracy for each part
        for (int partNum = 1; partNum <= 7; partNum++) {
            List<UserAnswer> partAnswers = answersByPart.getOrDefault(partNum, new ArrayList<>());
            double userAccuracy = partAnswers.isEmpty() ? 0 :
                (double) partAnswers.stream().filter(UserAnswer::getIsCorrect).count() / partAnswers.size() * 100;
            
            // Calculate average accuracy for this part across all users
            double avgAccuracy = calculateAverageAccuracyForPart(partNum);
            
            PartPerformanceDTO performance = new PartPerformanceDTO();
            performance.setPart(partNum);
            performance.setName(getPartName(partNum));
            performance.setUserAccuracy(Math.round(userAccuracy * 100.0) / 100.0);
            performance.setAvgAccuracy(Math.round(avgAccuracy * 100.0) / 100.0);
            partPerformance.add(performance);
        }
        
        return partPerformance;
    }
    
    private double calculateAverageAccuracyForPart(int partNum) {
        // Get all user results
        List<UserResult> allResults = userResultRepository.findAll();
        
        // Get all user answers for this part
        List<UserAnswer> allPartAnswers = allResults.stream()
            .flatMap(result -> userAnswerRepository.findByUserResultId(result.getId()).stream())
            .filter(answer -> answer.getQuestion().getPart().getPart_num() == partNum)
            .collect(Collectors.toList());
        
        if (allPartAnswers.isEmpty()) {
            return 0.0;
        }
        
        // Calculate average accuracy
        return (double) allPartAnswers.stream().filter(UserAnswer::getIsCorrect).count() 
            / allPartAnswers.size() * 100;
    }
    
    private String getPartName(int partNum) {
        switch (partNum) {
            case 1: return "Photographs";
            case 2: return "Question-Response";
            case 3: return "Conversations";
            case 4: return "Talks";
            case 5: return "Incomplete Sentences";
            case 6: return "Text Completion";
            case 7: return "Reading Comprehension";
            default: return "Unknown Part";
        }
    }
    
    private String determineImprovementTrend(List<UserResult> results) {
        if (results.size() < 2) return "stable";
        
        // Get last 3 results or all if less than 3
        List<UserResult> recentResults = results.subList(Math.max(0, results.size() - 3), results.size());
        
        // Calculate average improvement
        double totalImprovement = 0;
        for (int i = 1; i < recentResults.size(); i++) {
            totalImprovement += recentResults.get(i).getTotal_score() - recentResults.get(i-1).getTotal_score();
        }
        double avgImprovement = totalImprovement / (recentResults.size() - 1);
        
        if (avgImprovement > 10) return "upward";
        if (avgImprovement < -10) return "declining";
        return "stable";
    }
}
