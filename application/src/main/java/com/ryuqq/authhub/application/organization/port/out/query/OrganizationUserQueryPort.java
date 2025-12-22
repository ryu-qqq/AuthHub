package com.ryuqq.authhub.application.organization.port.out.query;

import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationUsersQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import java.util.List;

/**
 * OrganizationUserQueryPort - 조직 사용자 조회 Port-Out 인터페이스
 *
 * <p>조직에 소속된 사용자 조회를 위한 Persistence 계층 포트입니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Application Layer에서 정의
 *   <li>Persistence Adapter에서 구현
 *   <li>Application DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OrganizationUserQueryPort {

    /**
     * 조직에 소속된 사용자 목록 조회
     *
     * @param query 조회 조건
     * @return 사용자 목록
     */
    List<OrganizationUserResponse> searchUsersByOrganizationId(SearchOrganizationUsersQuery query);

    /**
     * 조직에 소속된 사용자 수 조회
     *
     * @param query 조회 조건
     * @return 사용자 수
     */
    long countUsersByOrganizationId(SearchOrganizationUsersQuery query);
}
