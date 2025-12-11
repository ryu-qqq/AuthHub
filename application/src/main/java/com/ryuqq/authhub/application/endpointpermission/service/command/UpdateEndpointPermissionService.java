package com.ryuqq.authhub.application.endpointpermission.service.command;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.UpdateEndpointPermissionUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * UpdateEndpointPermissionService - 엔드포인트 권한 수정 Service
 *
 * <p>UpdateEndpointPermissionUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateEndpointPermissionService implements UpdateEndpointPermissionUseCase {

    private final EndpointPermissionTransactionManager transactionManager;
    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;
    private final Clock clock;

    public UpdateEndpointPermissionService(
            EndpointPermissionTransactionManager transactionManager,
            EndpointPermissionReadManager readManager,
            EndpointPermissionAssembler assembler,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public EndpointPermissionResponse execute(UpdateEndpointPermissionCommand command) {
        // 1. 기존 엔드포인트 권한 조회
        EndpointPermissionId id = EndpointPermissionId.of(command.endpointPermissionId());
        EndpointPermission endpointPermission =
                readManager
                        .findById(id)
                        .orElseThrow(() -> new EndpointPermissionNotFoundException(id));

        // 2. 변경 사항 적용
        EndpointPermission updated = applyUpdates(endpointPermission, command);

        // 3. Manager: 영속화
        EndpointPermission saved = transactionManager.persist(updated);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }

    private EndpointPermission applyUpdates(
            EndpointPermission endpointPermission, UpdateEndpointPermissionCommand command) {

        EndpointPermission updated = endpointPermission;

        // 설명 변경
        if (command.description() != null) {
            updated =
                    updated.changeDescription(EndpointDescription.of(command.description()), clock);
        }

        // 공개 여부 변경
        if (command.isPublic() != null) {
            if (command.isPublic() && !updated.isPublic()) {
                updated = updated.makePublic(clock);
            } else if (!command.isPublic() && updated.isPublic()) {
                RequiredPermissions permissions =
                        command.requiredPermissions() != null
                                ? RequiredPermissions.of(command.requiredPermissions())
                                : RequiredPermissions.empty();
                RequiredRoles roles =
                        command.requiredRoles() != null
                                ? RequiredRoles.of(command.requiredRoles())
                                : RequiredRoles.empty();
                updated = updated.makeProtected(permissions, roles, clock);
            }
        }

        // 필요 권한 변경 (protected인 경우만)
        if (!updated.isPublic() && command.requiredPermissions() != null) {
            updated =
                    updated.changeRequiredPermissions(
                            RequiredPermissions.of(command.requiredPermissions()), clock);
        }

        // 필요 역할 변경 (protected인 경우만)
        if (!updated.isPublic() && command.requiredRoles() != null) {
            updated = updated.changeRequiredRoles(RequiredRoles.of(command.requiredRoles()), clock);
        }

        return updated;
    }
}
