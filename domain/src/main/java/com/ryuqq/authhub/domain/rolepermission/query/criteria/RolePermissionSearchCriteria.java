package com.ryuqq.authhub.domain.rolepermission.query.criteria;

import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.vo.RolePermissionSortKey;
import java.util.List;

/**
 * RolePermissionSearchCriteria - 역할-권한 관계 검색 조건
 *
 * <p>역할-권한 관계 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>roleId - 특정 역할의 권한 목록 조회
 *   <li>roleIds - 여러 역할의 권한 목록 조회
 *   <li>permissionId - 특정 권한이 부여된 역할 목록 조회
 *   <li>permissionIds - 여러 권한 중 하나라도 부여된 역할 목록 조회
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
public record RolePermissionSearchCriteria(
        RoleId roleId,
        List<RoleId> roleIds,
        PermissionId permissionId,
        List<PermissionId> permissionIds,
        QueryContext<RolePermissionSortKey> queryContext) {

    // ========== Factory Methods ==========

    /**
     * 팩토리 메서드
     *
     * @param roleId 역할 ID (단건)
     * @param roleIds 역할 ID 목록
     * @param permissionId 권한 ID (단건)
     * @param permissionIds 권한 ID 목록
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이징 정보
     * @return RolePermissionSearchCriteria
     */
    public static RolePermissionSearchCriteria of(
            RoleId roleId,
            List<RoleId> roleIds,
            PermissionId permissionId,
            List<PermissionId> permissionIds,
            RolePermissionSortKey sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest) {
        QueryContext<RolePermissionSortKey> context =
                QueryContext.of(sortKey, sortDirection, pageRequest);
        return new RolePermissionSearchCriteria(
                roleId, roleIds, permissionId, permissionIds, context);
    }

    /**
     * 역할 ID로 권한 목록 조회 (기본 페이징)
     *
     * @param roleId 역할 ID
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return RolePermissionSearchCriteria
     */
    public static RolePermissionSearchCriteria ofRoleId(
            RoleId roleId, int pageNumber, int pageSize) {
        QueryContext<RolePermissionSortKey> context =
                QueryContext.of(
                        RolePermissionSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new RolePermissionSearchCriteria(roleId, null, null, null, context);
    }

    /**
     * 권한 ID로 역할 목록 조회 (기본 페이징)
     *
     * @param permissionId 권한 ID
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return RolePermissionSearchCriteria
     */
    public static RolePermissionSearchCriteria ofPermissionId(
            PermissionId permissionId, int pageNumber, int pageSize) {
        QueryContext<RolePermissionSortKey> context =
                QueryContext.of(
                        RolePermissionSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new RolePermissionSearchCriteria(null, null, permissionId, null, context);
    }

    /**
     * 여러 역할 ID로 권한 목록 조회
     *
     * @param roleIds 역할 ID 목록
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return RolePermissionSearchCriteria
     */
    public static RolePermissionSearchCriteria ofRoleIds(
            List<RoleId> roleIds, int pageNumber, int pageSize) {
        QueryContext<RolePermissionSortKey> context =
                QueryContext.of(
                        RolePermissionSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new RolePermissionSearchCriteria(null, roleIds, null, null, context);
    }

    // ========== Query Methods ==========

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
     * 권한 ID 필터가 있는지 확인
     *
     * @return 권한 ID가 있으면 true
     */
    public boolean hasPermissionIdFilter() {
        return permissionId != null;
    }

    /**
     * 권한 ID 목록 필터가 있는지 확인
     *
     * @return 권한 ID 목록이 있으면 true
     */
    public boolean hasPermissionIdsFilter() {
        return permissionIds != null && !permissionIds.isEmpty();
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
    public RolePermissionSortKey sortKey() {
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
