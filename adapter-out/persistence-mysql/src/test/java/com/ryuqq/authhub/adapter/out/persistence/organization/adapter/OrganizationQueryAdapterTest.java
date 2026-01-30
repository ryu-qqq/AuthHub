package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
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
 * OrganizationQueryAdapter 단위 테스트
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
@DisplayName("OrganizationQueryAdapter 단위 테스트")
class OrganizationQueryAdapterTest {

    @Mock private OrganizationQueryDslRepository repository;

    @Mock private OrganizationJpaEntityMapper mapper;

    private OrganizationQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();
            OrganizationJpaEntity entity = OrganizationJpaEntityFixture.create();
            Organization expectedDomain = OrganizationFixture.create();

            given(repository.findByOrganizationId(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Organization> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(repository.findByOrganizationId(id.value())).willReturn(Optional.empty());

            // when
            Optional<Organization> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("OrganizationId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(repository.findByOrganizationId(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByOrganizationId(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(repository.existsByOrganizationId(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(repository.existsByOrganizationId(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Test Org");

            given(repository.existsByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("NonExistent Org");

            given(repository.existsByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("VO에서 value 추출하여 Repository 호출")
        void shouldExtractValues_AndCallRepository() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Test Org");

            given(repository.existsByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(false);

            // when
            sut.existsByTenantIdAndName(tenantId, name);

            // then
            then(repository).should().existsByTenantIdAndName(tenantId.value(), name.value());
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            OrganizationSearchCriteria criteria = createTestCriteria();
            OrganizationJpaEntity entity1 = OrganizationJpaEntityFixture.createWithName("Org 1");
            OrganizationJpaEntity entity2 = OrganizationJpaEntityFixture.createWithName("Org 2");
            Organization domain1 = OrganizationFixture.createWithName("Org 1");
            Organization domain2 = OrganizationFixture.createWithName("Org 2");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Organization> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            OrganizationSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Organization> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnCount_FromRepository() {
            // given
            OrganizationSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(15L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(15L);
        }

        @Test
        @DisplayName("결과가 없으면 0 반환")
        void shouldReturnZero_WhenNoResults() {
            // given
            OrganizationSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ==================== Helper Methods ====================

    private OrganizationSearchCriteria createTestCriteria() {
        return OrganizationSearchCriteria.ofSimple(
                List.of(OrganizationFixture.defaultTenantId()),
                null,
                null,
                DateRange.of(null, null),
                0,
                10);
    }
}
