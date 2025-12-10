package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchRolesService - 역할 검색 Service
 *
 * <p>SearchRolesUseCase를 구현합니다.
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
public class SearchRolesService implements SearchRolesUseCase {

    private final RoleReadManager readManager;
    private final RoleAssembler assembler;

    public SearchRolesService(RoleReadManager readManager, RoleAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public List<RoleResponse> execute(SearchRolesQuery query) {
        // 1. 역할 검색
        List<Role> roles = readManager.search(query);

        // 2. Assembler: Response 변환
        return roles.stream().map(assembler::toResponse).toList();
    }
}
