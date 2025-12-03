package com.ryuqq.authhub.adapter.in.rest.user.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.exception.UserErrorCode;

/**
 * UserErrorMapper 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>supports() - PREFIX 기반 지원 여부</li>
 *   <li>map() - HttpStatus, Title, Detail, Type URI 매핑</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("error-mapper")
@DisplayName("UserErrorMapper 테스트")
class UserErrorMapperTest {

    private MessageSource messageSource;
    private UserErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        errorMapper = new UserErrorMapper(messageSource);
    }

    @Nested
    @DisplayName("supports() 테스트")
    class SupportsTest {

        @Test
        @DisplayName("USER- 접두사를 가진 코드는 지원한다")
        void givenUserPrefix_whenSupports_thenReturnsTrue() {
            // when & then
            assertThat(errorMapper.supports("USER-001")).isTrue();
            assertThat(errorMapper.supports("USER-002")).isTrue();
            assertThat(errorMapper.supports("USER-009")).isTrue();
        }

        @Test
        @DisplayName("USER- 접두사가 없는 코드는 지원하지 않는다")
        void givenNonUserPrefix_whenSupports_thenReturnsFalse() {
            // when & then
            assertThat(errorMapper.supports("TENANT-001")).isFalse();
            assertThat(errorMapper.supports("AUTH-001")).isFalse();
            assertThat(errorMapper.supports("ROLE-001")).isFalse();
        }

        @Test
        @DisplayName("null 코드는 지원하지 않는다")
        void givenNullCode_whenSupports_thenReturnsFalse() {
            // when & then
            assertThat(errorMapper.supports(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("map() HttpStatus 매핑 테스트")
    class HttpStatusMappingTest {

        @Test
        @DisplayName("USER_NOT_FOUND (USER-001)는 404 Not Found로 매핑된다")
        void givenUserNotFound_whenMap_thenReturns404() {
            // given
            DomainException ex = new DomainException(UserErrorCode.USER_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("DUPLICATE_EMAIL (USER-006)은 409 Conflict로 매핑된다")
        void givenDuplicateEmail_whenMap_thenReturns409() {
            // given
            DomainException ex = new DomainException(UserErrorCode.DUPLICATE_EMAIL);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.status()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("INVALID_USER_TYPE (USER-003)은 400 Bad Request로 매핑된다")
        void givenInvalidUserType_whenMap_thenReturns400() {
            // given
            DomainException ex = new DomainException(UserErrorCode.INVALID_USER_TYPE);
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
        @DisplayName("Type URI는 소문자, 하이픈 구분으로 생성된다")
        void givenUserNotFound_whenMap_thenGeneratesCorrectTypeUri() {
            // given
            DomainException ex = new DomainException(UserErrorCode.USER_NOT_FOUND);
            stubMessageSource();

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            URI expected = URI.create("https://api.authhub.com/problems/user/user-001");
            assertThat(mapped.type()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("map() MessageSource 통합 테스트")
    class MessageSourceTest {

        @Test
        @DisplayName("MessageSource에서 Title과 Detail을 조회한다")
        void givenException_whenMap_thenResolvesMessages() {
            // given
            DomainException ex = new DomainException(
                    UserErrorCode.USER_NOT_FOUND,
                    Map.of("userId", "123")
            );

            when(messageSource.getMessage(
                    anyString(),
                    any(),
                    anyString(),
                    any(Locale.class)
            )).thenReturn("사용자를 찾을 수 없습니다");

            // when
            ErrorMapper.MappedError mapped = errorMapper.map(ex, Locale.KOREAN);

            // then
            assertThat(mapped.title()).isEqualTo("사용자를 찾을 수 없습니다");
            assertThat(mapped.detail()).isEqualTo("사용자를 찾을 수 없습니다");
        }
    }

    private void stubMessageSource() {
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenReturn("Test Message");
    }
}
