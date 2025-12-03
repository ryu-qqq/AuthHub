package com.ryuqq.authhub.adapter.in.rest.organization.error;

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
import com.ryuqq.authhub.domain.organization.exception.OrganizationErrorCode;

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

    private MessageSource messageSource;
    private OrganizationErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        errorMapper = new OrganizationErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("ORGANIZATION- 접두사를 가진 코드는 지원한다")
        void givenOrganizationPrefix_whenSupports_thenReturnsTrue() {
            assertThat(errorMapper.supports("ORGANIZATION-001")).isTrue();
            assertThat(errorMapper.supports("ORGANIZATION-005")).isTrue();
        }

        @Test
        @DisplayName("ORGANIZATION- 접두사가 없는 코드는 지원하지 않는다")
        void givenNonOrganizationPrefix_whenSupports_thenReturnsFalse() {
            assertThat(errorMapper.supports("USER-001")).isFalse();
            assertThat(errorMapper.supports("TENANT-001")).isFalse();
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
        @DisplayName("ORGANIZATION_NOT_FOUND (ORGANIZATION-001)는 404 Not Found로 매핑된다")
        void givenOrganizationNotFound_whenMap_thenReturns404() {
            // given
            DomainException ex = new DomainException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("ACTIVE_USERS_EXIST (ORGANIZATION-005)는 409 Conflict로 매핑된다")
        void givenActiveUsersExist_whenMap_thenReturns409() {
            // given
            DomainException ex = new DomainException(OrganizationErrorCode.ACTIVE_USERS_EXIST);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("INVALID_ORGANIZATION_NAME (ORGANIZATION-003)은 400 Bad Request로 매핑된다")
        void givenInvalidOrganizationName_whenMap_thenReturns400() {
            // given
            DomainException ex = new DomainException(OrganizationErrorCode.INVALID_ORGANIZATION_NAME);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("map() Type URI 생성 테스트")
    class TypeUriTest {

        @Test
        @DisplayName("Type URI가 올바르게 생성된다")
        void givenOrganizationNotFound_whenMap_thenGeneratesCorrectTypeUri() {
            // given
            DomainException ex = new DomainException(OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            URI expected = URI.create("https://api.authhub.com/problems/organization/organization-001");
            assertThat(mapped.type()).isEqualTo(expected);
        }
    }

    private void stubMessageSource() {
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenReturn("Test Message");
    }
}
