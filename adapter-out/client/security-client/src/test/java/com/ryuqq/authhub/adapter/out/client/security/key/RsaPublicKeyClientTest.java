package com.ryuqq.authhub.adapter.out.client.security.key;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.client.security.common.RsaKeyLoader;
import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import com.ryuqq.authhub.adapter.out.client.security.fixture.SecurityClientFixtures;
import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RsaPublicKeyClient")
class RsaPublicKeyClientTest {

    private RsaKeyLoader rsaKeyLoader;
    private RsaPublicKeyClient sut;

    @BeforeEach
    void setUp() {
        rsaKeyLoader = new RsaKeyLoader(createJwtPropertiesWithRsa());
        sut = new RsaPublicKeyClient(rsaKeyLoader);
    }

    @Nested
    @DisplayName("getPublicKeys")
    class GetPublicKeys {

        @Test
        @DisplayName("RSA 활성화 시 JWK 목록 반환")
        void returnsJwkListWhenRsaEnabled() throws Exception {
            JwtProperties props = createJwtPropertiesWithRsa();
            rsaKeyLoader = new RsaKeyLoader(props);
            sut = new RsaPublicKeyClient(rsaKeyLoader);

            List<JwkResponse> result = sut.getPublicKeys();

            assertThat(result).hasSize(1);
            JwkResponse jwk = result.get(0);
            assertThat(jwk.kid()).isEqualTo("test-key-1");
            assertThat(jwk.kty()).isEqualTo("RSA");
            assertThat(jwk.use()).isEqualTo("sig");
            assertThat(jwk.alg()).isEqualTo("RS256");
            assertThat(jwk.n()).isNotBlank();
            assertThat(jwk.e()).isNotBlank();
        }

        @Test
        @DisplayName("RSA 비활성화 시 빈 목록 반환")
        void returnsEmptyListWhenRsaDisabled() {
            JwtProperties props = new JwtProperties();
            props.getRsa().setEnabled(false);
            rsaKeyLoader = new RsaKeyLoader(props);
            sut = new RsaPublicKeyClient(rsaKeyLoader);

            List<JwkResponse> result = sut.getPublicKeys();

            assertThat(result).isEmpty();
        }
    }

    private JwtProperties createJwtPropertiesWithRsa() {
        try {
            return SecurityClientFixtures.jwtPropertiesRsaWithContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
