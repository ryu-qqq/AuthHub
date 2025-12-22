package com.ryuqq.authhub.adapter.in.rest.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * ApiResponse - 표준 API 성공 응답 래퍼
 *
 * <p>모든 REST API 성공 응답의 일관된 형식을 제공합니다.
 *
 * <p><strong>응답 전략:</strong>
 *
 * <ul>
 *   <li>성공 응답: ApiResponse&lt;T&gt; 사용
 *   <li>실패 응답: RFC 7807 ProblemDetail 사용 (GlobalExceptionHandler에서 처리)
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 성공 응답
 * ApiResponse<UserDto> response = ApiResponse.ofSuccess(userDto);
 *
 * // 데이터 없는 성공 응답
 * ApiResponse<Void> response = ApiResponse.ofSuccess();
 * }</pre>
 *
 * <p><strong>응답 형식:</strong>
 *
 * <pre>{@code
 * {
 *   "success": true,
 *   "data": { ... },
 *   "timestamp": "2025-10-23T10:30:00",
 *   "requestId": "req-123456"
 * }
 * }</pre>
 *
 * @param <T> 응답 데이터 타입
 * @author ryu-qqq
 * @since 2025-10-23
 */
@Schema(description = "표준 API 성공 응답 래퍼")
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부") boolean success,
        @Schema(description = "응답 데이터") T data,
        @Schema(description = "응답 시간") LocalDateTime timestamp,
        @Schema(description = "요청 ID") String requestId) {

    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 ApiResponse
     * @author ryu-qqq
     * @since 2025-10-23
     */
    public static <T> ApiResponse<T> ofSuccess(T data) {
        return new ApiResponse<>(true, data, LocalDateTime.now(), generateRequestId());
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     *
     * @param <T> 데이터 타입
     * @return 성공 ApiResponse
     * @author ryu-qqq
     * @since 2025-10-23
     */
    public static <T> ApiResponse<T> ofSuccess() {
        return ofSuccess(null);
    }

    /**
     * Request ID 생성
     *
     * <p>실제 운영 환경에서는 MDC나 분산 추적 시스템의 Trace ID를 사용하는 것이 좋습니다.
     *
     * @return Request ID
     * @author ryu-qqq
     * @since 2025-10-23
     */
    private static String generateRequestId() {
        // TODO: MDC or Distributed Tracing ID 사용 권장
        return "req-" + System.currentTimeMillis();
    }
}
