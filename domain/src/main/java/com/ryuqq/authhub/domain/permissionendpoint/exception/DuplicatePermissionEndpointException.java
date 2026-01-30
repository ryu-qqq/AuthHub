package com.ryuqq.authhub.domain.permissionendpoint.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * DuplicatePermissionEndpointException - 동일한 URL 패턴과 HTTP 메서드 조합이 이미 존재할 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicatePermissionEndpointException extends DomainException {

    public DuplicatePermissionEndpointException(String urlPattern, String httpMethod) {
        super(
                PermissionEndpointErrorCode.DUPLICATE_PERMISSION_ENDPOINT,
                Map.of("urlPattern", urlPattern, "httpMethod", httpMethod));
    }
}
