package com.ryuqq.authhub.adapter.in.rest.auth.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.auth.exception.AuthErrorCode;
import com.ryuqq.authhub.domain.common.exception.DomainException;

/**
 * AuthErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("AuthErrorMapper 테스트")
class AuthErrorMapperTest {

    private MessageSource messageSource;
    private AuthErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        errorMapper = new AuthErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("AUTH- 접두사를 가진 코드는 지원한다")
        void givenAuthPrefix_whenSupports_thenReturnsTrue() {
            assertThat(errorMapper.supports("AUTH-001")).isTrue();
            assertThat(errorMapper.supports("AUTH-007")).isTrue();
        }

        @Test
        @DisplayName("AUTH- 접두사가 없는 코드는 지원하지 않는다")
        void givenNonAuthPrefix_whenSupports_thenReturnsFalse() {
            assertThat(errorMapper.supports("USER-001")).isFalse();
            assertThat(errorMapper.supports("ROLE-001")).isFalse();
        }

        @Test
        @DisplayName("null 코드는 지원하지 않는다")
        void givenNullCode_whenSupports_thenReturnsFalse() {
            assertThat(errorMapper.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("map() HttpStatus 매핑 테스트")
    class HttpStatusMappingTest {

        @Test
        @DisplayName("INVALID_CREDENTIALS (AUTH-001)는 401 Unauthorized로 매핑된다")
        void givenInvalidCredentials_whenMap_thenReturns401() {
            // given
            DomainException ex = new DomainException(AuthErrorCode.INVALID_CREDENTIALS);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("EXPIRED_REFRESH_TOKEN (AUTH-003)는 401 Unauthorized로 매핑된다")
        void givenExpiredRefreshToken_whenMap_thenReturns401() {
            // given
            DomainException ex = new DomainException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("FORBIDDEN (AUTH-007)는 403 Forbidden으로 매핑된다")
        void givenForbidden_whenMap_thenReturns403() {
            // given
            DomainException ex = new DomainException(AuthErrorCode.FORBIDDEN);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("map() Type URI 생성 테스트")
    class TypeUriTest {

        @Test
        @DisplayName("Type URI가 올바르게 생성된다")
        void givenInvalidCredentials_whenMap_thenGeneratesCorrectTypeUri() {
            // given
            DomainException ex = new DomainException(AuthErrorCode.INVALID_CREDENTIALS);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            URI expected = URI.create("https://api.authhub.com/problems/auth/auth-001");
            assertThat(mapped.type()).isEqualTo(expected);
        }
    }

    private void stubMessageSource() {
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenReturn("Test Message");
    }
}
