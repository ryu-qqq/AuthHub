package com.ryuqq.authhub.application.organization.port.in;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;

/**
 * CreateOrganizationUseCase - 조직 생성 UseCase 인터페이스
 *
 * <p>새 조직 생성을 위한 입력 포트입니다.
 *
 * <p><strong>Command UseCase:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port 구조
 *   <li>트랜잭션은 Manager에서 관리
 *   <li>Tenant 검증 필수
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreateOrganizationUseCase {

    /**
     * 조직 생성 실행
     *
     * @param command 조직 생성 명령
     * @return 생성된 조직 정보
     * @throws com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException Tenant를 찾을 수 없는 경우
     * @throws com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException Tenant가 비활성 상태인
     *     경우
     */
    CreateOrganizationResponse execute(CreateOrganizationCommand command);
}
