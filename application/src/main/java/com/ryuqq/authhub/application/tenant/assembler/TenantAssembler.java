package com.ryuqq.authhub.application.tenant.assembler;

import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * TenantAssembler - Tenant Domain ↔ DTO 변환 담당
 *
 * <p>도메인 객체와 응답 DTO 간의 변환을 담당하는 어셈블러입니다.
 *
 * <p><strong>규칙:</strong>
 * <ul>
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)</li>
 *   <li>null-safe 변환</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantAssembler {

    /**
     * Tenant Domain을 TenantResponse로 변환
     *
     * @param tenant Tenant 도메인 객체
     * @return TenantResponse DTO
     * @throws NullPointerException tenant가 null인 경우
     */
    public TenantResponse toResponse(Tenant tenant) {
        Objects.requireNonNull(tenant, "Tenant는 null일 수 없습니다");

        return new TenantResponse(
                tenant.tenantIdValue(),
                tenant.tenantNameValue(),
                tenant.statusValue(),
                tenant.createdAt(),
                tenant.updatedAt()
        );
    }
}
