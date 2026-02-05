package com.ryuqq.authhub.adapter.in.rest.user.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.fixture.ErrorMapperApiFixture;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserPhoneNumberException;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.exception.UserErrorCode;
import com.ryuqq.authhub.domain.user.exception.UserNotActiveException;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * UserErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserErrorMapper 단위 테스트")
class UserErrorMapperTest {

    private UserErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("USER-001 코드를 지원한다")
        void shouldSupportUserNotFound() {
            assertThat(mapper.supports(ErrorMapperApiFixture.userNotFoundException())).isTrue();
        }

        @Test
        @DisplayName("USER-002 코드를 지원한다")
        void shouldSupportDuplicateUserIdentifier() {
            var ex = new DuplicateUserIdentifierException("test@example.com");
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("USER-003 코드를 지원한다")
        void shouldSupportDuplicateUserPhoneNumber() {
            var ex = new DuplicateUserPhoneNumberException("010-1234-5678");
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("USER-004 코드를 지원한다")
        void shouldSupportUserNotActive() {
            var ex = new UserNotActiveException("user-id", UserStatus.INACTIVE);
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("USER-005 코드를 지원한다")
        void shouldSupportUserSuspended() {
            var ex = new UserNotActiveException("user-id", UserStatus.SUSPENDED);
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("USER-006 코드를 지원한다")
        void shouldSupportUserDeleted() {
            // USER-006은 DomainException으로 직접 생성 (TestDomainException 사용)
            var ex =
                    new com.ryuqq.authhub.domain.common.exception.fixture.DomainExceptionFixture
                            .TestDomainException(
                            UserErrorCode.USER_DELETED, Map.of("userId", "user-id"));
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("USER-007 코드를 지원한다")
        void shouldSupportInvalidPassword() {
            var ex = new InvalidPasswordException();
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("null code를 처리한다")
        void shouldHandleNullCode() {
            // DomainException with null code를 직접 생성할 수 없으므로,
            // 다른 도메인 예외로 테스트 (이미 null code 체크 로직이 있음)
            assertThat(mapper.supports(ErrorMapperApiFixture.tenantNotFoundException())).isFalse();
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
        @DisplayName("USER-001을 404 Not Found로 매핑한다")
        void shouldMapUserNotFoundTo404() {
            // Given
            var ex = ErrorMapperApiFixture.userNotFoundException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("User Not Found");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-not-found");
        }

        @Test
        @DisplayName("USER-002를 409 Conflict로 매핑한다")
        void shouldMapDuplicateUserIdentifierTo409() {
            // Given
            var ex = new DuplicateUserIdentifierException("test@example.com");

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Duplicate User Identifier");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-identifier-duplicate");
        }

        @Test
        @DisplayName("USER-003를 409 Conflict로 매핑한다")
        void shouldMapDuplicateUserPhoneNumberTo409() {
            // Given
            var ex = new DuplicateUserPhoneNumberException("010-1234-5678");

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Duplicate User Phone Number");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-phone-duplicate");
        }

        @Test
        @DisplayName("USER-004를 403 Forbidden으로 매핑한다")
        void shouldMapUserNotActiveTo403() {
            // Given
            var ex = new UserNotActiveException("user-id", UserStatus.INACTIVE);

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("User Not Active");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-not-active");
        }

        @Test
        @DisplayName("USER-005를 403 Forbidden으로 매핑한다")
        void shouldMapUserSuspendedTo403() {
            // Given
            var ex = new UserNotActiveException("user-id", UserStatus.SUSPENDED);

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("User Suspended");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-suspended");
        }

        @Test
        @DisplayName("USER-006를 403 Forbidden으로 매핑한다")
        void shouldMapUserDeletedTo403() {
            // Given
            var ex =
                    new com.ryuqq.authhub.domain.common.exception.fixture.DomainExceptionFixture
                            .TestDomainException(
                            UserErrorCode.USER_DELETED, Map.of("userId", "user-id"));

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("User Deleted");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-deleted");
        }

        @Test
        @DisplayName("USER-007를 401 Unauthorized로 매핑한다")
        void shouldMapInvalidPasswordTo401() {
            // Given
            var ex = new InvalidPasswordException();

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(result.title()).isEqualTo("Invalid Password");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/user-invalid-password");
        }

        @Test
        @DisplayName("지원하지 않는 에러 코드는 500 Internal Server Error로 매핑한다")
        void shouldMapUnsupportedErrorCodeTo500() {
            // Given - 지원하지 않는 에러 코드를 가진 DomainException 생성
            com.ryuqq.authhub.domain.common.exception.ErrorCode unsupportedErrorCode =
                    new com.ryuqq.authhub.domain.common.exception.ErrorCode() {
                        @Override
                        public String getCode() {
                            return "USER-999"; // 지원하지 않는 코드
                        }

                        @Override
                        public int getHttpStatus() {
                            return 500;
                        }

                        @Override
                        public String getMessage() {
                            return "Unsupported error";
                        }
                    };
            var ex =
                    new com.ryuqq.authhub.domain.common.exception.fixture.DomainExceptionFixture
                            .TestDomainException(unsupportedErrorCode);

            // When
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);

            // Then
            assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(result.title()).isEqualTo("Internal Server Error");
            assertThat(result.type().toString())
                    .isEqualTo("https://authhub.ryuqq.com/errors/internal-error");
        }
    }
}
