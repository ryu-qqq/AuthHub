package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
 *   <li>findByKey() - 권한 키로 단건 조회
 *   <li>existsByKey() - 권한 키 존재 여부 확인
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
     *   <li>PermissionId에서 UUID 추출
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
     * 권한 키로 권한 단건 조회
     *
     * @param key 권한 키 ("{resource}:{action}" 형식)
     * @return Optional<Permission>
     */
    @Override
    public Optional<Permission> findByKey(PermissionKey key) {
        return repository.findByKey(key.value()).map(mapper::toDomain);
    }

    /**
     * 권한 키 존재 여부 확인
     *
     * @param key 권한 키
     * @return 존재 여부
     */
    @Override
    public boolean existsByKey(PermissionKey key) {
        return repository.existsByKey(key.value());
    }

    /**
     * 권한 검색 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Query에서 검색 조건 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param query 검색 조건
     * @return Permission 목록
     */
    @Override
    public List<Permission> search(SearchPermissionsQuery query) {
        int offset = query.page() * query.size();
        String typeString = query.type() != null ? query.type().name() : null;

        return repository
                .search(query.resource(), query.action(), typeString, offset, query.size())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * 권한 검색 개수 조회
     *
     * @param query 검색 조건
     * @return 조건에 맞는 권한 총 개수
     */
    @Override
    public long count(SearchPermissionsQuery query) {
        String typeString = query.type() != null ? query.type().name() : null;
        return repository.count(query.resource(), query.action(), typeString);
    }

    /**
     * 여러 ID로 권한 목록 조회
     *
     * @param permissionIds 권한 ID Set
     * @return Permission 목록
     */
    @Override
    public List<Permission> findAllByIds(Set<PermissionId> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        Set<UUID> uuids =
                permissionIds.stream().map(PermissionId::value).collect(Collectors.toSet());
        return repository.findAllByIds(uuids).stream().map(mapper::toDomain).toList();
    }
}
