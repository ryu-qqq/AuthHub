package com.ryuqq.authhub.domain.role.vo;

import java.util.List;

/**
 * RoleType - 역할 유형 열거형
 *
 * <p>역할의 유형을 정의합니다.
 *
 * <ul>
 *   <li>SYSTEM: 시스템 기본 역할 (수정/삭제 불가) - 예: SUPER_ADMIN, TENANT_ADMIN
 *   <li>CUSTOM: 사용자 정의 역할 (수정/삭제 가능)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleType {

    /** 시스템 기본 역할 - 수정/삭제 불가 */
    SYSTEM,

    /** 사용자 정의 역할 - 수정/삭제 가능 */
    CUSTOM;

    /**
     * 시스템 역할 여부 확인
     *
     * @return 시스템 역할이면 true
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 커스텀 역할 여부 확인
     *
     * @return 커스텀 역할이면 true
     */
    public boolean isCustom() {
        return this == CUSTOM;
    }

    /**
     * 문자열 목록으로부터 RoleType 목록 파싱
     *
     * <p>null이거나 빈 목록이면 null을 반환합니다.
     *
     * <p>매칭되지 않는 값은 무시됩니다.
     *
     * @param types 타입 문자열 목록 (nullable)
     * @return RoleType 목록 또는 null
     */
    public static List<RoleType> parseList(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        List<RoleType> result =
                types.stream()
                        .map(RoleType::fromStringOrNull)
                        .filter(type -> type != null)
                        .toList();
        return result.isEmpty() ? null : result;
    }

    /**
     * 문자열로부터 RoleType 파싱 (실패 시 null)
     *
     * @param value 타입 문자열 (nullable)
     * @return RoleType 또는 null
     */
    private static RoleType fromStringOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
