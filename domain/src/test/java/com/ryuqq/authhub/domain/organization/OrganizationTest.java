package com.ryuqq.authhub.domain.organization;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Organization Aggregate 테스트")
class OrganizationTest {

    @Test
    @DisplayName("유효한 데이터로 Organization 생성 성공")
    void shouldCreateOrganizationWithValidData() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        Long tenantId = 1L;

        // When
        Organization organization = OrganizationFixture.anOrganization(organizationId);

        // Then
        assertThat(organization).isNotNull();
        assertThat(organization.getOrganizationId()).isEqualTo(organizationId);
        assertThat(organization.getOrganizationName()).isNotNull();
        assertThat(organization.getTenantId()).isNotNull();
        assertThat(organization.getOrganizationStatus()).isEqualTo(OrganizationStatus.ACTIVE);
    }

    @Test
    @DisplayName("null organizationId로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullOrganizationId() {
        // Given
        OrganizationId nullOrganizationId = null;
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        Long tenantId = 1L;

        // When & Then
        assertThatThrownBy(() -> Organization.create(nullOrganizationId, organizationName, tenantId, OrganizationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null organizationName로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullOrganizationName() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName nullOrganizationName = null;
        Long tenantId = 1L;

        // When & Then
        assertThatThrownBy(() -> Organization.create(organizationId, nullOrganizationName, tenantId, OrganizationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 null일 수 없습니다");
    }

    @Test
    @DisplayName("null tenantId로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantId() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        Long nullTenantId = null;

        // When & Then
        assertThatThrownBy(() -> Organization.create(organizationId, organizationName, nullTenantId, OrganizationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null organizationStatus로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullOrganizationStatus() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        Long tenantId = 1L;
        OrganizationStatus nullStatus = null;

        // When & Then
        assertThatThrownBy(() -> Organization.create(organizationId, organizationName, tenantId, nullStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationStatus는 null일 수 없습니다");
    }

    @Test
    @DisplayName("INACTIVE 상태로 Organization 생성 성공")
    void shouldCreateInactiveOrganization() {
        // When
        Organization organization = OrganizationFixture.anInactiveOrganization();

        // Then
        assertThat(organization).isNotNull();
        assertThat(organization.getOrganizationStatus()).isEqualTo(OrganizationStatus.INACTIVE);
    }

    @Test
    @DisplayName("DELETED 상태로 Organization 생성 성공")
    void shouldCreateDeletedOrganization() {
        // When
        Organization organization = OrganizationFixture.aDeletedOrganization();

        // Then
        assertThat(organization).isNotNull();
        assertThat(organization.getOrganizationStatus()).isEqualTo(OrganizationStatus.DELETED);
    }
}
