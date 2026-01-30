package com.ryuqq.authhub.application.token.port.in.query;

import com.ryuqq.authhub.application.token.dto.response.PublicKeysResponse;

/**
 * GetPublicKeyUseCase - 공개키 조회 UseCase 인터페이스
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
public interface GetPublicKeyUseCase {

    /**
     * 공개키 목록 조회
     *
     * @return 공개키 목록 (RFC 7517 JWKS 형식)
     */
    PublicKeysResponse execute();
}
