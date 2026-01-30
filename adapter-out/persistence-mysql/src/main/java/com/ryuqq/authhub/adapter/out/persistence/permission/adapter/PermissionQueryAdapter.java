package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PermissionQueryAdapter - 권한 Query Adapter (조회 전용)
 *
 * <p>PermissionQueryPort 구현체로서 권한 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>PermissionQueryDslRepository (1개) + PermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>existsById() - ID 존재 여부 확인
 *   <li>existsByPermissionKey() - 권한 키 존재 여부 확인 (Global 전역)
 *   <li>findByPermissionKey() - 권한 키로 단건 조회
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
public class PermissionQueryAdapter implements PermissionQueryPort {

    private final PermissionQueryDslRepository repository;
    private final PermissionJpaEntityMapper mapper;

    public PermissionQueryAdapter(
            PermissionQueryDslRepository repository, PermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 권한 단건 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>PermissionId에서 Long 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param permissionId 권한 ID
     * @return Optional<Permission>
     */
    @Override
    public Optional<Permission> findById(PermissionId permissionId) {
        return repository.findByPermissionId(permissionId.value()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 권한 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(PermissionId id) {
        return repository.existsByPermissionId(id.value());
    }

    /**
     * 권한 키 존재 여부 확인 (Global 전역)
     *
     * <p>tenantId와 관계없이 전역적으로 permissionKey 존재 여부를 확인합니다.
     *
     * @param permissionKey 권한 키
     * @return 존재 여부
     */
    @Override
    public boolean existsByPermissionKey(String permissionKey) {
        return repository.existsByPermissionKey(permissionKey);
    }

    /**
     * 권한 키로 단건 조회
     *
     * @param permissionKey 권한 키
     * @return Optional<Permission>
     */
    @Override
    public Optional<Permission> findByPermissionKey(String permissionKey) {
        return repository.findByPermissionKey(permissionKey).map(mapper::toDomain);
    }

    /**
     * 조건에 맞는 권한 목록 조회 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>PermissionSearchCriteria에서 검색 조건 추출
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return Permission Domain 목록
     */
    @Override
    public List<Permission> findAllBySearchCriteria(PermissionSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 권한 개수 조회
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return 조건에 맞는 권한 총 개수
     */
    @Override
    public long countBySearchCriteria(PermissionSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    /**
     * ID 목록으로 권한 다건 조회
     *
     * @param ids 권한 ID 목록
     * @return Permission Domain 목록
     */
    @Override
    public List<Permission> findAllByIds(List<PermissionId> ids) {
        List<Long> permissionIds = ids.stream().map(PermissionId::value).toList();
        return repository.findAllByIds(permissionIds).stream().map(mapper::toDomain).toList();
    }

    /**
     * permissionKey 목록으로 권한 다건 조회
     *
     * <p>벌크 동기화 시 기존 Permission을 한 번에 조회합니다.
     *
     * @param permissionKeys 권한 키 목록
     * @return Permission Domain 목록
     */
    @Override
    public List<Permission> findAllByPermissionKeys(List<String> permissionKeys) {
        return repository.findAllByPermissionKeys(permissionKeys).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
