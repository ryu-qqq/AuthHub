package com.ryuqq.authhub.application.role.dto.query;

import com.ryuqq.authhub.domain.role.identifier.RoleId;

/**
 * SearchRoleUsersQuery - 역할별 사용자 조회 Query DTO
 *
 * <p>특정 역할이 할당된 사용자 목록을 조회하기 위한 Query입니다.
 *
 * <p><strong>페이징:</strong>
 *
 * <ul>
 *   <li>page: 0부터 시작하는 페이지 번호
 *   <li>size: 페이지당 조회 건수 (최대 100)
 * </ul>
 *
 * @param roleId 역할 ID
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchRoleUsersQuery(RoleId roleId, int page, int size) {

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchRoleUsersQuery 생성
     *
     * @param roleId 역할 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    public SearchRoleUsersQuery {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > MAX_SIZE) {
            size = DEFAULT_SIZE;
        }
    }

    /**
     * 정적 팩토리 메서드
     *
     * @param roleId 역할 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchRoleUsersQuery
     */
    public static SearchRoleUsersQuery of(RoleId roleId, int page, int size) {
        return new SearchRoleUsersQuery(roleId, page, size);
    }

    /**
     * 오프셋 계산
     *
     * @return 데이터베이스 조회 시작 위치
     */
    public long offset() {
        return (long) page * size;
    }
}
