package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.user.dto.response.UserResult;

/**
 * GetUserUseCase - 사용자 단건 조회 UseCase (Port-In)
 *
 * <p>사용자 단건 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Get{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>ID 파라미터, Result DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserUseCase {

    /**
     * 사용자 단건 조회 실행
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 결과 DTO
     */
    UserResult execute(String userId);
}
