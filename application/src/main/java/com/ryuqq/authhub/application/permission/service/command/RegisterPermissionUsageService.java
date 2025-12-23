package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.dto.command.RegisterPermissionUsageCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.application.permission.manager.command.PermissionUsageTransactionManager;
import com.ryuqq.authhub.application.permission.manager.query.PermissionUsageReadManager;
import com.ryuqq.authhub.application.permission.port.in.command.RegisterPermissionUsageUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.CodeLocation;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * RegisterPermissionUsageService - 권한 사용 이력 등록 Service
 *
 * <p>RegisterPermissionUsageUseCase를 구현합니다.
 *
 * <p>n8n에서 승인 후 권한 사용 이력을 등록합니다. 동일 권한+서비스 조합이 이미 존재하면 업데이트 (UPSERT)합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager 책임)
 *   <li>Manager 통해 Port 간접 호출
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RegisterPermissionUsageService implements RegisterPermissionUsageUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterPermissionUsageService.class);

    private final PermissionUsageReadManager readManager;
    private final PermissionUsageTransactionManager transactionManager;
    private final Clock clock;

    public RegisterPermissionUsageService(
            PermissionUsageReadManager readManager,
            PermissionUsageTransactionManager transactionManager,
            Clock clock) {
        this.readManager = readManager;
        this.transactionManager = transactionManager;
        this.clock = clock;
    }

    @Override
    public PermissionUsageResponse execute(RegisterPermissionUsageCommand command) {
        PermissionKey permissionKey = PermissionKey.of(command.permissionKey());
        ServiceName serviceName = ServiceName.of(command.serviceName());
        List<CodeLocation> locations = toCodeLocations(command.locations());

        log.info(
                "[PERMISSION-USAGE] Registering usage for permission={}, service={}",
                command.permissionKey(),
                command.serviceName());

        // UPSERT: 기존 이력이 있으면 업데이트, 없으면 신규 생성
        Optional<PermissionUsage> existingUsage =
                readManager.findByKeyAndService(permissionKey, serviceName);

        PermissionUsage usage;
        if (existingUsage.isPresent()) {
            log.info(
                    "[PERMISSION-USAGE] Updating existing usage for permission={}, service={}",
                    command.permissionKey(),
                    command.serviceName());
            usage = existingUsage.get().updateUsage(locations, clock);
        } else {
            log.info(
                    "[PERMISSION-USAGE] Creating new usage for permission={}, service={}",
                    command.permissionKey(),
                    command.serviceName());
            usage = PermissionUsage.create(permissionKey, serviceName, locations, clock);
        }

        PermissionUsage savedUsage = transactionManager.persist(usage);

        return toResponse(savedUsage);
    }

    private List<CodeLocation> toCodeLocations(List<String> locations) {
        if (locations == null || locations.isEmpty()) {
            return List.of();
        }
        return locations.stream().map(CodeLocation::of).toList();
    }

    private PermissionUsageResponse toResponse(PermissionUsage usage) {
        return new PermissionUsageResponse(
                usage.usageIdValue(),
                usage.permissionKeyValue(),
                usage.serviceNameValue(),
                usage.locationValues(),
                usage.getLastScannedAt(),
                usage.createdAt());
    }
}
