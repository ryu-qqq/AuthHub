package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * GetUserUseCase - 사용자 단건 조회 Port-In
 *
 * <p>사용자 단건 조회 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Query DTO 입력, Response DTO 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserUseCase {

    /**
     * 사용자 단건 조회 실행
     *
     * @param query 조회 Query
     * @return 사용자 응답
     */
    UserResponse execute(GetUserQuery query);
}
