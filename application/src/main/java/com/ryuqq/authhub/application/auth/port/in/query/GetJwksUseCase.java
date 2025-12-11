package com.ryuqq.authhub.application.auth.port.in.query;

import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;

/**
 * GetJwksUseCase - JWKS 조회 UseCase
 *
 * <p>Gateway에서 JWT 서명 검증용 공개키 목록을 조회할 때 사용됩니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query UseCase 인터페이스
 *   <li>Response DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetJwksUseCase {

    /**
     * JWKS 조회
     *
     * @return 공개키 목록 (JWKS 형식)
     */
    JwksResponse execute();
}
