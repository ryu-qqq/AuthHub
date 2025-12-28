package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * RoleDetailApiResponse - 역할 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 역할에 할당된 권한 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>RoleSummaryApiResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>permissions 목록 추가 (할당된 권한 상세 정보)
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
 * @see RoleSummaryApiResponse 목록 조회용 응답 DTO
 * @see RolePermissionSummaryApiResponse 권한 요약 정보
 */
@Schema(description = "역할 상세 응답 (Admin용)")
public record RoleDetailApiResponse(
        @Schema(description = "역할 ID") String roleId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "테넌트 이름") String tenantName,
        @Schema(description = "역할 이름") String name,
        @Schema(description = "역할 설명") String description,
        @Schema(description = "역할 범위 (GLOBAL, TENANT, ORGANIZATION)") String scope,
        @Schema(description = "역할 유형 (SYSTEM, CUSTOM)") String type,
        @Schema(description = "할당된 권한 목록") List<RolePermissionSummaryApiResponse> permissions,
        @Schema(description = "역할이 할당된 사용자 수") int userCount,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
