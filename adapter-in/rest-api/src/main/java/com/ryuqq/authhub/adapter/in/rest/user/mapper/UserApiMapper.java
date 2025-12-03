package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangeUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;

/**
 * User REST API ↔ Application Layer 변환 Mapper
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.
 *
 * <p><strong>변환 방향:</strong>
 * <ul>
 *   <li>API Request → Command (Controller → Application)</li>
 * </ul>
 *
 * <p><strong>Response 변환:</strong>
 * <ul>
 *   <li>Application Response → API Response 변환은 각 Response DTO의 from() 메서드 사용</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserApiMapper {

    /**
     * CreateUserApiRequest → CreateUserCommand 변환
     *
     * @param request REST API 사용자 생성 요청
     * @return Application Layer 사용자 생성 명령
     */
    public CreateUserCommand toCreateUserCommand(CreateUserApiRequest request) {
        return new CreateUserCommand(
                request.tenantId(),
                request.organizationId(),
                request.identifier(),
                request.password(),
                request.userType(),
                request.name(),
                request.phoneNumber()
        );
    }

    /**
     * UpdateUserApiRequest → UpdateUserCommand 변환
     *
     * @param userId 수정 대상 사용자 ID (Path Variable)
     * @param request REST API 사용자 수정 요청
     * @return Application Layer 사용자 수정 명령
     */
    public UpdateUserCommand toUpdateUserCommand(UUID userId, UpdateUserApiRequest request) {
        return new UpdateUserCommand(
                userId,
                request.name(),
                request.phoneNumber()
        );
    }

    /**
     * ChangePasswordApiRequest → ChangePasswordCommand 변환
     *
     * @param userId 사용자 ID (Path Variable)
     * @param request REST API 비밀번호 변경 요청
     * @return Application Layer 비밀번호 변경 명령
     */
    public ChangePasswordCommand toChangePasswordCommand(UUID userId, ChangePasswordApiRequest request) {
        return new ChangePasswordCommand(
                userId,
                request.currentPassword(),
                request.newPassword(),
                request.verified()
        );
    }

    /**
     * ChangeUserStatusApiRequest → ChangeUserStatusCommand 변환
     *
     * @param userId 사용자 ID (Path Variable)
     * @param request REST API 상태 변경 요청
     * @return Application Layer 상태 변경 명령
     */
    public ChangeUserStatusCommand toChangeUserStatusCommand(UUID userId, ChangeUserStatusApiRequest request) {
        return new ChangeUserStatusCommand(
                userId,
                request.targetStatus(),
                request.reason()
        );
    }
}
