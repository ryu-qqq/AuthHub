package com.ryuqq.authhub.application.tenantservice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenantservice.port.out.query.TenantServiceQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import java.util.Collections;
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
 * TenantServiceReadManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ReadManager는 QueryPort 위임 + 예외 변환 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>조회 실패 시 적절한 DomainException 발생 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantServiceReadManager 단위 테스트")
class TenantServiceReadManagerTest {

    @Mock private TenantServiceQueryPort queryPort;

    private TenantServiceReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: TenantService가 존재하면 해당 TenantService 반환")
        void shouldReturnTenantService_WhenExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();
            TenantService expected = TenantServiceFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            TenantService result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: TenantService가 존재하지 않으면 TenantServiceNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id))
                    .isInstanceOf(TenantServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByIdOptional 메서드")
    class FindByIdOptional {

        @Test
        @DisplayName("성공: TenantService가 존재하면 Optional로 반환")
        void shouldReturnOptional_WhenExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();
            TenantService expected = TenantServiceFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Optional<TenantService> result = sut.findByIdOptional(id);

            // then
            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmptyOptional_WhenNotExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<TenantService> result = sut.findByIdOptional(id);

            // then
            assertThat(result).isEmpty();
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

            given(queryPort.existsById(id)).willReturn(true);

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

            given(queryPort.existsById(id)).willReturn(false);

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

            given(queryPort.existsByTenantIdAndServiceId(tenantId, serviceId)).willReturn(true);

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

            given(queryPort.existsByTenantIdAndServiceId(tenantId, serviceId)).willReturn(false);

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
        @DisplayName("성공: TenantService가 존재하면 Optional로 반환")
        void shouldReturnOptional_WhenExists() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();
            TenantService expected = TenantServiceFixture.create();

            given(queryPort.findByTenantIdAndServiceId(tenantId, serviceId))
                    .willReturn(Optional.of(expected));

            // when
            Optional<TenantService> result = sut.findByTenantIdAndServiceId(tenantId, serviceId);

            // then
            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmptyOptional_WhenNotExists() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(queryPort.findByTenantIdAndServiceId(tenantId, serviceId))
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
        @DisplayName("성공: Criteria에 맞는 TenantService 목록 반환")
        void shouldReturnTenantServices_MatchingCriteria() {
            // given
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));
            List<TenantService> expected = List.of(TenantServiceFixture.create());

            given(queryPort.findAllByCriteria(criteria)).willReturn(expected);

            // when
            List<TenantService> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 TenantService가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoMatch() {
            // given
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));

            given(queryPort.findAllByCriteria(criteria)).willReturn(Collections.emptyList());

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
        @DisplayName("성공: Criteria에 맞는 TenantService 개수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));
            long expectedCount = 25L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }
}
