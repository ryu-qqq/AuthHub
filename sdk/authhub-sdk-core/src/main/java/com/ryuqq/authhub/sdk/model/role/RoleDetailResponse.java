package com.ryuqq.authhub.sdk.model.role;

import java.time.Instant;
import java.util.List;

/**
 * 역할 상세 조회 응답 (Admin용).
 *
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param name 역할 이름
 * @param description 역할 설명
 * @param scope 역할 범위 (GLOBAL, TENANT, ORGANIZATION)
 * @param type 역할 유형 (SYSTEM, CUSTOM)
 * @param permissions 할당된 권한 목록
 * @param userCount 역할이 할당된 사용자 수
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 */
public record RoleDetailResponse(
        String roleId,
        String tenantId,
        String tenantName,
        String name,
        String description,
        String scope,
        String type,
        List<RolePermissionSummaryResponse> permissions,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
