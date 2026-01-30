package com.ryuqq.authhub.domain.role.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * RoleSortKey - 역할 정렬 키 Value Object
 *
 * <p>역할 목록 조회 시 사용할 수 있는 정렬 기준을 정의합니다.
 *
 * <p><strong>지원 정렬 필드:</strong>
 *
 * <ul>
 *   <li>{@link #ROLE_ID}: 역할 ID
 *   <li>{@link #NAME}: 역할 이름 (알파벳순)
 *   <li>{@link #DISPLAY_NAME}: 표시 이름
 *   <li>{@link #CREATED_AT}: 생성일시 (기본값)
 *   <li>{@link #UPDATED_AT}: 수정일시
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleSortKey implements SortKey {

    /** 역할 ID 기준 정렬 */
    ROLE_ID("roleId"),

    /** 역할 이름 기준 정렬 (알파벳순) */
    NAME("name"),

    /** 표시 이름 기준 정렬 */
    DISPLAY_NAME("displayName"),

    /** 생성일시 기준 정렬 (기본값) */
    CREATED_AT("createdAt"),

    /** 수정일시 기준 정렬 */
    UPDATED_AT("updatedAt");

    private final String fieldName;

    RoleSortKey(String fieldName) {
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
    public static RoleSortKey defaultKey() {
        return CREATED_AT;
    }

    /**
     * 문자열로부터 RoleSortKey 파싱 (대소문자 무관)
     *
     * @param value 문자열 (null이거나 유효하지 않으면 기본값 반환)
     * @return RoleSortKey
     */
    public static RoleSortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        String normalized = value.toUpperCase().trim().replace("-", "_");
        for (RoleSortKey key : values()) {
            if (key.name().equals(normalized) || key.fieldName.equalsIgnoreCase(value.trim())) {
                return key;
            }
        }
        return defaultKey();
    }
}
