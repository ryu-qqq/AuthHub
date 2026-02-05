package com.ryuqq.authhub.application.service.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.service.port.out.query.ServiceQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
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
 * ServiceReadManager 단위 테스트
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
@DisplayName("ServiceReadManager 단위 테스트")
class ServiceReadManagerTest {

    @Mock private ServiceQueryPort queryPort;

    private ServiceReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 서비스가 존재하면 해당 서비스 반환")
        void shouldReturnService_WhenExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();
            Service expected = ServiceFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Service result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 서비스가 존재하지 않으면 ServiceNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id)).isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByIdOptional 메서드")
    class FindByIdOptional {

        @Test
        @DisplayName("존재하면 Optional에 감싸서 반환")
        void shouldReturnOptionalWithService_WhenExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();
            Service expected = ServiceFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Optional<Service> result = sut.findByIdOptional(id);

            // then
            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Service> result = sut.findByIdOptional(id);

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
            ServiceId id = ServiceFixture.defaultId();

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
            ServiceId id = ServiceFixture.defaultId();

            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByCode 메서드")
    class ExistsByCode {

        @Test
        @DisplayName("코드가 존재하면 true 반환")
        void shouldReturnTrue_WhenCodeExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(queryPort.existsByCode(code)).willReturn(true);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("코드가 존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenCodeNotExists() {
            // given
            ServiceCode code = ServiceCode.of("NON_EXISTENT");

            given(queryPort.existsByCode(code)).willReturn(false);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCode 메서드")
    class FindByCode {

        @Test
        @DisplayName("성공: 코드에 해당하는 서비스가 존재하면 해당 서비스 반환")
        void shouldReturnService_WhenCodeExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();
            Service expected = ServiceFixture.create();

            given(queryPort.findByCode(code)).willReturn(Optional.of(expected));

            // when
            Service result = sut.findByCode(code);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCode(code);
        }

        @Test
        @DisplayName("실패: 코드에 해당하는 서비스가 없으면 ServiceNotFoundException 발생")
        void shouldThrowException_WhenCodeNotExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(queryPort.findByCode(code)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findByCode(code))
                    .isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCodeOptional 메서드")
    class FindByCodeOptional {

        @Test
        @DisplayName("존재하면 Optional에 감싸서 반환")
        void shouldReturnOptionalWithService_WhenCodeExists() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();
            Service expected = ServiceFixture.create();

            given(queryPort.findByCode(code)).willReturn(Optional.of(expected));

            // when
            Optional<Service> result = sut.findByCodeOptional(code);

            // then
            assertThat(result).isPresent().contains(expected);
            then(queryPort).should().findByCode(code);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenCodeNotExists() {
            // given
            ServiceCode code = ServiceCode.of("NON_EXISTENT");

            given(queryPort.findByCode(code)).willReturn(Optional.empty());

            // when
            Optional<Service> result = sut.findByCodeOptional(code);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 서비스 목록 반환")
        void shouldReturnServices_MatchingCriteria() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);
            List<Service> expected = List.of(ServiceFixture.create());

            given(queryPort.findAllByCriteria(criteria)).willReturn(expected);

            // when
            List<Service> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 서비스가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoMatch() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);

            given(queryPort.findAllByCriteria(criteria)).willReturn(List.of());

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
        @DisplayName("성공: 조건에 맞는 서비스 수 반환")
        void shouldReturnCount() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);

            given(queryPort.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("findAllActive 메서드")
    class FindAllActive {

        @Test
        @DisplayName("성공: 활성 서비스 목록 반환")
        void shouldReturnActiveServices() {
            // given
            List<Service> expected = List.of(ServiceFixture.create());

            given(queryPort.findAllActive()).willReturn(expected);

            // when
            List<Service> result = sut.findAllActive();

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllActive();
        }

        @Test
        @DisplayName("활성 서비스가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoActiveServices() {
            // given
            given(queryPort.findAllActive()).willReturn(List.of());

            // when
            List<Service> result = sut.findAllActive();

            // then
            assertThat(result).isEmpty();
        }
    }
}
