package com.ryuqq.authhub.adapter.in.rest.user.error;

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
 * UserErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("UserErrorMapper 테스트")
class UserErrorMapperTest {

    private UserErrorMapper mapper;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @BeforeEach
    void setUp() {
        mapper = new UserErrorMapper();
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("USER-001 코드 지원")
        void givenUser001Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-001")).isTrue();
        }

        @Test
        @DisplayName("USER-002 코드 지원")
        void givenUser002Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-002")).isTrue();
        }

        @Test
        @DisplayName("USER-003 코드 지원")
        void givenUser003Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-003")).isTrue();
        }

        @Test
        @DisplayName("USER-004 코드 지원")
        void givenUser004Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-004")).isTrue();
        }

        @Test
        @DisplayName("USER-005 코드 지원")
        void givenUser005Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-005")).isTrue();
        }

        @Test
        @DisplayName("USER-006 코드 지원")
        void givenUser006Code_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("USER-006")).isTrue();
        }

        @Test
        @DisplayName("지원하지 않는 코드는 false 반환")
        void givenUnsupportedCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports("ROLE-001")).isFalse();
            assertThat(mapper.supports("AUTH-001")).isFalse();
            assertThat(mapper.supports("UNKNOWN")).isFalse();
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
        @DisplayName("USER-001 → 404 Not Found")
        void givenUser001Exception_whenMap_thenReturnsNotFound() {
            // given
            DomainException ex = new DomainException("USER-001", "사용자를 찾을 수 없습니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("User Not Found");
            assertThat(error.detail()).isEqualTo("사용자를 찾을 수 없습니다.");
            assertThat(error.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-not-found");
        }

        @Test
        @DisplayName("USER-002 → 400 Bad Request")
        void givenUser002Exception_whenMap_thenReturnsBadRequest() {
            // given
            DomainException ex = new DomainException("USER-002", "잘못된 사용자 상태입니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("Invalid User State");
            assertThat(error.detail()).isEqualTo("잘못된 사용자 상태입니다.");
        }

        @Test
        @DisplayName("USER-003 → 409 Conflict")
        void givenUser003Exception_whenMap_thenReturnsConflict() {
            // given
            DomainException ex = new DomainException("USER-003", "사용자 식별자가 중복됩니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Duplicate User Identifier");
        }

        @Test
        @DisplayName("USER-004 → 404 Not Found")
        void givenUser004Exception_whenMap_thenReturnsNotFound() {
            // given
            DomainException ex = new DomainException("USER-004", "사용자 역할을 찾을 수 없습니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("User Role Not Found");
        }

        @Test
        @DisplayName("USER-005 → 409 Conflict")
        void givenUser005Exception_whenMap_thenReturnsConflict() {
            // given
            DomainException ex = new DomainException("USER-005", "사용자 역할이 중복됩니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Duplicate User Role");
        }

        @Test
        @DisplayName("USER-006 → 400 Bad Request")
        void givenUser006Exception_whenMap_thenReturnsBadRequest() {
            // given
            DomainException ex = new DomainException("USER-006", "잘못된 비밀번호입니다.");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("Invalid Password");
        }

        @Test
        @DisplayName("알 수 없는 USER 코드 → 500 Internal Server Error")
        void givenUnknownUserCode_whenMap_thenReturnsInternalServerError() {
            // given
            DomainException ex = new DomainException("USER-999", "알 수 없는 오류");

            // when
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            // then
            assertThat(error.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(error.title()).isEqualTo("Internal Server Error");
        }
    }
}
