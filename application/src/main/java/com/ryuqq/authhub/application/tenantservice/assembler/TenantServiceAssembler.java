package com.ryuqq.authhub.application.tenantservice.assembler;

import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantServiceAssembler - Domain -> Result 변환
 *
 * <p>Domain Aggregate를 Result DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain -> Result 변환만 (toDomain 금지!)
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
public class TenantServiceAssembler {

    /**
     * Domain -> Result 변환 (단건)
     *
     * @param tenantService TenantService Domain
     * @return TenantServiceResult DTO
     */
    public TenantServiceResult toResult(TenantService tenantService) {
        return new TenantServiceResult(
                tenantService.tenantServiceIdValue(),
                tenantService.tenantIdValue(),
                tenantService.serviceIdValue(),
                tenantService.statusValue(),
                tenantService.subscribedAt(),
                tenantService.createdAt(),
                tenantService.updatedAt());
    }

    /**
     * Domain -> Result 변환 (목록)
     *
     * @param tenantServices TenantService Domain 목록
     * @return TenantServiceResult DTO 목록
     */
    public List<TenantServiceResult> toResultList(List<TenantService> tenantServices) {
        if (tenantServices == null || tenantServices.isEmpty()) {
            return List.of();
        }
        return tenantServices.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 -> TenantServicePageResult 변환
     *
     * @param tenantServices TenantService Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return TenantServicePageResult
     */
    public TenantServicePageResult toPageResult(
            List<TenantService> tenantServices, int page, int size, long totalElements) {
        List<TenantServiceResult> content = toResultList(tenantServices);
        return TenantServicePageResult.of(content, page, size, totalElements);
    }
}
