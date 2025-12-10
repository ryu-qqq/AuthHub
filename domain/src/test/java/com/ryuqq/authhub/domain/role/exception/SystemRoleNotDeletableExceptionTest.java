package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SystemRoleNotDeletableException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SystemRoleNotDeletableException 테스트")
class SystemRoleNotDeletableExceptionTest {

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID roleId = UUID.randomUUID();

            // when
            SystemRoleNotDeletableException exception = new SystemRoleNotDeletableException(roleId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-004");
            assertThat(exception.args()).containsEntry("roleId", roleId);
        }
    }

    @Nested
    @DisplayName("RoleId 생성자")
    class RoleIdConstructorTest {

        @Test
        @DisplayName("RoleId로 예외를 생성한다")
        void shouldCreateExceptionWithRoleId() {
            // given
            UUID uuid = UUID.randomUUID();
            RoleId roleId = RoleId.of(uuid);

            // when
            SystemRoleNotDeletableException exception = new SystemRoleNotDeletableException(roleId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-004");
            assertThat(exception.args()).containsEntry("roleId", uuid);
        }
    }

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("역할 이름으로 예외를 생성한다")
        void shouldCreateExceptionWithRoleName() {
            // given
            String roleName = "SUPER_ADMIN";

            // when
            SystemRoleNotDeletableException exception =
                    new SystemRoleNotDeletableException(roleName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-004");
            assertThat(exception.args()).containsEntry("roleName", roleName);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            SystemRoleNotDeletableException exception =
                    new SystemRoleNotDeletableException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            SystemRoleNotDeletableException exception =
                    new SystemRoleNotDeletableException(UUID.randomUUID());

            // then
            assertThat(exception.code())
                    .isEqualTo(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getMessage());
        }
    }
}
