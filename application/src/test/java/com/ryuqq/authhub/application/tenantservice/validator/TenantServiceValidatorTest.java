package com.ryuqq.authhub.application.tenantservice.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceReadManager;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.DuplicateTenantServiceException;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantServiceValidator 단위 테스트
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
@DisplayName("TenantServiceValidator 단위 테스트")
class TenantServiceValidatorTest {

    @Mock private TenantServiceReadManager readManager;

    private TenantServiceValidator sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceValidator(readManager);
    }

    @Nested
    @DisplayName("validateExists 메서드")
    class ValidateExists {

        @Test
        @DisplayName("성공: TenantService가 존재하면 예외 없음")
        void shouldNotThrow_WhenTenantServiceExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(readManager.existsById(id)).willReturn(true);

            // when & then
            assertThatCode(() -> sut.validateExists(id)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: TenantService가 존재하지 않으면 TenantServiceNotFoundException 발생")
        void shouldThrowException_WhenTenantServiceNotExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(readManager.existsById(id)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.validateExists(id))
                    .isInstanceOf(TenantServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: TenantService가 존재하면 해당 TenantService 반환")
        void shouldReturnTenantService_WhenExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();
            TenantService expected = TenantServiceFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            TenantService result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: TenantService가 존재하지 않으면 TenantServiceNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            TenantServiceId id = TenantServiceFixture.defaultId();

            given(readManager.findById(id)).willThrow(new TenantServiceNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(TenantServiceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicated 메서드")
    class ValidateNotDuplicated {

        @Test
        @DisplayName("성공: 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenNotDuplicated() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(readManager.existsByTenantIdAndServiceId(tenantId, serviceId)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNotDuplicated(tenantId, serviceId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 중복되면 DuplicateTenantServiceException 발생")
        void shouldThrowException_WhenDuplicated() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(readManager.existsByTenantIdAndServiceId(tenantId, serviceId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicated(tenantId, serviceId))
                    .isInstanceOf(DuplicateTenantServiceException.class);
        }

        @Test
        @DisplayName("ReadManager를 통해 중복 여부 확인")
        void shouldCheckDuplication_ThroughReadManager() {
            // given
            TenantId tenantId = TenantServiceFixture.defaultTenantId();
            ServiceId serviceId = TenantServiceFixture.defaultServiceId();

            given(readManager.existsByTenantIdAndServiceId(tenantId, serviceId)).willReturn(false);

            // when
            sut.validateNotDuplicated(tenantId, serviceId);

            // then
            then(readManager).should().existsByTenantIdAndServiceId(tenantId, serviceId);
        }
    }
}
