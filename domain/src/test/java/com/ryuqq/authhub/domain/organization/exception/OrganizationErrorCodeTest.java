package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationErrorCode 테스트")
class OrganizationErrorCodeTest {

    @Nested
    @DisplayName("ORGANIZATION_NOT_FOUND")
    class OrganizationNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getCode()).isEqualTo("ORG-001");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getMessage())
                    .isEqualTo("Organization not found");
        }
    }

    @Nested
    @DisplayName("INVALID_ORGANIZATION_STATE")
    class InvalidOrganizationStateTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getCode())
                    .isEqualTo("ORG-002");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getMessage())
                    .isEqualTo("Invalid organization state transition");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_ORGANIZATION_NAME")
    class DuplicateOrganizationNameTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getCode())
                    .isEqualTo("ORG-003");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getHttpStatus())
                    .isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getMessage())
                    .isEqualTo("Organization name already exists in this tenant");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }

        @Test
        @DisplayName("모든 에러 코드가 존재한다")
        void shouldHaveAllErrorCodes() {
            OrganizationErrorCode[] values = OrganizationErrorCode.values();

            assertThat(values).hasSize(3);
            assertThat(values)
                    .containsExactly(
                            OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                            OrganizationErrorCode.INVALID_ORGANIZATION_STATE,
                            OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME);
        }
    }
}
