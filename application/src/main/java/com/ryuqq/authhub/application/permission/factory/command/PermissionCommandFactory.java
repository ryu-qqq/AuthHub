package com.ryuqq.authhub.application.permission.factory.command;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import java.time.Clock;
import org.springframework.stereotype.Component;

/**
 * PermissionCommandFactory - Command → Domain 변환
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
public class PermissionCommandFactory {

    private final Clock clock;
    private final UuidHolder uuidHolder;

    public PermissionCommandFactory(Clock clock, UuidHolder uuidHolder) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
    }

    /**
     * CreatePermissionCommand → Permission 변환
     *
     * <p>UUIDv7 기반 식별자를 생성하여 Permission을 생성합니다.
     *
     * @param command 권한 생성 Command
     * @return 새로운 Permission 인스턴스 (UUIDv7 ID 할당)
     */
    public Permission create(CreatePermissionCommand command) {
        PermissionId permissionId = PermissionId.forNew(uuidHolder.random());
        Resource resource = Resource.of(command.resource());
        Action action = Action.of(command.action());
        PermissionDescription description =
                command.description() != null
                        ? PermissionDescription.of(command.description())
                        : null;

        if (command.isSystem()) {
            return Permission.createSystem(permissionId, resource, action, description, clock);
        } else {
            return Permission.createCustom(permissionId, resource, action, description, clock);
        }
    }
}
