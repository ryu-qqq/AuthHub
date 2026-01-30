package com.ryuqq.authhub.application.role.fixture;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;

/**
 * Role Command DTO 테스트 픽스처
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
public final class RoleCommandFixtures {

    private static final Long DEFAULT_ROLE_ID = RoleFixture.defaultIdValue();
    private static final String DEFAULT_TENANT_ID = RoleFixture.defaultTenantIdString();
    private static final String DEFAULT_NAME = "TEST_ROLE";
    private static final String DEFAULT_DISPLAY_NAME = "테스트 역할";
    private static final String DEFAULT_DESCRIPTION = "테스트용 역할입니다";

    private RoleCommandFixtures() {}

    // ==================== CreateRoleCommand ====================

    /** 기본 생성 Command 반환 (Global 커스텀 역할) */
    public static CreateRoleCommand createCommand() {
        return new CreateRoleCommand(
                null, DEFAULT_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_DESCRIPTION, false);
    }

    /** 지정된 이름으로 생성 Command 반환 */
    public static CreateRoleCommand createCommandWithName(String name) {
        return new CreateRoleCommand(null, name, name + " 표시명", name + " 설명", false);
    }

    /** 테넌트 역할 생성 Command 반환 */
    public static CreateRoleCommand createTenantCommand() {
        return new CreateRoleCommand(
                DEFAULT_TENANT_ID, DEFAULT_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_DESCRIPTION, false);
    }

    /** 시스템 역할 생성 Command 반환 */
    public static CreateRoleCommand createSystemCommand() {
        return new CreateRoleCommand(null, "SUPER_ADMIN", "슈퍼 관리자", "시스템 전체 관리 권한", true);
    }

    // ==================== UpdateRoleCommand ====================

    /** 기본 수정 Command 반환 */
    public static UpdateRoleCommand updateCommand() {
        return new UpdateRoleCommand(DEFAULT_ROLE_ID, "수정된 표시명", "수정된 설명");
    }

    /** 지정된 값으로 수정 Command 반환 */
    public static UpdateRoleCommand updateCommand(
            Long roleId, String displayName, String description) {
        return new UpdateRoleCommand(roleId, displayName, description);
    }

    // ==================== DeleteRoleCommand ====================

    /** 기본 삭제 Command 반환 */
    public static DeleteRoleCommand deleteCommand() {
        return new DeleteRoleCommand(DEFAULT_ROLE_ID);
    }

    /** 지정된 역할 ID로 삭제 Command 반환 */
    public static DeleteRoleCommand deleteCommand(Long roleId) {
        return new DeleteRoleCommand(roleId);
    }

    // ==================== 기본값 접근자 ====================

    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }
}
