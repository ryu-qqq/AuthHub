package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import org.springframework.stereotype.Component;

/**
 * UserCommandApiMapper - User Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandApiMapper {

    /**
     * CreateUserApiRequest -> CreateUserCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateUserCommand toCommand(CreateUserApiRequest request) {
        return new CreateUserCommand(
                request.organizationId(),
                request.identifier(),
                request.phoneNumber(),
                request.password());
    }

    /**
     * UpdateUserApiRequest + PathVariable ID -> UpdateUserCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param userId User ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateUserCommand toCommand(String userId, UpdateUserApiRequest request) {
        return new UpdateUserCommand(userId, request.phoneNumber());
    }

    /**
     * ChangePasswordApiRequest + PathVariable ID -> ChangePasswordCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param userId User ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangePasswordCommand toCommand(String userId, ChangePasswordApiRequest request) {
        return new ChangePasswordCommand(userId, request.currentPassword(), request.newPassword());
    }
}
