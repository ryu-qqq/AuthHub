package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * OrganizationQueryAdapter - 조직 Query Adapter (조회 전용)
 *
 * <p>OrganizationQueryPort 구현체로서 조직 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>OrganizationQueryDslRepository (1개) + OrganizationJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>existsByTenantIdAndName() - 테넌트 내 이름 중복 확인
 *   <li>findAllByTenantIdAndCriteria() - 테넌트 범위 조건 검색
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
public class OrganizationQueryAdapter implements OrganizationQueryPort {

    private final OrganizationQueryDslRepository repository;
    private final OrganizationJpaEntityMapper mapper;

    public OrganizationQueryAdapter(
            OrganizationQueryDslRepository repository, OrganizationJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 조직 단건 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>OrganizationId에서 UUID 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param organizationId 조직 ID
     * @return Optional<Organization>
     */
    @Override
    public Optional<Organization> findById(OrganizationId organizationId) {
        return repository.findByOrganizationId(organizationId.value()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param organizationId 조직 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(OrganizationId organizationId) {
        return repository.existsByOrganizationId(organizationId.value());
    }

    /**
     * 테넌트 내 이름 중복 확인
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndName(TenantId tenantId, OrganizationName name) {
        return repository.existsByTenantIdAndName(tenantId.value(), name.value());
    }

    /**
     * 테넌트 범위 내 조직 목록 조회 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름 필터 (null 허용, 부분 검색)
     * @param status 조직 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return Organization Domain 목록
     */
    @Override
    public List<Organization> findAllByTenantIdAndCriteria(
            TenantId tenantId, String name, String status, int offset, int limit) {
        return repository
                .findAllByTenantIdAndCriteria(tenantId.value(), name, status, offset, limit)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * 테넌트 범위 내 조직 개수 조회
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름 필터 (null 허용, 부분 검색)
     * @param status 조직 상태 필터 (null 허용)
     * @return 조건에 맞는 조직 총 개수
     */
    @Override
    public long countByTenantIdAndCriteria(TenantId tenantId, String name, String status) {
        return repository.countByTenantIdAndCriteria(tenantId.value(), name, status);
    }
}
