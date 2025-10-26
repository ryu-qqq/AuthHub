package com.ryuqq.authhub.application.security.blacklist.port.in;

import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;

/**
 * 토큰 블랙리스트 추가 UseCase (Port In).
 *
 * <p>JWT 토큰을 블랙리스트에 추가하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>JWT 토큰 블랙리스트 등록 (로그아웃, 토큰 무효화)</li>
 *   <li>Redis SET에 JTI 저장</li>
 *   <li>TTL 자동 설정 (만료 시간까지)</li>
 *   <li>BlacklistedToken Aggregate 생성</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 등록 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 로그아웃 시 토큰 블랙리스트 등록
 * AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
 *     "unique-jwt-id-123",
 *     1735689600L,
 *     BlacklistReason.LOGOUT
 * );
 *
 * addToBlacklistUseCase.addToBlacklist(command);
 *
 * // 보안 침해로 인한 토큰 무효화
 * AddToBlacklistUseCase.Command breachCommand = new AddToBlacklistUseCase.Command(
 *     "suspicious-jwt-id-456",
 *     1735689600L,
 *     BlacklistReason.SECURITY_BREACH
 * );
 *
 * addToBlacklistUseCase.addToBlacklist(breachCommand);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface AddToBlacklistUseCase {

    /**
     * JWT 토큰을 블랙리스트에 추가합니다.
     *
     * <p>Redis SET에 JTI를 추가하고, TTL을 만료 시간까지로 설정합니다.
     * 이미 블랙리스트에 존재하는 경우 멱등성을 보장합니다.</p>
     *
     * @param command 블랙리스트 추가 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void addToBlacklist(Command command);

    /**
     * 블랙리스트 추가 요청 Command.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Command {

        private final String jti;
        private final long expiresAtEpochSeconds;
        private final BlacklistReason reason;

        /**
         * Command 생성자.
         *
         * @param jti JWT ID (고유 식별자)
         * @param expiresAtEpochSeconds 만료 시간 (Epoch 초 단위)
         * @param reason 블랙리스트 등록 사유
         * @throws IllegalArgumentException jti가 null이거나 빈 문자열, expiresAtEpochSeconds가 음수, reason이 null인 경우
         */
        public Command(
                final String jti,
                final long expiresAtEpochSeconds,
                final BlacklistReason reason
        ) {
            if (jti == null || jti.isBlank()) {
                throw new IllegalArgumentException("JTI cannot be null or blank");
            }
            if (expiresAtEpochSeconds < 0) {
                throw new IllegalArgumentException("ExpiresAt cannot be negative");
            }
            if (reason == null) {
                throw new IllegalArgumentException("BlacklistReason cannot be null");
            }

            this.jti = jti;
            this.expiresAtEpochSeconds = expiresAtEpochSeconds;
            this.reason = reason;
        }

        /**
         * JWT ID를 반환합니다.
         *
         * @return JWT ID
         */
        public String getJti() {
            return this.jti;
        }

        /**
         * 만료 시간을 Epoch 초 단위로 반환합니다.
         *
         * @return 만료 시간 (Epoch 초)
         */
        public long getExpiresAtEpochSeconds() {
            return this.expiresAtEpochSeconds;
        }

        /**
         * 블랙리스트 등록 사유를 반환합니다.
         *
         * @return BlacklistReason
         */
        public BlacklistReason getReason() {
            return this.reason;
        }
    }
}
