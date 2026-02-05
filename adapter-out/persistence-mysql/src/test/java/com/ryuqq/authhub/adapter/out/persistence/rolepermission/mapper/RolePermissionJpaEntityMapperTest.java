package com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.fixture.RolePermissionJpaEntityFixture;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 Entity ↔ Domain 양방향 변환 담당
 *   <li>Mock 불필요 (순수 변환 로직)
 *   <li>isNew()에 따른 create()/of() 분기 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionJpaEntityMapper 단위 테스트")
class RolePermissionJpaEntityMapperTest {

    private RolePermissionJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: 신규 Domain은 create() 사용")
        void shouldUseCreate_WhenDomainIsNew() {
            // given
            RolePermission newDomain = RolePermissionFixture.createNew();

            // when
            RolePermissionJpaEntity result = sut.toEntity(newDomain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRolePermissionId()).isNull();
            assertThat(result.getRoleId()).isEqualTo(newDomain.roleIdValue());
            assertThat(result.getPermissionId()).isEqualTo(newDomain.permissionIdValue());
            assertThat(result.getCreatedAt()).isEqualTo(newDomain.createdAt());
        }

        @Test
        @DisplayName("성공: 기존 Domain은 of() 사용")
        void shouldUseOf_WhenDomainIsNotNew() {
            // given
            RolePermission domain = RolePermissionFixture.create();

            // when
            RolePermissionJpaEntity result = sut.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRolePermissionId()).isEqualTo(domain.rolePermissionIdValue());
            assertThat(result.getRoleId()).isEqualTo(domain.roleIdValue());
            assertThat(result.getPermissionId()).isEqualTo(domain.permissionIdValue());
            assertThat(result.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("모든 필드가 올바르게 매핑됨")
        void shouldMapAllFields_Correctly() {
            // given
            RolePermission domain = RolePermissionFixture.createWithRoleAndPermission(2L, 3L);

            // when
            RolePermissionJpaEntity result = sut.toEntity(domain);

            // then
            assertThat(result.getRolePermissionId()).isEqualTo(domain.rolePermissionIdValue());
            assertThat(result.getRoleId()).isEqualTo(2L);
            assertThat(result.getPermissionId()).isEqualTo(3L);
            assertThat(result.getCreatedAt()).isEqualTo(RolePermissionFixture.fixedTime());
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            RolePermissionJpaEntity entity = RolePermissionJpaEntityFixture.create();

            // when
            RolePermission result = sut.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRolePermissionId().value())
                    .isEqualTo(entity.getRolePermissionId());
            assertThat(result.getRoleId().value()).isEqualTo(entity.getRoleId());
            assertThat(result.getPermissionId().value()).isEqualTo(entity.getPermissionId());
            assertThat(result.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("재구성된 Domain은 isNew()가 false")
        void shouldSetIsNewToFalse_WhenReconstituted() {
            // given
            RolePermissionJpaEntity entity = RolePermissionJpaEntityFixture.create();

            // when
            RolePermission result = sut.toDomain(entity);

            // then
            assertThat(result.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            RolePermission domain = RolePermissionFixture.create();

            // when
            RolePermissionJpaEntity entity = sut.toEntity(domain);
            RolePermission restored = sut.toDomain(entity);

            // then
            assertThat(restored.getRolePermissionId()).isEqualTo(domain.getRolePermissionId());
            assertThat(restored.getRoleId()).isEqualTo(domain.getRoleId());
            assertThat(restored.getPermissionId()).isEqualTo(domain.getPermissionId());
            assertThat(restored.createdAt()).isEqualTo(domain.createdAt());
        }
    }
}
