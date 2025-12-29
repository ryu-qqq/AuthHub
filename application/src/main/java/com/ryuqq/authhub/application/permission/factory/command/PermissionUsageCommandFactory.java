package com.ryuqq.authhub.application.permission.factory.command;

import com.ryuqq.authhub.application.permission.dto.command.RegisterPermissionUsageCommand;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.CodeLocation;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionUsageCommandFactory - Command → Domain 변환
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
public class PermissionUsageCommandFactory {

    private final Clock clock;

    public PermissionUsageCommandFactory(Clock clock) {
        this.clock = clock;
    }

    /**
     * RegisterPermissionUsageCommand → PermissionUsage 변환 (신규 생성)
     *
     * @param command 권한 사용 등록 Command
     * @return 새로운 PermissionUsage 인스턴스
     */
    public PermissionUsage create(RegisterPermissionUsageCommand command) {
        PermissionKey permissionKey = PermissionKey.of(command.permissionKey());
        ServiceName serviceName = ServiceName.of(command.serviceName());
        List<CodeLocation> locations = toCodeLocations(command.locations());

        return PermissionUsage.create(permissionKey, serviceName, locations, clock);
    }

    /**
     * 문자열 리스트 → CodeLocation 리스트 변환
     *
     * @param locations 코드 위치 문자열 리스트 (nullable)
     * @return CodeLocation 리스트 (빈 리스트 가능)
     */
    public List<CodeLocation> toCodeLocations(List<String> locations) {
        if (locations == null || locations.isEmpty()) {
            return List.of();
        }
        return locations.stream().map(CodeLocation::of).toList();
    }
}
