package com.ryuqq.authhub.adapter.in.rest.tenant.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * TenantErrorMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>지원하는 에러 코드 검증
 *   <li>에러 코드별 HTTP 상태 코드 매핑
 *   <li>에러 코드별 타이틀 매핑
 *   <li>에러 코드별 타입 URI 매핑
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TenantErrorMapper 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class TenantErrorMapperTest {

    private TenantErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantErrorMapper();
    }

    @Nested
    @DisplayName("supports(String) 테스트")
    class SupportsTest {

        @Test
        @DisplayName("[supports] TENANT-001 코드를 지원한다")
        void supports_shouldReturnTrueForTenant001() {
            // When
            boolean result = mapper.supports("TENANT-001");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[supports] TENANT-002 코드를 지원한다")
        void supports_shouldReturnTrueForTenant002() {
            // When
            boolean result = mapper.supports("TENANT-002");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[supports] TENANT-003 코드를 지원한다")
        void supports_shouldReturnTrueForTenant003() {
            // When
            boolean result = mapper.supports("TENANT-003");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[supports] TENANT-004 코드를 지원한다")
        void supports_shouldReturnTrueForTenant004() {
            // When
            boolean result = mapper.supports("TENANT-004");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[supports] 지원하지 않는 코드에 대해 false 반환")
        void supports_shouldReturnFalseForUnsupportedCode() {
            // When
            boolean result = mapper.supports("USER-001");

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[supports] null 코드에 대해 false 반환")
        void supports_shouldReturnFalseForNullCode() {
            // When
            boolean result = mapper.supports(null);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map(DomainException, Locale) 테스트")
    class MapTest {

        @Test
        @DisplayName("[map] TENANT-001 -> 404 Not Found")
        void map_tenant001_shouldReturn404NotFound() {
            // Given
            DomainException exception = new DomainException("TENANT-001", "테넌트를 찾을 수 없습니다");

            // When
            ErrorMapper.MappedError result = mapper.map(exception, Locale.KOREAN);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Tenant Not Found");
            assertThat(result.detail()).isEqualTo("테넌트를 찾을 수 없습니다");
            assertThat(result.type())
                    .isEqualTo(URI.create("https://authhub.ryuqq.com/errors/tenant-not-found"));
        }

        @Test
        @DisplayName("[map] TENANT-002 -> 409 Conflict")
        void map_tenant002_shouldReturn409Conflict() {
            // Given
            DomainException exception = new DomainException("TENANT-002", "테넌트 이름이 중복됩니다");

            // When
            ErrorMapper.MappedError result = mapper.map(exception, Locale.KOREAN);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Tenant Name Duplicate");
            assertThat(result.detail()).isEqualTo("테넌트 이름이 중복됩니다");
            assertThat(result.type())
                    .isEqualTo(URI.create("https://authhub.ryuqq.com/errors/tenant-duplicate"));
        }

        @Test
        @DisplayName("[map] TENANT-003 -> 400 Bad Request")
        void map_tenant003_shouldReturn400BadRequest() {
            // Given
            DomainException exception = new DomainException("TENANT-003", "테넌트 이름이 유효하지 않습니다");

            // When
            ErrorMapper.MappedError result = mapper.map(exception, Locale.KOREAN);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Invalid Tenant Name");
            assertThat(result.detail()).isEqualTo("테넌트 이름이 유효하지 않습니다");
            assertThat(result.type())
                    .isEqualTo(URI.create("https://authhub.ryuqq.com/errors/tenant-invalid-name"));
        }

        @Test
        @DisplayName("[map] TENANT-004 -> 422 Unprocessable Entity")
        void map_tenant004_shouldReturn422UnprocessableEntity() {
            // Given
            DomainException exception = new DomainException("TENANT-004", "상태 전환이 불가능합니다");

            // When
            ErrorMapper.MappedError result = mapper.map(exception, Locale.KOREAN);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(result.title()).isEqualTo("Invalid Tenant Status Transition");
            assertThat(result.detail()).isEqualTo("상태 전환이 불가능합니다");
            assertThat(result.type())
                    .isEqualTo(
                            URI.create("https://authhub.ryuqq.com/errors/tenant-invalid-status"));
        }

        @Test
        @DisplayName("[map] 알 수 없는 코드 -> 500 Internal Server Error")
        void map_unknownCode_shouldReturn500InternalServerError() {
            // Given
            DomainException exception = new DomainException("TENANT-999", "알 수 없는 에러");

            // When
            ErrorMapper.MappedError result = mapper.map(exception, Locale.KOREAN);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(result.title()).isEqualTo("Internal Server Error");
            assertThat(result.detail()).isEqualTo("알 수 없는 에러");
            assertThat(result.type())
                    .isEqualTo(URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        }
    }
}
