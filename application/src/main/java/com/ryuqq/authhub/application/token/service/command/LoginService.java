package com.ryuqq.authhub.application.token.service.command;

import com.ryuqq.authhub.application.token.assembler.LoginResponseAssembler;
import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.LoginResponse;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.internal.LoginCoordinator;
import com.ryuqq.authhub.application.token.internal.TokenCommandFacade;
import com.ryuqq.authhub.application.token.manager.TokenProviderManager;
import com.ryuqq.authhub.application.token.port.in.command.LoginUseCase;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.domain.token.vo.RefreshToken;
import org.springframework.stereotype.Service;

/**
 * LoginService - 로그인 서비스 구현체
 *
 * <p>로그인 비즈니스 로직을 처리합니다.
 *
 * <p><strong>책임 분리:</strong>
 *
 * <ul>
 *   <li>조율: LoginCoordinator (User/Organization/Tenant 조회, 검증, Context 생성)
 *   <li>역할/권한 조회: UserRoleReadFacade
 *   <li>토큰 발급: TokenProviderManager
 *   <li>토큰 저장: TokenCommandFacade
 *   <li>응답 조립: LoginResponseAssembler
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class LoginService implements LoginUseCase {

    private final LoginCoordinator loginCoordinator;
    private final UserRoleReadFacade userRoleReadFacade;
    private final TokenProviderManager tokenProviderManager;
    private final TokenCommandFacade tokenCommandFacade;
    private final LoginResponseAssembler loginResponseAssembler;

    public LoginService(
            LoginCoordinator loginCoordinator,
            UserRoleReadFacade userRoleReadFacade,
            TokenProviderManager tokenProviderManager,
            TokenCommandFacade tokenCommandFacade,
            LoginResponseAssembler loginResponseAssembler) {
        this.loginCoordinator = loginCoordinator;
        this.userRoleReadFacade = userRoleReadFacade;
        this.tokenProviderManager = tokenProviderManager;
        this.tokenCommandFacade = tokenCommandFacade;
        this.loginResponseAssembler = loginResponseAssembler;
    }

    @Override
    public LoginResponse execute(LoginCommand command) {
        TokenClaimsComposite context = loginCoordinator.coordinate(command);

        RolesAndPermissionsComposite rolesAndPermissions =
                userRoleReadFacade.findRolesAndPermissionsByUserId(context.userId());

        TokenResponse tokenResponse =
                tokenProviderManager.generateTokenPair(context, rolesAndPermissions);

        RefreshToken refreshToken =
                RefreshToken.of(
                        context.userId(),
                        tokenResponse.refreshToken(),
                        tokenResponse.refreshTokenExpiresIn());

        tokenCommandFacade.persistRefreshToken(refreshToken);

        return loginResponseAssembler.toLoginResponse(context.userId(), tokenResponse);
    }
}
