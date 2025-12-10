package com.ryuqq.authhub.application.endpointpermission.factory.command;

import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * EndpointPermissionCommandFactory - Command → Domain 변환
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
public class EndpointPermissionCommandFactory {

    private final Clock clock;

    public EndpointPermissionCommandFactory(Clock clock) {
        this.clock = clock;
    }

    /**
     * CreateEndpointPermissionCommand → EndpointPermission 변환
     *
     * @param command 엔드포인트 권한 생성 Command
     * @return 새로운 EndpointPermission 인스턴스
     */
    public EndpointPermission create(CreateEndpointPermissionCommand command) {
        UUID id = UUID.randomUUID();
        ServiceName serviceName = ServiceName.of(command.serviceName());
        EndpointPath path = EndpointPath.of(command.path());
        HttpMethod method = HttpMethod.fromString(command.method());
        EndpointDescription description = EndpointDescription.of(command.description());
        RequiredPermissions requiredPermissions =
                RequiredPermissions.of(command.requiredPermissions());
        RequiredRoles requiredRoles = RequiredRoles.of(command.requiredRoles());

        if (command.isPublic()) {
            return EndpointPermission.createPublic(
                    id, serviceName, path, method, description, clock);
        } else {
            return EndpointPermission.createProtected(
                    id,
                    serviceName,
                    path,
                    method,
                    description,
                    requiredPermissions,
                    requiredRoles,
                    clock);
        }
    }
}
