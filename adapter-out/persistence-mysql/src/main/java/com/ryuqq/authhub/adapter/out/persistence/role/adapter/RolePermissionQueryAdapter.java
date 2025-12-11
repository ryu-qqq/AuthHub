package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.application.role.port.out.query.RolePermissionQueryPort;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RolePermissionQueryAdapter - 역할 권한 Query Adapter
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
public class RolePermissionQueryAdapter implements RolePermissionQueryPort {

    private final RolePermissionQueryDslRepository queryDslRepository;
    private final RolePermissionJpaEntityMapper mapper;

    public RolePermissionQueryAdapter(
            RolePermissionQueryDslRepository queryDslRepository,
            RolePermissionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(
            RoleId roleId, PermissionId permissionId) {
        return queryDslRepository
                .findByRoleIdAndPermissionId(roleId.value(), permissionId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<RolePermission> findAllByRoleId(RoleId roleId) {
        return queryDslRepository.findAllByRoleId(roleId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByRoleIdAndPermissionId(RoleId roleId, PermissionId permissionId) {
        return queryDslRepository.existsByRoleIdAndPermissionId(
                roleId.value(), permissionId.value());
    }

    @Override
    public List<RolePermission> findAllByRoleIds(Set<RoleId> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        Set<UUID> uuids = roleIds.stream().map(RoleId::value).collect(Collectors.toSet());
        return queryDslRepository.findAllByRoleIds(uuids).stream().map(mapper::toDomain).toList();
    }
}
