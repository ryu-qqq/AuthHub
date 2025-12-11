package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleNotFoundException 테스트")
class UserRoleNotFoundExceptionTest {

    @Nested
    @DisplayName("userId + roleId 생성자")
    class ConstructorTest {

        @Test
        @DisplayName("사용자 ID와 역할 ID로 예외를 생성한다")
        void shouldCreateExceptionWithUserIdAndRoleId() {
            // given
            UUID userId = UUID.randomUUID();
            UUID roleId = UUID.randomUUID();

            // when
            UserRoleNotFoundException exception = new UserRoleNotFoundException(userId, roleId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-004");
            assertThat(exception.args()).containsEntry("userId", userId);
            assertThat(exception.args()).containsEntry("roleId", roleId);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            UserRoleNotFoundException exception =
                    new UserRoleNotFoundException(UUID.randomUUID(), UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            UserRoleNotFoundException exception =
                    new UserRoleNotFoundException(UUID.randomUUID(), UUID.randomUUID());

            // then
            assertThat(exception.code()).isEqualTo(UserErrorCode.USER_ROLE_NOT_FOUND.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(UserErrorCode.USER_ROLE_NOT_FOUND.getMessage());
        }
    }
}
