package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DuplicateTenantNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DuplicateTenantNameException 테스트")
class DuplicateTenantNameExceptionTest {

    @Nested
    @DisplayName("DuplicateTenantNameException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String name으로 예외를 생성한다")
        void shouldCreateWithStringName() {
            // given
            String name = "Test Tenant";

            // when
            DuplicateTenantNameException exception = new DuplicateTenantNameException(name);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.DUPLICATE_TENANT_NAME);
            assertThat(exception.code()).isEqualTo("TENANT-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("name", name);
        }

        @Test
        @DisplayName("TenantName으로 예외를 생성한다")
        void shouldCreateWithTenantName() {
            // given
            TenantName tenantName = TenantName.of("Test Tenant");

            // when
            DuplicateTenantNameException exception = new DuplicateTenantNameException(tenantName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.DUPLICATE_TENANT_NAME);
            assertThat(exception.code()).isEqualTo("TENANT-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("name", tenantName.value());
        }
    }

    @Nested
    @DisplayName("DuplicateTenantNameException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_TENANT_NAME이다")
        void errorCodeShouldBeDuplicateTenantName() {
            // given
            DuplicateTenantNameException exception = new DuplicateTenantNameException("name");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.DUPLICATE_TENANT_NAME);
            assertThat(exception.code()).isEqualTo("TENANT-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateTenantNameException exception = new DuplicateTenantNameException("name");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
