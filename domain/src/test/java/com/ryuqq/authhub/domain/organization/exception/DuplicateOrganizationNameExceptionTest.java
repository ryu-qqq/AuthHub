package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DuplicateOrganizationNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DuplicateOrganizationNameException 테스트")
class DuplicateOrganizationNameExceptionTest {

    @Nested
    @DisplayName("DuplicateOrganizationNameException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String 파라미터로 예외를 생성한다")
        void shouldCreateWithStringParameters() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            String organizationName = "Test Organization";

            // when
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(tenantId, organizationName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME);
            assertThat(exception.code()).isEqualTo("ORG-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("organizationName", organizationName);
        }

        @Test
        @DisplayName("TenantId와 OrganizationName으로 예외를 생성한다")
        void shouldCreateWithTenantIdAndOrganizationName() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789abc");
            OrganizationName name = OrganizationName.of("Test Organization");

            // when
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(tenantId, name);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME);
            assertThat(exception.code()).isEqualTo("ORG-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("tenantId", tenantId.value());
            assertThat(exception.args()).containsEntry("organizationName", name.value());
        }
    }

    @Nested
    @DisplayName("DuplicateOrganizationNameException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_ORGANIZATION_NAME이다")
        void errorCodeShouldBeDuplicateOrganizationName() {
            // given
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException("tenantId", "name");

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME);
            assertThat(exception.code()).isEqualTo("ORG-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException("tenantId", "name");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
