package com.ryuqq.authhub.application.endpointpermission.service.query;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.SearchEndpointPermissionsUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchEndpointPermissionsService - 엔드포인트 권한 검색 Service
 *
 * <p>SearchEndpointPermissionsUseCase를 구현합니다.
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
public class SearchEndpointPermissionsService implements SearchEndpointPermissionsUseCase {

    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;

    public SearchEndpointPermissionsService(
            EndpointPermissionReadManager readManager, EndpointPermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public List<EndpointPermissionResponse> execute(SearchEndpointPermissionsQuery query) {
        List<EndpointPermission> endpointPermissions = readManager.search(query);
        return endpointPermissions.stream().map(assembler::toResponse).toList();
    }

    @Override
    public long count(SearchEndpointPermissionsQuery query) {
        return readManager.count(query);
    }
}
