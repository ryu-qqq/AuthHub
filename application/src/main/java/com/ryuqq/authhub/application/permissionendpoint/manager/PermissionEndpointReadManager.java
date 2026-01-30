package com.ryuqq.authhub.application.permissionendpoint.manager;

import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointQueryPort;
import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointSpecQueryPort;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionEndpointReadManager - 단일 Port 조회 관리
 *
 * <p>QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 *   <li>Criteria 기반 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointReadManager {

    private final PermissionEndpointQueryPort queryPort;
    private final PermissionEndpointSpecQueryPort specQueryPort;

    public PermissionEndpointReadManager(
            PermissionEndpointQueryPort queryPort, PermissionEndpointSpecQueryPort specQueryPort) {
        this.queryPort = queryPort;
        this.specQueryPort = specQueryPort;
    }

    /**
     * ID로 PermissionEndpoint 조회 (필수)
     *
     * @param id PermissionEndpoint ID
     * @return PermissionEndpoint Domain
     * @throws PermissionEndpointNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public PermissionEndpoint findById(PermissionEndpointId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new PermissionEndpointNotFoundException(id));
    }

    /**
     * ID로 PermissionEndpoint 존재 여부 확인
     *
     * @param id PermissionEndpoint ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(PermissionEndpointId id) {
        return queryPort.existsById(id);
    }

    /**
     * URL 패턴과 HTTP 메서드로 존재 여부 확인
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByUrlPatternAndHttpMethod(String urlPattern, HttpMethod httpMethod) {
        return queryPort.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod);
    }

    /**
     * URL 패턴과 HTTP 메서드로 PermissionEndpoint 조회
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return PermissionEndpoint Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<PermissionEndpoint> findByUrlPatternAndHttpMethod(
            String urlPattern, HttpMethod httpMethod) {
        return queryPort.findByUrlPatternAndHttpMethod(urlPattern, httpMethod);
    }

    /**
     * 검색 조건으로 PermissionEndpoint 목록 조회
     *
     * @param criteria 검색 조건 (PermissionEndpointSearchCriteria)
     * @return PermissionEndpoint Domain 목록
     */
    @Transactional(readOnly = true)
    public List<PermissionEndpoint> findAllBySearchCriteria(
            PermissionEndpointSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 검색 조건으로 PermissionEndpoint 개수 조회
     *
     * @param criteria 검색 조건 (PermissionEndpointSearchCriteria)
     * @return 조건에 맞는 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(PermissionEndpointSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }

    /**
     * 모든 활성 엔드포인트-권한 스펙 조회
     *
     * <p>Gateway가 URL 기반 권한 검사를 위해 전체 매핑 정보를 조회합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    @Transactional(readOnly = true)
    public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
        return specQueryPort.findAllActiveSpecs();
    }

    /**
     * URL 패턴 목록으로 존재하는 PermissionEndpoint 조회 (IN절)
     *
     * <p>벌크 동기화 시 기존 엔드포인트를 한 번에 조회합니다.
     *
     * @param urlPatterns URL 패턴 목록
     * @return 존재하는 PermissionEndpoint 목록
     */
    @Transactional(readOnly = true)
    public List<PermissionEndpoint> findAllByUrlPatterns(List<String> urlPatterns) {
        if (urlPatterns == null || urlPatterns.isEmpty()) {
            return List.of();
        }
        return queryPort.findAllByUrlPatterns(urlPatterns);
    }
}
