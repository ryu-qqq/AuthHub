package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * UserJpaEntity - User JPA Entity
 *
 * <p>User Domain 객체를 영속화하는 JPA Entity입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>JPA 관계 어노테이션 금지 (Long FK 전략)
 *   <li>of() 정적 팩토리 메서드 사용
 *   <li>기본 생성자는 protected (JPA용)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "organization_id")
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "identifier", nullable = false, length = 255)
    private String identifier;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    protected UserJpaEntity() {}

    private UserJpaEntity(
            UUID id,
            Long tenantId,
            Long organizationId,
            UserType userType,
            UserStatus status,
            String identifier,
            String hashedPassword,
            String name,
            String phoneNumber,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.userType = userType;
        this.status = status;
        this.identifier = identifier;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public static UserJpaEntity of(
            UUID id,
            Long tenantId,
            Long organizationId,
            UserType userType,
            UserStatus status,
            String identifier,
            String hashedPassword,
            String name,
            String phoneNumber,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new UserJpaEntity(
                id,
                tenantId,
                organizationId,
                userType,
                status,
                identifier,
                hashedPassword,
                name,
                phoneNumber,
                createdAt,
                updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserJpaEntity that = (UserJpaEntity) o;
        if (id == null || that.id == null) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "UserJpaEntity{"
                + "id="
                + id
                + ", tenantId="
                + tenantId
                + ", organizationId="
                + organizationId
                + ", userType="
                + userType
                + ", status="
                + status
                + ", identifier='"
                + identifier
                + "'"
                + "}";
    }
}
