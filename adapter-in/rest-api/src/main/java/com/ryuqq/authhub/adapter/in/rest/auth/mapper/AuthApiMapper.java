package com.ryuqq.authhub.adapter.in.rest.auth.mapper;

import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;

/**
 * Auth REST API ↔ Application Layer 변환 Mapper
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.
 *
 * <p><strong>변환 방향:</strong>
 * <ul>
 *   <li>API Request → Command (Controller → Application)</li>
 * </ul>
 *
 * <p><strong>Response 변환:</strong>
 * <ul>
 *   <li>Application Response → API Response 변환은 각 Response DTO의 from() 메서드 사용</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class AuthApiMapper {

    /**
     * LoginApiRequest → LoginCommand 변환
     *
     * @param request REST API 로그인 요청
     * @return Application Layer 로그인 명령
     */
    public LoginCommand toLoginCommand(LoginApiRequest request) {
        return new LoginCommand(
                request.tenantId(),
                request.identifier(),
                request.password()
        );
    }

    /**
     * RefreshTokenApiRequest → RefreshTokenCommand 변환
     *
     * @param request REST API 토큰 갱신 요청
     * @return Application Layer 토큰 갱신 명령
     */
    public RefreshTokenCommand toRefreshTokenCommand(RefreshTokenApiRequest request) {
        return new RefreshTokenCommand(request.refreshToken());
    }

    /**
     * LogoutApiRequest → LogoutCommand 변환
     *
     * @param request REST API 로그아웃 요청
     * @return Application Layer 로그아웃 명령
     */
    public LogoutCommand toLogoutCommand(LogoutApiRequest request) {
        return new LogoutCommand(request.userId());
    }
}
