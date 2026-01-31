package com.ryuqq.authhub.adapter.in.rest.token.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PublicKeysApiResponse - 공개키 목록 API 응답 DTO
 *
 * <p>RFC 7517 JSON Web Key Set (JWKS) 명세에 따른 키 목록입니다.
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
@Schema(description = "공개키 목록 응답 (RFC 7517 JWKS 형식)")
public record PublicKeysApiResponse(
        @Schema(description = "공개키 목록") List<PublicKeyApiResponse> keys) {}
