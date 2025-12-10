package com.ryuqq.authhub.domain.permission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SystemPermissionNotModifiableException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SystemPermissionNotModifiableException 테스트")
class SystemPermissionNotModifiableExceptionTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("권한 키로 예외를 생성한다")
        void shouldCreateExceptionWithPermissionKey() {
            // given
            String permissionKey = "system:admin";

            // when
            SystemPermissionNotModifiableException exception =
                    new SystemPermissionNotModifiableException(permissionKey);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("PERMISSION-003");
            assertThat(exception.args()).containsEntry("permissionKey", permissionKey);
        }

        @Test
        @DisplayName("args에 permissionKey가 포함된다")
        void shouldContainPermissionKeyInArgs() {
            // given
            String permissionKey = "system:manage";

            // when
            SystemPermissionNotModifiableException exception =
                    new SystemPermissionNotModifiableException(permissionKey);

            // then
            assertThat(exception.args()).hasSize(1);
            assertThat(exception.args()).containsKey("permissionKey");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            SystemPermissionNotModifiableException exception =
                    new SystemPermissionNotModifiableException("system:admin");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            SystemPermissionNotModifiableException exception =
                    new SystemPermissionNotModifiableException("system:admin");

            // then
            assertThat(exception.code())
                    .isEqualTo(PermissionErrorCode.SYSTEM_PERMISSION_NOT_MODIFIABLE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(PermissionErrorCode.SYSTEM_PERMISSION_NOT_MODIFIABLE.getMessage());
        }
    }
}
