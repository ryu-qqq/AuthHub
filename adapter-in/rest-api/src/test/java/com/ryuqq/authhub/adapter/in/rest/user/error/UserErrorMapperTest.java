package com.ryuqq.authhub.adapter.in.rest.user.error;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * UserErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserErrorMapper 단위 테스트")
class UserErrorMapperTest {

    private UserErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("USER-001 코드를 지원한다")
        void shouldSupportUserNotFound() {
            assertThat(mapper.supports(ErrorMapperApiFixture.userNotFoundException())).isTrue();
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
        @DisplayName("USER-001을 404 Not Found로 매핑한다")
        void shouldMapUserNotFoundTo404() {
            // Given
            var ex = ErrorMapperApiFixture.userNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("User Not Found");
        }
    }
}
