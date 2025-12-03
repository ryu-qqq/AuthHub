package com.ryuqq.authhub.application.user.port.in;

import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import java.util.UUID;

/**
 * GetUserUseCase - 사용자 조회 UseCase 인터페이스
 *
 * <p>사용자 정보 조회를 위한 입력 포트입니다.
 *
 * <p><strong>Query UseCase:</strong>
 *
 * <ul>
 *   <li>읽기 전용 트랜잭션 (@Transactional(readOnly = true))
 *   <li>데이터 변경 없음
 *   <li>Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserUseCase {

    /**
     * 사용자 조회 실행
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 응답
     * @throws com.ryuqq.authhub.domain.user.exception.UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    UserResponse execute(UUID userId);
}
