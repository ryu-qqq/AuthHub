package com.ryuqq.authhub.application.role.factory.command;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * RoleCommandFactory - 역할 생성 Factory
 *
 * <p>CreateRoleCommand → Role 변환 전담
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Factory suffix 네이밍
 *   <li>Command → Domain Aggregate 변환만 담당
 *   <li>Port/Repository 주입 금지
 *   <li>Clock 주입 허용
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleCommandFactory {

    private final Clock clock;

    public RoleCommandFactory(Clock clock) {
        this.clock = clock;
    }

    /**
     * CreateRoleCommand → Role 변환
     *
     * @param command 역할 생성 Command
     * @return 새로운 Role 인스턴스
     */
    public Role create(CreateRoleCommand command) {
        RoleName name = RoleName.of(command.name());
        RoleDescription description =
                command.description() != null ? RoleDescription.of(command.description()) : null;
        RoleScope scope = parseScope(command.scope());
        TenantId tenantId = command.tenantId() != null ? TenantId.of(command.tenantId()) : null;

        if (command.isSystem()) {
            // 시스템 역할은 GLOBAL 범위만 허용
            return Role.createSystemGlobal(name, description, clock);
        }

        // 커스텀 역할은 범위에 따라 생성
        return switch (scope) {
            case GLOBAL -> throw new IllegalArgumentException("커스텀 역할은 GLOBAL 범위로 생성할 수 없습니다");
            case TENANT -> Role.createCustomTenant(tenantId, name, description, clock);
            case ORGANIZATION -> Role.createCustomOrganization(tenantId, name, description, clock);
        };
    }

    private RoleScope parseScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return RoleScope.ORGANIZATION; // 기본값
        }
        try {
            return RoleScope.valueOf(scope.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 RoleScope: " + scope);
        }
    }
}
