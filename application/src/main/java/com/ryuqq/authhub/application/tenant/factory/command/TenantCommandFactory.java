package com.ryuqq.authhub.application.tenant.factory.command;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.Clock;
import org.springframework.stereotype.Component;

/**
 * TenantCommandFactory - Command → Domain 변환
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
public class TenantCommandFactory {

    private final Clock clock;
    private final UuidHolder uuidHolder;

    public TenantCommandFactory(Clock clock, UuidHolder uuidHolder) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
    }

    /**
     * CreateTenantCommand → Tenant 변환
     *
     * <p>UUIDv7 기반 식별자를 생성하여 Tenant를 생성합니다.
     *
     * @param command 테넌트 생성 Command
     * @return 새로운 Tenant 인스턴스 (UUIDv7 ID 할당)
     */
    public Tenant create(CreateTenantCommand command) {
        TenantId tenantId = TenantId.forNew(uuidHolder.random());
        return Tenant.create(tenantId, TenantName.of(command.name()), clock);
    }
}
