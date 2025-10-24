package com.ryuqq.authhub.bootstrap.persistence.token.adapter;

import com.ryuqq.authhub.bootstrap.persistence.token.entity.RefreshTokenRedisEntity;
import com.ryuqq.authhub.bootstrap.persistence.token.mapper.TokenEntityMapper;
import com.ryuqq.authhub.bootstrap.persistence.token.repository.RefreshTokenRedisRepository;
import com.ryuqq.authhub.application.auth.port.out.LoadRefreshTokenPort;
import com.ryuqq.authhub.application.auth.port.out.SaveRefreshTokenPort;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Token Redis Adapter.
 *
 * <p>Redis를 사용하여 Refresh Token을 저장하고 조회하는 Adapter입니다.
 * {@link SaveRefreshTokenPort}와 {@link LoadRefreshTokenPort}를 구현하여
 * Application Layer의 Port를 Persistence Layer의 Redis Repository와 연결합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Refresh Token을 Redis에 저장 (TTL 자동 관리)</li>
 *   <li>TokenId로 Refresh Token 조회</li>
 *   <li>Domain Token ↔ Redis Entity 변환 (TokenEntityMapper 위임)</li>
 * </ul>
 *
 * <p><strong>Redis Key 패턴:</strong></p>
 * <ul>
 *   <li>Primary Key: {@code refresh_token:{tokenId}}</li>
 *   <li>TTL: 14일(1,209,600초) - @RedisHash에서 자동 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - Mapper를 통한 변환, 직접 chaining 금지</li>
 *   <li>✅ Transaction 경계 - Redis는 내부 시스템, @Transactional 내부 호출 가능</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인 성공 시 Refresh Token을 Redis에 저장</li>
 *   <li>Access Token 재발급 시 Refresh Token 조회 및 검증</li>
 *   <li>Refresh Token Rotation 전략 구현</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TokenRedisAdapter implements SaveRefreshTokenPort, LoadRefreshTokenPort {

    private final RefreshTokenRedisRepository repository;
    private final TokenEntityMapper mapper;

    /**
     * TokenRedisAdapter 생성자.
     * Spring의 의존성 주입을 통해 Repository와 Mapper를 주입받습니다.
     *
     * @param repository RefreshTokenRedisRepository (null 불가)
     * @param mapper TokenEntityMapper (null 불가)
     * @throws NullPointerException repository 또는 mapper가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public TokenRedisAdapter(
            final RefreshTokenRedisRepository repository,
            final TokenEntityMapper mapper
    ) {
        this.repository = Objects.requireNonNull(repository, "RefreshTokenRedisRepository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "TokenEntityMapper cannot be null");
    }

    /**
     * Refresh Token을 Redis에 저장합니다.
     *
     * <p>Token은 REFRESH 타입이어야 하며, ACCESS 타입 토큰은 저장할 수 없습니다.
     * Redis의 TTL(Time To Live)은 {@code @RedisHash(timeToLive = 1209600)}에 의해
     * 14일(1,209,600초)로 자동 설정됩니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>Redis는 내부 시스템으로, MySQL과 동일하게 취급합니다.</li>
     *   <li>외부 API (S3, HTTP, SQS)와 구분됩니다.</li>
     * </ul>
     *
     * <p><strong>동작 흐름:</strong></p>
     * <ol>
     *   <li>Token이 null이 아닌지 검증</li>
     *   <li>Token이 REFRESH 타입인지 검증 (Mapper에서 수행)</li>
     *   <li>Token을 RefreshTokenRedisEntity로 변환</li>
     *   <li>Redis에 저장 (기존 토큰이 있으면 덮어쓰기)</li>
     * </ol>
     *
     * @param token 저장할 Refresh Token (TokenType이 REFRESH여야 함) (null 불가)
     * @throws NullPointerException token이 null인 경우
     * @throws IllegalArgumentException token이 REFRESH 타입이 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void save(final Token token) {
        Objects.requireNonNull(token, "Token cannot be null");

        final RefreshTokenRedisEntity entity = this.mapper.toEntity(token);
        this.repository.save(entity);
    }

    /**
     * TokenId를 사용하여 Redis에서 Refresh Token을 조회합니다.
     *
     * <p>Refresh Token이 Redis에 존재하면 Token Domain Aggregate를 반환하고,
     * 존재하지 않으면 {@code Optional.empty()}를 반환합니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>Redis는 내부 시스템으로, MySQL과 동일하게 취급합니다.</li>
     *   <li>외부 API (S3, HTTP, SQS)와 구분됩니다.</li>
     * </ul>
     *
     * <p><strong>동작 흐름:</strong></p>
     * <ol>
     *   <li>TokenId가 null이 아닌지 검증</li>
     *   <li>TokenId를 String으로 변환 (UUID String)</li>
     *   <li>Redis에서 Entity 조회</li>
     *   <li>조회된 Entity를 Domain Token으로 변환</li>
     * </ol>
     *
     * <p><strong>반환값:</strong></p>
     * <ul>
     *   <li>Token이 존재하면: {@code Optional<Token>} (TokenType이 REFRESH)</li>
     *   <li>Token이 존재하지 않으면: {@code Optional.empty()}</li>
     * </ul>
     *
     * @param tokenId Refresh Token의 고유 식별자 (null 불가)
     * @return Refresh Token이 존재하면 {@code Optional<Token>}, 존재하지 않으면 {@code Optional.empty()}
     * @throws NullPointerException tokenId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public Optional<Token> load(final TokenId tokenId) {
        Objects.requireNonNull(tokenId, "TokenId cannot be null");

        return this.repository.findById(tokenId.asString())
                .map(this.mapper::toDomain);
    }
}
