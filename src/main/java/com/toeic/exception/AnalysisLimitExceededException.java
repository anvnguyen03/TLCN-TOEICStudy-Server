package com.toeic.exception;

public class AnalysisLimitExceededException extends RuntimeException {
    public AnalysisLimitExceededException(String message) {
        super(message);
    }
} 