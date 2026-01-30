package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * UserApiResponse - 사용자 조회 응답 DTO
 *
 * <p>사용자 정보를 반환하는 응답 DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Domain 타입 직접 의존 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 조회 응답")
public record UserApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "소속 조직 ID") String organizationId,
        @Schema(description = "로그인 식별자") String identifier,
        @Schema(description = "전화번호") String phoneNumber,
        @Schema(description = "사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)") String status,
        @Schema(description = "생성 시각") Instant createdAt,
        @Schema(description = "수정 시각") Instant updatedAt) {}
