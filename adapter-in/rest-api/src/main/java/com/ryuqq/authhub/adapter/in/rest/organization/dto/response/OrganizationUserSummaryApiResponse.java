package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * OrganizationUserSummaryApiResponse - 조직 상세 조회 시 포함되는 사용자 요약 정보 (Admin용)
 *
 * <p>조직에 소속된 사용자의 핵심 정보만 포함합니다.
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
 * @see OrganizationDetailApiResponse 상세 조회용 응답 DTO
 */
@Schema(description = "조직 사용자 요약 정보")
public record OrganizationUserSummaryApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "사용자 이메일") String email,
        @Schema(description = "소속 일시") Instant createdAt) {}
