package com.ryuqq.authhub.application.role.assembler;

import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.dto.response.RoleResult;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RoleAssembler - Domain → Result 변환
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
public class RoleAssembler {

    /**
     * Domain → Result 변환 (단건)
     *
     * @param role Role Domain
     * @return RoleResult DTO
     */
    public RoleResult toResult(Role role) {
        return new RoleResult(
                role.roleIdValue(),
                role.tenantIdValue(),
                role.nameValue(),
                role.displayNameValue(),
                role.descriptionValue(),
                role.typeValue(),
                role.createdAt(),
                role.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param roles Role Domain 목록
     * @return RoleResult DTO 목록
     */
    public List<RoleResult> toResultList(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → RolePageResult 변환
     *
     * @param roles Role Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return RolePageResult
     */
    public RolePageResult toPageResult(List<Role> roles, int page, int size, long totalElements) {
        List<RoleResult> content = toResultList(roles);
        return RolePageResult.of(content, page, size, totalElements);
    }
}
