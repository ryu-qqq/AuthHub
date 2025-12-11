package com.ryuqq.authhub.application.permission.port.out.query;

import com.ryuqq.authhub.application.permission.dto.response.EndpointPermissionResponse;
import java.util.List;

/**
 * PermissionSpecPort - 권한 명세 조회 Port
 *
 * <p>엔드포인트별 권한 명세를 조회하는 아웃바운드 포트입니다. Persistence Layer의 EndpointPermission 테이블에서 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query Port 인터페이스
 *   <li>Application DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionSpecPort {

    /**
     * 서비스명 조회
     *
     * @return 서비스 식별자
     */
    String getServiceName();

    /**
     * 전체 엔드포인트 권한 목록 조회 (삭제되지 않은 항목만)
     *
     * @return 엔드포인트별 권한 목록
     */
    List<EndpointPermissionResponse> getEndpointPermissions();

    /**
     * 활성화된 엔드포인트 권한 개수 조회
     *
     * @return 활성화된 권한 개수
     */
    long countActivePermissions();
}
