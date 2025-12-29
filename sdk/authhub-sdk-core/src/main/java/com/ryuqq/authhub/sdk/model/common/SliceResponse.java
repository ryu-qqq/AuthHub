package com.ryuqq.authhub.sdk.model.common;

import java.util.List;

/**
 * Cursor 기반 슬라이스 응답.
 *
 * @param <T> 콘텐츠 타입
 */
public record SliceResponse<T>(List<T> content, int size, boolean hasNext, String nextCursor) {}
