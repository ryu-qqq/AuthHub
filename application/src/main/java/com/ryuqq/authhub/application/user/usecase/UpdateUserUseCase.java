package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * UpdateUserUseCase - 사용자 정보 수정 UseCase 인터페이스
 *
 * <p>사용자 프로필 정보 수정 비즈니스 로직을 정의하는 인터페이스입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>인터페이스로 정의 (구현체와 분리)</li>
 *   <li>Command DTO 입력, Response DTO 출력</li>
 *   <li>Domain 객체 직접 반환 금지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateUserUseCase {

    /**
     * 사용자 프로필 정보 수정
     *
     * @param command 사용자 수정 요청 데이터
     * @return 수정된 사용자 정보
     * @throws NullPointerException command가 null인 경우
     * @throws IllegalArgumentException User가 존재하지 않거나 userId가 null인 경우
     * @throws IllegalStateException User가 삭제된 상태인 경우
     */
    UserResponse execute(UpdateUserCommand command);
}
