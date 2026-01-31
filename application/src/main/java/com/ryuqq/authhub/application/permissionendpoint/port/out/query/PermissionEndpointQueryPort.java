package com.ryuqq.authhub.application.permissionendpoint.port.out.query;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.util.List;
import java.util.Optional;

/**
 * PermissionEndpointQueryPort - PermissionEndpoint Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공
 *   <li>저장/수정/삭제 메서드 금지 (CommandPort로 분리)
 *   <li>Value Object 파라미터
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionEndpointQueryPort {

    /**
     * ID로 PermissionEndpoint 단건 조회
     *
     * @param id PermissionEndpoint ID (Value Object)
     * @return PermissionEndpoint Domain (Optional)
     */
    Optional<PermissionEndpoint> findById(PermissionEndpointId id);

    /**
     * ID로 PermissionEndpoint 존재 여부 확인
     *
     * @param id PermissionEndpoint ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(PermissionEndpointId id);

    /**
     * URL 패턴과 HTTP 메서드 조합으로 존재 여부 확인
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return 존재 여부
     */
    boolean existsByUrlPatternAndHttpMethod(String urlPattern, HttpMethod httpMethod);

    /**
     * URL 패턴과 HTTP 메서드로 PermissionEndpoint 조회
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return PermissionEndpoint Domain (Optional)
     */
    Optional<PermissionEndpoint> findByUrlPatternAndHttpMethod(
            String urlPattern, HttpMethod httpMethod);

    /**
     * Permission ID로 연결된 모든 엔드포인트 조회
     *
     * @param permissionId 권한 ID
     * @return PermissionEndpoint Domain 목록
     */
    List<PermissionEndpoint> findAllByPermissionId(PermissionId permissionId);

    /**
     * 조건에 맞는 엔드포인트 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건
     * @return PermissionEndpoint Domain 목록
     */
    List<PermissionEndpoint> findAllBySearchCriteria(PermissionEndpointSearchCriteria criteria);

    /**
     * 조건에 맞는 엔드포인트 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건
     * @return 조건에 맞는 PermissionEndpoint 총 개수
     */
    long countBySearchCriteria(PermissionEndpointSearchCriteria criteria);

    /**
     * URL 패턴에 매칭되는 모든 엔드포인트 조회 (Gateway용)
     *
     * <p>URL 패턴 매칭을 통해 요청 URL에 해당하는 엔드포인트를 찾습니다.
     *
     * @param requestUrl 요청 URL
     * @param httpMethod HTTP 메서드
     * @return 매칭되는 PermissionEndpoint 목록
     */
    List<PermissionEndpoint> findMatchingEndpoints(String requestUrl, HttpMethod httpMethod);

    /**
     * URL 패턴과 HTTP 메서드 조합 목록으로 존재하는 엔드포인트 조회 (IN절)
     *
     * <p>벌크 동기화 시 기존 PermissionEndpoint를 한 번에 조회합니다.
     *
     * @param urlPatterns URL 패턴 목록
     * @return 존재하는 PermissionEndpoint 목록
     */
    List<PermissionEndpoint> findAllByUrlPatterns(List<String> urlPatterns);
}
