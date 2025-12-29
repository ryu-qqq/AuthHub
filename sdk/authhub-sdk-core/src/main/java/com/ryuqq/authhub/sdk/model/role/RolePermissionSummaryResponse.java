package com.ryuqq.authhub.sdk.model.role;

/**
 * 역할 상세 조회 시 포함되는 권한 요약 정보.
 *
 * @param permissionId 권한 ID
 * @param permissionKey 권한 키
 * @param description 권한 설명
 * @param resource 리소스
 * @param action 액션
 */
public record RolePermissionSummaryResponse(
        String permissionId,
        String permissionKey,
        String description,
        String resource,
        String action) {}
