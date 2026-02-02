package com.ryuqq.authhub.application.permissionendpoint.service.query;

import com.ryuqq.authhub.application.permissionendpoint.assembler.PermissionEndpointAssembler;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.application.permissionendpoint.port.in.query.GetEndpointPermissionSpecUseCase;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetEndpointPermissionSpecService - Gateway용 엔드포인트-권한 스펙 조회 서비스
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 전체 엔드포인트-권한 매핑 정보를 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (Manager에서 처리)
 *   <li>ReadManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetEndpointPermissionSpecService implements GetEndpointPermissionSpecUseCase {

    private final PermissionEndpointReadManager readManager;
    private final PermissionEndpointAssembler assembler;

    public GetEndpointPermissionSpecService(
            PermissionEndpointReadManager readManager, PermissionEndpointAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public EndpointPermissionSpecListResult getAll() {
        List<EndpointPermissionSpecResult> specs = readManager.findAllActiveSpecs();
        Instant latestUpdatedAt = readManager.findLatestUpdatedAt();
        return assembler.toSpecListResult(specs, latestUpdatedAt);
    }
}
