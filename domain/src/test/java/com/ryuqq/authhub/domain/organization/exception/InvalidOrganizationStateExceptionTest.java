package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidOrganizationStateException 테스트")
class InvalidOrganizationStateExceptionTest {

    @Test
    @DisplayName("organizationId와 reason으로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithOrganizationIdAndReason() {
        // Given
        Long organizationId = 123L;
        String reason = "Organization is already deleted";

        // When
        InvalidOrganizationStateException exception =
                new InvalidOrganizationStateException(organizationId, reason);

        // Then
        assertThat(exception.code()).isEqualTo("ORGANIZATION-004");
        assertThat(exception.getMessage()).isEqualTo("Invalid organization status");
        assertThat(exception.args()).containsEntry("organizationId", organizationId);
        assertThat(exception.args()).containsEntry("reason", reason);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long organizationId = 456L;
        String reason = "Organization is inactive";
        InvalidOrganizationStateException exception =
                new InvalidOrganizationStateException(organizationId, reason);

        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.INVALID_ORGANIZATION_STATUS;

        // Then
        assertThat(exception.code()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
    }
}
