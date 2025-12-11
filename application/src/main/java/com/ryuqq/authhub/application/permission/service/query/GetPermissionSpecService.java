package com.ryuqq.authhub.application.permission.service.query;

import com.ryuqq.authhub.application.permission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.permission.dto.response.PermissionSpecResponse;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionSpecUseCase;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionSpecPort;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetPermissionSpecService - 권한 명세 조회 서비스
 *
 * <p>Gateway에서 엔드포인트별 권한 명세를 조회할 때 사용됩니다. DB에서 EndpointPermission 테이블을 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>UseCase 인터페이스 구현
 *   <li>{@code @Transactional} 금지 (Query Service)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetPermissionSpecService implements GetPermissionSpecUseCase {

    private final PermissionSpecPort permissionSpecPort;
    private final ClockHolder clockHolder;

    public GetPermissionSpecService(
            PermissionSpecPort permissionSpecPort, ClockHolder clockHolder) {
        this.permissionSpecPort = permissionSpecPort;
        this.clockHolder = clockHolder;
    }

    /**
     * 권한 명세 조회
     *
     * <p>DB의 EndpointPermission 테이블에서 권한 명세를 조회하여 반환합니다.
     *
     * @return 전체 엔드포인트별 권한 명세
     */
    @Override
    public PermissionSpecResponse execute() {
        long count = permissionSpecPort.countActivePermissions();
        List<EndpointPermissionResponse> permissions = permissionSpecPort.getEndpointPermissions();
        return PermissionSpecResponse.of(
                (int) count, Instant.now(clockHolder.clock()), permissions);
    }
}
