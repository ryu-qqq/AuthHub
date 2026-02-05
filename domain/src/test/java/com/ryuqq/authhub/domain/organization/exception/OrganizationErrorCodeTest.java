package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationErrorCode 테스트")
class OrganizationErrorCodeTest {

    @Nested
    @DisplayName("OrganizationErrorCode getCode 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("ORGANIZATION_NOT_FOUND의 코드는 'ORG-001'이다")
        void organizationNotFoundCodeShouldBeOrg001() {
            // then
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getCode()).isEqualTo("ORG-001");
        }

        @Test
        @DisplayName("INVALID_ORGANIZATION_STATE의 코드는 'ORG-002'이다")
        void invalidOrganizationStateCodeShouldBeOrg002() {
            // then
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getCode())
                    .isEqualTo("ORG-002");
        }

        @Test
        @DisplayName("DUPLICATE_ORGANIZATION_NAME의 코드는 'ORG-003'이다")
        void duplicateOrganizationNameCodeShouldBeOrg003() {
            // then
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getCode())
                    .isEqualTo("ORG-003");
        }
    }

    @Nested
    @DisplayName("OrganizationErrorCode getHttpStatus 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("ORGANIZATION_NOT_FOUND의 HTTP 상태는 404이다")
        void organizationNotFoundHttpStatusShouldBe404() {
            // then
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("INVALID_ORGANIZATION_STATE의 HTTP 상태는 400이다")
        void invalidOrganizationStateHttpStatusShouldBe400() {
            // then
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getHttpStatus())
                    .isEqualTo(400);
        }

        @Test
        @DisplayName("DUPLICATE_ORGANIZATION_NAME의 HTTP 상태는 409이다")
        void duplicateOrganizationNameHttpStatusShouldBe409() {
            // then
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getHttpStatus())
                    .isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("OrganizationErrorCode getMessage 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("ORGANIZATION_NOT_FOUND의 메시지를 반환한다")
        void organizationNotFoundShouldHaveMessage() {
            // then
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getMessage())
                    .isEqualTo("Organization not found");
        }

        @Test
        @DisplayName("INVALID_ORGANIZATION_STATE의 메시지를 반환한다")
        void invalidOrganizationStateShouldHaveMessage() {
            // then
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getMessage())
                    .isEqualTo("Invalid organization state transition");
        }

        @Test
        @DisplayName("DUPLICATE_ORGANIZATION_NAME의 메시지를 반환한다")
        void duplicateOrganizationNameShouldHaveMessage() {
            // then
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getMessage())
                    .isEqualTo("Organization name already exists in this tenant");
        }
    }

    @Nested
    @DisplayName("OrganizationErrorCode ErrorCode 인터페이스 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("모든 OrganizationErrorCode는 ErrorCode 인터페이스를 구현한다")
        void allErrorCodesShouldImplementErrorCodeInterface() {
            // then
            assertThat(OrganizationErrorCode.ORGANIZATION_NOT_FOUND).isInstanceOf(ErrorCode.class);
            assertThat(OrganizationErrorCode.INVALID_ORGANIZATION_STATE)
                    .isInstanceOf(ErrorCode.class);
            assertThat(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME)
                    .isInstanceOf(ErrorCode.class);
        }
    }
}
