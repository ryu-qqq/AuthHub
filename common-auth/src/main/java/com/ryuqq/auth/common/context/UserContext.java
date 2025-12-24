package com.ryuqq.auth.common.context;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * UserContext - 사용자 컨텍스트
 *
 * <p>현재 요청의 사용자 정보를 담는 불변 객체입니다. Gateway에서 설정한 헤더 값을 기반으로 생성됩니다.
 *
 * <p><strong>불변성:</strong>
 *
 * <ul>
 *   <li>모든 필드는 final
 *   <li>컬렉션은 unmodifiable로 반환
 *   <li>Builder 패턴으로 생성
 * </ul>
 *
 * <p><strong>SecurityContext 구현:</strong>
 *
 * <p>이 클래스는 {@link SecurityContext} 인터페이스를 구현하여 권한 검사 기능과 호환됩니다. 다른 프로젝트에서는 자체 UserContext를 만들고
 * SecurityContext만 구현하면 됩니다.
 *
 * @author development-team
 * @since 1.0.0
 * @see SecurityContext
 */
public final class UserContext implements SecurityContext {

    private final String userId;
    private final String tenantId;
    private final String organizationId;
    private final String email;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final String scope;
    private final boolean serviceAccount;
    private final String correlationId;
    private final String requestSource;

    private UserContext(Builder builder) {
        this.userId = builder.userId;
        this.tenantId = builder.tenantId;
        this.organizationId = builder.organizationId;
        this.email = builder.email;
        this.roles = builder.roles != null ? Set.copyOf(builder.roles) : Set.of();
        this.permissions = builder.permissions != null ? Set.copyOf(builder.permissions) : Set.of();
        this.scope = builder.scope;
        this.serviceAccount = builder.serviceAccount;
        this.correlationId = builder.correlationId;
        this.requestSource = builder.requestSource;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public String getScope() {
        return scope;
    }

    public boolean isServiceAccount() {
        return serviceAccount;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getRequestSource() {
        return requestSource;
    }

    /**
     * 특정 역할을 가지고 있는지 확인
     *
     * @param role 역할 이름
     * @return 역할 보유 여부
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * 특정 권한을 가지고 있는지 확인
     *
     * @param permission 권한 이름
     * @return 권한 보유 여부
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission) || permissions.contains("*:*");
    }

    /**
     * 인증된 사용자인지 확인
     *
     * @return 인증 여부
     */
    public boolean isAuthenticated() {
        return userId != null && !userId.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserContext that = (UserContext) o;
        return serviceAccount == that.serviceAccount
                && Objects.equals(userId, that.userId)
                && Objects.equals(tenantId, that.tenantId)
                && Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tenantId, organizationId, serviceAccount);
    }

    @Override
    public String toString() {
        return "UserContext{"
                + "userId='"
                + userId
                + '\''
                + ", tenantId='"
                + tenantId
                + '\''
                + ", organizationId='"
                + organizationId
                + '\''
                + ", serviceAccount="
                + serviceAccount
                + '}';
    }

    public static final class Builder {
        private String userId;
        private String tenantId;
        private String organizationId;
        private String email;
        private Set<String> roles;
        private Set<String> permissions;
        private String scope;
        private boolean serviceAccount;
        private String correlationId;
        private String requestSource;

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

        public Builder email(String email) {
            this.email = email;
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

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder serviceAccount(boolean serviceAccount) {
            this.serviceAccount = serviceAccount;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder requestSource(String requestSource) {
            this.requestSource = requestSource;
            return this;
        }

        public UserContext build() {
            return new UserContext(this);
        }
    }
}
