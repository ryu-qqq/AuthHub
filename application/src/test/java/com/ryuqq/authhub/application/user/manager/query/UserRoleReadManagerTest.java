package com.ryuqq.authhub.application.user.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.user.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleReadManager 단위 테스트")
class UserRoleReadManagerTest {

    @Mock private UserRoleQueryPort queryPort;

    private UserRoleReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new UserRoleReadManager(queryPort);
    }

    @Nested
    @DisplayName("findByUserIdAndRoleId 메서드")
    class FindByUserIdAndRoleIdTest {

        @Test
        @DisplayName("사용자 역할을 조회한다")
        void shouldFindUserRole() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();
            UserRole expectedRole = UserRole.of(userId, roleId, Instant.now());
            given(queryPort.findByUserIdAndRoleId(userId, roleId))
                    .willReturn(Optional.of(expectedRole));

            // when
            Optional<UserRole> result = readManager.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().userIdValue()).isEqualTo(userId.value());
            assertThat(result.get().roleIdValue()).isEqualTo(roleId.value());
        }

        @Test
        @DisplayName("역할이 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleId.of(UUID.randomUUID());
            given(queryPort.findByUserIdAndRoleId(userId, roleId)).willReturn(Optional.empty());

            // when
            Optional<UserRole> result = readManager.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByUserId 메서드")
    class FindAllByUserIdTest {

        @Test
        @DisplayName("사용자의 모든 역할을 조회한다")
        void shouldFindAllUserRoles() {
            // given
            UserId userId = UserFixture.defaultId();
            UserRole role1 = UserRole.of(userId, RoleFixture.defaultId(), Instant.now());
            UserRole role2 = UserRole.of(userId, RoleId.of(UUID.randomUUID()), Instant.now());
            given(queryPort.findAllByUserId(userId)).willReturn(List.of(role1, role2));

            // when
            List<UserRole> result = readManager.findAllByUserId(userId);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("역할이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoRoles() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            given(queryPort.findAllByUserId(userId)).willReturn(List.of());

            // when
            List<UserRole> result = readManager.findAllByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndRoleId 메서드")
    class ExistsByUserIdAndRoleIdTest {

        @Test
        @DisplayName("역할이 할당되어 있으면 true를 반환한다")
        void shouldReturnTrueWhenRoleAssigned() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();
            given(queryPort.existsByUserIdAndRoleId(userId, roleId)).willReturn(true);

            // when
            boolean result = readManager.existsByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("역할이 할당되어 있지 않으면 false를 반환한다")
        void shouldReturnFalseWhenRoleNotAssigned() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleId.of(UUID.randomUUID());
            given(queryPort.existsByUserIdAndRoleId(userId, roleId)).willReturn(false);

            // when
            boolean result = readManager.existsByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isFalse();
        }
    }
}
