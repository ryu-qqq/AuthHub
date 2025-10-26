package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.adapter;

import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity.BlacklistedTokenRedisEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.mapper.BlacklistEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.repository.BlacklistRedisRepository;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * BlacklistPersistenceAdapter 단위 테스트.
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다.
 * Redis나 Spring Context 없이 빠르게 실행됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>RedisTemplate, Repository, Mapper는 Mock으로 대체</li>
 *   <li>각 Port 메서드의 동작 검증</li>
 *   <li>Redis Hash + SET + ZSET 구조 검증</li>
 *   <li>Null 안전성 및 유효성 검증</li>
 *   <li>배치 삭제 로직 검증</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("BlacklistPersistenceAdapter 단위 테스트")
class BlacklistPersistenceAdapterTest {

    @Mock
    private BlacklistRedisRepository repository;

    @Mock
    private BlacklistEntityMapper mapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private BlacklistPersistenceAdapter adapter;

    private static final String TEST_JTI = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TEST_JTI_2 = "550e8400-e29b-41d4-a716-446655440001";
    private static final long TEST_EXPIRES_AT_EPOCH_SECONDS = 1704067200L; // 2024-01-01 00:00:00 UTC
    private static final String SET_KEY = "blacklist:tokens";
    private static final String ZSET_KEY = "blacklist:expiry";

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForSet()).willReturn(setOperations);
        given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
    }

    @Nested
    @DisplayName("add 테스트 - 블랙리스트 토큰 추가")
    class AddTests {

        @Test
        @DisplayName("토큰을 블랙리스트에 추가하고 Redis Hash, SET, ZSET에 저장한다")
        void add_ValidToken_SavesSuccessfully() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "LOGOUT",
                    Instant.now(),
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(any(BlacklistedTokenRedisEntity.class))).willReturn(entity);
            given(setOperations.add(eq(SET_KEY), eq(TEST_JTI))).willReturn(1L);
            given(zSetOperations.add(eq(ZSET_KEY), eq(TEST_JTI), anyDouble())).willReturn(true);

            // when
            adapter.add(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
            then(repository).should(times(1)).save(entity);
            then(setOperations).should(times(1)).add(SET_KEY, TEST_JTI);
            then(zSetOperations).should(times(1)).add(eq(ZSET_KEY), eq(TEST_JTI), anyDouble());
        }

        @Test
        @DisplayName("토큰이 null이면 예외를 발생시킨다")
        void add_NullToken_ThrowsException() {
            assertThatThrownBy(() -> adapter.add(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("BlacklistedToken cannot be null");
        }

        @Test
        @DisplayName("PASSWORD_CHANGE 사유로 토큰을 추가한다")
        void add_PasswordChangeReason_SavesSuccessfully() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.PASSWORD_CHANGE
            );

            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "PASSWORD_CHANGE",
                    Instant.now(),
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(any(BlacklistedTokenRedisEntity.class))).willReturn(entity);

            // when
            adapter.add(domain);

            // then
            then(repository).should(times(1)).save(entity);
        }
    }

    @Nested
    @DisplayName("exists 테스트 - JTI 존재 여부 확인")
    class ExistsTests {

        @Test
        @DisplayName("JTI가 블랙리스트에 존재하면 true를 반환한다")
        void exists_JtiInBlacklist_ReturnsTrue() {
            // given
            given(setOperations.isMember(SET_KEY, TEST_JTI)).willReturn(true);

            // when
            final boolean result = adapter.exists(TEST_JTI);

            // then
            assertThat(result).isTrue();
            then(setOperations).should(times(1)).isMember(SET_KEY, TEST_JTI);
        }

        @Test
        @DisplayName("JTI가 블랙리스트에 존재하지 않으면 false를 반환한다")
        void exists_JtiNotInBlacklist_ReturnsFalse() {
            // given
            given(setOperations.isMember(SET_KEY, TEST_JTI)).willReturn(false);

            // when
            final boolean result = adapter.exists(TEST_JTI);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Redis 응답이 null이면 false를 반환한다")
        void exists_NullRedisResponse_ReturnsFalse() {
            // given
            given(setOperations.isMember(SET_KEY, TEST_JTI)).willReturn(null);

            // when
            final boolean result = adapter.exists(TEST_JTI);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("JTI가 null이면 예외를 발생시킨다")
        void exists_NullJti_ThrowsException() {
            assertThatThrownBy(() -> adapter.exists(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("jti cannot be null or empty");
        }

        @Test
        @DisplayName("JTI가 빈 문자열이면 예외를 발생시킨다")
        void exists_EmptyJti_ThrowsException() {
            assertThatThrownBy(() -> adapter.exists(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("jti cannot be null or empty");
        }

        @Test
        @DisplayName("JTI가 공백 문자열이면 예외를 발생시킨다")
        void exists_BlankJti_ThrowsException() {
            assertThatThrownBy(() -> adapter.exists("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("jti cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("findExpiredJtis 테스트 - 만료된 JTI 조회")
    class FindExpiredJtisTests {

        @Test
        @DisplayName("만료된 JTI 목록을 조회한다")
        void findExpiredJtis_ExpiredTokensExist_ReturnsJtis() {
            // given
            final long maxEpochSeconds = Instant.now().getEpochSecond();
            final int limit = 100;
            final Set<String> expiredJtis = Set.of(TEST_JTI, TEST_JTI_2);

            given(zSetOperations.rangeByScore(
                    eq(ZSET_KEY),
                    eq(Double.NEGATIVE_INFINITY),
                    anyDouble(),
                    eq(0L),
                    eq((long) limit)
            )).willReturn(expiredJtis);

            // when
            final Set<String> result = adapter.findExpiredJtis(maxEpochSeconds, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(TEST_JTI, TEST_JTI_2);
        }

        @Test
        @DisplayName("만료된 토큰이 없으면 빈 Set을 반환한다")
        void findExpiredJtis_NoExpiredTokens_ReturnsEmptySet() {
            // given
            final long maxEpochSeconds = Instant.now().getEpochSecond();
            final int limit = 100;

            given(zSetOperations.rangeByScore(
                    eq(ZSET_KEY),
                    eq(Double.NEGATIVE_INFINITY),
                    anyDouble(),
                    eq(0L),
                    eq((long) limit)
            )).willReturn(Set.of());

            // when
            final Set<String> result = adapter.findExpiredJtis(maxEpochSeconds, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Redis 응답이 null이면 빈 Set을 반환한다")
        void findExpiredJtis_NullRedisResponse_ReturnsEmptySet() {
            // given
            final long maxEpochSeconds = Instant.now().getEpochSecond();
            final int limit = 100;

            given(zSetOperations.rangeByScore(
                    eq(ZSET_KEY),
                    eq(Double.NEGATIVE_INFINITY),
                    anyDouble(),
                    eq(0L),
                    eq((long) limit)
            )).willReturn(null);

            // when
            final Set<String> result = adapter.findExpiredJtis(maxEpochSeconds, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maxEpochSeconds가 음수이면 예외를 발생시킨다")
        void findExpiredJtis_NegativeMaxEpochSeconds_ThrowsException() {
            assertThatThrownBy(() -> adapter.findExpiredJtis(-1L, 100))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("maxEpochSeconds cannot be negative");
        }

        @Test
        @DisplayName("limit이 0 이하면 예외를 발생시킨다")
        void findExpiredJtis_ZeroLimit_ThrowsException() {
            final long maxEpochSeconds = Instant.now().getEpochSecond();

            assertThatThrownBy(() -> adapter.findExpiredJtis(maxEpochSeconds, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("limit must be positive");
        }

        @Test
        @DisplayName("limit이 음수이면 예외를 발생시킨다")
        void findExpiredJtis_NegativeLimit_ThrowsException() {
            final long maxEpochSeconds = Instant.now().getEpochSecond();

            assertThatThrownBy(() -> adapter.findExpiredJtis(maxEpochSeconds, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("limit must be positive");
        }
    }

    @Nested
    @DisplayName("removeAll 테스트 - 배치 삭제")
    class RemoveAllTests {

        @Test
        @DisplayName("JTI 목록을 Redis Hash, SET, ZSET에서 모두 삭제한다 (배치 삭제)")
        void removeAll_ValidJtis_RemovesSuccessfully() {
            // given
            final Set<String> jtis = Set.of(TEST_JTI, TEST_JTI_2);

            // when
            final int removedCount = adapter.removeAll(jtis);

            // then
            assertThat(removedCount).isEqualTo(2);
            // Redis Hash 배치 삭제 검증 (redisTemplate.delete() 1회 호출)
            then(redisTemplate).should(times(1)).delete(argThat(keys ->
                    keys instanceof java.util.Collection &&
                    ((java.util.Collection<?>) keys).size() == 2 &&
                    ((java.util.Collection<?>) keys).containsAll(
                            Set.of("blacklist_token:" + TEST_JTI, "blacklist_token:" + TEST_JTI_2)
                    )
            ));
            // Repository deleteById() 호출 안 함 (배치 삭제로 대체)
            then(repository).should(never()).deleteById(anyString());
            then(repository).should(never()).deleteAllById(any());
            // SET, ZSET 삭제 검증
            then(setOperations).should(times(1)).remove(eq(SET_KEY), any(Object[].class));
            then(zSetOperations).should(times(1)).remove(eq(ZSET_KEY), any(Object[].class));
        }

        @Test
        @DisplayName("단일 JTI를 삭제한다 (배치 삭제)")
        void removeAll_SingleJti_RemovesSuccessfully() {
            // given
            final Set<String> jtis = Set.of(TEST_JTI);

            // when
            final int removedCount = adapter.removeAll(jtis);

            // then
            assertThat(removedCount).isEqualTo(1);
            // Redis Hash 배치 삭제 검증 (단일 JTI도 배치 처리)
            then(redisTemplate).should(times(1)).delete(argThat(keys ->
                    keys instanceof java.util.Collection &&
                    ((java.util.Collection<?>) keys).size() == 1 &&
                    ((java.util.Collection<?>) keys).contains("blacklist_token:" + TEST_JTI)
            ));
            // Repository deleteById() 호출 안 함
            then(repository).should(never()).deleteById(anyString());
        }

        @Test
        @DisplayName("JTI 목록이 null이면 예외를 발생시킨다")
        void removeAll_NullJtis_ThrowsException() {
            assertThatThrownBy(() -> adapter.removeAll(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("JTI set cannot be null or empty");
        }

        @Test
        @DisplayName("JTI 목록이 비어있으면 예외를 발생시킨다")
        void removeAll_EmptyJtis_ThrowsException() {
            assertThatThrownBy(() -> adapter.removeAll(Set.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("JTI set cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("토큰 추가 → 존재 확인 → 삭제 시나리오가 올바르게 동작한다")
        void scenario_AddCheckRemove_WorksCorrectly() {
            // given - 토큰 추가
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "LOGOUT",
                    Instant.now(),
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(any(BlacklistedTokenRedisEntity.class))).willReturn(entity);

            // when - 토큰 추가
            adapter.add(domain);

            // then - 토큰 추가 검증
            then(repository).should(times(1)).save(entity);
            then(setOperations).should(times(1)).add(SET_KEY, TEST_JTI);
            then(zSetOperations).should(times(1)).add(eq(ZSET_KEY), eq(TEST_JTI), anyDouble());

            // given - 토큰 존재 확인
            given(setOperations.isMember(SET_KEY, TEST_JTI)).willReturn(true);

            // when - 존재 확인
            final boolean exists = adapter.exists(TEST_JTI);

            // then - 존재 검증
            assertThat(exists).isTrue();

            // when - 토큰 삭제
            final int removedCount = adapter.removeAll(Set.of(TEST_JTI));

            // then - 삭제 검증
            assertThat(removedCount).isEqualTo(1);
            then(repository).should(times(1)).deleteById(TEST_JTI);
        }

        @Test
        @DisplayName("만료된 토큰 조회 → 배치 삭제 시나리오가 올바르게 동작한다")
        void scenario_FindExpiredAndRemove_WorksCorrectly() {
            // given - 만료된 토큰 조회
            final long currentEpochSeconds = Instant.now().getEpochSecond();
            final Set<String> expiredJtis = Set.of(TEST_JTI, TEST_JTI_2);

            given(zSetOperations.rangeByScore(
                    eq(ZSET_KEY),
                    eq(Double.NEGATIVE_INFINITY),
                    anyDouble(),
                    eq(0L),
                    eq(1000L)
            )).willReturn(expiredJtis);

            // when - 만료된 토큰 조회
            final Set<String> foundJtis = adapter.findExpiredJtis(currentEpochSeconds, 1000);

            // then - 조회 검증
            assertThat(foundJtis).hasSize(2);
            assertThat(foundJtis).containsExactlyInAnyOrder(TEST_JTI, TEST_JTI_2);

            // when - 배치 삭제
            final int removedCount = adapter.removeAll(foundJtis);

            // then - 삭제 검증
            assertThat(removedCount).isEqualTo(2);
            then(repository).should(times(2)).deleteById(anyString());
        }
    }
}
