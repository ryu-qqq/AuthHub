package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.dto.OrganizationUserProjection;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationUserQueryDslRepository;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationUsersQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationUserQueryPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationUserQueryAdapter - 조직 사용자 Query Adapter
 *
 * <p>OrganizationUserQueryPort 구현체로서 조직별 사용자 조회를 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>OrganizationUserQueryDslRepository (1개)
 *   <li>Persistence DTO → Application DTO 변환
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Application DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationUserQueryAdapter implements OrganizationUserQueryPort {

    private final OrganizationUserQueryDslRepository repository;

    public OrganizationUserQueryAdapter(OrganizationUserQueryDslRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OrganizationUserResponse> searchUsersByOrganizationId(
            SearchOrganizationUsersQuery query) {
        List<OrganizationUserProjection> projections =
                repository.searchUsersByOrganizationId(
                        query.organizationId().value(), query.offset(), query.size());
        return projections.stream().map(this::toResponse).toList();
    }

    @Override
    public long countUsersByOrganizationId(SearchOrganizationUsersQuery query) {
        return repository.countUsersByOrganizationId(query.organizationId().value());
    }

    /**
     * Persistence DTO → Application DTO 변환
     *
     * @param projection Persistence DTO
     * @return Application DTO
     */
    private OrganizationUserResponse toResponse(OrganizationUserProjection projection) {
        return new OrganizationUserResponse(
                projection.userId() != null ? projection.userId().toString() : null,
                projection.email(),
                projection.tenantId() != null ? projection.tenantId().toString() : null,
                projection.createdAt());
    }
}
