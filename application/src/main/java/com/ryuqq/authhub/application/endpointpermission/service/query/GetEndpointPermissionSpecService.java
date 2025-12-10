package com.ryuqq.authhub.application.endpointpermission.service.query;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetEndpointPermissionSpecUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * GetEndpointPermissionSpecService - 엔드포인트 권한 스펙 조회 Service (인증용)
 *
 * <p>GetEndpointPermissionSpecUseCase를 구현합니다. 요청 경로와 메서드에 매칭되는 엔드포인트 권한 스펙을 조회합니다.
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
public class GetEndpointPermissionSpecService implements GetEndpointPermissionSpecUseCase {

    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;

    public GetEndpointPermissionSpecService(
            EndpointPermissionReadManager readManager, EndpointPermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public Optional<EndpointPermissionSpecResponse> execute(GetEndpointPermissionSpecQuery query) {
        ServiceName serviceName = ServiceName.of(query.serviceName());
        HttpMethod method = HttpMethod.fromString(query.method());

        // 해당 서비스 + 메서드의 모든 엔드포인트 권한 조회
        List<EndpointPermission> candidates =
                readManager.findAllByServiceNameAndMethod(serviceName, method);

        // 요청 경로와 매칭되는 엔드포인트 찾기
        return candidates.stream()
                .filter(ep -> ep.matchesPath(query.requestPath()))
                .findFirst()
                .map(assembler::toSpecResponse);
    }
}
