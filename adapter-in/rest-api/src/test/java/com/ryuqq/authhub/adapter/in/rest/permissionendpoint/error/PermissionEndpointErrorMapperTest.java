package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.error;

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
 * PermissionEndpointErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionEndpointErrorMapper 단위 테스트")
class PermissionEndpointErrorMapperTest {

    private PermissionEndpointErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionEndpointErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("PermissionEndpointNotFoundException을 지원한다")
        void shouldSupportPermissionEndpointNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.permissionEndpointNotFoundException()))
                    .isTrue();
        }

        @Test
        @DisplayName("DuplicatePermissionEndpointException을 지원한다")
        void shouldSupportDuplicatePermissionEndpointException() {
            assertThat(
                            mapper.supports(
                                    ErrorMapperApiFixture.duplicatePermissionEndpointException()))
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
        @DisplayName("PermissionEndpointNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapNotFoundTo404() {
            var ex = ErrorMapperApiFixture.permissionEndpointNotFoundException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("DuplicatePermissionEndpointException을 409 Conflict로 매핑한다")
        void shouldMapDuplicateTo409() {
            var ex = ErrorMapperApiFixture.duplicatePermissionEndpointException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
        }
    }
}
