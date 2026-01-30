package com.ryuqq.authhub.application.token.service.query;

import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import com.ryuqq.authhub.application.token.dto.response.PublicKeysResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetPublicKeyUseCase;
import com.ryuqq.authhub.application.token.port.out.client.PublicKeyClient;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetPublicKeyService - 공개키 조회 서비스
 *
 * <p>Gateway에서 JWT 서명 검증용 공개키 목록을 조회할 때 사용됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetPublicKeyService implements GetPublicKeyUseCase {

    private final PublicKeyClient publicKeyClient;

    public GetPublicKeyService(PublicKeyClient publicKeyClient) {
        this.publicKeyClient = publicKeyClient;
    }

    /**
     * 공개키 목록 조회
     *
     * <p>RSA 공개키 목록을 조회하여 RFC 7517 JWKS 형식으로 반환합니다.
     *
     * @return 공개키 목록 (JWKS 형식)
     */
    @Override
    public PublicKeysResponse execute() {
        List<JwkResponse> publicKeys = publicKeyClient.getPublicKeys();
        return PublicKeysResponse.of(publicKeys);
    }
}
