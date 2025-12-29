package com.ryuqq.authhub.sdk.model.common;

import java.time.LocalDateTime;

/**
 * AuthHub API 표준 응답 래퍼.
 *
 * @param <T> 응답 데이터 타입
 */
public record ApiResponse<T>(boolean success, T data, LocalDateTime timestamp, String requestId) {}
