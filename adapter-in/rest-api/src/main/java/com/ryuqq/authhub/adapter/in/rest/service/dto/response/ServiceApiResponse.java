package com.ryuqq.authhub.adapter.in.rest.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ServiceApiResponse - Service 조회 API Response
 *
 * <p>Service 조회 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * <p>ADTO-006: Response DTO에 createdAt, updatedAt 시간 필드 포함.
 *
 * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
 *
 * @param serviceId 서비스 ID
 * @param serviceCode 서비스 코드
 * @param name 서비스 이름
 * @param description 서비스 설명
 * @param status 서비스 상태 (ACTIVE, INACTIVE)
 * @param createdAt 생성 시각 (ISO 8601 포맷 문자열)
 * @param updatedAt 수정 시각 (ISO 8601 포맷 문자열)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Service 조회 응답")
public record ServiceApiResponse(
        @Schema(description = "Service ID", example = "1") Long serviceId,
        @Schema(description = "서비스 코드", example = "SVC_STORE") String serviceCode,
        @Schema(description = "서비스 이름", example = "자사몰") String name,
        @Schema(description = "서비스 설명", example = "자사몰 서비스") String description,
        @Schema(description = "서비스 상태", example = "ACTIVE") String status,
        @Schema(description = "생성 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String createdAt,
        @Schema(description = "수정 시각 (ISO 8601)", example = "2024-01-15T10:30:00+09:00")
                String updatedAt) {}
