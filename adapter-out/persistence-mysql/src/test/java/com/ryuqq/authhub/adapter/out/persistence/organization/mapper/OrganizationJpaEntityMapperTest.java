package com.ryuqq.authhub.adapter.out.persistence.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
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
 * OrganizationJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationJpaEntityMapper 단위 테스트")
class OrganizationJpaEntityMapperTest {

    private OrganizationJpaEntityMapper mapper;

    private static final UUID ORG_UUID = UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new OrganizationJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            Organization domain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOrganizationId()).isEqualTo(domain.organizationIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            Organization domain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
        }

        @Test
        @DisplayName("신규 Domain도 Entity로 변환된다")
        void shouldConvertNewDomainToEntity() {
            // given
            Organization domain = OrganizationFixture.createNew();

            // when
            OrganizationJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOrganizationId()).isNotNull();
            assertThat(entity.getName()).isEqualTo("New Organization");
            assertThat(entity.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Organization domain = mapper.toDomain(entity);

            // then
            assertThat(domain.organizationIdValue()).isEqualTo(ORG_UUID);
            assertThat(domain.tenantIdValue()).isEqualTo(TENANT_UUID);
            assertThat(domain.nameValue()).isEqualTo("Test Organization");
            assertThat(domain.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Organization domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.updatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다양한 상태의 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithDifferentStatusToDomain() {
            // given
            OrganizationJpaEntity inactiveEntity =
                    OrganizationJpaEntity.of(
                            ORG_UUID,
                            TENANT_UUID,
                            "Inactive Org",
                            OrganizationStatus.INACTIVE,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            OrganizationJpaEntity deletedEntity =
                    OrganizationJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-123456789df0"),
                            TENANT_UUID,
                            "Deleted Org",
                            OrganizationStatus.DELETED,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Organization inactiveDomain = mapper.toDomain(inactiveEntity);
            Organization deletedDomain = mapper.toDomain(deletedEntity);

            // then
            assertThat(inactiveDomain.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
            assertThat(deletedDomain.getStatus()).isEqualTo(OrganizationStatus.DELETED);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            Organization originalDomain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = mapper.toEntity(originalDomain);
            OrganizationJpaEntity entityWithId =
                    OrganizationJpaEntity.of(
                            originalDomain.organizationIdValue(),
                            entity.getTenantId(),
                            entity.getName(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            Organization convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.organizationIdValue())
                    .isEqualTo(originalDomain.organizationIdValue());
            assertThat(convertedDomain.tenantIdValue()).isEqualTo(originalDomain.tenantIdValue());
            assertThat(convertedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(convertedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(convertedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(convertedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }
    }
}
