package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * TenantDetailApiResponse - 테넌트 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 테넌트에 소속된 조직 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>TenantSummaryApiResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>organizations 목록 추가 (소속 조직 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
 * </ul>
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
 * @see TenantSummaryApiResponse 목록 조회용 응답 DTO
 * @see TenantOrganizationSummaryApiResponse 조직 요약 정보
 */
@Schema(description = "테넌트 상세 응답 (Admin용)")
public record TenantDetailApiResponse(
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "테넌트 이름") String name,
        @Schema(description = "테넌트 상태 (ACTIVE, INACTIVE)") String status,
        @Schema(description = "소속 조직 목록 (최근 N개)")
                List<TenantOrganizationSummaryApiResponse> organizations,
        @Schema(description = "테넌트에 소속된 전체 조직 수") int organizationCount,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
