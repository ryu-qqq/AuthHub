package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
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
 * RoleJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleJpaEntityMapper 단위 테스트")
class RoleJpaEntityMapperTest {

    private RoleJpaEntityMapper mapper;

    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new RoleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            Role domain = RoleFixture.create();

            // when
            RoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getRoleId()).isEqualTo(domain.roleIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.getScope()).isEqualTo(domain.getScope());
            assertThat(entity.getType()).isEqualTo(domain.getType());
            assertThat(entity.isDeleted()).isEqualTo(domain.isDeleted());
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            Role domain = RoleFixture.create();

            // when
            RoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
        }

        @Test
        @DisplayName("시스템 글로벌 역할도 Entity로 변환된다")
        void shouldConvertSystemGlobalRoleToEntity() {
            // given
            Role domain = RoleFixture.createSystemGlobal();

            // when
            RoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getTenantId()).isNull();
            assertThat(entity.getScope()).isEqualTo(RoleScope.GLOBAL);
            assertThat(entity.getType()).isEqualTo(RoleType.SYSTEM);
        }

        @Test
        @DisplayName("신규 Domain도 Entity로 변환된다")
        void shouldConvertNewDomainToEntity() {
            // given
            Role domain = RoleFixture.createNew();

            // when
            RoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo("NEW_ROLE");
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            "Test role description",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(entity);

            // then
            assertThat(domain.roleIdValue()).isEqualTo(ROLE_UUID);
            assertThat(domain.tenantIdValue()).isEqualTo(TENANT_UUID);
            assertThat(domain.nameValue()).isEqualTo("TEST_ROLE");
            assertThat(domain.descriptionValue()).isEqualTo("Test role description");
            assertThat(domain.getScope()).isEqualTo(RoleScope.ORGANIZATION);
            assertThat(domain.getType()).isEqualTo(RoleType.CUSTOM);
            assertThat(domain.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            "설명",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.updatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("null tenantId는 올바르게 변환된다 (GLOBAL 역할)")
        void shouldConvertNullTenantIdForGlobalRole() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            null,
                            "SUPER_ADMIN",
                            "System super admin role",
                            RoleScope.GLOBAL,
                            RoleType.SYSTEM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(entity);

            // then
            assertThat(domain.tenantIdValue()).isNull();
            assertThat(domain.getScope()).isEqualTo(RoleScope.GLOBAL);
        }

        @Test
        @DisplayName("null description은 빈 RoleDescription으로 변환된다")
        void shouldConvertNullDescriptionToEmpty() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            null,
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(entity);

            // then - RoleDescription.empty()는 빈 문자열("")을 가진 객체를 생성함 (validate()에서 null→"" 변환)
            assertThat(domain.descriptionValue()).isEmpty();
        }

        @Test
        @DisplayName("삭제된 역할도 올바르게 변환된다")
        void shouldConvertDeletedRole() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            TENANT_UUID,
                            "DELETED_ROLE",
                            "Deleted role",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            true,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("다양한 Scope의 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithDifferentScopes() {
            // given
            RoleJpaEntity tenantScope =
                    RoleJpaEntity.of(
                            1L,
                            ROLE_UUID,
                            TENANT_UUID,
                            "TENANT_ROLE",
                            "Tenant role",
                            RoleScope.TENANT,
                            RoleType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Role domain = mapper.toDomain(tenantScope);

            // then
            assertThat(domain.getScope()).isEqualTo(RoleScope.TENANT);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            Role originalDomain = RoleFixture.create();

            // when
            RoleJpaEntity entity = mapper.toEntity(originalDomain);
            RoleJpaEntity entityWithId =
                    RoleJpaEntity.of(
                            1L,
                            originalDomain.roleIdValue(),
                            entity.getTenantId(),
                            entity.getName(),
                            entity.getDescription(),
                            entity.getScope(),
                            entity.getType(),
                            entity.isDeleted(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            Role convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.roleIdValue()).isEqualTo(originalDomain.roleIdValue());
            assertThat(convertedDomain.tenantIdValue()).isEqualTo(originalDomain.tenantIdValue());
            assertThat(convertedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(convertedDomain.descriptionValue())
                    .isEqualTo(originalDomain.descriptionValue());
            assertThat(convertedDomain.getScope()).isEqualTo(originalDomain.getScope());
            assertThat(convertedDomain.getType()).isEqualTo(originalDomain.getType());
            assertThat(convertedDomain.isDeleted()).isEqualTo(originalDomain.isDeleted());
        }
    }
}
