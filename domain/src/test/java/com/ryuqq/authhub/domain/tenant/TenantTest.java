package com.ryuqq.authhub.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Tenant Aggregate 테스트")
class TenantTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");
    private final Clock laterClock = () -> Instant.parse("2025-11-24T01:00:00Z");

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("forNew - 새 Tenant 생성 성공")
        void forNew_validData_success() {
            // Given
            TenantName tenantName = TenantName.of("Test Tenant");

            // When
            Tenant tenant = Tenant.forNew(tenantName, clock);

            // Then
            assertThat(tenant).isNotNull();
            assertThat(tenant.tenantIdValue()).isNull(); // 새 객체는 ID가 null
            assertThat(tenant.tenantNameValue()).isEqualTo("Test Tenant");
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(tenant.isNew()).isTrue();
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.createdAt()).isEqualTo(clock.now());
            assertThat(tenant.updatedAt()).isEqualTo(clock.now());
        }

        @Test
        @DisplayName("forNew - null tenantName으로 생성 시 예외 발생")
        void forNew_nullTenantName_throwsException() {
            // Given
            TenantName nullTenantName = null;

            // When & Then
            assertThatThrownBy(() -> Tenant.forNew(nullTenantName, clock))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null일 수 없습니다");
        }

        @Test
        @DisplayName("of - 모든 필드 지정하여 Tenant 생성 성공")
        void of_validData_success() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            TenantStatus tenantStatus = TenantStatus.ACTIVE;
            Instant createdAt = clock.now();
            Instant updatedAt = laterClock.now();

            // When
            Tenant tenant = Tenant.of(tenantId, tenantName, tenantStatus, createdAt, updatedAt);

            // Then
            assertThat(tenant).isNotNull();
            assertThat(tenant.tenantIdValue()).isEqualTo(1L);
            assertThat(tenant.tenantNameValue()).isEqualTo("Test Tenant");
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(tenant.isNew()).isFalse();
            assertThat(tenant.createdAt()).isEqualTo(createdAt);
            assertThat(tenant.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("reconstitute - DB에서 Tenant 재구성 성공")
        void reconstitute_validData_success() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            TenantStatus tenantStatus = TenantStatus.ACTIVE;
            Instant createdAt = clock.now();
            Instant updatedAt = laterClock.now();

            // When
            Tenant tenant =
                    Tenant.reconstitute(tenantId, tenantName, tenantStatus, createdAt, updatedAt);

            // Then
            assertThat(tenant).isNotNull();
            assertThat(tenant.tenantIdValue()).isEqualTo(1L);
            assertThat(tenant.tenantNameValue()).isEqualTo("Test Tenant");
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(tenant.isNew()).isFalse();
            assertThat(tenant.createdAt()).isEqualTo(createdAt);
            assertThat(tenant.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("reconstitute - null tenantId로 재구성 시 예외 발생")
        void reconstitute_nullTenantId_throwsException() {
            // Given
            TenantId nullTenantId = null;
            TenantName tenantName = TenantName.of("Test Tenant");

            // When & Then
            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            nullTenantId,
                                            tenantName,
                                            TenantStatus.ACTIVE,
                                            clock.now(),
                                            clock.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null tenantId");
        }

        @Test
        @DisplayName("null tenantName으로 생성 시 예외 발생")
        void nullTenantName_throwsException() {
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
        @DisplayName("null tenantStatus로 생성 시 예외 발생")
        void nullTenantStatus_throwsException() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            TenantStatus nullStatus = null;

            // When & Then
            assertThatThrownBy(
                            () ->
                                    Tenant.of(
                                            tenantId,
                                            tenantName,
                                            nullStatus,
                                            clock.now(),
                                            clock.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantStatus는 null일 수 없습니다");
        }

        @Test
        @DisplayName("null createdAt으로 생성 시 예외 발생")
        void nullCreatedAt_throwsException() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            Instant nullCreatedAt = null;

            // When & Then
            assertThatThrownBy(
                            () ->
                                    Tenant.of(
                                            tenantId,
                                            tenantName,
                                            TenantStatus.ACTIVE,
                                            nullCreatedAt,
                                            clock.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("createdAt는 null일 수 없습니다");
        }

        @Test
        @DisplayName("null updatedAt으로 생성 시 예외 발생")
        void nullUpdatedAt_throwsException() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            Instant nullUpdatedAt = null;

            // When & Then
            assertThatThrownBy(
                            () ->
                                    Tenant.of(
                                            tenantId,
                                            tenantName,
                                            TenantStatus.ACTIVE,
                                            clock.now(),
                                            nullUpdatedAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("updatedAt는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("비즈니스 메서드 테스트")
    class BusinessMethodTests {

        @Test
        @DisplayName("activate - INACTIVE에서 ACTIVE로 활성화 성공")
        void activate_fromInactive_success() {
            // Given
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();

            // When
            Tenant activatedTenant = inactiveTenant.activate(laterClock);

            // Then
            assertThat(activatedTenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(activatedTenant.isActive()).isTrue();
            assertThat(activatedTenant.updatedAt()).isEqualTo(laterClock.now());
            assertThat(activatedTenant.createdAt())
                    .isEqualTo(inactiveTenant.createdAt()); // 생성시간은 변경되지 않음
        }

        @Test
        @DisplayName("activate - DELETED 상태에서 활성화 시 예외 발생")
        void activate_fromDeleted_throwsException() {
            // Given
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThatThrownBy(() -> deletedTenant.activate(laterClock))
                    .isInstanceOf(InvalidTenantStateException.class)
                    .hasMessageContaining("Invalid tenant status");
        }

        @Test
        @DisplayName("deactivate - ACTIVE에서 INACTIVE로 비활성화 성공")
        void deactivate_fromActive_success() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();

            // When
            Tenant deactivatedTenant = activeTenant.deactivate(laterClock);

            // Then
            assertThat(deactivatedTenant.statusValue()).isEqualTo("INACTIVE");
            assertThat(deactivatedTenant.isActive()).isFalse();
            assertThat(deactivatedTenant.updatedAt()).isEqualTo(laterClock.now());
            assertThat(deactivatedTenant.createdAt()).isEqualTo(activeTenant.createdAt());
        }

        @Test
        @DisplayName("deactivate - DELETED 상태에서 비활성화 시 예외 발생")
        void deactivate_fromDeleted_throwsException() {
            // Given
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThatThrownBy(() -> deletedTenant.deactivate(laterClock))
                    .isInstanceOf(InvalidTenantStateException.class)
                    .hasMessageContaining("Invalid tenant status");
        }

        @Test
        @DisplayName("delete - ACTIVE에서 DELETED로 삭제 성공")
        void delete_fromActive_success() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();

            // When
            Tenant deletedTenant = activeTenant.delete(laterClock);

            // Then
            assertThat(deletedTenant.statusValue()).isEqualTo("DELETED");
            assertThat(deletedTenant.isDeleted()).isTrue();
            assertThat(deletedTenant.updatedAt()).isEqualTo(laterClock.now());
            assertThat(deletedTenant.createdAt()).isEqualTo(activeTenant.createdAt());
        }

        @Test
        @DisplayName("delete - INACTIVE에서 DELETED로 삭제 성공")
        void delete_fromInactive_success() {
            // Given
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();

            // When
            Tenant deletedTenant = inactiveTenant.delete(laterClock);

            // Then
            assertThat(deletedTenant.statusValue()).isEqualTo("DELETED");
            assertThat(deletedTenant.isDeleted()).isTrue();
            assertThat(deletedTenant.updatedAt()).isEqualTo(laterClock.now());
        }

        @Test
        @DisplayName("delete - 이미 DELETED 상태에서 삭제 시 예외 발생")
        void delete_alreadyDeleted_throwsException() {
            // Given
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThatThrownBy(() -> deletedTenant.delete(laterClock))
                    .isInstanceOf(InvalidTenantStateException.class)
                    .hasMessageContaining("Invalid tenant status");
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드 테스트")
    class HelperMethodTests {

        @Test
        @DisplayName("tenantIdValue - Tenant ID Long 값 반환")
        void tenantIdValue_returnsLongValue() {
            // Given
            TenantId tenantId = TenantId.of(123L);
            Tenant tenant = TenantFixture.aTenant(tenantId);

            // When & Then
            assertThat(tenant.tenantIdValue()).isEqualTo(123L);
        }

        @Test
        @DisplayName("tenantIdValue - 새 Tenant의 경우 null 반환")
        void tenantIdValue_newTenant_returnsNull() {
            // Given
            Tenant newTenant = TenantFixture.aNewTenant();

            // When & Then
            assertThat(newTenant.tenantIdValue()).isNull();
        }

        @Test
        @DisplayName("tenantNameValue - Tenant Name String 값 반환")
        void tenantNameValue_returnsStringValue() {
            // Given
            TenantName tenantName = TenantName.of("My Tenant");
            Tenant tenant = TenantFixture.aTenant(tenantName);

            // When & Then
            assertThat(tenant.tenantNameValue()).isEqualTo("My Tenant");
        }

        @Test
        @DisplayName("statusValue - TenantStatus name 반환")
        void statusValue_returnsStatusName() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThat(activeTenant.statusValue()).isEqualTo("ACTIVE");
            assertThat(inactiveTenant.statusValue()).isEqualTo("INACTIVE");
            assertThat(deletedTenant.statusValue()).isEqualTo("DELETED");
        }

        @Test
        @DisplayName("isNew - 새 Tenant 여부 확인")
        void isNew_checksNewTenant() {
            // Given
            Tenant newTenant = TenantFixture.aNewTenant();
            Tenant existingTenant = TenantFixture.aTenant();

            // When & Then
            assertThat(newTenant.isNew()).isTrue();
            assertThat(existingTenant.isNew()).isFalse();
        }

        @Test
        @DisplayName("createdAt - 생성 시간 반환")
        void createdAt_returnsCreationTime() {
            // Given
            Instant expectedCreatedAt = clock.now();
            Tenant tenant = TenantFixture.builder().createdAt(expectedCreatedAt).build();

            // When & Then
            assertThat(tenant.createdAt()).isEqualTo(expectedCreatedAt);
        }

        @Test
        @DisplayName("updatedAt - 수정 시간 반환")
        void updatedAt_returnsUpdateTime() {
            // Given
            Instant expectedUpdatedAt = laterClock.now();
            Tenant tenant = TenantFixture.builder().updatedAt(expectedUpdatedAt).build();

            // When & Then
            assertThat(tenant.updatedAt()).isEqualTo(expectedUpdatedAt);
        }

        @Test
        @DisplayName("isActive - 활성 상태 확인")
        void isActive_checksActiveStatus() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThat(activeTenant.isActive()).isTrue();
            assertThat(inactiveTenant.isActive()).isFalse();
            assertThat(deletedTenant.isActive()).isFalse();
        }

        @Test
        @DisplayName("isDeleted - 삭제 상태 확인")
        void isDeleted_checksDeletedStatus() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();
            Tenant deletedTenant = TenantFixture.aDeletedTenant();

            // When & Then
            assertThat(activeTenant.isDeleted()).isFalse();
            assertThat(inactiveTenant.isDeleted()).isFalse();
            assertThat(deletedTenant.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("Legacy Getter 테스트")
    class LegacyGetterTests {

        @Test
        @DisplayName("getTenantId - TenantId 객체 반환")
        void getTenantId_returnsTenantIdObject() {
            // Given
            TenantId expectedTenantId = TenantId.of(1L);
            Tenant tenant = TenantFixture.aTenant(expectedTenantId);

            // When & Then
            assertThat(tenant.getTenantId()).isEqualTo(expectedTenantId);
        }

        @Test
        @DisplayName("getTenantName - TenantName 객체 반환")
        void getTenantName_returnsTenantNameObject() {
            // Given
            TenantName expectedTenantName = TenantName.of("Test Tenant");
            Tenant tenant = TenantFixture.aTenant(expectedTenantName);

            // When & Then
            assertThat(tenant.getTenantName()).isEqualTo(expectedTenantName);
        }

        @Test
        @DisplayName("getTenantStatus - TenantStatus 객체 반환")
        void getTenantStatus_returnsTenantStatusObject() {
            // Given
            Tenant activeTenant = TenantFixture.aTenant();
            Tenant inactiveTenant = TenantFixture.anInactiveTenant();

            // When & Then
            assertThat(activeTenant.getTenantStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(inactiveTenant.getTenantStatus()).isEqualTo(TenantStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("Object 메서드 테스트")
    class ObjectMethodTests {

        @Test
        @DisplayName("equals - 동일한 객체는 같음")
        void equals_sameObject_returnsTrue() {
            // Given
            Tenant tenant = TenantFixture.aTenant();

            // When & Then
            assertThat(tenant.equals(tenant)).isTrue();
        }

        @Test
        @DisplayName("equals - 모든 필드가 같으면 같음")
        void equals_sameFields_returnsTrue() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            Instant createdAt = clock.now();
            Instant updatedAt = laterClock.now();

            Tenant tenant1 =
                    Tenant.of(tenantId, tenantName, TenantStatus.ACTIVE, createdAt, updatedAt);
            Tenant tenant2 =
                    Tenant.of(tenantId, tenantName, TenantStatus.ACTIVE, createdAt, updatedAt);

            // When & Then
            assertThat(tenant1).isEqualTo(tenant2);
        }

        @Test
        @DisplayName("equals - null과 비교하면 다름")
        @SuppressWarnings("PMD.EqualsNull")
        void equals_withNull_returnsFalse() {
            // Given
            Tenant tenant = TenantFixture.aTenant();

            // When & Then
            assertThat(tenant.equals(null)).isFalse();
        }

        @Test
        @DisplayName("equals - 다른 클래스와 비교하면 다름")
        void equals_withDifferentClass_returnsFalse() {
            // Given
            Tenant tenant = TenantFixture.aTenant();
            String otherObject = "not a tenant";

            // When & Then
            assertThat(tenant.equals(otherObject)).isFalse();
        }

        @Test
        @DisplayName("equals - 다른 필드값이면 다름")
        void equals_differentFields_returnsFalse() {
            // Given
            Tenant tenant1 = TenantFixture.aTenant(TenantId.of(1L));
            Tenant tenant2 = TenantFixture.aTenant(TenantId.of(2L));

            // When & Then
            assertThat(tenant1).isNotEqualTo(tenant2);
        }

        @Test
        @DisplayName("hashCode - 같은 객체는 같은 해시코드")
        void hashCode_sameObject_sameHashCode() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            Instant createdAt = clock.now();
            Instant updatedAt = laterClock.now();

            Tenant tenant1 =
                    Tenant.of(tenantId, tenantName, TenantStatus.ACTIVE, createdAt, updatedAt);
            Tenant tenant2 =
                    Tenant.of(tenantId, tenantName, TenantStatus.ACTIVE, createdAt, updatedAt);

            // When & Then
            assertThat(tenant1.hashCode()).isEqualTo(tenant2.hashCode());
        }

        @Test
        @DisplayName("toString - 모든 필드 포함한 문자열 반환")
        void toString_containsAllFields() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            TenantName tenantName = TenantName.of("Test Tenant");
            Tenant tenant =
                    Tenant.of(tenantId, tenantName, TenantStatus.ACTIVE, clock.now(), clock.now());

            // When
            String result = tenant.toString();

            // Then
            assertThat(result).contains("Tenant{");
            assertThat(result).contains("tenantId=");
            assertThat(result).contains("tenantName=");
            assertThat(result).contains("tenantStatus=");
            assertThat(result).contains("createdAt=");
            assertThat(result).contains("updatedAt=");
        }
    }
}
