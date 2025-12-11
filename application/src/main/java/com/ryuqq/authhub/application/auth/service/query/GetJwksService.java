package com.ryuqq.authhub.application.auth.service.query;

import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;
import com.ryuqq.authhub.application.auth.port.in.query.GetJwksUseCase;
import com.ryuqq.authhub.application.auth.port.out.query.JwksPort;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetJwksService - JWKS 조회 서비스
 *
 * <p>Gateway에서 JWT 서명 검증용 공개키 목록을 조회할 때 사용됩니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 필수
 *   <li>UseCase 인터페이스 구현
 *   <li>{@code @Transactional} 금지 (Query Service)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetJwksService implements GetJwksUseCase {

    private final JwksPort jwksPort;

    public GetJwksService(JwksPort jwksPort) {
        this.jwksPort = jwksPort;
    }

    /**
     * JWKS 조회
     *
     * <p>RSA 공개키 목록을 조회하여 JWKS 형식으로 반환합니다.
     *
     * @return 공개키 목록 (JWKS 형식)
     */
    @Override
    public JwksResponse execute() {
        List<JwkResponse> publicKeys = jwksPort.getPublicKeys();
        return JwksResponse.of(publicKeys);
    }
}
