package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper.PermissionEndpointJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointQueryDslRepository;
import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointQueryPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointQueryAdapter - PermissionEndpoint Query Port 구현체
 *
 * <p>PermissionEndpoint Domain을 조회하는 Adapter입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Port 구현체로 @Component 등록
 *   <li>Mapper 의존 필수
 *   <li>Domain 반환 (Entity 반환 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointQueryAdapter implements PermissionEndpointQueryPort {

    private final PermissionEndpointJpaRepository jpaRepository;
    private final PermissionEndpointQueryDslRepository queryDslRepository;
    private final PermissionEndpointJpaEntityMapper mapper;

    public PermissionEndpointQueryAdapter(
            PermissionEndpointJpaRepository jpaRepository,
            PermissionEndpointQueryDslRepository queryDslRepository,
            PermissionEndpointJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<PermissionEndpoint> findById(PermissionEndpointId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(PermissionEndpointId id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    public boolean existsByUrlPatternAndHttpMethod(String urlPattern, HttpMethod httpMethod) {
        return jpaRepository.existsByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                urlPattern, httpMethod);
    }

    @Override
    public Optional<PermissionEndpoint> findByUrlPatternAndHttpMethod(
            String urlPattern, HttpMethod httpMethod) {
        return jpaRepository
                .findByUrlPatternAndHttpMethodAndDeletedAtIsNull(urlPattern, httpMethod)
                .map(mapper::toDomain);
    }

    @Override
    public List<PermissionEndpoint> findAllByPermissionId(PermissionId permissionId) {
        List<PermissionEndpointJpaEntity> entities =
                jpaRepository.findAllByPermissionIdAndDeletedAtIsNull(permissionId.value());
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PermissionEndpoint> findAllBySearchCriteria(
            PermissionEndpointSearchCriteria criteria) {
        List<PermissionEndpointJpaEntity> entities =
                queryDslRepository.findAllBySearchCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countBySearchCriteria(PermissionEndpointSearchCriteria criteria) {
        return queryDslRepository.countBySearchCriteria(criteria);
    }

    @Override
    public List<PermissionEndpoint> findMatchingEndpoints(
            String requestUrl, HttpMethod httpMethod) {
        // Gateway용 매칭 로직 - 실제로는 더 정교한 URL 패턴 매칭이 필요할 수 있음
        List<PermissionEndpointJpaEntity> entities =
                queryDslRepository.findByUrlPatternLike(requestUrl);
        return entities.stream()
                .map(mapper::toDomain)
                .filter(endpoint -> endpoint.matches(requestUrl, httpMethod))
                .toList();
    }

    @Override
    public List<PermissionEndpoint> findAllByUrlPatterns(List<String> urlPatterns) {
        List<PermissionEndpointJpaEntity> entities =
                queryDslRepository.findAllByUrlPatterns(urlPatterns);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
