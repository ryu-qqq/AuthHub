package com.ryuqq.authhub.adapter.out.persistence.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (null description, 다양한 상태) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceJpaEntityMapper 단위 테스트")
class ServiceJpaEntityMapperTest {

    private ServiceJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            Service domain = ServiceFixture.create();

            // when
            ServiceJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getServiceId()).isEqualTo(domain.serviceIdValue());
            assertThat(entity.getServiceCode()).isEqualTo(domain.serviceCodeValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Service가 올바르게 매핑됨")
        void shouldMapActiveStatus_Correctly() {
            // given
            Service activeDomain = ServiceFixture.create();

            // when
            ServiceJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 Service가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            Service inactiveDomain = ServiceFixture.createInactive();

            // when
            ServiceJpaEntity entity = sut.toEntity(inactiveDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("설명이 없는 Service가 올바르게 매핑됨")
        void shouldMapEmptyDescription_Correctly() {
            // given
            Service domainWithoutDescription = ServiceFixture.createWithoutDescription();

            // when
            ServiceJpaEntity entity = sut.toEntity(domainWithoutDescription);

            // then
            assertThat(entity.getDescription()).isNull();
        }

        @Test
        @DisplayName("신규 Service는 ID가 null로 매핑됨")
        void shouldMapIdToNull_WhenNewService() {
            // given
            Service newDomain = ServiceFixture.createNew();

            // when
            ServiceJpaEntity entity = sut.toEntity(newDomain);

            // then
            assertThat(entity.getServiceId()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);

            // when
            Service domain = sut.toDomain(entity);

            // then
            assertThat(domain.serviceIdValue()).isEqualTo(entity.getServiceId());
            assertThat(domain.serviceCodeValue()).isEqualTo(entity.getServiceCode());
            assertThat(domain.nameValue()).isEqualTo(entity.getName());
            assertThat(domain.descriptionValue()).isEqualTo(entity.getDescription());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity가 올바르게 매핑됨")
        void shouldMapActiveStatus_Correctly() {
            // given
            ServiceJpaEntity activeEntity = ServiceJpaEntityFixture.createWithId(1L);

            // when
            Service domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 Entity가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            ServiceJpaEntity inactiveEntity = ServiceJpaEntityFixture.createInactive();

            // when
            Service domain = sut.toDomain(inactiveEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(ServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("설명이 없는 Entity가 올바르게 매핑됨")
        void shouldMapNullDescription_Correctly() {
            // given
            ServiceJpaEntity entityWithoutDescription =
                    ServiceJpaEntityFixture.createWithoutDescription();

            // when
            Service domain = sut.toDomain(entityWithoutDescription);

            // then
            assertThat(domain.descriptionValue()).isNull();
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            Service originalDomain = ServiceFixture.create();

            // when
            ServiceJpaEntity entity = sut.toEntity(originalDomain);
            Service reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.serviceIdValue())
                    .isEqualTo(originalDomain.serviceIdValue());
            assertThat(reconstitutedDomain.serviceCodeValue())
                    .isEqualTo(originalDomain.serviceCodeValue());
            assertThat(reconstitutedDomain.nameValue()).isEqualTo(originalDomain.nameValue());
            assertThat(reconstitutedDomain.descriptionValue())
                    .isEqualTo(originalDomain.descriptionValue());
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(reconstitutedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(reconstitutedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }

        @Test
        @DisplayName("비활성 Service도 양방향 변환 시 데이터 보존")
        void shouldPreserveInactiveData_OnRoundTrip() {
            // given
            Service inactiveDomain = ServiceFixture.createInactive();

            // when
            ServiceJpaEntity entity = sut.toEntity(inactiveDomain);
            Service reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(inactiveDomain.getStatus());
        }

        @Test
        @DisplayName("설명 없는 Service도 양방향 변환 시 데이터 보존")
        void shouldPreserveEmptyDescription_OnRoundTrip() {
            // given
            Service domainWithoutDescription = ServiceFixture.createWithoutDescription();

            // when
            ServiceJpaEntity entity = sut.toEntity(domainWithoutDescription);
            Service reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.descriptionValue())
                    .isEqualTo(domainWithoutDescription.descriptionValue());
        }
    }
}
