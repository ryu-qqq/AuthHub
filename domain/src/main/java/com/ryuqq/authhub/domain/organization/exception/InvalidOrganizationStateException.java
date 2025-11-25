package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InvalidOrganizationStateException - Organization이 유효하지 않은 상태일 때 발생
 *
 * <p>Organization이 비즈니스 규칙에 위배되는 상태이거나 허용되지 않는 작업을 시도할 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 *
 * <ul>
 *   <li>삭제된 Organization에 대한 작업 시도
 *   <li>비활성화된 Organization 사용
 *   <li>만료된 Organization 접근
 *   <li>상태 전환 규칙 위반
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidOrganizationStateException extends DomainException {

    /**
     * Constructor - organizationId와 사유로 예외 생성
     *
     * @param organizationId 상태가 유효하지 않은 Organization ID
     * @param reason 상태 유효성 위반 사유
     * @author development-team
     * @since 1.0.0
     */
    public InvalidOrganizationStateException(Long organizationId, String reason) {
        super(
                OrganizationErrorCode.INVALID_ORGANIZATION_STATUS.getCode(),
                OrganizationErrorCode.INVALID_ORGANIZATION_STATUS.getMessage(),
                Map.of(
                        "organizationId", organizationId,
                        "reason", reason));
    }
}
