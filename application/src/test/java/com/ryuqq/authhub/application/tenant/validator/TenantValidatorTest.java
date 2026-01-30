package com.ryuqq.authhub.application.tenant.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantValidator 단위 테스트")
class TenantValidatorTest {

    @Mock private TenantReadManager readManager;

    private TenantValidator sut;

    @BeforeEach
    void setUp() {
        sut = new TenantValidator(readManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 테넌트가 존재하면 해당 테넌트 반환")
        void shouldReturnTenant_WhenExists() {
            // given
            TenantId id = TenantFixture.defaultId();
            Tenant expected = TenantFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            Tenant result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 테넌트가 존재하지 않으면 TenantNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(readManager.findById(id)).willThrow(new TenantNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(TenantNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicated 메서드")
    class ValidateNameNotDuplicated {

        @Test
        @DisplayName("성공: 이름이 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenNameIsNotDuplicated() {
            // given
            TenantName name = TenantName.of("Unique Tenant");

            given(readManager.existsByName(name)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNameNotDuplicated(name)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 이름이 중복되면 DuplicateTenantNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            TenantName name = TenantName.of("Duplicate Tenant");

            given(readManager.existsByName(name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicated(name))
                    .isInstanceOf(DuplicateTenantNameException.class);
        }
    }
}
