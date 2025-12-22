package com.ryuqq.authhub.application.role.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRoleUsersQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import com.ryuqq.authhub.application.role.port.in.query.SearchRoleUsersUseCase;
import com.ryuqq.authhub.application.role.port.out.query.RoleUserQueryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SearchRoleUsersService - 역할별 사용자 조회 Service
 *
 * <p>특정 역할이 할당된 사용자 목록을 조회하는 비즈니스 로직을 구현합니다.
 *
 * <p><strong>조회 로직:</strong>
 *
 * <ol>
 *   <li>역할 ID로 할당된 사용자 목록 조회
 *   <li>페이징 정보와 함께 반환
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class SearchRoleUsersService implements SearchRoleUsersUseCase {

    private final RoleUserQueryPort roleUserQueryPort;

    public SearchRoleUsersService(RoleUserQueryPort roleUserQueryPort) {
        this.roleUserQueryPort = roleUserQueryPort;
    }

    @Override
    public PageResponse<RoleUserResponse> execute(SearchRoleUsersQuery query) {
        List<RoleUserResponse> users = roleUserQueryPort.searchUsersByRoleId(query);
        long totalElements = roleUserQueryPort.countUsersByRoleId(query);

        int totalPages = (int) Math.ceil((double) totalElements / query.size());
        boolean isFirst = query.page() == 0;
        boolean isLast = query.page() >= totalPages - 1 || totalPages == 0;

        return PageResponse.of(
                users, query.page(), query.size(), totalElements, totalPages, isFirst, isLast);
    }
}
