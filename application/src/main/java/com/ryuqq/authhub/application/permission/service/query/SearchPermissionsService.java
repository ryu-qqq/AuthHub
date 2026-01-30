package com.ryuqq.authhub.application.permission.service.query;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.factory.PermissionQueryFactory;
import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchPermissionsService - 권한 목록 검색 Service
 *
 * <p>SearchPermissionsUseCase를 구현합니다.
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
public class SearchPermissionsService implements SearchPermissionsUseCase {

    private final PermissionQueryFactory queryFactory;
    private final PermissionReadManager readManager;
    private final PermissionAssembler assembler;

    public SearchPermissionsService(
            PermissionQueryFactory queryFactory,
            PermissionReadManager readManager,
            PermissionAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public PermissionPageResult execute(PermissionSearchParams params) {
        // 1. Factory: SearchParams → Criteria 변환
        PermissionSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 권한 목록 조회
        List<Permission> permissions = readManager.findAllBySearchCriteria(criteria);

        // 3. Manager: 전체 개수 조회
        long totalElements = readManager.countBySearchCriteria(criteria);

        // 4. Assembler: Domain → Result 변환
        return assembler.toPageResult(
                permissions, criteria.pageNumber(), criteria.size(), totalElements);
    }
}
