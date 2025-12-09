package com.ryuqq.authhub.domain.user.aggregate;

import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * User Aggregate Root
 *
 * <p>사용자 도메인의 핵심 엔티티입니다.
 *
 * <p><strong>불변 규칙:</strong>
 * <ul>
 *   <li>모든 필드는 final
 *   <li>상태 변경은 새 인스턴스 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class User {

    private final UserId userId;
    private final TenantId tenantId;
    private final OrganizationId organizationId;
    private final String identifier;
    private final String hashedPassword;
    private final UserStatus userStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            String identifier,
            String hashedPassword,
            UserStatus userStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.identifier = identifier;
        this.hashedPassword = hashedPassword;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새 사용자 생성
     *
     * @param userId Application Layer에서 생성된 UserId
     * @param tenantId 테넌트 ID
     * @param organizationId 소속 조직 ID
     * @param identifier 사용자 식별자 (이메일 등)
     * @param hashedPassword 해시된 비밀번호
     * @param clock 시간 주입 (ClockHolder.clock())
     * @return 새로운 User 인스턴스
     */
    public static User create(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            String identifier,
            String hashedPassword,
            Clock clock) {
        if (organizationId == null) {
            throw new IllegalArgumentException("OrganizationId는 필수입니다");
        }
        Instant now = clock.instant();
        return new User(
                userId,
                tenantId,
                organizationId,
                identifier,
                hashedPassword,
                UserStatus.ACTIVE,
                now,
                now);
    }

    /**
     * 영속성에서 복원
     */
    public static User reconstitute(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            String identifier,
            String hashedPassword,
            UserStatus userStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new User(
                userId,
                tenantId,
                organizationId,
                identifier,
                hashedPassword,
                userStatus,
                createdAt,
                updatedAt);
    }

    public UserId getUserId() {
        return userId;
    }

    public UUID userIdValue() {
        return userId.value();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public UUID tenantIdValue() {
        return tenantId.value();
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public UUID organizationIdValue() {
        return organizationId.value();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public boolean isActive() {
        return userStatus.isActive();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
