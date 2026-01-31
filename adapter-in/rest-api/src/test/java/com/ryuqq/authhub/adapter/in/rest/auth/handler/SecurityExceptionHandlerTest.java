package com.ryuqq.authhub.adapter.in.rest.auth.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * SecurityExceptionHandler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SecurityExceptionHandler 단위 테스트")
class SecurityExceptionHandlerTest {

    private SecurityExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SecurityExceptionHandler(new ObjectMapper());
    }

    @Nested
    @DisplayName("commence() 메서드는")
    class CommenceMethod {

        @Test
        @DisplayName("AuthenticationException 시 401 Unauthorized를 반환한다")
        void shouldReturn401ForAuthenticationException() throws Exception {
            // Given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            var authException = new BadCredentialsException("Invalid credentials");

            // When
            handler.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentType())
                    .contains(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            assertThat(response.getHeader("x-error-code")).isEqualTo("UNAUTHORIZED");
        }
    }

    @Nested
    @DisplayName("handle() 메서드는")
    class HandleMethod {

        @Test
        @DisplayName("AccessDeniedException 시 403 Forbidden을 반환한다")
        void shouldReturn403ForAccessDeniedException() throws Exception {
            // Given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            var accessDeniedException = new AccessDeniedException("Access denied");

            // When
            handler.handle(request, response, accessDeniedException);

            // Then
            assertThat(response.getStatus()).isEqualTo(403);
            assertThat(response.getContentType())
                    .contains(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            assertThat(response.getHeader("x-error-code")).isEqualTo("FORBIDDEN");
        }
    }
}
