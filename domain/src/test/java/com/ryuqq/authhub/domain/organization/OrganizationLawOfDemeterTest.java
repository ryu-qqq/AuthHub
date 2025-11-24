package com.ryuqq.authhub.domain.organization;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Organization Law of Demeter 준수 테스트")
class OrganizationLawOfDemeterTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("organizationIdValue - Organization ID의 Long 값을 직접 반환 (Getter 체이닝 방지)")
    void shouldReturnOrganizationIdValueDirectly() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        Organization organization = Organization.of(
                id,
                OrganizationName.of("Test"),
                TenantId.of(1L),
                OrganizationStatus.ACTIVE,
                clock.now(),
                clock.now()
        );

        // When
        Long idValue = organization.organizationIdValue();

        // Then
        assertThat(idValue).isEqualTo(100L);
    }

    @Test
    @DisplayName("organizationNameValue - Organization Name의 String 값을 직접 반환")
    void shouldReturnOrganizationNameValueDirectly() {
        // Given
        Organization organization = Organization.forNew(
                OrganizationName.of("Test Organization"),
                TenantId.of(1L),
                clock
        );

        // When
        String nameValue = organization.organizationNameValue();

        // Then
        assertThat(nameValue).isEqualTo("Test Organization");
    }

    @Test
    @DisplayName("statusValue - OrganizationStatus의 name() 값을 직접 반환")
    void shouldReturnStatusValueDirectly() {
        // Given
        Organization organization = Organization.forNew(
                OrganizationName.of("Test"),
                TenantId.of(1L),
                clock
        );

        // When
        String statusValue = organization.statusValue();

        // Then
        assertThat(statusValue).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("isNew - OrganizationId가 null이면 true, 아니면 false 반환")
    void shouldReturnIsNewCorrectly() {
        // Given
        Organization newOrganization = Organization.forNew(
                OrganizationName.of("New Org"),
                TenantId.of(1L),
                clock
        );

        Organization existingOrganization = Organization.of(
                OrganizationId.of(100L),
                OrganizationName.of("Existing Org"),
                TenantId.of(1L),
                OrganizationStatus.ACTIVE,
                clock.now(),
                clock.now()
        );

        // When & Then
        assertThat(newOrganization.isNew()).isTrue();
        assertThat(existingOrganization.isNew()).isFalse();
    }

    @Test
    @DisplayName("createdAt/updatedAt - Instant 값을 직접 반환")
    void shouldReturnTimestampsDirectly() {
        // Given
        Instant createdAt = Instant.parse("2025-11-20T00:00:00Z");
        Instant updatedAt = Instant.parse("2025-11-23T00:00:00Z");

        Organization organization = Organization.of(
                OrganizationId.of(100L),
                OrganizationName.of("Test"),
                TenantId.of(1L),
                OrganizationStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        // When & Then
        assertThat(organization.createdAt()).isEqualTo(createdAt);
        assertThat(organization.updatedAt()).isEqualTo(updatedAt);
    }
}
