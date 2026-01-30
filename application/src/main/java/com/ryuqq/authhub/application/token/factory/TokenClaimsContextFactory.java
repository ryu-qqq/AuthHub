package com.ryuqq.authhub.application.token.factory;

import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.user.aggregate.User;
import org.springframework.stereotype.Component;

/**
 * TokenClaimsContextFactory - TokenClaimsContext 생성 Factory
 *
 * <p>토큰 발급에 필요한 TokenClaimsContext를 생성합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>User, Tenant, Organization 정보로 TokenClaimsContext 생성
 *   <li>JWT Payload에 포함될 Claim 정보 조립
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 금지 (단순 생성만)
 *   <li>검증 로직 금지 (Validator/Coordinator에서 수행)
 *   <li>roles/permissions는 TokenCommandManager에서 enrichment
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenClaimsContextFactory {

    /**
     * TokenClaimsContext 생성
     *
     * <p>기본 사용자 정보로 컨텍스트를 생성합니다. roles/permissions는 포함되지 않습니다.
     *
     * @param user 사용자 도메인 객체
     * @param tenant 테넌트 도메인 객체
     * @param organization 조직 도메인 객체
     * @return TokenClaimsContext 토큰 발급용 컨텍스트
     */
    public TokenClaimsComposite create(User user, Tenant tenant, Organization organization) {
        return TokenClaimsComposite.builder()
                .userId(user.getUserId())
                .tenantId(tenant.tenantIdValue())
                .tenantName(tenant.nameValue())
                .organizationId(organization.organizationIdValue())
                .organizationName(organization.nameValue())
                .email(user.identifierValue())
                .build();
    }
}
