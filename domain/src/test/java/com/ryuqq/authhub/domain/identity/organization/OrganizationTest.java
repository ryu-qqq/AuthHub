package com.ryuqq.authhub.domain.identity.organization;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Organization Aggregate Root 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Organization Aggregate 단위 테스트")
class OrganizationTest {

    @Nested
    @DisplayName("Factory Method 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("createSeller()로 SELLER 타입 조직을 생성할 수 있다")
        void testCreateSeller() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Store");

            // when
            Organization organization = Organization.createSeller(ownerId, name);

            // then
            assertThat(organization).isNotNull();
            assertThat(organization.getId()).isNotNull();
            assertThat(organization.getOwnerId()).isEqualTo(ownerId);
            assertThat(organization.getName()).isEqualTo(name);
            assertThat(organization.getType()).isEqualTo(OrganizationType.SELLER);
            assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(organization.isSeller()).isTrue();
            assertThat(organization.isCompany()).isFalse();
            assertThat(organization.isActive()).isTrue();
            assertThat(organization.getCreatedAt()).isNotNull();
            assertThat(organization.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("createCompany()로 COMPANY 타입 조직을 생성할 수 있다")
        void testCreateCompany() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Corporation");

            // when
            Organization organization = Organization.createCompany(ownerId, name);

            // then
            assertThat(organization).isNotNull();
            assertThat(organization.getId()).isNotNull();
            assertThat(organization.getOwnerId()).isEqualTo(ownerId);
            assertThat(organization.getName()).isEqualTo(name);
            assertThat(organization.getType()).isEqualTo(OrganizationType.COMPANY);
            assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(organization.isSeller()).isFalse();
            assertThat(organization.isCompany()).isTrue();
            assertThat(organization.isActive()).isTrue();
            assertThat(organization.getCreatedAt()).isNotNull();
            assertThat(organization.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("createSeller()에 null ownerId를 전달하면 예외가 발생한다")
        void testCreateSellerWithNullOwnerId() {
            // given
            OrganizationName name = new OrganizationName("Nike Store");

            // when & then
            assertThatThrownBy(() -> Organization.createSeller(null, name))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createSeller()에 null name을 전달하면 예외가 발생한다")
        void testCreateSellerWithNullName() {
            // given
            UserId ownerId = UserId.newId();

            // when & then
            assertThatThrownBy(() -> Organization.createSeller(ownerId, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("reconstruct() 테스트")
    class ReconstructTest {

        @Test
        @DisplayName("reconstruct()로 기존 데이터로부터 Organization을 재구성할 수 있다")
        void testReconstruct() {
            // given
            OrganizationId id = OrganizationId.newId();
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Store");
            OrganizationType type = OrganizationType.SELLER;
            OrganizationStatus status = OrganizationStatus.ACTIVE;
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            // when
            Organization organization = Organization.reconstruct(
                    id, ownerId, name, type, status, createdAt, updatedAt
            );

            // then
            assertThat(organization).isNotNull();
            assertThat(organization.getId()).isEqualTo(id);
            assertThat(organization.getOwnerId()).isEqualTo(ownerId);
            assertThat(organization.getName()).isEqualTo(name);
            assertThat(organization.getType()).isEqualTo(type);
            assertThat(organization.getStatus()).isEqualTo(status);
            assertThat(organization.getCreatedAt()).isEqualTo(createdAt);
            assertThat(organization.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("Law of Demeter 준수 테스트")
    class LawOfDemeterTest {

        @Test
        @DisplayName("getNameValue()로 조직명 문자열을 직접 반환할 수 있다")
        void testGetNameValue() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Store");
            Organization organization = Organization.createSeller(ownerId, name);

            // when
            String nameValue = organization.getNameValue();

            // then
            assertThat(nameValue).isEqualTo("Nike Store");
        }

        @Test
        @DisplayName("getTypeDescription()로 타입 설명을 직접 반환할 수 있다")
        void testGetTypeDescription() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Store");
            Organization organization = Organization.createSeller(ownerId, name);

            // when
            String typeDescription = organization.getTypeDescription();

            // then
            assertThat(typeDescription).isEqualTo("판매자");
        }

        @Test
        @DisplayName("getStatusDescription()로 상태 설명을 직접 반환할 수 있다")
        void testGetStatusDescription() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike Store");
            Organization organization = Organization.createSeller(ownerId, name);

            // when
            String statusDescription = organization.getStatusDescription();

            // then
            assertThat(statusDescription).isEqualTo("활성");
        }
    }

    @Nested
    @DisplayName("상태 전환 테스트")
    class StatusTransitionTest {

        @Test
        @DisplayName("활성 조직을 정지 상태로 전환할 수 있다")
        void testSuspend() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            );

            // when
            Organization suspended = organization.suspend();

            // then
            assertThat(suspended.getStatus()).isEqualTo(OrganizationStatus.SUSPENDED);
            assertThat(suspended.isSuspended()).isTrue();
            assertThat(suspended.isActive()).isFalse();
            assertThat(suspended.isOperational()).isFalse();
            assertThat(suspended.getUpdatedAt()).isAfter(organization.getUpdatedAt());
        }

        @Test
        @DisplayName("정지 상태가 아닌 조직을 정지하려 하면 예외가 발생한다")
        void testSuspendNonActiveOrganization() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            ).delete();

            // when & then
            assertThatThrownBy(() -> organization.suspend())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only active organizations");
        }

        @Test
        @DisplayName("정지 조직을 활성 상태로 전환할 수 있다")
        void testActivate() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            ).suspend();

            // when
            Organization activated = organization.activate();

            // then
            assertThat(activated.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(activated.isActive()).isTrue();
            assertThat(activated.isSuspended()).isFalse();
            assertThat(activated.isOperational()).isTrue();
            assertThat(activated.getUpdatedAt()).isAfter(organization.getUpdatedAt());
        }

        @Test
        @DisplayName("정지 상태가 아닌 조직을 활성화하려 하면 예외가 발생한다")
        void testActivateNonSuspendedOrganization() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            );

            // when & then
            assertThatThrownBy(() -> organization.activate())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only suspended organizations");
        }

        @Test
        @DisplayName("조직을 삭제 상태로 전환할 수 있다")
        void testDelete() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            );

