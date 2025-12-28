package com.ryuqq.authhub.application.role.dto.query;

import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import java.util.UUID;

/**
 * SearchRolesQuery - 역할 목록 검색 Query DTO (Admin-Friendly 확장)
 *
 * <p>어드민 화면에서 다양한 조건으로 역할을 검색할 수 있도록 확장된 Query DTO입니다.
 *
 * <p><strong>확장된 필터:</strong>
 *
 * <ul>
 *   <li>날짜 범위 필터 (createdFrom, createdTo)
 *   <li>키워드 검색 (name 부분 일치)
 *   <li>정렬 옵션 (sortBy, sortDirection)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID 필터 (선택)
 * @param name 역할 이름 필터 (선택, 부분 일치)
 * @param scope 역할 범위 필터 (선택, GLOBAL/TENANT/ORGANIZATION)
 * @param type 역할 타입 필터 (선택, SYSTEM/CUSTOM)
 * @param createdFrom 생성일 시작 (inclusive)
 * @param createdTo 생성일 종료 (inclusive)
 * @param sortBy 정렬 기준 (createdAt, updatedAt, name)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchRolesQuery(
        UUID tenantId,
        String name,
        RoleScope scope,
        RoleType type,
        Instant createdFrom,
        Instant createdTo,
        String sortBy,
        String sortDirection,
        int page,
        int size) {

    /** 기본 정렬 기준 */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** 기본 정렬 방향 */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 최대 페이지 크기 */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 기존 호환성을 위한 간단한 생성자 대체 팩토리 메서드
     *
     * @param tenantId 테넌트 ID
     * @param name 역할 이름
     * @param scope 역할 범위
     * @param type 역할 유형
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchRolesQuery 인스턴스
     */
    public static SearchRolesQuery of(
            UUID tenantId, String name, RoleScope scope, RoleType type, int page, int size) {
        return new SearchRolesQuery(
                tenantId,
                name,
                scope,
                type,
                null,
                null,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page,
                size);
    }
}
