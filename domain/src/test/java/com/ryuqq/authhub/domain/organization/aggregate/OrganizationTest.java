package com.ryuqq.authhub.domain.organization.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Organization Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Organization Aggregate 테스트")
class OrganizationTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("Organization 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 Organization을 성공적으로 생성한다")
        void shouldCreateOrganizationSuccessfully() {
            // given
            OrganizationId orgId = OrganizationId.forNew("01941234-5678-7000-8000-123456789999");
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789abc");
            OrganizationName name = OrganizationName.of("New Organization");

            // when
            Organization org = Organization.create(orgId, tenantId, name, NOW);

            // then
            assertThat(org.organizationIdValue()).isEqualTo(orgId.value());
            assertThat(org.tenantIdValue()).isEqualTo(tenantId.value());
            assertThat(org.nameValue()).isEqualTo(name.value());
            assertThat(org.isActive()).isTrue();
            assertThat(org.isDeleted()).isFalse();
            assertThat(org.createdAt()).isEqualTo(NOW);
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("reconstitute로 Organization을 재구성한다")
        void shouldReconstituteFromDatabase() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.organizationIdValue()).isEqualTo(OrganizationFixture.defaultIdString());
            assertThat(org.tenantIdValue()).isEqualTo(OrganizationFixture.defaultTenantIdString());
            assertThat(org.isActive()).isTrue();
            assertThat(org.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("Organization 테넌트 격리 테스트")
    class TenantIsolationTests {

        @Test
        @DisplayName("Organization은 tenantId를 변경할 수 없다 (불변)")
        void shouldNotChangeTenantId() {
            // given
            String originalTenantId = OrganizationFixture.defaultTenantIdString();
            Organization org = OrganizationFixture.create();

            // then - tenantId는 final이므로 변경 불가
            assertThat(org.tenantIdValue()).isEqualTo(originalTenantId);
        }

        @Test
        @DisplayName("다른 테넌트의 Organization을 생성할 수 있다")
        void shouldCreateOrganizationWithDifferentTenant() {
            // given
            String differentTenantId = "01941234-5678-7000-8000-different1234";

            // when
            Organization org = OrganizationFixture.createWithTenant(differentTenantId);

            // then
            assertThat(org.tenantIdValue()).isEqualTo(differentTenantId);
        }
    }

    @Nested
    @DisplayName("Organization 이름 변경 테스트")
    class NameChangeTests {

        @Test
        @DisplayName("Organization 이름을 변경한다")
        void shouldChangeName() {
            // given
            Organization org = OrganizationFixture.create();
            OrganizationName newName = OrganizationName.of("Changed Organization Name");

            // when
            org.changeName(newName, NOW);

            // then
            assertThat(org.nameValue()).isEqualTo("Changed Organization Name");
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Organization 상태 변경 테스트")
    class StatusChangeTests {

        @Test
        @DisplayName("ACTIVE 상태를 INACTIVE로 변경한다")
        void shouldChangeStatusToInactive() {
            // given
            Organization org = OrganizationFixture.create();
            assertThat(org.isActive()).isTrue();

            // when
            org.changeStatus(OrganizationStatus.INACTIVE, NOW);

            // then
            assertThat(org.isActive()).isFalse();
            assertThat(org.statusValue()).isEqualTo("INACTIVE");
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("INACTIVE 상태를 ACTIVE로 변경한다")
        void shouldChangeStatusToActive() {
            // given
            Organization org = OrganizationFixture.createInactive();
            assertThat(org.isActive()).isFalse();

            // when
            org.changeStatus(OrganizationStatus.ACTIVE, NOW);

            // then
            assertThat(org.isActive()).isTrue();
            assertThat(org.statusValue()).isEqualTo("ACTIVE");
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("이미 ACTIVE 상태인 Organization을 ACTIVE로 변경해도 updatedAt이 갱신된다")
        void shouldUpdateTimestampWhenChangingToSameActiveStatus() {
            // given
            Organization org = OrganizationFixture.create();
            Instant laterTime = NOW.plusSeconds(100);

            // when
            org.changeStatus(OrganizationStatus.ACTIVE, laterTime);

            // then
            assertThat(org.isActive()).isTrue();
            assertThat(org.updatedAt()).isEqualTo(laterTime);
        }

        @Test
        @DisplayName("이미 INACTIVE 상태인 Organization을 INACTIVE로 변경해도 updatedAt이 갱신된다")
        void shouldUpdateTimestampWhenChangingToSameInactiveStatus() {
            // given
            Organization org = OrganizationFixture.createInactive();
            Instant laterTime = NOW.plusSeconds(100);

            // when
            org.changeStatus(OrganizationStatus.INACTIVE, laterTime);

            // then
            assertThat(org.isActive()).isFalse();
            assertThat(org.updatedAt()).isEqualTo(laterTime);
        }
    }

    @Nested
    @DisplayName("Organization 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("Organization을 삭제(소프트 삭제)한다")
        void shouldDeleteOrganization() {
            // given
            Organization org = OrganizationFixture.create();

            // when
            org.delete(NOW);

            // then
            assertThat(org.isDeleted()).isTrue();
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("삭제된 Organization을 복원한다")
        void shouldRestoreOrganization() {
            // given
            Organization org = OrganizationFixture.createDeleted();
            assertThat(org.isDeleted()).isTrue();

            // when
            org.restore(NOW);

            // then
            assertThat(org.isDeleted()).isFalse();
            assertThat(org.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Organization Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isActive는 ACTIVE 상태에서 true를 반환한다")
        void isActiveShouldReturnTrueWhenActive() {
            // given
            Organization activeOrg = OrganizationFixture.create();
            Organization inactiveOrg = OrganizationFixture.createInactive();

            // then
            assertThat(activeOrg.isActive()).isTrue();
            assertThat(inactiveOrg.isActive()).isFalse();
        }

        @Test
        @DisplayName("statusValue는 상태 문자열을 반환한다")
        void statusValueShouldReturnStatusString() {
            // given
            Organization activeOrg = OrganizationFixture.create();
            Organization inactiveOrg = OrganizationFixture.createInactive();

            // then
            assertThat(activeOrg.statusValue()).isEqualTo("ACTIVE");
            assertThat(inactiveOrg.statusValue()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("Organization equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 organizationId를 가진 Organization은 동등하다")
        void shouldBeEqualWhenSameOrganizationId() {
            // given
            Organization org1 = OrganizationFixture.create();
            Organization org2 = OrganizationFixture.create();

            // then
            assertThat(org1).isEqualTo(org2);
            assertThat(org1.hashCode()).isEqualTo(org2.hashCode());
        }

        @Test
        @DisplayName("다른 organizationId를 가진 Organization은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentOrganizationId() {
            // given
            Organization org1 = OrganizationFixture.create();
            Organization org2 = OrganizationFixture.createNew();

            // then - createNew()는 다른 ID를 생성하므로 동등하지 않음
            assertThat(org1).isNotEqualTo(org2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            // given
            Organization org = OrganizationFixture.create();
            String differentType = "not an organization";

            // then
            assertThat(org).isNotEqualTo(differentType);
        }
    }

    @Nested
    @DisplayName("Organization Getter 메서드 테스트")
    class GetterMethodTests {

        @Test
        @DisplayName("getOrganizationId()는 organizationId를 반환한다")
        void getOrganizationIdShouldReturnOrganizationId() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.getOrganizationId()).isEqualTo(OrganizationFixture.defaultId());
        }

        @Test
        @DisplayName("getTenantId()는 tenantId를 반환한다")
        void getTenantIdShouldReturnTenantId() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.getTenantId()).isEqualTo(OrganizationFixture.defaultTenantId());
        }

        @Test
        @DisplayName("getName()은 name을 반환한다")
        void getNameShouldReturnName() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.getName().value()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("getStatus()는 status를 반환한다")
        void getStatusShouldReturnStatus() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }

        @Test
        @DisplayName("getDeletionStatus()는 deletionStatus를 반환한다")
        void getDeletionStatusShouldReturnDeletionStatus() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.getDeletionStatus()).isEqualTo(DeletionStatus.active());
        }

        @Test
        @DisplayName("createdAt()은 생성 시간을 반환한다")
        void createdAtShouldReturnCreatedAt() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.createdAt()).isEqualTo(OrganizationFixture.fixedTime());
        }

        @Test
        @DisplayName("updatedAt()은 수정 시간을 반환한다")
        void updatedAtShouldReturnUpdatedAt() {
            // given
            Organization org = OrganizationFixture.create();

            // then
            assertThat(org.updatedAt()).isEqualTo(OrganizationFixture.fixedTime());
        }
    }

    @Nested
    @DisplayName("Organization toString 테스트")
    class ToStringTests {

        @Test
        @DisplayName("toString()은 Organization 정보를 문자열로 반환한다")
        void toStringShouldReturnOrganizationInfo() {
            // given
            Organization org = OrganizationFixture.create();

            // when
            String result = org.toString();

            // then
            assertThat(result)
                    .contains("Organization")
                    .contains(OrganizationFixture.defaultIdString())
                    .contains(OrganizationFixture.defaultTenantIdString());
        }
    }
}
