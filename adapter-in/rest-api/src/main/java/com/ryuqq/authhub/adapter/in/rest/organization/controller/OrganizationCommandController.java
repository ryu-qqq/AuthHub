package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.DeleteOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OrganizationCommandController - 조직 Command API 컨트롤러 (Admin)
 *
 * <p>조직 생성, 수정, 삭제 등 Command 작업을 처리합니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/admin/organizations (admin.connectly.com)
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
@Tag(name = "Organization", description = "조직 관리 API (Admin)")
@RestController
@RequestMapping(ApiPaths.Organizations.BASE)
public class OrganizationCommandController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationUseCase updateOrganizationUseCase;
    private final UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase;
    private final DeleteOrganizationUseCase deleteOrganizationUseCase;
    private final OrganizationApiMapper mapper;

    public OrganizationCommandController(
            CreateOrganizationUseCase createOrganizationUseCase,
            UpdateOrganizationUseCase updateOrganizationUseCase,
            UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase,
            DeleteOrganizationUseCase deleteOrganizationUseCase,
            OrganizationApiMapper mapper) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.updateOrganizationUseCase = updateOrganizationUseCase;
        this.updateOrganizationStatusUseCase = updateOrganizationStatusUseCase;
        this.deleteOrganizationUseCase = deleteOrganizationUseCase;
        this.mapper = mapper;
    }

    /**
     * 조직 생성
     *
     * <p>POST /api/v1/organizations
     *
     * @param request 조직 생성 요청
     * @return 201 Created + 생성된 조직 ID
     */
    @Operation(summary = "조직 생성", description = "새로운 조직을 생성합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "조직 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 조직")
    })
    @PreAuthorize("@access.superAdmin() or @access.hasPermission('organization:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationApiRequest request) {
        OrganizationResponse response =
                createOrganizationUseCase.execute(mapper.toCommand(request));
        CreateOrganizationApiResponse apiResponse = mapper.toCreateResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 조직 수정
     *
     * <p>PUT /api/v1/organizations/{organizationId}
     *
     * @param organizationId 조직 ID
     * @param request 조직 수정 요청
     * @return 200 OK
     */
    @Operation(summary = "조직 수정", description = "조직 정보를 수정합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.organization(#organizationId, 'update')")
    @PutMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<Void>> updateOrganization(
            @Parameter(description = "조직 ID", required = true) @PathVariable String organizationId,
            @Valid @RequestBody UpdateOrganizationApiRequest request) {
        updateOrganizationUseCase.execute(mapper.toCommand(organizationId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 조직 상태 변경
     *
     * <p>PATCH /api/v1/organizations/{organizationId}/status
     *
     * @param organizationId 조직 ID
     * @param request 상태 변경 요청
     * @return 200 OK + 변경된 조직 정보
     */
    @Operation(summary = "조직 상태 변경", description = "조직의 상태를 변경합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.organization(#organizationId, 'update')")
    @PatchMapping("/{organizationId}/status")
    public ResponseEntity<ApiResponse<OrganizationApiResponse>> updateOrganizationStatus(
            @Parameter(description = "조직 ID", required = true) @PathVariable String organizationId,
            @Valid @RequestBody UpdateOrganizationStatusApiRequest request) {
        OrganizationResponse response =
                updateOrganizationStatusUseCase.execute(
                        mapper.toStatusCommand(organizationId, request));
        return ResponseEntity.ok(ApiResponse.ofSuccess(mapper.toApiResponse(response)));
    }

    /**
     * 조직 삭제
     *
     * <p>DELETE /api/v1/organizations/{organizationId}
     *
     * @param organizationId 조직 ID
     * @return 204 No Content
     */
    @Operation(summary = "조직 삭제", description = "조직을 삭제합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "조직을 찾을 수 없음")
    })
    @PreAuthorize("@access.organization(#organizationId, 'delete')")
    @PatchMapping("/{organizationId}/delete")
    public ResponseEntity<Void> deleteOrganization(
            @Parameter(description = "조직 ID", required = true) @PathVariable
                    String organizationId) {
        deleteOrganizationUseCase.execute(mapper.toDeleteCommand(organizationId));
        return ResponseEntity.noContent().build();
    }
}
