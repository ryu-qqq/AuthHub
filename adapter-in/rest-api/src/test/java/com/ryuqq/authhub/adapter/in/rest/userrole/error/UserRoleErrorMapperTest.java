package com.ryuqq.authhub.adapter.in.rest.userrole.error;

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
 * UserRoleErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserRoleErrorMapper 단위 테스트")
class UserRoleErrorMapperTest {

    private UserRoleErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserRoleErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("USER_ROLE-001 코드를 지원한다")
        void shouldSupportUserRoleNotFound() {
            assertThat(mapper.supports(ErrorMapperApiFixture.userRoleNotFoundException())).isTrue();
        }

        @Test
        @DisplayName("USER_ROLE-002 코드를 지원한다")
        void shouldSupportDuplicateUserRole() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicateUserRoleException()))
                    .isTrue();
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
        @DisplayName("USER_ROLE-001을 404 Not Found로 매핑한다")
        void shouldMapUserRoleNotFoundTo404() {
            var ex = ErrorMapperApiFixture.userRoleNotFoundException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("USER_ROLE-002를 409 Conflict로 매핑한다")
        void shouldMapDuplicateUserRoleTo409() {
            var ex = ErrorMapperApiFixture.duplicateUserRoleException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
        }
    }
}
