package com.ryuqq.authhub.application.security.blacklist.service.command;

import com.ryuqq.authhub.application.security.blacklist.port.in.CleanupBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.out.RemoveFromBlacklistPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Blacklist Cleanup Service - Blacklist 정리 UseCase 구현체.
 *
 * <p>만료된 JWT 토큰을 블랙리스트에서 제거하는 Batch Service입니다.
 * Scheduler를 통해 주기적으로 실행되어 Redis 메모리를 효율적으로 관리합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li><strong>만료된 토큰 조회</strong>:
 *     <ul>
 *       <li>현재 시간 기준 만료된 JTI 목록 조회</li>
 *       <li>Redis ZSET ZRANGEBYSCORE 사용 (0 ~ currentTime)</li>
 *       <li>배치 크기: 1000개 (성능 최적화)</li>
 *     </ul>
 *   </li>
 *   <li><strong>배치 삭제</strong>:
 *     <ul>
 *       <li>Redis SET에서 JTI 제거 (SREM blacklist:tokens {jti1} {jti2} ...)</li>
 *       <li>Redis ZSET에서 만료 정보 제거 (ZREM blacklist:expiry {jti1} {jti2} ...)</li>
 *       <li>Redis Pipeline 사용 (네트워크 왕복 최소화)</li>
 *     </ul>
 *   </li>
 *   <li><strong>통계 반환</strong>:
 *     <ul>
 *       <li>삭제된 토큰 수 반환</li>
 *       <li>로그 기록 (INFO 레벨)</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리 (성능 최적화)</li>
 *   <li>✅ @Transactional 미사용 - Redis는 자체적으로 원자성 보장</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - 의존성 역전 원칙 준수</li>
 *   <li>✅ Batch 처리 - 대량 삭제 시 성능 최적화</li>
 * </ul>
 *
 * <p><strong>트랜잭션 설계:</strong></p>
 * <ul>
 *   <li>@Transactional 없음 - Redis 연산은 자체적으로 원자성 보장</li>
 *   <li>Redis ZRANGEBYSCORE, SREM, ZREM - 원자적 연산</li>
 *   <li>DB 트랜잭션 불필요 - 읽기/쓰기가 모두 Redis</li>
 *   <li>성능 최적화 - 트랜잭션 오버헤드 제거</li>
 *   <li>ArchUnit 예외 - Redis 기반 서비스는 @Transactional 불필요</li>
 * </ul>
 *
 * <p><strong>성능 고려사항:</strong></p>
 * <ul>
 *   <li>배치 크기: 1000개 (조정 가능)</li>
 *   <li>Redis Pipeline 사용 권장 (네트워크 왕복 최소화)</li>
 *   <li>실행 시간 제한: 최대 30초 (타임아웃)</li>
 *   <li>Scheduler 실행 주기: 1시간 (조정 가능)</li>
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
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>RemoveFromBlacklistPort - Redis ZSET/SET 제거</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class BlacklistCleanupService implements CleanupBlacklistUseCase {

    private static final Logger log = LoggerFactory.getLogger(BlacklistCleanupService.class);

    /**
     * Batch 삭제 시 한 번에 조회할 최대 개수 (성능 최적화).
     */
    private static final int BATCH_SIZE = 1000;

    private final RemoveFromBlacklistPort removeFromBlacklistPort;

    /**
     * BlacklistCleanupService 생성자.
     *
     * @param removeFromBlacklistPort Redis 제거 Port
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public BlacklistCleanupService(final RemoveFromBlacklistPort removeFromBlacklistPort) {
        this.removeFromBlacklistPort = Objects.requireNonNull(removeFromBlacklistPort, "RemoveFromBlacklistPort cannot be null");
    }

    /**
     * 만료된 블랙리스트 토큰을 정리합니다 (Command Operation).
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
     * <p><strong>로깅:</strong></p>
     * <ul>
     *   <li>삭제 시작 로그 (INFO 레벨)</li>
     *   <li>삭제 완료 로그 (INFO 레벨, 삭제된 개수 포함)</li>
     *   <li>만료된 토큰이 없으면 DEBUG 레벨</li>
     * </ul>
     *
     * @return 정리 결과 Result (삭제된 토큰 수)
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public CleanupBlacklistUseCase.Result cleanup() {
        log.info("Starting blacklist cleanup process...");

        // 1. 현재 시간 기준 만료된 토큰 조회
        final long currentEpochSeconds = Instant.now().getEpochSecond();
        final Set<String> expiredJtis = this.removeFromBlacklistPort.findExpiredJtis(
                currentEpochSeconds,
                BATCH_SIZE
        );

        // 2. 만료된 토큰이 없으면 종료
        if (expiredJtis.isEmpty()) {
            log.debug("No expired tokens found in blacklist");
            return new CleanupBlacklistUseCase.Result(0);
        }

        // 3. 배치 삭제 (Redis Pipeline 사용)
        final int deletedCount = this.removeFromBlacklistPort.removeAll(expiredJtis);

        log.info("Blacklist cleanup completed. Deleted {} expired tokens", deletedCount);

        return new CleanupBlacklistUseCase.Result(deletedCount);
    }
}
