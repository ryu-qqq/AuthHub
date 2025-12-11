package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateTenantNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateTenantNameException 테스트")
class DuplicateTenantNameExceptionTest {

    @Nested
    @DisplayName("TenantName 생성자")
    class TenantNameConstructorTest {

        @Test
        @DisplayName("TenantName으로 예외를 생성한다")
        void shouldCreateExceptionWithTenantName() {
            // given
            TenantName tenantName = TenantName.of("Duplicate Tenant");

            // when
            DuplicateTenantNameException exception = new DuplicateTenantNameException(tenantName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-003");
            assertThat(exception.args()).containsEntry("name", "Duplicate Tenant");
        }
    }

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("문자열로 예외를 생성한다")
        void shouldCreateExceptionWithString() {
            // given
            String name = "Duplicate Name";

            // when
            DuplicateTenantNameException exception = new DuplicateTenantNameException(name);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-003");
            assertThat(exception.args()).containsEntry("name", "Duplicate Name");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            DuplicateTenantNameException exception = new DuplicateTenantNameException("Test");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateTenantNameException exception = new DuplicateTenantNameException("Test");

            // then
            assertThat(exception.code()).isEqualTo(TenantErrorCode.DUPLICATE_TENANT_NAME.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(TenantErrorCode.DUPLICATE_TENANT_NAME.getMessage());
        }
    }
}
