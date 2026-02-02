package com.ryuqq.authhub.application.permissionendpoint.fixture;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;

/**
 * PermissionEndpoint Command DTO 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointCommandFixtures {

    private static final Long DEFAULT_ENDPOINT_ID = PermissionEndpointFixture.defaultIdValue();
    private static final Long DEFAULT_PERMISSION_ID =
            PermissionEndpointFixture.defaultPermissionIdValue();
    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final String DEFAULT_URL_PATTERN = PermissionEndpointFixture.defaultUrlPattern();
    private static final String DEFAULT_HTTP_METHOD = "GET";
    private static final String DEFAULT_DESCRIPTION = "사용자 목록 조회 엔드포인트";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    private PermissionEndpointCommandFixtures() {}

    /** 기본 생성 Command 반환 */
    public static CreatePermissionEndpointCommand createCommand() {
        return new CreatePermissionEndpointCommand(
                DEFAULT_PERMISSION_ID,
                DEFAULT_SERVICE_NAME,
                DEFAULT_URL_PATTERN,
                DEFAULT_HTTP_METHOD,
                DEFAULT_DESCRIPTION,
                DEFAULT_IS_PUBLIC);
    }

    /** 지정된 값으로 생성 Command 반환 */
    public static CreatePermissionEndpointCommand createCommand(
            Long permissionId,
            String serviceName,
            String urlPattern,
            String httpMethod,
            String description,
            boolean isPublic) {
        return new CreatePermissionEndpointCommand(
                permissionId, serviceName, urlPattern, httpMethod, description, isPublic);
    }

    /** 기본 수정 Command 반환 */
    public static UpdatePermissionEndpointCommand updateCommand() {
        return new UpdatePermissionEndpointCommand(
                DEFAULT_ENDPOINT_ID,
                DEFAULT_SERVICE_NAME,
                "/api/v1/users/{id}",
                "PUT",
                "수정된 설명",
                DEFAULT_IS_PUBLIC);
    }

    /** 기본 삭제 Command 반환 */
    public static DeletePermissionEndpointCommand deleteCommand() {
        return new DeletePermissionEndpointCommand(DEFAULT_ENDPOINT_ID);
    }
}
