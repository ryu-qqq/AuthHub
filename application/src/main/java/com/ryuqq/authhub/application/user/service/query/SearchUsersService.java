package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.factory.UserQueryFactory;
import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchUsersService - 사용자 목록 검색 Service
 *
 * <p>SearchUsersUseCase를 구현합니다.
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
public class SearchUsersService implements SearchUsersUseCase {

    private final UserQueryFactory queryFactory;
    private final UserReadManager readManager;
    private final UserAssembler assembler;

    public SearchUsersService(
            UserQueryFactory queryFactory, UserReadManager readManager, UserAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public UserPageResult execute(UserSearchParams params) {
        // 1. Factory: SearchParams → SearchCriteria
        UserSearchCriteria criteria = queryFactory.toCriteria(params);

        // 2. Manager: 조회 (병렬 실행 가능)
        List<User> users = readManager.findAllBySearchCriteria(criteria);
        long totalCount = readManager.countBySearchCriteria(criteria);

        // 3. Assembler: Domain → PageResult
        return assembler.toPageResult(users, criteria.pageNumber(), criteria.size(), totalCount);
    }
}
