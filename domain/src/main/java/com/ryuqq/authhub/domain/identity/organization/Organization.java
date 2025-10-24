package com.ryuqq.authhub.domain.identity.organization;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationType;

import java.time.Instant;
import java.util.Objects;

/**
 * Organization Aggregate Root.
 *
 * <p>조직 도메인의 Aggregate Root로서, 조직의 기본 정보와 상태를 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>조직 식별자(OrganizationId) 관리</li>
 *   <li>조직 소유자(UserId) 관리 - Long FK 패턴 대신 Value Object 사용</li>
 *   <li>조직명(OrganizationName) 관리 및 중복 검증</li>
 *   <li>조직 타입(OrganizationType) 관리 - SELLER, COMPANY</li>
 *   <li>조직 상태(OrganizationStatus) 관리 - ACTIVE, SUSPENDED, DELETED</li>
 *   <li>조직 라이프사이클 관리 - 활성화, 정지, 삭제</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ UserId는 Value Object로 참조 - JPA 관계 어노테이션 금지</li>
 *   <li>✅ Factory Method 패턴 - 타입별 생성 로직 분리</li>
 *   <li>✅ 불변성 보장 - 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>조직은 반드시 고유한 OrganizationId를 가져야 함</li>
 *   <li>조직은 반드시 소유자 UserId를 참조해야 함</li>
 *   <li>조직명은 중복 불가 (Application Layer에서 검증)</li>
 *   <li>조직 타입은 생성 시 결정되며 변경 불가 (SELLER 또는 COMPANY)</li>
 *   <li>조직 상태는 ACTIVE → SUSPENDED → DELETED 흐름을 따름</li>
 *   <li>삭제된 조직은 복구 불가</li>
 * </ul>
 *
 * <p><strong>도메인 이벤트:</strong></p>
 * <ul>
 *   <li>조직 생성 시 OrganizationCreatedEvent 발행</li>
 *   <li>조직 상태 변경 시 Application Layer에서 이벤트 발행</li>
 *   <li>Domain Layer는 Framework 의존성 없이 Pure Java로 구현</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class Organization {

    private final OrganizationId id;
    private final UserId ownerId;
    private final OrganizationName name;
    private final OrganizationType type;
    private final OrganizationStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Organization 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 조직 식별자 (null 불가)
     * @param ownerId 소유자 식별자 (null 불가)
     * @param name 조직명 (null 불가)
     * @param type 조직 타입 (null 불가)
     * @param status 조직 상태 (null 불가)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private Organization(
            final OrganizationId id,
            final UserId ownerId,
            final OrganizationName name,
            final OrganizationType type,
            final OrganizationStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "OrganizationId cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "OwnerId cannot be null");
        this.name = Objects.requireNonNull(name, "OrganizationName cannot be null");
        this.type = Objects.requireNonNull(type, "OrganizationType cannot be null");
        this.status = Objects.requireNonNull(status, "OrganizationStatus cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    /**
     * SELLER 타입의 조직을 생성합니다 (Factory Method).
     * B2C 플랫폼의 판매자 조직을 위한 팩토리 메서드입니다.
     *
     * @param ownerId 소유자 식별자 (null 불가)
     * @param name 조직명 (null 불가)
     * @return SELLER 타입의 새로운 Organization 인스턴스
     * @throws NullPointerException ownerId 또는 name이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Organization createSeller(final UserId ownerId, final OrganizationName name) {
        return create(ownerId, name, OrganizationType.SELLER);
    }

    /**
     * COMPANY 타입의 조직을 생성합니다 (Factory Method).
     * B2B 플랫폼의 기업 조직을 위한 팩토리 메서드입니다.
     *
     * @param ownerId 소유자 식별자 (null 불가)
     * @param name 조직명 (null 불가)
     * @return COMPANY 타입의 새로운 Organization 인스턴스
     * @throws NullPointerException ownerId 또는 name이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Organization createCompany(final UserId ownerId, final OrganizationName name) {
        return create(ownerId, name, OrganizationType.COMPANY);
    }

    /**
     * 지정된 타입의 조직을 생성하는 공통 헬퍼 메서드.
     * 조직 생성 로직의 중복을 제거하고 일관성을 보장합니다.
     *
     * @param ownerId 소유자 식별자 (null 불가)
     * @param name 조직명 (null 불가)
     * @param type 조직 타입 (null 불가)
     * @return 지정된 타입의 새로운 Organization 인스턴스
     * @throws NullPointerException ownerId, name 또는 type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private static Organization create(
            final UserId ownerId,
            final OrganizationName name,
            final OrganizationType type
    ) {
        final Instant now = Instant.now();
        return new Organization(
                OrganizationId.newId(),
                ownerId,
                name,
                type,
                OrganizationStatus.ACTIVE,
                now,
                now
        );
    }

    /**
     * 기존 데이터로부터 Organization을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 조직 식별자 (null 불가)
     * @param ownerId 소유자 식별자 (null 불가)
     * @param name 조직명 (null 불가)
     * @param type 조직 타입 (null 불가)
     * @param status 조직 상태 (null 불가)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 재구성된 Organization 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Organization reconstruct(
            final OrganizationId id,
            final UserId ownerId,
            final OrganizationName name,
            final OrganizationType type,
            final OrganizationStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new Organization(id, ownerId, name, type, status, createdAt, updatedAt);
    }

    /**
     * 조직 식별자를 반환합니다.
     *
     * @return OrganizationId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public OrganizationId getId() {
        return this.id;
    }

    /**
     * 소유자 식별자를 반환합니다.
     *
     * @return UserId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getOwnerId() {
        return this.ownerId;
    }

    /**
     * 조직명을 반환합니다.
     *
     * @return OrganizationName 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public OrganizationName getName() {
        return this.name;
    }

    /**
     * 조직 타입을 반환합니다.
     *
     * @return OrganizationType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public OrganizationType getType() {
        return this.type;
    }

    /**
     * 조직 상태를 반환합니다.
     *
     * @return OrganizationStatus enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public OrganizationStatus getStatus() {
        return this.status;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 수정 시각을 반환합니다.
     *
     * @return 수정 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * 조직명 문자열을 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - name.getValue() 대신 사용.
     *
     * @return 조직명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getNameValue() {
        return this.name.getValue();
    }

    /**
     * 조직 타입 설명을 반환합니다 (Law of Demeter 준수).
     *
     * @return 조직 타입 설명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTypeDescription() {
        return this.type.getDescription();
    }

    /**
     * 조직 상태 설명을 반환합니다 (Law of Demeter 준수).
     *
     * @return 조직 상태 설명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getStatusDescription() {
        return this.status.getDescription();
    }

    /**
     * 조직이 SELLER 타입인지 확인합니다.
     *
     * @return SELLER 타입이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSeller() {
        return this.type == OrganizationType.SELLER;
    }

    /**
     * 조직이 COMPANY 타입인지 확인합니다.
     *
     * @return COMPANY 타입이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isCompany() {
        return this.type == OrganizationType.COMPANY;
    }

    /**
     * 조직이 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    /**
     * 조직이 정지 상태인지 확인합니다.
     *
     * @return 정지 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSuspended() {
        return this.status.isSuspended();
    }

    /**
     * 조직이 삭제 상태인지 확인합니다.
     *
     * @return 삭제 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDeleted() {
        return this.status.isDeleted();
    }

    /**
     * 조직이 운영 가능한 상태인지 확인합니다 (ACTIVE 상태).
     *
     * @return 운영 가능하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isOperational() {
        return this.status.isOperational();
    }

    /**
     * 조직명을 변경합니다.
     * 불변성 원칙에 따라 새로운 Organization 인스턴스를 반환합니다.
     *
     * @param newName 새로운 조직명 (null 불가)
     * @return 조직명이 변경된 새로운 Organization 인스턴스
     * @throws NullPointerException newName이 null인 경우
     * @throws IllegalStateException 조직이 삭제 상태인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Organization updateName(final OrganizationName newName) {
        Objects.requireNonNull(newName, "New organization name cannot be null");

        if (this.isDeleted()) {
            throw new IllegalStateException("Cannot update name of a deleted organization");
        }

        if (this.name.equals(newName)) {
            return this; // 동일한 조직명이면 현재 인스턴스 반환
        }

        return new Organization(
                this.id,
                this.ownerId,
                newName,
                this.type,
                this.status,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 조직을 정지 상태로 전환합니다.
     * 정책 위반, 결제 미납 등의 사유로 일시적으로 기능이 제한됩니다.
     * 불변성 원칙에 따라 새로운 Organization 인스턴스를 반환합니다.
     *
     * @return 정지 상태로 변경된 새로운 Organization 인스턴스
     * @throws IllegalStateException 조직이 활성 상태가 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Organization suspend() {
        if (!this.isActive()) {
            throw new IllegalStateException("Only active organizations can be suspended");
        }

        return new Organization(
                this.id,
                this.ownerId,
                this.name,
                this.type,
                OrganizationStatus.SUSPENDED,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 조직을 활성 상태로 전환합니다 (정지 해제).
     * 정지 사유가 해결되어 정상 운영을 재개합니다.
     * 불변성 원칙에 따라 새로운 Organization 인스턴스를 반환합니다.
     *
     * @return 활성 상태로 변경된 새로운 Organization 인스턴스
     * @throws IllegalStateException 조직이 정지 상태가 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Organization activate() {
        if (!this.isSuspended()) {
            throw new IllegalStateException("Only suspended organizations can be activated");
        }

        return new Organization(
                this.id,
                this.ownerId,
                this.name,
                this.type,
                OrganizationStatus.ACTIVE,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 조직을 삭제 상태로 전환합니다 (논리적 삭제).
     * 삭제된 조직은 복구할 수 없으며, 모든 기능이 차단됩니다.
     * 불변성 원칙에 따라 새로운 Organization 인스턴스를 반환합니다.
     *
     * @return 삭제 상태로 변경된 새로운 Organization 인스턴스
     * @throws IllegalStateException 조직이 이미 삭제 상태인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Organization delete() {
        if (this.isDeleted()) {
            throw new IllegalStateException("Organization is already deleted");
        }

        return new Organization(
                this.id,
                this.ownerId,
                this.name,
                this.type,
                OrganizationStatus.DELETED,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 두 Organization 객체의 동등성을 비교합니다.
     * OrganizationId가 같으면 같은 조직으로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return OrganizationId가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Organization other = (Organization) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * OrganizationId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * Organization의 문자열 표현을 반환합니다.
     *
     * @return "Organization{id=..., ownerId=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "Organization{" +
                "id=" + this.id +
                ", ownerId=" + this.ownerId +
                ", name=" + this.name +
                ", type=" + this.type +
                ", status=" + this.status +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
