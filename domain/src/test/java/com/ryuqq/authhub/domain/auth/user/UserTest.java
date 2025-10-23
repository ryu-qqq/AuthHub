package com.ryuqq.authhub.domain.auth.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * User Aggregate Root 단위 테스트.
 *
 * <p>User 도메인 객체의 생성, 상태 전환, 도메인 규칙을 검증합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("User Aggregate Root 테스트")
class UserTest {

    @Nested
    @DisplayName("사용자 생성")
    class CreateUserTest {

        @Test
        @DisplayName("새로운 사용자를 생성하면 ACTIVE 상태로 생성된다")
        void create_ShouldReturnActiveUser() {
            // given
            UserId userId = UserId.newId();

            // when
            User user = User.create(userId);

            // then
            assertThat(user).isNotNull();
            assertThat(user.getId()).isEqualTo(userId);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("새로운 사용자는 로그인 기록이 없다")
        void create_ShouldHaveNeverLoggedIn() {
            // given
            UserId userId = UserId.newId();

            // when
            User user = User.create(userId);

            // then
            assertThat(user.hasNeverLoggedIn()).isTrue();
            assertThat(user.getLastLoginAt().hasNeverLoggedIn()).isTrue();
        }

        @Test
        @DisplayName("사용자 생성 시 createdAt과 updatedAt이 설정된다")
        void create_ShouldSetTimestamps() {
            // given
            UserId userId = UserId.newId();

            // when
            User user = User.create(userId);

            // then
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
            assertThat(user.getCreatedAt()).isBeforeOrEqualTo(user.getUpdatedAt());
        }

        @Test
        @DisplayName("null UserId로 생성하면 예외가 발생한다")
        void create_WithNullId_ShouldThrowException() {
            // when & then
            assertThatThrownBy(() -> User.create(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("UserId cannot be null");
        }
    }

    @Nested
    @DisplayName("사용자 재구성")
    class ReconstructUserTest {

        @Test
        @DisplayName("기존 데이터로 User를 재구성할 수 있다")
        void reconstruct_ShouldRestoreUser() {
            // given
            UserId userId = UserId.newId();
            UserStatus status = UserStatus.ACTIVE;
            LastLoginAt lastLoginAt = LastLoginAt.now();
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            // when
            User user = User.reconstruct(userId, status, lastLoginAt, createdAt, updatedAt);

            // then
            assertThat(user.getId()).isEqualTo(userId);
            assertThat(user.getStatus()).isEqualTo(status);
            assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("로그인 처리")
    class LoginTest {

        @Test
        @DisplayName("활성 사용자는 로그인할 수 있다")
        void login_ActiveUser_ShouldSucceed() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId);

            // when
            User loggedInUser = user.login();

            // then
            assertThat(loggedInUser.hasNeverLoggedIn()).isFalse();
            assertThat(loggedInUser.getLastLoginAt().hasLoggedIn()).isTrue();
            assertThat(loggedInUser.getUpdatedAt()).isAfter(user.getUpdatedAt());
        }

        @Test
        @DisplayName("정지된 사용자는 로그인할 수 없다")
        void login_SuspendedUser_ShouldThrowException() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId).suspend();

            // when & then
            assertThatThrownBy(user::login)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot login")
                    .hasMessageContaining("SUSPENDED");
        }

        @Test
        @DisplayName("비활성 사용자는 로그인할 수 없다")
        void login_InactiveUser_ShouldThrowException() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId).deactivate();

