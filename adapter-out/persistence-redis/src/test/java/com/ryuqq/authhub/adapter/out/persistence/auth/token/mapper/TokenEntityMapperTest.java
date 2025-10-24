package com.ryuqq.authhub.adapter.out.persistence.auth.token.mapper;

import com.ryuqq.authhub.adapter.out.persistence.auth.token.entity.RefreshTokenRedisEntity;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TokenEntityMapper 단위 테스트.
 *
 * <p>Mapper의 변환 로직을 검증합니다.
 * Domain ↔ Redis Entity 변환이 올바르게 수행되는지 확인합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>Plain Unit Test - Mock 없이 실제 Mapper 객체 테스트</li>
 *   <li>변환 로직의 정확성 검증</li>
 *   <li>Null 안전성 검증</li>
 *   <li>TokenType 검증 (REFRESH만 허용)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("TokenEntityMapper 단위 테스트")
class TokenEntityMapperTest {

    private TokenEntityMapper mapper;
    private UserId testUserId;
    private TokenId testTokenId;

    @BeforeEach
    void setUp() {
        mapper = new TokenEntityMapper();
        testUserId = UserId.newId();
        testTokenId = TokenId.newId();
    }

    @Test
    @DisplayName("Domain Token을 Redis Entity로 변환한다")
    void toEntity_Success() {
        // given
        Token token = Token.create(
                testUserId,
                TokenType.REFRESH,
                JwtToken.from("TEST_JWT_TOKEN"),
                Duration.ofDays(14)
        );

        // when
        RefreshTokenRedisEntity entity = mapper.toEntity(token);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getTokenId()).isEqualTo(token.getId().asString());
        assertThat(entity.getUserId()).isEqualTo(testUserId.asString());
        assertThat(entity.getJwtToken()).isEqualTo("TEST_JWT_TOKEN");
        assertThat(entity.getIssuedAt()).isEqualTo(token.getIssuedAt().value().toEpochMilli());
        assertThat(entity.getExpiresAt()).isEqualTo(token.getExpiresAt().value().toEpochMilli());
    }

    @Test
    @DisplayName("ACCESS Token은 Redis Entity로 변환할 수 없다")
    void toEntity_AccessToken_ThrowsException() {
        // given
        Token accessToken = Token.create(
                testUserId,
                TokenType.ACCESS,
                JwtToken.from("TEST_ACCESS_TOKEN"),
                Duration.ofMinutes(15)
        );

        // when & then
        assertThatThrownBy(() -> mapper.toEntity(accessToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only REFRESH type tokens can be stored in Redis");
    }

    @Test
    @DisplayName("null Token을 Entity로 변환 시 NullPointerException 발생")
    void toEntity_NullToken_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Token cannot be null");
    }

    @Test
    @DisplayName("Redis Entity를 Domain Token으로 변환한다")
    void toDomain_Success() {
        // given
        long issuedAtMillis = System.currentTimeMillis();
        long expiresAtMillis = issuedAtMillis + Duration.ofDays(14).toMillis();

        RefreshTokenRedisEntity entity = RefreshTokenRedisEntity.create(
                testTokenId.asString(),
                testUserId.asString(),
                "TEST_JWT_TOKEN",
                issuedAtMillis,
                expiresAtMillis
        );

        // when
        Token token = mapper.toDomain(entity);

        // then
        assertThat(token).isNotNull();
        assertThat(token.getId().asString()).isEqualTo(testTokenId.asString());
        assertThat(token.getUserId().asString()).isEqualTo(testUserId.asString());
        assertThat(token.getJwtValue()).isEqualTo("TEST_JWT_TOKEN");
        assertThat(token.getType()).isEqualTo(TokenType.REFRESH);
        assertThat(token.getIssuedAt().value().toEpochMilli()).isEqualTo(issuedAtMillis);
        assertThat(token.getExpiresAt().value().toEpochMilli()).isEqualTo(expiresAtMillis);
    }

    @Test
    @DisplayName("null Entity를 Domain으로 변환 시 NullPointerException 발생")
    void toDomain_NullEntity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("RefreshTokenRedisEntity cannot be null");
    }

    @Test
    @DisplayName("Domain → Entity → Domain 양방향 변환이 올바르게 동작한다")
    void roundTrip_Success() {
        // given
        Token originalToken = Token.create(
                testUserId,
                TokenType.REFRESH,
                JwtToken.from("TEST_JWT_TOKEN"),
                Duration.ofDays(14)
        );

        // when
        RefreshTokenRedisEntity entity = mapper.toEntity(originalToken);
        Token reconstructedToken = mapper.toDomain(entity);

        // then
        assertThat(reconstructedToken).isNotNull();
        assertThat(reconstructedToken.getId().asString()).isEqualTo(originalToken.getId().asString());
        assertThat(reconstructedToken.getUserId()).isEqualTo(originalToken.getUserId());
        assertThat(reconstructedToken.getJwtValue()).isEqualTo(originalToken.getJwtValue());
        assertThat(reconstructedToken.getType()).isEqualTo(TokenType.REFRESH);
        assertThat(reconstructedToken.getIssuedAt()).isEqualTo(originalToken.getIssuedAt());
        assertThat(reconstructedToken.getExpiresAt()).isEqualTo(originalToken.getExpiresAt());
    }
}
