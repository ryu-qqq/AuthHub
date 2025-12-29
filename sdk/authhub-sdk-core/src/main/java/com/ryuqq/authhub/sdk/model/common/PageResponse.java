package com.ryuqq.authhub.sdk.model.common;

import java.util.List;

/**
 * Offset 기반 페이지 응답.
 *
 * @param <T> 콘텐츠 타입
 */
public record PageResponse<T>(
        List<T> content, int page, int size, long totalElements, int totalPages, boolean hasNext) {}
