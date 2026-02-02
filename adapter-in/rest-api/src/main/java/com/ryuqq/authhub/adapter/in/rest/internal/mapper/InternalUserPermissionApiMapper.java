package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import org.springframework.stereotype.Component;

/**
 * InternalUserPermissionApiMapper - Internal User Permission API Mapper
 *
 * <p>Application Layer 결과를 API 응답으로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalUserPermissionApiMapper {

    /**
     * Application 결과를 API 응답으로 변환
     *
     * @param result Application Layer 결과
     * @return API 응답 DTO
     */
    public UserPermissionsApiResponse toApiResponse(UserPermissionsResult result) {
        return new UserPermissionsApiResponse(
                result.userId(), result.roles(), result.permissions());
    }
}
