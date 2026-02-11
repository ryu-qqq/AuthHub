package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.UserContextApiResponse;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InternalUserContextApiMapper - Internal User Context API Mapper
 *
 * <p>Application Layer 결과를 Internal API 응답으로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalUserContextApiMapper {

    /**
     * Application 결과를 API 응답으로 변환
     *
     * @param response Application Layer 결과
     * @return API 응답 DTO
     */
    public UserContextApiResponse toApiResponse(MyContextResponse response) {
        UserContextApiResponse.TenantInfo tenant =
                new UserContextApiResponse.TenantInfo(response.tenantId(), response.tenantName());

        UserContextApiResponse.OrganizationInfo organization =
                new UserContextApiResponse.OrganizationInfo(
                        response.organizationId(), response.organizationName());

        List<UserContextApiResponse.RoleInfo> roles =
                response.roles().stream()
                        .map(role -> new UserContextApiResponse.RoleInfo(role.id(), role.name()))
                        .toList();

        return new UserContextApiResponse(
                response.userId(),
                response.email(),
                response.name(),
                response.phoneNumber(),
                tenant,
                organization,
                roles,
                response.permissions());
    }
}
