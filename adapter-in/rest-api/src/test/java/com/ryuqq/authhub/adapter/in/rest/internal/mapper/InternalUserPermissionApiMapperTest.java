package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalUserPermissionApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InternalUserPermissionApiMapper 단위 테스트")
class InternalUserPermissionApiMapperTest {

    private InternalUserPermissionApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalUserPermissionApiMapper();
    }

    @Nested
    @DisplayName("toApiResponse 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("UserPermissionsResult를 UserPermissionsApiResponse로 정상 변환한다")
        void shouldConvertToUserPermissionsApiResponse() {
            // Given
            String userId = InternalApiFixture.defaultUserId();
            Set<String> roles = InternalApiFixture.defaultRoles();
            Set<String> permissions = InternalApiFixture.defaultPermissions();
            UserPermissionsResult result = new UserPermissionsResult(userId, roles, permissions);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).isEqualTo(roles);
            assertThat(response.permissions()).isEqualTo(permissions);
        }

        @Test
        @DisplayName("빈 roles와 permissions를 가진 UserPermissionsResult를 변환한다")
        void shouldConvertEmptyRolesAndPermissions() {
            // Given
            String userId = InternalApiFixture.defaultUserId();
            UserPermissionsResult result = UserPermissionsResult.empty(userId);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).isEmpty();
            assertThat(response.permissions()).isEmpty();
        }

        @Test
        @DisplayName("result가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResultIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toApiResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
