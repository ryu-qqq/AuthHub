package com.ryuqq.authhub.adapter.in.rest.permission.error;

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
 * PermissionErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionErrorMapper 단위 테스트")
class PermissionErrorMapperTest {

    private PermissionErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("PermissionNotFoundException을 지원한다")
        void shouldSupportPermissionNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.permissionNotFoundException()))
                    .isTrue();
        }

        @Test
        @DisplayName("DuplicatePermissionKeyException을 지원한다")
        void shouldSupportDuplicatePermissionKeyException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicatePermissionKeyException()))
                    .isTrue();
        }

        @Test
        @DisplayName("SystemPermissionNotModifiableException을 지원한다")
        void shouldSupportSystemPermissionNotModifiableException() {
            assertThat(
                            mapper.supports(
                                    ErrorMapperApiFixture.systemPermissionNotModifiableException()))
                    .isTrue();
        }

        @Test
        @DisplayName("SystemPermissionNotDeletableException을 지원한다")
        void shouldSupportSystemPermissionNotDeletableException() {
            assertThat(
                            mapper.supports(
                                    ErrorMapperApiFixture.systemPermissionNotDeletableException()))
                    .isTrue();
        }

        @Test
        @DisplayName("PermissionInUseException을 지원한다")
        void shouldSupportPermissionInUseException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.permissionInUseException())).isTrue();
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
        @DisplayName("PermissionNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapPermissionNotFoundTo404() {
            var ex = ErrorMapperApiFixture.permissionNotFoundException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("DuplicatePermissionKeyException을 409 Conflict로 매핑한다")
        void shouldMapDuplicatePermissionKeyTo409() {
            var ex = ErrorMapperApiFixture.duplicatePermissionKeyException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("SystemPermissionNotModifiableException을 403 Forbidden으로 매핑한다")
        void shouldMapSystemPermissionNotModifiableTo403() {
            var ex = ErrorMapperApiFixture.systemPermissionNotModifiableException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("System Permission Not Modifiable");
        }

        @Test
        @DisplayName("SystemPermissionNotDeletableException을 403 Forbidden으로 매핑한다")
        void shouldMapSystemPermissionNotDeletableTo403() {
            var ex = ErrorMapperApiFixture.systemPermissionNotDeletableException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("System Permission Not Deletable");
        }

        @Test
        @DisplayName("PermissionInUseException을 409 Conflict로 매핑한다")
        void shouldMapPermissionInUseTo409() {
            var ex = ErrorMapperApiFixture.permissionInUseException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Permission In Use");
        }
    }
}
