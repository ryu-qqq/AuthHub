package com.ryuqq.authhub.domain.role.vo;

/**
 * RoleType - 역할 유형
 *
 * <p>역할의 생성 및 관리 방식을 정의합니다.
 *
 * <ul>
 *   <li>SYSTEM: 시스템에서 기본 제공하는 역할 (수정/삭제 불가)
 *   <li>CUSTOM: 사용자가 정의한 커스텀 역할 (수정/삭제 가능)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleType {

    /**
     * 시스템 기본 역할 - 수정 및 삭제 불가
     *
     * <p>예: SUPER_ADMIN, TENANT_ADMIN, ORG_ADMIN, USER
     */
    SYSTEM,

    /**
     * 사용자 정의 역할 - 수정 및 삭제 가능
     *
     * <p>테넌트 또는 조직에서 필요에 따라 생성
     */
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
     * 수정 가능 여부 확인
     *
     * @return 수정 가능하면 true (커스텀 역할만 수정 가능)
     */
    public boolean isModifiable() {
        return this == CUSTOM;
    }
}
