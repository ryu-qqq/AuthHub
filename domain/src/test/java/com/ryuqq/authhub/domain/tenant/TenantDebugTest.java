package com.ryuqq.authhub.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tenant Debug 테스트")
class TenantDebugTest {

    private final Clock laterClock = () -> Instant.parse("2025-11-24T01:00:00Z");

    @Test
    @DisplayName("DELETED 상태 Tenant 생성 확인")
    void checkDeletedTenantCreation() {
        // Given & When
        Tenant deletedTenant = TenantFixture.aDeletedTenant();

        // Then
        System.out.println("Deleted Tenant Status: " + deletedTenant.statusValue());
        System.out.println("Is Deleted: " + deletedTenant.isDeleted());
        assertThat(deletedTenant.statusValue()).isEqualTo("DELETED");
        assertThat(deletedTenant.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("DELETED 상태에서 activate 시도")
    void testActivateFromDeleted() {
        // Given
        Tenant deletedTenant = TenantFixture.aDeletedTenant();
        System.out.println("Before activate - Status: " + deletedTenant.statusValue());

        // When & Then
        try {
            deletedTenant.activate(laterClock);
            System.out.println("ERROR: Exception should have been thrown!");
        } catch (InvalidTenantStateException e) {
            System.out.println("Exception caught: " + e.getClass().getSimpleName());
            System.out.println("Exception message: " + e.getMessage());

            assertThat(e).isInstanceOf(InvalidTenantStateException.class);
            assertThat(e.getMessage()).contains("Invalid tenant status");
        }
    }
}
