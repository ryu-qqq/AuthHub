package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.factory.RoleQueryFactory;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.query.SearchRolesUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchRolesService - 역할 목록 검색 Service
 *
 * <p>SearchRolesUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchRolesService implements SearchRolesUseCase {

    private final RoleQueryFactory queryFactory;
    private final RoleReadManager readManager;
    private final RoleAssembler assembler;

    public SearchRolesService(
            RoleQueryFactory queryFactory, RoleReadManager readManager, RoleAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public RolePageResult execute(RoleSearchParams params) {
        // 1. Factory: SearchParams → SearchCriteria
        RoleSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 조회 (병렬 실행 가능)
        List<Role> roles = readManager.findAllBySearchCriteria(criteria);
        long totalCount = readManager.countBySearchCriteria(criteria);

        // 3. Assembler: Domain → PageResult
        return assembler.toPageResult(roles, criteria.pageNumber(), criteria.size(), totalCount);
    }
}
