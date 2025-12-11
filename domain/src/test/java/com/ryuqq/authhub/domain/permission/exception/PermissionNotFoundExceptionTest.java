package com.ryuqq.authhub.domain.permission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionNotFoundException 테스트")
class PermissionNotFoundExceptionTest {

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID permissionId = UUID.randomUUID();

            // when
            PermissionNotFoundException exception = new PermissionNotFoundException(permissionId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("PERMISSION-001");
            assertThat(exception.args()).containsEntry("permissionId", permissionId);
        }
    }

    @Nested
    @DisplayName("PermissionId 생성자")
    class PermissionIdConstructorTest {

        @Test
        @DisplayName("PermissionId로 예외를 생성한다")
        void shouldCreateExceptionWithPermissionId() {
            // given
            UUID uuid = UUID.randomUUID();
            PermissionId permissionId = PermissionId.of(uuid);

            // when
            PermissionNotFoundException exception = new PermissionNotFoundException(permissionId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("PERMISSION-001");
            assertThat(exception.args()).containsEntry("permissionId", uuid);
        }
    }

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("권한 키로 예외를 생성한다")
        void shouldCreateExceptionWithPermissionKey() {
            // given
            String permissionKey = "user:read";

            // when
            PermissionNotFoundException exception = new PermissionNotFoundException(permissionKey);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("PERMISSION-001");
            assertThat(exception.args()).containsEntry("permissionKey", permissionKey);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            PermissionNotFoundException exception =
                    new PermissionNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            PermissionNotFoundException exception =
                    new PermissionNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.code())
                    .isEqualTo(PermissionErrorCode.PERMISSION_NOT_FOUND.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(PermissionErrorCode.PERMISSION_NOT_FOUND.getMessage());
        }
    }
}
