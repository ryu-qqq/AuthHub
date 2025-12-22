package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleUserMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleUserQueryDslRepository;
import com.ryuqq.authhub.application.role.dto.query.SearchRoleUsersQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import com.ryuqq.authhub.application.role.port.out.query.RoleUserQueryPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RoleUserQueryAdapter - 역할-사용자 관계 Query Adapter
 *
 * <p>RoleUserQueryPort 구현체로서 역할별 사용자 조회를 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>RoleUserQueryDslRepository (1개) + RoleUserMapper (1개)
 *   <li>Persistence DTO → Application DTO 변환
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Application DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleUserQueryAdapter implements RoleUserQueryPort {

    private final RoleUserQueryDslRepository repository;
    private final RoleUserMapper mapper;

    public RoleUserQueryAdapter(RoleUserQueryDslRepository repository, RoleUserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<RoleUserResponse> searchUsersByRoleId(SearchRoleUsersQuery query) {
        return repository
                .searchUsersByRoleId(query.roleId().value(), query.offset(), query.size())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public long countUsersByRoleId(SearchRoleUsersQuery query) {
        return repository.countUsersByRoleId(query.roleId().value());
    }
}
