package com.ryuqq.authhub.application.endpointpermission.port.out.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.List;
import java.util.Optional;

/**
 * EndpointPermissionQueryPort - 엔드포인트 권한 조회 Port (Port-Out)
 *
 * <p>엔드포인트 권한 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}QueryPort} 네이밍
 *   <li>Domain Aggregate/VO 파라미터/반환
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface EndpointPermissionQueryPort {

    /**
     * ID로 EndpointPermission 조회
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @return Optional EndpointPermission
     */
    Optional<EndpointPermission> findById(EndpointPermissionId endpointPermissionId);

    /**
     * 서비스명 + 경로 + 메서드로 조회 (유니크 제약)
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return Optional EndpointPermission
     */
    Optional<EndpointPermission> findByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method);

    /**
     * 서비스명 + 경로 + 메서드 존재 여부 확인
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return 존재 여부
     */
    boolean existsByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method);

    /**
     * 서비스별 엔드포인트 권한 목록 조회 (경로 매칭용)
     *
     * @param serviceName 서비스 이름
     * @param method HTTP 메서드
     * @return EndpointPermission 목록
     */
    List<EndpointPermission> findAllByServiceNameAndMethod(
            ServiceName serviceName, HttpMethod method);

    /**
     * 엔드포인트 권한 검색
     *
     * @param query 검색 조건
     * @return EndpointPermission 목록
     */
    List<EndpointPermission> search(SearchEndpointPermissionsQuery query);

    /**
     * 엔드포인트 권한 검색 총 개수
     *
     * @param query 검색 조건
     * @return 총 개수
     */
    long count(SearchEndpointPermissionsQuery query);
}
