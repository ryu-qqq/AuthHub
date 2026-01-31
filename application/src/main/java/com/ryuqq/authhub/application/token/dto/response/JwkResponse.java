package com.ryuqq.authhub.application.token.dto.response;

/**
 * JwkResponse - 개별 JWK Response DTO
 *
 * <p>RFC 7517 JSON Web Key (JWK) 명세에 따른 개별 키 정보입니다.
 *
 * @param kid Key ID
 * @param kty Key Type (RSA)
 * @param use Public Key Use (sig)
 * @param alg Algorithm (RS256)
 * @param n RSA modulus (Base64URL encoded)
 * @param e RSA public exponent (Base64URL encoded)
 * @author development-team
 * @since 1.0.0
 */
public record JwkResponse(String kid, String kty, String use, String alg, String n, String e) {

    /**
     * RSA 공개키로 JwkResponse 생성
     *
     * @param kid Key ID
     * @param n RSA modulus
     * @param e RSA exponent
     * @return JwkResponse
     */
    public static JwkResponse of(String kid, String n, String e) {
        return new JwkResponse(kid, "RSA", "sig", "RS256", n, e);
    }
}
