package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.port.in.query.GetRoleDetailUseCase;
import com.ryuqq.authhub.application.role.port.out.query.RoleAdminQueryPort;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.springframework.stereotype.Service;

/**
 * GetRoleDetailService - 역할 상세 조회 Service (Admin용)
 *
 * <p>GetRoleDetailUseCase를 구현합니다.
 *
 * <p><strong>일반 QueryService와의 차이점:</strong>
 *
 * <ul>
 *   <li>AdminQueryPort 직접 호출 (Manager 불필요)
 *   <li>Assembler 불필요 (DTO Projection 직접 반환)
 *   <li>RoleDetailResponse 반환 (권한 목록 포함)
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
 * @see GetRoleDetailUseCase Admin 상세 조회 UseCase
 * @see RoleAdminQueryPort Admin 조회 Port
 */
@Service
public class GetRoleDetailService implements GetRoleDetailUseCase {

    private final RoleAdminQueryPort adminQueryPort;

    public GetRoleDetailService(RoleAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    @Override
    public RoleDetailResponse execute(GetRoleQuery query) {
        RoleId roleId = RoleId.of(query.roleId());
        return adminQueryPort
                .findRoleDetail(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
    }
}
