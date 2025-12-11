package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateRoleNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateRoleNameException 테스트")
class DuplicateRoleNameExceptionTest {

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("문자열 역할 이름으로 예외를 생성한다")
        void shouldCreateExceptionWithRoleName() {
            // given
            String roleName = "ADMIN";

            // when
            DuplicateRoleNameException exception = new DuplicateRoleNameException(roleName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-002");
            assertThat(exception.args()).containsEntry("roleName", roleName);
        }
    }

    @Nested
    @DisplayName("RoleName 생성자")
    class RoleNameConstructorTest {

        @Test
        @DisplayName("RoleName VO로 예외를 생성한다")
        void shouldCreateExceptionWithRoleNameVo() {
            // given
            RoleName roleName = RoleName.of("MANAGER");

            // when
            DuplicateRoleNameException exception = new DuplicateRoleNameException(roleName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-002");
            assertThat(exception.args()).containsEntry("roleName", "MANAGER");
        }
    }

    @Nested
    @DisplayName("tenantId, roleName 생성자")
    class TenantIdRoleNameConstructorTest {

        @Test
        @DisplayName("tenantId와 역할 이름으로 예외를 생성한다")
        void shouldCreateExceptionWithTenantIdAndRoleName() {
            // given
            UUID tenantId = UUID.randomUUID();
            String roleName = "OPERATOR";

            // when
            DuplicateRoleNameException exception =
                    new DuplicateRoleNameException(tenantId, roleName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ROLE-002");
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
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
            DuplicateRoleNameException exception = new DuplicateRoleNameException("ADMIN");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateRoleNameException exception = new DuplicateRoleNameException("ADMIN");

            // then
            assertThat(exception.code()).isEqualTo(RoleErrorCode.DUPLICATE_ROLE_NAME.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(RoleErrorCode.DUPLICATE_ROLE_NAME.getMessage());
        }
    }
}
