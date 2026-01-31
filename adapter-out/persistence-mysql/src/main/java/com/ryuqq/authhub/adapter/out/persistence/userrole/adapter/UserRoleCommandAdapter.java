package com.ryuqq.authhub.adapter.out.persistence.userrole.adapter;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.application.userrole.port.out.command.UserRoleCommandPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserRoleCommandAdapter - 사용자-역할 관계 Command Adapter (CUD 전용)
 *
 * <p>UserRoleCommandPort 구현체로서 사용자-역할 관계 저장/삭제 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>UserRoleJpaRepository (1개) + UserRoleJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 사용자-역할 관계 저장
 *   <li>persistAll() - 사용자-역할 관계 다건 저장
 *   <li>delete() - 사용자-역할 관계 삭제
 *   <li>deleteAllByUserId() - 사용자의 모든 역할 관계 삭제
 *   <li>deleteAll() - 사용자-역할 관계 다건 삭제
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Hard Delete (관계 테이블)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleCommandAdapter implements UserRoleCommandPort {

    private final UserRoleJpaRepository repository;
    private final UserRoleJpaEntityMapper mapper;

    public UserRoleCommandAdapter(
            UserRoleJpaRepository repository, UserRoleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 사용자-역할 관계 저장
     *
     * @param userRole 저장할 사용자-역할 관계
     * @return 저장된 사용자-역할 관계 (ID 포함)
     */
    @Override
    public UserRole persist(UserRole userRole) {
        UserRoleJpaEntity entity = mapper.toEntity(userRole);
        UserRoleJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * 사용자-역할 관계 다건 저장
     *
     * @param userRoles 저장할 사용자-역할 관계 목록
     * @return 저장된 사용자-역할 관계 목록
     */
    @Override
    public List<UserRole> persistAll(List<UserRole> userRoles) {
        List<UserRoleJpaEntity> entities = userRoles.stream().map(mapper::toEntity).toList();
        List<UserRoleJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 사용자-역할 관계 삭제 (Hard Delete)
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    @Override
    public void delete(UserId userId, RoleId roleId) {
        repository.deleteByUserIdAndRoleId(userId.value(), roleId.value());
    }

    /**
     * 사용자의 모든 역할 관계 삭제 (Hard Delete - User 삭제 시 Cascade)
     *
     * @param userId 사용자 ID
     */
    @Override
    public void deleteAllByUserId(UserId userId) {
        repository.deleteAllByUserId(userId.value());
    }

    /**
     * 사용자-역할 관계 다건 삭제 (Hard Delete)
     *
     * @param userId 사용자 ID
     * @param roleIds 삭제할 역할 ID 목록
     */
    @Override
    public void deleteAll(UserId userId, List<RoleId> roleIds) {
        List<Long> roleIdValues = roleIds.stream().map(RoleId::value).toList();
        repository.deleteAllByUserIdAndRoleIdIn(userId.value(), roleIdValues);
    }
}
