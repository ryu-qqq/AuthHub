package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchOrganizationsService - 조직 목록 조회 Service
 *
 * <p>SearchOrganizationsUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (읽기 전용, 불필요)
 *   <li>ReadManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchOrganizationsService implements SearchOrganizationsUseCase {

    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;

    public SearchOrganizationsService(
            OrganizationReadManager readManager, OrganizationAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PageResponse<OrganizationResponse> execute(SearchOrganizationsQuery query) {
        // 1. ReadManager: 조회
        TenantId tenantId = TenantId.of(query.tenantId());
        int offset = query.page() * query.size();
        List<Organization> organizations =
                readManager.findAllByTenantIdAndCriteria(
                        tenantId, query.name(), query.status(), offset, query.size());

        // 2. ReadManager: 총 개수 조회
        long totalCount =
                readManager.countByTenantIdAndCriteria(tenantId, query.name(), query.status());

        // 3. Assembler: Response 변환
        List<OrganizationResponse> content = assembler.toResponseList(organizations);

        // 4. PageResponse 생성
        int totalPages = (int) Math.ceil((double) totalCount / query.size());
        boolean isFirst = query.page() == 0;
        boolean isLast = query.page() >= totalPages - 1 || organizations.size() < query.size();

        return PageResponse.of(
                content, query.page(), query.size(), totalCount, totalPages, isFirst, isLast);
    }
}
