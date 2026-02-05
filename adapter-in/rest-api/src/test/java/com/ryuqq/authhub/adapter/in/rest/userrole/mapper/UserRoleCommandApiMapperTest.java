package com.ryuqq.authhub.adapter.in.rest.userrole.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.RevokeUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.fixture.UserRoleApiFixture;
import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserRoleCommandApiMapper 단위 테스트")
class UserRoleCommandApiMapperTest {

    private UserRoleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserRoleCommandApiMapper();
    }

    @Nested
    @DisplayName("toAssignCommand(String, AssignUserRoleApiRequest) 메서드는")
    class ToAssignCommand {

        @Test
        @DisplayName("AssignUserRoleApiRequest와 userId를 AssignUserRoleCommand로 변환한다")
        void shouldConvertToAssignUserRoleCommand() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();

            // When
            AssignUserRoleCommand result = mapper.toAssignCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).isEqualTo(UserRoleApiFixture.defaultRoleIds());
        }

        @Test
        @DisplayName("단일 역할 ID로 변환한다")
        void shouldConvertSingleRoleId() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            Long roleId = 1L;
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignSingleRoleRequest(roleId);

            // When
            AssignUserRoleCommand result = mapper.toAssignCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).containsExactly(roleId);
        }

        @Test
        @DisplayName("다중 역할 ID로 변환한다")
        void shouldConvertMultipleRoleIds() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            List<Long> roleIds = List.of(1L, 2L, 3L);
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest(roleIds);

            // When
            AssignUserRoleCommand result = mapper.toAssignCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).isEqualTo(roleIds);
        }

        @Test
        @DisplayName("userId가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenUserIdIsNull() {
            // Given
            String userId = null;
            AssignUserRoleApiRequest request = UserRoleApiFixture.assignUserRoleRequest();

            // When & Then
            assertThatThrownBy(() -> mapper.toAssignCommand(userId, request))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            AssignUserRoleApiRequest request = null;

            // When & Then
            assertThatThrownBy(() -> mapper.toAssignCommand(userId, request))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toRevokeCommand(String, RevokeUserRoleApiRequest) 메서드는")
    class ToRevokeCommand {

        @Test
        @DisplayName("RevokeUserRoleApiRequest와 userId를 RevokeUserRoleCommand로 변환한다")
        void shouldConvertToRevokeUserRoleCommand() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeUserRoleRequest();

            // When
            RevokeUserRoleCommand result = mapper.toRevokeCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).isEqualTo(UserRoleApiFixture.defaultRoleIds());
        }

        @Test
        @DisplayName("단일 역할 ID로 변환한다")
        void shouldConvertSingleRoleId() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            Long roleId = 1L;
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeSingleRoleRequest(roleId);

            // When
            RevokeUserRoleCommand result = mapper.toRevokeCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).containsExactly(roleId);
        }

        @Test
        @DisplayName("다중 역할 ID로 변환한다")
        void shouldConvertMultipleRoleIds() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            List<Long> roleIds = List.of(1L, 2L, 3L);
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeUserRoleRequest(roleIds);

            // When
            RevokeUserRoleCommand result = mapper.toRevokeCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roleIds()).isEqualTo(roleIds);
        }

        @Test
        @DisplayName("userId가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenUserIdIsNull() {
            // Given
            String userId = null;
            RevokeUserRoleApiRequest request = UserRoleApiFixture.revokeUserRoleRequest();

            // When & Then
            assertThatThrownBy(() -> mapper.toRevokeCommand(userId, request))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            String userId = UserRoleApiFixture.defaultUserId();
            RevokeUserRoleApiRequest request = null;

            // When & Then
            assertThatThrownBy(() -> mapper.toRevokeCommand(userId, request))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
