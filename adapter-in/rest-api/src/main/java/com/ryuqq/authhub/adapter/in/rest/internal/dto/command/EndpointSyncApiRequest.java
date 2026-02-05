package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * EndpointSyncApiRequest - 엔드포인트 동기화 요청 API DTO
 *
 * <p>다른 서비스에서 AuthHub로 엔드포인트 동기화를 요청할 때 사용합니다.
 *
 * @param serviceName 서비스 이름 (예: "authhub", "marketplace")
 * @param endpoints 엔드포인트 정보 목록
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 동기화 요청")
public record EndpointSyncApiRequest(
        @Schema(description = "서비스 이름", example = "marketplace")
                @NotBlank(message = "서비스 이름은 필수입니다")
                String serviceName,
        @Schema(
                        description = "서비스 코드 (Role-Permission 자동 매핑용, optional)",
                        example = "SVC_MARKETPLACE")
                String serviceCode,
        @Schema(description = "엔드포인트 목록") @NotEmpty(message = "엔드포인트 목록은 비어있을 수 없습니다") @Valid
                List<EndpointInfoApiRequest> endpoints) {

    /**
     * EndpointInfoApiRequest - 개별 엔드포인트 정보
     *
     * @param httpMethod HTTP 메서드
     * @param pathPattern URL 패턴
     * @param permissionKey 권한 키
     * @param description 설명
     */
    @Schema(description = "엔드포인트 정보")
    public record EndpointInfoApiRequest(
            @Schema(description = "HTTP 메서드", example = "POST")
                    @NotBlank(message = "HTTP 메서드는 필수입니다")
                    String httpMethod,
            @Schema(description = "URL 패턴", example = "/api/v1/products")
                    @NotBlank(message = "URL 패턴은 필수입니다")
                    String pathPattern,
            @Schema(description = "권한 키", example = "product:create")
                    @NotBlank(message = "권한 키는 필수입니다")
                    String permissionKey,
            @Schema(description = "설명", example = "상품 생성") String description) {}
}
