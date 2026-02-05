package com.ryuqq.authhub.adapter.in.rest.service.error;

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
 * ServiceErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ServiceErrorMapper 단위 테스트")
class ServiceErrorMapperTest {

    private ServiceErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("ServiceNotFoundException을 지원한다")
        void shouldSupportServiceNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.serviceNotFoundException())).isTrue();
        }

        @Test
        @DisplayName("DuplicateServiceIdException을 지원한다")
        void shouldSupportDuplicateServiceIdException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicateServiceIdException()))
                    .isTrue();
        }

        @Test
        @DisplayName("ServiceInUseException을 지원한다")
        void shouldSupportServiceInUseException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.serviceInUseException())).isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void shouldNotSupportOtherDomainExceptions() {
            assertThat(mapper.supports(ErrorMapperApiFixture.tenantNotFoundException())).isFalse();
            assertThat(mapper.supports(ErrorMapperApiFixture.roleNotFoundException())).isFalse();
        }
    }

    @Nested
    @DisplayName("map() 메서드는")
    class MapMethod {

        @Test
        @DisplayName("ServiceNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapServiceNotFoundTo404() {
            // Given
            var ex = ErrorMapperApiFixture.serviceNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Service Not Found");
            assertThat(result.detail()).isNotNull();
            assertThat(result.type()).hasPath("/errors/service/not-found");
        }

        @Test
        @DisplayName("DuplicateServiceIdException을 409 Conflict로 매핑한다")
        void shouldMapDuplicateServiceIdTo409() {
            // Given
            var ex = ErrorMapperApiFixture.duplicateServiceIdException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Service Code Duplicate");
            assertThat(result.detail()).isNotNull();
            assertThat(result.type()).hasPath("/errors/service/duplicate-code");
        }

        @Test
        @DisplayName("ServiceInUseException을 409 Conflict로 매핑한다")
        void shouldMapServiceInUseTo409() {
            // Given
            var ex = ErrorMapperApiFixture.serviceInUseException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Service In Use");
            assertThat(result.detail()).isNotNull();
            assertThat(result.type()).hasPath("/errors/service/in-use");
        }
    }
}
