package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * TenantJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantJpaEntityMapper 단위 테스트")
class TenantJpaEntityMapperTest {

    private TenantJpaEntityMapper mapper;

    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new TenantJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            Tenant domain = TenantFixture.create();

            // when
            TenantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            Tenant domain = TenantFixture.create();

            // when
            TenantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
        }

        @Test
        @DisplayName("신규 Domain(ID 있음)도 Entity로 변환된다")
        void shouldConvertNewDomainToEntity() {
            // given
            Tenant domain = TenantFixture.createNew();

            // when
            TenantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getTenantId()).isNotNull();
            assertThat(entity.getName()).isEqualTo("New Tenant");
            assertThat(entity.getStatus()).isEqualTo(TenantStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Tenant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.tenantIdValue()).isEqualTo(TENANT_UUID);
            assertThat(domain.nameValue()).isEqualTo("Test Tenant");
            assertThat(domain.getStatus()).isEqualTo(TenantStatus.ACTIVE);
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Tenant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.updatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다양한 상태의 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithDifferentStatusToDomain() {
            // given
            TenantJpaEntity inactiveEntity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Inactive",
                            TenantStatus.INACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            TenantJpaEntity deletedEntity =
                    TenantJpaEntity.of(
                            2L,
                            TENANT_UUID,
                            "Deleted",
                            TenantStatus.DELETED,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Tenant inactiveDomain = mapper.toDomain(inactiveEntity);
            Tenant deletedDomain = mapper.toDomain(deletedEntity);

            // then
            assertThat(inactiveDomain.getStatus()).isEqualTo(TenantStatus.INACTIVE);
            assertThat(deletedDomain.getStatus()).isEqualTo(TenantStatus.DELETED);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            Tenant originalDomain = TenantFixture.create();

            // when
            TenantJpaEntity entity = mapper.toEntity(originalDomain);
            // Entity에 ID가 설정되어야 reconstitute가 가능하므로, 새로운 Entity 생성
            TenantJpaEntity entityWithId =
                    TenantJpaEntity.of(
                            1L,
                            originalDomain.tenantIdValue(),
                            entity.getName(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            Tenant convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.tenantIdValue()).isEqualTo(originalDomain.tenantIdValue());
            assertThat(convertedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(convertedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(convertedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(convertedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }
    }
}
