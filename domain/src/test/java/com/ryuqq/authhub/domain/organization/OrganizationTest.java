package com.ryuqq.authhub.domain.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Organization Aggregate 테스트")
class OrganizationTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("유효한 데이터로 Organization 생성 성공")
    void shouldCreateOrganizationWithValidData() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        TenantId tenantId = TenantId.of(1L);

        // When
        Organization organization = OrganizationFixture.anOrganization(organizationId);

        // Then
        assertThat(organization).isNotNull();
        assertThat(organization.organizationIdValue()).isEqualTo(organizationId.value());
        assertThat(organization.organizationNameValue()).isNotNull();
        assertThat(organization.tenantIdValue()).isNotNull();
        assertThat(organization.statusValue()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("null organizationName로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullOrganizationName() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName nullOrganizationName = null;
        TenantId tenantId = TenantId.of(1L);

        // When & Then
        assertThatThrownBy(
                        () ->
                                Organization.of(
                                        organizationId,
                                        nullOrganizationName,
                                        tenantId,
                                        OrganizationStatus.ACTIVE,
                                        clock.now(),
                                        clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrganizationName은 null일 수 없습니다");
    }

    @Test
    @DisplayName("null tenantId로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantId() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        TenantId nullTenantId = null;

        // When & Then
        assertThatThrownBy(
                        () ->
                                Organization.of(
                                        organizationId,
                                        organizationName,
                                        nullTenantId,
                                        OrganizationStatus.ACTIVE,
                                        clock.now(),
                                        clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null organizationStatus로 Organization 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullOrganizationStatus() {
        // Given
        OrganizationId organizationId = OrganizationId.of(100L);
        OrganizationName organizationName = OrganizationName.of("Test Organization");
        TenantId tenantId = TenantId.of(1L);
        OrganizationStatus nullStatus = null;

        // When & Then
        assertThatThrownBy(
                        () ->
                                Organization.of(
                                        organizationId,
                                        organizationName,
                                        tenantId,
                                        nullStatus,
                                        clock.now(),
                                        clock.now()))
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
        assertThat(organization.statusValue()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("DELETED 상태로 Organization 생성 성공")
    void shouldCreateDeletedOrganization() {
        // When
        Organization organization = OrganizationFixture.aDeletedOrganization();

        // Then
        assertThat(organization).isNotNull();
        assertThat(organization.statusValue()).isEqualTo("DELETED");
    }
}
