package com.ryuqq.authhub.domain.role.vo;

/**
 * RoleSearchField - 역할 검색 필드
 *
 * <p>역할 검색 시 사용할 수 있는 검색 대상 필드를 정의합니다.
 *
 * <ul>
 *   <li>NAME: 역할 이름으로 검색 (예: "SUPER_ADMIN")
 *   <li>DISPLAY_NAME: 표시 이름으로 검색 (예: "슈퍼 관리자")
 *   <li>DESCRIPTION: 설명으로 검색
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleSearchField {

    /** 역할 이름 검색 (기본값) */
    NAME,

    /** 표시 이름 검색 */
    DISPLAY_NAME,

    /** 설명 검색 */
    DESCRIPTION;

    /**
     * 기본 검색 필드 반환
     *
     * @return NAME (기본값)
     */
    public static RoleSearchField defaultField() {
        return NAME;
    }

    /**
     * 문자열로부터 RoleSearchField 파싱
     *
     * <p>null이거나 빈 문자열이면 기본값(NAME)을 반환합니다.
     *
     * <p>매칭되지 않는 값이면 기본값(NAME)을 반환합니다.
     *
     * @param value 검색 필드 문자열 (nullable)
     * @return RoleSearchField enum
     */
    public static RoleSearchField fromString(String value) {
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
     * 이름 검색 필드인지 확인
     *
     * @return NAME이면 true
     */
    public boolean isName() {
        return this == NAME;
    }

    /**
     * 표시 이름 검색 필드인지 확인
     *
     * @return DISPLAY_NAME이면 true
     */
    public boolean isDisplayName() {
        return this == DISPLAY_NAME;
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
