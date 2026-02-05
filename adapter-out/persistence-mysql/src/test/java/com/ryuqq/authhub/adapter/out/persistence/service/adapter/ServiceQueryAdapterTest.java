package com.ryuqq.authhub.adapter.out.persistence.service.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.service.mapper.ServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
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
 * ServiceQueryAdapter 단위 테스트
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
@DisplayName("ServiceQueryAdapter 단위 테스트")
class ServiceQueryAdapterTest {

    @Mock private ServiceQueryDslRepository repository;

    @Mock private ServiceJpaEntityMapper mapper;

    private ServiceQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            Service expectedDomain = ServiceFixture.create();

            given(repository.findByServiceId(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Service> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(repository.findByServiceId(id.value())).willReturn(Optional.empty());

            // when
            Optional<Service> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("ServiceId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(repository.findByServiceId(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByServiceId(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(repository.existsByServiceId(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(repository.existsByServiceId(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCode 메서드")
    class FindByCode {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            Service expectedDomain = ServiceFixture.create();

            given(repository.findByServiceCode(code.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Service> result = sut.findByCode(code);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(repository.findByServiceCode(code.value())).willReturn(Optional.empty());

            // when
            Optional<Service> result = sut.findByCode(code);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("ServiceCode에서 value 추출하여 Repository 호출")
        void shouldExtractCodeValue_AndCallRepository() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(repository.findByServiceCode(code.value())).willReturn(Optional.empty());

            // when
            sut.findByCode(code);

            // then
            then(repository).should().findByServiceCode(code.value());
        }
    }

    @Nested
    @DisplayName("existsByCode 메서드")
    class ExistsByCode {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(repository.existsByServiceCode(code.value())).willReturn(true);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(repository.existsByServiceCode(code.value())).willReturn(false);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("ServiceCode에서 value 추출하여 Repository 호출")
        void shouldExtractCodeValue_AndCallRepository() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(repository.existsByServiceCode(code.value())).willReturn(false);

            // when
            sut.existsByCode(code);

            // then
            then(repository).should().existsByServiceCode(code.value());
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            ServiceSearchCriteria criteria = createTestCriteria();
            ServiceJpaEntity entity1 = ServiceJpaEntityFixture.createWithName("서비스 1");
            ServiceJpaEntity entity2 = ServiceJpaEntityFixture.createWithName("서비스 2");
            Service domain1 = ServiceFixture.createWithName("서비스 1");
            Service domain2 = ServiceFixture.createWithName("서비스 2");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Service> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            ServiceSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Service> result = sut.findAllByCriteria(criteria);

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
            ServiceSearchCriteria criteria = createTestCriteria();

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
            ServiceSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("findAllActive 메서드")
    class FindAllActive {

        @Test
        @DisplayName("성공: 활성 상태 Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAllActive_ThenReturnDomainList() {
            // given
            ServiceJpaEntity entity1 = ServiceJpaEntityFixture.createWithName("활성 서비스 1");
            ServiceJpaEntity entity2 = ServiceJpaEntityFixture.createWithName("활성 서비스 2");
            Service domain1 = ServiceFixture.createWithName("활성 서비스 1");
            Service domain2 = ServiceFixture.createWithName("활성 서비스 2");

            given(repository.findAllActive()).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Service> result = sut.findAllActive();

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("활성 상태 서비스가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoActiveServices() {
            // given
            given(repository.findAllActive()).willReturn(List.of());

            // when
            List<Service> result = sut.findAllActive();

            // then
            assertThat(result).isEmpty();
        }
    }

    // ==================== Helper Methods ====================

    private ServiceSearchCriteria createTestCriteria() {
        return ServiceSearchCriteria.of(
                null,
                ServiceSearchField.SERVICE_CODE,
                null,
                DateRange.of(null, null),
                ServiceSortKey.CREATED_AT,
                SortDirection.ASC,
                PageRequest.of(0, 10));
    }
}
