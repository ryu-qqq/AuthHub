package com.ryuqq.authhub.application.tenant.assembler;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * TenantCommandAssembler - Tenant Command DTO → Domain 변환 담당
 *
 * <p>Command DTO를 도메인 객체로 변환합니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Command → Domain 변환 로직만 포함
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)
 *   <li>null-safe 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantCommandAssembler {

    private final ClockHolder clockHolder;

    public TenantCommandAssembler(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    /**
     * CreateTenantCommand를 Tenant Domain으로 변환
     *
     * @param command 생성 명령 DTO
     * @return 새로운 Tenant 도메인 객체
     */
    public Tenant toTenant(CreateTenantCommand command) {
        Objects.requireNonNull(command, "CreateTenantCommand는 null일 수 없습니다");

        return Tenant.forNew(TenantName.of(command.name()), clockHolder.clock());
    }

    /**
     * Tenant Domain을 CreateTenantResponse로 변환
     *
     * @param tenantId Tenant Id 도메인 객체
     * @return CreateTenantResponse DTO
     * @throws NullPointerException tenant가 null인 경우
     */
    public CreateTenantResponse toCreateResponse(TenantId tenantId) {
        return new CreateTenantResponse(tenantId.value());
    }
}
