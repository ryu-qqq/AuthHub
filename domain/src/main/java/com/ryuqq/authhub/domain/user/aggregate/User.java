package com.ryuqq.authhub.domain.user.aggregate;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User Aggregate Root - 사용자 도메인 객체
 *
 * <p>Tenant와 Organization에 속하는 사용자를 나타내는 Aggregate Root입니다.
 *
 * <p><strong>팩토리 메서드:</strong>
 *
 * <ul>
 *   <li>{@code forNew()} - 새 User 생성 (ID null, ACTIVE 상태)
 *   <li>{@code of()} - 기존 User 로드 (모든 필드 지정)
 *   <li>{@code reconstitute()} - DB에서 User 재구성
 * </ul>
 *
 * <p><strong>비즈니스 규칙:</strong>
 *
 * <ul>
 *   <li>DELETED 상태에서는 activate/deactivate 불가
 *   <li>이미 DELETED 상태이면 delete 재시도 불가
 *   <li>상태 변경 시 updatedAt 자동 갱신
 *   <li>organizationId는 nullable (일부 사용자는 조직 미소속 가능)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class User {

    private final UserId userId;
    private final TenantId tenantId;
    private final OrganizationId organizationId;
    private final UserType userType;
    private final UserStatus userStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Instant createdAt,
            Instant updatedAt) {
        validateTenantId(tenantId);
        validateUserType(userType);
        validateUserStatus(userStatus);
        validateTimestamps(createdAt, updatedAt);

        this.userId = userId;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.userType = userType;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * forNew - 새 User 생성 (도메인 유스케이스)
     *
     * <p>ID는 null이며, ACTIVE 상태로 생성됩니다.
     *
     * <p>생성 시간과 수정 시간이 동일하게 설정됩니다.
     *
     * @param tenantId 소속 Tenant ID (필수)
     * @param organizationId 소속 Organization ID (nullable)
     * @param userType 사용자 유형 (필수)
     * @param clock 시간 제공자
     * @return 새로 생성된 User
     * @author development-team
     * @since 1.0.0
     */
    public static User forNew(
            TenantId tenantId, OrganizationId organizationId, UserType userType, Clock clock) {
        Instant now = clock.now();
        return new User(null, tenantId, organizationId, userType, UserStatus.ACTIVE, now, now);
    }

    /**
     * of - 기존 User 로드 (애플리케이션 유스케이스)
     *
     * <p>모든 필드를 명시적으로 지정하여 User를 생성합니다.
     *
     * @param userId User ID (nullable - forNew의 경우)
     * @param tenantId 소속 Tenant ID (필수)
     * @param organizationId 소속 Organization ID (nullable)
     * @param userType 사용자 유형 (필수)
     * @param userStatus 사용자 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 로드된 User
     * @author development-team
     * @since 1.0.0
     */
    public static User of(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new User(
                userId, tenantId, organizationId, userType, userStatus, createdAt, updatedAt);
    }

    /**
     * reconstitute - DB에서 User 재구성 (Persistence Adapter 전용)
     *
     * <p>DB에서 조회한 데이터로 User를 재구성합니다.
     *
     * <p>ID는 필수입니다.
     *
     * @param userId User ID (필수)
     * @param tenantId 소속 Tenant ID (필수)
     * @param organizationId 소속 Organization ID (nullable)
     * @param userType 사용자 유형 (필수)
     * @param userStatus 사용자 상태 (필수)
     * @param createdAt 생성 시간 (필수)
     * @param updatedAt 수정 시간 (필수)
     * @return 재구성된 User
     * @author development-team
     * @since 1.0.0
     */
    public static User reconstitute(
            UserId userId,
            TenantId tenantId,
            OrganizationId organizationId,
            UserType userType,
            UserStatus userStatus,
            Instant createdAt,
            Instant updatedAt) {
        if (userId == null) {
            throw new IllegalArgumentException("reconstitute requires non-null userId");
        }
        return new User(
                userId, tenantId, organizationId, userType, userStatus, createdAt, updatedAt);
    }

    private void validateTenantId(TenantId tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
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

    private void validateTimestamps(Instant createdAt, Instant updatedAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt는 null일 수 없습니다");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt는 null일 수 없습니다");
        }
    }

    // ========== Business Methods ==========

    /**
     * activate - User를 활성화 (INACTIVE → ACTIVE)
     *
     * <p>DELETED 상태에서는 activate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 활성화된 새 User 인스턴스
     * @throws InvalidUserStateException DELETED 상태에서 activate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public User activate(Clock clock) {
        if (this.userStatus == UserStatus.DELETED) {
            throw new InvalidUserStateException(userIdValue(), "Cannot activate deleted user");
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.ACTIVE,
                this.createdAt,
                clock.now());
    }

    /**
     * deactivate - User를 비활성화 (ACTIVE → INACTIVE)
     *
     * <p>DELETED 상태에서는 deactivate 불가합니다.
     *
     * @param clock 시간 제공자
     * @return 비활성화된 새 User 인스턴스
     * @throws InvalidUserStateException DELETED 상태에서 deactivate 시도 시
     * @author development-team
     * @since 1.0.0
     */
    public User deactivate(Clock clock) {
        if (this.userStatus == UserStatus.DELETED) {
            throw new InvalidUserStateException(userIdValue(), "Cannot deactivate deleted user");
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.INACTIVE,
                this.createdAt,
                clock.now());
    }

    /**
     * delete - User를 삭제 (ACTIVE/INACTIVE → DELETED)
     *
     * <p>이미 DELETED 상태이면 예외가 발생합니다.
     *
     * @param clock 시간 제공자
     * @return 삭제된 새 User 인스턴스
     * @throws InvalidUserStateException 이미 DELETED 상태일 경우
     * @author development-team
     * @since 1.0.0
     */
    public User delete(Clock clock) {
        if (this.userStatus == UserStatus.DELETED) {
            throw new InvalidUserStateException(userIdValue(), "User is already deleted");
        }
        return new User(
                this.userId,
                this.tenantId,
                this.organizationId,
                this.userType,
                UserStatus.DELETED,
                this.createdAt,
                clock.now());
    }

    /**
     * assignOrganization - User에 Organization 할당
     *
     * <p>이미 Organization에 속한 User의 Organization을 변경합니다.
     *
     * @param organizationId 새로 할당할 Organization ID (nullable - 조직 미소속 가능)
     * @param clock 시간 제공자
     * @return Organization이 변경된 새 User 인스턴스
     * @author development-team
     * @since 1.0.0
     */
    public User assignOrganization(OrganizationId organizationId, Clock clock) {
        return new User(
                this.userId,
                this.tenantId,
                organizationId,
                this.userType,
                this.userStatus,
                this.createdAt,
                clock.now());
    }

    // ========== Law of Demeter 준수: Primitive 값 접근 헬퍼 메서드 ==========

    /**
     * userIdValue - User ID의 UUID 값 반환
     *
     * <p>Getter 체이닝 방지 (user.getUserId().value() ❌)
     *
     * @return User ID UUID 값 (nullable - forNew의 경우)
     * @author development-team
     * @since 1.0.0
     */
    public UUID userIdValue() {
        return (userId == null) ? null : userId.value();
    }

    /**
     * tenantIdValue - Tenant ID의 Long 값 반환
     *
     * <p>Getter 체이닝 방지 (user.getTenantId().value() ❌)
     *
     * @return Tenant ID Long 값
     * @author development-team
     * @since 1.0.0
     */
    public Long tenantIdValue() {
        return tenantId.value();
    }

    /**
     * organizationIdValue - Organization ID의 Long 값 반환
     *
     * <p>Getter 체이닝 방지 (user.getOrganizationId().value() ❌)
     *
     * @return Organization ID Long 값 (nullable - 조직 미소속 경우)
     * @author development-team
     * @since 1.0.0
     */
    public Long organizationIdValue() {
        return (organizationId == null) ? null : organizationId.value();
    }

    /**
     * userTypeValue - UserType의 name() 값 반환
     *
     * @return UserType name (예: "ADMIN", "USER")
     * @author development-team
     * @since 1.0.0
     */
    public String userTypeValue() {
        return userType.name();
    }

    /**
     * statusValue - UserStatus의 name() 값 반환
     *
     * @return UserStatus name (예: "ACTIVE", "INACTIVE", "DELETED")
     * @author development-team
     * @since 1.0.0
     */
    public String statusValue() {
        return userStatus.name();
    }

    /**
     * isNew - User가 새로 생성된 객체인지 확인
     *
     * @return userId가 null이면 true, 아니면 false
     * @author development-team
     * @since 1.0.0
     */
    public boolean isNew() {
        return userId == null;
    }

    /**
     * createdAt - 생성 시간 반환
     *
     * @return 생성 시간 (Instant)
     * @author development-team
     * @since 1.0.0
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * updatedAt - 수정 시간 반환
     *
     * @return 수정 시간 (Instant)
     * @author development-team
     * @since 1.0.0
     */
    public Instant updatedAt() {
        return updatedAt;
    }

    /**
     * isActive - User가 활성 상태인지 확인
     *
     * @return 상태가 ACTIVE이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE;
    }

    /**
     * isDeleted - User가 삭제 상태인지 확인
     *
     * @return 상태가 DELETED이면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean isDeleted() {
        return userStatus == UserStatus.DELETED;
    }

    /**
     * hasOrganization - User가 Organization에 속해있는지 확인
     *
     * @return organizationId가 null이 아니면 true
     * @author development-team
     * @since 1.0.0
     */
    public boolean hasOrganization() {
        return organizationId != null;
    }

    // ========== Legacy Getters (호환성 유지) ==========

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
        return Objects.equals(userId, user.userId)
                && Objects.equals(tenantId, user.tenantId)
                && Objects.equals(organizationId, user.organizationId)
                && userType == user.userType
                && userStatus == user.userStatus
                && Objects.equals(createdAt, user.createdAt)
                && Objects.equals(updatedAt, user.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId, tenantId, organizationId, userType, userStatus, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "User{"
                + "userId="
                + userId
                + ", tenantId="
                + tenantId
                + ", organizationId="
                + organizationId
                + ", userType="
                + userType
                + ", userStatus="
                + userStatus
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }
}
