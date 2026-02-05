package com.ryuqq.authhub.application.userrole.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.exception.RoleInUseException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleValidator 단위 테스트")
class UserRoleValidatorTest {

    @Mock private UserRoleReadManager readManager;

    private UserRoleValidator sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleValidator(readManager);
    }

    @Nested
    @DisplayName("validateNotInUse 메서드")
    class ValidateNotInUse {

        @Test
        @DisplayName("성공: 역할이 사용 중이 아니면 예외 없음")
        void shouldNotThrow_WhenRoleNotInUse() {
            // given
            RoleId roleId = UserRoleFixture.defaultRoleId();

            given(readManager.existsByRoleId(roleId)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNotInUse(roleId)).doesNotThrowAnyException();
            then(readManager).should().existsByRoleId(roleId);
        }

        @Test
        @DisplayName("실패: 역할이 사용 중이면 RoleInUseException 발생")
        void shouldThrowRoleInUseException_WhenRoleInUse() {
            // given
            RoleId roleId = UserRoleFixture.defaultRoleId();

            given(readManager.existsByRoleId(roleId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotInUse(roleId))
                    .isInstanceOf(RoleInUseException.class);
            then(readManager).should().existsByRoleId(roleId);
        }
    }
}
