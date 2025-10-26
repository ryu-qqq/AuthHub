package com.ryuqq.authhub.application.security.blacklist.service.command;

import com.ryuqq.authhub.application.security.blacklist.assembler.BlacklistAssembler;
import com.ryuqq.authhub.application.security.blacklist.port.in.AddToBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.in.CheckBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.out.AddToBlacklistPort;
import com.ryuqq.authhub.application.security.blacklist.port.out.CheckBlacklistPort;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Blacklist Service - Blacklist UseCase 구현체.
 *
 * <p>Redis 기반 분산 JWT 블랙리스트 시스템의 핵심 비즈니스 로직을 담당하는 Command Service입니다.
 * BlacklistedToken Aggregate를 활용하여 토큰 무효화를 처리하고, Redis Port를 통해 블랙리스트를 관리합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li><strong>Add to Blacklist</strong>:
 *     <ul>
 *       <li>Command → BlacklistedToken Aggregate 변환 (Assembler)</li>
 *       <li>Redis SET에 JTI 추가 (AddToBlacklistPort)</li>
 *       <li>Redis ZSET에 만료 시간 추가 (정리 대상 식별)</li>
 *       <li>TTL 자동 설정 (만료 시간까지)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Check Blacklist</strong>:
 *     <ul>
 *       <li>Redis SET에서 JTI 존재 여부 확인 (CheckBlacklistPort)</li>
 *       <li>O(1) 복잡도로 빠른 조회</li>
 *       <li>boolean 반환 (블랙리스트 여부)</li>
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
 *   <li>✅ Command/Query 분리 - Add(Command), Check(Query)</li>
 * </ul>
 *
 * <p><strong>트랜잭션 설계:</strong></p>
 * <ul>
 *   <li>@Transactional 없음 - Redis 연산은 자체적으로 원자성 보장</li>
 *   <li>Redis SADD 명령 - 원자적(Atomic) 연산</li>
 *   <li>Redis SISMEMBER 명령 - 원자적 조회</li>
 *   <li>DB 트랜잭션 불필요 - 읽기/쓰기가 모두 Redis</li>
 *   <li>성능 최적화 - 트랜잭션 오버헤드 제거</li>
 *   <li>ArchUnit 예외 - Redis 기반 서비스는 @Transactional 불필요</li>
 * </ul>
 *
 * <p><strong>Redis 구조:</strong></p>
 * <pre>
 * blacklist:tokens = SET of JTIs
 * blacklist:expiry = ZSET {jti: expiration_timestamp}
 * </pre>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>AddToBlacklistPort - Redis SET/ZSET 추가</li>
 *   <li>CheckBlacklistPort - Redis SET 존재 확인</li>
 *   <li>BlacklistAssembler - Command → Domain 변환</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class BlacklistService implements
        AddToBlacklistUseCase,
        CheckBlacklistUseCase {

    private final AddToBlacklistPort addToBlacklistPort;
    private final CheckBlacklistPort checkBlacklistPort;
    private final BlacklistAssembler blacklistAssembler;

    /**
     * BlacklistService 생성자.
     *
     * @param addToBlacklistPort Redis 추가 Port
     * @param checkBlacklistPort Redis 확인 Port
     * @param blacklistAssembler Domain ↔ Application 변환 Assembler
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public BlacklistService(
            final AddToBlacklistPort addToBlacklistPort,
            final CheckBlacklistPort checkBlacklistPort,
            final BlacklistAssembler blacklistAssembler
    ) {
        this.addToBlacklistPort = Objects.requireNonNull(addToBlacklistPort, "AddToBlacklistPort cannot be null");
        this.checkBlacklistPort = Objects.requireNonNull(checkBlacklistPort, "CheckBlacklistPort cannot be null");
        this.blacklistAssembler = Objects.requireNonNull(blacklistAssembler, "BlacklistAssembler cannot be null");
    }

    /**
     * JWT 토큰을 블랙리스트에 추가합니다 (Command Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Command → BlacklistedToken Aggregate 변환 (Assembler)</li>
     *   <li>Redis SET에 JTI 추가 (SADD blacklist:tokens {jti})</li>
     *   <li>Redis ZSET에 만료 시간 추가 (ZADD blacklist:expiry {expiresAt} {jti})</li>
     *   <li>TTL 설정 (만료 시간까지)</li>
     * </ol>
     *
     * <p><strong>멱등성:</strong></p>
     * <ul>
     *   <li>이미 블랙리스트에 존재하는 토큰을 다시 추가해도 문제 없음</li>
     *   <li>Redis SADD는 중복을 자동으로 처리</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>@Transactional 없음 - Redis는 자체적으로 원자성 보장</li>
     *   <li>네트워크 지연 가능 - 비동기 처리 고려 가능</li>
     * </ul>
     *
     * @param command 블랙리스트 추가 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void addToBlacklist(final AddToBlacklistUseCase.Command command) {
        Objects.requireNonNull(command, "Command cannot be null");

        // 1. Command → BlacklistedToken Aggregate 변환
        final BlacklistedToken token = this.blacklistAssembler.toDomain(command);

        // 2. Redis SET/ZSET에 추가 (TTL 포함)
        this.addToBlacklistPort.add(token);
    }

    /**
     * JWT 토큰이 블랙리스트에 등록되어 있는지 확인합니다 (Query Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis SET에서 JTI 존재 여부 확인 (SISMEMBER blacklist:tokens {jti})</li>
     *   <li>O(1) 복잡도로 빠른 조회</li>
     * </ol>
     *
     * @param query 블랙리스트 확인 Query
     * @return 블랙리스트에 등록되어 있으면 true, 아니면 false
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean isBlacklisted(final CheckBlacklistUseCase.Query query) {
        Objects.requireNonNull(query, "Query cannot be null");

        // Redis SET에서 JTI 존재 확인
        return this.checkBlacklistPort.exists(query.getJti());
    }
}
