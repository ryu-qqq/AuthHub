package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.mapper;

import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity.BlacklistedTokenRedisEntity;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistedTokenId;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BlacklistEntityMapper 단위 테스트.
 *
 * <p>Domain {@link BlacklistedToken}과 Redis Entity {@link BlacklistedTokenRedisEntity} 간
 * 변환 로직을 검증합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>순수 Java 단위 테스트 (Spring Context 불필요)</li>
 *   <li>Domain → Entity 변환 검증 (toEntity)</li>
 *   <li>Entity → Domain 변환 검증 (toDomain)</li>
 *   <li>VO 추출 및 변환 검증</li>
 *   <li>Null 안전성 검증</li>
 *   <li>양방향 변환 일관성 검증</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("BlacklistEntityMapper 단위 테스트")
class BlacklistEntityMapperTest {

    private BlacklistEntityMapper mapper;

    private static final String TEST_JTI = "00000000-0000-0000-0000-000000000001";
    private static final long TEST_EXPIRES_AT_EPOCH_SECONDS = 1704067200L; // 2024-01-01 00:00:00 UTC
    private static final Instant TEST_BLACKLISTED_AT = Instant.now();

    @BeforeEach
    void setUp() {
        this.mapper = new BlacklistEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 테스트 - Domain → Redis Entity 변환")
    class ToEntityTests {

        @Test
        @DisplayName("정상적인 Domain Aggregate를 Entity로 변환한다")
        void toEntity_ValidDomain_ConvertsSuccessfully() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity).isNotNull();
            assertThat(entity.getJti()).isEqualTo(TEST_JTI);
            assertThat(entity.getReason()).isEqualTo("LOGOUT");
            assertThat(entity.getBlacklistedAt()).isNotNull();
            assertThat(entity.getExpiresAt()).isEqualTo(TEST_EXPIRES_AT_EPOCH_SECONDS * 1000); // milliseconds
            assertThat(entity.getTtl()).isPositive(); // TTL should be calculated
        }

