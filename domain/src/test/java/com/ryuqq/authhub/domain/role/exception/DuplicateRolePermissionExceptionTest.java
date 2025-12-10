package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateRolePermissionException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateRolePermissionException 테스트")
class DuplicateRolePermissionExceptionTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("roleId와 permissionId로 예외를 생성한다")
        void shouldCreateExceptionWithRoleIdAndPermissionId() {
            // given
            UUID roleId = UUID.randomUUID();
            UUID permissionId = UUID.randomUUID();

            // when
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(roleId, permissionId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-007");
            assertThat(exception.args()).containsEntry("roleId", roleId);
            assertThat(exception.args()).containsEntry("permissionId", permissionId);
        }

        @Test
        @DisplayName("args에 roleId와 permissionId가 포함된다")
        void shouldContainRoleIdAndPermissionIdInArgs() {
            // given
            UUID roleId = UUID.randomUUID();
            UUID permissionId = UUID.randomUUID();

            // when
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(roleId, permissionId);

            // then
            assertThat(exception.args()).hasSize(2);
            assertThat(exception.args()).containsKey("roleId");
            assertThat(exception.args()).containsKey("permissionId");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(UUID.randomUUID(), UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(UUID.randomUUID(), UUID.randomUUID());

            // then
            assertThat(exception.code())
                    .isEqualTo(RoleErrorCode.DUPLICATE_ROLE_PERMISSION.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(RoleErrorCode.DUPLICATE_ROLE_PERMISSION.getMessage());
        }
    }
}
