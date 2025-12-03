package com.ryuqq.authhub.application.user.port.in;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;

/**
 * CreateUserUseCase - 사용자 생성 UseCase 인터페이스
 *
 * <p>사용자 생성 비즈니스 로직을 정의하는 인터페이스입니다.
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
public interface CreateUserUseCase {

    /**
     * 사용자 생성
     *
     * @param command 사용자 생성 요청 데이터
     * @return 생성된 사용자 정보 (ID, 생성 시간)
     * @throws NullPointerException command가 null인 경우
     * @throws IllegalArgumentException Tenant 또는 Organization이 존재하지 않는 경우
     * @throws IllegalStateException Tenant 또는 Organization이 비활성 상태인 경우
     */
    CreateUserResponse execute(CreateUserCommand command);
}
