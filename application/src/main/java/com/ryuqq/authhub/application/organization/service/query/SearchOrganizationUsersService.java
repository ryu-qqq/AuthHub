package com.ryuqq.authhub.application.organization.service.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationUsersQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationUsersUseCase;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationUserQueryPort;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchOrganizationUsersService - 조직별 사용자 조회 Service
 *
 * <p>SearchOrganizationUsersUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 금지 (읽기 전용, 불필요)
 *   <li>Port → PageResponse 흐름
 *   <li>Lombok 금지
 * </ul>
 *
 * <p><strong>특수 케이스:</strong>
 *
 * <ul>
 *   <li>관계 조회 API로서 OrganizationUserQueryPort 직접 사용
 *   <li>조직 도메인이 아닌 사용자 조회이므로 Manager 사용 불필요
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchOrganizationUsersService implements SearchOrganizationUsersUseCase {

    private final OrganizationUserQueryPort queryPort;

    public SearchOrganizationUsersService(OrganizationUserQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public PageResponse<OrganizationUserResponse> execute(SearchOrganizationUsersQuery query) {
        // 1. Port: 사용자 목록 조회
        List<OrganizationUserResponse> users = queryPort.searchUsersByOrganizationId(query);

        // 2. Port: 총 개수 조회
        long totalCount = queryPort.countUsersByOrganizationId(query);

        // 3. PageResponse 생성
        int totalPages = (int) Math.ceil((double) totalCount / query.size());
        boolean isFirst = query.page() == 0;
        boolean isLast = query.page() >= totalPages - 1 || users.size() < query.size();

        return PageResponse.of(
                users, query.page(), query.size(), totalCount, totalPages, isFirst, isLast);
    }
}
