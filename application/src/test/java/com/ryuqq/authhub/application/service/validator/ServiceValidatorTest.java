package com.ryuqq.authhub.application.service.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.exception.DuplicateServiceIdException;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ServiceValidator 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Validator는 조회 기반 검증 로직 담당 → ReadManager 협력 검증
 *   <li>검증 실패 시 적절한 DomainException 발생 검증
 *   <li>검증 성공 시 예외 없이 정상 반환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceValidator 단위 테스트")
class ServiceValidatorTest {

    @Mock private ServiceReadManager readManager;

    private ServiceValidator sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceValidator(readManager);
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공: 서비스가 존재하면 예외 없음")
        void shouldNotThrow_WhenServiceExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(readManager.existsById(id)).willReturn(true);

            // when & then
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
            then(readManager).should().existsById(id);
        }

        @Test
        @DisplayName("실패: 서비스가 존재하지 않으면 ServiceNotFoundException 발생")
        void shouldThrowException_WhenServiceNotExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(readManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 서비스가 존재하면 해당 서비스 반환")
        void shouldReturnService_WhenExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();
            Service expected = ServiceFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            Service result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 서비스가 존재하지 않으면 ServiceNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            ServiceId id = ServiceFixture.defaultId();

            given(readManager.findById(id)).willThrow(new ServiceNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicated 메서드")
    class ValidateCodeNotDuplicated {

        @Test
        @DisplayName("성공: 코드가 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenCodeIsNotDuplicated() {
            // given
            ServiceCode code = ServiceCode.of("SVC_NEW");

            given(readManager.existsByCode(code)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateCodeNotDuplicated(code)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 코드가 중복되면 DuplicateServiceIdException 발생")
        void shouldThrowException_WhenCodeIsDuplicated() {
            // given
            ServiceCode code = ServiceFixture.defaultCode();

            given(readManager.existsByCode(code)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicated(code))
                    .isInstanceOf(DuplicateServiceIdException.class);
        }
    }
}
