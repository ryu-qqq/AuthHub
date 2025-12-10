package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper.EndpointPermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository.EndpointPermissionQueryDslRepository;
import com.ryuqq.authhub.application.permission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionSpecPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionSpecQueryAdapter - 권한 명세 조회 어댑터
 *
 * <p>EndpointPermission 테이블에서 권한 명세를 조회하여 Application Layer의 PermissionSpecPort를 구현합니다. Gateway에서
 * 권한 검증을 위해 사용되는 GET /api/v1/permissions/spec API에 데이터를 제공합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Port 구현체
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (단순 조회 및 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionSpecQueryAdapter implements PermissionSpecPort {

    private static final String SERVICE_NAME = "authhub";

    private final EndpointPermissionQueryDslRepository queryDslRepository;
    private final EndpointPermissionJpaEntityMapper mapper;

    public PermissionSpecQueryAdapter(
            EndpointPermissionQueryDslRepository queryDslRepository,
            EndpointPermissionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public List<EndpointPermissionResponse> getEndpointPermissions() {
        return queryDslRepository.findAllByServiceName(SERVICE_NAME).stream()
                .map(mapper::toDomain)
                .map(
                        domain ->
                                EndpointPermissionResponse.of(
                                        domain.serviceNameValue(),
                                        domain.pathValue(),
                                        domain.methodValue(),
                                        domain.requiredPermissionValues().stream().toList(),
                                        domain.requiredRoleValues().stream().toList(),
                                        domain.isPublic()))
                .toList();
    }

    @Override
    public long countActivePermissions() {
        return queryDslRepository.countByServiceName(SERVICE_NAME);
    }
}
