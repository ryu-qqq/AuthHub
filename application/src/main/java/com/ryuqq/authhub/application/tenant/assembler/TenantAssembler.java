package com.ryuqq.authhub.application.tenant.assembler;

import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantAssembler - Domain → Response 변환
 *
 * <p>Domain Aggregate를 Response DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Response 변환만 (toDomain 금지!)
 *   <li>Port/Repository 의존 금지
 *   <li>비즈니스 로직 금지
 *   <li>Getter 체이닝 금지 (Law of Demeter)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantAssembler {

    /**
     * Domain → Response 변환 (단건)
     *
     * @param tenant Tenant Domain
     * @return TenantResponse DTO
     */
    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.tenantIdValue(),
                tenant.nameValue(),
                tenant.statusValue(),
                tenant.createdAt(),
                tenant.updatedAt());
    }

    /**
     * Domain → Response 변환 (목록)
     *
     * @param tenants Tenant Domain 목록
     * @return TenantResponse DTO 목록
     */
    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        if (tenants == null || tenants.isEmpty()) {
            return List.of();
        }
        return tenants.stream().map(this::toResponse).toList();
    }
}
