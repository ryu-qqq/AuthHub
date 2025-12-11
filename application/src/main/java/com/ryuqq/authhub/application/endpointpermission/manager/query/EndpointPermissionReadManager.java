package com.ryuqq.authhub.application.endpointpermission.manager.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.port.out.query.EndpointPermissionQueryPort;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * EndpointPermissionReadManager - 읽기 전용 Port 관리
 *
 * <p>QueryPort에 대한 읽기 전용 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 클래스 레벨
 *   <li>조회 메서드만 제공 (순수 위임)
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class EndpointPermissionReadManager {

    private final EndpointPermissionQueryPort queryPort;

    public EndpointPermissionReadManager(EndpointPermissionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 EndpointPermission 조회
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @return Optional EndpointPermission
     */
    public Optional<EndpointPermission> findById(EndpointPermissionId endpointPermissionId) {
        return queryPort.findById(endpointPermissionId);
    }

    /**
     * 서비스명 + 경로 + 메서드로 조회
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return Optional EndpointPermission
     */
    public Optional<EndpointPermission> findByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method) {
        return queryPort.findByServiceNameAndPathAndMethod(serviceName, path, method);
    }

    /**
     * 서비스명 + 경로 + 메서드 존재 여부 확인
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return 존재 여부
     */
    public boolean existsByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method) {
        return queryPort.existsByServiceNameAndPathAndMethod(serviceName, path, method);
    }

    /**
     * 서비스별 엔드포인트 권한 목록 조회 (경로 매칭용)
     *
     * @param serviceName 서비스 이름
     * @param method HTTP 메서드
     * @return EndpointPermission 목록
     */
    public List<EndpointPermission> findAllByServiceNameAndMethod(
            ServiceName serviceName, HttpMethod method) {
        return queryPort.findAllByServiceNameAndMethod(serviceName, method);
    }

    /**
     * 엔드포인트 권한 검색
     *
     * @param query 검색 조건
     * @return EndpointPermission 목록
     */
    public List<EndpointPermission> search(SearchEndpointPermissionsQuery query) {
        return queryPort.search(query);
    }

    /**
     * 엔드포인트 권한 검색 총 개수
     *
     * @param query 검색 조건
     * @return 총 개수
     */
    public long count(SearchEndpointPermissionsQuery query) {
        return queryPort.count(query);
    }
}
