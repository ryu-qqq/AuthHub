package com.ryuqq.authhub.domain.rolepermission.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * RolePermissionSortKey - 역할-권한 관계 정렬 키
 *
 * <p>역할-권한 관계 조회 시 사용할 수 있는 정렬 기준을 정의합니다.
 *
 * <p><strong>정렬 필드:</strong>
 *
 * <ul>
 *   <li>ROLE_PERMISSION_ID - 관계 ID 순
 *   <li>ROLE_ID - 역할 ID 순
 *   <li>PERMISSION_ID - 권한 ID 순
 *   <li>CREATED_AT - 생성일 순 (기본값)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RolePermissionSortKey implements SortKey {
    ROLE_PERMISSION_ID("rolePermissionId"),
    ROLE_ID("roleId"),
    PERMISSION_ID("permissionId"),
    CREATED_AT("createdAt");

    private final String fieldName;

    RolePermissionSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 RolePermissionSortKey 파싱 (실패 시 기본값)
     *
     * @param value 정렬 키 문자열 (nullable)
     * @return RolePermissionSortKey 또는 기본값 (CREATED_AT)
     */
    public static RolePermissionSortKey fromStringOrDefault(String value) {
        if (value == null || value.isBlank()) {
            return CREATED_AT;
        }
        try {
            return RolePermissionSortKey.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CREATED_AT;
        }
    }
}
