package com.ryuqq.authhub.application.permission.port.in.query;

import com.ryuqq.authhub.application.permission.dto.response.PermissionSpecResponse;

/**
 * GetPermissionSpecUseCase - 권한 명세 조회 UseCase
 *
 * <p>Gateway에서 엔드포인트별 권한 명세를 조회할 때 사용됩니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query UseCase 인터페이스
 *   <li>Response DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetPermissionSpecUseCase {

    /**
     * 권한 명세 조회
     *
     * @return 전체 엔드포인트별 권한 명세
     */
    PermissionSpecResponse execute();
}
