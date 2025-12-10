package com.ryuqq.authhub.application.permission.service.query;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchPermissionsService - 권한 검색 Service
 *
 * <p>SearchPermissionsUseCase를 구현합니다.
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
public class SearchPermissionsService implements SearchPermissionsUseCase {

    private final PermissionReadManager readManager;
    private final PermissionAssembler assembler;

    public SearchPermissionsService(
            PermissionReadManager readManager, PermissionAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public List<PermissionResponse> execute(SearchPermissionsQuery query) {
        // 1. Permission 검색
        List<Permission> permissions = readManager.search(query);

        // 2. Assembler: Response 변환
        return permissions.stream().map(assembler::toResponse).toList();
    }
}
