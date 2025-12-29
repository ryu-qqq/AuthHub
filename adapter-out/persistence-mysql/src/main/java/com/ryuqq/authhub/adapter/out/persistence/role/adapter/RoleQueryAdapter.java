package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
 *   <li>findByTenantIdAndName() - 테넌트 내 역할 이름으로 조회
 *   <li>existsByTenantIdAndName() - 테넌트 내 역할 이름 존재 여부 확인
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
     *   <li>RoleId에서 UUID 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param roleId 역할 ID
     * @return Optional<Role>
     */
    @Override
    public Optional<Role> findById(RoleId roleId) {
        return repository.findByRoleId(roleId.value()).map(mapper::toDomain);
    }

    /**
     * 테넌트 내 역할 이름으로 역할 단건 조회
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return Optional<Role>
     */
    @Override
    public Optional<Role> findByTenantIdAndName(TenantId tenantId, RoleName name) {
        UUID tenantUuid = tenantId != null ? tenantId.value() : null;
        return repository.findByTenantIdAndName(tenantUuid, name.value()).map(mapper::toDomain);
    }

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndName(TenantId tenantId, RoleName name) {
        UUID tenantUuid = tenantId != null ? tenantId.value() : null;
        return repository.existsByTenantIdAndName(tenantUuid, name.value());
    }

    /**
     * 역할 검색 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Query 객체를 직접 Repository로 전달
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param query 검색 조건
     * @return Role 목록
     */
    @Override
    public List<Role> search(SearchRolesQuery query) {
        return repository.searchByQuery(query).stream().map(mapper::toDomain).toList();
    }

    /**
     * 역할 검색 개수 조회
     *
     * @param query 검색 조건
     * @return 조건에 맞는 역할 총 개수
     */
    @Override
    public long count(SearchRolesQuery query) {
        return repository.countByQuery(query);
    }

    /**
     * 여러 ID로 역할 목록 조회
     *
     * @param roleIds 역할 ID Set
     * @return Role 목록
     */
    @Override
    public List<Role> findAllByIds(Set<RoleId> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        Set<UUID> uuids = roleIds.stream().map(RoleId::value).collect(Collectors.toSet());
        return repository.findAllByIds(uuids).stream().map(mapper::toDomain).toList();
    }
}
