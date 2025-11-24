package com.ryuqq.authhub.adapter.out.persistence.redis.token.adapter;

import com.ryuqq.authhub.adapter.out.persistence.redis.token.entity.RefreshTokenRedisEntity;
import com.ryuqq.authhub.adapter.out.persistence.redis.token.mapper.TokenEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.redis.token.repository.RefreshTokenRedisRepository;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * TokenRedisAdapter 단위 테스트.
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다.
 * Redis나 Spring Context 없이 빠르게 실행됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>Repository와 Mapper는 Mock으로 대체</li>
 *   <li>각 Port 메서드의 동작 검증</li>
 *   <li>Null 안전성 검증</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TokenRedisAdapter 단위 테스트")
class TokenRedisAdapterTest {

    @Mock
    private RefreshTokenRedisRepository repository;

    @Mock
    private TokenEntityMapper mapper;

    @InjectMocks
    private TokenRedisAdapter adapter;

    private TokenId testTokenId;
    private UserId testUserId;
    private Token testToken;
    private RefreshTokenRedisEntity testEntity;

    @BeforeEach
    void setUp() {
        testTokenId = TokenId.newId();
        testUserId = UserId.newId();

        testToken = Token.create(
                testUserId,
                TokenType.REFRESH,
                JwtToken.from("TEST_JWT_TOKEN"),
                Duration.ofDays(14)
        );

        testEntity = RefreshTokenRedisEntity.create(
                testTokenId.asString(),
                testUserId.asString(),
                "TEST_JWT_TOKEN",
                System.currentTimeMillis(),
                System.currentTimeMillis() + Duration.ofDays(14).toMillis()
        );
    }

    @Test
    @DisplayName("Refresh Token을 성공적으로 저장한다")
    void save_Success() {
        // given
        given(mapper.toEntity(testToken)).willReturn(testEntity);
        given(repository.save(testEntity)).willReturn(testEntity);

        // when
        adapter.save(testToken);

        // then
        then(mapper).should(times(1)).toEntity(testToken);
        then(repository).should(times(1)).save(testEntity);
    }

    @Test
    @DisplayName("null Token 저장 시 NullPointerException 발생")
    void save_NullToken_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Token cannot be null");

        then(mapper).shouldHaveNoInteractions();
        then(repository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("TokenId로 Refresh Token을 성공적으로 조회한다")
    void load_Success() {
        // given
        given(repository.findById(testTokenId.asString())).willReturn(Optional.of(testEntity));
        given(mapper.toDomain(testEntity)).willReturn(testToken);

        // when
        Optional<Token> result = adapter.load(testTokenId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testToken);

        then(repository).should(times(1)).findById(testTokenId.asString());
        then(mapper).should(times(1)).toDomain(testEntity);
    }

    @Test
    @DisplayName("존재하지 않는 TokenId로 조회 시 Empty를 반환한다")
    void load_NotFound_ReturnsEmpty() {
        // given
        given(repository.findById(testTokenId.asString())).willReturn(Optional.empty());

        // when
        Optional<Token> result = adapter.load(testTokenId);

        // then
        assertThat(result).isEmpty();

        then(repository).should(times(1)).findById(testTokenId.asString());
        then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("null TokenId로 조회 시 NullPointerException 발생")
    void load_NullTokenId_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.load(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("TokenId cannot be null");

        then(repository).shouldHaveNoInteractions();
        then(mapper).shouldHaveNoInteractions();
    }
}
