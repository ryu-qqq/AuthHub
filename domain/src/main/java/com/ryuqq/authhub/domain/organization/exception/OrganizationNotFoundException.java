package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

import java.util.Map;

/**
 * OrganizationNotFoundException - Organization을 찾을 수 없을 때 발생
 *
 * <p>특정 ID의 Organization이 존재하지 않거나 조회할 수 없을 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 * <ul>
 *   <li>존재하지 않는 Organization ID로 조회</li>
 *   <li>삭제된 Organization 조회</li>
 *   <li>권한 없는 Organization 접근</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class OrganizationNotFoundException extends DomainException {

    /**
     * Constructor - organizationId로 예외 생성
     *
     * @param organizationId 조회 시도한 Organization ID
     * @author development-team
     * @since 1.0.0
     */
    public OrganizationNotFoundException(Long organizationId) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getCode(),
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND.getMessage(),
                Map.of("organizationId", organizationId)
        );
    }
}
