package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * TenantQueryAdapter - 테넌트 Query Adapter (조회 전용)
 *
 * <p>TenantQueryPort 구현체로서 테넌트 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>TenantQueryDslRepository (1개) + TenantJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>existsByName() - 이름 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
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
public class TenantQueryAdapter implements TenantQueryPort {

    private final TenantQueryDslRepository repository;
    private final TenantJpaEntityMapper mapper;

    public TenantQueryAdapter(TenantQueryDslRepository repository, TenantJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 테넌트 단건 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>TenantId에서 UUID 추출 후 String 변환
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param tenantId 테넌트 ID
     * @return Optional<Tenant>
     */
    @Override
    public Optional<Tenant> findById(TenantId tenantId) {
        return repository.findByTenantId(tenantId.value().toString()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param tenantId 테넌트 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(TenantId tenantId) {
        return repository.existsByTenantId(tenantId.value().toString());
    }

    /**
     * 이름 존재 여부 확인
     *
     * @param name 테넌트 이름
     * @return 존재 여부
     */
    @Override
    public boolean existsByName(TenantName name) {
        return repository.existsByName(name.value());
    }

    /**
     * 이름 존재 여부 확인 (특정 ID 제외)
     *
     * <p>이름 변경 시 중복 검증에 사용됩니다 (자기 자신 제외).
     *
     * @param name 테넌트 이름
     * @param excludeId 제외할 테넌트 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsByNameAndIdNot(TenantName name, TenantId excludeId) {
        return repository.existsByNameAndIdNot(name.value(), excludeId.value().toString());
    }

    /**
     * 조건에 맞는 테넌트 목록 조회 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>TenantSearchCriteria에서 검색 조건 추출
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return Tenant Domain 목록
     */
    @Override
    public List<Tenant> findAllByCriteria(TenantSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 테넌트 개수 조회
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return 조건에 맞는 테넌트 총 개수
     */
    @Override
    public long countByCriteria(TenantSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
