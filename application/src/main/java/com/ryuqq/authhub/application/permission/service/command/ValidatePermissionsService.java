package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.dto.command.ValidatePermissionsCommand;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.command.ValidatePermissionsUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ValidatePermissionsService - 권한 검증 Service
 *
 * <p>ValidatePermissionsUseCase를 구현합니다.
 *
 * <p>CI/CD 파이프라인에서 서비스의 @PreAuthorize 권한들이 AuthHub에 등록되어 있는지 검증합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager 통해 Port 간접 호출
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class ValidatePermissionsService implements ValidatePermissionsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ValidatePermissionsService.class);

    private final PermissionReadManager readManager;

    public ValidatePermissionsService(PermissionReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public ValidatePermissionsResult execute(ValidatePermissionsCommand command) {
        String serviceName = command.serviceName();
        List<String> requestedKeys =
                command.permissions().stream()
                        .map(ValidatePermissionsCommand.PermissionEntry::key)
                        .toList();

        if (requestedKeys.isEmpty()) {
            log.info(
                    "[PERMISSION-VALIDATE] No permissions to validate for service: {}",
                    serviceName);
            return ValidatePermissionsResult.allValid(serviceName, List.of());
        }

        log.info(
                "[PERMISSION-VALIDATE] Validating {} permissions for service: {}",
                requestedKeys.size(),
                serviceName);

        // 1. 요청된 권한 키들을 PermissionKey VO로 변환
        Set<PermissionKey> permissionKeys =
                requestedKeys.stream().map(PermissionKey::of).collect(Collectors.toSet());

        // 2. DB에서 존재하는 권한 조회
        List<Permission> existingPermissions = readManager.findAllByKeys(permissionKeys);

        // 3. 존재하는 권한 키 Set
        Set<String> existingKeySet =
                existingPermissions.stream()
                        .map(p -> p.getKey().value())
                        .collect(Collectors.toSet());

        // 4. 요청된 권한과 비교하여 분류
        List<String> existing = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String key : requestedKeys) {
            if (existingKeySet.contains(key)) {
                existing.add(key);
            } else {
                missing.add(key);
            }
        }

        // 5. 결과 생성 및 반환
        if (missing.isEmpty()) {
            log.info(
                    "[PERMISSION-VALIDATE] All {} permissions valid for service: {}",
                    existing.size(),
                    serviceName);
            return ValidatePermissionsResult.allValid(serviceName, existing);
        } else {
            log.warn(
                    "[PERMISSION-VALIDATE] Missing {} permissions for service: {}, missing={}",
                    missing.size(),
                    serviceName,
                    missing);
            return ValidatePermissionsResult.withMissing(serviceName, existing, missing);
        }
    }
}
