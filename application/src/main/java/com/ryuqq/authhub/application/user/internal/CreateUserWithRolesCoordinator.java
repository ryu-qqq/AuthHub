package com.ryuqq.authhub.application.user.internal;

import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.application.userrole.factory.UserRoleCommandFactory;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CreateUserWithRolesCoordinator - 사용자 생성 + 역할 할당 Coordinator
 *
 * <p>Service에서 Factory로 생성된 Bundle을 받아 검증 → UserRole 채움 → Facade 한방 영속화를 조율합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>사용자 식별자/전화번호 중복 검증 (UserValidator)
 *   <li>serviceCode → serviceId 리졸브 (ServiceReadManager)
 *   <li>roleName → Role 조회 (RoleReadManager)
 *   <li>UserRole 도메인 객체 생성 (UserRoleCommandFactory)
 *   <li>Bundle에 UserRole 채움 + Facade 한방 영속화 (UserWithRolesCommandFacade)
 * </ul>
 *
 * <p><strong>Role 조회 로직:</strong>
 *
 * <ul>
 *   <li>serviceCode + roleNames 제공 → SERVICE scope Role (tenantId=null, serviceId=resolved)
 *   <li>roleNames만 제공 (serviceCode 없음) → GLOBAL scope Role (tenantId=null, serviceId=null)
 *   <li>roleNames 없음 → 사용자만 생성 (역할 할당 스킵)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class CreateUserWithRolesCoordinator {

    private final OrganizationValidator organizationValidator;
    private final UserValidator userValidator;
    private final ServiceReadManager serviceReadManager;
    private final RoleReadManager roleReadManager;
    private final UserRoleCommandFactory userRoleCommandFactory;
    private final UserWithRolesCommandFacade commandFacade;

    public CreateUserWithRolesCoordinator(
            OrganizationValidator organizationValidator,
            UserValidator userValidator,
            ServiceReadManager serviceReadManager,
            RoleReadManager roleReadManager,
            UserRoleCommandFactory userRoleCommandFactory,
            UserWithRolesCommandFacade commandFacade) {
        this.organizationValidator = organizationValidator;
        this.userValidator = userValidator;
        this.serviceReadManager = serviceReadManager;
        this.roleReadManager = roleReadManager;
        this.userRoleCommandFactory = userRoleCommandFactory;
        this.commandFacade = commandFacade;
    }

    /**
     * 사용자 생성 + 역할 할당 조율
     *
     * @param bundle Factory에서 생성된 Bundle (User + 빈 UserRole + serviceCode + roleNames)
     * @return 생성 결과 (userId, assignedRoleCount)
     */
    public CreateUserWithRolesResult coordinate(CreateUserWithRolesBundle bundle) {
        User user = bundle.user();

        // 1. 검증
        validateUser(user);

        // 2. UserRole resolve 후 Bundle에 채움
        CreateUserWithRolesBundle enriched = bundle.withUserRoles(resolveUserRoles(user, bundle));

        // 3. Facade 한방 영속화
        String userId = commandFacade.persistAll(enriched);

        return CreateUserWithRolesResult.of(userId, enriched.assignedRoleCount());
    }

    private void validateUser(User user) {
        organizationValidator.findExistingOrThrow(user.getOrganizationId());
        userValidator.validateIdentifierNotDuplicated(
                user.getOrganizationId(), user.getIdentifier());
        userValidator.validatePhoneNumberNotDuplicated(
                user.getOrganizationId(), user.getPhoneNumber());
    }

    private List<UserRole> resolveUserRoles(User user, CreateUserWithRolesBundle bundle) {
        if (bundle.roleNames() == null || bundle.roleNames().isEmpty()) {
            return List.of();
        }

        ServiceId serviceId = resolveServiceId(bundle.serviceCode());

        List<RoleId> roleIds =
                bundle.roleNames().stream()
                        .map(
                                name ->
                                        roleReadManager.findByTenantIdAndServiceIdAndName(
                                                null, serviceId, RoleName.of(name)))
                        .map(Role::getRoleId)
                        .toList();

        return userRoleCommandFactory.createAll(user.getUserId(), roleIds);
    }

    private ServiceId resolveServiceId(String serviceCode) {
        ServiceCode code = ServiceCode.fromNullable(serviceCode);
        if (code == null) {
            return null;
        }
        Service service = serviceReadManager.findByCode(code);
        return service.getServiceId();
    }
}
