package com.ryuqq.authhub.application.user.port.in;

import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;

/**
 * ChangePasswordUseCase - 비밀번호 변경 UseCase 인터페이스
 *
 * <p>사용자 비밀번호 변경을 위한 입력 포트입니다.
 *
 * <p><strong>비즈니스 규칙:</strong>
 *
 * <ul>
 *   <li>현재 비밀번호 검증 필수
 *   <li>활성 상태(ACTIVE) 사용자만 변경 가능
 *   <li>새 비밀번호는 해싱되어 저장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ChangePasswordUseCase {

    /**
     * 비밀번호 변경 실행
     *
     * @param command 비밀번호 변경 요청 데이터
     * @throws com.ryuqq.authhub.domain.user.exception.UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws com.ryuqq.authhub.domain.user.exception.InvalidPasswordException 현재 비밀번호가 일치하지 않는 경우
     * @throws com.ryuqq.authhub.domain.user.exception.InvalidUserStateException 사용자가 활성 상태가 아닌 경우
     */
    void execute(ChangePasswordCommand command);
}
