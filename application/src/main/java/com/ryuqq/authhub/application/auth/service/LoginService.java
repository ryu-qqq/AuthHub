package com.ryuqq.authhub.application.auth.service;

import com.ryuqq.authhub.application.auth.assembler.LoginResponseAssembler;
import com.ryuqq.authhub.application.auth.component.LoginValidator;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.TokenClaimsContext;
import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import org.springframework.stereotype.Service;

/**
 * LoginService - 로그인 서비스 구현체
 *
 * <p>로그인 비즈니스 로직을 처리합니다.
 *
 * <p><strong>책임 분리:</strong>
 *
 * <ul>
 *   <li>검증: LoginValidator (비밀번호, 활성 상태)
 *   <li>토큰 발급: TokenManager (트랜잭션 외부)
 *   <li>응답 조립: LoginResponseAssembler
 * </ul>
 *
 * <p><strong>하이브리드 JWT 전략:</strong>
 *
 * <ul>
 *   <li>JWT에 tenant_name, org_name, email 포함
 *   <li>다른 서비스에서 키 없이 Base64 디코딩으로 확인 가능
 * </ul>
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>보안상 이유로 "사용자 없음"과 "비밀번호 불일치"를 구분하지 않음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class LoginService implements LoginUseCase {

    private final UserQueryPort userQueryPort;
    private final TenantQueryPort tenantQueryPort;
    private final OrganizationQueryPort organizationQueryPort;
    private final LoginValidator loginValidator;
    private final TokenManager tokenManager;
    private final LoginResponseAssembler loginResponseAssembler;

    public LoginService(
            UserQueryPort userQueryPort,
            TenantQueryPort tenantQueryPort,
            OrganizationQueryPort organizationQueryPort,
            LoginValidator loginValidator,
            TokenManager tokenManager,
            LoginResponseAssembler loginResponseAssembler) {
        this.userQueryPort = userQueryPort;
        this.tenantQueryPort = tenantQueryPort;
        this.organizationQueryPort = organizationQueryPort;
        this.loginValidator = loginValidator;
        this.tokenManager = tokenManager;
        this.loginResponseAssembler = loginResponseAssembler;
    }

    @Override
    public LoginResponse execute(LoginCommand command) {
        TenantId tenantId = TenantId.of(command.tenantId());

        User user = findUser(tenantId, command);
        loginValidator.validate(command.password(), user, command.tenantId(), command.identifier());

        // Tenant, Organization 조회 및 TokenClaimsContext 구성
        TokenClaimsContext context = buildTokenClaimsContext(user);
        TokenResponse tokenResponse = tokenManager.issueTokens(context);

        return loginResponseAssembler.toLoginResponse(user, tokenResponse);
    }

    private User findUser(TenantId tenantId, LoginCommand command) {
        return userQueryPort
                .findByTenantIdAndIdentifier(tenantId, command.identifier())
                .orElseThrow(
                        () ->
                                new InvalidCredentialsException(
                                        command.tenantId(), command.identifier()));
    }

    /**
     * User 정보를 기반으로 TokenClaimsContext 구성
     *
     * @param user 로그인한 사용자
     * @return TokenClaimsContext (tenant_name, org_name, email 포함)
     */
    private TokenClaimsContext buildTokenClaimsContext(User user) {
        Tenant tenant =
                tenantQueryPort
                        .findById(user.getTenantId())
                        .orElseThrow(
                                () ->
                                        new InvalidCredentialsException(
                                                user.tenantIdValue(), user.getIdentifier()));

        Organization organization =
                organizationQueryPort
                        .findById(user.getOrganizationId())
                        .orElseThrow(
                                () ->
                                        new InvalidCredentialsException(
                                                user.tenantIdValue(), user.getIdentifier()));

        return TokenClaimsContext.builder()
                .userId(user.getUserId())
                .tenantId(tenant.tenantIdValue())
                .tenantName(tenant.nameValue())
                .organizationId(organization.organizationIdValue())
                .organizationName(organization.nameValue())
                .email(user.getIdentifier())
                .build();
    }
}
