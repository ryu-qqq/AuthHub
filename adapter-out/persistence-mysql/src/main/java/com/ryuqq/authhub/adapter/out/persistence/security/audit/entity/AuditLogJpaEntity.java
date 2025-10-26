package com.ryuqq.authhub.adapter.out.persistence.security.audit.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * AuditLog JPA Entity.
 *
 * <p>AuditLog Aggregate를 관계형 데이터베이스에 영속화하기 위한 JPA Entity입니다.
 * Hexagonal Architecture의 Persistence Adapter 계층에 위치하며, Domain Layer와 분리됩니다.</p>
 *
 * <p><strong>인덱스 전략:</strong></p>
 * <ul>
 *   <li>idx_audit_user_action - (user_id, action_type) 복합 인덱스: 사용자별 액션 조회 최적화</li>
 *   <li>idx_audit_occurred_at - 발생 시각 인덱스: 시간 범위 조회 최적화</li>
 *   <li>idx_audit_resource - (resource_type, resource_id) 복합 인덱스: 리소스별 조회 최적화</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ Long FK 전략 - JPA 관계 어노테이션 절대 금지 ({@code @ManyToOne}, {@code @OneToMany} 등)</li>
 *   <li>✅ Hibernate 전용 protected 생성자 제공</li>
 *   <li>✅ 불변성 지향 - setter는 package-private으로 제한</li>
 *   <li>✅ UUID 기반 ID - String 타입으로 저장</li>
 *   <li>✅ Enum은 STRING으로 저장 - EnumType.STRING 사용</li>
 * </ul>
 *
 * <p><strong>Long FK 전략 예시:</strong></p>
 * <pre>
 * ❌ 잘못된 방식 (관계 어노테이션 사용):
 *    {@code @ManyToOne}
 *    {@code private UserJpaEntity user;}
 *
 * ✅ 올바른 방식 (Long FK 사용):
 *    {@code private Long userId;}
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user_action", columnList = "user_id, action_type"),
                @Index(name = "idx_audit_occurred_at", columnList = "occurred_at"),
                @Index(name = "idx_audit_resource", columnList = "resource_type, resource_id")
        }
)
public class AuditLogJpaEntity {

    /**
     * 데이터베이스 기본 키 (Auto Increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 감사 로그의 고유 식별자 (UUID 기반, 도메인 ID).
     * Domain의 AuditLogId.asString()와 매핑됩니다.
     */
    @Column(name = "audit_log_id", nullable = false, unique = true, length = 36, updatable = false)
    private String auditLogId;

    /**
     * 사용자 ID (Long FK 전략).
     * ❌ {@code @ManyToOne UserJpaEntity} 금지
     * ✅ Long userId 사용
     * Domain의 UserId.asString()를 Long으로 변환하여 저장합니다.
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 액션 타입 (LOGIN, LOGOUT, CREATE, UPDATE, DELETE).
     * Domain의 ActionType enum과 매핑됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionTypeEnum actionType;

    /**
     * 리소스 타입 (USER, ORGANIZATION, COMPANY).
     * Domain의 ResourceType enum과 매핑됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 50)
    private ResourceTypeEnum resourceType;

    /**
     * 리소스 ID (nullable - 일부 액션은 특정 리소스 없이 발생).
     */
    @Column(name = "resource_id", length = 255)
    private String resourceId;

    /**
     * IP 주소 (IPv4 최대 15자, IPv6 최대 45자).
     */
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    /**
     * User-Agent (브라우저/클라이언트 정보).
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * 감사 로그 발생 시각.
     */
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    /**
     * Hibernate 전용 기본 생성자 (protected).
     * 외부에서 직접 호출 금지, Hibernate만 사용합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    protected AuditLogJpaEntity() {
        // Hibernate용 기본 생성자
    }

    /**
     * AuditLogJpaEntity 생성자 (private).
     * 외부에서는 of() 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param auditLogId 감사 로그 ID
     * @param userId 사용자 ID
     * @param actionType 액션 타입
     * @param resourceType 리소스 타입
     * @param resourceId 리소스 ID
     * @param ipAddress IP 주소
     * @param userAgent User-Agent
     * @param occurredAt 발생 시각
     */
    private AuditLogJpaEntity(
            final String auditLogId,
            final String userId,
            final ActionTypeEnum actionType,
            final ResourceTypeEnum resourceType,
            final String resourceId,
            final String ipAddress,
            final String userAgent,
            final Instant occurredAt
    ) {
        this.auditLogId = auditLogId;
        this.userId = userId;
        this.actionType = actionType;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.occurredAt = occurredAt;
    }

    /**
     * AuditLogJpaEntity를 생성합니다 (Factory Method).
     *
     * @param auditLogId 감사 로그 ID
     * @param userId 사용자 ID
     * @param actionType 액션 타입
     * @param resourceType 리소스 타입
     * @param resourceId 리소스 ID
     * @param ipAddress IP 주소
     * @param userAgent User-Agent
     * @param occurredAt 발생 시각
     * @return 생성된 AuditLogJpaEntity 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static AuditLogJpaEntity of(
            final String auditLogId,
            final String userId,
            final ActionTypeEnum actionType,
            final ResourceTypeEnum resourceType,
            final String resourceId,
            final String ipAddress,
            final String userAgent,
            final Instant occurredAt
    ) {
        return new AuditLogJpaEntity(
                auditLogId,
                userId,
                actionType,
                resourceType,
                resourceId,
                ipAddress,
                userAgent,
                occurredAt
        );
    }

    /**
     * 데이터베이스 기본 키를 반환합니다.
     *
     * @return 데이터베이스 기본 키
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 감사 로그 ID를 반환합니다.
     *
     * @return 감사 로그 ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getAuditLogId() {
        return this.auditLogId;
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * 액션 타입을 반환합니다.
     *
     * @return 액션 타입
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ActionTypeEnum getActionType() {
        return this.actionType;
    }

    /**
     * 리소스 타입을 반환합니다.
     *
     * @return 리소스 타입
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ResourceTypeEnum getResourceType() {
        return this.resourceType;
    }

    /**
     * 리소스 ID를 반환합니다.
     *
     * @return 리소스 ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getResourceId() {
        return this.resourceId;
    }

    /**
     * IP 주소를 반환합니다.
     *
     * @return IP 주소
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    /**
     * User-Agent를 반환합니다.
     *
     * @return User-Agent
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserAgent() {
        return this.userAgent;
    }

    /**
     * 발생 시각을 반환합니다.
     *
     * @return 발생 시각
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    /**
     * 두 AuditLogJpaEntity 객체의 동등성을 비교합니다.
     * auditLogId가 같으면 같은 감사 로그로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return auditLogId가 같으면 true, 아니면 false
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
        AuditLogJpaEntity other = (AuditLogJpaEntity) obj;
        return Objects.equals(this.auditLogId, other.auditLogId);
    }

    /**
     * 해시 코드를 반환합니다.
     * auditLogId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.auditLogId);
    }

    /**
     * AuditLogJpaEntity의 문자열 표현을 반환합니다.
     *
     * @return "AuditLogJpaEntity{id=..., auditLogId=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return String.format("AuditLogJpaEntity{id=%d, auditLogId='%s', userId='%s', actionType=%s, resourceType=%s, resourceId='%s', ipAddress='%s', occurredAt=%s}",
                this.id,
                this.auditLogId,
                this.userId,
                this.actionType,
                this.resourceType,
                this.resourceId,
                this.ipAddress,
                this.occurredAt
        );
    }
}
