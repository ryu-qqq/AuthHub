package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;

/**
 * GetTenantConfigUseCase - Gateway용 테넌트 설정 조회 UseCase
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 설정 정보를 조회합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>Gateway 요청 처리 시 테넌트 활성 여부 검증
 *   <li>테넌트별 설정 캐싱
 * </ul>
 *
 * <p><strong>예외:</strong> 존재하지 않는 테넌트인 경우 {@code TenantNotFoundException} 발생
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetTenantConfigUseCase {

    /**
     * 테넌트 ID로 설정 조회
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 설정
     * @throws com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException 존재하지 않는 테넌트인 경우
     */
    TenantConfigResult getByTenantId(String tenantId);
}
