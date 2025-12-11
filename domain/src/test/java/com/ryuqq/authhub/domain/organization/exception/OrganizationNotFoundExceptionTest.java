package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationNotFoundException 테스트")
class OrganizationNotFoundExceptionTest {

    @Nested
    @DisplayName("OrganizationId 생성자")
    class OrganizationIdConstructorTest {

        @Test
        @DisplayName("OrganizationId로 예외를 생성한다")
        void shouldCreateExceptionWithOrganizationId() {
            // given
            UUID uuid = UUID.randomUUID();
            OrganizationId organizationId = OrganizationId.of(uuid);

            // when
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(organizationId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.args()).containsEntry("organizationId", uuid);
        }
    }

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            OrganizationNotFoundException exception = new OrganizationNotFoundException(uuid);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.args()).containsEntry("organizationId", uuid);
        }
    }

    @Nested
    @DisplayName("tenantId + organizationName 생성자")
    class TenantIdAndNameConstructorTest {

        @Test
        @DisplayName("테넌트 ID와 조직 이름으로 예외를 생성한다")
        void shouldCreateExceptionWithTenantIdAndName() {
            // given
            UUID tenantId = UUID.randomUUID();
            String organizationName = "Test Organization";

            // when
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(tenantId, organizationName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-001");
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("organizationName", organizationName);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            OrganizationNotFoundException exception =
                    new OrganizationNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.code())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getMessage());
        }
    }
}
