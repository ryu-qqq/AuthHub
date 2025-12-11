package com.ryuqq.authhub.application.auth.dto.command;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Set;
import java.util.UUID;

/**
 * JWT Token Claims Context
 *
 * <p>JWT 토큰 생성에 필요한 모든 Claim 정보를 담는 불변 컨텍스트입니다.
 *
 * <p><strong>포함 정보:</strong>
 *
 * <ul>
 *   <li>userId: 사용자 ID (subject)
 *   <li>tenantId, tenantName: 테넌트 정보
 *   <li>organizationId, organizationName: 조직 정보
 *   <li>email: 사용자 이메일 (identifier)
 *   <li>roles, permissions: 권한 정보
 * </ul>
 *
 * <p><strong>하이브리드 JWT 전략:</strong>
 *
 * <ul>
 *   <li>불변 정보 (tenantId, tenantName, email): JWT에 포함
 *   <li>가변 정보 (roles, permissions): JWT에 포함 + Gateway 실시간 조회 가능
 *   <li>선택적 정보 (organizationId, organizationName): JWT에 포함, Gateway에서 전환 가능
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record TokenClaimsContext(
        UserId userId,
        UUID tenantId,
        String tenantName,
        UUID organizationId,
        String organizationName,
        String email,
        Set<String> roles,
        Set<String> permissions) {

    /**
     * Builder 패턴으로 TokenClaimsContext 생성
     *
     * @return TokenClaimsContextBuilder
     */
    public static TokenClaimsContextBuilder builder() {
        return new TokenClaimsContextBuilder();
    }

    /** TokenClaimsContext Builder */
    public static final class TokenClaimsContextBuilder {
        private UserId userId;
        private UUID tenantId;
        private String tenantName;
        private UUID organizationId;
        private String organizationName;
        private String email;
        private Set<String> roles = Set.of();
        private Set<String> permissions = Set.of();

        private TokenClaimsContextBuilder() {}

        public TokenClaimsContextBuilder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public TokenClaimsContextBuilder tenantId(UUID tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public TokenClaimsContextBuilder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public TokenClaimsContextBuilder organizationId(UUID organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public TokenClaimsContextBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public TokenClaimsContextBuilder email(String email) {
            this.email = email;
            return this;
        }

        public TokenClaimsContextBuilder roles(Set<String> roles) {
            this.roles = roles != null ? roles : Set.of();
            return this;
        }

        public TokenClaimsContextBuilder permissions(Set<String> permissions) {
            this.permissions = permissions != null ? permissions : Set.of();
            return this;
        }

        public TokenClaimsContext build() {
            return new TokenClaimsContext(
                    userId,
                    tenantId,
                    tenantName,
                    organizationId,
                    organizationName,
                    email,
                    roles,
                    permissions);
        }
    }
}
