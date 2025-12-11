package com.ryuqq.authhub.adapter.out.security.jwt;

import com.ryuqq.authhub.adapter.out.security.config.JwtProperties;
import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import com.ryuqq.authhub.application.auth.port.out.query.JwksPort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * JWKS Adapter - RSA 공개키 조회 어댑터
 *
 * <p>RSA 공개키 파일을 읽어 JWKS 형식으로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Port 구현체
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (변환만 수행)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class JwksAdapter implements JwksPort {

    private final JwtProperties jwtProperties;

    public JwksAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public List<JwkResponse> getPublicKeys() {
        JwtProperties.RsaKeyProperties rsaProperties = jwtProperties.getRsa();

        if (!rsaProperties.isEnabled()) {
            return Collections.emptyList();
        }

        String publicKeyPath = rsaProperties.getPublicKeyPath();
        if (publicKeyPath == null || publicKeyPath.isBlank()) {
            return Collections.emptyList();
        }

        try {
            RSAPublicKey publicKey = loadPublicKey(publicKeyPath);
            JwkResponse jwk = toJwkResponse(rsaProperties.getKeyId(), publicKey);
            return List.of(jwk);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(
                    "Failed to load RSA public key from path: " + publicKeyPath, e);
        }
    }

    private RSAPublicKey loadPublicKey(String path)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContent = Files.readString(Path.of(path));
        String publicKeyPem =
                keyContent
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
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
