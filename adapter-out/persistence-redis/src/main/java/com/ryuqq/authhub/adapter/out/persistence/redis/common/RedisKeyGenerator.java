package com.ryuqq.authhub.adapter.out.persistence.redis.common;

/**
 * RedisKeyGenerator - Redis 키 생성 유틸리티
 *
 * <p>Redis 키 패턴을 일관성 있게 관리합니다.
 *
 * <p><strong>키 패턴 규칙:</strong>
 *
 * <ul>
 *   <li>Prefix: {@code {domain}::{type}::{identifier}}
 *   <li>예시: {@code refresh_token::user::userId}
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>KEYS 명령어 절대 금지 (O(N) 복잡도)
 *   <li>키 패턴 변경 시 마이그레이션 필수
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RedisKeyGenerator {

    private static final String DELIMITER = "::";

    private RedisKeyGenerator() {
        // Utility class
    }

    /**
     * RefreshToken 키 생성 - UserId 기준
     *
     * <p>패턴: {@code refresh_token::user::{userId}}
     *
     * @param userId 사용자 ID
     * @return Redis 키
     */
    public static String refreshTokenByUser(String userId) {
        return "refresh_token" + DELIMITER + "user" + DELIMITER + userId;
    }

    /**
     * RefreshToken 키 생성 - Token 기준
     *
     * <p>패턴: {@code refresh_token::token::{token}}
     *
     * @param token RefreshToken 값
     * @return Redis 키
     */
    public static String refreshTokenByToken(String token) {
        return "refresh_token" + DELIMITER + "token" + DELIMITER + token;
    }

    /**
     * 멱등키 캐시 키 생성
     *
     * <p>패턴: {@code idempotency::{operation}::{key}}
     *
     * @param operation 작업 유형 (예: onboarding)
     * @param idempotencyKey 클라이언트가 전송한 멱등키
     * @return Redis 키
     */
    public static String idempotency(String operation, String idempotencyKey) {
        return "idempotency" + DELIMITER + operation + DELIMITER + idempotencyKey;
    }
}
