package com.ryuqq.authhub.adapter.in.rest.role.error;

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
 * RoleErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RoleErrorMapper 단위 테스트")
class RoleErrorMapperTest {

    private RoleErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RoleErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("RoleNotFoundException을 지원한다")
        void shouldSupportRoleNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.roleNotFoundException())).isTrue();
        }

        @Test
        @DisplayName("DuplicateRoleNameException을 지원한다")
        void shouldSupportDuplicateRoleNameException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicateRoleNameException()))
                    .isTrue();
        }

        @Test
        @DisplayName("SystemRoleNotModifiableException을 지원한다")
        void shouldSupportSystemRoleNotModifiableException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.systemRoleNotModifiableException()))
                    .isTrue();
        }

        @Test
        @DisplayName("SystemRoleNotDeletableException을 지원한다")
        void shouldSupportSystemRoleNotDeletableException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.systemRoleNotDeletableException()))
                    .isTrue();
        }

        @Test
        @DisplayName("RoleInUseException을 지원한다")
        void shouldSupportRoleInUseException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.roleInUseException())).isTrue();
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
        @DisplayName("RoleNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapRoleNotFoundTo404() {
            // Given
            var ex = ErrorMapperApiFixture.roleNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Role Not Found");
        }

        @Test
        @DisplayName("DuplicateRoleNameException을 409 Conflict로 매핑한다")
        void shouldMapDuplicateRoleNameTo409() {
            // Given
            var ex = ErrorMapperApiFixture.duplicateRoleNameException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Role Name Duplicate");
        }

        @Test
        @DisplayName("SystemRoleNotModifiableException을 403 Forbidden으로 매핑한다")
        void shouldMapSystemRoleNotModifiableTo403() {
            // Given
            var ex = ErrorMapperApiFixture.systemRoleNotModifiableException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("System Role Not Modifiable");
        }

        @Test
        @DisplayName("SystemRoleNotDeletableException을 403 Forbidden으로 매핑한다")
        void shouldMapSystemRoleNotDeletableTo403() {
            // Given
            var ex = ErrorMapperApiFixture.systemRoleNotDeletableException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("System Role Not Deletable");
        }

        @Test
        @DisplayName("RoleInUseException을 409 Conflict로 매핑한다")
        void shouldMapRoleInUseTo409() {
            // Given
            var ex = ErrorMapperApiFixture.roleInUseException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Role In Use");
        }
    }
}
