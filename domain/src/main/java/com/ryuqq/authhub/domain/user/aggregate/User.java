package com.ryuqq.authhub.domain.user.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User Aggregate Root - 사용자 도메인 모델
 *
 * <p>사용자 정보와 인증/인가 관련 비즈니스 로직을 캡슐화합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용</li>
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지</li>
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임</li>
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class User {

    private final UserId userId;
    private final TenantId tenantId;
    private final OrganizationId organizationId;
    private final UserType userType;
    private final UserStatus userStatus;
    private final Credential credential;
    private final UserProfile profile;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Credential credential,
            UserProfile profile,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(tenantId, userStatus);
        this.userId = userId;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.userType = userType != null ? userType : UserType.PUBLIC;
        this.userStatus = userStatus;
        this.credential = credential;
        this.profile = profile != null ? profile : UserProfile.empty();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(TenantId tenantId, UserStatus userStatus) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        if (userStatus == null) {
            throw new IllegalArgumentException("UserStatus는 null일 수 없습니다");
        }
    }

    /**
     * 새로운 User 생성 (ID 미할당)
     */
    public static User forNew(
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            Credential credential,
            UserProfile profile,
            Clock clock) {
        Instant now = clock.now();
        return new User(
                null,
                tenantId,
                organizationId,
                userType,
                UserStatus.ACTIVE,
                credential,
                profile,
                now,
                now
        );
    }

    /**
     * DB에서 User 재구성 (reconstitute)
     */
    public static User reconstitute(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Credential credential,
            UserProfile profile,
            Instant createdAt,
            Instant updatedAt) {
        if (userId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null userId");
        }
        return new User(
                userId,
                tenantId,
                organizationId,
                userType,
                userStatus,
                credential,
                profile,
                createdAt,
                updatedAt
        );
    }

    /**
     * 모든 필드 지정하여 User 생성
     */
    public static User of(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Credential credential,
            UserProfile profile,
            Instant createdAt,
            Instant updatedAt) {
        return new User(
                userId,
                tenantId,
                organizationId,
                userType,
                userStatus,
                credential,
                profile,
                createdAt,
                updatedAt
        );
    }

    // ========== Business Methods ==========

    /**
     * User 활성화
     */
    public User activate(Clock clock) {
        if (!userStatus.canTransitionTo(UserStatus.ACTIVE)) {
            throw new InvalidUserStateException(userStatus, UserStatus.ACTIVE);
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.ACTIVE,
                this.credential,
                this.profile,
                this.createdAt,
                clock.now()
        );
    }

    /**
     * User 비활성화
     */
    public User deactivate(Clock clock) {
        if (!userStatus.canTransitionTo(UserStatus.INACTIVE)) {
            throw new InvalidUserStateException(userStatus, UserStatus.INACTIVE);
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.INACTIVE,
                this.credential,
                this.profile,
                this.createdAt,
                clock.now()
        );
    }

    /**
     * User 삭제 (논리적)
     */
    public User delete(Clock clock) {
        if (!userStatus.canTransitionTo(UserStatus.DELETED)) {
            throw new InvalidUserStateException(userStatus, UserStatus.DELETED);
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.DELETED,
                this.credential,
                this.profile,
                this.createdAt,
                clock.now()
        );
    }

    /**
     * Organization 할당/변경
     */
    public User assignOrganization(OrganizationId newOrganizationId, Clock clock) {
        return new User(
                this.userId,
                this.tenantId,
                newOrganizationId,
                this.userType,
                this.userStatus,
                this.credential,
                this.profile,
                this.createdAt,
                clock.now()
        );
    }

    // ========== Helper Methods ==========

    public UUID userIdValue() {
        return userId != null ? userId.value() : null;
    }

    public Long tenantIdValue() {
        return tenantId.value();
    }

    public Long organizationIdValue() {
        return organizationId != null ? organizationId.value() : null;
    }

    public String userTypeValue() {
        return userType.name();
    }

    public String statusValue() {
        return userStatus.name();
    }

    public boolean isNew() {
        return userId == null;
    }

    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return userStatus == UserStatus.DELETED;
    }

    public boolean hasOrganization() {
        return organizationId != null;
    }

    // ========== Getter Methods ==========

    public UserId getUserId() {
        return userId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public Credential getCredential() {
        return credential;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // ========== Object Methods ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (userId == null || user.userId == null) {
            return false;
        }
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return userId != null ? Objects.hash(userId) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", tenantId=" + tenantId
                + ", organizationId=" + organizationId
                + ", userType=" + userType
                + ", userStatus=" + userStatus
                + "}";
    }
}
