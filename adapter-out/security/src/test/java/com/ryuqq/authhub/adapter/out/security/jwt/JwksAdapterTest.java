package com.ryuqq.authhub.adapter.out.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.security.config.JwtProperties;
import com.ryuqq.authhub.application.auth.dto.response.JwkResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * JwksAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("JwksAdapter 단위 테스트")
class JwksAdapterTest {

    private JwtProperties jwtProperties;
    private JwksAdapter adapter;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        adapter = new JwksAdapter(jwtProperties);
    }

    @Nested
    @DisplayName("getPublicKeys 메서드")
    class GetPublicKeysTest {

        @Test
        @DisplayName("RSA가 비활성화되어 있으면 빈 리스트를 반환한다")
        void shouldReturnEmptyListWhenRsaDisabled() {
            // given
            JwtProperties.RsaKeyProperties rsaProps = new JwtProperties.RsaKeyProperties();
            rsaProps.setEnabled(false);
            jwtProperties.setRsa(rsaProps);
            adapter = new JwksAdapter(jwtProperties);

            // when
            List<JwkResponse> result = adapter.getPublicKeys();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("공개키 경로가 null이면 빈 리스트를 반환한다")
        void shouldReturnEmptyListWhenPublicKeyPathIsNull() {
            // given
            JwtProperties.RsaKeyProperties rsaProps = new JwtProperties.RsaKeyProperties();
            rsaProps.setEnabled(true);
            rsaProps.setPublicKeyPath(null);
            jwtProperties.setRsa(rsaProps);
            adapter = new JwksAdapter(jwtProperties);

            // when
            List<JwkResponse> result = adapter.getPublicKeys();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("공개키 경로가 빈 문자열이면 빈 리스트를 반환한다")
        void shouldReturnEmptyListWhenPublicKeyPathIsBlank() {
            // given
            JwtProperties.RsaKeyProperties rsaProps = new JwtProperties.RsaKeyProperties();
            rsaProps.setEnabled(true);
            rsaProps.setPublicKeyPath("   ");
            jwtProperties.setRsa(rsaProps);
            adapter = new JwksAdapter(jwtProperties);

            // when
            List<JwkResponse> result = adapter.getPublicKeys();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("RSA가 기본 설정(비활성화)이면 빈 리스트를 반환한다")
        void shouldReturnEmptyListWithDefaultSettings() {
            // given - default RSA settings (disabled)

            // when
            List<JwkResponse> result = adapter.getPublicKeys();

            // then
            assertThat(result).isEmpty();
        }
    }
}
