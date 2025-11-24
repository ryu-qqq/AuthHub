package com.ryuqq.authhub.domain.organization.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrganizationNotFoundException 테스트")
class OrganizationNotFoundExceptionTest {

    @Test
    @DisplayName("organizationId로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithOrganizationId() {
        // Given
        Long organizationId = 123L;

        // When
        OrganizationNotFoundException exception = new OrganizationNotFoundException(organizationId);

        // Then
        assertThat(exception.getCode()).isEqualTo("ORGANIZATION-001");
        assertThat(exception.getMessage()).isEqualTo("Organization not found");
        assertThat(exception.getArgs()).containsEntry("organizationId", organizationId);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long organizationId = 456L;
        OrganizationNotFoundException exception = new OrganizationNotFoundException(organizationId);

        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.ORGANIZATION_NOT_FOUND;

        // Then
        assertThat(exception.getCode()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
    }
}