        @Test
        @DisplayName("FORCE_LOGOUT 사유를 올바르게 변환한다")
        void toEntity_ForceLogout_ConvertsReasonCorrectly() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.FORCE_LOGOUT
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReason()).isEqualTo("FORCE_LOGOUT");
        }

        @Test
        @DisplayName("SECURITY_BREACH 사유를 올바르게 변환한다")
        void toEntity_SecurityBreach_ConvertsReasonCorrectly() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.SECURITY_BREACH
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReason()).isEqualTo("SECURITY_BREACH");
        }

        @Test
        @DisplayName("PASSWORD_CHANGE 사유를 올바르게 변환한다")
        void toEntity_PasswordChange_ConvertsReasonCorrectly() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.PASSWORD_CHANGE
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReason()).isEqualTo("PASSWORD_CHANGE");
        }

        @Test
        @DisplayName("domain이 null이면 예외를 발생시킨다")
        void toEntity_NullDomain_ThrowsException() {
            assertThatThrownBy(() -> mapper.toEntity(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("BlacklistedToken domain cannot be null");
        }

        @Test
        @DisplayName("JTI가 null인 Domain은 예외를 발생시킨다")
        void toEntity_DomainWithNullJti_ThrowsException() {
            // given
            final BlacklistedTokenId id = BlacklistedTokenId.newId();
            final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS);
            final BlacklistReason reason = BlacklistReason.LOGOUT;

            // when & then
            assertThatThrownBy(() ->
                    BlacklistedToken.reconstruct(id, null, expiresAt, reason, TEST_BLACKLISTED_AT)
            )
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toDomain 테스트 - Redis Entity → Domain 변환")
    class ToDomainTests {

        @Test
        @DisplayName("정상적인 Entity를 Domain으로 변환한다")
        void toDomain_ValidEntity_ConvertsSuccessfully() {
            // given
            final long expiresAtMillis = TEST_EXPIRES_AT_EPOCH_SECONDS * 1000;
            final long blacklistedAtMillis = TEST_BLACKLISTED_AT.toEpochMilli();

            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "LOGOUT",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochMilli(expiresAtMillis)
            );

            // when
            final BlacklistedToken domain = mapper.toDomain(entity);

            // then
            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isNotNull();
            assertThat(domain.getJti().asString()).isEqualTo(TEST_JTI);
            assertThat(domain.getReason()).isEqualTo(BlacklistReason.LOGOUT);
            assertThat(domain.getBlacklistedAt()).isNotNull();
            assertThat(domain.getExpiresAt().toEpochSeconds()).isEqualTo(TEST_EXPIRES_AT_EPOCH_SECONDS);
        }

        @Test
        @DisplayName("PASSWORD_CHANGE 사유를 올바르게 변환한다")
        void toDomain_PasswordChange_ConvertsReasonCorrectly() {
            // given
            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "PASSWORD_CHANGE",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            // when
            final BlacklistedToken domain = mapper.toDomain(entity);

            // then
            assertThat(domain.getReason()).isEqualTo(BlacklistReason.PASSWORD_CHANGE);
        }

        @Test
        @DisplayName("entity가 null이면 예외를 발생시킨다")
        void toDomain_NullEntity_ThrowsException() {
            assertThatThrownBy(() -> mapper.toDomain(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("BlacklistedTokenRedisEntity cannot be null");
        }

        @Test
        @DisplayName("유효하지 않은 BlacklistReason은 예외를 발생시킨다")
        void toDomain_InvalidReason_ThrowsException() {
            // given
            final BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "INVALID_REASON",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No enum constant");
        }
    }

    @Nested
    @DisplayName("양방향 변환 일관성 테스트")
    class BidirectionalConsistencyTests {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 핵심 데이터가 보존된다")
        void roundTrip_PreservesEssentialData() {
            // given
            final BlacklistedToken originalDomain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(originalDomain);
            final BlacklistedToken reconstructedDomain = mapper.toDomain(entity);

            // then
            assertThat(reconstructedDomain.getJti().asString())
                    .isEqualTo(originalDomain.getJti().asString());
            assertThat(reconstructedDomain.getReason())
                    .isEqualTo(originalDomain.getReason());
            assertThat(reconstructedDomain.getExpiresAt().toEpochSeconds())
                    .isEqualTo(originalDomain.getExpiresAt().toEpochSeconds());
        }

        @Test
        @DisplayName("여러 BlacklistReason에 대해 양방향 변환이 일관성을 유지한다")
        void roundTrip_AllReasons_MaintainsConsistency() {
            for (BlacklistReason reason : BlacklistReason.values()) {
                // given
                final BlacklistedToken originalDomain = BlacklistedToken.create(
                        Jti.of(TEST_JTI),
                        ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                        reason
                );

                // when
                final BlacklistedTokenRedisEntity entity = mapper.toEntity(originalDomain);
                final BlacklistedToken reconstructedDomain = mapper.toDomain(entity);

                // then
                assertThat(reconstructedDomain.getReason())
                        .as("Reason consistency for " + reason)
                        .isEqualTo(reason);
            }
        }

        @Test
        @DisplayName("같은 JTI를 가진 Entity는 항상 같은 BlacklistedTokenId를 생성한다 (결정적 UUID)")
        void toDomain_SameJti_GeneratesSameId() {
            // given
            final BlacklistedTokenRedisEntity entity1 = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "LOGOUT",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            final BlacklistedTokenRedisEntity entity2 = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,  // 같은 JTI
                    "FORCE_LOGOUT",  // 다른 reason
                    Instant.now(),  // 다른 blacklistedAt
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS + 1000)  // 다른 expiresAt
            );

            // when
            final BlacklistedToken domain1 = mapper.toDomain(entity1);
            final BlacklistedToken domain2 = mapper.toDomain(entity2);

            // then
            // 같은 JTI를 가진 엔티티는 항상 같은 ID를 생성해야 함
            assertThat(domain1.getId())
                    .isEqualTo(domain2.getId())
                    .withFailMessage("같은 JTI는 항상 같은 BlacklistedTokenId를 생성해야 합니다");

            // Domain equals/hashCode도 동일해야 함 (ID 기반 비교)
            assertThat(domain1).isEqualTo(domain2);
            assertThat(domain1.hashCode()).isEqualTo(domain2.hashCode());
        }

        @Test
        @DisplayName("다른 JTI를 가진 Entity는 다른 BlacklistedTokenId를 생성한다")
        void toDomain_DifferentJti_GeneratesDifferentId() {
            // given
            final BlacklistedTokenRedisEntity entity1 = BlacklistedTokenRedisEntity.create(
                    TEST_JTI,
                    "LOGOUT",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            final BlacklistedTokenRedisEntity entity2 = BlacklistedTokenRedisEntity.create(
                    TEST_JTI_2,  // 다른 JTI
                    "LOGOUT",
                    TEST_BLACKLISTED_AT,
                    Instant.ofEpochSecond(TEST_EXPIRES_AT_EPOCH_SECONDS)
            );

            // when
            final BlacklistedToken domain1 = mapper.toDomain(entity1);
            final BlacklistedToken domain2 = mapper.toDomain(entity2);

            // then
            // 다른 JTI를 가진 엔티티는 다른 ID를 생성해야 함
            assertThat(domain1.getId())
                    .isNotEqualTo(domain2.getId())
                    .withFailMessage("다른 JTI는 다른 BlacklistedTokenId를 생성해야 합니다");

            // Domain equals/hashCode도 달라야 함
            assertThat(domain1).isNotEqualTo(domain2);
            assertThat(domain1.hashCode()).isNotEqualTo(domain2.hashCode());
        }
    }

    @Nested
    @DisplayName("VO 추출 메서드 테스트")
    class VoExtractionTests {

        @Test
        @DisplayName("JTI 추출이 올바르게 동작한다")
        void extractJti_ValidDomain_ExtractsCorrectly() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getJti()).isEqualTo(TEST_JTI);
        }

        @Test
        @DisplayName("ExpiresAt 추출이 올바르게 동작한다")
        void extractExpiresAt_ValidDomain_ExtractsCorrectly() {
            // given
            final BlacklistedToken domain = BlacklistedToken.create(
                    Jti.of(TEST_JTI),
                    ExpiresAt.fromEpochSeconds(TEST_EXPIRES_AT_EPOCH_SECONDS),
                    BlacklistReason.LOGOUT
            );

            // when
            final BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExpiresAt()).isEqualTo(TEST_EXPIRES_AT_EPOCH_SECONDS * 1000);
        }
    }
}
