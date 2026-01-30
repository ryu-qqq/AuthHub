package com.ryuqq.authhub.application.permissionendpoint.assembler;

import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointResult;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointAssembler - Domain → Result 변환
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
public class PermissionEndpointAssembler {

    /**
     * Domain → Result 변환 (단건)
     *
     * @param permissionEndpoint PermissionEndpoint Domain
     * @return PermissionEndpointResult DTO
     */
    public PermissionEndpointResult toResult(PermissionEndpoint permissionEndpoint) {
        return new PermissionEndpointResult(
                permissionEndpoint.permissionEndpointIdValue(),
                permissionEndpoint.permissionIdValue(),
                permissionEndpoint.urlPatternValue(),
                permissionEndpoint.httpMethodValue(),
                permissionEndpoint.descriptionValue(),
                permissionEndpoint.createdAt(),
                permissionEndpoint.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param permissionEndpoints PermissionEndpoint Domain 목록
     * @return PermissionEndpointResult DTO 목록
     */
    public List<PermissionEndpointResult> toResultList(
            List<PermissionEndpoint> permissionEndpoints) {
        if (permissionEndpoints == null || permissionEndpoints.isEmpty()) {
            return List.of();
        }
        return permissionEndpoints.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → PermissionEndpointPageResult 변환
     *
     * @param permissionEndpoints PermissionEndpoint Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return PermissionEndpointPageResult
     */
    public PermissionEndpointPageResult toPageResult(
            List<PermissionEndpoint> permissionEndpoints, int page, int size, long totalElements) {
        List<PermissionEndpointResult> content = toResultList(permissionEndpoints);
        return PermissionEndpointPageResult.of(content, page, size, totalElements);
    }

    /**
     * 스펙 목록 → EndpointPermissionSpecListResult 변환
     *
     * <p>Gateway용 엔드포인트-권한 스펙 목록을 변환합니다.
     *
     * @param specs EndpointPermissionSpecResult 목록
     * @return EndpointPermissionSpecListResult
     */
    public EndpointPermissionSpecListResult toSpecListResult(
            List<EndpointPermissionSpecResult> specs) {
        return EndpointPermissionSpecListResult.of(specs);
    }
}
