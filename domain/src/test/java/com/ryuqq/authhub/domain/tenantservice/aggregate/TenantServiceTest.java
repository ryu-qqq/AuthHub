package com.ryuqq.authhub.domain.tenantservice.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantService Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantService Aggregate 테스트")
class TenantServiceTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("TenantService 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 TenantService를 성공적으로 생성한다")
        void shouldCreateTenantServiceSuccessfully() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-tenant123456");
            ServiceId serviceId = ServiceId.of(2L);

            // when
            TenantService tenantService = TenantService.create(tenantId, serviceId, NOW);

            // then
            assertThat(tenantService.tenantIdValue())
                    .isEqualTo("01941234-5678-7000-8000-tenant123456");
            assertThat(tenantService.serviceIdValue()).isEqualTo(2L);
            assertThat(tenantService.isActive()).isTrue();
            assertThat(tenantService.isNew()).isTrue();
            assertThat(tenantService.subscribedAt()).isEqualTo(NOW);
            assertThat(tenantService.createdAt()).isEqualTo(NOW);
            assertThat(tenantService.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("reconstitute로 TenantService를 재구성한다")
        void shouldReconstituteFromDatabase() {
            // given
            TenantService tenantService = TenantServiceFixture.create();

            // then
            assertThat(tenantService.tenantServiceIdValue())
                    .isEqualTo(TenantServiceFixture.defaultIdValue());
            assertThat(tenantService.tenantIdValue())
                    .isEqualTo(TenantServiceFixture.defaultTenantIdValue());
            assertThat(tenantService.serviceIdValue())
                    .isEqualTo(TenantServiceFixture.defaultServiceIdValue());
            assertThat(tenantService.isActive()).isTrue();
            assertThat(tenantService.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("TenantService 테넌트-서비스 관계 테스트")
    class RelationshipTests {

        @Test
        @DisplayName("다른 테넌트의 TenantService를 생성할 수 있다")
        void shouldCreateTenantServiceWithDifferentTenant() {
            // given
            String differentTenantId = "01941234-5678-7000-8000-different1234";

            // when
            TenantService tenantService = TenantServiceFixture.createWithTenant(differentTenantId);

            // then
            assertThat(tenantService.tenantIdValue()).isEqualTo(differentTenantId);
        }

        @Test
        @DisplayName("다른 서비스의 TenantService를 생성할 수 있다")
        void shouldCreateTenantServiceWithDifferentService() {
            // given
            Long differentServiceId = 99L;

            // when
            TenantService tenantService =
                    TenantServiceFixture.createWithService(differentServiceId);

            // then
            assertThat(tenantService.serviceIdValue()).isEqualTo(differentServiceId);
        }
    }

    @Nested
    @DisplayName("TenantService 상태 변경 테스트")
    class StatusChangeTests {

        @Test
        @DisplayName("ACTIVE 상태를 INACTIVE로 변경한다 (구독 해지)")
        void shouldChangeStatusToInactive() {
            // given
            TenantService tenantService = TenantServiceFixture.create();
            assertThat(tenantService.isActive()).isTrue();

            // when
            tenantService.deactivate(NOW);

            // then
            assertThat(tenantService.isActive()).isFalse();
            assertThat(tenantService.statusValue()).isEqualTo("INACTIVE");
            assertThat(tenantService.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("INACTIVE 상태를 ACTIVE로 변경한다 (구독 재활성화)")
        void shouldChangeStatusToActive() {
            // given
            TenantService tenantService = TenantServiceFixture.createInactive();
            assertThat(tenantService.isActive()).isFalse();

            // when
            tenantService.activate(NOW);

            // then
            assertThat(tenantService.isActive()).isTrue();
            assertThat(tenantService.statusValue()).isEqualTo("ACTIVE");
            assertThat(tenantService.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("ACTIVE 상태를 SUSPENDED로 변경한다 (구독 일시 중지)")
        void shouldChangeStatusToSuspended() {
            // given
            TenantService tenantService = TenantServiceFixture.create();
            assertThat(tenantService.isActive()).isTrue();

            // when
            tenantService.suspend(NOW);

            // then
            assertThat(tenantService.isActive()).isFalse();
            assertThat(tenantService.statusValue()).isEqualTo("SUSPENDED");
            assertThat(tenantService.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("changeStatus로 ACTIVE 상태로 변경한다")
        void shouldChangeStatusToActiveUsingChangeStatus() {
            // given
            TenantService tenantService = TenantServiceFixture.createInactive();

            // when
            tenantService.changeStatus(TenantServiceStatus.ACTIVE, NOW);

            // then
            assertThat(tenantService.isActive()).isTrue();
            assertThat(tenantService.statusValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("changeStatus로 INACTIVE 상태로 변경한다")
        void shouldChangeStatusToInactiveUsingChangeStatus() {
            // given
            TenantService tenantService = TenantServiceFixture.create();

            // when
            tenantService.changeStatus(TenantServiceStatus.INACTIVE, NOW);

            // then
            assertThat(tenantService.isActive()).isFalse();
            assertThat(tenantService.statusValue()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("changeStatus로 SUSPENDED 상태로 변경한다")
        void shouldChangeStatusToSuspendedUsingChangeStatus() {
            // given
            TenantService tenantService = TenantServiceFixture.create();

            // when
            tenantService.changeStatus(TenantServiceStatus.SUSPENDED, NOW);

            // then
            assertThat(tenantService.isActive()).isFalse();
            assertThat(tenantService.statusValue()).isEqualTo("SUSPENDED");
        }
    }

    @Nested
    @DisplayName("TenantService Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isActive는 ACTIVE 상태에서만 true를 반환한다")
        void isActiveShouldReturnTrueOnlyWhenActive() {
            // given
            TenantService activeService = TenantServiceFixture.create();
            TenantService inactiveService = TenantServiceFixture.createInactive();
            TenantService suspendedService = TenantServiceFixture.createSuspended();

            // then
            assertThat(activeService.isActive()).isTrue();
            assertThat(inactiveService.isActive()).isFalse();
            assertThat(suspendedService.isActive()).isFalse();
        }

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenNoId() {
            // given
            TenantService newService = TenantServiceFixture.createNew();
            TenantService existingService = TenantServiceFixture.create();

            // then
            assertThat(newService.isNew()).isTrue();
            assertThat(existingService.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("TenantService equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 tenantServiceId를 가진 TenantService는 동등하다")
        void shouldBeEqualWhenSameTenantServiceId() {
            // given
            TenantService service1 = TenantServiceFixture.create();
            TenantService service2 = TenantServiceFixture.create();

            // then
            assertThat(service1).isEqualTo(service2);
            assertThat(service1.hashCode()).isEqualTo(service2.hashCode());
        }

        @Test
        @DisplayName("ID가 없을 때 tenantId와 serviceId 조합으로 동등성을 판단한다")
        void shouldBeEqualBySameTenantAndServiceWhenNoId() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-test12345678");
            ServiceId serviceId = ServiceId.of(3L);

            TenantService service1 = TenantService.create(tenantId, serviceId, NOW);
            TenantService service2 = TenantService.create(tenantId, serviceId, NOW);

            // then
            assertThat(service1).isEqualTo(service2);
            assertThat(service1.hashCode()).isEqualTo(service2.hashCode());
        }

        @Test
        @DisplayName("다른 tenantId를 가진 TenantService는 동등하지 않다 (ID 없을 때)")
        void shouldNotBeEqualWhenDifferentTenantId() {
            // given
            TenantId tenantId1 = TenantId.of("01941234-5678-7000-8000-tenant111111");
            TenantId tenantId2 = TenantId.of("01941234-5678-7000-8000-tenant222222");
            ServiceId serviceId = ServiceId.of(1L);

            TenantService service1 = TenantService.create(tenantId1, serviceId, NOW);
            TenantService service2 = TenantService.create(tenantId2, serviceId, NOW);

            // then
            assertThat(service1).isNotEqualTo(service2);
        }

        @Test
        @DisplayName("다른 serviceId를 가진 TenantService는 동등하지 않다 (ID 없을 때)")
        void shouldNotBeEqualWhenDifferentServiceId() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-tenant111111");
            ServiceId serviceId1 = ServiceId.of(1L);
            ServiceId serviceId2 = ServiceId.of(2L);

            TenantService service1 = TenantService.create(tenantId, serviceId1, NOW);
            TenantService service2 = TenantService.create(tenantId, serviceId2, NOW);

            // then
            assertThat(service1).isNotEqualTo(service2);
        }
    }
}
