package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * UserJpaEntity - 사용자 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 데이터베이스 테이블과 매핑됩니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>userId(String)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>String FK 전략:</strong>
 *
 * <ul>
 *   <li>organizationId는 String 타입으로 관리 (JPA 관계 어노테이션 금지)
 *   <li>조직과의 관계는 String ID를 통해 애플리케이션에서 관리
 * </ul>
 *
 * <p><strong>SoftDeletableEntity 상속:</strong>
 *
 * <ul>
 *   <li>createdAt, updatedAt (BaseAuditEntity)
 *   <li>deletedAt (SoftDeletableEntity)
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
                    name = "uk_users_org_identifier",
                    columnNames = {"organization_id", "identifier"})
        },
        indexes = {
            @Index(name = "idx_users_organization_id", columnList = "organization_id"),
            @Index(name = "idx_users_identifier", columnList = "identifier"),
            @Index(name = "idx_users_status", columnList = "status")
        })
public class UserJpaEntity extends SoftDeletableEntity {

    /** 사용자 UUID - UUIDv7 (Primary Key, String 저장) */
    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** 조직 UUID - FK (String FK 전략: JPA 관계 어노테이션 금지) */
    @Column(name = "organization_id", nullable = false, length = 36)
    private String organizationId;

    /** 로그인 식별자 (이메일 또는 사용자명) */
    @Column(name = "identifier", nullable = false, length = 100)
    private String identifier;

    /** 전화번호 (선택) */
    @Column(name = "phone_number", length = 20)
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
     *
     * @param userId 사용자 UUID (PK, String)
     * @param organizationId 조직 UUID (String)
     * @param identifier 로그인 식별자
     * @param phoneNumber 전화번호 (nullable)
     * @param hashedPassword 해시된 비밀번호
     * @param status 사용자 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC, nullable)
     */
    private UserJpaEntity(
            String userId,
            String organizationId,
            String identifier,
            String phoneNumber,
            String hashedPassword,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.userId = userId;
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
     * @param userId 사용자 UUID (PK, String)
     * @param organizationId 조직 UUID (String)
     * @param identifier 로그인 식별자
     * @param phoneNumber 전화번호 (nullable)
     * @param hashedPassword 해시된 비밀번호
     * @param status 사용자 상태
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @param deletedAt 삭제 일시 (Instant, UTC, nullable)
     * @return UserJpaEntity 인스턴스
     */
    public static UserJpaEntity of(
            String userId,
            String organizationId,
            String identifier,
            String phoneNumber,
            String hashedPassword,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new UserJpaEntity(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                status,
                createdAt,
                updatedAt,
                deletedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public String getUserId() {
        return userId;
    }

    public String getOrganizationId() {
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
