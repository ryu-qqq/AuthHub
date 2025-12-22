package com.ryuqq.authhub.application.organization.dto.query;

import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;

/**
 * SearchOrganizationUsersQuery - 조직별 사용자 검색 Query DTO
 *
 * <p>조직에 소속된 사용자 목록 조회를 위한 Query 객체입니다.
 *
 * @param organizationId 조직 ID
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchOrganizationUsersQuery(OrganizationId organizationId, int page, int size) {

    /**
     * 정적 팩토리 메서드
     *
     * @param organizationId 조직 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchOrganizationUsersQuery
     */
    public static SearchOrganizationUsersQuery of(
            OrganizationId organizationId, int page, int size) {
        return new SearchOrganizationUsersQuery(organizationId, page, size);
    }

    /**
     * offset 계산
     *
     * @return offset 값
     */
    public long offset() {
        return (long) page * size;
    }
}
