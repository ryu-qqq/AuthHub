package com.ryuqq.authhub.application.token.port.out.client;

import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import java.util.List;

/**
 * PublicKeyClient - 공개키 조회 Client
 *
 * <p>RSA 공개키 정보를 JWK 형식으로 조회하는 아웃바운드 클라이언트입니다.
 *
 * <p><strong>사용 예:</strong>
 *
 * <ul>
 *   <li>JWKS 엔드포인트 (.well-known/jwks.json) 응답 생성
 *   <li>Gateway에서 JWT 검증용 공개키 제공
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PublicKeyClient {

    /**
     * 전체 공개키 목록 조회
     *
     * @return JWK 형식의 공개키 목록
     */
    List<JwkResponse> getPublicKeys();
}
