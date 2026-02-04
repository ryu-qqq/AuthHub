package com.ryuqq.authhub.domain.service.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import java.util.Map;

/**
 * DuplicateServiceIdException - 서비스 코드가 중복될 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateServiceIdException extends DomainException {

    public DuplicateServiceIdException(ServiceCode serviceCode) {
        super(ServiceErrorCode.DUPLICATE_SERVICE_CODE, Map.of("serviceCode", serviceCode.value()));
    }

    public DuplicateServiceIdException(String serviceCode) {
        super(ServiceErrorCode.DUPLICATE_SERVICE_CODE, Map.of("serviceCode", serviceCode));
    }
}
