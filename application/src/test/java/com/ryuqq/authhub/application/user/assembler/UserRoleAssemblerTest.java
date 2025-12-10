package com.ryuqq.authhub.application.user.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleAssembler 단위 테스트")
class UserRoleAssemblerTest {

    private UserRoleAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new UserRoleAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("UserRole을 UserRoleResponse로 변환한다")
        void shouldConvertToResponse() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();
            Instant assignedAt = Instant.parse("2025-01-01T00:00:00Z");
            UserRole userRole = UserRole.of(userId, roleId, assignedAt);

            // when
            UserRoleResponse result = assembler.toResponse(userRole);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId.value());
            assertThat(result.roleId()).isEqualTo(roleId.value());
            assertThat(result.assignedAt()).isEqualTo(assignedAt);
        }

        @Test
        @DisplayName("다양한 역할 할당 시간을 올바르게 변환한다")
        void shouldConvertWithVariousAssignedAt() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();
            Instant recentAssignedAt = Instant.now();
            UserRole userRole = UserRole.of(userId, roleId, recentAssignedAt);

            // when
            UserRoleResponse result = assembler.toResponse(userRole);

            // then
            assertThat(result.assignedAt()).isEqualTo(recentAssignedAt);
        }
    }
}
