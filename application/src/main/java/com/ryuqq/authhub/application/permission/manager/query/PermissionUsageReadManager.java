package com.ryuqq.authhub.application.permission.manager.query;

import com.ryuqq.authhub.application.permission.port.out.query.PermissionUsageQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionUsageReadManager - 권한 사용 이력 조회 관리자
 *
 * <p>PermissionUsage QueryPort에 대한 읽기 전용 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 클래스 레벨
 *   <li>조회 메서드만 제공 (순수 위임)
 *   <li>단일 QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class PermissionUsageReadManager {

    private final PermissionUsageQueryPort queryPort;

    public PermissionUsageReadManager(PermissionUsageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 권한키 + 서비스명으로 사용 이력 조회
     *
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @return 사용 이력 (없으면 Optional.empty())
     */
    public Optional<PermissionUsage> findByKeyAndService(
            PermissionKey permissionKey, ServiceName serviceName) {
        return queryPort.findByKeyAndService(permissionKey, serviceName);
    }

    /**
     * 권한키로 모든 사용 이력 조회
     *
     * @param permissionKey 권한 키
     * @return 사용 이력 목록
     */
    public List<PermissionUsage> findAllByPermissionKey(PermissionKey permissionKey) {
        return queryPort.findAllByPermissionKey(permissionKey);
    }

    /**
     * 서비스명으로 모든 사용 이력 조회
     *
     * @param serviceName 서비스명
     * @return 사용 이력 목록
     */
    public List<PermissionUsage> findAllByServiceName(ServiceName serviceName) {
        return queryPort.findAllByServiceName(serviceName);
    }
}
