package com.ryuqq.authhub.application.role.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * RoleDetailResponse - 역할 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 역할에 할당된 권한 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>RoleSummaryResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>permissions 목록 추가 (할당된 권한 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID (GLOBAL 범위인 경우 null)
 * @param tenantName 테넌트 이름 (GLOBAL 범위인 경우 null)
 * @param name 역할 이름
 * @param description 역할 설명
 * @param scope 역할 범위 (GLOBAL/TENANT/ORGANIZATION)
 * @param type 역할 유형 (SYSTEM/CUSTOM)
 * @param permissions 할당된 권한 목록
 * @param userCount 역할이 할당된 사용자 수
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author development-team
 * @since 1.0.0
 * @see RoleSummaryResponse 목록 조회용 응답 DTO
 */
public record RoleDetailResponse(
        UUID roleId,
        UUID tenantId,
        String tenantName,
        String name,
        String description,
        String scope,
        String type,
        List<RolePermissionSummary> permissions,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * RolePermissionSummary - 역할 상세에 포함되는 권한 요약 정보
     *
     * @param permissionId 권한 ID
     * @param name 권한 이름
     * @param description 권한 설명
     * @param resource 리소스 타입
     * @param action 액션
     */
    public record RolePermissionSummary(
            UUID permissionId, String name, String description, String resource, String action) {}
}
