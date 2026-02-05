package com.ryuqq.authhub.application.permission.assembler;

import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResult;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionAssembler - Domain → Result 변환 (Global Only)
 *
 * <p>Domain Aggregate를 Result DTO로 변환합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 변환 로직이 제거되었습니다
 * </ul>
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
public class PermissionAssembler {

    /**
     * Domain → Result 변환 (단건)
     *
     * @param permission Permission Domain
     * @return PermissionResult DTO
     */
    public PermissionResult toResult(Permission permission) {
        return new PermissionResult(
                permission.permissionIdValue(),
                permission.serviceIdValue(),
                permission.permissionKeyValue(),
                permission.resourceValue(),
                permission.actionValue(),
                permission.descriptionValue(),
                permission.typeValue(),
                permission.createdAt(),
                permission.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param permissions Permission Domain 목록
     * @return PermissionResult DTO 목록
     */
    public List<PermissionResult> toResultList(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → PermissionPageResult 변환
     *
     * @param permissions Permission Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return PermissionPageResult
     */
    public PermissionPageResult toPageResult(
            List<Permission> permissions, int page, int size, long totalElements) {
        List<PermissionResult> content = toResultList(permissions);
        return PermissionPageResult.of(content, page, size, totalElements);
    }
}
