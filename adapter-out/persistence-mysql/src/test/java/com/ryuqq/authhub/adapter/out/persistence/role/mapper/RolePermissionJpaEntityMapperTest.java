package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionJpaEntityMapper 단위 테스트")
class RolePermissionJpaEntityMapperTest {

    @Mock private UuidHolder uuidHolder;

    private RolePermissionJpaEntityMapper mapper;

    private static final UUID ROLE_PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789000");
    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final UUID PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789222");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new RolePermissionJpaEntityMapper(uuidHolder);
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

            given(uuidHolder.random()).willReturn(ROLE_PERMISSION_UUID);

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRolePermissionId()).isEqualTo(ROLE_PERMISSION_UUID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_UUID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_UUID);
            assertThat(entity.getGrantedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다른 권한도 Entity로 변환된다")
        void shouldConvertAnotherPermissionToEntity() {
            // given
            UUID anotherPermissionId = UUID.fromString("01941234-5678-7000-8000-123456789333");
            UUID generatedUuid = UUID.randomUUID();
            RolePermission domain =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID),
                            PermissionId.of(anotherPermissionId),
                            Instant.now());

            given(uuidHolder.random()).willReturn(generatedUuid);

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRolePermissionId()).isEqualTo(generatedUuid);
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
                    RolePermissionJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-123456789333"),
                            ROLE_UUID,
                            PERMISSION_UUID,
                            FIXED_INSTANT);

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

            given(uuidHolder.random()).willReturn(ROLE_PERMISSION_UUID);

            // when
            RolePermissionJpaEntity entity = mapper.toEntity(originalDomain);
            RolePermissionJpaEntity entityWithId =
                    RolePermissionJpaEntity.of(
                            entity.getRolePermissionId(),
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
