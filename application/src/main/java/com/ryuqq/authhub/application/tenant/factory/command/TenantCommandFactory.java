package com.ryuqq.authhub.application.tenant.factory.command;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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

    /**
     * String → TenantName 변환
     *
     * <p>Validator에서 사용할 TenantName VO를 생성합니다.
     *
     * @param name 테넌트 이름 문자열
     * @return TenantName VO
     */
    public TenantName toName(String name) {
        return TenantName.of(name);
    }

    /**
     * 테넌트 이름 변경 적용
     *
     * @param tenant 이름을 변경할 테넌트
     * @param newName 새로운 테넌트 이름
     * @return 이름이 변경된 새로운 Tenant 인스턴스
     */
    public Tenant applyNameChange(Tenant tenant, TenantName newName) {
        return tenant.changeName(newName, clock);
    }

    /**
     * 테넌트 상태 변경 적용
     *
     * <p>대상 상태에 따라 적절한 도메인 메서드를 호출합니다. DELETED 상태 변경은 DeleteTenantUseCase를 사용해야 합니다.
     *
     * @param tenant 상태를 변경할 테넌트
     * @param targetStatus 목표 상태 문자열 (ACTIVE, INACTIVE)
     * @return 상태가 변경된 새로운 Tenant 인스턴스
     * @throws IllegalArgumentException DELETED 상태로 변경 시도 시
     */
    public Tenant applyStatusChange(Tenant tenant, String targetStatus) {
        TenantStatus status = TenantStatus.valueOf(targetStatus);
        return switch (status) {
            case ACTIVE -> tenant.activate(clock);
            case INACTIVE -> tenant.deactivate(clock);
            case DELETED ->
                    throw new IllegalArgumentException(
                            "DELETED 상태로의 변경은 DeleteTenantUseCase를 사용하세요");
        };
    }
}
