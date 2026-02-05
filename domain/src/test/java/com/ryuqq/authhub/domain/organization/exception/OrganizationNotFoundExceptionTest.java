package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationNotFoundException 테스트")
class OrganizationNotFoundExceptionTest {

    @Nested
    @DisplayName("OrganizationNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String organizationId로 예외를 생성한다")
        void shouldCreateWithStringOrganizationId() {
            // given
            String organizationId = "01941234-5678-7000-8000-123456789999";

            // when
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(organizationId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("organizationId", organizationId);
        }

        @Test
        @DisplayName("OrganizationId로 예외를 생성한다")
        void shouldCreateWithOrganizationId() {
            // given
            OrganizationId organizationId =
                    OrganizationId.of("01941234-5678-7000-8000-123456789999");

            // when
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(organizationId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("organizationId", organizationId.value());
        }

        @Test
        @DisplayName("tenantId와 organizationName으로 예외를 생성한다")
        void shouldCreateWithTenantIdAndOrganizationName() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            String organizationName = "Test Organization";

            // when
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(tenantId, organizationName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("organizationName", organizationName);
        }
    }

    @Nested
    @DisplayName("OrganizationNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 ORGANIZATION_NOT_FOUND이다")
        void errorCodeShouldBeOrganizationNotFound() {
            // given
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException("organizationId");

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ORG-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException("organizationId");

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
