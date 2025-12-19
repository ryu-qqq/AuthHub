package com.ryuqq.authhub.domain.organization.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Organization Aggregate Root 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Organization Aggregate 테스트")
class OrganizationTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final ClockHolder FIXED_CLOCK_HOLDER =
            () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    private static final Clock FIXED_CLOCK = FIXED_CLOCK_HOLDER.clock();

    @Nested
    @DisplayName("create 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("새로운 Organization을 생성한다")
        void shouldCreateNewOrganization() {
            // given
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            OrganizationName name = OrganizationName.of("New Organization");

            // when
            Organization organization =
                    Organization.create(organizationId, tenantId, name, FIXED_CLOCK);

            // then
            assertThat(organization).isNotNull();
            assertThat(organization.isNew()).isFalse();
            assertThat(organization.nameValue()).isEqualTo("New Organization");
            assertThat(organization.isActive()).isTrue();
            assertThat(organization.organizationIdValue()).isNotNull();
            assertThat(organization.createdAt()).isEqualTo(FIXED_TIME);
            assertThat(organization.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("생성된 Organization은 ACTIVE 상태이다")
        void shouldCreateWithActiveStatus() {
            // given
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            OrganizationName name = OrganizationName.of("Active Organization");

            // when
            Organization organization =
                    Organization.create(organizationId, tenantId, name, FIXED_CLOCK);

            // then
            assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(organization.statusValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("organizationId가 null이면 예외 발생")
        void shouldThrowExceptionWhenOrganizationIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    Organization.create(
                                            null,
                                            TenantId.of(UUID.randomUUID()),
                                            OrganizationName.of("Test"),
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null일 수 없습니다");
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowExceptionWhenTenantIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    Organization.create(
                                            OrganizationId.forNew(UUID.randomUUID()),
                                            null,
                                            OrganizationName.of("Test"),
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null일 수 없습니다");
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void shouldThrowExceptionWhenNameIsNull() {
            assertThatThrownBy(
                            () ->
                                    Organization.create(
                                            OrganizationId.forNew(UUID.randomUUID()),
                                            TenantId.of(UUID.randomUUID()),
                                            null,
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드")
    class ReconstituteTest {

        @Test
        @DisplayName("DB에서 Organization을 재구성한다")
        void shouldReconstituteOrganizationFromDb() {
            // given
            UUID orgUuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();
            OrganizationId organizationId = OrganizationId.of(orgUuid);
            TenantId tenantId = TenantId.of(tenantUuid);
            OrganizationName name = OrganizationName.of("Test Organization");
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");

            // when
            Organization organization =
                    Organization.reconstitute(
                            organizationId,
                            tenantId,
                            name,
                            OrganizationStatus.ACTIVE,
                            createdAt,
                            updatedAt);

            // then
            assertThat(organization).isNotNull();
            assertThat(organization.isNew()).isFalse();
            assertThat(organization.organizationIdValue()).isEqualTo(orgUuid);
            assertThat(organization.tenantIdValue()).isEqualTo(tenantUuid);
            assertThat(organization.nameValue()).isEqualTo("Test Organization");
            assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(organization.createdAt()).isEqualTo(createdAt);
            assertThat(organization.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("organizationId가 null이면 예외 발생")
        void shouldThrowExceptionWhenOrganizationIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            null,
                                            TenantId.of(UUID.randomUUID()),
                                            OrganizationName.of("Test"),
                                            OrganizationStatus.ACTIVE,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null organizationId");
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외 발생")
        void shouldThrowExceptionWhenRequiredFieldIsNull() {
            OrganizationId id = OrganizationId.of(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());

            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            id,
                                            null,
                                            OrganizationName.of("Test"),
                                            OrganizationStatus.ACTIVE,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            id,
                                            tenantId,
                                            null,
                                            OrganizationStatus.ACTIVE,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            id,
                                            tenantId,
                                            OrganizationName.of("Test"),
                                            null,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationStatus는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            id,
                                            tenantId,
                                            OrganizationName.of("Test"),
                                            OrganizationStatus.ACTIVE,
                                            null,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("createdAt는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Organization.reconstitute(
                                            id,
                                            tenantId,
                                            OrganizationName.of("Test"),
                                            OrganizationStatus.ACTIVE,
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
        @DisplayName("활성 상태의 조직 이름을 변경한다")
        void shouldChangeNameForActiveOrganization() {
            // given
            Organization organization = OrganizationFixture.create();
            OrganizationName newName = OrganizationName.of("Changed Name");

            // when
            Organization changedOrganization = organization.changeName(newName, FIXED_CLOCK);

            // then
            assertThat(changedOrganization.nameValue()).isEqualTo("Changed Name");
            assertThat(changedOrganization.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("비활성 상태의 조직 이름을 변경한다")
        void shouldChangeNameForInactiveOrganization() {
            // given
            Organization organization = OrganizationFixture.createInactive();
            OrganizationName newName = OrganizationName.of("Changed Name");

            // when
            Organization changedOrganization = organization.changeName(newName, FIXED_CLOCK);

            // then
            assertThat(changedOrganization.nameValue()).isEqualTo("Changed Name");
        }

        @Test
        @DisplayName("삭제된 조직 이름 변경 시 예외 발생")
        void shouldThrowExceptionWhenChangingNameOfDeletedOrganization() {
            // given
            Organization organization = OrganizationFixture.createDeleted();
            OrganizationName newName = OrganizationName.of("New Name");

            // when/then
            assertThatThrownBy(() -> organization.changeName(newName, FIXED_CLOCK))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }

    @Nested
    @DisplayName("activate 비즈니스 메서드")
    class ActivateTest {

        @Test
        @DisplayName("비활성 상태의 조직을 활성화한다")
        void shouldActivateInactiveOrganization() {
            // given
            Organization organization = OrganizationFixture.createInactive();

            // when
            Organization activatedOrganization = organization.activate(FIXED_CLOCK);

            // then
            assertThat(activatedOrganization.isActive()).isTrue();
            assertThat(activatedOrganization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(activatedOrganization.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("삭제된 조직 활성화 시 예외 발생")
        void shouldThrowExceptionWhenActivatingDeletedOrganization() {
            // given
            Organization organization = OrganizationFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> organization.activate(FIXED_CLOCK))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }

    @Nested
    @DisplayName("deactivate 비즈니스 메서드")
    class DeactivateTest {

        @Test
        @DisplayName("활성 상태의 조직을 비활성화한다")
        void shouldDeactivateActiveOrganization() {
            // given
            Organization organization = OrganizationFixture.create();

            // when
            Organization deactivatedOrganization = organization.deactivate(FIXED_CLOCK);

            // then
            assertThat(deactivatedOrganization.isActive()).isFalse();
            assertThat(deactivatedOrganization.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
            assertThat(deactivatedOrganization.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("삭제된 조직 비활성화 시 예외 발생")
        void shouldThrowExceptionWhenDeactivatingDeletedOrganization() {
            // given
            Organization organization = OrganizationFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> organization.deactivate(FIXED_CLOCK))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }

    @Nested
    @DisplayName("delete 비즈니스 메서드")
    class DeleteTest {

        @Test
        @DisplayName("활성 상태의 조직을 삭제한다")
        void shouldDeleteActiveOrganization() {
            // given
            Organization organization = OrganizationFixture.create();

            // when
            Organization deletedOrganization = organization.delete(FIXED_CLOCK);

            // then
            assertThat(deletedOrganization.isDeleted()).isTrue();
            assertThat(deletedOrganization.getStatus()).isEqualTo(OrganizationStatus.DELETED);
            assertThat(deletedOrganization.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("비활성 상태의 조직을 삭제한다")
        void shouldDeleteInactiveOrganization() {
            // given
            Organization organization = OrganizationFixture.createInactive();

            // when
            Organization deletedOrganization = organization.delete(FIXED_CLOCK);

            // then
            assertThat(deletedOrganization.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("이미 삭제된 조직 삭제 시 예외 발생")
        void shouldThrowExceptionWhenDeletingAlreadyDeletedOrganization() {
            // given
            Organization organization = OrganizationFixture.createDeleted();

            // when/then
            assertThatThrownBy(() -> organization.delete(FIXED_CLOCK))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 ID를 가진 Organization은 동일하다")
        void shouldBeEqualWhenSameOrganizationId() {
            // given
            UUID uuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();
            Organization org1 =
                    Organization.reconstitute(
                            OrganizationId.of(uuid),
                            TenantId.of(tenantUuid),
                            OrganizationName.of("Org1"),
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Organization org2 =
                    Organization.reconstitute(
                            OrganizationId.of(uuid),
                            TenantId.of(tenantUuid),
                            OrganizationName.of("Org2"),
                            OrganizationStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(org1).isEqualTo(org2);
            assertThat(org1.hashCode()).isEqualTo(org2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Organization은 다르다")
        void shouldNotBeEqualWhenDifferentOrganizationId() {
            // given
            UUID tenantUuid = UUID.randomUUID();
            Organization org1 =
                    Organization.reconstitute(
                            OrganizationId.of(UUID.randomUUID()),
                            TenantId.of(tenantUuid),
                            OrganizationName.of("Organization"),
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Organization org2 =
                    Organization.reconstitute(
                            OrganizationId.of(UUID.randomUUID()),
                            TenantId.of(tenantUuid),
                            OrganizationName.of("Organization"),
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(org1).isNotEqualTo(org2);
        }

        @Test
        @DisplayName("다른 ID로 생성된 Organization은 서로 다르다")
        void shouldNotBeEqualWhenDifferentIdCreated() {
            // given
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            Organization org1 =
                    Organization.create(
                            OrganizationId.forNew(UUID.randomUUID()),
                            tenantId,
                            OrganizationName.of("Org1"),
                            FIXED_CLOCK);
            Organization org2 =
                    Organization.create(
                            OrganizationId.forNew(UUID.randomUUID()),
                            tenantId,
                            OrganizationName.of("Org2"),
                            FIXED_CLOCK);

            // then
            assertThat(org1).isNotEqualTo(org2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            Organization organization = OrganizationFixture.create();

            // then
            assertThat(organization).isEqualTo(organization);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            Organization organization = OrganizationFixture.create();

            // then
            assertThat(organization).isNotEqualTo(null);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Organization의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            Organization organization = OrganizationFixture.create();

            // when
            String toString = organization.toString();

            // then
            assertThat(toString).contains("Organization");
            assertThat(toString).contains("organizationId");
            assertThat(toString).contains("tenantId");
            assertThat(toString).contains("name");
            assertThat(toString).contains("status");
        }
    }
}
