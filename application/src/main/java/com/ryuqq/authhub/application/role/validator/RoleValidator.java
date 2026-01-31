package com.ryuqq.authhub.application.role.validator;

import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RoleValidator - 역할 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleValidator {

    private final RoleReadManager readManager;

    public RoleValidator(RoleReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * Role 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Role ID (VO)
     * @return Role 조회된 도메인 객체
     * @throws com.ryuqq.authhub.domain.role.exception.RoleNotFoundException 존재하지 않는 경우
     */
    public Role findExistingOrThrow(RoleId id) {
        return readManager.findById(id);
    }

    /**
     * 역할 이름 중복 검증 (테넌트 범위 내)
     *
     * <p>tenantId가 null이면 Global 역할 내에서 중복 검증.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 검증할 역할 이름
     * @throws DuplicateRoleNameException 중복 시
     */
    public void validateNameNotDuplicated(TenantId tenantId, RoleName name) {
        if (readManager.existsByTenantIdAndName(tenantId, name)) {
            throw new DuplicateRoleNameException(name);
        }
    }

    /**
     * Role 다건 존재 검증 (IN절 일괄 조회)
     *
     * <p>요청된 모든 Role ID가 존재하는지 한 번의 쿼리로 검증합니다. 존재하지 않는 ID가 있으면 첫 번째 누락된 ID에 대해 예외를 발생시킵니다.
     *
     * @param ids Role ID 목록
     * @return 조회된 Role 목록 (검증 통과)
     * @throws RoleNotFoundException 존재하지 않는 ID가 있는 경우
     */
    public List<Role> validateAllExist(List<RoleId> ids) {
        List<Role> roles = readManager.findAllByIds(ids);

        // 조회된 Role ID Set
        Set<Long> foundIds =
                roles.stream().map(r -> r.getRoleId().value()).collect(Collectors.toSet());

        // 요청한 ID 중 조회되지 않은 ID 찾기
        ids.stream()
                .filter(id -> !foundIds.contains(id.value()))
                .findFirst()
                .ifPresent(
                        missingId -> {
                            throw new RoleNotFoundException(missingId);
                        });

        return roles;
    }
}
