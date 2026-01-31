package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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
 * TenantQueryAdapter 단위 테스트
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
@DisplayName("TenantQueryAdapter 단위 테스트")
class TenantQueryAdapterTest {

    @Mock private TenantQueryDslRepository repository;

    @Mock private TenantJpaEntityMapper mapper;

    private TenantQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new TenantQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            TenantId id = TenantFixture.defaultId();
            TenantJpaEntity entity = TenantJpaEntityFixture.create();
            Tenant expectedDomain = TenantFixture.create();

            given(repository.findByTenantId(id.value().toString())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Tenant> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(repository.findByTenantId(id.value().toString())).willReturn(Optional.empty());

            // when
            Optional<Tenant> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("TenantId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(repository.findByTenantId(id.value().toString())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByTenantId(id.value().toString());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(repository.existsByTenantId(id.value().toString())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(repository.existsByTenantId(id.value().toString())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantName name = TenantName.of("Test Tenant");

            given(repository.existsByName(name.value())).willReturn(true);

            // when
            boolean result = sut.existsByName(name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantName name = TenantName.of("NonExistent Tenant");

            given(repository.existsByName(name.value())).willReturn(false);

            // when
            boolean result = sut.existsByName(name);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("VO에서 value 추출하여 Repository 호출")
        void shouldExtractValue_AndCallRepository() {
            // given
            TenantName name = TenantName.of("Test Tenant");

            given(repository.existsByName(name.value())).willReturn(false);

            // when
            sut.existsByName(name);

            // then
            then(repository).should().existsByName(name.value());
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 메서드")
    class ExistsByNameAndIdNot {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantName name = TenantName.of("Test Tenant");
            TenantId excludeId = TenantFixture.defaultId();

            given(repository.existsByNameAndIdNot(name.value(), excludeId.value().toString()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantName name = TenantName.of("Test Tenant");
            TenantId excludeId = TenantFixture.defaultId();

            given(repository.existsByNameAndIdNot(name.value(), excludeId.value().toString()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            TenantSearchCriteria criteria = createTestCriteria();
            TenantJpaEntity entity1 = TenantJpaEntityFixture.createWithName("Tenant 1");
            TenantJpaEntity entity2 = TenantJpaEntityFixture.createWithName("Tenant 2");
            Tenant domain1 = TenantFixture.createWithName("Tenant 1");
            Tenant domain2 = TenantFixture.createWithName("Tenant 2");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Tenant> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            TenantSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Tenant> result = sut.findAllByCriteria(criteria);

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
            TenantSearchCriteria criteria = createTestCriteria();

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
            TenantSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ==================== Helper Methods ====================

    private TenantSearchCriteria createTestCriteria() {
        return TenantSearchCriteria.ofSimple(null, null, DateRange.of(null, null), 0, 10);
    }
}
