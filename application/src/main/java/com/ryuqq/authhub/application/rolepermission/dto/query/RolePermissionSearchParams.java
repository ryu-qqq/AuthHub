package com.ryuqq.authhub.application.rolepermission.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * RolePermissionSearchParams - 역할-권한 관계 검색 파라미터
 *
 * <p>역할-권한 관계 목록 조회 시 사용하는 검색 파라미터입니다.
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
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
 * @param searchParams 공통 검색 파라미터 (페이징, 정렬)
 * @param roleId 역할 ID (단건)
 * @param roleIds 역할 ID 목록
 * @param permissionId 권한 ID (단건)
 * @param permissionIds 권한 ID 목록
 * @author development-team
 * @since 1.0.0
 */
public record RolePermissionSearchParams(
        CommonSearchParams searchParams,
        Long roleId,
        List<Long> roleIds,
        Long permissionId,
        List<Long> permissionIds) {

    /**
     * RolePermissionSearchParams 생성 (전체 파라미터)
     *
     * @param searchParams 공통 검색 파라미터
     * @param roleId 역할 ID (단건)
     * @param roleIds 역할 ID 목록
     * @param permissionId 권한 ID (단건)
     * @param permissionIds 권한 ID 목록
     * @return RolePermissionSearchParams 인스턴스
     */
    public static RolePermissionSearchParams of(
            CommonSearchParams searchParams,
            Long roleId,
            List<Long> roleIds,
            Long permissionId,
            List<Long> permissionIds) {
        return new RolePermissionSearchParams(
                searchParams, roleId, roleIds, permissionId, permissionIds);
    }

    /**
     * 역할 ID로 권한 목록 조회용 팩토리 메서드
     *
     * @param roleId 역할 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return RolePermissionSearchParams
     */
    public static RolePermissionSearchParams ofRoleId(Long roleId, Integer page, Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, null, null, "grantedAt", "DESC", page, size);
        return new RolePermissionSearchParams(searchParams, roleId, null, null, null);
    }

    /**
     * 권한 ID로 역할 목록 조회용 팩토리 메서드
     *
     * @param permissionId 권한 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return RolePermissionSearchParams
     */
    public static RolePermissionSearchParams ofPermissionId(
            Long permissionId, Integer page, Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, null, null, "grantedAt", "DESC", page, size);
        return new RolePermissionSearchParams(searchParams, null, null, permissionId, null);
    }

    // ==================== Delegate Methods ====================

    /**
     * 정렬 기준 반환 (delegate)
     *
     * @return 정렬 기준
     */
    public String sortKey() {
        return searchParams.sortKey();
    }

    /**
     * 정렬 방향 반환 (delegate)
     *
     * @return 정렬 방향
     */
    public String sortDirection() {
        return searchParams.sortDirection();
    }

    /**
     * 페이지 번호 반환 (delegate)
     *
     * @return 페이지 번호
     */
    public Integer page() {
        return searchParams.page();
    }

    /**
     * 페이지 크기 반환 (delegate)
     *
     * @return 페이지 크기
     */
    public Integer size() {
        return searchParams.size();
    }

    /**
     * 역할 ID 필터가 있는지 확인
     *
     * @return roleId가 null이 아니면 true
     */
    public boolean hasRoleId() {
        return roleId != null;
    }

    /**
     * 역할 ID 목록 필터가 있는지 확인
     *
     * @return roleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasRoleIds() {
        return roleIds != null && !roleIds.isEmpty();
    }

    /**
     * 권한 ID 필터가 있는지 확인
     *
     * @return permissionId가 null이 아니면 true
     */
    public boolean hasPermissionId() {
        return permissionId != null;
    }

    /**
     * 권한 ID 목록 필터가 있는지 확인
     *
     * @return permissionIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasPermissionIds() {
        return permissionIds != null && !permissionIds.isEmpty();
    }
}
