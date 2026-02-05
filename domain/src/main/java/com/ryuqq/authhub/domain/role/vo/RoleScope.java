package com.ryuqq.authhub.domain.role.vo;

import java.util.List;

/**
 * RoleScope - 역할 범위 열거형
 *
 * <p>역할이 적용되는 범위를 정의합니다.
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템 공유 역할 (tenantId=null, serviceId=null)
 *   <li>SERVICE: 특정 서비스 전용 역할 (tenantId=null, serviceId=not null)
 *   <li>TENANT: 특정 테넌트 전용 역할 (tenantId=not null, serviceId=null)
 *   <li>TENANT_SERVICE: 특정 테넌트 + 서비스 전용 역할 (tenantId=not null, serviceId=not null)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleScope {

    /** 전체 시스템 공유 역할 */
    GLOBAL,

    /** 특정 서비스 전용 역할 */
    SERVICE,

    /** 특정 테넌트 전용 역할 */
    TENANT,

    /** 특정 테넌트 + 서비스 전용 역할 */
    TENANT_SERVICE;

    /**
     * Global 범위인지 확인
     *
     * @return GLOBAL이면 true
     */
    public boolean isGlobal() {
        return this == GLOBAL;
    }

    /**
     * 서비스 범위를 포함하는지 확인
     *
     * @return SERVICE 또는 TENANT_SERVICE이면 true
     */
    public boolean hasService() {
        return this == SERVICE || this == TENANT_SERVICE;
    }

    /**
     * 테넌트 범위를 포함하는지 확인
     *
     * @return TENANT 또는 TENANT_SERVICE이면 true
     */
    public boolean hasTenant() {
        return this == TENANT || this == TENANT_SERVICE;
    }

    /**
     * 문자열 목록으로부터 RoleScope 목록 파싱
     *
     * <p>null이거나 빈 목록이면 null을 반환합니다.
     *
     * <p>매칭되지 않는 값은 무시됩니다.
     *
     * @param scopes 범위 문자열 목록 (nullable)
     * @return RoleScope 목록 또는 null
     */
    public static List<RoleScope> parseList(List<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return null;
        }
        List<RoleScope> result =
                scopes.stream()
                        .map(RoleScope::fromStringOrNull)
                        .filter(scope -> scope != null)
                        .toList();
        return result.isEmpty() ? null : result;
    }

    /**
     * 문자열로부터 RoleScope 파싱 (실패 시 null)
     *
     * @param value 범위 문자열 (nullable)
     * @return RoleScope 또는 null
     */
    private static RoleScope fromStringOrNull(String value) {
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
