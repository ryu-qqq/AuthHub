package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import org.springframework.stereotype.Component;

/**
 * UserQueryApiMapper - User Query API 변환 매퍼
 *
 * <p>Application Response와 API Response 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Response -> API Response 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserQueryApiMapper {

    /**
     * UserResult -> UserApiResponse 변환
     *
     * @param result Application 응답 DTO
     * @return API 응답 DTO
     */
    public UserApiResponse toApiResponse(UserResult result) {
        return new UserApiResponse(
                result.userId(),
                result.organizationId(),
                result.identifier(),
                result.phoneNumber(),
                result.status(),
                result.createdAt(),
                result.updatedAt());
    }
}
