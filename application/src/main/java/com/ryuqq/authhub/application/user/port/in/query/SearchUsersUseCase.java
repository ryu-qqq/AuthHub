package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;

/**
 * SearchUsersUseCase - 사용자 목록 검색 UseCase (Port-In)
 *
 * <p>사용자 목록 검색 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Bc}sUseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>SearchParams 파라미터, PageResult 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchUsersUseCase {

    /**
     * 사용자 목록 검색 실행
     *
     * @param params 검색 파라미터
     * @return 사용자 페이지 결과
     */
    UserPageResult execute(UserSearchParams params);
}
