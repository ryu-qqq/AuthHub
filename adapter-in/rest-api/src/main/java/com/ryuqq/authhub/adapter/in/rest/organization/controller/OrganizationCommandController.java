package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.OrganizationApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationIdApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationCommandApiMapper;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationNameUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
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
 * OrganizationCommandController - Organization 생성/수정 API
 *
 * <p>조직 생성/수정 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-003: @Valid 필수 적용.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "Organization", description = "조직 관리 API")
@RestController
@RequestMapping(OrganizationApiEndpoints.ORGANIZATIONS)
public class OrganizationCommandController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final UpdateOrganizationNameUseCase updateOrganizationNameUseCase;
    private final UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase;
    private final OrganizationCommandApiMapper mapper;

    /**
     * OrganizationCommandController 생성자
     *
     * @param createOrganizationUseCase Organization 생성 UseCase
     * @param updateOrganizationNameUseCase Organization 이름 수정 UseCase
     * @param updateOrganizationStatusUseCase Organization 상태 수정 UseCase
     * @param mapper API 매퍼
     */
    public OrganizationCommandController(
            CreateOrganizationUseCase createOrganizationUseCase,
            UpdateOrganizationNameUseCase updateOrganizationNameUseCase,
            UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase,
            OrganizationCommandApiMapper mapper) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.updateOrganizationNameUseCase = updateOrganizationNameUseCase;
        this.updateOrganizationStatusUseCase = updateOrganizationStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * Organization 생성 API
     *
     * <p>새로운 조직을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Organization ID
     */
    @Operation(summary = "조직 생성", description = "새로운 조직을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 조직 이름")
    })
    @PreAuthorize("@access.superAdmin() or @access.hasPermission('organization:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationIdApiResponse>> create(
            @Valid @RequestBody CreateOrganizationApiRequest request) {

        CreateOrganizationCommand command = mapper.toCommand(request);
        String organizationId = createOrganizationUseCase.execute(command);

        OrganizationIdApiResponse apiResponse = OrganizationIdApiResponse.of(organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Organization 이름 수정 API
     *
     * <p>기존 조직의 이름을 수정합니다.
     *
     * @param organizationId Organization ID
     * @param request 수정 요청 DTO
     * @return 수정된 Organization ID
     */
    @Operation(summary = "조직 이름 수정", description = "기존 조직의 이름을 수정합니다.")
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
    @PutMapping(OrganizationApiEndpoints.NAME)
    public ResponseEntity<ApiResponse<OrganizationIdApiResponse>> updateName(
            @Parameter(description = "Organization ID", required = true)
                    @PathVariable(OrganizationApiEndpoints.PATH_ORGANIZATION_ID)
                    String organizationId,
            @Valid @RequestBody UpdateOrganizationNameApiRequest request) {

        UpdateOrganizationNameCommand command = mapper.toCommand(organizationId, request);
        updateOrganizationNameUseCase.execute(command);

        OrganizationIdApiResponse apiResponse = OrganizationIdApiResponse.of(organizationId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Organization 상태 수정 API
     *
     * <p>기존 조직의 상태를 수정합니다.
     *
     * @param organizationId Organization ID
     * @param request 수정 요청 DTO
     * @return 수정된 Organization ID
     */
    @Operation(summary = "조직 상태 수정", description = "기존 조직의 상태를 수정합니다 (ACTIVE, INACTIVE, DELETED).")
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
    @PatchMapping(OrganizationApiEndpoints.STATUS)
    public ResponseEntity<ApiResponse<OrganizationIdApiResponse>> updateStatus(
            @Parameter(description = "Organization ID", required = true)
                    @PathVariable(OrganizationApiEndpoints.PATH_ORGANIZATION_ID)
                    String organizationId,
            @Valid @RequestBody UpdateOrganizationStatusApiRequest request) {

        UpdateOrganizationStatusCommand command = mapper.toCommand(organizationId, request);
        updateOrganizationStatusUseCase.execute(command);

        OrganizationIdApiResponse apiResponse = OrganizationIdApiResponse.of(organizationId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
