package com.ryuqq.authhub.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tenant Aggregate 테스트")
class TenantTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("유효한 데이터로 Tenant 생성 성공")
    void shouldCreateTenantWithValidData() {
        // Given
        TenantId tenantId = TenantId.of(1L);
        TenantName tenantName = TenantName.of("Test Tenant");

        // When
        Tenant tenant = TenantFixture.aTenant(tenantId);

        // Then
        assertThat(tenant).isNotNull();
        assertThat(tenant.tenantIdValue()).isEqualTo(tenantId.value());
        assertThat(tenant.tenantNameValue()).isNotNull();
        assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("null tenantName로 Tenant 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantName() {
        // Given
        TenantId tenantId = TenantId.of(1L);
        TenantName nullTenantName = null;

        // When & Then
        assertThatThrownBy(
                        () ->
                                Tenant.of(
                                        tenantId,
                                        nullTenantName,
                                        TenantStatus.ACTIVE,
                                        clock.now(),
                                        clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantName은 null일 수 없습니다");
    }

    @Test
    @DisplayName("null tenantStatus로 Tenant 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantStatus() {
        // Given
        TenantId tenantId = TenantId.of(1L);
        TenantName tenantName = TenantName.of("Test Tenant");
        TenantStatus nullStatus = null;

        // When & Then
        assertThatThrownBy(
                        () -> Tenant.of(tenantId, tenantName, nullStatus, clock.now(), clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantStatus는 null일 수 없습니다");
    }

    @Test
    @DisplayName("INACTIVE 상태로 Tenant 생성 성공")
    void shouldCreateInactiveTenant() {
        // When
        Tenant tenant = TenantFixture.anInactiveTenant();

        // Then
        assertThat(tenant).isNotNull();
        assertThat(tenant.statusValue()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("DELETED 상태로 Tenant 생성 성공")
    void shouldCreateDeletedTenant() {
        // When
        Tenant tenant = TenantFixture.aDeletedTenant();

        // Then
        assertThat(tenant).isNotNull();
        assertThat(tenant.statusValue()).isEqualTo("DELETED");
    }
}
