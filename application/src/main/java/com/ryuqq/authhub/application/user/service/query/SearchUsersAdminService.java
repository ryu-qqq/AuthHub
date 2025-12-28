package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersAdminUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserAdminQueryPort;
import org.springframework.stereotype.Service;

/**
 * SearchUsersAdminService - 사용자 목록 검색 Service (Admin용)
 *
 * <p>SearchUsersAdminUseCase를 구현합니다.
 *
 * <p><strong>일반 QueryService와의 차이점:</strong>
 *
 * <ul>
 *   <li>AdminQueryPort 직접 호출 (Manager 불필요)
 *   <li>Assembler 불필요 (DTO Projection 직접 반환)
 *   <li>PageResponse 반환
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (읽기 전용이므로 불필요)
 *   <li>Port 직접 호출 허용 (Admin 패턴)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchUsersAdminUseCase Admin 검색 UseCase
 * @see UserAdminQueryPort Admin 조회 Port
 */
@Service
public class SearchUsersAdminService implements SearchUsersAdminUseCase {

    private final UserAdminQueryPort adminQueryPort;

    public SearchUsersAdminService(UserAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    @Override
    public PageResponse<UserSummaryResponse> execute(SearchUsersQuery query) {
        return adminQueryPort.searchUsers(query);
    }
}
