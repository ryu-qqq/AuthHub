package com.ryuqq.authhub.adapter.in.rest.common.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * ErrorMapperRegistry 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ErrorMapperRegistry 단위 테스트")
class ErrorMapperRegistryTest {

    @Mock private ErrorMapper mockMapper;

    private ErrorMapperRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ErrorMapperRegistry(List.of(mockMapper));
    }

    @Nested
    @DisplayName("map() 메서드는")
    class MapMethod {

        @Test
        @DisplayName("매칭되는 매퍼가 있으면 MappedError를 반환한다")
        void shouldReturnMappedErrorWhenMapperSupports() {
            // Given
            DomainException ex = ErrorMapperApiFixture.tenantNotFoundException();
            var expectedMapped =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Tenant Not Found",
                            "Tenant not found",
                            URI.create("about:blank"));

            when(mockMapper.supports(ex)).thenReturn(true);
            when(mockMapper.map(eq(ex), any(Locale.class))).thenReturn(expectedMapped);

            // When
            var result = registry.map(ex, Locale.KOREA);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedMapped);
        }

        @Test
        @DisplayName("매칭되는 매퍼가 없으면 empty를 반환한다")
        void shouldReturnEmptyWhenNoMapperSupports() {
            // Given
            DomainException ex = ErrorMapperApiFixture.tenantNotFoundException();
            when(mockMapper.supports(ex)).thenReturn(false);

            // When
            var result = registry.map(ex, Locale.KOREA);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("defaultMapping() 메서드는")
    class DefaultMappingMethod {

        @Test
        @DisplayName("DomainException의 httpStatus로 기본 MappedError를 반환한다")
        void shouldReturnDefaultMappedError() {
            // Given
            DomainException ex = ErrorMapperApiFixture.tenantNotFoundException();

            // When
            var result = registry.defaultMapping(ex);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Not Found");
            assertThat(result.detail()).isNotNull();
            assertThat(result.type()).isEqualTo(URI.create("about:blank"));
        }
    }
}
