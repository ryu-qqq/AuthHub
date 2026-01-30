package com.ryuqq.authhub.adapter.out.client.security.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import com.ryuqq.authhub.adapter.out.client.security.fixture.SecurityClientFixtures;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RsaKeyLoader")
class RsaKeyLoaderTest {

    private JwtProperties jwtProperties;
    private RsaKeyLoader sut;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        sut = new RsaKeyLoader(jwtProperties);
    }

    @Nested
    @DisplayName("isRsaEnabled")
    class IsRsaEnabled {

        @Test
        @DisplayName("RSA 비활성화 시 false 반환")
        void returnsFalseWhenDisabled() {
            jwtProperties.getRsa().setEnabled(false);

            assertThat(sut.isRsaEnabled()).isFalse();
        }

        @Test
        @DisplayName("RSA 활성화 시 true 반환")
        void returnsTrueWhenEnabled() {
            jwtProperties.getRsa().setEnabled(true);

            assertThat(sut.isRsaEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("getKeyId")
    class GetKeyId {

        @Test
        @DisplayName("설정된 Key ID 반환")
        void returnsConfiguredKeyId() {
            jwtProperties.getRsa().setKeyId("custom-key-id");

            assertThat(sut.getKeyId()).isEqualTo("custom-key-id");
        }

        @Test
        @DisplayName("기본 Key ID 반환")
        void returnsDefaultKeyId() {
            assertThat(sut.getKeyId()).isEqualTo("authhub-key-1");
        }
    }

    @Nested
    @DisplayName("loadPublicKey")
    class LoadPublicKey {

        @Test
        @DisplayName("keyContent로 공개키 로드")
        void loadsPublicKeyFromContent() throws Exception {
            KeyPair keyPair = SecurityClientFixtures.generateRsaKeyPair();
            String publicKeyPem = SecurityClientFixtures.toPublicKeyPem(keyPair);

            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPublicKeyContent(publicKeyPem);

            RSAPublicKey result = sut.loadPublicKey();

            assertThat(result).isNotNull();
            assertThat(result.getModulus()).isNotNull();
            assertThat(result.getPublicExponent()).isNotNull();
        }

        @Test
        @DisplayName("keyPath로 공개키 로드")
        void loadsPublicKeyFromFile(@TempDir Path tempDir) throws Exception {
            KeyPair keyPair = SecurityClientFixtures.generateRsaKeyPair();
            String publicKeyPem = SecurityClientFixtures.toPublicKeyPem(keyPair);
            Path keyFile = tempDir.resolve("public.pem");
            Files.writeString(keyFile, publicKeyPem);

            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPublicKeyPath(keyFile.toString());

            RSAPublicKey result = sut.loadPublicKey();

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("keyContent와 keyPath 모두 없으면 예외")
        void throwsWhenNeitherContentNorPath() {
            jwtProperties.getRsa().setEnabled(true);

            assertThatThrownBy(() -> sut.loadPublicKey())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("neither publicKeyContent nor publicKeyPath");
        }

        @Test
        @DisplayName("keyPath가 빈 문자열이면 예외")
        void throwsWhenPathIsBlank() {
            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPublicKeyPath("   ");

            assertThatThrownBy(() -> sut.loadPublicKey())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("neither publicKeyContent nor publicKeyPath");
        }

        @Test
        @DisplayName("존재하지 않는 파일 경로면 예외")
        void throwsWhenFileNotFound() {
            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPublicKeyPath("/nonexistent/path/public.pem");

            assertThatThrownBy(() -> sut.loadPublicKey())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to load RSA");
        }
    }

    @Nested
    @DisplayName("loadPrivateKey")
    class LoadPrivateKey {

        @Test
        @DisplayName("keyContent로 비밀키 로드")
        void loadsPrivateKeyFromContent() throws Exception {
            KeyPair keyPair = SecurityClientFixtures.generateRsaKeyPair();
            String privateKeyPem = SecurityClientFixtures.toPrivateKeyPem(keyPair);

            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPrivateKeyContent(privateKeyPem);

            RSAPrivateKey result = sut.loadPrivateKey();

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("keyPath로 비밀키 로드")
        void loadsPrivateKeyFromFile(@TempDir Path tempDir) throws Exception {
            KeyPair keyPair = SecurityClientFixtures.generateRsaKeyPair();
            String privateKeyPem = SecurityClientFixtures.toPrivateKeyPem(keyPair);
            Path keyFile = tempDir.resolve("private.pem");
            Files.writeString(keyFile, privateKeyPem);

            jwtProperties.getRsa().setEnabled(true);
            jwtProperties.getRsa().setPrivateKeyPath(keyFile.toString());

            RSAPrivateKey result = sut.loadPrivateKey();

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("keyContent와 keyPath 모두 없으면 예외")
        void throwsWhenNeitherContentNorPath() {
            jwtProperties.getRsa().setEnabled(true);

            assertThatThrownBy(() -> sut.loadPrivateKey())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("neither privateKeyContent nor privateKeyPath");
        }
    }
}
