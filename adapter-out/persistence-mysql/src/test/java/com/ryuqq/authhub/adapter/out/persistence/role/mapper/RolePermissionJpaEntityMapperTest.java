package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionJpaEntityMapper 단위 테스트")
class RolePermissionJpaEntityMapperTest {

    private RolePermissionJpaEntityMapper mapper;

    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final UUID PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789222");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new RolePermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            RolePermission domain =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID), PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getRoleId()).isEqualTo(ROLE_UUID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_UUID);
            assertThat(entity.getGrantedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다른 권한도 Entity로 변환된다")
        void shouldConvertAnotherPermissionToEntity() {
            // given
            UUID anotherPermissionId = UUID.fromString("01941234-5678-7000-8000-123456789333");
            RolePermission domain =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID),
                            PermissionId.of(anotherPermissionId),
                            Instant.now());

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getRoleId()).isEqualTo(ROLE_UUID);
            assertThat(entity.getPermissionId()).isEqualTo(anotherPermissionId);
            assertThat(entity.getGrantedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.of(1L, ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);

            // when
            RolePermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.roleIdValue()).isEqualTo(ROLE_UUID);
            assertThat(domain.permissionIdValue()).isEqualTo(PERMISSION_UUID);
            assertThat(domain.getGrantedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            RolePermission originalDomain =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID), PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(originalDomain);
            RolePermissionJpaEntity entityWithId =
                    RolePermissionJpaEntity.of(
                            1L,
                            entity.getRoleId(),
                            entity.getPermissionId(),
                            entity.getGrantedAt());
            RolePermission convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.roleIdValue()).isEqualTo(originalDomain.roleIdValue());
            assertThat(convertedDomain.permissionIdValue())
                    .isEqualTo(originalDomain.permissionIdValue());
            assertThat(convertedDomain.getGrantedAt()).isEqualTo(originalDomain.getGrantedAt());
        }
    }
}
