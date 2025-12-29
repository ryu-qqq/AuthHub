package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.factory.query.UserQueryFactory;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.query.criteria.UserCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchUsersService - 사용자 목록 검색 Service
 *
 * <p>SearchUsersUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
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
    public PageResponse<UserResponse> execute(SearchUsersQuery query) {
        // 1. Query → Criteria 변환
        UserCriteria criteria = queryFactory.toCriteria(query);

        // 2. ReadManager: 조회
        List<User> users = readManager.findAllByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        // 3. Assembler: Response 변환
        List<UserResponse> content = assembler.toResponseList(users);

        // 4. PageResponse 생성
        int page = criteria.page().page();
        int size = criteria.page().size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1 || totalPages == 0;

        return PageResponse.of(content, page, size, totalElements, totalPages, first, last);
    }
}
