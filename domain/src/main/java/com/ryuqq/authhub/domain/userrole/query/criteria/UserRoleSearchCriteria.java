package com.ryuqq.authhub.domain.userrole.query.criteria;

import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;

/**
 * UserRoleSearchCriteria - 사용자-역할 관계 검색 조건
 *
 * <p>사용자-역할 관계 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>userId - 특정 사용자의 역할 목록 조회
 *   <li>userIds - 여러 사용자의 역할 목록 조회
 *   <li>roleId - 특정 역할이 할당된 사용자 목록 조회
 *   <li>roleIds - 여러 역할 중 하나라도 할당된 사용자 목록 조회
 * </ul>
 *
 * <p><strong>페이징:</strong>
 *
 * <ul>
 *   <li>QueryContext를 통해 페이지 번호, 크기, 정렬 정보 관리
 *   <li>offset()과 size()로 QueryDSL에서 직접 사용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record UserRoleSearchCriteria(
        UserId userId,
        List<UserId> userIds,
        RoleId roleId,
        List<RoleId> roleIds,
        QueryContext<UserRoleSortKey> queryContext) {

    // ========== Factory Methods ==========

    /**
     * 팩토리 메서드
     *
     * @param userId 사용자 ID (단건)
     * @param userIds 사용자 ID 목록
     * @param roleId 역할 ID (단건)
     * @param roleIds 역할 ID 목록
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이징 정보
     * @return UserRoleSearchCriteria
     */
    public static UserRoleSearchCriteria of(
            UserId userId,
            List<UserId> userIds,
            RoleId roleId,
            List<RoleId> roleIds,
            UserRoleSortKey sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest) {
        QueryContext<UserRoleSortKey> context =
                QueryContext.of(sortKey, sortDirection, pageRequest);
        return new UserRoleSearchCriteria(userId, userIds, roleId, roleIds, context);
    }

    /**
     * 사용자 ID로 역할 목록 조회 (기본 페이징)
     *
     * @param userId 사용자 ID
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return UserRoleSearchCriteria
     */
    public static UserRoleSearchCriteria ofUserId(UserId userId, int pageNumber, int pageSize) {
        QueryContext<UserRoleSortKey> context =
                QueryContext.of(
                        UserRoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new UserRoleSearchCriteria(userId, null, null, null, context);
    }

    /**
     * 역할 ID로 사용자 목록 조회 (기본 페이징)
     *
     * @param roleId 역할 ID
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return UserRoleSearchCriteria
     */
    public static UserRoleSearchCriteria ofRoleId(RoleId roleId, int pageNumber, int pageSize) {
        QueryContext<UserRoleSortKey> context =
                QueryContext.of(
                        UserRoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new UserRoleSearchCriteria(null, null, roleId, null, context);
    }

    /**
     * 여러 사용자 ID로 역할 목록 조회
     *
     * @param userIds 사용자 ID 목록
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return UserRoleSearchCriteria
     */
    public static UserRoleSearchCriteria ofUserIds(
            List<UserId> userIds, int pageNumber, int pageSize) {
        QueryContext<UserRoleSortKey> context =
                QueryContext.of(
                        UserRoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new UserRoleSearchCriteria(null, userIds, null, null, context);
    }

    /**
     * 여러 역할 ID로 사용자 목록 조회
     *
     * @param roleIds 역할 ID 목록
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return UserRoleSearchCriteria
     */
    public static UserRoleSearchCriteria ofRoleIds(
            List<RoleId> roleIds, int pageNumber, int pageSize) {
        QueryContext<UserRoleSortKey> context =
                QueryContext.of(
                        UserRoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new UserRoleSearchCriteria(null, null, null, roleIds, context);
    }

    // ========== Query Methods ==========

    /**
     * 사용자 ID 필터가 있는지 확인
     *
     * @return 사용자 ID가 있으면 true
     */
    public boolean hasUserIdFilter() {
        return userId != null;
    }

    /**
     * 사용자 ID 목록 필터가 있는지 확인
     *
     * @return 사용자 ID 목록이 있으면 true
     */
    public boolean hasUserIdsFilter() {
        return userIds != null && !userIds.isEmpty();
    }

    /**
     * 역할 ID 필터가 있는지 확인
     *
     * @return 역할 ID가 있으면 true
     */
    public boolean hasRoleIdFilter() {
        return roleId != null;
    }

    /**
     * 역할 ID 목록 필터가 있는지 확인
     *
     * @return 역할 ID 목록이 있으면 true
     */
    public boolean hasRoleIdsFilter() {
        return roleIds != null && !roleIds.isEmpty();
    }

    /**
     * 페이지 번호 반환 (0-based)
     *
     * @return 페이지 번호
     */
    public int pageNumber() {
        return queryContext.page();
    }

    /**
     * 페이지 크기 반환
     *
     * @return 페이지 크기
     */
    public int size() {
        return queryContext.size();
    }

    /**
     * 오프셋 반환 (QueryDSL에서 사용)
     *
     * @return 오프셋
     */
    public long offset() {
        return queryContext.offset();
    }

    /**
     * 정렬 키 반환
     *
     * @return 정렬 키
     */
    public UserRoleSortKey sortKey() {
        return queryContext.sortKey();
    }

    /**
     * 정렬 방향 반환
     *
     * @return 정렬 방향
     */
    public SortDirection sortDirection() {
        return queryContext.sortDirection();
    }
}
