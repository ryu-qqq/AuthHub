package com.ryuqq.authhub.adapter.in.rest.auth.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
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
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("AuthErrorMapper 단위 테스트")
class AuthErrorMapperTest {

    @Mock private MessageSource messageSource;

    private AuthErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("AUTH- 접두사 예외를 지원한다")
        void shouldSupportAuthPrefix() {
            assertThat(mapper.supports(ErrorMapperApiFixture.invalidCredentialsException()))
                    .isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void shouldNotSupportOtherDomainExceptions() {
            assertThat(mapper.supports(ErrorMapperApiFixture.tenantNotFoundException())).isFalse();
        }
    }

    @Nested
    @DisplayName("map() 메서드는")
    class MapMethod {

        @Test
        @DisplayName("AUTH-001을 401 Unauthorized로 매핑한다")
        void shouldMapAuth001To401() {
            var ex = ErrorMapperApiFixture.invalidCredentialsException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
