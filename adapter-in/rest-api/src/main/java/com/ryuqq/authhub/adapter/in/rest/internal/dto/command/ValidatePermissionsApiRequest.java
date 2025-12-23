package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ValidatePermissionsApiRequest - 권한 검증 API 요청 DTO
 *
 * <p>CI/CD에서 PermissionScanner가 생성한 permissions.json을 전달합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 검증 요청")
public record ValidatePermissionsApiRequest(
        @Schema(description = "서비스명", example = "product-service") @NotBlank String serviceName,
        @Schema(description = "검증할 권한 목록") @NotEmpty @Valid
                List<PermissionEntryApiRequest> permissions) {

    /**
     * 권한 항목 요청
     *
     * @param key 권한 키 (예: product:read)
     * @param locations 해당 권한이 사용된 위치 목록
     */
    @Schema(description = "권한 항목")
    public record PermissionEntryApiRequest(
            @Schema(description = "권한 키", example = "product:read") @NotBlank String key,
            @Schema(description = "사용 위치 목록", example = "[\"ProductController.java:45\"]")
                    List<String> locations) {}
}
