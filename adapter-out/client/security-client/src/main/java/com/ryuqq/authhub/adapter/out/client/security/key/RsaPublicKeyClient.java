package com.ryuqq.authhub.adapter.out.client.security.key;

import com.ryuqq.authhub.adapter.out.client.security.common.RsaKeyLoader;
import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import com.ryuqq.authhub.application.token.port.out.client.PublicKeyClient;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RsaPublicKeyClient - RSA 공개키 Client
 *
 * <p>RSA 공개키를 JWK 형식으로 변환하여 제공합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>RSA 공개키 로드 (RsaKeyLoader 위임)
 *   <li>JWK 형식 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RsaPublicKeyClient implements PublicKeyClient {

    private final RsaKeyLoader rsaKeyLoader;

    public RsaPublicKeyClient(RsaKeyLoader rsaKeyLoader) {
        this.rsaKeyLoader = rsaKeyLoader;
    }

    @Override
    public List<JwkResponse> getPublicKeys() {
        if (!rsaKeyLoader.isRsaEnabled()) {
            return Collections.emptyList();
        }

        RSAPublicKey publicKey = rsaKeyLoader.loadPublicKey();
        JwkResponse jwk = toJwkResponse(rsaKeyLoader.getKeyId(), publicKey);
        return List.of(jwk);
    }

    private JwkResponse toJwkResponse(String keyId, RSAPublicKey publicKey) {
        String n =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(publicKey.getModulus().toByteArray());
        String e =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(publicKey.getPublicExponent().toByteArray());
        return JwkResponse.of(keyId, n, e);
    }
}
