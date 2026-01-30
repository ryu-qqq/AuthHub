package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantJpaEntityMapper 단위 테스트
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
@DisplayName("TenantJpaEntityMapper 단위 테스트")
class TenantJpaEntityMapperTest {

    private TenantJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new TenantJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            Tenant domain = TenantFixture.create();

            // when
            TenantJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue().toString());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            Tenant activeDomain = TenantFixture.create();

            // when
            TenantJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            Tenant deletedDomain = TenantFixture.createDeleted();

            // when
            TenantJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("비활성 Domain이 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            Tenant inactiveDomain = TenantFixture.createInactive();

            // when
            TenantJpaEntity entity = sut.toEntity(inactiveDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            TenantJpaEntity entity = TenantJpaEntityFixture.create();

            // when
            Tenant domain = sut.toDomain(entity);

            // then
            assertThat(domain.tenantIdValue().toString()).isEqualTo(entity.getTenantId());
            assertThat(domain.nameValue()).isEqualTo(entity.getName());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity는 DeletionStatus가 active로 매핑됨")
        void shouldMapToDeletionStatusActive_WhenEntityIsActive() {
            // given
            TenantJpaEntity activeEntity = TenantJpaEntityFixture.create();

            // when
            Tenant domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus가 deleted로 매핑됨")
        void shouldMapToDeletionStatusDeleted_WhenEntityIsDeleted() {
            // given
            TenantJpaEntity deletedEntity = TenantJpaEntityFixture.createDeleted();

            // when
            Tenant domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("비활성 Entity가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            TenantJpaEntity inactiveEntity = TenantJpaEntityFixture.createInactive();

            // when
            Tenant domain = sut.toDomain(inactiveEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(TenantStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            Tenant originalDomain = TenantFixture.create();

            // when
            TenantJpaEntity entity = sut.toEntity(originalDomain);
            Tenant reconstitutedDomain = sut.toDomain(entity);

            // then
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
            Tenant deletedDomain = TenantFixture.createDeleted();

            // when
            TenantJpaEntity entity = sut.toEntity(deletedDomain);
            Tenant reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.isDeleted()).isEqualTo(deletedDomain.isDeleted());
            assertThat(reconstitutedDomain.getDeletionStatus().deletedAt())
                    .isEqualTo(deletedDomain.getDeletionStatus().deletedAt());
        }
    }
}
