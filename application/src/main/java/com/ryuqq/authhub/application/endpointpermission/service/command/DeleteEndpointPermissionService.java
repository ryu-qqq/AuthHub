package com.ryuqq.authhub.application.endpointpermission.service.command;

import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.DeleteEndpointPermissionUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeleteEndpointPermissionService - 엔드포인트 권한 삭제 Service
 *
 * <p>DeleteEndpointPermissionUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager 책임)
 *   <li>Manager 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class DeleteEndpointPermissionService implements DeleteEndpointPermissionUseCase {

    private final EndpointPermissionTransactionManager transactionManager;
    private final EndpointPermissionReadManager readManager;
    private final Clock clock;

    public DeleteEndpointPermissionService(
            EndpointPermissionTransactionManager transactionManager,
            EndpointPermissionReadManager readManager,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteEndpointPermissionCommand command) {
        // 1. 기존 엔드포인트 권한 조회
        EndpointPermissionId id = EndpointPermissionId.of(command.endpointPermissionId());
        EndpointPermission endpointPermission =
                readManager
                        .findById(id)
                        .orElseThrow(() -> new EndpointPermissionNotFoundException(id));

        // 2. 소프트 삭제
        EndpointPermission deleted = endpointPermission.delete(clock);

        // 3. Manager: 영속화
        transactionManager.persist(deleted);
    }
}
