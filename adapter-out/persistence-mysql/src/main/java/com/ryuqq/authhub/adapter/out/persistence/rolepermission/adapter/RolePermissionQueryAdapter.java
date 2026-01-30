package com.ryuqq.authhub.adapter.out.persistence.rolepermission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.application.rolepermission.port.out.query.RolePermissionQueryPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RolePermissionQueryAdapter - 역할-권한 관계 Query Adapter (조회 전용)
 *
 * <p>RolePermissionQueryPort 구현체로서 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>RolePermissionQueryDslRepository (1개) + RolePermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>exists() - 관계 존재 여부 확인
 *   <li>findByRoleIdAndPermissionId() - 관계 조회
 *   <li>findAllByRoleId() - 역할의 권한 목록 조회
 *   <li>findAllByPermissionId() - 권한이 부여된 역할 목록 조회
 *   <li>existsByPermissionId() - 권한 사용 여부 확인
 *   <li>findAllBySearchCriteria() - 조건 검색
 *   <li>countBySearchCriteria() - 조건 검색 개수
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Domain 반환 (Mapper로 변환)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionQueryAdapter implements RolePermissionQueryPort {

    private final RolePermissionQueryDslRepository repository;
    private final RolePermissionJpaEntityMapper mapper;

    public RolePermissionQueryAdapter(
            RolePermissionQueryDslRepository repository, RolePermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 역할-권한 관계 존재 여부 확인
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 존재 여부
     */
    @Override
    public boolean exists(RoleId roleId, PermissionId permissionId) {
        return repository.exists(roleId.value(), permissionId.value());
    }

    /**
     * 역할-권한 관계 조회
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 역할-권한 관계 (Optional)
     */
    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(
            RoleId roleId, PermissionId permissionId) {
        return repository
                .findByRoleIdAndPermissionId(roleId.value(), permissionId.value())
                .map(mapper::toDomain);
    }

    /**
     * 역할의 권한 목록 조회
     *
     * @param roleId 역할 ID
     * @return 역할-권한 관계 목록
     */
    @Override
    public List<RolePermission> findAllByRoleId(RoleId roleId) {
        return repository.findAllByRoleId(roleId.value()).stream().map(mapper::toDomain).toList();
    }

    /**
     * 권한이 부여된 역할 목록 조회
     *
     * @param permissionId 권한 ID
     * @return 역할-권한 관계 목록
     */
    @Override
    public List<RolePermission> findAllByPermissionId(PermissionId permissionId) {
        return repository.findAllByPermissionId(permissionId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * 권한 사용 여부 확인 (Permission 삭제 검증용)
     *
     * @param permissionId 권한 ID
     * @return 사용 중 여부
     */
    @Override
    public boolean existsByPermissionId(PermissionId permissionId) {
        return repository.existsByPermissionId(permissionId.value());
    }

    /**
     * 조건에 맞는 역할-권한 관계 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return 역할-권한 관계 목록
     */
    @Override
    public List<RolePermission> findAllBySearchCriteria(RolePermissionSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 역할-권한 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    @Override
    public long countBySearchCriteria(RolePermissionSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    /**
     * 역할에 이미 부여된 권한 ID 목록 조회 (요청된 permissionIds 중에서)
     *
     * @param roleId 역할 ID
     * @param permissionIds 확인할 권한 ID 목록
     * @return 이미 부여된 권한 ID 목록
     */
    @Override
    public List<PermissionId> findGrantedPermissionIds(
            RoleId roleId, List<PermissionId> permissionIds) {
        List<Long> permissionIdValues = permissionIds.stream().map(PermissionId::value).toList();
        return repository.findGrantedPermissionIds(roleId.value(), permissionIdValues).stream()
                .map(PermissionId::of)
                .toList();
    }

    /**
     * 여러 역할의 권한 목록 조회 (IN절)
     *
     * @param roleIds 역할 ID 목록
     * @return 역할-권한 관계 목록
     */
    @Override
    public List<RolePermission> findAllByRoleIds(List<RoleId> roleIds) {
        List<Long> roleIdValues = roleIds.stream().map(RoleId::value).toList();
        return repository.findAllByRoleIds(roleIdValues).stream().map(mapper::toDomain).toList();
    }
}
