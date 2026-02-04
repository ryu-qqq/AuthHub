package com.ryuqq.authhub.adapter.out.persistence.tenantservice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture.TenantServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper.TenantServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantServiceQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>QueryDslRepository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantServiceQueryAdapter 단위 테스트")
class TenantServiceQueryAdapterTest {

    @Mock private TenantServiceQueryDslRepository repository;

    @Mock private TenantServiceJpaEntityMapper mapper;

    private TenantServiceQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);
            TenantService expectedDomain = TenantServiceFixture.create();

            given(repository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<TenantService> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(repository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<TenantService> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("TenantServiceId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(repository.findById(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findById(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(repository.existsById(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(repository.existsById(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndServiceId 메서드")
    class ExistsByTenantIdAndServiceId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(repository.existsByTenantIdAndServiceId(tenantId.value(), serviceId.value()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByTenantIdAndServiceId(tenantId, serviceId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(repository.existsByTenantIdAndServiceId(tenantId.value(), serviceId.value()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByTenantIdAndServiceId(tenantId, serviceId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndServiceId 메서드")
    class FindByTenantIdAndServiceId {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.createWithId(1L);
            TenantService expectedDomain = TenantServiceFixture.create();

            given(repository.findByTenantIdAndServiceId(tenantId.value(), serviceId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<TenantService> result = sut.findByTenantIdAndServiceId(tenantId, serviceId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(repository.findByTenantIdAndServiceId(tenantId.value(), serviceId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<TenantService> result = sut.findByTenantIdAndServiceId(tenantId, serviceId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            TenantServiceSearchCriteria criteria = createTestCriteria();
            TenantServiceJpaEntity entity1 =
                    TenantServiceJpaEntityFixture.createWithTenant(
                            "01941234-5678-7000-8000-000000000001");
            TenantServiceJpaEntity entity2 =
                    TenantServiceJpaEntityFixture.createWithTenant(
                            "01941234-5678-7000-8000-000000000002");
            TenantService domain1 = TenantServiceFixture.create();
            TenantService domain2 = TenantServiceFixture.create();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<TenantService> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            TenantServiceSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<TenantService> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria 메서드")
    class CountByCriteria {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnCount_FromRepository() {
            // given
            TenantServiceSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(15L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(15L);
        }

        @Test
        @DisplayName("결과가 없으면 0 반환")
        void shouldReturnZero_WhenNoResults() {
            // given
            TenantServiceSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ==================== Helper Methods ====================

    private TenantServiceSearchCriteria createTestCriteria() {
        return TenantServiceSearchCriteria.of(
                null,
                null,
                null,
                DateRange.of(null, null),
                TenantServiceSortKey.SUBSCRIBED_AT,
                SortDirection.ASC,
                PageRequest.of(0, 10));
    }
}
