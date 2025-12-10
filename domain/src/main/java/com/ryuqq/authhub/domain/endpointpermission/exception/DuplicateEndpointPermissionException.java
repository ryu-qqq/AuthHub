package com.ryuqq.authhub.domain.endpointpermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * DuplicateEndpointPermissionException - 중복된 엔드포인트 권한 매핑일 때 발생하는 예외
 *
 * <p>동일한 serviceName + path + method 조합이 이미 존재할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateEndpointPermissionException extends DomainException {

    public DuplicateEndpointPermissionException(String serviceName, String path, String method) {
        super(
                EndpointPermissionErrorCode.DUPLICATE_ENDPOINT_PERMISSION,
                Map.of("serviceName", serviceName, "path", path, "method", method));
    }
}
