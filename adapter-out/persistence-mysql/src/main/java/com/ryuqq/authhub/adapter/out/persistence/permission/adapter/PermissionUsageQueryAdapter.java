package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionUsageJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionUsageQueryDslRepository;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionUsageQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PermissionUsageQueryAdapter - 권한 사용 이력 Query Adapter (조회 전용)
 *
 * <p>PermissionUsageQueryPort 구현체로서 사용 이력 조회 작업을 담당합니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (ReadManager에서 readOnly 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionUsageQueryAdapter implements PermissionUsageQueryPort {

    private final PermissionUsageQueryDslRepository queryRepository;
    private final PermissionUsageJpaEntityMapper mapper;

    public PermissionUsageQueryAdapter(
            PermissionUsageQueryDslRepository queryRepository,
            PermissionUsageJpaEntityMapper mapper) {
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<PermissionUsage> findByKeyAndService(
            PermissionKey permissionKey, ServiceName serviceName) {
        return queryRepository
                .findByKeyAndService(permissionKey.value(), serviceName.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<PermissionUsage> findAllByPermissionKey(PermissionKey permissionKey) {
        return queryRepository.findAllByPermissionKey(permissionKey.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PermissionUsage> findAllByServiceName(ServiceName serviceName) {
        return queryRepository.findAllByServiceName(serviceName.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
