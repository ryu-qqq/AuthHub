package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.ENDPOINTS;
import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.ENDPOINTS_SYNC;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointSyncResultApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalEndpointSyncApiMapper;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.port.in.command.SyncEndpointsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalEndpointSyncController - Internal 엔드포인트 동기화 Controller
 *
 * <p>다른 서비스에서 AuthHub로 엔드포인트를 동기화하는 Internal API를 제공합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>서비스 토큰 인증으로 보호됩니다
 *   <li>내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(ENDPOINTS)
@Tag(name = "Internal - Endpoint Sync", description = "엔드포인트 동기화 Internal API")
public class InternalEndpointSyncController {

    private final SyncEndpointsUseCase syncEndpointsUseCase;
    private final InternalEndpointSyncApiMapper mapper;

    public InternalEndpointSyncController(
            SyncEndpointsUseCase syncEndpointsUseCase, InternalEndpointSyncApiMapper mapper) {
        this.syncEndpointsUseCase = syncEndpointsUseCase;
        this.mapper = mapper;
    }

    /**
     * 엔드포인트 동기화
     *
     * <p>다른 서비스의 @RequirePermission 어노테이션이 붙은 엔드포인트를 AuthHub에 동기화합니다.
     *
     * @param request 동기화 요청
     * @return 동기화 결과
     */
    @PostMapping(ENDPOINTS_SYNC)
    @Operation(summary = "엔드포인트 동기화", description = "서비스의 엔드포인트를 AuthHub에 동기화합니다")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "동기화 성공")
    })
    public ApiResponse<EndpointSyncResultApiResponse> syncEndpoints(
            @Valid @RequestBody EndpointSyncApiRequest request) {
        SyncEndpointsCommand command = mapper.toCommand(request);
        SyncEndpointsResult result = syncEndpointsUseCase.sync(command);
        return ApiResponse.ofSuccess(mapper.toApiResponse(result));
    }
}
