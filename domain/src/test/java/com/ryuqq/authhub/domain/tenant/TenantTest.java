package com.ryuqq.authhub.domain.tenant;

import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tenant Aggregate 테스트")
class TenantTest {

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
        assertThat(tenant.getTenantId()).isEqualTo(tenantId);
        assertThat(tenant.getTenantName()).isNotNull();
        assertThat(tenant.getTenantStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    @DisplayName("null tenantId로 Tenant 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantId() {
        // Given
        TenantId nullTenantId = null;
        TenantName tenantName = TenantName.of("Test Tenant");

        // When & Then
        assertThatThrownBy(() -> Tenant.create(nullTenantId, tenantName, TenantStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null tenantName로 Tenant 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantName() {
        // Given
        TenantId tenantId = TenantId.of(1L);
        TenantName nullTenantName = null;

        // When & Then
        assertThatThrownBy(() -> Tenant.create(tenantId, nullTenantName, TenantStatus.ACTIVE))
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
        assertThatThrownBy(() -> Tenant.create(tenantId, tenantName, nullStatus))
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
        assertThat(tenant.getTenantStatus()).isEqualTo(TenantStatus.INACTIVE);
    }

    @Test
    @DisplayName("DELETED 상태로 Tenant 생성 성공")
    void shouldCreateDeletedTenant() {
        // When
        Tenant tenant = TenantFixture.aDeletedTenant();

        // Then
        assertThat(tenant).isNotNull();
        assertThat(tenant.getTenantStatus()).isEqualTo(TenantStatus.DELETED);
    }
}
