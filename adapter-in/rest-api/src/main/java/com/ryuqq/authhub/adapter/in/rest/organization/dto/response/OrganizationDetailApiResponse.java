package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * OrganizationDetailApiResponse - 조직 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 조직에 소속된 사용자 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>OrganizationSummaryApiResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>users 목록 추가 (소속 사용자 상세 정보)
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
 * @see OrganizationSummaryApiResponse 목록 조회용 응답 DTO
 * @see OrganizationUserSummaryApiResponse 사용자 요약 정보
 */
@Schema(description = "조직 상세 응답 (Admin용)")
public record OrganizationDetailApiResponse(
        @Schema(description = "조직 ID") String organizationId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "테넌트 이름") String tenantName,
        @Schema(description = "조직 이름") String name,
        @Schema(description = "조직 상태 (ACTIVE, INACTIVE, SUSPENDED)") String status,
        @Schema(description = "소속 사용자 목록 (최근 N명)") List<OrganizationUserSummaryApiResponse> users,
        @Schema(description = "조직에 소속된 전체 사용자 수") int userCount,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
