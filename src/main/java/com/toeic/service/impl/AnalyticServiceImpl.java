package com.toeic.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toeic.dto.response.AdminDashboardStats;
import com.toeic.dto.response.TestAnalyticsDTO;
import com.toeic.dto.response.TestAnalyticsDTO.ImprovementStatsDTO;
import com.toeic.dto.response.TestAnalyticsDTO.OverallStatsDTO;
import com.toeic.dto.response.TestAnalyticsDTO.PartPerformanceDTO;
import com.toeic.dto.response.TestAnalyticsDTO.ScoreHistoryDTO;
import com.toeic.dto.response.TestAnalyticsDTO.recentTestsDTO;
import com.toeic.entity.Course;
import com.toeic.entity.CourseEnrollment;
import com.toeic.entity.Part;
import com.toeic.entity.UserAnswer;
import com.toeic.entity.UserResult;
import com.toeic.exception.ResourceNotFoundException;
import com.toeic.repository.CourseEnrollmentRepository;
import com.toeic.repository.CourseRepository;
import com.toeic.repository.PartRepository;
import com.toeic.repository.UserAnswerRepository;
import com.toeic.repository.UserRepository;
import com.toeic.repository.UserResultRepository;
import com.toeic.service.AnalyticService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {

    private final UserResultRepository userResultRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

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

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getAdminDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();

        // Get last month users
        stats.setLastMonthUsers((int) userRepository.findAll().stream()
            .filter(user -> user.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) && user.getCreatedAt().isBefore(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
            .count());
        
        // Get last month test attempts
        stats.setLastMonthTestAttemps((int) userResultRepository.findAll().stream()
            .filter(result -> result.getCompleted_at().isAfter(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) && result.getCompleted_at().isBefore(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
            .count());
        
        // Get last month course enrollments
        stats.setLastMonthCourseEnrollments((int) courseEnrollmentRepository.findAll().stream()
            .filter(enrollment -> enrollment.getEnrolledAt().isAfter(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) && enrollment.getEnrolledAt().isBefore(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
            .count());
        
        // Get last month revenue
        stats.setLastMonthRevenue((int) courseEnrollmentRepository.findAll().stream()
            .filter(enrollment -> enrollment.getEnrolledAt().isAfter(LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) && enrollment.getEnrolledAt().isBefore(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)))
            .mapToDouble(enrollment -> enrollment.getCourse().getPrice().doubleValue())
            .sum());

        // Get total users
        stats.setTotalUsers((int) userRepository.count());

        // Get total test attempts
        stats.setTestAttemps((int) userResultRepository.count());
        
        // Get total course enrollments
        stats.setCourseEnrollments((int) courseEnrollmentRepository.count());
        
        // Calculate total and monthly revenue from course enrollments
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findAll();
        double totalRevenue = enrollments.stream()
            .mapToDouble(enrollment -> enrollment.getCourse().getPrice().doubleValue())
            .sum();
        stats.setTotalRevenue((int) totalRevenue);
        
        // Calculate monthly revenue (this month)
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        
        double currentMonthRevenue = enrollments.stream()
            .filter(enrollment -> enrollment.getEnrolledAt().isAfter(monthStart) && enrollment.getEnrolledAt().isBefore(monthEnd))
            .mapToDouble(enrollment -> enrollment.getCourse().getPrice().doubleValue())
            .sum();
        stats.setMonthlyRevenue((int) currentMonthRevenue);
        
        // Get top courses by rating
        List<Course> courses = courseRepository.findAll();
        List<AdminDashboardStats.TopCourse> topCourses = courses.stream()
            .map(course -> {
                AdminDashboardStats.TopCourse topCourse = stats.new TopCourse();
                topCourse.setId(course.getId().intValue());
                topCourse.setTitle(course.getTitle());
                topCourse.setEnrollments(course.getEnrollments().size());
                topCourse.setRevenue((int) (course.getPrice().doubleValue() * course.getEnrollments().size()));
                
                // Calculate average rating
                double avgRating = course.getReviews().stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0);
                topCourse.setRating(Math.round(avgRating * 100.0) / 100.0);
                
                return topCourse;
            })
            .sorted(Comparator.comparing(AdminDashboardStats.TopCourse::getRating).reversed())
            .limit(3)
            .collect(Collectors.toList());
        stats.setTopCourses(topCourses);
        
        // Calculate revenue trend for last 6 months
        AdminDashboardStats.RevenueTrend revenueTrend = new AdminDashboardStats.RevenueTrend();
        List<String> revenueLabels = new ArrayList<>();
        List<Integer> revenueData = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart1 = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd1 = monthStart1.plusMonths(1);
            
            double monthlyRevenue = enrollments.stream()
                .filter(enrollment -> enrollment.getEnrolledAt().isAfter(monthStart1) && enrollment.getEnrolledAt().isBefore(monthEnd1))
                .mapToDouble(enrollment -> enrollment.getCourse().getPrice().doubleValue())
                .sum();
            
            revenueLabels.add(monthStart1.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            revenueData.add((int) monthlyRevenue);
        }
        
        revenueTrend.setLabels(revenueLabels);
        revenueTrend.setData(revenueData);
        stats.setRevenueTrend(revenueTrend);
        
        // Calculate user growth for last 6 months
        AdminDashboardStats.UserGrowth userGrowth = new AdminDashboardStats.UserGrowth();
        List<String> userLabels = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        List<Integer> activeUsers = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart2 = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd2 = monthStart2.plusMonths(1);
            
            // Count new users in this month
            long newUsersCount = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt().isAfter(monthStart2) && user.getCreatedAt().isBefore(monthEnd2))
                .count();
            
            // Count active users in this month
            long activeUsersCount = userRepository.findAll().stream()
                .filter(user -> user.isActivated() && 
                              user.getCreatedAt().isAfter(monthStart2) && 
                              user.getCreatedAt().isBefore(monthEnd2))
                .count();
            
            userLabels.add(monthStart2.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            newUsers.add((int) newUsersCount);
            activeUsers.add((int) activeUsersCount);
        }
        
        userGrowth.setLabels(userLabels);
        userGrowth.setNewUsers(newUsers);
        userGrowth.setActiveUsers(activeUsers);
        stats.setUserGrowth(userGrowth);
        
        // Calculate test score distribution
        AdminDashboardStats.TestScoreDistribution scoreDistribution = new AdminDashboardStats.TestScoreDistribution();
        List<String> scoreLabels = Arrays.asList("0-200", "201-400", "401-600", "601-800", "801-990");
        List<Integer> scoreData = new ArrayList<>();
        
        List<UserResult> allResults = userResultRepository.findAll();
        for (String range : scoreLabels) {
            String[] bounds = range.split("-");
            int lower = Integer.parseInt(bounds[0]);
            int upper = Integer.parseInt(bounds[1]);
            
            long count = allResults.stream()
                .filter(result -> result.getTotal_score() >= lower && result.getTotal_score() <= upper)
                .count();
            
            scoreData.add((int) count);
        }
        
        scoreDistribution.setLabels(scoreLabels);
        scoreDistribution.setData(scoreData);
        stats.setTestScoreDistribution(scoreDistribution);
        
        return stats;
    }

}
