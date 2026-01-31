package com.ryuqq.authhub.application.user.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserPhoneNumberException;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserValidator 단위 테스트")
class UserValidatorTest {

    @Mock private UserReadManager readManager;

    @Mock private PasswordEncoderClient passwordEncoderClient;

    private UserValidator sut;

    @BeforeEach
    void setUp() {
        sut = new UserValidator(readManager, passwordEncoderClient);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 사용자가 존재하면 해당 사용자 반환")
        void shouldReturnUser_WhenExists() {
            // given
            UserId id = UserFixture.defaultId();
            User expected = UserFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            User result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            UserId id = UserFixture.defaultId();

            given(readManager.findById(id)).willThrow(new UserNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateIdentifierNotDuplicated 메서드")
    class ValidateIdentifierNotDuplicated {

        @Test
        @DisplayName("성공: 식별자가 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenIdentifierNotDuplicated() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            Identifier identifier = UserFixture.defaultIdentifier();

            given(readManager.existsByOrganizationIdAndIdentifier(organizationId, identifier))
                    .willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateIdentifierNotDuplicated(organizationId, identifier))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 식별자가 중복되면 DuplicateUserIdentifierException 발생")
        void shouldThrowException_WhenIdentifierDuplicated() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            Identifier identifier = UserFixture.defaultIdentifier();

            given(readManager.existsByOrganizationIdAndIdentifier(organizationId, identifier))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateIdentifierNotDuplicated(organizationId, identifier))
                    .isInstanceOf(DuplicateUserIdentifierException.class);
        }
    }

    @Nested
    @DisplayName("validatePhoneNumberNotDuplicated 메서드")
    class ValidatePhoneNumberNotDuplicated {

        @Test
        @DisplayName("성공: 전화번호가 null이면 검증 건너뜀")
        void shouldNotThrow_WhenPhoneNumberIsNull() {
            // when & then
            assertThatCode(
                            () ->
                                    sut.validatePhoneNumberNotDuplicated(
                                            UserFixture.defaultOrganizationId(), null))
                    .doesNotThrowAnyException();
            then(readManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: 전화번호가 중복되면 DuplicateUserPhoneNumberException 발생")
        void shouldThrowException_WhenPhoneNumberDuplicated() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            given(readManager.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validatePhoneNumberNotDuplicated(organizationId, phoneNumber))
                    .isInstanceOf(DuplicateUserPhoneNumberException.class);
        }
    }
}