            // when
            Organization deleted = organization.delete();

            // then
            assertThat(deleted.getStatus()).isEqualTo(OrganizationStatus.DELETED);
            assertThat(deleted.isDeleted()).isTrue();
            assertThat(deleted.isActive()).isFalse();
            assertThat(deleted.isOperational()).isFalse();
            assertThat(deleted.getUpdatedAt()).isAfter(organization.getUpdatedAt());
        }

        @Test
        @DisplayName("이미 삭제된 조직을 다시 삭제하려 하면 예외가 발생한다")
        void testDeleteAlreadyDeletedOrganization() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            ).delete();

            // when & then
            assertThatThrownBy(() -> organization.delete())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already deleted");
        }
    }

    @Nested
    @DisplayName("조직명 변경 테스트")
    class UpdateNameTest {

        @Test
        @DisplayName("조직명을 변경할 수 있다")
        void testUpdateName() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            );
            OrganizationName newName = new OrganizationName("Nike Official Store");

            // when
            Organization updated = organization.updateName(newName);

            // then
            assertThat(updated.getName()).isEqualTo(newName);
            assertThat(updated.getNameValue()).isEqualTo("Nike Official Store");
            assertThat(updated.getUpdatedAt()).isAfter(organization.getUpdatedAt());
        }

        @Test
        @DisplayName("같은 조직명으로 변경하려 하면 현재 인스턴스를 반환한다")
        void testUpdateNameWithSameName() {
            // given
            OrganizationName name = new OrganizationName("Nike Store");
            Organization organization = Organization.createSeller(UserId.newId(), name);

            // when
            Organization updated = organization.updateName(name);

            // then
            assertThat(updated).isSameAs(organization);
        }

        @Test
        @DisplayName("삭제된 조직의 조직명을 변경하려 하면 예외가 발생한다")
        void testUpdateNameOfDeletedOrganization() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            ).delete();
            OrganizationName newName = new OrganizationName("Nike Official Store");

            // when & then
            assertThatThrownBy(() -> organization.updateName(newName))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot update name of a deleted organization");
        }

        @Test
        @DisplayName("updateName()에 null을 전달하면 예외가 발생한다")
        void testUpdateNameWithNull() {
            // given
            Organization organization = Organization.createSeller(
                    UserId.newId(),
                    new OrganizationName("Nike Store")
            );

            // when & then
            assertThatThrownBy(() -> organization.updateName(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("동등성 및 해시코드 테스트")
    class EqualityAndHashCodeTest {

        @Test
        @DisplayName("같은 ID를 가진 Organization은 동등하다")
        void testEquality() {
            // given
            OrganizationId id = OrganizationId.newId();
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike");
            Instant now = Instant.now();

            Organization org1 = Organization.reconstruct(
                    id, ownerId, name, OrganizationType.SELLER, OrganizationStatus.ACTIVE, now, now
            );
            Organization org2 = Organization.reconstruct(
                    id, ownerId, name, OrganizationType.SELLER, OrganizationStatus.ACTIVE, now, now
            );

            // when & then
            assertThat(org1).isEqualTo(org2);
            assertThat(org1.hashCode()).isEqualTo(org2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Organization은 동등하지 않다")
        void testInequality() {
            // given
            UserId ownerId = UserId.newId();
            OrganizationName name = new OrganizationName("Nike");

            Organization org1 = Organization.createSeller(ownerId, name);
            Organization org2 = Organization.createSeller(ownerId, name);

            // when & then
            assertThat(org1).isNotEqualTo(org2);
        }
    }
}
