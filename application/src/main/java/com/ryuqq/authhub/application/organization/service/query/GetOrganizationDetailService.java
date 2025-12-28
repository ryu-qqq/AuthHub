package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationDetailUseCase;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Service;

/**
 * GetOrganizationDetailService - Admin 조직 상세 조회 서비스
 *
 * <p>어드민 친화적 조직 상세 조회를 처리합니다. AdminQueryPort를 통해 연관 데이터를 포함한 Detail DTO를 직접 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>Port 의존 (구현체 직접 의존 금지)
 *   <li>{@code @Transactional} 금지 (Manager/Facade 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetOrganizationDetailService implements GetOrganizationDetailUseCase {

    private final OrganizationAdminQueryPort adminQueryPort;

    /**
     * 생성자 주입
     *
     * @param adminQueryPort Admin 조회 포트
     */
    public GetOrganizationDetailService(OrganizationAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    /**
     * Admin 조직 상세 조회 실행
     *
     * <p>AdminQueryPort를 통해 tenantName, users, userCount를 포함한 Detail DTO를 직접 조회합니다.
     *
     * @param query 조회 조건 (조직 ID)
     * @return 조직 상세 정보 (연관 데이터 포함)
     * @throws OrganizationNotFoundException 조직을 찾을 수 없는 경우
     */
    @Override
    public OrganizationDetailResponse execute(GetOrganizationQuery query) {
        OrganizationId organizationId = OrganizationId.of(query.organizationId());
        return adminQueryPort
                .findOrganizationDetail(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }
}
