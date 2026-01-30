package com.ryuqq.authhub.application.permissionendpoint.port.out.query;

import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import java.util.List;

/**
 * PermissionEndpointSpecQueryPort - Gateway용 엔드포인트-권한 스펙 조회 포트
 *
 * <p>PermissionEndpoint와 Permission을 조인하여 Gateway가 필요로 하는 스펙 정보를 제공합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공
 *   <li>Application DTO 반환 (Domain 조합이 필요하므로 예외적으로 DTO 반환 허용)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionEndpointSpecQueryPort {

    /**
     * 모든 활성 엔드포인트-권한 스펙 조회
     *
     * <p>삭제되지 않은 PermissionEndpoint와 연결된 Permission의 permissionKey를 함께 조회합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    List<EndpointPermissionSpecResult> findAllActiveSpecs();
}
