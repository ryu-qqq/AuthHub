package com.ryuqq.authhub.application.permissionendpoint.manager;

import com.ryuqq.authhub.application.permissionendpoint.port.out.command.PermissionEndpointCommandPort;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionEndpointCommandManager - PermissionEndpoint 명령 관리자
 *
 * <p>PermissionEndpoint 저장과 관련된 비즈니스 로직을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>CommandPort만 의존
 *   <li>영속화 로직만 담당
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointCommandManager {

    private final PermissionEndpointCommandPort permissionEndpointCommandPort;

    public PermissionEndpointCommandManager(
            PermissionEndpointCommandPort permissionEndpointCommandPort) {
        this.permissionEndpointCommandPort = permissionEndpointCommandPort;
    }

    /**
     * PermissionEndpoint 영속화
     *
     * @param permissionEndpoint PermissionEndpoint Domain
     * @return 영속화된 ID
     */
    @Transactional
    public Long persist(PermissionEndpoint permissionEndpoint) {
        return permissionEndpointCommandPort.persist(permissionEndpoint);
    }

    /**
     * PermissionEndpoint 다건 영속화 (벌크)
     *
     * <p>벌크 동기화 시 새로운 PermissionEndpoint 목록을 한 번에 저장합니다.
     *
     * @param permissionEndpoints 영속화할 PermissionEndpoint 목록
     */
    @Transactional
    public void persistAll(List<PermissionEndpoint> permissionEndpoints) {
        if (permissionEndpoints == null || permissionEndpoints.isEmpty()) {
            return;
        }
        for (PermissionEndpoint endpoint : permissionEndpoints) {
            permissionEndpointCommandPort.persist(endpoint);
        }
    }
}
