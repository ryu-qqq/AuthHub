package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.ForceChangePasswordCommand;

/**
 * ForceChangePasswordUseCase - 강제 비밀번호 변경 UseCase (Port-In)
 *
 * <p>현재 비밀번호 검증 없이 비밀번호를 변경합니다. Internal API (M2M) 전용입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ForceChangePasswordUseCase {

    /**
     * 강제 비밀번호 변경 실행
     *
     * @param command 강제 비밀번호 변경 Command
     */
    void execute(ForceChangePasswordCommand command);
}
