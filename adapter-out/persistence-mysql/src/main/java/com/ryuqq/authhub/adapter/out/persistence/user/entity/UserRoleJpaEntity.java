package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

/**
 * UserRoleJpaEntity - 사용자 역할 매핑 JPA Entity
 *
 * <p>User와 Role 간의 N:M 관계를 나타내는 중간 테이블입니다.
 *
 * <p><strong>Unique 제약:</strong>
 *
 * <ul>
 *   <li>userId + roleId 조합으로 유니크
 *   <li>동일 역할 중복 할당 방지
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
public class UserRoleJpaEntity {

    /** 기본 키 - AUTO_INCREMENT (내부 Long ID) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 사용자 UUID */
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    /** 역할 UUID */
    @Column(name = "role_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID roleId;

    /** 할당 시간 */
    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    /**
     * JPA 기본 생성자 (protected)
     *
     * <p>JPA 스펙 요구사항으로 반드시 필요합니다.
     */
    protected UserRoleJpaEntity() {}

    /**
     * 전체 필드 생성자 (private)
     *
     * <p>직접 호출 금지, of() 스태틱 메서드로만 생성하세요.
     */
    private UserRoleJpaEntity(Long id, UUID userId, UUID roleId, Instant assignedAt) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
    }

    /**
     * of() 스태틱 팩토리 메서드 (Mapper 전용)
     *
     * <p>Entity 생성은 반드시 이 메서드를 통해서만 가능합니다.
     *
     * @param id 내부 기본 키 (신규 생성 시 null)
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     * @param assignedAt 할당 시간
     * @return UserRoleJpaEntity 인스턴스
     */
    public static UserRoleJpaEntity of(Long id, UUID userId, UUID roleId, Instant assignedAt) {
        return new UserRoleJpaEntity(id, userId, roleId, assignedAt);
    }

    // ===== Getters (Setter 제공 금지) =====

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }
}
