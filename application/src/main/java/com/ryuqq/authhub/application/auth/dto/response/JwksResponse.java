package com.ryuqq.authhub.application.auth.dto.response;

import java.util.List;

/**
 * JwksResponse - JWKS 응답 DTO
 *
 * <p>RFC 7517 JSON Web Key Set (JWKS) 명세에 따른 키 목록입니다. Gateway에서 JWT 서명 검증용 Public Key를 조회할 때 사용됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record JwksResponse(List<JwkResponse> keys) {

    public static JwksResponse of(List<JwkResponse> keys) {
        return new JwksResponse(keys);
    }
}
