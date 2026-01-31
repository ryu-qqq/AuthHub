package com.ryuqq.authhub.adapter.in.rest.token.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.TokenApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.MyContextApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeysApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.mapper.TokenApiMapper;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
import com.ryuqq.authhub.application.token.port.in.query.GetPublicKeyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TokenQueryController - 토큰 관련 조회 API
 *
 * <p>공개키 목록 조회 등 토큰 관련 Query 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 *
 * <ul>
 *   <li>GET /api/v1/auth/jwks - 공개키 목록 조회 (200 OK)
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
public class TokenQueryController {

    private final GetPublicKeyUseCase getPublicKeyUseCase;
    private final GetMyContextUseCase getMyContextUseCase;
    private final TokenApiMapper tokenApiMapper;

    public TokenQueryController(
            GetPublicKeyUseCase getPublicKeyUseCase,
            GetMyContextUseCase getMyContextUseCase,
            TokenApiMapper tokenApiMapper) {
        this.getPublicKeyUseCase = getPublicKeyUseCase;
        this.getMyContextUseCase = getMyContextUseCase;
        this.tokenApiMapper = tokenApiMapper;
    }

    /**
     * 공개키 목록 조회 (Gateway 전용)
     *
     * <p>GET /api/v1/auth/jwks
     *
     * <p>Gateway에서 JWT 서명 검증용 공개키 목록을 조회합니다. Gateway 시작 시 및 주기적으로 호출하여 키를 캐싱합니다.
     *
     * @return 200 OK + 공개키 목록 (RFC 7517 JWKS 형식)
     */
    @Operation(summary = "공개키 목록 조회", description = "Gateway에서 JWT 서명 검증용 공개키 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping(TokenApiEndpoints.JWKS)
    public ResponseEntity<PublicKeysApiResponse> getPublicKeys() {
        return ResponseEntity.ok(
                tokenApiMapper.toPublicKeysApiResponse(getPublicKeyUseCase.execute()));
    }

    /**
     * 내 정보 조회 API
     *
     * <p>GET /api/v1/auth/me
     *
     * <p>현재 로그인한 사용자의 전체 컨텍스트(테넌트, 조직, 역할, 권한)를 조회합니다.
     *
     * @return 200 OK + 사용자 컨텍스트
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 테넌트, 조직, 역할, 권한 정보를 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 필요")
    })
    @GetMapping(TokenApiEndpoints.ME)
    public ResponseEntity<ApiResponse<MyContextApiResponse>> getMyContext() {
        String userId = SecurityContextHolder.getCurrentUserId();
        MyContextResponse useCaseResponse = getMyContextUseCase.execute(userId);
        MyContextApiResponse apiResponse = tokenApiMapper.toMyContextApiResponse(useCaseResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
