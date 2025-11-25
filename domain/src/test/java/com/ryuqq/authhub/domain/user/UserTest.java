package com.ryuqq.authhub.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User Aggregate 테스트")
class UserTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Test
    @DisplayName("유효한 데이터로 User 생성 성공")
    void shouldCreateUserWithValidData() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());

        // When
        User user = UserFixture.aUser(userId);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.userIdValue()).isEqualTo(userId.value());
        assertThat(user.tenantIdValue()).isNotNull();
        assertThat(user.userTypeValue()).isEqualTo("PUBLIC");
        assertThat(user.statusValue()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("null tenantId로 User 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantId() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());
        TenantId nullTenantId = null;
        OrganizationId organizationId = OrganizationId.of(100L);
        UserType userType = UserType.PUBLIC;
        UserStatus userStatus = UserStatus.ACTIVE;

        // When & Then
        assertThatThrownBy(
                        () ->
                                User.of(
                                        userId,
                                        nullTenantId,
                                        organizationId,
                                        userType,
                                        userStatus,
                                        clock.now(),
                                        clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null UserStatus로 User 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullUserStatus() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());
        TenantId tenantId = TenantId.of(1L);
        OrganizationId organizationId = OrganizationId.of(100L);
        UserType userType = UserType.PUBLIC;
        UserStatus nullStatus = null;

        // When & Then
        assertThatThrownBy(
                        () ->
                                User.of(
                                        userId,
                                        tenantId,
                                        organizationId,
                                        userType,
                                        nullStatus,
                                        clock.now(),
                                        clock.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserStatus는 null일 수 없습니다");
    }

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("forNew - 새 User 생성 성공")
        void forNew_validData_success() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            OrganizationId organizationId = OrganizationId.of(100L);
            UserType userType = UserType.PUBLIC;

            // When
            User user = User.forNew(tenantId, organizationId, userType, clock);

            // Then
            assertThat(user.isNew()).isTrue();
            assertThat(user.userIdValue()).isNull();
            assertThat(user.tenantIdValue()).isEqualTo(1L);
            assertThat(user.organizationIdValue()).isEqualTo(100L);
            assertThat(user.getUserType()).isEqualTo(UserType.PUBLIC);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.createdAt()).isEqualTo(clock.now());
            assertThat(user.updatedAt()).isEqualTo(clock.now());
        }

        @Test
        @DisplayName("forNew - Organization 없이 User 생성 성공")
        void forNew_withoutOrganization_success() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            UserType userType = UserType.PUBLIC;

            // When
            User user = User.forNew(tenantId, null, userType, clock);

            // Then
            assertThat(user.isNew()).isTrue();
            assertThat(user.organizationIdValue()).isNull();
            assertThat(user.hasOrganization()).isFalse();
        }

        @Test
        @DisplayName("reconstitute - DB에서 User 재구성 성공")
        void reconstitute_validData_success() {
            // Given
            UserId userId = UserId.of(UUID.randomUUID());
            TenantId tenantId = TenantId.of(1L);
            OrganizationId organizationId = OrganizationId.of(100L);
            UserType userType = UserType.PUBLIC;
            UserStatus userStatus = UserStatus.ACTIVE;
            Instant createdAt = clock.now();
            Instant updatedAt = clock.now();

            // When
            User user =
                    User.reconstitute(
                            userId,
                            tenantId,
                            organizationId,
                            userType,
                            userStatus,
                            createdAt,
                            updatedAt);

            // Then
            assertThat(user.isNew()).isFalse();
            assertThat(user.userIdValue()).isEqualTo(userId.value());
            assertThat(user.tenantIdValue()).isEqualTo(1L);
            assertThat(user.organizationIdValue()).isEqualTo(100L);
            assertThat(user.getUserType()).isEqualTo(UserType.PUBLIC);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("reconstitute - null userId로 재구성 시 예외 발생")
        void reconstitute_nullUserId_throwsException() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            OrganizationId organizationId = OrganizationId.of(100L);
            UserType userType = UserType.PUBLIC;
            UserStatus userStatus = UserStatus.ACTIVE;

            // When & Then
            assertThatThrownBy(
                            () ->
                                    User.reconstitute(
                                            null,
                                            tenantId,
                                            organizationId,
                                            userType,
                                            userStatus,
                                            clock.now(),
                                            clock.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null userId");
        }

        @Test
        @DisplayName("of - 모든 필드 지정하여 User 생성 성공")
        void of_validData_success() {
            // Given
            UserId userId = UserId.of(UUID.randomUUID());
            TenantId tenantId = TenantId.of(1L);
            OrganizationId organizationId = OrganizationId.of(100L);
            UserType userType = UserType.INTERNAL;
            UserStatus userStatus = UserStatus.INACTIVE;
            Instant createdAt = clock.now();
            Instant updatedAt = clock.now();

            // When
            User user =
                    User.of(
                            userId,
                            tenantId,
                            organizationId,
                            userType,
                            userStatus,
                            createdAt,
                            updatedAt);

            // Then
            assertThat(user.isNew()).isFalse();
            assertThat(user.userIdValue()).isEqualTo(userId.value());
            assertThat(user.tenantIdValue()).isEqualTo(1L);
            assertThat(user.organizationIdValue()).isEqualTo(100L);
            assertThat(user.getUserType()).isEqualTo(UserType.INTERNAL);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(user.createdAt()).isEqualTo(createdAt);
            assertThat(user.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("of - null userId로 새 User 생성 성공")
        void of_nullUserId_success() {
            // Given
            TenantId tenantId = TenantId.of(1L);
            OrganizationId organizationId = OrganizationId.of(100L);
            UserType userType = UserType.PUBLIC;
            UserStatus userStatus = UserStatus.ACTIVE;

            // When
            User user =
                    User.of(
                            null,
                            tenantId,
                            organizationId,
                            userType,
                            userStatus,
                            clock.now(),
                            clock.now());

            // Then
            assertThat(user.isNew()).isTrue();
            assertThat(user.userIdValue()).isNull();
        }
    }

    @Nested
    @DisplayName("비즈니스 메서드 테스트")
    class BusinessMethodTests {

        @Test
        @DisplayName("activate - INACTIVE User를 ACTIVE로 변경 성공")
        void activate_inactiveUser_success() {
            // Given
            User inactiveUser = UserFixture.aUserWithStatus(UserStatus.INACTIVE);
            Clock newClock = () -> Instant.parse("2025-11-25T00:00:00Z");

            // When
            User activatedUser = inactiveUser.activate(newClock);

            // Then
            assertThat(activatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(activatedUser.isActive()).isTrue();
            assertThat(activatedUser.updatedAt()).isEqualTo(newClock.now());
            assertThat(activatedUser.createdAt()).isEqualTo(inactiveUser.createdAt());
        }

        @Test
        @DisplayName("activate - DELETED User 활성화 시 예외 발생")
        void activate_deletedUser_throwsException() {
            // Given
            User deletedUser = UserFixture.aUserWithStatus(UserStatus.DELETED);

            // When & Then
            assertThatThrownBy(() -> deletedUser.activate(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");
        }

        @Test
        @DisplayName("deactivate - ACTIVE User를 INACTIVE로 변경 성공")
        void deactivate_activeUser_success() {
            // Given
            User activeUser = UserFixture.aUserWithStatus(UserStatus.ACTIVE);
            Clock newClock = () -> Instant.parse("2025-11-25T00:00:00Z");

            // When
            User deactivatedUser = activeUser.deactivate(newClock);

            // Then
            assertThat(deactivatedUser.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(deactivatedUser.isActive()).isFalse();
            assertThat(deactivatedUser.updatedAt()).isEqualTo(newClock.now());
        }

        @Test
        @DisplayName("deactivate - DELETED User 비활성화 시 예외 발생")
        void deactivate_deletedUser_throwsException() {
            // Given
            User deletedUser = UserFixture.aUserWithStatus(UserStatus.DELETED);

            // When & Then
            assertThatThrownBy(() -> deletedUser.deactivate(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");
        }

        @Test
        @DisplayName("delete - ACTIVE User를 DELETED로 변경 성공")
        void delete_activeUser_success() {
            // Given
            User activeUser = UserFixture.aUserWithStatus(UserStatus.ACTIVE);
            Clock newClock = () -> Instant.parse("2025-11-25T00:00:00Z");

            // When
            User deletedUser = activeUser.delete(newClock);

            // Then
            assertThat(deletedUser.getUserStatus()).isEqualTo(UserStatus.DELETED);
            assertThat(deletedUser.isDeleted()).isTrue();
            assertThat(deletedUser.updatedAt()).isEqualTo(newClock.now());
        }

        @Test
        @DisplayName("delete - 이미 DELETED User 삭제 시 예외 발생")
        void delete_alreadyDeletedUser_throwsException() {
            // Given
            User deletedUser = UserFixture.aUserWithStatus(UserStatus.DELETED);

            // When & Then
            assertThatThrownBy(() -> deletedUser.delete(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");
        }

        @Test
        @DisplayName("assignOrganization - Organization 할당 성공")
        void assignOrganization_validOrganizationId_success() {
            // Given
            User user = UserFixture.aUser();
            OrganizationId newOrganizationId = OrganizationId.of(200L);
            Clock newClock = () -> Instant.parse("2025-11-25T00:00:00Z");

            // When
            User updatedUser = user.assignOrganization(newOrganizationId, newClock);

            // Then
            assertThat(updatedUser.organizationIdValue()).isEqualTo(200L);
            assertThat(updatedUser.hasOrganization()).isTrue();
            assertThat(updatedUser.updatedAt()).isEqualTo(newClock.now());
        }

        @Test
        @DisplayName("assignOrganization - Organization 제거 (null 할당)")
        void assignOrganization_nullOrganizationId_success() {
            // Given
            User user = UserFixture.aUser();
            Clock newClock = () -> Instant.parse("2025-11-25T00:00:00Z");

            // When
            User updatedUser = user.assignOrganization(null, newClock);

            // Then
            assertThat(updatedUser.organizationIdValue()).isNull();
            assertThat(updatedUser.hasOrganization()).isFalse();
            assertThat(updatedUser.updatedAt()).isEqualTo(newClock.now());
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드 테스트")
    class HelperMethodTests {

        @Test
        @DisplayName("userIdValue - User ID 값 반환")
        void userIdValue_returnsCorrectValue() {
            // Given
            UserId userId = UserId.of(UUID.randomUUID());
            User user = UserFixture.aUser(userId);

            // When & Then
            assertThat(user.userIdValue()).isEqualTo(userId.value());
        }

        @Test
        @DisplayName("tenantIdValue - Tenant ID 값 반환")
        void tenantIdValue_returnsCorrectValue() {
            // Given
            TenantId tenantId = TenantId.of(123L);
            User user = UserFixture.aUserWithTenantId(tenantId);

            // When & Then
            assertThat(user.tenantIdValue()).isEqualTo(123L);
        }

        @Test
        @DisplayName("organizationIdValue - Organization ID 값 반환")
        void organizationIdValue_returnsCorrectValue() {
            // Given
            User user = UserFixture.aUser();

            // When & Then
            assertThat(user.organizationIdValue()).isNotNull();
        }

        @Test
        @DisplayName("userTypeValue - UserType name 반환")
        void userTypeValue_returnsCorrectValue() {
            // Given
            User publicUser = UserFixture.aUser();
            User internalUser = UserFixture.anInternalUser();

            // When & Then
            assertThat(publicUser.userTypeValue()).isEqualTo("PUBLIC");
            assertThat(internalUser.userTypeValue()).isEqualTo("INTERNAL");
        }

        @Test
        @DisplayName("statusValue - UserStatus name 반환")
        void statusValue_returnsCorrectValue() {
            // Given
            User activeUser = UserFixture.aUserWithStatus(UserStatus.ACTIVE);
            User inactiveUser = UserFixture.aUserWithStatus(UserStatus.INACTIVE);

            // When & Then
            assertThat(activeUser.statusValue()).isEqualTo("ACTIVE");
            assertThat(inactiveUser.statusValue()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("isNew - 새 User 여부 확인")
        void isNew_checksCorrectly() {
            // Given
            User newUser = UserFixture.aNewUser();
            User existingUser = UserFixture.aUser();

            // When & Then
            assertThat(newUser.isNew()).isTrue();
            assertThat(existingUser.isNew()).isFalse();
        }

        @Test
        @DisplayName("isActive - 활성 상태 확인")
        void isActive_checksCorrectly() {
            // Given
            User activeUser = UserFixture.aUserWithStatus(UserStatus.ACTIVE);
            User inactiveUser = UserFixture.aUserWithStatus(UserStatus.INACTIVE);

            // When & Then
            assertThat(activeUser.isActive()).isTrue();
            assertThat(inactiveUser.isActive()).isFalse();
        }

        @Test
        @DisplayName("isDeleted - 삭제 상태 확인")
        void isDeleted_checksCorrectly() {
            // Given
            User activeUser = UserFixture.aUserWithStatus(UserStatus.ACTIVE);
            User deletedUser = UserFixture.aUserWithStatus(UserStatus.DELETED);

            // When & Then
            assertThat(activeUser.isDeleted()).isFalse();
            assertThat(deletedUser.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("hasOrganization - Organization 소속 여부 확인")
        void hasOrganization_checksCorrectly() {
            // Given
            User userWithOrg = UserFixture.aUser();
            User userWithoutOrg = User.forNew(TenantId.of(1L), null, UserType.PUBLIC, clock);

            // When & Then
            assertThat(userWithOrg.hasOrganization()).isTrue();
            assertThat(userWithoutOrg.hasOrganization()).isFalse();
        }
    }

    @Nested
    @DisplayName("Builder 패턴 테스트")
    class BuilderPatternTests {

        @Test
        @DisplayName("Builder - 기본값으로 User 생성")
        void builder_defaultValues_success() {
            // When
            User user = UserFixture.builder().asExisting().build();

            // Then
            assertThat(user.isNew()).isFalse();
            assertThat(user.tenantIdValue()).isEqualTo(1L);
            assertThat(user.organizationIdValue()).isEqualTo(100L);
            assertThat(user.getUserType()).isEqualTo(UserType.PUBLIC);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Builder - 새 User 생성")
        void builder_newUser_success() {
            // When
            User user = UserFixture.builder().asNew().asInternal().build();

            // Then
            assertThat(user.isNew()).isTrue();
            assertThat(user.userIdValue()).isNull();
            assertThat(user.getUserType()).isEqualTo(UserType.INTERNAL);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Builder - 조직 없는 User 생성")
        void builder_withoutOrganization_success() {
            // When
            User user = UserFixture.builder().asExisting().withoutOrganization().build();

            // Then
            assertThat(user.hasOrganization()).isFalse();
            assertThat(user.organizationIdValue()).isNull();
        }

        @Test
        @DisplayName("Builder - 삭제된 User 생성")
        void builder_deletedUser_success() {
            // When
            User user = UserFixture.builder().asExisting().asDeleted().build();

            // Then
            assertThat(user.isDeleted()).isTrue();
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
        }

        @Test
        @DisplayName("Builder - 특정 시간으로 User 생성")
        void builder_specificTime_success() {
            // Given
            Instant specificTime = Instant.parse("2025-12-01T10:00:00Z");

            // When
            User user =
                    UserFixture.builder()
                            .asExisting()
                            .createdAt(specificTime)
                            .updatedAt(specificTime)
                            .build();

            // Then
            assertThat(user.createdAt()).isEqualTo(specificTime);
            assertThat(user.updatedAt()).isEqualTo(specificTime);
        }
    }

    @Nested
    @DisplayName("Object 메서드 테스트")
    class ObjectMethodTests {

        @Test
        @DisplayName("equals - 동일한 User 객체 비교")
        void equals_sameUser_returnsTrue() {
            // Given
            UserId userId = UserId.of(UUID.randomUUID());
            User user1 = UserFixture.aUser(userId);
            User user2 = UserFixture.aUser(userId);

            // When & Then
            assertThat(user1).isEqualTo(user2);
        }

        @Test
        @DisplayName("equals - 다른 User 객체 비교")
        void equals_differentUser_returnsFalse() {
            // Given
            UserId userId1 = UserId.of(UUID.randomUUID());
            UserId userId2 = UserId.of(UUID.randomUUID());
            User user1 = UserFixture.aUser(userId1);
            User user2 = UserFixture.aUser(userId2);

            // When & Then
            assertThat(user1).isNotEqualTo(user2);
        }

        @Test
        @DisplayName("hashCode - 동일한 User 객체 해시코드")
        void hashCode_sameUser_sameHashCode() {
            // Given
            UserId userId = UserId.of(UUID.randomUUID());
            User user1 = UserFixture.aUser(userId);
            User user2 = UserFixture.aUser(userId);

            // When & Then
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("toString - 문자열 표현 포함 확인")
        void toString_containsExpectedFields() {
            // Given
            User user = UserFixture.aUser();

            // When
            String userString = user.toString();

            // Then
            assertThat(userString).contains("User{");
            assertThat(userString).contains("userId=");
            assertThat(userString).contains("tenantId=");
            assertThat(userString).contains("userType=");
            assertThat(userString).contains("userStatus=");
        }
    }
}
