package com.ryuqq.authhub.application.endpointpermission.service.query;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetEndpointPermissionUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import org.springframework.stereotype.Service;

/**
 * GetEndpointPermissionService - 엔드포인트 권한 단건 조회 Service
 *
 * <p>GetEndpointPermissionUseCase를 구현합니다.
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
public class GetEndpointPermissionService implements GetEndpointPermissionUseCase {

    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;

    public GetEndpointPermissionService(
            EndpointPermissionReadManager readManager, EndpointPermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public EndpointPermissionResponse execute(GetEndpointPermissionQuery query) {
        EndpointPermissionId id = EndpointPermissionId.of(query.endpointPermissionId());
        EndpointPermission endpointPermission =
                readManager
                        .findById(id)
                        .orElseThrow(() -> new EndpointPermissionNotFoundException(id));

        return assembler.toResponse(endpointPermission);
    }
}
