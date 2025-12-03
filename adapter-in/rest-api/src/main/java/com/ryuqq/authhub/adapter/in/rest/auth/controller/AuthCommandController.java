package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.TokenApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.auth.port.in.LogoutUseCase;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;

import jakarta.validation.Valid;

/**
 * Auth Command Controller - 인증 관련 상태 변경 API
 *
 * <p>로그인, 토큰 갱신, 로그아웃 등 인증 관련 Command 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>POST /api/v1/auth/login - 로그인 (201 Created)</li>
 *   <li>POST /api/v1/auth/refresh - 토큰 갱신 (200 OK)</li>
 *   <li>POST /api/v1/auth/logout - 로그아웃 (200 OK)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.auth.base}")
@Validated
public class AuthCommandController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthApiMapper authApiMapper;

    public AuthCommandController(
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            AuthApiMapper authApiMapper) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.authApiMapper = authApiMapper;
    }

    /**
     * 로그인 API
     *
     * @param request 로그인 요청 DTO
     * @return 201 Created와 로그인 응답 (userId, accessToken, refreshToken)
     */
    @PostMapping("${api.endpoints.auth.login}")
    public ResponseEntity<ApiResponse<LoginApiResponse>> login(
            @Valid @RequestBody LoginApiRequest request) {
        LoginCommand command = authApiMapper.toLoginCommand(request);
        LoginResponse useCaseResponse = loginUseCase.execute(command);
        LoginApiResponse apiResponse = LoginApiResponse.from(useCaseResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 토큰 갱신 API
     *
     * @param request 토큰 갱신 요청 DTO
     * @return 200 OK와 새로운 토큰 응답 (accessToken, refreshToken)
     */
    @PostMapping("${api.endpoints.auth.refresh}")
    public ResponseEntity<ApiResponse<TokenApiResponse>> refresh(
            @Valid @RequestBody RefreshTokenApiRequest request) {
        RefreshTokenCommand command = authApiMapper.toRefreshTokenCommand(request);
        TokenResponse useCaseResponse = refreshTokenUseCase.execute(command);
        TokenApiResponse apiResponse = TokenApiResponse.from(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 로그아웃 API
     *
     * @param request 로그아웃 요청 DTO
     * @return 200 OK
     */
    @PostMapping("${api.endpoints.auth.logout}")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutApiRequest request) {
        LogoutCommand command = authApiMapper.toLogoutCommand(request);
        logoutUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ofSuccess());
    }
}
