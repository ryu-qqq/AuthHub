package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.application.user.port.out.query.UserAdminQueryPort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserAdminQueryAdapter - 사용자 Admin Query Adapter
 *
 * <p>UserAdminQueryPort 구현체로서 어드민 화면용 확장된 사용자 조회를 담당합니다.
 *
 * <p><strong>일반 QueryAdapter와의 차이점:</strong>
 *
 * <ul>
 *   <li>DTO Projection 직접 반환 (Mapper 불필요)
 *   <li>관련 엔티티 정보 포함 (tenantName, organizationName 등)
 *   <li>집계 정보 포함 (roleCount 등)
 * </ul>
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>UserAdminQueryDslRepository (1개)만 의존
 *   <li>필드 1개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 페이징 계산만)
 *   <li>DTO Projection 직접 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see UserAdminQueryPort Admin 전용 Port-Out 인터페이스
 * @see UserAdminQueryDslRepository Admin 전용 QueryDSL Repository
 */
@Component
public class UserAdminQueryAdapter implements UserAdminQueryPort {

    private final UserAdminQueryDslRepository repository;

    public UserAdminQueryAdapter(UserAdminQueryDslRepository repository) {
        this.repository = repository;
    }

    /**
     * 조건에 맞는 사용자 목록 검색 (Admin용)
     *
     * <p>확장된 필터와 관련 엔티티 정보를 포함한 Summary 응답을 반환합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 사용자 Summary 목록
     */
    @Override
    public PageResponse<UserSummaryResponse> searchUsers(SearchUsersQuery query) {
        List<UserSummaryResponse> content = repository.searchUsers(query);
        long totalElements = repository.countUsers(query);

        return createPageResponse(content, query.page(), query.size(), totalElements);
    }

    /**
     * 사용자 상세 조회 (Admin용)
     *
     * <p>사용자의 할당된 역할 목록을 포함한 상세 정보를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 상세 정보 Optional
     */
    @Override
    public Optional<UserDetailResponse> findUserDetail(UserId userId) {
        return repository.findUserDetail(userId.value());
    }

    /**
     * 조건에 맞는 사용자 수 조회
     *
     * @param query 검색 조건
     * @return 사용자 수
     */
    @Override
    public long countUsers(SearchUsersQuery query) {
        return repository.countUsers(query);
    }

    /**
     * PageResponse 생성 헬퍼
     *
     * @param content 현재 페이지 데이터
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 데이터 개수
     * @param <T> 콘텐츠 타입
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
