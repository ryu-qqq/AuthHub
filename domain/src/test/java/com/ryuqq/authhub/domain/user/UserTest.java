package com.ryuqq.authhub.domain.user;

import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Aggregate 테스트")
class UserTest {

    @Test
    @DisplayName("유효한 데이터로 User 생성 성공")
    void shouldCreateUserWithValidData() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());

        // When
        User user = UserFixture.aUser(userId);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(userId);
        assertThat(user.getTenantId()).isNotNull();
        assertThat(user.getUserType()).isEqualTo(UserType.PUBLIC);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("null tenantId로 User 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullTenantId() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());
        Long nullTenantId = null;
        Long organizationId = 100L;
        UserType userType = UserType.PUBLIC;
        UserStatus userStatus = UserStatus.ACTIVE;

        // When & Then
        assertThatThrownBy(() -> User.create(userId, nullTenantId, organizationId, userType, userStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TenantId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("null UserStatus로 User 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullUserStatus() {
        // Given
        UserId userId = UserId.of(UUID.randomUUID());
        Long tenantId = 1L;
        Long organizationId = 100L;
        UserType userType = UserType.PUBLIC;
        UserStatus nullStatus = null;

        // When & Then
        assertThatThrownBy(() -> User.create(userId, tenantId, organizationId, userType, nullStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserStatus는 null일 수 없습니다");
    }
}
