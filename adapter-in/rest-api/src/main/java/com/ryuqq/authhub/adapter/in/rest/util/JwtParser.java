package com.ryuqq.authhub.adapter.in.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;

import java.util.Base64;

/**
 * JWT Parser - JWT 토큰 파싱 및 Claims 추출 유틸리티.
 *
 * <p>JWT 토큰을 파싱하여 Claims를 추출하고, JTI(JWT ID) 등의 정보를 반환하는 유틸리티 클래스입니다.
 * JJWT 0.12.x 라이브러리를 사용하며, 서명 검증 없이 Claims만 추출합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>JWT 토큰 파싱 (서명 검증 제외)</li>
 *   <li>Claims 추출 (Payload 부분)</li>
 *   <li>JTI(JWT ID) 추출 - 블랙리스트 확인용</li>
 *   <li>예외 처리 - 잘못된 형식의 토큰 감지</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Utility 클래스 - Static 메서드, Private 생성자</li>
 *   <li>✅ 예외 처리 - IllegalArgumentException, JwtException</li>
 * </ul>
 *
 * <p><strong>JWT 구조:</strong></p>
 * <pre>
 * JWT = [Header].[Payload].[Signature]
 * Header: 알고리즘, 토큰 타입
 * Payload: Claims (사용자 정보, JTI, exp, iat 등)
 * Signature: 서명 (검증용)
 * </pre>
 *
 * <p><strong>JTI (JWT ID):</strong></p>
 * <ul>
 *   <li>RFC 7519 표준 클레임</li>
 *   <li>JWT의 고유 식별자</li>
 *   <li>블랙리스트 관리에 사용 (로그아웃 시 JTI를 Redis에 등록)</li>
 *   <li>중복 방지 및 추적 가능</li>
 * </ul>
 *
 * <p><strong>서명 검증 제외 이유:</strong></p>
 * <ul>
 *   <li>Filter 단계에서는 블랙리스트 확인만 수행</li>
 *   <li>서명 검증은 Spring Security의 JwtAuthenticationFilter에서 처리</li>
 *   <li>성능 최적화 - 불필요한 검증 중복 제거</li>
 *   <li>단순히 JTI 추출만 필요하므로 Payload만 파싱</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJ1bmlxdWUtaWQtMTIzIiwic3ViIjoiMSIsImV4cCI6MTY5ODc2NTQzMn0.signature";
 *
 * try {
 *     String jti = JwtParser.extractJti(token);
 *     // jti = "unique-id-123"
 * } catch (IllegalArgumentException e) {
 *     // 토큰이 null이거나 빈 문자열
 * } catch (JwtException e) {
 *     // 잘못된 JWT 형식
 * }
 * </pre>
 *
 * <p><strong>예외 처리:</strong></p>
 * <ul>
 *   <li>IllegalArgumentException - token이 null이거나 빈 문자열인 경우</li>
 *   <li>JwtException - JWT 파싱 실패 (잘못된 형식, 손상된 토큰)</li>
 *   <li>JwtException - JTI 클레임이 없는 경우</li>
 * </ul>
 *
 * <p><strong>설계 원칙:</strong></p>
 * <ul>
 *   <li>Stateless - 인스턴스 생성 불필요</li>
 *   <li>Thread-Safe - Static 메서드, 공유 상태 없음</li>
 *   <li>Fail-Fast - 잘못된 입력 즉시 예외 발생</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class JwtParser {

    /**
     * Private Constructor - Utility 클래스이므로 인스턴스 생성 방지.
     */
    private JwtParser() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * ObjectMapper 인스턴스 (Thread-Safe, 재사용 가능).
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * JWT 토큰에서 JTI(JWT ID)를 추출합니다.
     *
     * <p>JWT의 Payload 부분을 Base64 디코딩하여 "jti" 클레임을 추출합니다.
     * 서명 검증은 수행하지 않으며, 단순히 Payload만 파싱합니다.</p>
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>토큰 null/빈 문자열 검증</li>
     *   <li>JWT를 "."으로 분리 (header, payload, signature)</li>
     *   <li>Payload 부분 Base64 디코딩</li>
     *   <li>JSON 파싱하여 "jti" 클레임 추출</li>
     *   <li>JTI null 검증</li>
     * </ol>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>서명 검증 제외로 빠른 처리</li>
     *   <li>Base64 디코딩 및 JSON 파싱만 수행</li>
     *   <li>블랙리스트 확인 시 매 요청마다 호출되므로 성능 중요</li>
     * </ul>
     *
     * @param token JWT 토큰 문자열 (null 불가, 빈 문자열 불가)
     * @return JTI (JWT ID) 문자열
     * @throws IllegalArgumentException token이 null이거나 빈 문자열인 경우
     * @throws JwtException JWT 파싱 실패 (잘못된 형식, 손상된 토큰, JTI 없음)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static String extractJti(final String token) {
        // 1. 입력 검증
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT token cannot be null or blank");
        }

        try {
            // 2. JWT를 "."으로 분리 (header.payload.signature)
            final String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new JwtException("Invalid JWT format: missing required parts");
            }

            // 3. Payload 부분 (index 1) Base64 디코딩
            final String payload = parts[1];
            final byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            final String decodedPayload = new String(decodedBytes);

            // 4. JSON 파싱하여 "jti" 클레임 추출
            final JsonNode jsonNode = OBJECT_MAPPER.readTree(decodedPayload);
            final JsonNode jtiNode = jsonNode.get("jti");

            // 5. JTI null 검증
            if (jtiNode == null || jtiNode.isNull() || jtiNode.asText().isBlank()) {
                throw new JwtException("JWT token does not contain JTI claim");
            }

            return jtiNode.asText();

        } catch (final IllegalArgumentException e) {
            // Base64 디코딩 실패
            throw new JwtException("Failed to decode JWT token: " + e.getMessage(), e);
        } catch (final Exception e) {
            // JSON 파싱 실패 또는 기타 예외
            throw new JwtException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }
}
