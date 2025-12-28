package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.application.role.port.out.query.RoleAdminQueryPort;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RoleAdminQueryAdapter - 역할 Admin 조회 Adapter
 *
 * <p>{@link RoleAdminQueryPort}의 구현체로서 Admin용 역할 조회 기능을 제공합니다.
 *
 * <p><strong>일반 QueryAdapter와의 차이점:</strong>
 *
 * <ul>
 *   <li>Domain 대신 DTO Projection 직접 반환
 *   <li>Mapper 불필요 (Repository에서 DTO 생성)
 *   <li>Admin 화면 최적화된 응답
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Port 구현 필수
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see RoleAdminQueryPort Admin 조회 Port
 */
@Component
public class RoleAdminQueryAdapter implements RoleAdminQueryPort {

    private final RoleAdminQueryDslRepository repository;

    public RoleAdminQueryAdapter(RoleAdminQueryDslRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<RoleSummaryResponse> searchRoles(SearchRolesQuery query) {
        List<RoleSummaryResponse> content = repository.searchRoles(query);
        long totalElements = repository.countRoles(query);
        return createPageResponse(content, query.page(), query.size(), totalElements);
    }

    @Override
    public Optional<RoleDetailResponse> findRoleDetail(RoleId roleId) {
        return repository.findRoleDetail(roleId.value());
    }

    @Override
    public long countRoles(SearchRolesQuery query) {
        return repository.countRoles(query);
    }

    /**
     * PageResponse 생성
     *
     * @param content 목록 데이터
     * @param page 현재 페이지
     * @param size 페이지 크기
     * @param totalElements 총 요소 수
     * @return PageResponse
     */
    private <T> PageResponse<T> createPageResponse(
            List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean first = page == 0;
        boolean last = (page + 1) >= totalPages || totalPages == 0;
        return PageResponse.of(content, page, size, totalElements, totalPages, first, last);
    }
}
