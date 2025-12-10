package com.ryuqq.authhub.application.permission.service.query;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import org.springframework.stereotype.Service;

/**
 * GetPermissionService - 권한 단건 조회 Service
 *
 * <p>GetPermissionUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetPermissionService implements GetPermissionUseCase {

    private final PermissionReadManager readManager;
    private final PermissionAssembler assembler;

    public GetPermissionService(PermissionReadManager readManager, PermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PermissionResponse execute(GetPermissionQuery query) {
        // 1. Permission 조회
        PermissionId permissionId = PermissionId.of(query.permissionId());
        Permission permission = readManager.getById(permissionId);

        // 2. Assembler: Response 변환
        return assembler.toResponse(permission);
    }
}
