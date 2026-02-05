package com.ryuqq.authhub.application.service.validator;

import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.exception.DuplicateServiceIdException;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import org.springframework.stereotype.Component;

/**
 * ServiceValidator - 서비스 비즈니스 규칙 검증
 *
 * <p>서비스 관련 비즈니스 규칙 검증을 담당합니다. 조회가 필요한 검증 로직을 Service에서 분리합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceValidator {

    private final ServiceReadManager readManager;

    public ServiceValidator(ServiceReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * Service 존재 여부 검증
     *
     * @param id Service ID (VO)
     * @throws ServiceNotFoundException 존재하지 않는 경우
     */
    public void validateExists(ServiceId id) {
        if (!readManager.existsById(id)) {
            throw new ServiceNotFoundException(id);
        }
    }

    /**
     * Service 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Service ID (VO)
     * @return Service 조회된 도메인 객체
     * @throws ServiceNotFoundException 존재하지 않는 경우
     */
    public Service findExistingOrThrow(ServiceId id) {
        return readManager.findById(id);
    }

    /**
     * Service Code 중복 검증 (신규 생성용)
     *
     * @param serviceCode 검증할 서비스 코드 (VO)
     * @throws DuplicateServiceIdException 동일 코드의 서비스가 존재하는 경우
     */
    public void validateCodeNotDuplicated(ServiceCode serviceCode) {
        if (readManager.existsByCode(serviceCode)) {
            throw new DuplicateServiceIdException(serviceCode);
        }
    }
}
