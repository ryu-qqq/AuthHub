package com.ryuqq.authhub.application.token.dto.response;

import java.util.List;

/**
 * PublicKeysResponse - 공개키 목록 Response DTO
 *
 * <p>Gateway에서 JWT 서명 검증용 Public Key를 조회할 때 사용됩니다. RFC 7517 JSON Web Key Set (JWKS) 명세에 따른 키 목록입니다.
 *
 * @param keys 공개키 목록 (JWK 형식)
 * @author development-team
 * @since 1.0.0
 */
public record PublicKeysResponse(List<JwkResponse> keys) {

    /**
     * PublicKeysResponse 생성 팩토리 메서드
     *
     * @param keys 공개키 목록
     * @return PublicKeysResponse
     */
    public static PublicKeysResponse of(List<JwkResponse> keys) {
        return new PublicKeysResponse(keys);
    }
}
