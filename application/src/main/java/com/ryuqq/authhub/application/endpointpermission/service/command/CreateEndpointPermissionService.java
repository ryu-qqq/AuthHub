package com.ryuqq.authhub.application.endpointpermission.service.command;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.factory.command.EndpointPermissionCommandFactory;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.CreateEndpointPermissionUseCase;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.DuplicateEndpointPermissionException;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import org.springframework.stereotype.Service;

/**
 * CreateEndpointPermissionService - 엔드포인트 권한 생성 Service
 *
 * <p>CreateEndpointPermissionUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager 책임)
 *   <li>Factory → Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateEndpointPermissionService implements CreateEndpointPermissionUseCase {

    private final EndpointPermissionCommandFactory commandFactory;
    private final EndpointPermissionTransactionManager transactionManager;
    private final EndpointPermissionReadManager readManager;
    private final EndpointPermissionAssembler assembler;

    public CreateEndpointPermissionService(
            EndpointPermissionCommandFactory commandFactory,
            EndpointPermissionTransactionManager transactionManager,
            EndpointPermissionReadManager readManager,
            EndpointPermissionAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public EndpointPermissionResponse execute(CreateEndpointPermissionCommand command) {
        // 1. 중복 검사 (serviceName + path + method)
        ServiceName serviceName = ServiceName.of(command.serviceName());
        EndpointPath path = EndpointPath.of(command.path());
        HttpMethod method = HttpMethod.fromString(command.method());

        if (readManager.existsByServiceNameAndPathAndMethod(serviceName, path, method)) {
            throw new DuplicateEndpointPermissionException(
                    command.serviceName(), command.path(), command.method());
        }

        // 2. Factory: Command → Domain
        EndpointPermission endpointPermission = commandFactory.create(command);

        // 3. Manager: 영속화
        EndpointPermission saved = transactionManager.persist(endpointPermission);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
