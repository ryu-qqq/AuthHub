package com.ryuqq.authhub.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User 통합 테스트")
class UserDebugTest {

    private final Clock clock = () -> Instant.parse("2025-11-24T00:00:00Z");

    @Nested
    @DisplayName("상태 전환 통합 테스트")
    class StateTransitionIntegrationTests {

        @Test
        @DisplayName("User 생명주기 전체 테스트 - ACTIVE → INACTIVE → DELETED")
        void userLifecycle_fullTransition_success() {
            // Given - 새 User 생성
            User newUser = UserFixture.builder().asNew().build();

            assertThat(newUser.isNew()).isTrue();
            assertThat(newUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);

            // When - INACTIVE로 변경
            User inactiveUser = newUser.deactivate(clock);

            // Then
            assertThat(inactiveUser.getUserStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(inactiveUser.isActive()).isFalse();

            // When - 다시 ACTIVE로 변경
            User reactivatedUser = inactiveUser.activate(clock);

            // Then
            assertThat(reactivatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(reactivatedUser.isActive()).isTrue();

            // When - DELETED로 변경
            User deletedUser = reactivatedUser.delete(clock);

            // Then
            assertThat(deletedUser.getUserStatus()).isEqualTo(UserStatus.DELETED);
            assertThat(deletedUser.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("DELETED User는 모든 상태 변경 불가")
        void deletedUser_cannotChangeState() {
            // Given
            User deletedUser = UserFixture.builder().asExisting().asDeleted().build();

            // When & Then - activate 불가
            assertThatThrownBy(() -> deletedUser.activate(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");

            // When & Then - deactivate 불가
            assertThatThrownBy(() -> deletedUser.deactivate(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");

            // When & Then - delete 재시도 불가
            assertThatThrownBy(() -> deletedUser.delete(clock))
                    .isInstanceOf(InvalidUserStateException.class)
                    .hasMessageContaining("Invalid user status");
        }
    }

    @Nested
    @DisplayName("Builder 패턴 통합 테스트")
    class BuilderIntegrationTests {

        @Test
        @DisplayName("Builder로 복잡한 User 시나리오 생성")
        void builder_complexScenario_success() {
            // Given - 특정 조건의 User 생성
            Clock customClock = () -> Instant.parse("2025-12-01T15:30:00Z");

            User user =
                    UserFixture.builder()
                            .asExisting()
                            .asInternal()
                            .asSuspended()
                            .withoutOrganization()
                            .clock(customClock)
                            .build();

            // Then
            assertThat(user.isNew()).isFalse();
            assertThat(user.getUserType()).isEqualTo(UserType.INTERNAL);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
            assertThat(user.hasOrganization()).isFalse();
            assertThat(user.createdAt()).isEqualTo(customClock.now());
            assertThat(user.updatedAt()).isEqualTo(customClock.now());
        }

        @Test
        @DisplayName("Builder와 기존 메서드 조합 테스트")
        void builder_withBusinessMethods_success() {
            // Given
            User suspendedUser = UserFixture.builder().asExisting().asSuspended().build();

            // When - 활성화
            User activatedUser = suspendedUser.activate(clock);

            // Then
            assertThat(activatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(activatedUser.isActive()).isTrue();
        }
    }
}
