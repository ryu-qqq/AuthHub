package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserJpaEntity - 사용자 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>userId(UUID)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>Unique 제약:</strong>
 *
 * <ul>
 *   <li>tenantId + organizationId + identifier 조합으로 유니크
 *   <li>같은 조직 내 동일 식별자 금지
 * </ul>
 *
 * <p><strong>Lombok 금지:</strong>
 *
 * <ul>
 *   <li>Plain Java getter 사용
 *   <li>Setter 제공 금지
 *   <li>명시적 생성자 제공
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_users_tenant_org_identifier",
                    columnNames = {"tenant_id", "organization_id", "identifier"}),
            @UniqueConstraint(
                    name = "uk_users_tenant_phone",
                    columnNames = {"tenant_id", "phone_number"})
        },
        indexes = {
            @Index(name = "idx_users_tenant_id", columnList = "tenant_id"),
            @Index(name = "idx_users_organization_id", columnList = "organization_id"),
            @Index(name = "idx_users_identifier", columnList = "identifier"),
            @Index(name = "idx_users_phone_number", columnList = "phone_number"),
            @Index(name = "idx_users_status", columnList = "status")
        })
public class UserJpaEntity extends BaseAuditEntity {

    /** 사용자 UUID - UUIDv7 (Primary Key) */
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    /** 테넌트 UUID */
    @Column(name = "tenant_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID tenantId;

    /** 조직 UUID */
    @Column(name = "organization_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID organizationId;

    /** 사용자 식별자 (이메일 등) */
    @Column(name = "identifier", nullable = false, length = 255)
    private String identifier;

    /** 핸드폰 번호 (한국 형식, 필수) */
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    /** 해시된 비밀번호 */
    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    /** 사용자 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected UserJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private UserJpaEntity(
            UUID userId,
            UUID tenantId,
            UUID organizationId,
            String identifier,
            String phoneNumber,
            String hashedPassword,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.userId = userId;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.identifier = identifier;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.status = status;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param userId 사용자 UUID (PK)
     * @param tenantId 테넌트 UUID
     * @param organizationId 조직 UUID
     * @param identifier 사용자 식별자
     * @param phoneNumber 핸드폰 번호
     * @param hashedPassword 해시된 비밀번호
     * @param status 사용자 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return UserJpaEntity 인스턴스
     */
    public static UserJpaEntity of(
            UUID userId,
            UUID tenantId,
            UUID organizationId,
            String identifier,
            String phoneNumber,
            String hashedPassword,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new UserJpaEntity(
                userId,
                tenantId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                status,
                createdAt,
                updatedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public UUID getUserId() {
        return userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getStatus() {
        return status;
    }
}
