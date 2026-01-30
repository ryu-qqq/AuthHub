package com.ryuqq.authhub.domain.permissionendpoint.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import java.util.Map;

/**
 * PermissionEndpointNotFoundException - PermissionEndpoint를 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class PermissionEndpointNotFoundException extends DomainException {

    public PermissionEndpointNotFoundException(Long permissionEndpointId) {
        super(
                PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND,
                Map.of("permissionEndpointId", permissionEndpointId));
    }

    public PermissionEndpointNotFoundException(PermissionEndpointId permissionEndpointId) {
        this(permissionEndpointId.value());
    }

    public PermissionEndpointNotFoundException(String urlPattern, String httpMethod) {
        super(
                PermissionEndpointErrorCode.PERMISSION_ENDPOINT_NOT_FOUND,
                Map.of("urlPattern", urlPattern, "httpMethod", httpMethod));
    }
}
