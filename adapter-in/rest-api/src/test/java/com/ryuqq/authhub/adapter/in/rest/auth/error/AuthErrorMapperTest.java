package com.ryuqq.authhub.adapter.in.rest.auth.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

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
@ExtendWith(MockitoExtension.class)
class AuthErrorMapperTest {

    @Mock private MessageSource messageSource;

    private AuthErrorMapper mapper;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @BeforeEach
    void setUp() {
        mapper = new AuthErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("AUTH- 접두사 코드 지원")
        void givenAuthPrefixCode_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("AUTH-001")).isTrue();
            assertThat(mapper.supports("AUTH-002")).isTrue();
            assertThat(mapper.supports("AUTH-007")).isTrue();
        }

        @Test
        @DisplayName("AUTH- 접두사 없는 코드는 false 반환")
        void givenNonAuthCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports("USER-001")).isFalse();
            assertThat(mapper.supports("ROLE-001")).isFalse();
        }

        @Test
        @DisplayName("null 코드는 false 반환")
        void givenNullCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("map() 테스트")
    class MapTest {

        @Test
        @DisplayName("AUTH-001 → 401 Unauthorized")
        void givenAuth001Exception_whenMap_thenReturnsUnauthorized() {
            // given
            DomainException ex = new DomainException("AUTH-001", "인증 실패");
            when(messageSource.getMessage(
                            eq("problem.title.auth-001"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("Authentication Failed");
            when(messageSource.getMessage(
                            eq("problem.detail.auth-001"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("인증 실패");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(error.title()).isEqualTo("Authentication Failed");
            assertThat(error.type().toString()).contains("auth-001");
        }

        @Test
        @DisplayName("AUTH-007 → 403 Forbidden")
        void givenAuth007Exception_whenMap_thenReturnsForbidden() {
            // given
            DomainException ex = new DomainException("AUTH-007", "접근 금지");
            when(messageSource.getMessage(
                            eq("problem.title.auth-007"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("Access Forbidden");
            when(messageSource.getMessage(
                            eq("problem.detail.auth-007"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("접근 금지");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(error.title()).isEqualTo("Access Forbidden");
        }

        @Test
        @DisplayName("MessageSource 기본값 사용")
        void givenNoMessageKey_whenMap_thenUsesDefault() {
            // given
            DomainException ex = new DomainException("AUTH-002", "기본 오류 메시지");
            when(messageSource.getMessage(
                            eq("problem.title.auth-002"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("Unauthorized");
            when(messageSource.getMessage(
                            eq("problem.detail.auth-002"), any(), any(), eq(DEFAULT_LOCALE)))
                    .thenReturn("기본 오류 메시지");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(error.detail()).isEqualTo("기본 오류 메시지");
        }
    }
}
