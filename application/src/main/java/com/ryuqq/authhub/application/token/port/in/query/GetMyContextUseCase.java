package com.ryuqq.authhub.application.token.port.in.query;

import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;

/**
 * GetMyContextUseCase - 내 컨텍스트 조회 UseCase 인터페이스
 *
 * <p>현재 로그인한 사용자의 테넌트, 조직, 역할, 권한 등 전체 컨텍스트를 조회합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>레거시 서버: SDK를 통해 사용자 정보 조회
 *   <li>프론트엔드: 로그인 후 사용자 컨텍스트 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query UseCase 인터페이스
 *   <li>Response DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetMyContextUseCase {

    /**
     * 내 컨텍스트 조회
     *
     * @param userId 사용자 ID (토큰에서 추출)
     * @return 사용자 전체 컨텍스트
     */
    MyContextResponse execute(String userId);
}
