package com.ryuqq.authhub.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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
}
