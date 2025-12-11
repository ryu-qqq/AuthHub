package com.ryuqq.authhub.adapter.in.rest.permission.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * PermissionErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("PermissionErrorMapper 테스트")
class PermissionErrorMapperTest {

    private PermissionErrorMapper mapper;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @BeforeEach
    void setUp() {
        mapper = new PermissionErrorMapper();
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("PERMISSION-001 ~ PERMISSION-005 코드 지원")
        void givenPermissionCodes_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("PERMISSION-001")).isTrue();
            assertThat(mapper.supports("PERMISSION-002")).isTrue();
            assertThat(mapper.supports("PERMISSION-003")).isTrue();
            assertThat(mapper.supports("PERMISSION-004")).isTrue();
            assertThat(mapper.supports("PERMISSION-005")).isTrue();
        }

        @Test
        @DisplayName("지원하지 않는 코드는 false 반환")
        void givenUnsupportedCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports("USER-001")).isFalse();
            assertThat(mapper.supports("ROLE-001")).isFalse();
        }

        @Test
        @DisplayName("null 코드는 false 반환")
        void givenNullCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("map() 테스트")
    class MapTest {

        @Test
        @DisplayName("PERMISSION-001 → 404 Not Found")
        void givenPermission001Exception_whenMap_thenReturnsNotFound() {
            DomainException ex = new DomainException("PERMISSION-001", "권한을 찾을 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("Permission Not Found");
        }

        @Test
        @DisplayName("PERMISSION-002 → 409 Conflict")
        void givenPermission002Exception_whenMap_thenReturnsConflict() {
            DomainException ex = new DomainException("PERMISSION-002", "권한 키가 중복됩니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Duplicate Permission Key");
        }

        @Test
        @DisplayName("PERMISSION-003 → 400 Bad Request")
        void givenPermission003Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("PERMISSION-003", "시스템 권한은 수정할 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("System Permission Not Modifiable");
        }

        @Test
        @DisplayName("PERMISSION-004 → 400 Bad Request")
        void givenPermission004Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("PERMISSION-004", "시스템 권한은 삭제할 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("System Permission Not Deletable");
        }

        @Test
        @DisplayName("PERMISSION-005 → 400 Bad Request")
        void givenPermission005Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("PERMISSION-005", "잘못된 권한 키 형식입니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("Invalid Permission Key Format");
        }

        @Test
        @DisplayName("알 수 없는 PERMISSION 코드 → 500 Internal Server Error")
        void givenUnknownPermissionCode_whenMap_thenReturnsInternalServerError() {
            DomainException ex = new DomainException("PERMISSION-999", "알 수 없는 오류");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(error.title()).isEqualTo("Internal Server Error");
        }
    }
}
