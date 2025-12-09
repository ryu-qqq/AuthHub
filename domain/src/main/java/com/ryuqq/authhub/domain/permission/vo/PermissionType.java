package com.ryuqq.authhub.domain.permission.vo;

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

    /**
     * 시스템 기본 권한 - 수정/삭제 불가
     */
    SYSTEM,

    /**
     * 사용자 정의 권한 - 수정/삭제 가능
     */
    CUSTOM;

    public boolean isSystem() {
        return this == SYSTEM;
    }

    public boolean isCustom() {
        return this == CUSTOM;
    }

    public boolean isModifiable() {
        return this == CUSTOM;
    }
}
