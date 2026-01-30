package com.ryuqq.authhub.application.tenant.assembler;

import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantAssembler - Domain → Result 변환
 *
 * <p>Domain Aggregate를 Result DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Result 변환만 (toDomain 금지!)
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
     * Domain → Result 변환 (단건)
     *
     * @param tenant Tenant Domain
     * @return TenantResult DTO
     */
    public TenantResult toResult(Tenant tenant) {
        return new TenantResult(
                tenant.tenantIdValue(),
                tenant.nameValue(),
                tenant.statusValue(),
                tenant.createdAt(),
                tenant.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param tenants Tenant Domain 목록
     * @return TenantResult DTO 목록
     */
    public List<TenantResult> toResultList(List<Tenant> tenants) {
        if (tenants == null || tenants.isEmpty()) {
            return List.of();
        }
        return tenants.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → TenantPageResult 변환
     *
     * @param tenants Tenant Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return TenantPageResult
     */
    public TenantPageResult toPageResult(
            List<Tenant> tenants, int page, int size, long totalElements) {
        List<TenantResult> content = toResultList(tenants);
        return TenantPageResult.of(content, page, size, totalElements);
    }

    /**
     * Domain → TenantConfigResult 변환
     *
     * <p>Gateway가 테넌트 유효성 검증을 위해 필요한 설정 정보를 변환합니다.
     *
     * @param tenant Tenant Domain
     * @return TenantConfigResult DTO
     */
    public TenantConfigResult toConfigResult(Tenant tenant) {
        return new TenantConfigResult(
                tenant.tenantIdValue(),
                tenant.nameValue(),
                tenant.statusValue(),
                tenant.isActive());
    }
}
