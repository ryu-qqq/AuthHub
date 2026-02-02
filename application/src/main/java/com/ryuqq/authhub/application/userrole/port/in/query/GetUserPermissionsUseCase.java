package com.ryuqq.authhub.application.userrole.port.in.query;

import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;

/**
 * GetUserPermissionsUseCase - Gateway용 사용자 권한 조회 UseCase
 *
 * <p>Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>Gateway 요청 처리 시 사용자 인가 검증
 *   <li>엔드포인트별 권한 검사
 * </ul>
 *
 * <p><strong>예외:</strong> 존재하지 않는 사용자인 경우 빈 역할/권한 반환
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserPermissionsUseCase {

    /**
     * 사용자 ID로 역할/권한 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 역할/권한 정보
     */
    UserPermissionsResult getByUserId(String userId);
}
