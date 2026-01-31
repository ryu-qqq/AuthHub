package com.ryuqq.authhub.adapter.out.persistence.userrole.adapter;

import com.ryuqq.authhub.adapter.out.persistence.userrole.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleQueryDslRepository;
import com.ryuqq.authhub.application.userrole.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserRoleQueryAdapter - 사용자-역할 관계 Query Adapter (조회 전용)
 *
 * <p>UserRoleQueryPort 구현체로서 사용자-역할 관계 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>UserRoleQueryDslRepository (1개) + UserRoleJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>exists() - 사용자-역할 관계 존재 여부 확인
 *   <li>findByUserIdAndRoleId() - 사용자-역할 관계 조회
 *   <li>findAllByUserId() - 사용자의 역할 목록 조회
 *   <li>findAllByRoleId() - 역할이 할당된 사용자 목록 조회
 *   <li>existsByRoleId() - 역할 사용 여부 확인
 *   <li>findAllBySearchCriteria() - SearchCriteria 기반 조건 검색
 *   <li>countBySearchCriteria() - SearchCriteria 기반 개수 조회
 *   <li>findAssignedRoleIds() - 이미 할당된 역할 ID 목록 조회
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
public class UserRoleQueryAdapter implements UserRoleQueryPort {

    private final UserRoleQueryDslRepository repository;
    private final UserRoleJpaEntityMapper mapper;

    public UserRoleQueryAdapter(
            UserRoleQueryDslRepository repository, UserRoleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 사용자-역할 관계 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 존재 여부
     */
    @Override
    public boolean exists(UserId userId, RoleId roleId) {
        return repository.exists(userId.value(), roleId.value());
    }

    /**
     * 사용자-역할 관계 조회
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 사용자-역할 관계 (Optional)
     */
    @Override
    public Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return repository
                .findByUserIdAndRoleId(userId.value(), roleId.value())
                .map(mapper::toDomain);
    }

    /**
     * 사용자의 역할 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자-역할 관계 목록
     */
    @Override
    public List<UserRole> findAllByUserId(UserId userId) {
        return repository.findAllByUserId(userId.value()).stream().map(mapper::toDomain).toList();
    }

    /**
     * 역할이 할당된 사용자 목록 조회
     *
     * @param roleId 역할 ID
     * @return 사용자-역할 관계 목록
     */
    @Override
    public List<UserRole> findAllByRoleId(RoleId roleId) {
        return repository.findAllByRoleId(roleId.value()).stream().map(mapper::toDomain).toList();
    }

    /**
     * 역할이 어떤 사용자에게라도 할당되어 있는지 확인 (Role 삭제 검증용)
     *
     * @param roleId 역할 ID
     * @return 사용 중 여부
     */
    @Override
    public boolean existsByRoleId(RoleId roleId) {
        return repository.existsByRoleId(roleId.value());
    }

    /**
     * 조건에 맞는 사용자-역할 관계 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return 사용자-역할 관계 목록
     */
    @Override
    public List<UserRole> findAllBySearchCriteria(UserRoleSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 사용자-역할 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    @Override
    public long countBySearchCriteria(UserRoleSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    /**
     * 사용자에게 이미 할당된 역할 ID 목록 조회 (요청된 roleIds 중에서)
     *
     * @param userId 사용자 ID
     * @param roleIds 확인할 역할 ID 목록
     * @return 이미 할당된 역할 ID 목록
     */
    @Override
    public List<RoleId> findAssignedRoleIds(UserId userId, List<RoleId> roleIds) {
        List<Long> roleIdValues = roleIds.stream().map(RoleId::value).toList();
        List<Long> assignedIds = repository.findAssignedRoleIds(userId.value(), roleIdValues);
        return assignedIds.stream().map(RoleId::of).toList();
    }
}
