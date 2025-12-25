package com.ryuqq.authhub.domain.permission.fixture;

import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Permission 테스트 Fixture
 *
 * <p>테스트에서 사용할 Permission 객체를 쉽게 생성할 수 있도록 도와주는 클래스입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private PermissionFixture() {}

    // ========== 기본 생성 메서드 ==========

    /** 기본 커스텀 권한 생성 (user:read) */
    public static Permission create() {
        return Permission.createCustom(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("user"),
                Action.of("read"),
                PermissionDescription.of("사용자 조회 권한"),
                FIXED_CLOCK);
    }

    /** 지정된 resource와 action으로 커스텀 권한 생성 */
    public static Permission create(String resource, String action) {
        return Permission.createCustom(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of(resource),
                Action.of(action),
                PermissionDescription.empty(),
                FIXED_CLOCK);
    }

    /** 지정된 key로 커스텀 권한 생성 */
    public static Permission createWithKey(String key) {
        return Permission.createCustomWithKey(
                PermissionId.forNew(UUID.randomUUID()),
                PermissionKey.of(key),
                PermissionDescription.empty(),
                FIXED_CLOCK);
    }

    // ========== 시스템 권한 ==========

    /** 시스템 권한 생성 (user:read) */
    public static Permission createSystem() {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("user"),
                Action.of("read"),
                PermissionDescription.of("사용자 조회 권한 (시스템)"),
                FIXED_CLOCK);
    }

    /** 지정된 resource와 action으로 시스템 권한 생성 */
    public static Permission createSystem(String resource, String action) {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of(resource),
                Action.of(action),
                PermissionDescription.empty(),
                FIXED_CLOCK);
    }

    /** 시스템 권한 생성 - user:create */
    public static Permission createSystemUserCreate() {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("user"),
                Action.of("create"),
                PermissionDescription.of("사용자 생성 권한"),
                FIXED_CLOCK);
    }

    /** 시스템 권한 생성 - user:delete */
    public static Permission createSystemUserDelete() {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("user"),
                Action.of("delete"),
                PermissionDescription.of("사용자 삭제 권한"),
                FIXED_CLOCK);
    }

    /** 시스템 권한 생성 - organization:manage */
    public static Permission createSystemOrganizationManage() {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("organization"),
                Action.of("manage"),
                PermissionDescription.of("조직 관리 권한"),
                FIXED_CLOCK);
    }

    /** 시스템 권한 생성 - tenant:admin */
    public static Permission createSystemTenantAdmin() {
        return Permission.createSystem(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("tenant"),
                Action.of("admin"),
                PermissionDescription.of("테넌트 관리자 권한"),
                FIXED_CLOCK);
    }

    // ========== 커스텀 권한 ==========

    /** 커스텀 권한 생성 - report:export */
    public static Permission createCustomReportExport() {
        return Permission.createCustom(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("report"),
                Action.of("export"),
                PermissionDescription.of("리포트 내보내기 권한"),
                FIXED_CLOCK);
    }

    /** 커스텀 권한 생성 - dashboard:view */
    public static Permission createCustomDashboardView() {
        return Permission.createCustom(
                PermissionId.forNew(UUID.randomUUID()),
                Resource.of("dashboard"),
                Action.of("view"),
                PermissionDescription.of("대시보드 조회 권한"),
                FIXED_CLOCK);
    }

    // ========== 재구성 (DB 조회 시뮬레이션) ==========

    /** ID가 있는 권한 (DB에서 조회된 것처럼) */
    public static Permission createReconstituted() {
        return Permission.reconstitute(
                PermissionId.of(UUID.randomUUID()),
                PermissionKey.of("user:read"),
                PermissionDescription.of("사용자 조회 권한"),
                PermissionType.CUSTOM,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** 지정된 ID로 권한 재구성 */
    public static Permission createReconstituted(UUID permissionId) {
        return Permission.reconstitute(
                PermissionId.of(permissionId),
                PermissionKey.of("user:read"),
                PermissionDescription.of("사용자 조회 권한"),
                PermissionType.CUSTOM,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** 지정된 ID와 key로 권한 재구성 */
    public static Permission createReconstituted(UUID permissionId, String key) {
        return Permission.reconstitute(
                PermissionId.of(permissionId),
                PermissionKey.of(key),
                PermissionDescription.empty(),
                PermissionType.CUSTOM,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    /** 시스템 권한 재구성 */
    public static Permission createReconstitutedSystem(UUID permissionId, String key) {
        return Permission.reconstitute(
                PermissionId.of(permissionId),
                PermissionKey.of(key),
                PermissionDescription.empty(),
                PermissionType.SYSTEM,
                false,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:00:00Z"));
    }

    // ========== 삭제된 권한 ==========

    /** 삭제된 권한 */
    public static Permission createDeleted() {
        return Permission.reconstitute(
                PermissionId.of(UUID.randomUUID()),
                PermissionKey.of("deleted:permission"),
                PermissionDescription.of("삭제된 권한"),
                PermissionType.CUSTOM,
                true,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z"));
    }

    // ========== VO Fixture ==========

    /** Resource 생성 */
    public static Resource createResource() {
        return Resource.of("user");
    }

    /** Resource 생성 */
    public static Resource createResource(String value) {
        return Resource.of(value);
    }

    /** Action 생성 */
    public static Action createAction() {
        return Action.of("read");
    }

    /** Action 생성 */
    public static Action createAction(String value) {
        return Action.of(value);
    }

    /** PermissionKey 생성 */
    public static PermissionKey createPermissionKey() {
        return PermissionKey.of("user:read");
    }

    /** PermissionKey 생성 */
    public static PermissionKey createPermissionKey(String value) {
        return PermissionKey.of(value);
    }

    /** PermissionDescription 생성 */
    public static PermissionDescription createDescription() {
        return PermissionDescription.of("테스트 권한 설명");
    }

    /** PermissionDescription 생성 */
    public static PermissionDescription createDescription(String value) {
        return PermissionDescription.of(value);
    }

    /** PermissionId 생성 */
    public static PermissionId createPermissionId() {
        return PermissionId.of(UUID.randomUUID());
    }

    /** PermissionId 생성 */
    public static PermissionId createPermissionId(UUID uuid) {
        return PermissionId.of(uuid);
    }

    /** 테스트용 고정 Clock */
    public static Clock fixedClock() {
        return FIXED_CLOCK;
    }
}
