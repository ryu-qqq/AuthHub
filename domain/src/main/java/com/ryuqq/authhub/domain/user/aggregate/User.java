package com.ryuqq.authhub.domain.user.aggregate;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * User Aggregate Root - 사용자 도메인 모델
 *
 * <p>시스템 내 사용자를 정의하는 Aggregate입니다.
 *
 * <p><strong>필드 설명:</strong>
 *
 * <ul>
 *   <li>userId: 사용자 고유 식별자 (UUIDv7)
 *   <li>organizationId: 소속 조직 ID
 *   <li>identifier: 로그인 시 사용하는 식별자 (이메일 또는 사용자명)
 *   <li>phoneNumber: 전화번호 (선택)
 *   <li>hashedPassword: 해시된 비밀번호
 *   <li>status: 사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)
 *   <li>deletionStatus: 삭제 상태 (soft delete 관리)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지
 *   <li>Tell, Don't Ask 패턴 - 상태 질의 대신 행위 위임
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지
 *   <li>Null 검증은 생성 시점에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class User {

    private final UserId userId;
    private final OrganizationId organizationId;
    private final Identifier identifier;
    private PhoneNumber phoneNumber;
    private HashedPassword hashedPassword;
    private UserStatus status;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(
            UserId userId,
            OrganizationId organizationId,
            Identifier identifier,
            PhoneNumber phoneNumber,
            HashedPassword hashedPassword,
            UserStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        validateRequired(organizationId, identifier, hashedPassword);
        this.userId = userId;
        this.organizationId = organizationId;
        this.identifier = identifier;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateRequired(
            OrganizationId organizationId, Identifier identifier, HashedPassword hashedPassword) {
        if (organizationId == null) {
            throw new IllegalArgumentException("organizationId는 null일 수 없습니다");
        }
        if (identifier == null) {
            throw new IllegalArgumentException("identifier는 null일 수 없습니다");
        }
        if (hashedPassword == null) {
            throw new IllegalArgumentException("hashedPassword는 null일 수 없습니다");
        }
    }

    // ========== Factory Methods ==========

    /**
     * 새로운 사용자 생성
     *
     * @param userId 사용자 ID (외부에서 생성된 UUIDv7)
     * @param organizationId 소속 조직 ID
     * @param identifier 로그인 식별자
     * @param phoneNumber 전화번호 (nullable)
     * @param hashedPassword 해시된 비밀번호
     * @param now 현재 시간 (외부 주입)
     * @return 새로운 User 인스턴스
     */
    public static User create(
            UserId userId,
            OrganizationId organizationId,
            Identifier identifier,
            PhoneNumber phoneNumber,
            HashedPassword hashedPassword,
            Instant now) {
        if (userId == null) {
            throw new IllegalArgumentException("새 사용자 생성 시 userId는 필수입니다");
        }
        return new User(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * DB에서 User 재구성 (reconstitute)
     *
     * @param userId 사용자 ID
     * @param organizationId 소속 조직 ID
     * @param identifier 로그인 식별자
     * @param phoneNumber 전화번호 (nullable)
     * @param hashedPassword 해시된 비밀번호
     * @param status 사용자 상태
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     * @return 재구성된 User 인스턴스
     */
    public static User reconstitute(
            UserId userId,
            OrganizationId organizationId,
            Identifier identifier,
            PhoneNumber phoneNumber,
            HashedPassword hashedPassword,
            UserStatus status,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new User(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                status,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    // ========== Business Methods ==========

    /**
     * 사용자 정보 수정 (UpdateData 패턴)
     *
     * @param updateData 수정할 데이터
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void update(UserUpdateData updateData, Instant changedAt) {
        if (updateData.hasPhoneNumber()) {
            this.phoneNumber = updateData.phoneNumber();
        }
        this.updatedAt = changedAt;
    }

    /**
     * 비밀번호 변경
     *
     * @param newHashedPassword 새로운 해시된 비밀번호
     * @param changedAt 변경 시간 (외부 주입)
     */
    public void changePassword(HashedPassword newHashedPassword, Instant changedAt) {
        if (newHashedPassword == null) {
            throw new IllegalArgumentException("newHashedPassword는 null일 수 없습니다");
        }
        this.hashedPassword = newHashedPassword;
        this.updatedAt = changedAt;
    }

    /**
     * 사용자 활성화
     *
     * @param now 활성화 시간 (외부 주입)
     */
    public void activate(Instant now) {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = now;
    }

    /**
     * 사용자 비활성화
     *
     * @param now 비활성화 시간 (외부 주입)
     */
    public void deactivate(Instant now) {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = now;
    }

    /**
     * 사용자 정지
     *
     * @param now 정지 시간 (외부 주입)
     */
    public void suspend(Instant now) {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = now;
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     *
     * @param now 삭제 시간 (외부 주입)
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 사용자 복원 (삭제 취소)
     *
     * @param now 복원 시간 (외부 주입)
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // ========== Query Methods ==========

    /**
     * 사용자 ID 값 반환
     *
     * @return 사용자 ID (String)
     */
    public String userIdValue() {
        return userId != null ? userId.value() : null;
    }

    /**
     * 조직 ID 값 반환
     *
     * @return 조직 ID (String)
     */
    public String organizationIdValue() {
        return organizationId.value();
    }

    /**
     * 로그인 식별자 값 반환
     *
     * @return 식별자 (String)
     */
    public String identifierValue() {
        return identifier.value();
    }

    /**
     * 전화번호 값 반환
     *
     * @return 전화번호 (String) 또는 null
     */
    public String phoneNumberValue() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }

    /**
     * 해시된 비밀번호 값 반환
     *
     * @return 해시된 비밀번호 (String)
     */
    public String hashedPasswordValue() {
        return hashedPassword.value();
    }

    /**
     * 상태 값 반환
     *
     * @return 상태 문자열 (ACTIVE, INACTIVE, SUSPENDED, DELETED)
     */
    public String statusValue() {
        return status.name();
    }

    /**
     * 신규 생성 여부 확인
     *
     * @return ID가 없으면 true (신규)
     */
    public boolean isNew() {
        return userId == null;
    }

    /**
     * 활성 상태인지 확인
     *
     * @return ACTIVE면 true
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * 로그인 가능 상태인지 확인
     *
     * @return 로그인 가능하면 true
     */
    public boolean canLogin() {
        return status.canLogin();
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 정지 여부 확인
     *
     * @return SUSPENDED면 true
     */
    public boolean isSuspended() {
        return status.isSuspended();
    }

    // ========== Getter Methods ==========

    public UserId getUserId() {
        return userId;
    }

    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public HashedPassword getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getStatus() {
        return status;
    }

    public DeletionStatus getDeletionStatus() {
        return deletionStatus;
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
        User that = (User) o;
        if (userId == null || that.userId == null) {
            return Objects.equals(identifier, that.identifier)
                    && Objects.equals(organizationId, that.organizationId);
        }
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        if (userId != null) {
            return Objects.hash(userId);
        }
        return Objects.hash(identifier, organizationId);
    }

    @Override
    public String toString() {
        return "User{"
                + "userId="
                + userId
                + ", identifier='"
                + identifier.value()
                + '\''
                + ", status="
                + status
                + ", deleted="
                + deletionStatus.isDeleted()
                + '}';
    }
}
