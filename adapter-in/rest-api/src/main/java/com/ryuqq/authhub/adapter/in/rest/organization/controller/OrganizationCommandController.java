package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.CreateOrganizationUseCase;

import jakarta.validation.Valid;

/**
 * Organization Command Controller - 조직 상태 변경 API
 *
 * <p>조직 생성 등 Command 작업을 처리합니다.
 *
 * <p><strong>엔드포인트:</strong>
 * <ul>
 *   <li>POST /api/v1/organizations - 조직 생성 (201 Created)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("${api.endpoints.base-v1}${api.endpoints.organization.base}")
@Validated
public class OrganizationCommandController {

    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final OrganizationApiMapper organizationApiMapper;

    public OrganizationCommandController(
            CreateOrganizationUseCase createOrganizationUseCase,
            OrganizationApiMapper organizationApiMapper) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.organizationApiMapper = organizationApiMapper;
    }

    /**
     * 조직 생성 API
     *
     * @param request 조직 생성 요청 DTO
     * @return 201 Created와 생성된 조직 정보 (organizationId)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationApiRequest request) {
        CreateOrganizationCommand command = organizationApiMapper.toCreateOrganizationCommand(request);
        CreateOrganizationResponse useCaseResponse = createOrganizationUseCase.execute(command);
        CreateOrganizationApiResponse apiResponse = CreateOrganizationApiResponse.from(useCaseResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }
}
