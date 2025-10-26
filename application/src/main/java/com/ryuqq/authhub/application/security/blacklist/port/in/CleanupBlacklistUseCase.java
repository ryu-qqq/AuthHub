package com.ryuqq.authhub.application.security.blacklist.port.in;

/**
 * 블랙리스트 정리 UseCase (Port In).
 *
 * <p>만료된 JWT 토큰을 블랙리스트에서 제거하는 인바운드 포트입니다.
 * Scheduler를 통해 주기적으로 실행되어 메모리를 효율적으로 관리합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>만료된 토큰 식별 (현재 시간 기준)</li>
 *   <li>배치 삭제 (1000개씩)</li>
 *   <li>메모리 최적화 (불필요한 토큰 제거)</li>
 *   <li>정리 통계 반환 (삭제된 토큰 수)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 정리 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 *   <li>✅ Batch 처리 - 대량 삭제 시 성능 최적화</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Scheduler에서 1시간마다 실행
 * {@literal @}Scheduled(cron = "0 0 * * * *")
 * public void cleanupExpiredTokens() {
 *     CleanupBlacklistUseCase.Result result = cleanupBlacklistUseCase.cleanup();
 *     log.info("Cleaned up {} expired tokens", result.getDeletedCount());
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CleanupBlacklistUseCase {

    /**
     * 만료된 블랙리스트 토큰을 정리합니다.
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>현재 시간 기준 만료된 토큰 조회 (Redis ZSET ZRANGEBYSCORE)</li>
     *   <li>배치 삭제 (1000개씩, Redis SREM + ZREM)</li>
     *   <li>삭제된 토큰 수 반환</li>
     * </ol>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>배치 크기: 1000개 (조정 가능)</li>
     *   <li>Redis Pipeline 사용 (네트워크 왕복 최소화)</li>
     *   <li>실행 시간 제한: 최대 30초 (타임아웃)</li>
     * </ul>
     *
     * @return 정리 결과 Result
     * @author AuthHub Team
     * @since 1.0.0
     */
    Result cleanup();

    /**
     * 블랙리스트 정리 결과 Result.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Result {

        private final int deletedCount;

        /**
         * Result 생성자.
         *
         * @param deletedCount 삭제된 토큰 수
         * @throws IllegalArgumentException deletedCount가 음수인 경우
         */
        public Result(final int deletedCount) {
            if (deletedCount < 0) {
                throw new IllegalArgumentException("Deleted count cannot be negative");
            }

            this.deletedCount = deletedCount;
        }

        /**
         * 삭제된 토큰 수를 반환합니다.
         *
         * @return 삭제된 토큰 수
         */
        public int getDeletedCount() {
            return this.deletedCount;
        }
    }
}
