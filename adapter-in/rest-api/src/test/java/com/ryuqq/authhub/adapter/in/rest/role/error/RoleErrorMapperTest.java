package com.ryuqq.authhub.adapter.in.rest.role.error;

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
 * RoleErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("RoleErrorMapper 테스트")
class RoleErrorMapperTest {

    private RoleErrorMapper mapper;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @BeforeEach
    void setUp() {
        mapper = new RoleErrorMapper();
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("ROLE-001 ~ ROLE-007 코드 지원")
        void givenRoleCodes_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("ROLE-001")).isTrue();
            assertThat(mapper.supports("ROLE-002")).isTrue();
            assertThat(mapper.supports("ROLE-003")).isTrue();
            assertThat(mapper.supports("ROLE-004")).isTrue();
            assertThat(mapper.supports("ROLE-005")).isTrue();
            assertThat(mapper.supports("ROLE-006")).isTrue();
            assertThat(mapper.supports("ROLE-007")).isTrue();
        }

        @Test
        @DisplayName("지원하지 않는 코드는 false 반환")
        void givenUnsupportedCode_whenSupports_thenReturnsFalse() {
            assertThat(mapper.supports("USER-001")).isFalse();
            assertThat(mapper.supports("AUTH-001")).isFalse();
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
        @DisplayName("ROLE-001 → 404 Not Found")
        void givenRole001Exception_whenMap_thenReturnsNotFound() {
            DomainException ex = new DomainException("ROLE-001", "역할을 찾을 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("Role Not Found");
        }

        @Test
        @DisplayName("ROLE-002 → 409 Conflict")
        void givenRole002Exception_whenMap_thenReturnsConflict() {
            DomainException ex = new DomainException("ROLE-002", "역할 이름이 중복됩니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Duplicate Role Name");
        }

        @Test
        @DisplayName("ROLE-003 → 400 Bad Request")
        void givenRole003Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("ROLE-003", "시스템 역할은 수정할 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("System Role Not Modifiable");
        }

        @Test
        @DisplayName("ROLE-004 → 400 Bad Request")
        void givenRole004Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("ROLE-004", "시스템 역할은 삭제할 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("System Role Not Deletable");
        }

        @Test
        @DisplayName("ROLE-005 → 400 Bad Request")
        void givenRole005Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("ROLE-005", "잘못된 역할 범위입니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("Invalid Role Scope");
        }

        @Test
        @DisplayName("ROLE-006 → 404 Not Found")
        void givenRole006Exception_whenMap_thenReturnsNotFound() {
            DomainException ex = new DomainException("ROLE-006", "역할 권한을 찾을 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("Role Permission Not Found");
        }

        @Test
        @DisplayName("ROLE-007 → 409 Conflict")
        void givenRole007Exception_whenMap_thenReturnsConflict() {
            DomainException ex = new DomainException("ROLE-007", "역할 권한이 중복됩니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Duplicate Role Permission");
        }

        @Test
        @DisplayName("알 수 없는 ROLE 코드 → 500 Internal Server Error")
        void givenUnknownRoleCode_whenMap_thenReturnsInternalServerError() {
            DomainException ex = new DomainException("ROLE-999", "알 수 없는 오류");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(error.title()).isEqualTo("Internal Server Error");
        }
    }
}
