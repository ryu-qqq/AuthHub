package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Service;

/**
 * GetOrganizationService - 조직 단건 조회 Service
 *
 * <p>GetOrganizationUseCase를 구현합니다.
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
public class GetOrganizationService implements GetOrganizationUseCase {

    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;

    public GetOrganizationService(
            OrganizationReadManager readManager, OrganizationAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrganizationResponse execute(GetOrganizationQuery query) {
        // 1. ReadManager: 조회
        OrganizationId organizationId = OrganizationId.of(query.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. Assembler: Response 변환
        return assembler.toResponse(organization);
    }
}
