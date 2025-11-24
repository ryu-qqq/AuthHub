package com.ryuqq.authhub.domain.organization;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Organization 팩토리 메서드 테스트")
class OrganizationFactoryMethodTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("forNew - 새 Organization 생성 (ID null, ACTIVE 상태, 생성/수정 시간 동일)")
    void shouldCreateNewOrganizationWithForNew() {
        // Given
        OrganizationName name = OrganizationName.of("New Organization");
        Long tenantId = 1L;

        // When
        Organization organization = Organization.forNew(name, tenantId, clock);

        // Then
        assertThat(organization.organizationIdValue()).isNull();
        assertThat(organization.isNew()).isTrue();
        assertThat(organization.statusValue()).isEqualTo("ACTIVE");
        assertThat(organization.createdAt()).isEqualTo(clock.now());
        assertThat(organization.updatedAt()).isEqualTo(clock.now());
    }

    @Test
    @DisplayName("of - 기존 Organization 로드 (ID 있음, 지정된 상태, 시간 지정)")
    void shouldLoadExistingOrganizationWithOf() {
        // Given
        OrganizationId id = OrganizationId.of(100L);
        OrganizationName name = OrganizationName.of("Existing Organization");
        Long tenantId = 1L;
        OrganizationStatus status = OrganizationStatus.INACTIVE;
        Instant createdAt = Instant.parse("2025-11-20T00:00:00Z");
        Instant updatedAt = Instant.parse("2025-11-23T00:00:00Z");

        // When
        Organization organization = Organization.of(id, name, tenantId, status, createdAt, updatedAt);

        // Then
        assertThat(organization.organizationIdValue()).isEqualTo(100L);
        assertThat(organization.isNew()).isFalse();
        assertThat(organization.statusValue()).isEqualTo("INACTIVE");
        assertThat(organization.createdAt()).isEqualTo(createdAt);
        assertThat(organization.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("reconstitute - DB에서 Organization 재구성 (ID 필수, 상태 보존)")
    void shouldReconstituteOrganizationFromDatabase() {
        // Given
        OrganizationId id = OrganizationId.of(200L);
        OrganizationName name = OrganizationName.of("Reconstituted Organization");
        Long tenantId = 2L;
        OrganizationStatus status = OrganizationStatus.DELETED;
        Instant createdAt = Instant.parse("2025-11-15T00:00:00Z");
        Instant updatedAt = Instant.parse("2025-11-22T00:00:00Z");

        // When
        Organization organization = Organization.reconstitute(id, name, tenantId, status, createdAt, updatedAt);

        // Then
        assertThat(organization.organizationIdValue()).isEqualTo(200L);
        assertThat(organization.isNew()).isFalse();
        assertThat(organization.statusValue()).isEqualTo("DELETED");
        assertThat(organization.createdAt()).isEqualTo(createdAt);
        assertThat(organization.updatedAt()).isEqualTo(updatedAt);
    }
}
