package com.ryuqq.authhub.application.organization.dto.query;

import java.util.UUID;

/**
 * SearchOrganizationsQuery - 조직 목록 조회 Query DTO
 *
 * @param tenantId 테넌트 ID (필수 - 테넌트 범위 내 조회)
 * @param name 조직 이름 (선택 - 부분 검색)
 * @param status 조직 상태 (선택)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchOrganizationsQuery(
        UUID tenantId, String name, String status, int page, int size) {

    public SearchOrganizationsQuery {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }
    }

    public static SearchOrganizationsQuery of(UUID tenantId) {
        return new SearchOrganizationsQuery(tenantId, null, null, 0, 20);
    }

    public static SearchOrganizationsQuery of(UUID tenantId, int page, int size) {
        return new SearchOrganizationsQuery(tenantId, null, null, page, size);
    }
}
