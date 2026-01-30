package com.ryuqq.authhub.domain.permission.vo;

/**
 * PermissionSearchField - 권한 검색 필드
 *
 * <p>권한 검색 시 사용할 수 있는 검색 대상 필드를 정의합니다.
 *
 * <ul>
 *   <li>PERMISSION_KEY: 권한 키로 검색 (예: "user:read")
 *   <li>RESOURCE: 리소스명으로 검색 (예: "user")
 *   <li>ACTION: 행위명으로 검색 (예: "read")
 *   <li>DESCRIPTION: 설명으로 검색
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionSearchField {

    /** 권한 키 검색 (기본값) */
    PERMISSION_KEY,

    /** 리소스명 검색 */
    RESOURCE,

    /** 행위명 검색 */
    ACTION,

    /** 설명 검색 */
    DESCRIPTION;

    /**
     * 기본 검색 필드 반환
     *
     * @return PERMISSION_KEY (기본값)
     */
    public static PermissionSearchField defaultField() {
        return PERMISSION_KEY;
    }

    /**
     * 문자열로부터 PermissionSearchField 파싱
     *
     * <p>null이거나 빈 문자열이면 기본값(PERMISSION_KEY)을 반환합니다.
     *
     * <p>매칭되지 않는 값이면 기본값(PERMISSION_KEY)을 반환합니다.
     *
     * @param value 검색 필드 문자열 (nullable)
     * @return PermissionSearchField enum
     */
    public static PermissionSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultField();
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultField();
        }
    }

    /**
     * 권한 키 검색 필드인지 확인
     *
     * @return PERMISSION_KEY이면 true
     */
    public boolean isPermissionKey() {
        return this == PERMISSION_KEY;
    }

    /**
     * 리소스 검색 필드인지 확인
     *
     * @return RESOURCE이면 true
     */
    public boolean isResource() {
        return this == RESOURCE;
    }

    /**
     * 행위 검색 필드인지 확인
     *
     * @return ACTION이면 true
     */
    public boolean isAction() {
        return this == ACTION;
    }

    /**
     * 설명 검색 필드인지 확인
     *
     * @return DESCRIPTION이면 true
     */
    public boolean isDescription() {
        return this == DESCRIPTION;
    }
}
