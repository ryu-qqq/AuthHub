package com.ryuqq.authhub.adapter.out.persistence.userrole.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * UserRoleJpaEntity - 사용자-역할 관계 JPA Entity
 *
 * <p>Persistence Layer의 JPA Entity로서 사용자-역할 관계 테이블과 매핑됩니다.
 *
 * <p><strong>PK 전략:</strong>
 *
 * <ul>
 *   <li>userRoleId(Long)를 PK로 사용 (Auto Increment)
 *   <li>관계 테이블이므로 UUIDv7 대신 Long 사용
 * </ul>
 *
 * <p><strong>String/Long FK 전략:</strong>
 *
 * <ul>
 *   <li>userId는 String 타입 (User의 UUIDv7)
 *   <li>roleId는 Long 타입 (Role의 Auto Increment)
 *   <li>JPA 관계 어노테이션 금지
 * </ul>
 *
 * <p><strong>Soft Delete 미적용:</strong>
 *
 * <ul>
 *   <li>관계 테이블이므로 Hard Delete 적용
 *   <li>BaseAuditEntity만 상속 (createdAt, updatedAt)
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
        name = "user_roles",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_roles_user_role",
                    columnNames = {"user_id", "role_id"})
        },
        indexes = {
            @Index(name = "idx_user_roles_user_id", columnList = "user_id"),
            @Index(name = "idx_user_roles_role_id", columnList = "role_id")
        })
public class UserRoleJpaEntity extends BaseAuditEntity {

    /** 사용자-역할 관계 ID (Primary Key, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id", nullable = false)
    private Long userRoleId;

    /** 사용자 UUID - FK (String FK 전략: JPA 관계 어노테이션 금지) */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** 역할 ID - FK (Long FK 전략: JPA 관계 어노테이션 금지) */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected UserRoleJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 또는 forNew() 스태틱 메서드로만 생성하세요.
     *
     * @param userRoleId 관계 ID (PK, Long) - null이면 신규
     * @param userId 사용자 UUID (String)
     * @param roleId 역할 ID (Long)
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     */
    private UserRoleJpaEntity(
            Long userRoleId, String userId, Long roleId, Instant createdAt, Instant updatedAt) {
        super(createdAt, updatedAt);
        this.userRoleId = userRoleId;
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용 - 재구성용)
     *
     * <p>DB에서 조회한 데이터를 Entity로 변환할 때 사용합니다.
     *
     * @param userRoleId 관계 ID (PK, Long)
     * @param userId 사용자 UUID (String)
     * @param roleId 역할 ID (Long)
     * @param createdAt 생성 일시 (Instant, UTC)
     * @param updatedAt 수정 일시 (Instant, UTC)
     * @return UserRoleJpaEntity 인스턴스
     */
    public static UserRoleJpaEntity of(
            Long userRoleId, String userId, Long roleId, Instant createdAt, Instant updatedAt) {
        return new UserRoleJpaEntity(userRoleId, userId, roleId, createdAt, updatedAt);
    }

    /**
     * forNew() 스태틱 팩토리 메서드 (신규 생성용)
     *
     * <p>새로운 사용자-역할 관계를 생성할 때 사용합니다. userRoleId는 null로 설정됩니다.
     *
     * @param userId 사용자 UUID (String)
     * @param roleId 역할 ID (Long)
     * @param createdAt 생성 일시 (Instant, UTC)
     * @return UserRoleJpaEntity 인스턴스 (userRoleId = null)
     */
    public static UserRoleJpaEntity forNew(String userId, Long roleId, Instant createdAt) {
        return new UserRoleJpaEntity(null, userId, roleId, createdAt, createdAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getUserRoleId() {
        return userRoleId;
    }

    public String getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }
}
