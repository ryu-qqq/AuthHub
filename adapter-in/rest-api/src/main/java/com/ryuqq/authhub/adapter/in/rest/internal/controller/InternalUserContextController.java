package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.USERS;
import static com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints.USER_CONTEXT;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.UserContextApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalUserContextApiMapper;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalUserContextController - 사용자 컨텍스트 Internal API Controller
 *
 * <p>내부 서비스가 사용자의 전체 컨텍스트(테넌트, 조직, 역할, 권한 등)를 조회합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>이 API는 서비스 토큰 인증으로 보호됩니다
 *   <li>외부 접근이 차단된 내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(USERS)
@Tag(name = "Internal - User Context", description = "내부 서비스용 사용자 컨텍스트 Internal API")
public class InternalUserContextController {

    private final GetMyContextUseCase getMyContextUseCase;
    private final InternalUserContextApiMapper mapper;

    public InternalUserContextController(
            GetMyContextUseCase getMyContextUseCase, InternalUserContextApiMapper mapper) {
        this.getMyContextUseCase = getMyContextUseCase;
        this.mapper = mapper;
    }

    /**
     * 사용자 컨텍스트 조회
     *
     * <p>내부 서비스가 사용자의 전체 컨텍스트 정보를 조회하기 위해 호출합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 컨텍스트 정보
     */
    @GetMapping(USER_CONTEXT)
    @Operation(summary = "사용자 컨텍스트 조회", description = "내부 서비스가 사용자의 전체 컨텍스트 정보를 조회합니다.")
    public ApiResponse<UserContextApiResponse> getUserContext(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        MyContextResponse result = getMyContextUseCase.execute(userId);
        UserContextApiResponse response = mapper.toApiResponse(result);
        return ApiResponse.ofSuccess(response);
    }
}
