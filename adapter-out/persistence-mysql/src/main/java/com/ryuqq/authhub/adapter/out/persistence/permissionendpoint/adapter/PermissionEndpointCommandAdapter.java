package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper.PermissionEndpointJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.application.permissionendpoint.port.out.command.PermissionEndpointCommandPort;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointCommandAdapter - PermissionEndpoint Command Port 구현체
 *
 * <p>PermissionEndpoint Domain을 영속화하는 Adapter입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Port 구현체로 @Component 등록
 *   <li>Mapper 의존 필수
 *   <li>persist() 단일 메서드만 구현
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointCommandAdapter implements PermissionEndpointCommandPort {

    private final PermissionEndpointJpaRepository repository;
    private final PermissionEndpointJpaEntityMapper mapper;

    public PermissionEndpointCommandAdapter(
            PermissionEndpointJpaRepository repository, PermissionEndpointJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(PermissionEndpoint permissionEndpoint) {
        PermissionEndpointJpaEntity entity = mapper.toEntity(permissionEndpoint);
        PermissionEndpointJpaEntity saved = repository.save(entity);
        return saved.getPermissionEndpointId();
    }
}
