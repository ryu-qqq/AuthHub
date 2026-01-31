package com.ryuqq.authhub.domain.user.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * User Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("User Aggregate 테스트")
class UserTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("User 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 User를 성공적으로 생성한다")
        void shouldCreateUserSuccessfully() {
            // given
            UserId userId = UserId.forNew("01941234-5678-7000-8000-123456789999");
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            Identifier identifier = Identifier.of("newuser@test.com");
            PhoneNumber phoneNumber = PhoneNumber.of("010-9999-8888");
            HashedPassword password = HashedPassword.of("$2a$10$newhashedpassword");

            // when
            User user = User.create(userId, orgId, identifier, phoneNumber, password, NOW);

            // then
            assertThat(user.userIdValue()).isEqualTo(userId.value());
            assertThat(user.organizationIdValue()).isEqualTo(orgId.value());
            assertThat(user.identifierValue()).isEqualTo(identifier.value());
            assertThat(user.phoneNumberValue()).isEqualTo(phoneNumber.value());
            assertThat(user.hashedPasswordValue()).isEqualTo(password.value());
            assertThat(user.isActive()).isTrue();
            assertThat(user.canLogin()).isTrue();
            assertThat(user.isDeleted()).isFalse();
            assertThat(user.createdAt()).isEqualTo(NOW);
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("전화번호 없이 User를 생성한다")
        void shouldCreateUserWithoutPhoneNumber() {
            // given
            UserId userId = UserId.forNew("01941234-5678-7000-8000-123456789999");
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            Identifier identifier = Identifier.of("nophone@test.com");
            HashedPassword password = HashedPassword.of("$2a$10$hashedpassword");

            // when
            User user = User.create(userId, orgId, identifier, null, password, NOW);

            // then
            assertThat(user.phoneNumberValue()).isNull();
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("userId가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenUserIdIsNull() {
            // given
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            Identifier identifier = Identifier.of("test@test.com");
            HashedPassword password = HashedPassword.of("$2a$10$hashedpassword");

            // when & then
            assertThatThrownBy(() -> User.create(null, orgId, identifier, null, password, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("userId");
        }

        @Test
        @DisplayName("organizationId가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenOrganizationIdIsNull() {
            // given
            UserId userId = UserId.forNew("01941234-5678-7000-8000-123456789999");
            Identifier identifier = Identifier.of("test@test.com");
            HashedPassword password = HashedPassword.of("$2a$10$hashedpassword");

            // when & then
            assertThatThrownBy(() -> User.create(userId, null, identifier, null, password, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("organizationId");
        }

        @Test
        @DisplayName("identifier가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenIdentifierIsNull() {
            // given
            UserId userId = UserId.forNew("01941234-5678-7000-8000-123456789999");
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            HashedPassword password = HashedPassword.of("$2a$10$hashedpassword");

            // when & then
            assertThatThrownBy(() -> User.create(userId, orgId, null, null, password, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("identifier");
        }

        @Test
        @DisplayName("hashedPassword가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenHashedPasswordIsNull() {
            // given
            UserId userId = UserId.forNew("01941234-5678-7000-8000-123456789999");
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            Identifier identifier = Identifier.of("test@test.com");

            // when & then
            assertThatThrownBy(() -> User.create(userId, orgId, identifier, null, null, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hashedPassword");
        }
    }

    @Nested
    @DisplayName("User 상태 변경 테스트")
    class StatusChangeTests {

        @Test
        @DisplayName("User를 활성화한다")
        void shouldActivateUser() {
            // given
            User user = UserFixture.createInactive();

            // when
            user.activate(NOW);

            // then
            assertThat(user.isActive()).isTrue();
            assertThat(user.canLogin()).isTrue();
            assertThat(user.statusValue()).isEqualTo("ACTIVE");
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("User를 비활성화한다")
        void shouldDeactivateUser() {
            // given
            User user = UserFixture.create();

            // when
            user.deactivate(NOW);

            // then
            assertThat(user.isActive()).isFalse();
            assertThat(user.canLogin()).isFalse();
            assertThat(user.statusValue()).isEqualTo("INACTIVE");
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("User를 정지시킨다")
        void shouldSuspendUser() {
            // given
            User user = UserFixture.create();

            // when
            user.suspend(NOW);

            // then
            assertThat(user.isSuspended()).isTrue();
            assertThat(user.canLogin()).isFalse();
            assertThat(user.statusValue()).isEqualTo("SUSPENDED");
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("SUSPENDED 상태에서 다시 활성화한다")
        void shouldActivateFromSuspended() {
            // given
            User user = UserFixture.createSuspended();
            assertThat(user.isSuspended()).isTrue();
            assertThat(user.canLogin()).isFalse();

            // when
            user.activate(NOW);

            // then
            assertThat(user.isActive()).isTrue();
            assertThat(user.canLogin()).isTrue();
            assertThat(user.isSuspended()).isFalse();
        }
    }

    @Nested
    @DisplayName("User 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("User를 삭제(소프트 삭제)한다")
        void shouldDeleteUser() {
            // given
            User user = UserFixture.create();

            // when
            user.delete(NOW);

            // then
            assertThat(user.isDeleted()).isTrue();
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("삭제된 User를 복원한다")
        void shouldRestoreUser() {
            // given
            User user = UserFixture.createDeleted();
            assertThat(user.isDeleted()).isTrue();

            // when
            user.restore(NOW);

            // then
            assertThat(user.isDeleted()).isFalse();
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("User 비밀번호 변경 테스트")
    class PasswordChangeTests {

        @Test
        @DisplayName("비밀번호를 변경한다")
        void shouldChangePassword() {
            // given
            User user = UserFixture.create();
            String originalPassword = user.hashedPasswordValue();
            HashedPassword newPassword = HashedPassword.of("$2a$10$newhashedpassword123");

            // when
            user.changePassword(newPassword, NOW);

            // then
            assertThat(user.hashedPasswordValue()).isEqualTo(newPassword.value());
            assertThat(user.hashedPasswordValue()).isNotEqualTo(originalPassword);
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("null 비밀번호로 변경 시 예외가 발생한다")
        void shouldThrowExceptionWhenNewPasswordIsNull() {
            // given
            User user = UserFixture.create();

            // when & then
            assertThatThrownBy(() -> user.changePassword(null, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("newHashedPassword");
        }
    }

    @Nested
    @DisplayName("User 정보 수정 테스트")
    class UpdateTests {

        @Test
        @DisplayName("전화번호를 수정한다")
        void shouldUpdatePhoneNumber() {
            // given
            User user = UserFixture.create();
            PhoneNumber newPhone = PhoneNumber.of("010-5555-6666");
            UserUpdateData updateData = UserUpdateData.of(newPhone);

            // when
            user.update(updateData, NOW);

            // then
            assertThat(user.phoneNumberValue()).isEqualTo(newPhone.value());
            assertThat(user.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("전화번호가 null이면 변경하지 않는다")
        void shouldNotUpdateWhenPhoneNumberIsNull() {
            // given
            User user = UserFixture.create();
            String originalPhone = user.phoneNumberValue();
            UserUpdateData updateData = UserUpdateData.of(null);

            // when
            user.update(updateData, NOW);

            // then
            assertThat(user.phoneNumberValue()).isEqualTo(originalPhone);
        }
    }

    @Nested
    @DisplayName("User Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("canLogin은 ACTIVE 상태에서만 true를 반환한다")
        void canLoginShouldReturnTrueOnlyWhenActive() {
            // given
            User activeUser = UserFixture.create();
            User inactiveUser = UserFixture.createInactive();
            User suspendedUser = UserFixture.createSuspended();

            // then
            assertThat(activeUser.canLogin()).isTrue();
            assertThat(inactiveUser.canLogin()).isFalse();
            assertThat(suspendedUser.canLogin()).isFalse();
        }

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            User newUser = UserFixture.createNew();
            User existingUser = UserFixture.create();

            // then - createNew는 이미 ID가 있으므로 isNew가 false
            assertThat(newUser.isNew()).isFalse();
            assertThat(existingUser.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("User equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 userId를 가진 User는 동등하다")
        void shouldBeEqualWhenSameUserId() {
            // given
            User user1 = UserFixture.create();
            User user2 = UserFixture.create();

            // then
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("다른 userId를 가진 User는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentUserId() {
            // given
            User user1 = UserFixture.create();
            User user2 = UserFixture.createWithIdentifier("other@test.com");

            // 같은 DEFAULT_USER_ID를 사용하므로 동등함
            assertThat(user1).isEqualTo(user2);
        }
    }
}