            // when & then
            assertThatThrownBy(user::login)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot login")
                    .hasMessageContaining("INACTIVE");
        }
    }

    @Nested
    @DisplayName("상태 전환")
    class StatusTransitionTest {

        @Test
        @DisplayName("사용자를 비활성화할 수 있다")
        void deactivate_ShouldChangeStatusToInactive() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId);

            // when
            User deactivatedUser = user.deactivate();

            // then
            assertThat(deactivatedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(deactivatedUser.isActive()).isFalse();
            assertThat(deactivatedUser.canUseSystem()).isFalse();
        }

        @Test
        @DisplayName("사용자를 정지할 수 있다")
        void suspend_ShouldChangeStatusToSuspended() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId);

            // when
            User suspendedUser = user.suspend();

            // then
            assertThat(suspendedUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
            assertThat(suspendedUser.isSuspended()).isTrue();
            assertThat(suspendedUser.canUseSystem()).isFalse();
        }

        @Test
        @DisplayName("비활성 사용자를 다시 활성화할 수 있다")
        void activate_InactiveUser_ShouldBecomeActive() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId).deactivate();

            // when
            User activatedUser = user.activate();

            // then
            assertThat(activatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(activatedUser.isActive()).isTrue();
            assertThat(activatedUser.canUseSystem()).isTrue();
        }

        @Test
        @DisplayName("정지된 사용자를 다시 활성화할 수 있다")
        void activate_SuspendedUser_ShouldBecomeActive() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId).suspend();

            // when
            User activatedUser = user.activate();

            // then
            assertThat(activatedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(activatedUser.canUseSystem()).isTrue();
        }

        @Test
        @DisplayName("이미 활성 상태인 사용자를 활성화하면 같은 인스턴스를 반환한다")
        void activate_AlreadyActive_ShouldReturnSameInstance() {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId);

            // when
            User activatedUser = user.activate();

            // then
            assertThat(activatedUser).isSameAs(user);
        }

        @Test
        @DisplayName("상태 변경 시 updatedAt이 갱신된다")
        void statusChange_ShouldUpdateTimestamp() throws InterruptedException {
            // given
            UserId userId = UserId.newId();
            User user = User.create(userId);
            Instant originalUpdatedAt = user.getUpdatedAt();

            Thread.sleep(10); // 시간 차이 보장

            // when
            User deactivatedUser = user.deactivate();

            // then
            assertThat(deactivatedUser.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("불변성 보장")
    class ImmutabilityTest {

        @Test
        @DisplayName("로그인 시 원본 User는 변경되지 않는다")
        void login_ShouldNotModifyOriginal() {
            // given
            UserId userId = UserId.newId();
            User original = User.create(userId);
            LastLoginAt originalLoginAt = original.getLastLoginAt();

            // when
            User loggedIn = original.login();

            // then
            assertThat(original.getLastLoginAt()).isEqualTo(originalLoginAt);
            assertThat(original.hasNeverLoggedIn()).isTrue();
            assertThat(loggedIn.hasNeverLoggedIn()).isFalse();
        }

        @Test
        @DisplayName("상태 변경 시 원본 User는 변경되지 않는다")
        void statusChange_ShouldNotModifyOriginal() {
            // given
            UserId userId = UserId.newId();
            User original = User.create(userId);
            UserStatus originalStatus = original.getStatus();

            // when
            User suspended = original.suspend();

            // then
            assertThat(original.getStatus()).isEqualTo(originalStatus);
            assertThat(original.isActive()).isTrue();
            assertThat(suspended.isSuspended()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 비교")
    class EqualityTest {

        @Test
        @DisplayName("같은 UserId를 가진 User는 동등하다")
        void equals_SameId_ShouldBeEqual() {
            // given
            UserId userId = UserId.newId();
            User user1 = User.create(userId);
            User user2 = User.reconstruct(
                    userId,
                    UserStatus.INACTIVE,
                    LastLoginAt.now(),
                    Instant.now(),
                    Instant.now()
            );

            // when & then
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("다른 UserId를 가진 User는 동등하지 않다")
        void equals_DifferentId_ShouldNotBeEqual() {
            // given
            User user1 = User.create(UserId.newId());
            User user2 = User.create(UserId.newId());

            // when & then
            assertThat(user1).isNotEqualTo(user2);
        }
    }

    @Nested
    @DisplayName("Law of Demeter 준수")
    class LawOfDemeterTest {

        @Test
        @DisplayName("User는 시스템 사용 가능 여부를 직접 제공한다")
        void canUseSystem_ShouldBeProvidedByUser() {
            // given
            User activeUser = User.create(UserId.newId());
            User suspendedUser = activeUser.suspend();

            // when & then
            // Law of Demeter 준수: user.getStatus().canUseSystem() 대신 user.canUseSystem() 사용
            assertThat(activeUser.canUseSystem()).isTrue();
            assertThat(suspendedUser.canUseSystem()).isFalse();
        }

        @Test
        @DisplayName("User는 로그인 여부를 직접 제공한다")
        void hasNeverLoggedIn_ShouldBeProvidedByUser() {
            // given
            User user = User.create(UserId.newId());

            // when & then
            // Law of Demeter 준수: user.getLastLoginAt().hasNeverLoggedIn() 대신 user.hasNeverLoggedIn() 사용
            assertThat(user.hasNeverLoggedIn()).isTrue();
        }
    }
}
