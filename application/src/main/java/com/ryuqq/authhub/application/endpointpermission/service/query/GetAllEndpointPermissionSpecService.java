package com.ryuqq.authhub.application.endpointpermission.service.query;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetServiceEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecListResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetAllEndpointPermissionSpecUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetAllEndpointPermissionSpecService - 서비스별 엔드포인트 권한 스펙 조회 Service (Gateway용)
 *
 * <p>GetAllEndpointPermissionSpecUseCase를 구현합니다. Gateway에서 캐싱할 서비스별 엔드포인트 권한 스펙을 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetAllEndpointPermissionSpecService implements GetAllEndpointPermissionSpecUseCase {

    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;

    public GetAllEndpointPermissionSpecService(
            EndpointPermissionReadManager readManager, EndpointPermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public EndpointPermissionSpecListResponse execute(GetServiceEndpointPermissionSpecQuery query) {
        ServiceName serviceName = ServiceName.of(query.serviceName());
        List<EndpointPermission> endpointPermissions =
                readManager.findAllByServiceName(serviceName);
        return assembler.toSpecListResponse(endpointPermissions);
    }
}
