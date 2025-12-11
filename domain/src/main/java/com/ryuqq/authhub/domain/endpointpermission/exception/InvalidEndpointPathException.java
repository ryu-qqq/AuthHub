package com.ryuqq.authhub.domain.endpointpermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InvalidEndpointPathException - 유효하지 않은 엔드포인트 경로일 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidEndpointPathException extends DomainException {

    public InvalidEndpointPathException(String path) {
        super(EndpointPermissionErrorCode.INVALID_ENDPOINT_PATH, Map.of("path", path));
    }
}
