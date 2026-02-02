package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointQueryDslRepository;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointSpecQueryPort;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointSpecQueryAdapter - Gateway용 엔드포인트-권한 스펙 조회 Adapter
 *
 * <p>PermissionEndpoint와 Permission을 조인하여 Gateway가 필요로 하는 스펙 정보를 제공합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Port 구현체로 @Component 등록
 *   <li>Application DTO 반환 (조인 쿼리이므로 예외적으로 DTO 직접 반환)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointSpecQueryAdapter implements PermissionEndpointSpecQueryPort {

    private final PermissionEndpointQueryDslRepository queryDslRepository;

    public PermissionEndpointSpecQueryAdapter(
            PermissionEndpointQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
        return queryDslRepository.findAllActiveSpecs();
    }

    @Override
    public Instant findLatestUpdatedAt() {
        return queryDslRepository.findLatestUpdatedAt();
    }
}
