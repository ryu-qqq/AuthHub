package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;

/**
 * UserAdminQueryPort - 사용자 Admin 조회 Port-Out
 *
 * <p>어드민 화면을 위한 확장된 사용자 조회 인터페이스입니다. 일반 {@link UserQueryPort}와 달리 DTO Projection을 직접 반환합니다.
 *
 * <p><strong>일반 QueryPort와의 차이점:</strong>
 *
 * <ul>
 *   <li>Domain 대신 DTO Projection 반환
 *   <li>관련 엔티티 정보 포함 (tenantName, organizationName 등)
 *   <li>집계 정보 포함 (roleCount 등)
 *   <li>Admin 화면 최적화된 응답
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*AdminQueryPort 네이밍 규칙
 *   <li>Application Layer에서 정의
 *   <li>AdminQueryAdapter에서 구현
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see UserQueryPort 기본 사용자 조회 Port
 * @see UserSummaryResponse 목록 조회용 응답 DTO
 * @see UserDetailResponse 상세 조회용 응답 DTO
 */
public interface UserAdminQueryPort {

    /**
     * 조건에 맞는 사용자 목록 검색 (Admin용)
     *
     * <p>확장된 필터와 관련 엔티티 정보를 포함한 Summary 응답을 반환합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 사용자 Summary 목록
     */
    PageResponse<UserSummaryResponse> searchUsers(SearchUsersQuery query);

    /**
     * 사용자 상세 조회 (Admin용)
     *
     * <p>사용자의 할당된 역할 목록을 포함한 상세 정보를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 상세 정보 Optional
     */
    Optional<UserDetailResponse> findUserDetail(UserId userId);

    /**
     * 조건에 맞는 사용자 수 조회
     *
     * @param query 검색 조건
     * @return 사용자 수
     */
    long countUsers(SearchUsersQuery query);
}
