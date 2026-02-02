package com.ryuqq.authhub.sdk.model.internal;

/**
 * 개별 공개키 모델 (RFC 7517 JWK 형식).
 *
 * <p>Gateway가 JWT 서명 검증을 위해 사용합니다.
 *
 * @param kid Key ID
 * @param kty Key Type (RSA)
 * @param use Public Key Use (sig)
 * @param alg Algorithm (RS256)
 * @param n RSA modulus (Base64URL encoded)
 * @param e RSA public exponent (Base64URL encoded)
 */
public record PublicKey(String kid, String kty, String use, String alg, String n, String e) {}
