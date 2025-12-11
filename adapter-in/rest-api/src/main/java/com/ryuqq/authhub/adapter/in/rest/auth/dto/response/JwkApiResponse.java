package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * JwkApiResponse - 개별 JWK API 응답 DTO
 *
 * <p>RFC 7517 JSON Web Key (JWK) 명세에 따른 개별 키 정보입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "개별 JWK 응답")
public record JwkApiResponse(
        @Schema(description = "Key ID") String kid,
        @Schema(description = "Key Type (RSA)") String kty,
        @Schema(description = "Public Key Use (sig)") String use,
        @Schema(description = "Algorithm (RS256)") String alg,
        @Schema(description = "RSA modulus (Base64URL encoded)") String n,
        @Schema(description = "RSA public exponent (Base64URL encoded)") String e) {}
