package com.ryuqq.authhub.application.token.validator;

import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.factory.TokenClaimsContextFactory;
import com.ryuqq.authhub.application.token.manager.RefreshTokenReader;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.token.exception.InvalidRefreshTokenException;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;

/**
 * TokenValidator - 토큰 검증 컴포넌트
 *
 * <p>Refresh Token 검증 및 TokenClaimsContext 생성을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Refresh Token으로 UserId 조회 및 검증
 *   <li>사용자/조직/테넌트 조회 및 검증 (Validator 위임)
 *   <li>TokenClaimsContext 생성 (Factory 위임)
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Refresh Token으로 UserId 조회 (Cache → RDB fallback)
 *   <li>User 조회 및 존재 검증 (UserValidator)
 *   <li>Organization 조회 및 존재 검증 (OrganizationValidator)
 *   <li>Tenant 조회 및 존재 검증 (TenantValidator)
 *   <li>TokenClaimsContext 생성 및 반환
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenValidator {

    private final RefreshTokenReader refreshTokenReader;
    private final UserValidator userValidator;
    private final OrganizationValidator organizationValidator;
    private final TenantValidator tenantValidator;
    private final TokenClaimsContextFactory tokenClaimsContextFactory;

    public TokenValidator(
            RefreshTokenReader refreshTokenReader,
            UserValidator userValidator,
            OrganizationValidator organizationValidator,
            TenantValidator tenantValidator,
            TokenClaimsContextFactory tokenClaimsContextFactory) {
        this.refreshTokenReader = refreshTokenReader;
        this.userValidator = userValidator;
        this.organizationValidator = organizationValidator;
        this.tenantValidator = tenantValidator;
        this.tokenClaimsContextFactory = tokenClaimsContextFactory;
    }

    /**
     * Refresh Token 검증 및 TokenClaimsContext 생성
     *
     * <p>Refresh Token으로 사용자 정보를 조회하고 TokenClaimsContext를 생성합니다.
     *
     * @param refreshToken Refresh Token 값
     * @return TokenClaimsContext 토큰 발급에 필요한 컨텍스트
     * @throws InvalidRefreshTokenException 유효하지 않은 Refresh Token인 경우
     */
    public TokenClaimsComposite validateAndBuildContext(String refreshToken) {
        UserId userId =
                refreshTokenReader
                        .findUserIdByToken(refreshToken)
                        .orElseThrow(InvalidRefreshTokenException::new);

        User user = userValidator.findExistingOrThrow(userId);
        Organization organization =
                organizationValidator.findExistingOrThrow(user.getOrganizationId());
        Tenant tenant = tenantValidator.findExistingOrThrow(organization.getTenantId());

        return tokenClaimsContextFactory.create(user, tenant, organization);
    }
}
