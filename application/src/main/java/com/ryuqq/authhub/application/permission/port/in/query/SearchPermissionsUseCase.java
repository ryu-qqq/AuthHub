package com.ryuqq.authhub.application.permission.port.in.query;

import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;

/**
 * SearchPermissionsUseCase - 권한 목록 검색 UseCase (Port-In)
 *
 * <p>권한 목록 페이징 검색 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>SearchParams 파라미터, PageResult 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchPermissionsUseCase {

    /**
     * 권한 목록 검색 실행
     *
     * @param params 검색 파라미터
     * @return 권한 페이지 결과
     */
    PermissionPageResult execute(PermissionSearchParams params);
}
