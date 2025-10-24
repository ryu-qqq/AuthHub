package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.UserId;

import java.time.Duration;

/**
 * GenerateToken Port Interface.
 *
 * <p>JWT 토큰을 생성하는 Out Port입니다.
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
 *   <li>로그인 성공 시 Access Token 및 Refresh Token 생성</li>
 *   <li>Refresh Token을 사용한 Access Token 갱신</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ 외부 API 호출이 아닌 내부 계산 작업 (JWT 서명)</li>
 *   <li>✅ @Transactional 내부에서 호출 가능 (네트워크 I/O 없음)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface GenerateTokenPort {

    /**
     * JWT 토큰을 생성합니다.
     *
     * <p>UserId와 TokenType(ACCESS/REFRESH), 유효 기간(Duration)을 입력받아
     * JWT 서명이 포함된 Token Domain Aggregate를 생성합니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>JWT 서명 생성은 메모리 계산 작업이며, 외부 API 호출이 아닙니다.</li>
     *   <li>네트워크 I/O가 발생하지 않으므로 DB 커넥션 점유 시간에 영향 없습니다.</li>
     * </ul>
     *
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @param tokenType 토큰 타입 (ACCESS 또는 REFRESH) (null 불가)
     * @param validity 토큰 유효 기간 (null 불가, 양수)
     * @return 생성된 Token Domain Aggregate (JWT 서명 포함)
     * @throws IllegalArgumentException userId, tokenType, validity가 null이거나 validity가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Token generate(UserId userId, TokenType tokenType, Duration validity);
}
