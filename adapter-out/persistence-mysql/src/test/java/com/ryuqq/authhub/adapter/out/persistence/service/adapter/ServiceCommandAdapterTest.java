package com.ryuqq.authhub.adapter.out.persistence.service.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.service.mapper.ServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceJpaRepository;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ServiceCommandAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>JpaRepository/Mapper를 Mock으로 대체
 *   <li>Domain → Entity 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceCommandAdapter 단위 테스트")
class ServiceCommandAdapterTest {

    @Mock private ServiceJpaRepository repository;

    @Mock private ServiceJpaEntityMapper mapper;

    private ServiceCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: 신규 Service 저장 후 ID 반환")
        void shouldPersistNewService_ThenReturnId() {
            // given
            Service domain = ServiceFixture.createNew();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createNew();
            ServiceJpaEntity savedEntity =
                    ServiceJpaEntity.of(
                            1L,
                            entity.getServiceCode(),
                            entity.getName(),
                            entity.getDescription(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(1L);
        }

        @Test
        @DisplayName("성공: 기존 Service 수정 후 ID 반환")
        void shouldPersistExistingService_ThenReturnId() {
            // given
            Service domain = ServiceFixture.create();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(ServiceFixture.defaultIdValue());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환하여 Repository 호출")
        void shouldConvertDomainToEntity_ThenCallRepository() {
            // given
            Service domain = ServiceFixture.create();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("저장된 Entity의 ID를 반환")
        void shouldReturnSavedEntityId() {
            // given
            Service domain = ServiceFixture.create();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            ServiceJpaEntity savedEntity =
                    ServiceJpaEntity.of(
                            999L,
                            entity.getServiceCode(),
                            entity.getName(),
                            entity.getDescription(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(any(ServiceJpaEntity.class))).willReturn(savedEntity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(999L);
        }
    }
}
