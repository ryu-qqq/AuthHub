package com.ryuqq.authhub.adapter.in.rest.token.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.TokenApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.TokenApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.mapper.TokenApiMapper;
import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.token.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.token.dto.response.LoginResponse;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.port.in.command.LoginUseCase;
import com.ryuqq.authhub.application.token.port.in.command.LogoutUseCase;
import com.ryuqq.authhub.application.token.port.in.command.RefreshTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TokenCommandController - 토큰 관련 상태 변경 API
 *
 * <p>로그인, 토큰 갱신, 로그아웃 등 토큰 관련 Command 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 *
 * <ul>
 *   <li>POST /api/v1/auth/login - 로그인 (201 Created)
 *   <li>POST /api/v1/auth/refresh - 토큰 갱신 (200 OK)
 *   <li>POST /api/v1/auth/logout - 로그아웃 (200 OK)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>UseCase 단일 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "Token", description = "토큰/인증 관리 API")
@RestController
@RequestMapping(TokenApiEndpoints.BASE)
@Validated
public class TokenCommandController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final TokenApiMapper tokenApiMapper;

    public TokenCommandController(
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            TokenApiMapper tokenApiMapper) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.tokenApiMapper = tokenApiMapper;
    }

    /**
     * 로그인 API
     *
     * <p>이메일과 비밀번호로 로그인하여 Access Token과 Refresh Token을 발급받습니다.
     *
     * @param request 로그인 요청 DTO
     * @return 201 Created와 로그인 응답 (userId, accessToken, refreshToken)
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 실패")
    })
    @PostMapping(TokenApiEndpoints.LOGIN)
    public ResponseEntity<ApiResponse<LoginApiResponse>> login(
            @Valid @RequestBody LoginApiRequest request) {
        LoginCommand command = tokenApiMapper.toLoginCommand(request);
        LoginResponse useCaseResponse = loginUseCase.execute(command);
        LoginApiResponse apiResponse = LoginApiResponse.from(useCaseResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 토큰 갱신 API
     *
     * <p>Refresh Token으로 새로운 Access Token과 Refresh Token을 발급받습니다.
     *
     * @param request 토큰 갱신 요청 DTO
     * @return 200 OK와 새로운 토큰 응답 (accessToken, refreshToken)
     */
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 토큰을 발급받습니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "토큰 갱신 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "유효하지 않은 토큰")
    })
    @PostMapping(TokenApiEndpoints.REFRESH)
    public ResponseEntity<ApiResponse<TokenApiResponse>> refresh(
            @Valid @RequestBody RefreshTokenApiRequest request) {
        RefreshTokenCommand command = tokenApiMapper.toRefreshTokenCommand(request);
        TokenResponse useCaseResponse = refreshTokenUseCase.execute(command);
        TokenApiResponse apiResponse = TokenApiResponse.from(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 로그아웃 API
     *
     * <p>현재 세션의 Refresh Token을 무효화하여 로그아웃합니다.
     *
     * @param request 로그아웃 요청 DTO
     * @return 200 OK
     */
    @Operation(summary = "로그아웃", description = "현재 세션에서 로그아웃합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "로그아웃 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 필요")
    })
    @PostMapping(TokenApiEndpoints.LOGOUT)
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutApiRequest request) {
        LogoutCommand command = tokenApiMapper.toLogoutCommand(request);
        logoutUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.ofSuccess());
    }
}
