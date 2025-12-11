package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper.EndpointPermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository.EndpointPermissionQueryDslRepository;
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

/**
 * EndpointPermissionQueryAdapter - 엔드포인트 권한 Query Adapter (조회 전용)
 *
 * <p>EndpointPermissionQueryPort 구현체로서 엔드포인트 권한 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>EndpointPermissionQueryDslRepository (1개) + EndpointPermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>findByServiceNameAndPathAndMethod() - 유니크 키로 단건 조회
 *   <li>existsByServiceNameAndPathAndMethod() - 존재 여부 확인
 *   <li>findAllByServiceNameAndMethod() - 경로 매칭용 목록 조회
 *   <li>search() - 조건 검색
 *   <li>count() - 조건 개수 조회
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Domain 반환 (Mapper로 변환)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointPermissionQueryAdapter implements EndpointPermissionQueryPort {

    private final EndpointPermissionQueryDslRepository repository;
    private final EndpointPermissionJpaEntityMapper mapper;

    public EndpointPermissionQueryAdapter(
            EndpointPermissionQueryDslRepository repository,
            EndpointPermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 엔드포인트 권한 단건 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>EndpointPermissionId에서 UUID 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity -> Domain 변환 (Mapper)
     * </ol>
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @return Optional<EndpointPermission>
     */
    @Override
    public Optional<EndpointPermission> findById(EndpointPermissionId endpointPermissionId) {
        return repository
                .findByEndpointPermissionId(endpointPermissionId.value())
                .map(mapper::toDomain);
    }

    /**
     * 서비스명 + 경로 + HTTP 메서드로 단건 조회
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return Optional<EndpointPermission>
     */
    @Override
    public Optional<EndpointPermission> findByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method) {
        return repository
                .findByServiceNameAndPathAndMethod(serviceName.value(), path.value(), method.name())
                .map(mapper::toDomain);
    }

    /**
     * 서비스명 + 경로 + HTTP 메서드 존재 여부 확인
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return 존재 여부
     */
    @Override
    public boolean existsByServiceNameAndPathAndMethod(
            ServiceName serviceName, EndpointPath path, HttpMethod method) {
        return repository.existsByServiceNameAndPathAndMethod(
                serviceName.value(), path.value(), method.name());
    }

    /**
     * 서비스명 + HTTP 메서드로 목록 조회 (경로 매칭용)
     *
     * <p>인증 시 요청 경로와 매칭할 후보 목록을 조회합니다.
     *
     * @param serviceName 서비스 이름
     * @param method HTTP 메서드
     * @return EndpointPermission 목록
     */
    @Override
    public List<EndpointPermission> findAllByServiceNameAndMethod(
            ServiceName serviceName, HttpMethod method) {
        return repository.findAllByServiceNameAndMethod(serviceName.value(), method.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * 엔드포인트 권한 검색 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Query에서 검색 조건 추출
     *   <li>offset 계산 (page * size)
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity -> Domain 변환 (Mapper)
     * </ol>
     *
     * @param query 검색 조건
     * @return EndpointPermission 목록
     */
    @Override
    public List<EndpointPermission> search(SearchEndpointPermissionsQuery query) {
        int offset = query.page() * query.size();

        return repository
                .search(
                        query.serviceName(),
                        query.pathPattern(),
                        query.method(),
                        query.isPublic(),
                        offset,
                        query.size())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * 엔드포인트 권한 검색 개수 조회
     *
     * @param query 검색 조건
     * @return 조건에 맞는 총 개수
     */
    @Override
    public long count(SearchEndpointPermissionsQuery query) {
        return repository.count(
                query.serviceName(), query.pathPattern(), query.method(), query.isPublic());
    }
}
