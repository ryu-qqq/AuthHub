package com.ryuqq.authhub.application.rolepermission.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import java.util.List;

/**
 * RolePermission Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionQueryFixtures {

    private RolePermissionQueryFixtures() {}

    // ==================== RolePermissionSearchParams ====================

    /** 기본 검색 파라미터 반환 (전체 조회) */
    public static RolePermissionSearchParams searchParams() {
        return new RolePermissionSearchParams(defaultCommonSearchParams(), null, null, null, null);
    }

    /** 페이징 정보를 지정한 검색 파라미터 반환 */
    public static RolePermissionSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "grantedAt", "DESC", page, size);
        return new RolePermissionSearchParams(commonParams, null, null, null, null);
    }

    /** 역할 ID로 검색 파라미터 반환 */
    public static RolePermissionSearchParams searchParamsWithRoleId(Long roleId) {
        return new RolePermissionSearchParams(
                defaultCommonSearchParams(), roleId, null, null, null);
    }

    /** 역할 ID 목록으로 검색 파라미터 반환 */
    public static RolePermissionSearchParams searchParamsWithRoleIds(List<Long> roleIds) {
        return new RolePermissionSearchParams(
                defaultCommonSearchParams(), null, roleIds, null, null);
    }

    /** 권한 ID로 검색 파라미터 반환 */
    public static RolePermissionSearchParams searchParamsWithPermissionId(Long permissionId) {
        return new RolePermissionSearchParams(
                defaultCommonSearchParams(), null, null, permissionId, null);
    }

    /** 권한 ID 목록으로 검색 파라미터 반환 */
    public static RolePermissionSearchParams searchParamsWithPermissionIds(
            List<Long> permissionIds) {
        return new RolePermissionSearchParams(
                defaultCommonSearchParams(), null, null, null, permissionIds);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams (grantedAt DESC, 0, 20) */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "grantedAt", "DESC", 0, 20);
    }

    /** 기본 Role ID (Fixture와 동일) */
    public static Long defaultRoleId() {
        return RolePermissionFixture.defaultRoleIdValue();
    }

    /** 기본 Permission ID (Fixture와 동일) */
    public static Long defaultPermissionId() {
        return RolePermissionFixture.defaultPermissionIdValue();
    }
}
