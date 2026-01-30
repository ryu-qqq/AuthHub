package com.ryuqq.authhub.adapter.in.rest.rolepermission.error;

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
 * RolePermissionErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RolePermissionErrorMapper 단위 테스트")
class RolePermissionErrorMapperTest {

    private RolePermissionErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RolePermissionErrorMapper();
    }

    @Nested
    @DisplayName("supports() 메서드는")
    class SupportsMethod {

        @Test
        @DisplayName("RolePermissionNotFoundException을 지원한다")
        void shouldSupportRolePermissionNotFoundException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.rolePermissionNotFoundException()))
                    .isTrue();
        }

        @Test
        @DisplayName("DuplicateRolePermissionException을 지원한다")
        void shouldSupportDuplicateRolePermissionException() {
            assertThat(mapper.supports(ErrorMapperApiFixture.duplicateRolePermissionException()))
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
        @DisplayName("RolePermissionNotFoundException을 404 Not Found로 매핑한다")
        void shouldMapNotFoundTo404() {
            var ex = ErrorMapperApiFixture.rolePermissionNotFoundException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("DuplicateRolePermissionException을 409 Conflict로 매핑한다")
        void shouldMapDuplicateTo409() {
            var ex = ErrorMapperApiFixture.duplicateRolePermissionException();
            ErrorMapper.MappedError result = mapper.map(ex, Locale.KOREA);
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
        }
    }
}
