package com.ryuqq.authhub.domain.user.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * User Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("User Aggregate 테스트")
class UserTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("User 생성")
    class CreateTest {

        @Test
        @DisplayName("새 User를 생성한다")
        void shouldCreateUser() {
            // given
            UserId userId = UserId.forNew(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            OrganizationId organizationId = OrganizationId.of(UUID.randomUUID());
            String identifier = "test@example.com";
            String phoneNumber = "010-1234-5678";
            String hashedPassword = "hashed_password";

            // when
            User user =
                    User.create(
                            userId,
                            tenantId,
                            organizationId,
                            identifier,
                            phoneNumber,
                            hashedPassword,
                            FIXED_CLOCK);

            // then
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo(userId);
            assertThat(user.getTenantId()).isEqualTo(tenantId);
            assertThat(user.getOrganizationId()).isEqualTo(organizationId);
            assertThat(user.getIdentifier()).isEqualTo(identifier);
            assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
            assertThat(user.getHashedPassword()).isEqualTo(hashedPassword);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("OrganizationId가 null이면 예외 발생")
        void shouldThrowExceptionWhenOrganizationIdNull() {
            // given
            UserId userId = UserId.forNew(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            String identifier = "test@example.com";
            String phoneNumber = "010-1234-5678";
            String hashedPassword = "hashed_password";

            // when & then
            assertThatThrownBy(
                            () ->
                                    User.create(
                                            userId,
                                            tenantId,
                                            null,
                                            identifier,
                                            phoneNumber,
                                            hashedPassword,
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId");
        }
    }

    @Nested
    @DisplayName("User 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("DB에서 User를 재구성한다")
        void shouldReconstituteUser() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            OrganizationId organizationId = OrganizationId.of(UUID.randomUUID());
            String identifier = "test@example.com";
            String phoneNumber = "010-1234-5678";
            String hashedPassword = "hashed_password";
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2025-01-02T00:00:00Z");

            // when
            User user =
                    User.reconstitute(
                            userId,
                            tenantId,
                            organizationId,
                            identifier,
                            phoneNumber,
                            hashedPassword,
                            UserStatus.ACTIVE,
                            createdAt,
                            updatedAt);

            // then
            assertThat(user.getUserId()).isEqualTo(userId);
            assertThat(user.getTenantId()).isEqualTo(tenantId);
            assertThat(user.getOrganizationId()).isEqualTo(organizationId);
            assertThat(user.getIdentifier()).isEqualTo(identifier);
            assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드")
    class HelperMethodsTest {

        @Test
        @DisplayName("userIdValue는 UUID를 반환한다")
        void shouldReturnUserIdValue() {
            // given
            User user = UserFixture.create();

            // then
            assertThat(user.userIdValue()).isNotNull();
            assertThat(user.userIdValue()).isEqualTo(user.getUserId().value());
        }

        @Test
        @DisplayName("tenantIdValue는 UUID를 반환한다")
        void shouldReturnTenantIdValue() {
            // given
            User user = UserFixture.create();

            // then
            assertThat(user.tenantIdValue()).isNotNull();
            assertThat(user.tenantIdValue()).isEqualTo(user.getTenantId().value());
        }

        @Test
        @DisplayName("organizationIdValue는 UUID를 반환한다")
        void shouldReturnOrganizationIdValue() {
            // given
            User user = UserFixture.create();

            // then
            assertThat(user.organizationIdValue()).isNotNull();
            assertThat(user.organizationIdValue()).isEqualTo(user.getOrganizationId().value());
        }

        @Test
        @DisplayName("isActive는 ACTIVE 상태를 확인한다")
        void shouldCheckActiveStatus() {
            // given
            User activeUser = UserFixture.createWithStatus(UserStatus.ACTIVE);
            User inactiveUser = UserFixture.createWithStatus(UserStatus.INACTIVE);

            // then
            assertThat(activeUser.isActive()).isTrue();
            assertThat(inactiveUser.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Fixture 테스트")
    class FixtureTest {

        @Test
        @DisplayName("UserFixture.create는 기본 User를 생성한다")
        void shouldCreateDefaultUser() {
            // when
            User user = UserFixture.create();

            // then
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo(UserFixture.defaultId());
            assertThat(user.getTenantId()).isEqualTo(UserFixture.defaultTenantId());
            assertThat(user.getOrganizationId()).isEqualTo(UserFixture.defaultOrganizationId());
            assertThat(user.getIdentifier()).isEqualTo(UserFixture.defaultIdentifier());
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("UserFixture.createNew는 새 User를 생성한다")
        void shouldCreateNewUser() {
            // when
            User user = UserFixture.createNew();

            // then
            assertThat(user).isNotNull();
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("UserFixture.createWithOrganization은 지정된 Organization으로 생성한다")
        void shouldCreateUserWithOrganization() {
            // given
            UUID orgUuid = UUID.randomUUID();

            // when
            User user = UserFixture.createWithOrganization(orgUuid);

            // then
            assertThat(user.organizationIdValue()).isEqualTo(orgUuid);
        }

        @Test
        @DisplayName("UserFixture.createInactive는 비활성 User를 생성한다")
        void shouldCreateInactiveUser() {
            // when
            User user = UserFixture.createInactive();

            // then
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(user.isActive()).isFalse();
        }
    }
}
