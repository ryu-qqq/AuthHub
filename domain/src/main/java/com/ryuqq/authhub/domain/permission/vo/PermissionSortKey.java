package com.ryuqq.authhub.domain.permission.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * PermissionSortKey - 권한 정렬 키 Value Object
 *
 * <p>권한 목록 조회 시 사용할 수 있는 정렬 기준을 정의합니다.
 *
 * <p><strong>지원 정렬 필드:</strong>
 *
 * <ul>
 *   <li>{@link #PERMISSION_ID}: 권한 ID
 *   <li>{@link #PERMISSION_KEY}: 권한 키 (알파벳순)
 *   <li>{@link #RESOURCE}: 리소스명
 *   <li>{@link #CREATED_AT}: 생성일시 (기본값)
 *   <li>{@link #UPDATED_AT}: 수정일시
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionSortKey implements SortKey {

    /** 권한 ID 기준 정렬 */
    PERMISSION_ID("permissionId"),

    /** 권한 키 기준 정렬 (알파벳순) */
    PERMISSION_KEY("permissionKey"),

    /** 리소스명 기준 정렬 */
    RESOURCE("resource"),

    /** 생성일시 기준 정렬 (기본값) */
    CREATED_AT("createdAt"),

    /** 수정일시 기준 정렬 */
    UPDATED_AT("updatedAt");

    private final String fieldName;

    PermissionSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 기본 정렬 키 반환
     *
     * @return CREATED_AT (기본값)
     */
    public static PermissionSortKey defaultKey() {
        return CREATED_AT;
    }

    /**
     * 문자열로부터 PermissionSortKey 파싱 (대소문자 무관)
     *
     * @param value 문자열 (null이거나 유효하지 않으면 기본값 반환)
     * @return PermissionSortKey
     */
    public static PermissionSortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        String normalized = value.toUpperCase().trim().replace("-", "_");
        for (PermissionSortKey key : values()) {
            if (key.name().equals(normalized) || key.fieldName.equalsIgnoreCase(value.trim())) {
                return key;
            }
        }
        return defaultKey();
    }
}
