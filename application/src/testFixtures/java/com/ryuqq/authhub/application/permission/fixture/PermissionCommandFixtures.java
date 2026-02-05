package com.ryuqq.authhub.application.permission.fixture;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;

/**
 * Permission Command DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Command DTO를 제공합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain Fixture와 일관된 기본값 사용
 *   <li>테스트 가독성을 위한 명확한 팩토리 메서드
 *   <li>불변 객체 반환으로 테스트 격리 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionCommandFixtures {

    private static final Long DEFAULT_PERMISSION_ID = PermissionFixture.defaultIdValue();
    private static final String DEFAULT_RESOURCE = PermissionFixture.defaultResource();
    private static final String DEFAULT_ACTION = PermissionFixture.defaultAction();
    private static final String DEFAULT_DESCRIPTION = "사용자 조회 권한";

    private PermissionCommandFixtures() {}

    // ==================== CreatePermissionCommand ====================

    /** 기본 생성 Command 반환 (커스텀 권한) */
    public static CreatePermissionCommand createCommand() {
        return new CreatePermissionCommand(
                null, DEFAULT_RESOURCE, DEFAULT_ACTION, DEFAULT_DESCRIPTION, false);
    }

    /** 지정된 리소스/액션으로 생성 Command 반환 */
    public static CreatePermissionCommand createCommand(String resource, String action) {
        return new CreatePermissionCommand(
                null, resource, action, resource + " " + action + " 권한", false);
    }

    /** 시스템 권한 생성 Command 반환 */
    public static CreatePermissionCommand createSystemCommand() {
        return new CreatePermissionCommand(
                null, DEFAULT_RESOURCE, DEFAULT_ACTION, DEFAULT_DESCRIPTION, true);
    }

    /** 모든 값을 지정하여 생성 Command 반환 */
    public static CreatePermissionCommand createCommand(
            String resource, String action, String description, boolean isSystem) {
        return new CreatePermissionCommand(null, resource, action, description, isSystem);
    }

    // ==================== UpdatePermissionCommand ====================

    /** 기본 수정 Command 반환 */
    public static UpdatePermissionCommand updateCommand() {
        return new UpdatePermissionCommand(DEFAULT_PERMISSION_ID, "수정된 설명");
    }

    /** 지정된 값으로 수정 Command 반환 */
    public static UpdatePermissionCommand updateCommand(Long permissionId, String description) {
        return new UpdatePermissionCommand(permissionId, description);
    }

    // ==================== DeletePermissionCommand ====================

    /** 기본 삭제 Command 반환 */
    public static DeletePermissionCommand deleteCommand() {
        return new DeletePermissionCommand(DEFAULT_PERMISSION_ID);
    }

    /** 지정된 권한 ID로 삭제 Command 반환 */
    public static DeletePermissionCommand deleteCommand(Long permissionId) {
        return new DeletePermissionCommand(permissionId);
    }

    // ==================== 기본값 접근자 ====================

    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }

    public static String defaultResource() {
        return DEFAULT_RESOURCE;
    }

    public static String defaultAction() {
        return DEFAULT_ACTION;
    }
}
