package com.ryuqq.authhub.application.auth.port.out.query;

import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import java.util.List;

/**
 * JwksPort - JWKS 조회 Port
 *
 * <p>RSA 공개키 정보를 조회하는 아웃바운드 포트입니다. Security Adapter에서 RSA 키 파일을 읽어 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query Port 인터페이스
 *   <li>Application DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface JwksPort {

    /**
     * 전체 공개키 목록 조회
     *
     * @return JWK 형식의 공개키 목록
     */
    List<JwkResponse> getPublicKeys();
}
