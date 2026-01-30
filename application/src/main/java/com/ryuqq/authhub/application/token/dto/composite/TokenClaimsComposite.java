package com.ryuqq.authhub.application.token.dto.composite;

import com.ryuqq.authhub.domain.user.id.UserId;

/**
 * TokenClaimsComposite - JWT Token Claims Composite
 *
 * <p>JWT 토큰 생성에 필요한 Claim 정보를 담는 불변 컨텍스트입니다.
 *
 * <p><strong>포함 정보:</strong>
 *
 * <ul>
 *   <li>userId: 사용자 ID (subject)
 *   <li>tenantId, tenantName: 테넌트 정보
 *   <li>organizationId, organizationName: 조직 정보
 *   <li>email: 사용자 이메일 (identifier)
 *   <li>mfaVerified: MFA 인증 완료 여부
 * </ul>
 *
 * <p><strong>하이브리드 JWT 전략:</strong>
 *
 * <ul>
 *   <li>불변 정보 (tenantId, tenantName, email): JWT에 포함
 *   <li>선택적 정보 (organizationId, organizationName): JWT에 포함, Gateway에서 전환 가능
 *   <li>보안 정보 (mfaVerified): MFA 인증 상태, Gateway에서 MFA 필수 엔드포인트 검사용
 * </ul>
 *
 * <p><strong>주의:</strong> roles/permissions는 RolesAndPermissionsComposite로 분리됨
 *
 * @author development-team
 * @since 1.0.0
 */
public record TokenClaimsComposite(
        UserId userId,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        String email,
        boolean mfaVerified) {

    /**
     * Builder 패턴으로 TokenClaimsComposite 생성
     *
     * @return TokenClaimsCompositeBuilder
     */
    public static TokenClaimsCompositeBuilder builder() {
        return new TokenClaimsCompositeBuilder();
    }

    /** TokenClaimsComposite Builder */
    public static final class TokenClaimsCompositeBuilder {
        private UserId userId;
        private String tenantId;
        private String tenantName;
        private String organizationId;
        private String organizationName;
        private String email;
        private boolean mfaVerified = false;

        private TokenClaimsCompositeBuilder() {}

        public TokenClaimsCompositeBuilder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public TokenClaimsCompositeBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public TokenClaimsCompositeBuilder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public TokenClaimsCompositeBuilder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public TokenClaimsCompositeBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public TokenClaimsCompositeBuilder email(String email) {
            this.email = email;
            return this;
        }

        public TokenClaimsCompositeBuilder mfaVerified(boolean mfaVerified) {
            this.mfaVerified = mfaVerified;
            return this;
        }

        public TokenClaimsComposite build() {
            return new TokenClaimsComposite(
                    userId,
                    tenantId,
                    tenantName,
                    organizationId,
                    organizationName,
                    email,
                    mfaVerified);
        }
    }
}
