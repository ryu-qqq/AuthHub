package com.ryuqq.authhub.application.permission.port.out.query;

import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.util.List;
import java.util.Optional;

/**
 * PermissionUsageQueryPort - 권한 사용 이력 조회 Port (Port-Out)
 *
 * <p>권한 사용 이력 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}QueryPort} 네이밍
 *   <li>Domain VO 파라미터, Domain Aggregate 반환
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionUsageQueryPort {

    /**
     * 권한키 + 서비스명으로 사용 이력 조회
     *
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @return 사용 이력 (없으면 Optional.empty())
     */
    Optional<PermissionUsage> findByKeyAndService(
            PermissionKey permissionKey, ServiceName serviceName);

    /**
     * 권한키로 모든 사용 이력 조회
     *
     * @param permissionKey 권한 키
     * @return 사용 이력 목록
     */
    List<PermissionUsage> findAllByPermissionKey(PermissionKey permissionKey);

    /**
     * 서비스명으로 모든 사용 이력 조회
     *
     * @param serviceName 서비스명
     * @return 사용 이력 목록
     */
    List<PermissionUsage> findAllByServiceName(ServiceName serviceName);
}
