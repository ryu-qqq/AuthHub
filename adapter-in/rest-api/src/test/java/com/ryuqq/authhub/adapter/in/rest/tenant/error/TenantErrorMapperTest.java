package com.ryuqq.authhub.adapter.in.rest.tenant.error;

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
 * TenantErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantErrorMapper 단위 테스트")
class TenantErrorMapperTest {

    private TenantErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("TenantNotFoundException을 지원한다")
        void shouldSupportTenantNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.tenantNotFoundException())).isTrue();
        }

        @Test
        @DisplayName("DuplicateTenantNameException을 지원한다")
        void shouldSupportDuplicateTenantNameException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicateTenantNameException()))
                    .isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void shouldNotSupportOtherDomainExceptions() {
            assertThat(mapper.supports(ErrorMapperApiFixture.roleNotFoundException())).isFalse();
        }
    }

    @Nested
    @DisplayName("map() 메서드는")
    class MapMethod {

        @Test
        @DisplayName("TenantNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapTenantNotFoundTo404() {
            // Given
            var ex = ErrorMapperApiFixture.tenantNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Tenant Not Found");
        }

        @Test
        @DisplayName("DuplicateTenantNameException을 409 Conflict로 매핑한다")
        void shouldMapDuplicateTenantNameTo409() {
            // Given
            var ex = ErrorMapperApiFixture.duplicateTenantNameException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Tenant Name Duplicate");
        }

        @Test
        @DisplayName("미지원 예외는 default 브랜치로 400 Bad Request로 매핑한다")
        void shouldMapUnsupportedExceptionTo400() {
            // Given
            // supports()는 false를 반환하지만, map()이 호출되면 default 브랜치 실행
            // 실제로는 supports()가 false이면 map()이 호출되지 않지만,
            // switch default 브랜치 테스트를 위해 직접 호출
            var ex = ErrorMapperApiFixture.roleNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Tenant Error");
            assertThat(result.type().toString()).contains("/errors/tenant");
        }
    }
}
