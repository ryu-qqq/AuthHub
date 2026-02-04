package com.ryuqq.authhub.application.service.assembler;

import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.dto.response.ServiceResult;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ServiceAssembler - Domain → Result 변환
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
public class ServiceAssembler {

    /**
     * Domain → Result 변환 (단건)
     *
     * @param service Service Domain
     * @return ServiceResult DTO
     */
    public ServiceResult toResult(Service service) {
        return new ServiceResult(
                service.serviceIdValue(),
                service.serviceCodeValue(),
                service.nameValue(),
                service.descriptionValue(),
                service.statusValue(),
                service.createdAt(),
                service.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param services Service Domain 목록
     * @return ServiceResult DTO 목록
     */
    public List<ServiceResult> toResultList(List<Service> services) {
        if (services == null || services.isEmpty()) {
            return List.of();
        }
        return services.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → ServicePageResult 변환
     *
     * @param services Service Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return ServicePageResult
     */
    public ServicePageResult toPageResult(
            List<Service> services, int page, int size, long totalElements) {
        List<ServiceResult> content = toResultList(services);
        return ServicePageResult.of(content, page, size, totalElements);
    }
}
