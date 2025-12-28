package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * TenantOrganizationSummaryApiResponse - 테넌트 상세 조회 시 조직 요약 정보
 *
 * <p>테넌트 상세 조회 응답에 포함되는 조직 요약 정보입니다. 테넌트에 소속된 조직의 기본 정보를 표시합니다.
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
 * @see TenantDetailApiResponse 상세 조회용 응답 DTO
 */
@Schema(description = "테넌트 소속 조직 요약 정보")
public record TenantOrganizationSummaryApiResponse(
        @Schema(description = "조직 ID") String organizationId,
        @Schema(description = "조직 이름") String name,
        @Schema(description = "조직 상태 (ACTIVE, INACTIVE, SUSPENDED)") String status,
        @Schema(description = "생성 일시") Instant createdAt) {}
