package com.ryuqq.authhub.application.token.service.command;

import com.ryuqq.authhub.application.token.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.internal.TokenCommandFacade;
import com.ryuqq.authhub.application.token.manager.TokenProviderManager;
import com.ryuqq.authhub.application.token.port.in.command.RefreshTokenUseCase;
import com.ryuqq.authhub.application.token.validator.TokenValidator;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.domain.token.vo.RefreshToken;
import org.springframework.stereotype.Service;

/**
 * RefreshTokenService - 토큰 갱신 서비스 구현체
 *
 * <p>Refresh Token을 사용한 토큰 갱신 로직을 처리합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Refresh Token 검증 및 TokenClaimsContext 생성 (TokenValidator)
 *   <li>기존 Refresh Token 무효화 (TokenCommandFacade)
 *   <li>역할/권한 조회 (UserRoleReadFacade)
 *   <li>새 토큰 쌍 발급 (TokenProviderManager)
 *   <li>새 Refresh Token 저장 (TokenCommandFacade)
 * </ol>
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>토큰 갱신은 트랜잭션 외부에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenValidator tokenValidator;
    private final UserRoleReadFacade userRoleReadFacade;
    private final TokenProviderManager tokenProviderManager;
    private final TokenCommandFacade tokenCommandFacade;

    public RefreshTokenService(
            TokenValidator tokenValidator,
            UserRoleReadFacade userRoleReadFacade,
            TokenProviderManager tokenProviderManager,
            TokenCommandFacade tokenCommandFacade) {
        this.tokenValidator = tokenValidator;
        this.userRoleReadFacade = userRoleReadFacade;
        this.tokenProviderManager = tokenProviderManager;
        this.tokenCommandFacade = tokenCommandFacade;
    }

    @Override
    public TokenResponse execute(RefreshTokenCommand command) {
        String refreshTokenValue = command.refreshToken();

        TokenClaimsComposite context = tokenValidator.validateAndBuildContext(refreshTokenValue);

        tokenCommandFacade.revokeToken(refreshTokenValue);

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

        return tokenResponse;
    }
}
