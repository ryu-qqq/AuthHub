package com.ryuqq.authhub.application.auth.dto.response;

/**
 * JwkResponse - 개별 JWK 응답 DTO
 *
 * <p>RFC 7517 JSON Web Key (JWK) 명세에 따른 개별 키 정보입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record JwkResponse(
        String kid, // Key ID
        String kty, // Key Type (RSA)
        String use, // Public Key Use (sig)
        String alg, // Algorithm (RS256)
        String n, // RSA modulus (Base64URL encoded)
        String e // RSA public exponent (Base64URL encoded)
        ) {

    public static JwkResponse of(String kid, String n, String e) {
        return new JwkResponse(kid, "RSA", "sig", "RS256", n, e);
    }
}
