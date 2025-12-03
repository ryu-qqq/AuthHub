package com.ryuqq.authhub.application.user.port.in;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * UpdateUserUseCase - 사용자 정보 수정 UseCase 인터페이스
 *
 * <p>사용자 프로필 정보(이름, 전화번호) 수정 비즈니스 로직을 정의하는 인터페이스입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>인터페이스로 정의 (구현체와 분리)
 *   <li>Command DTO 입력, Response DTO 출력
 *   <li>Domain 객체 직접 반환 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateUserUseCase {

    /**
     * 사용자 정보 수정
     *
     * <p>이름과 전화번호를 수정합니다. null로 전달된 필드는 기존 값을 유지합니다.
     *
     * <p><strong>Tenant별 phoneNumber 유니크 제약:</strong> 같은 Tenant 내에서 동일한 phoneNumber가 이미 존재하면 예외가
     * 발생합니다.
     *
     * @param command 사용자 수정 요청 데이터
     * @return 수정된 사용자 정보
     * @throws NullPointerException command가 null인 경우
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     * @throws IllegalStateException 같은 Tenant에 동일한 phoneNumber가 이미 존재하는 경우
     */
    UserResponse execute(UpdateUserCommand command);
}
