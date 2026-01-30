package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.fixture.RoleJpaEntityFixture;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (null tenantId, deletedAt, 다양한 유형) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleJpaEntityMapper 단위 테스트")
class RoleJpaEntityMapperTest {

    private RoleJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new RoleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            Role domain = RoleFixture.create();

            // when
            RoleJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getRoleId()).isEqualTo(domain.roleIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayNameValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.getType()).isEqualTo(domain.getType());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            Role activeDomain = RoleFixture.create();

            // when
            RoleJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            Role deletedDomain = RoleFixture.createDeleted();

            // when
            RoleJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("Global 역할은 tenantId가 null로 매핑됨")
        void shouldMapTenantIdToNull_WhenDomainIsGlobal() {
            // given
            Role globalRole = RoleFixture.create(); // Global Role

            // when
            RoleJpaEntity entity = sut.toEntity(globalRole);

            // then
            assertThat(entity.getTenantId()).isNull();
        }

        @Test
        @DisplayName("테넌트 역할은 tenantId가 올바르게 매핑됨")
        void shouldMapTenantId_WhenDomainHasTenant() {
            // given
            Role tenantRole = RoleFixture.createTenantRole();

            // when
            RoleJpaEntity entity = sut.toEntity(tenantRole);

            // then
            assertThat(entity.getTenantId()).isNotNull();
        }

        @Test
        @DisplayName("시스템 역할 유형이 올바르게 매핑됨")
        void shouldMapSystemType_Correctly() {
            // given
            Role systemRole = RoleFixture.createSystemRole();

            // when
            RoleJpaEntity entity = sut.toEntity(systemRole);

            // then
            assertThat(entity.getType()).isEqualTo(RoleType.SYSTEM);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            RoleJpaEntity entity = RoleJpaEntityFixture.create();

            // when
            Role domain = sut.toDomain(entity);

            // then
            assertThat(domain.roleIdValue()).isEqualTo(entity.getRoleId());
            assertThat(domain.tenantIdValue()).isEqualTo(entity.getTenantId());
            assertThat(domain.nameValue()).isEqualTo(entity.getName());
            assertThat(domain.displayNameValue()).isEqualTo(entity.getDisplayName());
            assertThat(domain.descriptionValue()).isEqualTo(entity.getDescription());
            assertThat(domain.getType()).isEqualTo(entity.getType());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity는 DeletionStatus가 active로 매핑됨")
        void shouldMapToDeletionStatusActive_WhenEntityIsActive() {
            // given
            RoleJpaEntity activeEntity = RoleJpaEntityFixture.create();

            // when
            Role domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus가 deleted로 매핑됨")
        void shouldMapToDeletionStatusDeleted_WhenEntityIsDeleted() {
            // given
            RoleJpaEntity deletedEntity = RoleJpaEntityFixture.createDeleted();

            // when
            Role domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Global Entity는 tenantId가 null로 매핑됨")
        void shouldMapTenantIdToNull_WhenEntityIsGlobal() {
            // given
            RoleJpaEntity globalEntity = RoleJpaEntityFixture.create(); // Global

            // when
            Role domain = sut.toDomain(globalEntity);

            // then
            assertThat(domain.tenantIdValue()).isNull();
        }

        @Test
        @DisplayName("테넌트 Entity는 tenantId가 올바르게 매핑됨")
        void shouldMapTenantId_WhenEntityHasTenant() {
            // given
            RoleJpaEntity tenantEntity = RoleJpaEntityFixture.createWithTenant();

            // when
            Role domain = sut.toDomain(tenantEntity);

            // then
            assertThat(domain.tenantIdValue()).isNotNull();
        }

        @Test
        @DisplayName("시스템 역할 유형이 올바르게 매핑됨")
        void shouldMapSystemType_Correctly() {
            // given
            RoleJpaEntity systemEntity = RoleJpaEntityFixture.createSystemRole();

            // when
            Role domain = sut.toDomain(systemEntity);

            // then
            assertThat(domain.getType()).isEqualTo(RoleType.SYSTEM);
        }

        @Test
        @DisplayName("재구성된 Domain은 isNew()가 false")
        void shouldSetIsNewToFalse_WhenReconstituted() {
            // given
            RoleJpaEntity entity = RoleJpaEntityFixture.create();

            // when
            Role domain = sut.toDomain(entity);

            // then
            assertThat(domain.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            Role originalDomain = RoleFixture.create();

            // when
            RoleJpaEntity entity = sut.toEntity(originalDomain);
            Role reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.roleIdValue()).isEqualTo(originalDomain.roleIdValue());
            assertThat(reconstitutedDomain.tenantIdValue())
                    .isEqualTo(originalDomain.tenantIdValue());
            assertThat(reconstitutedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(reconstitutedDomain.displayNameValue())
                    .isEqualTo(originalDomain.displayNameValue());
            assertThat(reconstitutedDomain.descriptionValue())
                    .isEqualTo(originalDomain.descriptionValue());
            assertThat(reconstitutedDomain.getType()).isEqualTo(originalDomain.getType());
            assertThat(reconstitutedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(reconstitutedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }

        @Test
        @DisplayName("삭제된 Domain도 양방향 변환 시 데이터 보존")
        void shouldPreserveDeletedData_OnRoundTrip() {
            // given
            Role deletedDomain = RoleFixture.createDeleted();

            // when
            RoleJpaEntity entity = sut.toEntity(deletedDomain);
            Role reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.isDeleted()).isEqualTo(deletedDomain.isDeleted());
            assertThat(reconstitutedDomain.getDeletionStatus().deletedAt())
                    .isEqualTo(deletedDomain.getDeletionStatus().deletedAt());
        }
    }
}
