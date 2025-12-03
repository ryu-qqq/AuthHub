package com.ryuqq.authhub.application.user.port.in;

import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;

/**
 * ChangeUserStatusUseCase - 사용자 상태 변경 UseCase
 *
 * <p>사용자 상태를 변경하는 비즈니스 로직의 진입점입니다. ACTIVE, INACTIVE, SUSPENDED, DELETED 상태 간 전환을 처리합니다.
 *
 * <p><strong>지원 상태 변경:</strong>
 *
 * <ul>
 *   <li>ACTIVE → INACTIVE, SUSPENDED, DELETED
 *   <li>INACTIVE → ACTIVE, SUSPENDED, DELETED
 *   <li>SUSPENDED → ACTIVE, INACTIVE, DELETED
 *   <li>DELETED → 변경 불가 (최종 상태)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ChangeUserStatusUseCase {

    /**
     * 사용자 상태를 변경합니다.
     *
     * @param command 상태 변경 요청 데이터
     * @throws com.ryuqq.authhub.domain.user.exception.UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws com.ryuqq.authhub.domain.user.exception.InvalidUserStateException 상태 전환이 불가능한 경우
     */
    void execute(ChangeUserStatusCommand command);
}
