package com.ryuqq.authhub.application.token.internal;

import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.factory.TokenClaimsContextFactory;
import com.ryuqq.authhub.application.token.validator.LoginValidator;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.token.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import org.springframework.stereotype.Component;

/**
 * LoginCoordinator - 로그인 프로세스 Coordinator
 *
 * <p>크로스 도메인 검증 및 TokenClaimsContext 생성을 조율합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>User 조회 + 활성 상태 검증 (UserValidator)
 *   <li>Organization 존재 검증 (OrganizationValidator)
 *   <li>Tenant 존재 검증 (TenantValidator)
 *   <li>LoginValidator를 통한 비밀번호 검증
 *   <li>TokenClaimsContextFactory를 통한 컨텍스트 생성
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>User 조회 + 활성 상태 검증 (UserValidator)
 *   <li>Organization 조회 및 검증
 *   <li>Tenant 조회 및 검증
 *   <li>비밀번호 검증 (LoginValidator)
 *   <li>TokenClaimsContext 생성 및 반환
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class LoginCoordinator {

    private final UserValidator userValidator;
    private final OrganizationValidator organizationValidator;
    private final TenantValidator tenantValidator;
    private final LoginValidator loginValidator;
    private final TokenClaimsContextFactory tokenClaimsContextFactory;

    public LoginCoordinator(
            UserValidator userValidator,
            OrganizationValidator organizationValidator,
            TenantValidator tenantValidator,
            LoginValidator loginValidator,
            TokenClaimsContextFactory tokenClaimsContextFactory) {
        this.userValidator = userValidator;
        this.organizationValidator = organizationValidator;
        this.tenantValidator = tenantValidator;
        this.loginValidator = loginValidator;
        this.tokenClaimsContextFactory = tokenClaimsContextFactory;
    }

    /**
     * 로그인 프로세스 조율
     *
     * <p>사용자 인증 및 TokenClaimsContext 생성을 한 번에 처리합니다.
     *
     * @param command 로그인 커맨드 (identifier, password)
     * @return TokenClaimsContext 토큰 발급에 필요한 컨텍스트
     * @throws InvalidCredentialsException 인증 실패 시
     */
    public TokenClaimsComposite coordinate(LoginCommand command) {
        User user =
                userValidator.findActiveUserByIdentifierOrThrow(
                        Identifier.of(command.identifier()));

        Organization organization =
                organizationValidator.findExistingOrThrow(user.getOrganizationId());
        Tenant tenant = tenantValidator.findExistingOrThrow(organization.getTenantId());

        loginValidator.validatePassword(
                command.password(), user, tenant.tenantIdValue(), command.identifier());

        return tokenClaimsContextFactory.create(user, tenant, organization);
    }
}
