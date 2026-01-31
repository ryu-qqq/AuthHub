package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RoleQueryAdapter - 역할 Query Adapter (조회 전용)
 *
 * <p>RoleQueryPort 구현체로서 역할 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>RoleQueryDslRepository (1개) + RoleJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>existsById() - ID 존재 여부 확인
 *   <li>existsByTenantIdAndName() - 테넌트 내 역할 이름 존재 여부 확인
 *   <li>findByTenantIdAndName() - 테넌트 내 역할 이름으로 단건 조회
 *   <li>findAllBySearchCriteria() - 조건 검색
 *   <li>countBySearchCriteria() - 조건 검색 개수
 *   <li>findAllByIds() - ID 목록으로 다건 조회
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
public class RoleQueryAdapter implements RoleQueryPort {

    private final RoleQueryDslRepository repository;
    private final RoleJpaEntityMapper mapper;

    public RoleQueryAdapter(RoleQueryDslRepository repository, RoleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 역할 단건 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>RoleId에서 Long 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param id 역할 ID
     * @return Optional<Role>
     */
    @Override
    public Optional<Role> findById(RoleId id) {
        return repository.findByRoleId(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 역할 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(RoleId id) {
        return repository.existsByRoleId(id.value());
    }

    /**
     * 테넌트 내 역할 이름으로 존재 여부 확인
     *
     * <p>tenantId가 null이면 Global 역할 내에서 중복 확인합니다.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndName(TenantId tenantId, RoleName name) {
        String tenantIdValue = tenantId != null ? tenantId.value() : null;
        return repository.existsByTenantIdAndName(tenantIdValue, name.value());
    }

    /**
     * 테넌트 내 역할 이름으로 Role 조회
     *
     * <p>tenantId가 null이면 Global 역할 내에서 조회합니다.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @return Optional<Role>
     */
    @Override
    public Optional<Role> findByTenantIdAndName(TenantId tenantId, RoleName name) {
        String tenantIdValue = tenantId != null ? tenantId.value() : null;
        return repository.findByTenantIdAndName(tenantIdValue, name.value()).map(mapper::toDomain);
    }

    /**
     * 조건에 맞는 역할 목록 조회 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>RoleSearchCriteria에서 검색 조건 추출
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return Role Domain 목록
     */
    @Override
    public List<Role> findAllBySearchCriteria(RoleSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 역할 개수 조회
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return 조건에 맞는 역할 총 개수
     */
    @Override
    public long countBySearchCriteria(RoleSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    /**
     * ID 목록으로 역할 다건 조회
     *
     * @param ids 역할 ID 목록
     * @return Role Domain 목록
     */
    @Override
    public List<Role> findAllByIds(List<RoleId> ids) {
        List<Long> roleIds = ids.stream().map(RoleId::value).toList();
        return repository.findAllByIds(roleIds).stream().map(mapper::toDomain).toList();
    }
}
