package com.ryuqq.authhub.adapter.out.persistence.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (null deletedAt, 다양한 상태) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationJpaEntityMapper 단위 테스트")
class OrganizationJpaEntityMapperTest {

    private OrganizationJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            Organization domain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getOrganizationId()).isEqualTo(domain.organizationIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            Organization activeDomain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            Organization deletedDomain = OrganizationFixture.createDeleted();

            // when
            OrganizationJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("비활성 Domain이 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            Organization inactiveDomain = OrganizationFixture.createInactive();

            // when
            OrganizationJpaEntity entity = sut.toEntity(inactiveDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            OrganizationJpaEntity entity = OrganizationJpaEntityFixture.create();

            // when
            Organization domain = sut.toDomain(entity);

            // then
            assertThat(domain.organizationIdValue()).isEqualTo(entity.getOrganizationId());
            assertThat(domain.tenantIdValue()).isEqualTo(entity.getTenantId());
            assertThat(domain.nameValue()).isEqualTo(entity.getName());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity는 DeletionStatus가 active로 매핑됨")
        void shouldMapToDeletionStatusActive_WhenEntityIsActive() {
            // given
            OrganizationJpaEntity activeEntity = OrganizationJpaEntityFixture.create();

            // when
            Organization domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus가 deleted로 매핑됨")
        void shouldMapToDeletionStatusDeleted_WhenEntityIsDeleted() {
            // given
            OrganizationJpaEntity deletedEntity = OrganizationJpaEntityFixture.createDeleted();

            // when
            Organization domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("비활성 Entity가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            OrganizationJpaEntity inactiveEntity = OrganizationJpaEntityFixture.createInactive();

            // when
            Organization domain = sut.toDomain(inactiveEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            Organization originalDomain = OrganizationFixture.create();

            // when
            OrganizationJpaEntity entity = sut.toEntity(originalDomain);
            Organization reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.organizationIdValue())
                    .isEqualTo(originalDomain.organizationIdValue());
            assertThat(reconstitutedDomain.tenantIdValue())
                    .isEqualTo(originalDomain.tenantIdValue());
            assertThat(reconstitutedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(reconstitutedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(reconstitutedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }

        @Test
        @DisplayName("삭제된 Domain도 양방향 변환 시 데이터 보존")
        void shouldPreserveDeletedData_OnRoundTrip() {
            // given
            Organization deletedDomain = OrganizationFixture.createDeleted();

            // when
            OrganizationJpaEntity entity = sut.toEntity(deletedDomain);
            Organization reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.isDeleted()).isEqualTo(deletedDomain.isDeleted());
            assertThat(reconstitutedDomain.getDeletionStatus().deletedAt())
                    .isEqualTo(deletedDomain.getDeletionStatus().deletedAt());
        }
    }
}
