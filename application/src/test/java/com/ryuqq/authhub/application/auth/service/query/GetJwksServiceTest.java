package com.ryuqq.authhub.application.auth.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import com.ryuqq.authhub.application.auth.dto.response.JwksResponse;
import com.ryuqq.authhub.application.auth.port.out.query.JwksPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetJwksService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetJwksService 단위 테스트")
class GetJwksServiceTest {

    @Mock private JwksPort jwksPort;

    private GetJwksService service;

    @BeforeEach
    void setUp() {
        service = new GetJwksService(jwksPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("JWKS를 성공적으로 조회한다")
        void shouldRetrieveJwksSuccessfully() {
            // given
            JwkResponse jwk1 = JwkResponse.of("key-id-1", "modulus-1", "exponent-1");
            JwkResponse jwk2 = JwkResponse.of("key-id-2", "modulus-2", "exponent-2");

            given(jwksPort.getPublicKeys()).willReturn(List.of(jwk1, jwk2));

            // when
            JwksResponse response = service.execute();

            // then
            assertThat(response.keys()).hasSize(2);
            assertThat(response.keys()).containsExactly(jwk1, jwk2);
            verify(jwksPort).getPublicKeys();
        }

        @Test
        @DisplayName("공개키가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoPublicKeys() {
            // given
            given(jwksPort.getPublicKeys()).willReturn(List.of());

            // when
            JwksResponse response = service.execute();

            // then
            assertThat(response.keys()).isEmpty();
            verify(jwksPort).getPublicKeys();
        }

        @Test
        @DisplayName("단일 키도 정상적으로 반환한다")
        void shouldReturnSingleKey() {
            // given
            JwkResponse jwk = JwkResponse.of("single-key", "single-modulus", "single-exponent");

            given(jwksPort.getPublicKeys()).willReturn(List.of(jwk));

            // when
            JwksResponse response = service.execute();

            // then
            assertThat(response.keys()).hasSize(1);
            assertThat(response.keys().get(0).kid()).isEqualTo("single-key");
            assertThat(response.keys().get(0).kty()).isEqualTo("RSA");
            assertThat(response.keys().get(0).use()).isEqualTo("sig");
            assertThat(response.keys().get(0).alg()).isEqualTo("RS256");
        }
    }
}
