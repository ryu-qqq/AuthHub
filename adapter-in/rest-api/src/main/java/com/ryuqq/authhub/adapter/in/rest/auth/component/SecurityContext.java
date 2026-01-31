package com.ryuqq.authhub.adapter.in.rest.auth.component;

import java.util.Objects;
import java.util.Set;

/**
 * 요청별 보안 컨텍스트
 *
 * <p>Gateway에서 전달한 인증 정보를 담습니다. 불변 객체로 설계되어 Thread-safe합니다.
 *
 * <p>Gateway 연동 방식:
 *
 * <ul>
 *   <li>X-User-Id 헤더가 있으면 인증된 요청 (UUID 형식)
 *   <li>X-User-Id 헤더가 없으면 Anonymous (Public API)
 * </ul>
 *
 * <p>하이브리드 권한 체계:
 *
 * <ul>
 *   <li>Roles: 대략적인 접근 수준 (ROLE_TENANT_ADMIN 등)
 *   <li>Permissions: 세밀한 리소스 권한 (user:read, order:write 등)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityContext {

    private static final SecurityContext ANONYMOUS = new Builder().build();

    // ========== Role Constants (Gateway 계약) ==========
    /** 시스템 전체 관리자 */
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /** 테넌트 관리자 */
    public static final String ROLE_TENANT_ADMIN = "ROLE_TENANT_ADMIN";

    /** 조직 관리자 */
    public static final String ROLE_ORG_ADMIN = "ROLE_ORG_ADMIN";

    /** 일반 사용자 */
    public static final String ROLE_MEMBER = "ROLE_MEMBER";

    /** 모든 리소스에 대한 와일드카드 권한 */
    private static final String WILDCARD_ALL_PERMISSIONS = "*:*";

    /** 권한 구분자 (resource:action) */
    private static final String PERMISSION_DELIMITER = ":";

    /** 와일드카드 액션 */
    private static final String WILDCARD_ACTION = "*";

    /** 권한 형식의 파트 개수 (resource:action = 2) */
    private static final int PERMISSION_PARTS_COUNT = 2;

    private final String userId;
    private final String tenantId;
    private final String organizationId;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final String traceId;

    private SecurityContext(Builder builder) {
        this.userId = builder.userId;
        this.tenantId = builder.tenantId;
        this.organizationId = builder.organizationId;
        this.roles = builder.roles != null ? Set.copyOf(builder.roles) : Set.of();
        this.permissions = builder.permissions != null ? Set.copyOf(builder.permissions) : Set.of();
        this.traceId = builder.traceId;
    }

    /**
     * 익명 컨텍스트 반환 (Public 엔드포인트용)
     *
     * @return 익명 SecurityContext
     */
    public static SecurityContext anonymous() {
        return ANONYMOUS;
    }

    /**
     * Builder 생성
     *
     * @return 새로운 Builder 인스턴스
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 사용자 ID 반환 (UUID 형식)
     *
     * @return 사용자 ID (익명이면 null)
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 테넌트 ID 반환 (UUIDv7 문자열 형식)
     *
     * @return 테넌트 ID (없으면 null)
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 조직 ID 반환 (UUIDv7 문자열 형식)
     *
     * @return 조직 ID (없으면 null)
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * 역할 목록 반환 (불변)
     *
     * @return 역할 Set
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * 권한 목록 반환 (불변)
     *
     * @return 권한 Set (예: user:read, order:write)
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * 추적 ID 반환
     *
     * @return 추적 ID (없으면 null)
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 인증 여부 확인
     *
     * @return userId가 있으면 true
     */
    public boolean isAuthenticated() {
        return userId != null;
    }

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 역할 보유 시 true
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * 주어진 역할 중 하나라도 보유 여부 확인
     *
     * @param rolesToCheck 확인할 역할들
     * @return 하나라도 보유 시 true
     */
    public boolean hasAnyRole(String... rolesToCheck) {
        for (String role : rolesToCheck) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 특정 권한 보유 여부 확인
     *
     * <p>권한 형식: {resource}:{action} (예: user:read, order:write)
     *
     * <p>와일드카드 지원:
     *
     * <ul>
     *   <li>*:* - 모든 리소스의 모든 액션
     *   <li>user:* - user 리소스의 모든 액션
     * </ul>
     *
     * @param permission 확인할 권한 (예: user:read)
     * @return 권한 보유 시 true
     */
    public boolean hasPermission(String permission) {
        if (permissions.contains(WILDCARD_ALL_PERMISSIONS)) {
            return true;
        }
        if (permissions.contains(permission)) {
            return true;
        }
        // 와일드카드 체크 (예: user:* 가 user:read 포함)
        String[] parts = permission.split(PERMISSION_DELIMITER);
        if (parts.length == PERMISSION_PARTS_COUNT) {
            String wildcardPermission = parts[0] + PERMISSION_DELIMITER + WILDCARD_ACTION;
            return permissions.contains(wildcardPermission);
        }
        return false;
    }

    /**
     * 주어진 권한 중 하나라도 보유 여부 확인
     *
     * @param permissionsToCheck 확인할 권한들
     * @return 하나라도 보유 시 true
     */
    public boolean hasAnyPermission(String... permissionsToCheck) {
        for (String permission : permissionsToCheck) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 주어진 권한을 모두 보유 여부 확인
     *
     * @param permissionsToCheck 확인할 권한들
     * @return 모두 보유 시 true
     */
    public boolean hasAllPermissions(String... permissionsToCheck) {
        for (String permission : permissionsToCheck) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * SUPER_ADMIN 역할 보유 여부
     *
     * @return SUPER_ADMIN 보유 시 true
     */
    public boolean isSuperAdmin() {
        return hasRole(ROLE_SUPER_ADMIN);
    }

    /**
     * TENANT_ADMIN 역할 보유 여부
     *
     * @return TENANT_ADMIN 보유 시 true
     */
    public boolean isTenantAdmin() {
        return hasRole(ROLE_TENANT_ADMIN);
    }

    /**
     * ORG_ADMIN 역할 보유 여부
     *
     * @return ORG_ADMIN 보유 시 true
     */
    public boolean isOrgAdmin() {
        return hasRole(ROLE_ORG_ADMIN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecurityContext that = (SecurityContext) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(tenantId, that.tenantId)
                && Objects.equals(organizationId, that.organizationId)
                && Objects.equals(roles, that.roles)
                && Objects.equals(permissions, that.permissions)
                && Objects.equals(traceId, that.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tenantId, organizationId, roles, permissions, traceId);
    }

    @Override
    public String toString() {
        return "SecurityContext{"
                + "userId='"
                + userId
                + '\''
                + ", tenantId="
                + tenantId
                + ", organizationId="
                + organizationId
                + ", roles="
                + roles
                + ", permissions="
                + permissions
                + ", traceId='"
                + traceId
                + '\''
                + '}';
    }

    /** SecurityContext Builder */
    public static final class Builder {

        private String userId;
        private String tenantId;
        private String organizationId;
        private Set<String> roles;
        private Set<String> permissions;
        private String traceId;

        private Builder() {}

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public SecurityContext build() {
            return new SecurityContext(this);
        }
    }
}
