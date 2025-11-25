package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import com.ryuqq.authhub.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * UserJpaEntity - User JPA Entity
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    protected UserJpaEntity() {}

    private UserJpaEntity(
            Long id,
            String email,
            String password,
            String username,
            Long organizationId,
            Long tenantId,
            UserType userType,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.userType = userType;
        this.status = status;
    }

    public static UserJpaEntity of(
            Long id,
            String email,
            String password,
            String username,
            Long organizationId,
            Long tenantId,
            UserType userType,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new UserJpaEntity(
                id,
                email,
                password,
                username,
                organizationId,
                tenantId,
                userType,
                status,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserStatus getStatus() {
        return status;
    }
}
