package com.ryuqq.authhub.adapter.in.rest.organization.error;

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
 * OrganizationErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("OrganizationErrorMapper 테스트")
class OrganizationErrorMapperTest {

    private OrganizationErrorMapper mapper;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @BeforeEach
    void setUp() {
        mapper = new OrganizationErrorMapper();
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("ORG-001 ~ ORG-003 코드 지원")
        void givenOrgCodes_whenSupports_thenReturnsTrue() {
            assertThat(mapper.supports("ORG-001")).isTrue();
            assertThat(mapper.supports("ORG-002")).isTrue();
            assertThat(mapper.supports("ORG-003")).isTrue();
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
        @DisplayName("ORG-001 → 404 Not Found")
        void givenOrg001Exception_whenMap_thenReturnsNotFound() {
            DomainException ex = new DomainException("ORG-001", "조직을 찾을 수 없습니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("Organization Not Found");
        }

        @Test
        @DisplayName("ORG-002 → 400 Bad Request")
        void givenOrg002Exception_whenMap_thenReturnsBadRequest() {
            DomainException ex = new DomainException("ORG-002", "잘못된 조직 상태 전환입니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(error.title()).isEqualTo("Invalid Organization State Transition");
        }

        @Test
        @DisplayName("ORG-003 → 409 Conflict")
        void givenOrg003Exception_whenMap_thenReturnsConflict() {
            DomainException ex = new DomainException("ORG-003", "조직 이름이 중복됩니다.");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Organization Name Duplicate");
        }

        @Test
        @DisplayName("알 수 없는 ORG 코드 → 500 Internal Server Error")
        void givenUnknownOrgCode_whenMap_thenReturnsInternalServerError() {
            DomainException ex = new DomainException("ORG-999", "알 수 없는 오류");
            MappedError error = mapper.map(ex, DEFAULT_LOCALE);

            assertThat(error.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(error.title()).isEqualTo("Internal Server Error");
        }
    }
}
