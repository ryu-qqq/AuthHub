package com.ryuqq.authhub.adapter.out.persistence.permission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (deletedAt, 다양한 유형) 처리 검증
 *   <li>Global Only 설계 (tenantId 없음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionJpaEntityMapper 단위 테스트")
class PermissionJpaEntityMapperTest {

    private PermissionJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            Permission domain = PermissionFixture.create();

            // when
            PermissionJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getPermissionId()).isEqualTo(domain.permissionIdValue());
            assertThat(entity.getPermissionKey()).isEqualTo(domain.permissionKeyValue());
            assertThat(entity.getResource()).isEqualTo(domain.resourceValue());
            assertThat(entity.getAction()).isEqualTo(domain.actionValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.getType()).isEqualTo(domain.getType());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            Permission activeDomain = PermissionFixture.create();

            // when
            PermissionJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            Permission deletedDomain = PermissionFixture.createDeleted();

            // when
            PermissionJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("시스템 권한 유형이 올바르게 매핑됨")
        void shouldMapSystemType_Correctly() {
            // given
            Permission systemPermission = PermissionFixture.createSystemPermission();

            // when
            PermissionJpaEntity entity = sut.toEntity(systemPermission);

            // then
            assertThat(entity.getType()).isEqualTo(PermissionType.SYSTEM);
        }

        @Test
        @DisplayName("커스텀 권한 유형이 올바르게 매핑됨")
        void shouldMapCustomType_Correctly() {
            // given
            Permission customPermission = PermissionFixture.createCustomPermission();

            // when
            PermissionJpaEntity entity = sut.toEntity(customPermission);

            // then
            assertThat(entity.getType()).isEqualTo(PermissionType.CUSTOM);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();

            // when
            Permission domain = sut.toDomain(entity);

            // then
            assertThat(domain.permissionIdValue()).isEqualTo(entity.getPermissionId());
            assertThat(domain.permissionKeyValue()).isEqualTo(entity.getPermissionKey());
            assertThat(domain.resourceValue()).isEqualTo(entity.getResource());
            assertThat(domain.actionValue()).isEqualTo(entity.getAction());
            assertThat(domain.descriptionValue()).isEqualTo(entity.getDescription());
            assertThat(domain.getType()).isEqualTo(entity.getType());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity는 DeletionStatus가 active로 매핑됨")
        void shouldMapToDeletionStatusActive_WhenEntityIsActive() {
            // given
            PermissionJpaEntity activeEntity = PermissionJpaEntityFixture.create();

            // when
            Permission domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus가 deleted로 매핑됨")
        void shouldMapToDeletionStatusDeleted_WhenEntityIsDeleted() {
            // given
            PermissionJpaEntity deletedEntity = PermissionJpaEntityFixture.createDeleted();

            // when
            Permission domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("시스템 권한 유형이 올바르게 매핑됨")
        void shouldMapSystemType_Correctly() {
            // given
            PermissionJpaEntity systemEntity = PermissionJpaEntityFixture.createSystemPermission();

            // when
            Permission domain = sut.toDomain(systemEntity);

            // then
            assertThat(domain.getType()).isEqualTo(PermissionType.SYSTEM);
        }

        @Test
        @DisplayName("재구성된 Domain은 isNew()가 false")
        void shouldSetIsNewToFalse_WhenReconstituted() {
            // given
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();

            // when
            Permission domain = sut.toDomain(entity);

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
            Permission originalDomain = PermissionFixture.create();

            // when
            PermissionJpaEntity entity = sut.toEntity(originalDomain);
            Permission reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.permissionIdValue())
                    .isEqualTo(originalDomain.permissionIdValue());
            assertThat(reconstitutedDomain.permissionKeyValue())
                    .isEqualTo(originalDomain.permissionKeyValue());
            assertThat(reconstitutedDomain.resourceValue())
                    .isEqualTo(originalDomain.resourceValue());
            assertThat(reconstitutedDomain.actionValue()).isEqualTo(originalDomain.actionValue());
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
            Permission deletedDomain = PermissionFixture.createDeleted();

            // when
            PermissionJpaEntity entity = sut.toEntity(deletedDomain);
            Permission reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.isDeleted()).isEqualTo(deletedDomain.isDeleted());
            assertThat(reconstitutedDomain.getDeletionStatus().deletedAt())
                    .isEqualTo(deletedDomain.getDeletionStatus().deletedAt());
        }
    }
}
