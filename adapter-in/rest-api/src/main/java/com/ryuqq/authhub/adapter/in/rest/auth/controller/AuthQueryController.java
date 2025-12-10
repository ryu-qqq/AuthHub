package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.JwkApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.JwksApiResponse;
import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;
import com.ryuqq.authhub.application.auth.port.in.query.GetJwksUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth Query Controller - 인증 관련 조회 API
 *
 * <p>JWKS 등 인증 관련 Query 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 *
 * <ul>
 *   <li>GET /api/v1/auth/jwks - JWKS 조회 (200 OK)
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
@Tag(name = "Auth", description = "인증 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthQueryController {

    private final GetJwksUseCase getJwksUseCase;

    public AuthQueryController(GetJwksUseCase getJwksUseCase) {
        this.getJwksUseCase = getJwksUseCase;
    }

    /**
     * JWKS 조회 (Gateway 전용)
     *
     * <p>GET /api/v1/auth/jwks
     *
     * <p>Gateway에서 JWT 서명 검증용 공개키 목록을 조회합니다. Gateway 시작 시 및 주기적으로 호출하여 키를 캐싱합니다.
     *
     * @return 200 OK + JWKS (RFC 7517 형식)
     */
    @Operation(summary = "JWKS 조회", description = "Gateway에서 JWT 서명 검증용 공개키 목록을 조회합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping("/jwks")
    public ResponseEntity<JwksApiResponse> getJwks() {
        JwksResponse response = getJwksUseCase.execute();
        List<JwkApiResponse> keys =
                response.keys().stream()
                        .map(
                                jwk ->
                                        new JwkApiResponse(
                                                jwk.kid(), jwk.kty(), jwk.use(), jwk.alg(), jwk.n(),
                                                jwk.e()))
                        .toList();
        return ResponseEntity.ok(new JwksApiResponse(keys));
    }
}
