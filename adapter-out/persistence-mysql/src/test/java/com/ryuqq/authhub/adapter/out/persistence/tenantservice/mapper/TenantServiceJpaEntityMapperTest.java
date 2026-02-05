package com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture.TenantServiceJpaEntityFixture;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (다양한 상태) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceJpaEntityMapper 단위 테스트")
class TenantServiceJpaEntityMapperTest {

    private TenantServiceJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            TenantService domain = TenantServiceFixture.create();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.tenantServiceIdValue());
            assertThat(entity.getTenantId()).isEqualTo(domain.tenantIdValue());
            assertThat(entity.getServiceId()).isEqualTo(domain.serviceIdValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
            assertThat(entity.getSubscribedAt()).isEqualTo(domain.subscribedAt());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 TenantService가 올바르게 매핑됨")
        void shouldMapActiveStatus_Correctly() {
            // given
            TenantService activeDomain = TenantServiceFixture.create();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 TenantService가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            TenantService inactiveDomain = TenantServiceFixture.createInactive();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(inactiveDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("일시 중지 TenantService가 올바르게 매핑됨")
        void shouldMapSuspendedStatus_Correctly() {
            // given
            TenantService suspendedDomain = TenantServiceFixture.createSuspended();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(suspendedDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.SUSPENDED);
        }

        @Test
        @DisplayName("신규 TenantService는 ID가 null로 매핑됨")
        void shouldMapIdToNull_WhenNewTenantService() {
            // given
            TenantService newDomain = TenantServiceFixture.createNew();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(newDomain);

            // then
            assertThat(entity.getId()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);

            // when
            TenantService domain = sut.toDomain(entity);

            // then
            assertThat(domain.tenantServiceIdValue()).isEqualTo(entity.getId());
            assertThat(domain.tenantIdValue()).isEqualTo(entity.getTenantId());
            assertThat(domain.serviceIdValue()).isEqualTo(entity.getServiceId());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
            assertThat(domain.subscribedAt()).isEqualTo(entity.getSubscribedAt());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity가 올바르게 매핑됨")
        void shouldMapActiveStatus_Correctly() {
            // given
            TenantServiceJpaEntity activeEntity = TenantServiceJpaEntityFixture.createWithId(1L);

            // when
            TenantService domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(TenantServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 Entity가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            TenantServiceJpaEntity inactiveEntity = TenantServiceJpaEntityFixture.createInactive();

            // when
            TenantService domain = sut.toDomain(inactiveEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(TenantServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("일시 중지 Entity가 올바르게 매핑됨")
        void shouldMapSuspendedStatus_Correctly() {
            // given
            TenantServiceJpaEntity suspendedEntity =
                    TenantServiceJpaEntityFixture.createSuspended();

            // when
            TenantService domain = sut.toDomain(suspendedEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(TenantServiceStatus.SUSPENDED);
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            TenantService originalDomain = TenantServiceFixture.create();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(originalDomain);
            TenantService reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.tenantServiceIdValue())
                    .isEqualTo(originalDomain.tenantServiceIdValue());
            assertThat(reconstitutedDomain.tenantIdValue())
                    .isEqualTo(originalDomain.tenantIdValue());
            assertThat(reconstitutedDomain.serviceIdValue())
                    .isEqualTo(originalDomain.serviceIdValue());
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(reconstitutedDomain.subscribedAt()).isEqualTo(originalDomain.subscribedAt());
            assertThat(reconstitutedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(reconstitutedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }

        @Test
        @DisplayName("비활성 TenantService도 양방향 변환 시 데이터 보존")
        void shouldPreserveInactiveData_OnRoundTrip() {
            // given
            TenantService inactiveDomain = TenantServiceFixture.createInactive();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(inactiveDomain);
            TenantService reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(inactiveDomain.getStatus());
        }

        @Test
        @DisplayName("일시 중지 TenantService도 양방향 변환 시 데이터 보존")
        void shouldPreserveSuspendedData_OnRoundTrip() {
            // given
            TenantService suspendedDomain = TenantServiceFixture.createSuspended();

            // when
            TenantServiceJpaEntity entity = sut.toEntity(suspendedDomain);
            TenantService reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(suspendedDomain.getStatus());
        }
    }
}
