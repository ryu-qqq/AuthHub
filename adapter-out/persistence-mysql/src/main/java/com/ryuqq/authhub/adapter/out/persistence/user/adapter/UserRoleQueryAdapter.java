package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserRoleQueryDslRepository;
import com.ryuqq.authhub.application.user.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserRoleQueryAdapter - 사용자 역할 Query Adapter
 *
 * <p><strong>CQRS Query 패턴:</strong>
 *
 * <ul>
 *   <li>QueryDslRepository를 의존하여 조회 전용 쿼리 수행
 *   <li>Mapper를 통한 Entity → Domain 변환
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Port 구현체
 *   <li>QueryDslRepository 의존 (CQRS Query 패턴)
 *   <li>Mapper를 통한 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleQueryAdapter implements UserRoleQueryPort {

    private final UserRoleQueryDslRepository queryDslRepository;
    private final UserRoleJpaEntityMapper mapper;

    public UserRoleQueryAdapter(
            UserRoleQueryDslRepository queryDslRepository, UserRoleJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return queryDslRepository
                .findByUserIdAndRoleId(userId.value(), roleId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<UserRole> findAllByUserId(UserId userId) {
        return queryDslRepository.findAllByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return queryDslRepository.existsByUserIdAndRoleId(userId.value(), roleId.value());
    }
}
