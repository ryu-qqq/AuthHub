package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantCommandAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>Repository/Mapper를 Mock으로 대체
 *   <li>위임 및 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandAdapter 단위 테스트")
class TenantCommandAdapterTest {

    @Mock private TenantJpaRepository repository;

    @Mock private TenantJpaEntityMapper mapper;

    private TenantCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new TenantCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 ID 반환")
        void shouldConvertAndPersist_ThenReturnId() {
            // given
            Tenant domain = TenantFixture.create();
            TenantJpaEntity entity = TenantJpaEntityFixture.create();
            String expectedId = TenantJpaEntityFixture.defaultTenantId();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            String result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Mapper를 통해 Domain → Entity 변환")
        void shouldUseMapper_ToConvertDomainToEntity() {
            // given
            Tenant domain = TenantFixture.create();
            TenantJpaEntity entity = TenantJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }

        @Test
        @DisplayName("Repository를 통해 Entity 저장")
        void shouldUseRepository_ToSaveEntity() {
            // given
            Tenant domain = TenantFixture.create();
            TenantJpaEntity entity = TenantJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("새 Domain 저장 시에도 동일한 흐름")
        void shouldFollowSameFlow_WhenPersistingNewDomain() {
            // given
            Tenant newDomain = TenantFixture.createNew();
            TenantJpaEntity entity = TenantJpaEntityFixture.create();

            given(mapper.toEntity(newDomain)).willReturn(entity);
            given(repository.save(any(TenantJpaEntity.class))).willReturn(entity);

            // when
            String result = sut.persist(newDomain);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(newDomain);
            then(repository).should().save(entity);
        }
    }
}
