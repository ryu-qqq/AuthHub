package com.ryuqq.authhub.application.rolepermission.service.query;

import com.ryuqq.authhub.application.rolepermission.assembler.RolePermissionAssembler;
import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionPageResult;
import com.ryuqq.authhub.application.rolepermission.factory.RolePermissionQueryFactory;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.application.rolepermission.port.in.query.SearchRolePermissionsUseCase;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchRolePermissionsService - 역할-권한 관계 검색 Service
 *
 * <p>역할-권한 관계를 검색하는 비즈니스 로직을 수행합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Factory: SearchParams → SearchCriteria 변환
 *   <li>Manager: 조회
 *   <li>Assembler: Domain → PageResult 변환
 * </ol>
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchRolePermissionsService implements SearchRolePermissionsUseCase {

    private final RolePermissionQueryFactory queryFactory;
    private final RolePermissionReadManager readManager;
    private final RolePermissionAssembler assembler;

    public SearchRolePermissionsService(
            RolePermissionQueryFactory queryFactory,
            RolePermissionReadManager readManager,
            RolePermissionAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    /**
     * 역할-권한 관계 검색
     *
     * @param params 검색 파라미터
     * @return 역할-권한 관계 페이지 결과
     */
    @Override
    public RolePermissionPageResult search(RolePermissionSearchParams params) {
        // 1. Factory: SearchParams → SearchCriteria
        RolePermissionSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 조회
        List<RolePermission> rolePermissions = readManager.findAllBySearchCriteria(criteria);
        long totalCount = readManager.countBySearchCriteria(criteria);

        // 3. Assembler: Domain → PageResult
        return assembler.toPageResult(
                rolePermissions, criteria.pageNumber(), criteria.size(), totalCount);
    }
}
