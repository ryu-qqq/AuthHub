package com.ryuqq.authhub.domain.endpointpermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.util.Map;
import java.util.UUID;

/**
 * EndpointPermissionNotFoundException - 엔드포인트 권한 매핑을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class EndpointPermissionNotFoundException extends DomainException {

    public EndpointPermissionNotFoundException(UUID endpointPermissionId) {
        super(
                EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND,
                Map.of("endpointPermissionId", endpointPermissionId));
    }

    public EndpointPermissionNotFoundException(EndpointPermissionId endpointPermissionId) {
        this(endpointPermissionId.value());
    }

    public EndpointPermissionNotFoundException(String serviceName, String path, String method) {
        super(
                EndpointPermissionErrorCode.ENDPOINT_PERMISSION_NOT_FOUND,
                Map.of("serviceName", serviceName, "path", path, "method", method));
    }
}
