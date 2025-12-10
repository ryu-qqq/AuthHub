package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateOrganizationNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateOrganizationNameException 테스트")
class DuplicateOrganizationNameExceptionTest {

    @Nested
    @DisplayName("UUID + String 생성자")
    class UuidStringConstructorTest {

        @Test
        @DisplayName("UUID와 문자열로 예외를 생성한다")
        void shouldCreateExceptionWithUuidAndString() {
            // given
            UUID tenantId = UUID.randomUUID();
            String organizationName = "Duplicate Organization";

            // when
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(tenantId, organizationName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-003");
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("organizationName", organizationName);
        }
    }

    @Nested
    @DisplayName("TenantId + OrganizationName 생성자")
    class VoConstructorTest {

        @Test
        @DisplayName("TenantId와 OrganizationName VO로 예외를 생성한다")
        void shouldCreateExceptionWithVOs() {
            // given
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = TenantId.of(uuid);
            OrganizationName organizationName = OrganizationName.of("Duplicate Organization");

            // when
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(tenantId, organizationName);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-003");
            assertThat(exception.args()).containsEntry("tenantId", uuid);
            assertThat(exception.args())
                    .containsEntry("organizationName", "Duplicate Organization");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(UUID.randomUUID(), "Test");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateOrganizationNameException exception =
                    new DuplicateOrganizationNameException(UUID.randomUUID(), "Test");

            // then
            assertThat(exception.code())
                    .isEqualTo(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME.getMessage());
        }
    }
}
