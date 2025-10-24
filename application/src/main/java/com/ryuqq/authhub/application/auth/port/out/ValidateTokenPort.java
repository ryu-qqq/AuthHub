package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.token.Token;

/**
 * ValidateToken Port Interface.
 *
 * <p>JWT 토큰의 서명 및 만료 시각을 검증하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Infrastructure Layer(JWT 라이브러리)에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/auth/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-infrastructure/auth/jwt/} (Infrastructure Layer)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>Refresh Token을 사용한 Access Token 재발급 시 Refresh Token 검증</li>
 *   <li>API 요청 시 Access Token의 유효성 검증</li>
 * </ul>
 *
 * <p><strong>검증 항목:</strong></p>
 * <ul>
 *   <li>JWT 서명 검증 (RS256 알고리즘 사용)</li>
 *   <li>만료 시각(ExpiresAt) 검증</li>
 *   <li>토큰 형식 검증 (Header, Payload, Signature)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ 외부 API 호출이 아닌 내부 계산 작업 (JWT 검증)</li>
 *   <li>✅ @Transactional 내부에서 호출 가능 (네트워크 I/O 없음)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface ValidateTokenPort {

    /**
     * JWT 토큰의 서명과 만료 시각을 검증하고 Token Domain Aggregate를 반환합니다.
     *
     * <p>JWT 문자열을 파싱하여 서명 검증 및 만료 시각 확인을 수행합니다.
     * 검증이 성공하면 Token Domain Aggregate를 반환하고, 실패하면 예외를 발생시킵니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>JWT 검증은 메모리 계산 작업이며, 외부 API 호출이 아닙니다.</li>
     *   <li>네트워크 I/O가 발생하지 않으므로 DB 커넥션 점유 시간에 영향 없습니다.</li>
     * </ul>
     *
     * <p><strong>검증 과정:</strong></p>
     * <ol>
     *   <li>JWT 형식 검증 (Header.Payload.Signature)</li>
     *   <li>서명 검증 (RS256 알고리즘, 공개키 사용)</li>
     *   <li>만료 시각(exp claim) 확인</li>
     *   <li>Token Domain Aggregate 생성 및 반환</li>
     * </ol>
     *
     * @param jwtToken JWT 토큰 문자열 (null 불가, 공백 불가)
     * @return 검증된 Token Domain Aggregate
     * @throws IllegalArgumentException jwtToken이 null이거나 공백인 경우
     * @throws com.ryuqq.authhub.domain.auth.token.exception.InvalidTokenException JWT 형식이 잘못되었거나 서명 검증 실패
     * @throws com.ryuqq.authhub.domain.auth.token.exception.ExpiredTokenException 토큰이 만료된 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Token validate(String jwtToken);
}
