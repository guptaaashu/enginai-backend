package com.enginai.backend.course.dto;

import java.util.Map;

public record QuizSubmitRequest(Map<Long, String> answers) {}
