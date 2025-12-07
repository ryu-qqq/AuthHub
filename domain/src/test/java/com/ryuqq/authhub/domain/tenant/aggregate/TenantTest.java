package com.ryuqq.authhub.domain.tenant.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tenant Aggregate Root 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Tenant Aggregate 테스트")
class TenantTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Clock FIXED_CLOCK = () -> FIXED_TIME;

    @Nested
    @DisplayName("create 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("새로운 Tenant를 생성한다")
        void shouldCreateNewTenant() {
            // given
            TenantName name = TenantName.of("New Tenant");

            // when
            Tenant tenant = Tenant.create(name, FIXED_CLOCK);

            // then
            assertThat(tenant).isNotNull();
            assertThat(tenant.isNew()).isTrue();
            assertThat(tenant.nameValue()).isEqualTo("New Tenant");
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.tenantIdValue()).isNull();
            assertThat(tenant.createdAt()).isEqualTo(FIXED_TIME);
            assertThat(tenant.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("생성된 Tenant는 ACTIVE 상태이다")
        void shouldCreateWithActiveStatus() {
            // given
            TenantName name = TenantName.of("Active Tenant");

            // when
            Tenant tenant = Tenant.create(name, FIXED_CLOCK);

            // then
            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void shouldThrowExceptionWhenNameIsNull() {
            assertThatThrownBy(() -> Tenant.create(null, FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드")
    class ReconstituteTest {

        @Test
        @DisplayName("DB에서 Tenant를 재구성한다")
        void shouldReconstituteTenantFromDb() {
            // given
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = TenantId.of(uuid);
            TenantName name = TenantName.of("Test Tenant");
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");

            // when
            Tenant tenant =
                    Tenant.reconstitute(tenantId, name, TenantStatus.ACTIVE, createdAt, updatedAt);

            // then
            assertThat(tenant).isNotNull();
            assertThat(tenant.isNew()).isFalse();
            assertThat(tenant.tenantIdValue()).isEqualTo(uuid);
            assertThat(tenant.nameValue()).isEqualTo("Test Tenant");
            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.createdAt()).isEqualTo(createdAt);
            assertThat(tenant.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowExceptionWhenTenantIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            null,
                                            TenantName.of("Test"),
                                            TenantStatus.ACTIVE,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null tenantId");
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외 발생")
        void shouldThrowExceptionWhenRequiredFieldIsNull() {
            TenantId id = TenantId.forNew();

            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            id, null, TenantStatus.ACTIVE, FIXED_TIME, FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            id,
                                            TenantName.of("Test"),
                                            null,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantStatus는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            id,
                                            TenantName.of("Test"),
                                            TenantStatus.ACTIVE,
                                            null,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("createdAt는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Tenant.reconstitute(
                                            id,
                                            TenantName.of("Test"),
                                            TenantStatus.ACTIVE,
                                            FIXED_TIME,
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("updatedAt는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("changeName 비즈니스 메서드")
    class ChangeNameTest {

        @Test
        @DisplayName("활성 상태의 테넌트 이름을 변경한다")
        void shouldChangeNameForActiveTenant() {
            // given
            Tenant tenant = TenantFixture.create();
            TenantName newName = TenantName.of("Changed Name");

            // when
            Tenant changedTenant = tenant.changeName(newName, FIXED_CLOCK);

            // then
            assertThat(changedTenant.nameValue()).isEqualTo("Changed Name");
            assertThat(changedTenant.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("비활성 상태의 테넌트 이름을 변경한다")
        void shouldChangeNameForInactiveTenant() {
            // given
            Tenant tenant = TenantFixture.createInactive();
            TenantName newName = TenantName.of("Changed Name");

            // when
            Tenant changedTenant = tenant.changeName(newName, FIXED_CLOCK);

            // then
            assertThat(changedTenant.nameValue()).isEqualTo("Changed Name");
        }

        @Test
        @DisplayName("삭제된 테넌트 이름 변경 시 예외 발생")
        void shouldThrowExceptionWhenChangingNameOfDeletedTenant() {
            // given
            Tenant tenant = TenantFixture.createDeleted();
            TenantName newName = TenantName.of("New Name");

            // when/then
            assertThatThrownBy(() -> tenant.changeName(newName, FIXED_CLOCK))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }

    @Nested
    @DisplayName("activate 비즈니스 메서드")
    class ActivateTest {

        @Test
        @DisplayName("비활성 상태의 테넌트를 활성화한다")
        void shouldActivateInactiveTenant() {
            // given
            Tenant tenant = TenantFixture.createInactive();

            // when
            Tenant activatedTenant = tenant.activate(FIXED_CLOCK);

            // then
            assertThat(activatedTenant.isActive()).isTrue();
            assertThat(activatedTenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(activatedTenant.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("삭제된 테넌트 활성화 시 예외 발생")
        void shouldThrowExceptionWhenActivatingDeletedTenant() {
            // given
            Tenant tenant = TenantFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> tenant.activate(FIXED_CLOCK))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }

    @Nested
    @DisplayName("deactivate 비즈니스 메서드")
    class DeactivateTest {

        @Test
        @DisplayName("활성 상태의 테넌트를 비활성화한다")
        void shouldDeactivateActiveTenant() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            Tenant deactivatedTenant = tenant.deactivate(FIXED_CLOCK);

            // then
            assertThat(deactivatedTenant.isActive()).isFalse();
            assertThat(deactivatedTenant.getStatus()).isEqualTo(TenantStatus.INACTIVE);
            assertThat(deactivatedTenant.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("삭제된 테넌트 비활성화 시 예외 발생")
        void shouldThrowExceptionWhenDeactivatingDeletedTenant() {
            // given
            Tenant tenant = TenantFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> tenant.deactivate(FIXED_CLOCK))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }

    @Nested
    @DisplayName("delete 비즈니스 메서드")
    class DeleteTest {

        @Test
        @DisplayName("활성 상태의 테넌트를 삭제한다")
        void shouldDeleteActiveTenant() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            Tenant deletedTenant = tenant.delete(FIXED_CLOCK);

            // then
            assertThat(deletedTenant.isDeleted()).isTrue();
            assertThat(deletedTenant.getStatus()).isEqualTo(TenantStatus.DELETED);
            assertThat(deletedTenant.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("비활성 상태의 테넌트를 삭제한다")
        void shouldDeleteInactiveTenant() {
            // given
            Tenant tenant = TenantFixture.createInactive();

            // when
            Tenant deletedTenant = tenant.delete(FIXED_CLOCK);

            // then
            assertThat(deletedTenant.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("이미 삭제된 테넌트 삭제 시 예외 발생")
        void shouldThrowExceptionWhenDeletingAlreadyDeletedTenant() {
            // given
            Tenant tenant = TenantFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> tenant.delete(FIXED_CLOCK))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 ID를 가진 Tenant는 동일하다")
        void shouldBeEqualWhenSameTenantId() {
            // given
            UUID uuid = UUID.randomUUID();
            Tenant tenant1 =
                    Tenant.reconstitute(
                            TenantId.of(uuid),
                            TenantName.of("Tenant1"),
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Tenant tenant2 =
                    Tenant.reconstitute(
                            TenantId.of(uuid),
                            TenantName.of("Tenant2"),
                            TenantStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(tenant1).isEqualTo(tenant2);
            assertThat(tenant1.hashCode()).isEqualTo(tenant2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Tenant는 다르다")
        void shouldNotBeEqualWhenDifferentTenantId() {
            // given
            Tenant tenant1 =
                    Tenant.reconstitute(
                            TenantId.of(UUID.randomUUID()),
                            TenantName.of("Tenant"),
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Tenant tenant2 =
                    Tenant.reconstitute(
                            TenantId.of(UUID.randomUUID()),
                            TenantName.of("Tenant"),
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(tenant1).isNotEqualTo(tenant2);
        }

        @Test
        @DisplayName("ID가 null인 Tenant는 서로 다르다")
        void shouldNotBeEqualWhenIdIsNull() {
            // given
            Tenant tenant1 = Tenant.create(TenantName.of("Tenant1"), FIXED_CLOCK);
            Tenant tenant2 = Tenant.create(TenantName.of("Tenant2"), FIXED_CLOCK);

            // then
            assertThat(tenant1).isNotEqualTo(tenant2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            Tenant tenant = TenantFixture.create();

            // then
            assertThat(tenant).isEqualTo(tenant);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            Tenant tenant = TenantFixture.create();

            // then
            assertThat(tenant).isNotEqualTo(null);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Tenant의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            String toString = tenant.toString();

            // then
            assertThat(toString).contains("Tenant");
            assertThat(toString).contains("tenantId");
            assertThat(toString).contains("name");
            assertThat(toString).contains("status");
        }
    }
}
