package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserJpaEntityMapper 단위 테스트")
class UserJpaEntityMapperTest {

    private UserJpaEntityMapper mapper;

    private static final UUID USER_UUID = UUID.fromString("01941234-5678-7000-8000-000000000001");
    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID ORG_UUID = UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new UserJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            User domain = UserFixture.create();

            // when
            UserJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getUserId()).isEqualTo(domain.userIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getOrganizationId()).isEqualTo(domain.organizationIdValue());
            assertThat(entity.getIdentifier()).isEqualTo(domain.getIdentifier());
            assertThat(entity.getHashedPassword()).isEqualTo(domain.getHashedPassword());
            assertThat(entity.getStatus()).isEqualTo(domain.getUserStatus());
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            User domain = UserFixture.create();

            // when
            UserJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
        }

        @Test
        @DisplayName("신규 Domain도 Entity로 변환된다")
        void shouldConvertNewDomainToEntity() {
            // given
            User domain = UserFixture.createNew();

            // when
            UserJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getUserId()).isNotNull();
            assertThat(entity.getIdentifier()).isEqualTo("user@example.com");
            assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "test@example.com",
                            "010-1234-5678",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            User domain = mapper.toDomain(entity);

            // then
            assertThat(domain.userIdValue()).isEqualTo(USER_UUID);
            assertThat(domain.tenantIdValue()).isEqualTo(TENANT_UUID);
            assertThat(domain.organizationIdValue()).isEqualTo(ORG_UUID);
            assertThat(domain.getIdentifier()).isEqualTo("test@example.com");
            assertThat(domain.getPhoneNumber()).isEqualTo("010-1234-5678");
            assertThat(domain.getHashedPassword()).isEqualTo("hashed_password");
            assertThat(domain.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "test@example.com",
                            "010-1234-5678",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            User domain = mapper.toDomain(entity);

            // then
            assertThat(domain.getCreatedAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.getUpdatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다양한 상태의 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithDifferentStatusToDomain() {
            // given
            UserJpaEntity inactiveEntity =
                    UserJpaEntity.of(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "inactive@example.com",
                            "010-1111-1111",
                            "hashed_password",
                            UserStatus.INACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            UserJpaEntity lockedEntity =
                    UserJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-000000000002"),
                            TENANT_UUID,
                            ORG_UUID,
                            "locked@example.com",
                            "010-2222-2222",
                            "hashed_password",
                            UserStatus.LOCKED,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            User inactiveDomain = mapper.toDomain(inactiveEntity);
            User lockedDomain = mapper.toDomain(lockedEntity);

            // then
            assertThat(inactiveDomain.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(lockedDomain.getUserStatus()).isEqualTo(UserStatus.LOCKED);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            User originalDomain = UserFixture.create();

            // when
            UserJpaEntity entity = mapper.toEntity(originalDomain);
            UserJpaEntity entityWithId =
                    UserJpaEntity.of(
                            originalDomain.userIdValue(),
                            entity.getTenantId(),
                            entity.getOrganizationId(),
                            entity.getIdentifier(),
                            entity.getPhoneNumber(),
                            entity.getHashedPassword(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            User convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.userIdValue()).isEqualTo(originalDomain.userIdValue());
            assertThat(convertedDomain.tenantIdValue()).isEqualTo(originalDomain.tenantIdValue());
            assertThat(convertedDomain.organizationIdValue())
                    .isEqualTo(originalDomain.organizationIdValue());
            assertThat(convertedDomain.getIdentifier()).isEqualTo(originalDomain.getIdentifier());
            assertThat(convertedDomain.getHashedPassword())
                    .isEqualTo(originalDomain.getHashedPassword());
            assertThat(convertedDomain.getUserStatus()).isEqualTo(originalDomain.getUserStatus());
        }
    }
}
