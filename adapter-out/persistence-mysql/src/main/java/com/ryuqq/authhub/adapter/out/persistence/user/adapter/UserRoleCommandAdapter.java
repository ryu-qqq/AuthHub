package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.application.user.port.out.persistence.UserRolePersistencePort;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;

/**
 * UserRoleCommandAdapter - 사용자 역할 Command Adapter
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Port 구현체
 *   <li>Mapper를 통한 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleCommandAdapter implements UserRolePersistencePort {

    private final UserRoleJpaRepository repository;
    private final UserRoleJpaEntityMapper mapper;

    public UserRoleCommandAdapter(
            UserRoleJpaRepository repository, UserRoleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleJpaEntity entity = mapper.toEntity(userRole);
        UserRoleJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UserId userId, RoleId roleId) {
        repository.deleteByUserIdAndRoleId(userId.value(), roleId.value());
    }
}
