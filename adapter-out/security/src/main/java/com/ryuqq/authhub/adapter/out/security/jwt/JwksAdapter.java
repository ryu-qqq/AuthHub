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

    /**
     * JWKS 형식의 공개키 목록을 반환합니다.
     *
     * <p>로딩 우선순위:
     *
     * <ol>
     *   <li>publicKeyContent - 환경변수로 직접 전달된 키 내용 (ECS 권장)
     *   <li>publicKeyPath - 파일 경로에서 로딩 (로컬 개발용)
     * </ol>
     */
    @Override
    public List<JwkResponse> getPublicKeys() {
        JwtProperties.RsaKeyProperties rsaProperties = jwtProperties.getRsa();

        if (!rsaProperties.isEnabled()) {
            return Collections.emptyList();
        }

        try {
            RSAPublicKey publicKey = loadPublicKey(rsaProperties);
            JwkResponse jwk = toJwkResponse(rsaProperties.getKeyId(), publicKey);
            return List.of(jwk);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    private RSAPublicKey loadPublicKey(JwtProperties.RsaKeyProperties rsaProperties)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContent = rsaProperties.getResolvedPublicKeyContent();

        if (keyContent == null) {
            String path = rsaProperties.getPublicKeyPath();
            if (path == null || path.isBlank()) {
                throw new IllegalStateException(
                        "RSA is enabled but neither publicKeyContent nor publicKeyPath is"
                                + " configured");
            }
            keyContent = loadKeyFromFile(path);
        }

        return parseRsaPublicKey(keyContent);
    }

    private String loadKeyFromFile(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load RSA public key from path: " + path, e);
        }
    }

    private RSAPublicKey parseRsaPublicKey(String keyContent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
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
