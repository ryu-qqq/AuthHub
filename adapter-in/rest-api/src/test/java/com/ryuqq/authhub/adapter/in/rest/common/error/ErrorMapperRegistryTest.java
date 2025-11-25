package com.ryuqq.authhub.adapter.in.rest.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("ErrorMapperRegistry 테스트")
class ErrorMapperRegistryTest {

    @Nested
    @DisplayName("map(DomainException, Locale) 테스트")
    class MapTest {

        @Test
        @DisplayName("[map] 지원하는 에러 코드가 있으면 매핑된 결과 반환")
        void map_shouldReturnMappedErrorWhenSupportedCodeExists() {
            // Given
            ErrorMapper tenantMapper =
                    new TestErrorMapper(
                            "TENANT_NOT_FOUND",
                            HttpStatus.NOT_FOUND,
                            "Not Found",
                            "테넌트를 찾을 수 없습니다",
                            URI.create("/errors/tenant-not-found"));
            ErrorMapperRegistry registry = new ErrorMapperRegistry(List.of(tenantMapper));
            DomainException exception = new DomainException("TENANT_NOT_FOUND", "테넌트가 없습니다");

            // When
            Optional<ErrorMapper.MappedError> result = registry.map(exception, Locale.KOREAN);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.get().title()).isEqualTo("Not Found");
            assertThat(result.get().detail()).isEqualTo("테넌트를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("[map] 지원하는 에러 코드가 없으면 빈 Optional 반환")
        void map_shouldReturnEmptyWhenNoSupportedCodeExists() {
            // Given
            ErrorMapper userMapper =
                    new TestErrorMapper(
                            "USER_NOT_FOUND",
                            HttpStatus.NOT_FOUND,
                            "Not Found",
                            "사용자를 찾을 수 없습니다",
                            URI.create("/errors/user-not-found"));
            ErrorMapperRegistry registry = new ErrorMapperRegistry(List.of(userMapper));
            DomainException exception = new DomainException("TENANT_NOT_FOUND", "테넌트가 없습니다");

            // When
            Optional<ErrorMapper.MappedError> result = registry.map(exception, Locale.KOREAN);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[map] 빈 매퍼 리스트일 때 빈 Optional 반환")
        void map_shouldReturnEmptyWhenMapperListIsEmpty() {
            // Given
            ErrorMapperRegistry registry = new ErrorMapperRegistry(List.of());
            DomainException exception = new DomainException("ANY_ERROR", "에러 발생");

            // When
            Optional<ErrorMapper.MappedError> result = registry.map(exception, Locale.KOREAN);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[map] 여러 매퍼 중 첫 번째 일치하는 매퍼의 결과 반환")
        void map_shouldReturnFirstMatchingMapperResult() {
            // Given
            ErrorMapper firstMapper =
                    new TestErrorMapper(
                            "USER_NOT_FOUND",
                            HttpStatus.NOT_FOUND,
                            "First Mapper",
                            "첫 번째 매퍼",
                            URI.create("/errors/first"));
            ErrorMapper secondMapper =
                    new TestErrorMapper(
                            "USER_NOT_FOUND",
                            HttpStatus.BAD_REQUEST,
                            "Second Mapper",
                            "두 번째 매퍼",
                            URI.create("/errors/second"));
            ErrorMapperRegistry registry =
                    new ErrorMapperRegistry(List.of(firstMapper, secondMapper));
            DomainException exception = new DomainException("USER_NOT_FOUND", "사용자 없음");

            // When
            Optional<ErrorMapper.MappedError> result = registry.map(exception, Locale.KOREAN);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().title()).isEqualTo("First Mapper");
        }
    }

    @Nested
    @DisplayName("defaultMapping(DomainException) 테스트")
    class DefaultMappingTest {

        @Test
        @DisplayName("[defaultMapping] 기본 매핑 결과 반환 (BAD_REQUEST)")
        void defaultMapping_shouldReturnBadRequestMapping() {
            // Given
            ErrorMapperRegistry registry = new ErrorMapperRegistry(List.of());
            DomainException exception = new DomainException("UNKNOWN_ERROR", "알 수 없는 에러입니다");

            // When
            ErrorMapper.MappedError result = registry.defaultMapping(exception);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Bad Request");
            assertThat(result.detail()).isEqualTo("알 수 없는 에러입니다");
            assertThat(result.type()).isEqualTo(URI.create("about:blank"));
        }

        @Test
        @DisplayName("[defaultMapping] 예외 메시지가 null이면 기본 메시지 사용")
        void defaultMapping_shouldUseDefaultMessageWhenExceptionMessageIsNull() {
            // Given
            ErrorMapperRegistry registry = new ErrorMapperRegistry(List.of());
            DomainException exception = new DomainException("UNKNOWN_ERROR", null);

            // When
            ErrorMapper.MappedError result = registry.defaultMapping(exception);

            // Then
            assertThat(result.detail()).isEqualTo("Invalid request");
        }
    }

    /** 테스트용 ErrorMapper 구현체 */
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    private static class TestErrorMapper implements ErrorMapper {

        private final String supportedCode;
        private final HttpStatus status;
        private final String title;
        private final String detail;
        private final URI type;

        TestErrorMapper(
                String supportedCode, HttpStatus status, String title, String detail, URI type) {
            this.supportedCode = supportedCode;
            this.status = status;
            this.title = title;
            this.detail = detail;
            this.type = type;
        }

        @Override
        public boolean supports(String code) {
            return supportedCode.equals(code);
        }

        @Override
        public MappedError map(DomainException ex, Locale locale) {
            return new MappedError(status, title, detail, type);
        }
    }
}
