package com.ryuqq.authhub.application.userrole.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleCommandFactory 단위 테스트")
class UserRoleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private UserRoleCommandFactory sut;

    private static final Instant FIXED_TIME = UserRoleFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new UserRoleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("createAll 메서드")
    class CreateAll {

        @Test
        @DisplayName("성공: UserId와 RoleIds로 UserRole 목록 생성")
        void shouldCreateUserRoles_FromUserIdAndRoleIds() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<RoleId> roleIds = List.of(UserRoleFixture.defaultRoleId(), RoleId.of(2L));

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            List<UserRole> result = sut.createAll(userId, roleIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).userIdValue()).isEqualTo(userId.value());
            assertThat(result.get(0).roleIdValue()).isEqualTo(roleIds.get(0).value());
            assertThat(result.get(0).createdAt()).isEqualTo(FIXED_TIME);
        }
    }
}
