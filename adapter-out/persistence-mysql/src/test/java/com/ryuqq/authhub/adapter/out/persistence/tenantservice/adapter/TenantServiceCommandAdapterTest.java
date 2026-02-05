package com.ryuqq.authhub.adapter.out.persistence.tenantservice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture.TenantServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper.TenantServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceJpaRepository;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantServiceCommandAdapter 단위 테스트
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
@DisplayName("TenantServiceCommandAdapter 단위 테스트")
class TenantServiceCommandAdapterTest {

    @Mock private TenantServiceJpaRepository repository;

    @Mock private TenantServiceJpaEntityMapper mapper;

    private TenantServiceCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: 신규 TenantService 저장 후 ID 반환")
        void shouldPersistNewTenantService_ThenReturnId() {
            // given
            TenantService domain = TenantServiceFixture.createNew();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createNew();
            TenantServiceJpaEntity savedEntity =
                    TenantServiceJpaEntity.of(
                            1L,
                            entity.getTenantId(),
                            entity.getServiceId(),
                            entity.getStatus(),
                            entity.getSubscribedAt(),
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
        @DisplayName("성공: 기존 TenantService 수정 후 ID 반환")
        void shouldPersistExistingTenantService_ThenReturnId() {
            // given
            TenantService domain = TenantServiceFixture.create();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(TenantServiceFixture.defaultIdValue());
        }

        @Test
        @DisplayName("Domain을 Entity로 변환하여 Repository 호출")
        void shouldConvertDomainToEntity_ThenCallRepository() {
            // given
            TenantService domain = TenantServiceFixture.create();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);

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
            TenantService domain = TenantServiceFixture.create();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);
            TenantServiceJpaEntity savedEntity =
                    TenantServiceJpaEntity.of(
                            999L,
                            entity.getTenantId(),
                            entity.getServiceId(),
                            entity.getStatus(),
                            entity.getSubscribedAt(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(any(TenantServiceJpaEntity.class))).willReturn(savedEntity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(999L);
        }

        @Test
        @DisplayName("domain이 null이면 mapper 호출 시 예외 전파")
        void shouldPropagateException_WhenDomainIsNull() {
            given(mapper.toEntity(null)).willThrow(new NullPointerException("tenantService"));

            assertThatThrownBy(() -> sut.persist(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Repository.save() 예외가 그대로 전파됨")
        void shouldPropagateException_WhenRepositorySaveFails() {
            TenantService domain = TenantServiceFixture.create();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);
            RuntimeException repositoryException = new RuntimeException("DB constraint violation");

            given(mapper.toEntity(domain)).willReturn(entity);
            doThrow(repositoryException).when(repository).save(entity);

            assertThatThrownBy(() -> sut.persist(domain))
                    .isSameAs(repositoryException)
                    .hasMessageContaining("DB constraint violation");
        }
    }
}
