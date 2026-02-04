package com.ryuqq.authhub.domain.service.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.util.Map;

/**
 * ServiceInUseException - 서비스가 사용 중일 때 발생하는 예외
 *
 * <p>Permission이나 TenantService에서 참조 중인 Service를 삭제하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class ServiceInUseException extends DomainException {

    public ServiceInUseException(ServiceId serviceId) {
        super(ServiceErrorCode.SERVICE_IN_USE, Map.of("serviceId", serviceId.value()));
    }

    public ServiceInUseException(String serviceId) {
        super(ServiceErrorCode.SERVICE_IN_USE, Map.of("serviceId", serviceId));
    }
}
