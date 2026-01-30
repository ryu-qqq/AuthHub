package com.ryuqq.authhub.domain.tenant.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tenant Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Tenant Aggregate 테스트")
class TenantTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("Tenant 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 Tenant를 성공적으로 생성한다")
        void shouldCreateTenantSuccessfully() {
            // given
            TenantId tenantId = TenantId.forNew("01941234-5678-7000-8000-123456789999");
            TenantName name = TenantName.of("New Tenant");

            // when
            Tenant tenant = Tenant.create(tenantId, name, NOW);

            // then
            assertThat(tenant.tenantIdValue()).isEqualTo(tenantId.value());
            assertThat(tenant.nameValue()).isEqualTo(name.value());
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.isDeleted()).isFalse();
            assertThat(tenant.createdAt()).isEqualTo(NOW);
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("reconstitute로 Tenant를 재구성한다")
        void shouldReconstituteFromDatabase() {
            // given
            Tenant tenant = TenantFixture.create();

            // then
            assertThat(tenant.tenantIdValue()).isEqualTo(TenantFixture.defaultIdString());
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("Tenant 이름 변경 테스트")
    class NameChangeTests {

        @Test
        @DisplayName("Tenant 이름을 변경한다")
        void shouldChangeName() {
            // given
            Tenant tenant = TenantFixture.create();
            TenantName newName = TenantName.of("Changed Tenant Name");

            // when
            tenant.changeName(newName, NOW);

            // then
            assertThat(tenant.nameValue()).isEqualTo("Changed Tenant Name");
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Tenant 상태 변경 테스트")
    class StatusChangeTests {

        @Test
        @DisplayName("ACTIVE 상태를 INACTIVE로 변경한다")
        void shouldChangeStatusToInactive() {
            // given
            Tenant tenant = TenantFixture.create();
            assertThat(tenant.isActive()).isTrue();

            // when
            tenant.changeStatus(TenantStatus.INACTIVE, NOW);

            // then
            assertThat(tenant.isActive()).isFalse();
            assertThat(tenant.statusValue()).isEqualTo("INACTIVE");
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("INACTIVE 상태를 ACTIVE로 변경한다")
        void shouldChangeStatusToActive() {
            // given
            Tenant tenant = TenantFixture.createInactive();
            assertThat(tenant.isActive()).isFalse();

            // when
            tenant.changeStatus(TenantStatus.ACTIVE, NOW);

            // then
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Tenant 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("Tenant를 삭제(소프트 삭제)한다")
        void shouldDeleteTenant() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            tenant.delete(NOW);

            // then
            assertThat(tenant.isDeleted()).isTrue();
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("삭제된 Tenant를 복원한다")
        void shouldRestoreTenant() {
            // given
            Tenant tenant = TenantFixture.createDeleted();
            assertThat(tenant.isDeleted()).isTrue();

            // when
            tenant.restore(NOW);

            // then
            assertThat(tenant.isDeleted()).isFalse();
            assertThat(tenant.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Tenant Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isActive는 ACTIVE 상태에서 true를 반환한다")
        void isActiveShouldReturnTrueWhenActive() {
            // given
            Tenant activeTenant = TenantFixture.create();
            Tenant inactiveTenant = TenantFixture.createInactive();

            // then
            assertThat(activeTenant.isActive()).isTrue();
            assertThat(inactiveTenant.isActive()).isFalse();
        }

        @Test
        @DisplayName("statusValue는 상태 문자열을 반환한다")
        void statusValueShouldReturnStatusString() {
            // given
            Tenant activeTenant = TenantFixture.create();
            Tenant inactiveTenant = TenantFixture.createInactive();

            // then
            assertThat(activeTenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(inactiveTenant.statusValue()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("Tenant equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 tenantId를 가진 Tenant는 동등하다")
        void shouldBeEqualWhenSameTenantId() {
            // given
            Tenant tenant1 = TenantFixture.create();
            Tenant tenant2 = TenantFixture.create();

            // then
            assertThat(tenant1).isEqualTo(tenant2);
            assertThat(tenant1.hashCode()).isEqualTo(tenant2.hashCode());
        }

        @Test
        @DisplayName("다른 tenantId를 가진 Tenant는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentTenantId() {
            // given
            Tenant tenant1 = TenantFixture.create();
            Tenant tenant2 = TenantFixture.createNew();

            // then - createNew()는 다른 ID를 생성하므로 동등하지 않음
            assertThat(tenant1).isNotEqualTo(tenant2);
        }
    }
}
