package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesAdminUseCase;
import com.ryuqq.authhub.application.role.port.out.query.RoleAdminQueryPort;
import org.springframework.stereotype.Service;

/**
 * SearchRolesAdminService - 역할 목록 검색 Service (Admin용)
 *
 * <p>SearchRolesAdminUseCase를 구현합니다.
 *
 * <p><strong>일반 QueryService와의 차이점:</strong>
 *
 * <ul>
 *   <li>AdminQueryPort 직접 호출 (Manager 불필요)
 *   <li>Assembler 불필요 (DTO Projection 직접 반환)
 *   <li>PageResponse 반환
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (읽기 전용이므로 불필요)
 *   <li>Port 직접 호출 허용 (Admin 패턴)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchRolesAdminUseCase Admin 검색 UseCase
 * @see RoleAdminQueryPort Admin 조회 Port
 */
@Service
public class SearchRolesAdminService implements SearchRolesAdminUseCase {

    private final RoleAdminQueryPort adminQueryPort;

    public SearchRolesAdminService(RoleAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    @Override
    public PageResponse<RoleSummaryResponse> execute(SearchRolesQuery query) {
        return adminQueryPort.searchRoles(query);
    }
}
