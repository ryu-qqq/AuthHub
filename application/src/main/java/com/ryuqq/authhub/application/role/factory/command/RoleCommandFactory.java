package com.ryuqq.authhub.application.role.factory.command;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * RoleCommandFactory - Command → Domain 변환
 *
 * <p>Command DTO를 Domain Aggregate로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code create*()} 메서드 네이밍
 *   <li>순수 변환만 수행 (비즈니스 로직 금지)
 *   <li>Port 호출 금지 (조회 금지)
 *   <li>{@code @Transactional} 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleCommandFactory {

    private final Clock clock;
    private final UuidHolder uuidHolder;

    public RoleCommandFactory(Clock clock, UuidHolder uuidHolder) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
    }

    /**
     * CreateRoleCommand → Role 변환
     *
     * <p>UUIDv7 기반 식별자를 생성하여 Role을 생성합니다.
     *
     * @param command 역할 생성 Command
     * @return 새로운 Role 인스턴스 (UUIDv7 ID 할당)
     */
    public Role create(CreateRoleCommand command) {
        RoleId roleId = RoleId.of(uuidHolder.random());
        RoleName name = RoleName.of(command.name());
        RoleDescription description =
                command.description() != null ? RoleDescription.of(command.description()) : null;
        RoleScope scope = parseScope(command.scope());
        TenantId tenantId = command.tenantId() != null ? TenantId.of(command.tenantId()) : null;

        if (command.isSystem()) {
            // 시스템 역할은 GLOBAL 범위만 허용
            return Role.createSystemGlobal(roleId, name, description, clock);
        }

        // 커스텀 역할은 범위에 따라 생성
        return switch (scope) {
            case GLOBAL -> throw new IllegalArgumentException("커스텀 역할은 GLOBAL 범위로 생성할 수 없습니다");
            case TENANT -> Role.createCustomTenant(roleId, tenantId, name, description, clock);
            case ORGANIZATION ->
                    Role.createCustomOrganization(roleId, tenantId, name, description, clock);
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
