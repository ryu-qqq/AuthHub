package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.springframework.stereotype.Service;

/**
 * GetRoleService - 역할 단건 조회 Service
 *
 * <p>GetRoleUseCase를 구현합니다.
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
public class GetRoleService implements GetRoleUseCase {

    private final RoleReadManager readManager;
    private final RoleAssembler assembler;

    public GetRoleService(RoleReadManager readManager, RoleAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public RoleResponse execute(GetRoleQuery query) {
        // 1. Role 조회
        RoleId roleId = RoleId.of(query.roleId());
        Role role = readManager.getById(roleId);

        // 2. Assembler: Response 변환
        return assembler.toResponse(role);
    }
}
