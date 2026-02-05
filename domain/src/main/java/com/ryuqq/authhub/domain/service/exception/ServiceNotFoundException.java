package com.ryuqq.authhub.domain.service.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.util.Map;

/**
 * ServiceNotFoundException - 서비스를 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class ServiceNotFoundException extends DomainException {

    public ServiceNotFoundException(ServiceId serviceId) {
        super(ServiceErrorCode.SERVICE_NOT_FOUND, Map.of("serviceId", serviceId.value()));
    }

    public ServiceNotFoundException(String serviceId) {
        super(ServiceErrorCode.SERVICE_NOT_FOUND, Map.of("serviceId", serviceId));
    }
}
