package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;

/**
 * UserRoleJpaEntityMapper - 사용자 역할 Entity/Domain 변환기
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain ↔ Entity 양방향 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleJpaEntityMapper {

    /**
     * Domain 객체를 JPA Entity로 변환합니다.
     *
     * @param userRole 사용자 역할 도메인 객체
     * @return JPA Entity
     */
    public UserRoleJpaEntity toEntity(UserRole userRole) {
        return UserRoleJpaEntity.of(
                null, userRole.userIdValue(), userRole.roleIdValue(), userRole.getAssignedAt());
    }

    /**
     * JPA Entity를 Domain 객체로 변환합니다.
     *
     * @param entity JPA Entity
     * @return 사용자 역할 도메인 객체
     */
    public UserRole toDomain(UserRoleJpaEntity entity) {
        return UserRole.reconstitute(
                UserId.of(entity.getUserId()),
                RoleId.of(entity.getRoleId()),
                entity.getAssignedAt());
    }
}
