package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * UserApiResponse - 사용자 응답 DTO
 *
 * <p>사용자 조회 API의 응답 본문을 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 응답")
public record UserApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "조직 ID") String organizationId,
        @Schema(description = "사용자 식별자") String identifier,
        @Schema(description = "핸드폰 번호") String phoneNumber,
        @Schema(description = "사용자 상태") String status,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
