package com.ryuqq.authhub.application.permissionendpoint.service.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.internal.EndpointSyncCoordinator;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.SyncEndpointsUseCase;
import org.springframework.stereotype.Service;

/**
 * SyncEndpointsService - 엔드포인트 동기화 Service
 *
 * <p>SyncEndpointsUseCase를 구현합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Coordinator를 통한 Permission/PermissionEndpoint 동기화
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 → Coordinator에서 처리
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SyncEndpointsService implements SyncEndpointsUseCase {

    private final EndpointSyncCoordinator coordinator;

    public SyncEndpointsService(EndpointSyncCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public SyncEndpointsResult sync(SyncEndpointsCommand command) {
        return coordinator.coordinate(command);
    }
}
