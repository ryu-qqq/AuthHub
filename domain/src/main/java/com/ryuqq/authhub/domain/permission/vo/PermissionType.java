package com.ryuqq.authhub.domain.permission.vo;

import java.util.List;

/**
 * PermissionType - 권한 유형 열거형
 *
 * <p>권한의 유형을 정의합니다.
 *
 * <ul>
 *   <li>SYSTEM: 시스템 기본 권한 (수정/삭제 불가)
 *   <li>CUSTOM: 사용자 정의 권한 (수정/삭제 가능)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionType {

    /** 시스템 기본 권한 - 수정/삭제 불가 */
    SYSTEM,

    /** 사용자 정의 권한 - 수정/삭제 가능 */
    CUSTOM;

    /**
     * 시스템 권한 여부 확인
     *
     * @return 시스템 권한이면 true
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 커스텀 권한 여부 확인
     *
     * @return 커스텀 권한이면 true
     */
    public boolean isCustom() {
        return this == CUSTOM;
    }

    /**
     * 문자열 목록으로부터 PermissionType 목록 파싱
     *
     * <p>null이거나 빈 목록이면 null을 반환합니다.
     *
     * <p>매칭되지 않는 값은 무시됩니다.
     *
     * @param types 타입 문자열 목록 (nullable)
     * @return PermissionType 목록 또는 null
     */
    public static List<PermissionType> parseList(List<String> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        List<PermissionType> result =
                types.stream()
                        .map(PermissionType::fromStringOrNull)
                        .filter(type -> type != null)
                        .toList();
        return result.isEmpty() ? null : result;
    }

    /**
     * 문자열로부터 PermissionType 파싱 (실패 시 null)
     *
     * @param value 타입 문자열 (nullable)
     * @return PermissionType 또는 null
     */
    private static PermissionType fromStringOrNull(String value) {
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
