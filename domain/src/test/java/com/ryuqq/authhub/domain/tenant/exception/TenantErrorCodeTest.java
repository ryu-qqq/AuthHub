package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantErrorCode 테스트")
class TenantErrorCodeTest {

    @Nested
    @DisplayName("TENANT_NOT_FOUND")
    class TenantNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(TenantErrorCode.TENANT_NOT_FOUND.getCode()).isEqualTo("TENANT-001");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(TenantErrorCode.TENANT_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(TenantErrorCode.TENANT_NOT_FOUND.getMessage()).isEqualTo("Tenant not found");
        }
    }

    @Nested
    @DisplayName("INVALID_TENANT_STATE")
    class InvalidTenantStateTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(TenantErrorCode.INVALID_TENANT_STATE.getCode()).isEqualTo("TENANT-002");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(TenantErrorCode.INVALID_TENANT_STATE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(TenantErrorCode.INVALID_TENANT_STATE.getMessage())
                    .isEqualTo("Invalid tenant state transition");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_TENANT_NAME")
    class DuplicateTenantNameTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(TenantErrorCode.DUPLICATE_TENANT_NAME.getCode()).isEqualTo("TENANT-003");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(TenantErrorCode.DUPLICATE_TENANT_NAME.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(TenantErrorCode.DUPLICATE_TENANT_NAME.getMessage())
                    .isEqualTo("Tenant name already exists");
        }
    }

    @Nested
    @DisplayName("TENANT_HAS_ACTIVE_ORGANIZATIONS")
    class TenantHasActiveOrganizationsTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(TenantErrorCode.TENANT_HAS_ACTIVE_ORGANIZATIONS.getCode())
                    .isEqualTo("TENANT-004");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(TenantErrorCode.TENANT_HAS_ACTIVE_ORGANIZATIONS.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(TenantErrorCode.TENANT_HAS_ACTIVE_ORGANIZATIONS.getMessage())
                    .isEqualTo("Cannot deactivate tenant with active organizations");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            assertThat(TenantErrorCode.TENANT_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }

        @Test
        @DisplayName("모든 에러 코드가 존재한다")
        void shouldHaveAllErrorCodes() {
            TenantErrorCode[] values = TenantErrorCode.values();

            assertThat(values).hasSize(4);
            assertThat(values)
                    .containsExactly(
                            TenantErrorCode.TENANT_NOT_FOUND,
                            TenantErrorCode.INVALID_TENANT_STATE,
                            TenantErrorCode.DUPLICATE_TENANT_NAME,
                            TenantErrorCode.TENANT_HAS_ACTIVE_ORGANIZATIONS);
        }
    }
}
