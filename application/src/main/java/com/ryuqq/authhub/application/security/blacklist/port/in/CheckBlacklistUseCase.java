package com.ryuqq.authhub.application.security.blacklist.port.in;

/**
 * 블랙리스트 확인 UseCase (Port In).
 *
 * <p>JWT 토큰이 블랙리스트에 등록되어 있는지 확인하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis SET에서 JTI 존재 여부 확인</li>
 *   <li>블랙리스트 여부 반환 (boolean)</li>
 *   <li>빠른 조회 성능 (O(1) 복잡도)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 토큰 검증 시 블랙리스트 확인
 * CheckBlacklistUseCase.Query query = new CheckBlacklistUseCase.Query("unique-jwt-id-123");
 *
 * boolean isBlacklisted = checkBlacklistUseCase.isBlacklisted(query);
 * if (isBlacklisted) {
 *     throw new BlacklistedTokenException("Token has been blacklisted");
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckBlacklistUseCase {

    /**
     * JWT 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
     *
     * <p>Redis SET SISMEMBER 명령을 사용하여 O(1) 복잡도로 빠르게 확인합니다.</p>
     *
     * @param query 블랙리스트 확인 Query
     * @return 블랙리스트에 등록되어 있으면 true, 아니면 false
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean isBlacklisted(Query query);

    /**
     * 블랙리스트 확인 요청 Query.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Query {

        private final String jti;

        /**
         * Query 생성자.
         *
         * @param jti JWT ID (고유 식별자)
         * @throws IllegalArgumentException jti가 null이거나 빈 문자열인 경우
         */
        public Query(final String jti) {
            if (jti == null || jti.isBlank()) {
                throw new IllegalArgumentException("JTI cannot be null or blank");
            }

            this.jti = jti;
        }

        /**
         * JWT ID를 반환합니다.
         *
         * @return JWT ID
         */
        public String getJti() {
            return this.jti;
        }
    }
}
