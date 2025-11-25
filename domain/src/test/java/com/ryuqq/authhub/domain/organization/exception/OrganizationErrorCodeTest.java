package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OrganizationErrorCode 테스트")
class OrganizationErrorCodeTest {

    @Test
    @DisplayName("ORGANIZATION_NOT_FOUND 에러 코드 검증")
    void shouldHaveOrganizationNotFoundErrorCode() {
        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.ORGANIZATION_NOT_FOUND;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("ORGANIZATION-001");
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        assertThat(errorCode.getMessage()).isEqualTo("Organization not found");
    }

    @Test
    @DisplayName("INVALID_ORGANIZATION_ID 에러 코드 검증")
    void shouldHaveInvalidOrganizationIdErrorCode() {
        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.INVALID_ORGANIZATION_ID;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("ORGANIZATION-002");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid organization ID");
    }

    @Test
    @DisplayName("INVALID_ORGANIZATION_NAME 에러 코드 검증")
    void shouldHaveInvalidOrganizationNameErrorCode() {
        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.INVALID_ORGANIZATION_NAME;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("ORGANIZATION-003");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid organization name");
    }

    @Test
    @DisplayName("INVALID_ORGANIZATION_STATUS 에러 코드 검증")
    void shouldHaveInvalidOrganizationStatusErrorCode() {
        // When
        OrganizationErrorCode errorCode = OrganizationErrorCode.INVALID_ORGANIZATION_STATUS;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("ORGANIZATION-004");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid organization status");
    }
}
