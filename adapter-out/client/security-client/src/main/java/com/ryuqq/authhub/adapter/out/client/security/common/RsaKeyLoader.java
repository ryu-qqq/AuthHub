package com.ryuqq.authhub.adapter.out.client.security.common;

import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * RsaKeyLoader - RSA 키 로딩 유틸리티
 *
 * <p>RSA 공개키/비밀키 로딩 로직을 공통화합니다.
 *
 * <p><strong>키 로딩 우선순위:</strong>
 *
 * <ol>
 *   <li>keyContent - 환경변수로 직접 전달된 키 내용 (ECS 권장)
 *   <li>keyPath - 파일 경로에서 로딩 (로컬 개발용)
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RsaKeyLoader {

    private static final String RSA_ALGORITHM = "RSA";

    private final JwtProperties jwtProperties;

    public RsaKeyLoader(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * RSA 설정 활성화 여부
     *
     * @return RSA 활성화 여부
     */
    public boolean isRsaEnabled() {
        return jwtProperties.getRsa().isEnabled();
    }

    /**
     * RSA Key ID 조회
     *
     * @return Key ID
     */
    public String getKeyId() {
        return jwtProperties.getRsa().getKeyId();
    }

    /**
     * RSA Public Key 로드
     *
     * @return RSA 공개키
     * @throws IllegalStateException 키 로드 실패 시
     */
    public RSAPublicKey loadPublicKey() {
        JwtProperties.RsaKeyProperties rsaProperties = jwtProperties.getRsa();
        String keyContent = rsaProperties.getResolvedPublicKeyContent();

        if (keyContent == null) {
            String path = rsaProperties.getPublicKeyPath();
            if (path == null || path.isBlank()) {
                throw new IllegalStateException(
                        "RSA is enabled but neither publicKeyContent nor publicKeyPath is"
                                + " configured");
            }
            keyContent = loadKeyFromFile(path, "public");
        }

        return parsePublicKey(keyContent);
    }

    /**
     * RSA Private Key 로드
     *
     * @return RSA 비밀키
     * @throws IllegalStateException 키 로드 실패 시
     */
    public RSAPrivateKey loadPrivateKey() {
        JwtProperties.RsaKeyProperties rsaProperties = jwtProperties.getRsa();
        String keyContent = rsaProperties.getResolvedPrivateKeyContent();

        if (keyContent == null) {
            String path = rsaProperties.getPrivateKeyPath();
            if (path == null || path.isBlank()) {
                throw new IllegalStateException(
                        "RSA is enabled but neither privateKeyContent nor privateKeyPath is"
                                + " configured");
            }
            keyContent = loadKeyFromFile(path, "private");
        }

        return parsePrivateKey(keyContent);
    }

    private String loadKeyFromFile(String path, String keyType) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load RSA " + keyType + " key from path: " + path, e);
        }
    }

    private RSAPublicKey parsePublicKey(String keyContent) {
        try {
            String keyPem =
                    stripPemHeaders(
                            keyContent, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");

            byte[] encoded = Base64.getDecoder().decode(keyPem);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to parse RSA public key", e);
        }
    }

    private RSAPrivateKey parsePrivateKey(String keyContent) {
        try {
            String keyPem =
                    stripPemHeaders(
                            keyContent,
                            "-----BEGIN RSA PRIVATE KEY-----",
                            "-----END RSA PRIVATE KEY-----",
                            "-----BEGIN PRIVATE KEY-----",
                            "-----END PRIVATE KEY-----");

            byte[] encoded = Base64.getDecoder().decode(keyPem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to parse RSA private key", e);
        }
    }

    private String stripPemHeaders(String content, String... headers) {
        String result = content;
        for (String header : headers) {
            result = result.replace(header, "");
        }
        return result.replaceAll("\\s", "");
    }
}
