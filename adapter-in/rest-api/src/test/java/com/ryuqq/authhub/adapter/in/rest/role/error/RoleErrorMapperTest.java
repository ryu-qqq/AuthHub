package com.ryuqq.authhub.adapter.in.rest.role.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.exception.RoleErrorCode;

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

    private MessageSource messageSource;
    private RoleErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        errorMapper = new RoleErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("ROLE- 접두사를 가진 코드는 지원한다")
        void givenRolePrefix_whenSupports_thenReturnsTrue() {
            assertThat(errorMapper.supports("ROLE-001")).isTrue();
            assertThat(errorMapper.supports("ROLE-007")).isTrue();
        }

        @Test
        @DisplayName("ROLE- 접두사가 없는 코드는 지원하지 않는다")
        void givenNonRolePrefix_whenSupports_thenReturnsFalse() {
            assertThat(errorMapper.supports("USER-001")).isFalse();
            assertThat(errorMapper.supports("AUTH-001")).isFalse();
        }

        @Test
        @DisplayName("null 코드는 지원하지 않는다")
        void givenNullCode_whenSupports_thenReturnsFalse() {
            assertThat(errorMapper.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("map() HttpStatus 매핑 테스트")
    class HttpStatusMappingTest {

        @Test
        @DisplayName("ROLE_NOT_FOUND (ROLE-001)는 404 Not Found로 매핑된다")
        void givenRoleNotFound_whenMap_thenReturns404() {
            // given
            DomainException ex = new DomainException(RoleErrorCode.ROLE_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("DUPLICATE_ROLE_NAME (ROLE-003)는 409 Conflict로 매핑된다")
        void givenDuplicateRoleName_whenMap_thenReturns409() {
            // given
            DomainException ex = new DomainException(RoleErrorCode.DUPLICATE_ROLE_NAME);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("SYSTEM_ROLE_MODIFICATION_NOT_ALLOWED (ROLE-005)는 403 Forbidden으로 매핑된다")
        void givenSystemRoleModificationNotAllowed_whenMap_thenReturns403() {
            // given
            DomainException ex = new DomainException(RoleErrorCode.SYSTEM_ROLE_MODIFICATION_NOT_ALLOWED);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("map() Type URI 생성 테스트")
    class TypeUriTest {

        @Test
        @DisplayName("Type URI가 올바르게 생성된다")
        void givenRoleNotFound_whenMap_thenGeneratesCorrectTypeUri() {
            // given
            DomainException ex = new DomainException(RoleErrorCode.ROLE_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            URI expected = URI.create("https://api.authhub.com/problems/role/role-001");
            assertThat(mapped.type()).isEqualTo(expected);
        }
    }

    private void stubMessageSource() {
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenReturn("Test Message");
    }
}
