package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.CreateUserWithRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.CreateUserWithRolesResultApiResponse;
import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;
import org.springframework.stereotype.Component;

/**
 * InternalUserApiMapper - Internal 사용자 API 매퍼
 *
 * <p>REST API 계층과 Application 계층 간의 DTO 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Response -> API Response 변환.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalUserApiMapper {

    /**
     * API 요청 → Application Command 변환
     *
     * @param request API 요청 DTO
     * @return Application Command
     */
    public CreateUserWithRolesCommand toCommand(CreateUserWithRolesApiRequest request) {
        return new CreateUserWithRolesCommand(
                request.organizationId(),
                request.identifier(),
                request.phoneNumber(),
                request.password(),
                request.serviceCode(),
                request.roleNames());
    }

    /**
     * Application Result → API 응답 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public CreateUserWithRolesResultApiResponse toApiResponse(CreateUserWithRolesResult result) {
        return new CreateUserWithRolesResultApiResponse(
                result.userId(), result.assignedRoleCount());
    }
}
