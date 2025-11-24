package com.ryuqq.authhub.domain.user;

import com.ryuqq.authhub.domain.common.model.AggregateRoot;
import com.ryuqq.authhub.domain.user.vo.UserId;

import java.util.Objects;

/**
 * User Aggregate Root
 * 사용자 도메인 객체
 */
public class User implements AggregateRoot {

    private final UserId userId;
    private final Long tenantId;
    private final Long organizationId;
    private final UserType userType;
    private final UserStatus userStatus;

    private User(UserId userId, Long tenantId, Long organizationId, UserType userType, UserStatus userStatus) {
        validateUserId(userId);
        validateTenantId(tenantId);
        validateOrganizationId(organizationId);
        validateUserType(userType);
        validateUserStatus(userStatus);

        this.userId = userId;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    private void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
    }

    public static User create(UserId userId, Long tenantId, Long organizationId, UserType userType, UserStatus userStatus) {
        return new User(userId, tenantId, organizationId, userType, userStatus);
    }

    private void validateTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
    }

    private void validateOrganizationId(Long organizationId) {
        // OrganizationId는 nullable 허용 (일부 사용자는 조직에 속하지 않을 수 있음)
    }

    private void validateUserType(UserType userType) {
        if (userType == null) {
            throw new IllegalArgumentException("UserType은 null일 수 없습니다");
        }
    }

    private void validateUserStatus(UserStatus userStatus) {
        if (userStatus == null) {
            throw new IllegalArgumentException("UserStatus는 null일 수 없습니다");
        }
    }

    public UserId getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(tenantId, user.tenantId) &&
                Objects.equals(organizationId, user.organizationId) &&
                userType == user.userType &&
                userStatus == user.userStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tenantId, organizationId, userType, userStatus);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", tenantId=" + tenantId +
                ", organizationId=" + organizationId +
                ", userType=" + userType +
                ", userStatus=" + userStatus +
                '}';
    }
}
