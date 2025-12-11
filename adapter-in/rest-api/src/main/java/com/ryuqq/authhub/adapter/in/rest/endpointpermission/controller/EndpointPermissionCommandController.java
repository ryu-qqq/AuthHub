package com.ryuqq.authhub.adapter.in.rest.endpointpermission.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.CreateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.UpdateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.CreateEndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper.EndpointPermissionApiMapper;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.CreateEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.DeleteEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.UpdateEndpointPermissionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EndpointPermissionCommandController - 엔드포인트 권한 Command API 컨트롤러
 *
 * <p>엔드포인트 권한 생성, 수정, 삭제 등 Command 작업을 처리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>{@code @Valid} 필수
 *   <li>UseCase 단일 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "EndpointPermission", description = "엔드포인트 권한 관리 API")
@RestController
@RequestMapping("/api/v1/endpoint-permissions")
public class EndpointPermissionCommandController {

    private final CreateEndpointPermissionUseCase createEndpointPermissionUseCase;
    private final UpdateEndpointPermissionUseCase updateEndpointPermissionUseCase;
    private final DeleteEndpointPermissionUseCase deleteEndpointPermissionUseCase;
    private final EndpointPermissionApiMapper mapper;

    public EndpointPermissionCommandController(
            CreateEndpointPermissionUseCase createEndpointPermissionUseCase,
            UpdateEndpointPermissionUseCase updateEndpointPermissionUseCase,
            DeleteEndpointPermissionUseCase deleteEndpointPermissionUseCase,
            EndpointPermissionApiMapper mapper) {
        this.createEndpointPermissionUseCase = createEndpointPermissionUseCase;
        this.updateEndpointPermissionUseCase = updateEndpointPermissionUseCase;
        this.deleteEndpointPermissionUseCase = deleteEndpointPermissionUseCase;
        this.mapper = mapper;
    }

    /**
     * 엔드포인트 권한 생성
     *
     * <p>POST /api/v1/endpoint-permissions
     *
     * @param request 엔드포인트 권한 생성 요청
     * @return 201 Created + 생성된 엔드포인트 권한 ID
     */
    @Operation(summary = "엔드포인트 권한 생성", description = "새로운 엔드포인트 권한을 생성합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "엔드포인트 권한 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 엔드포인트 권한")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreateEndpointPermissionApiResponse>>
            createEndpointPermission(
                    @Valid @RequestBody CreateEndpointPermissionApiRequest request) {
        EndpointPermissionResponse response =
                createEndpointPermissionUseCase.execute(mapper.toCommand(request));
        CreateEndpointPermissionApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 엔드포인트 권한 수정
     *
     * <p>PUT /api/v1/endpoint-permissions/{endpointPermissionId}
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @param request 엔드포인트 권한 수정 요청
     * @return 200 OK
     */
    @Operation(summary = "엔드포인트 권한 수정", description = "엔드포인트 권한 정보를 수정합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "엔드포인트 권한을 찾을 수 없음")
    })
    @PutMapping("/{endpointPermissionId}")
    public ResponseEntity<ApiResponse<Void>> updateEndpointPermission(
            @Parameter(description = "엔드포인트 권한 ID", required = true) @PathVariable
                    String endpointPermissionId,
            @Valid @RequestBody UpdateEndpointPermissionApiRequest request) {
        updateEndpointPermissionUseCase.execute(mapper.toCommand(endpointPermissionId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 엔드포인트 권한 삭제
     *
     * <p>DELETE /api/v1/endpoint-permissions/{endpointPermissionId}
     *
     * @param endpointPermissionId 엔드포인트 권한 ID
     * @return 204 No Content
     */
    @Operation(summary = "엔드포인트 권한 삭제", description = "엔드포인트 권한을 삭제합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "엔드포인트 권한을 찾을 수 없음")
    })
    @PatchMapping("/{endpointPermissionId}/delete")
    public ResponseEntity<Void> deleteEndpointPermission(
            @Parameter(description = "엔드포인트 권한 ID", required = true) @PathVariable
                    String endpointPermissionId) {
        deleteEndpointPermissionUseCase.execute(mapper.toDeleteCommand(endpointPermissionId));
        return ResponseEntity.noContent().build();
    }
}
