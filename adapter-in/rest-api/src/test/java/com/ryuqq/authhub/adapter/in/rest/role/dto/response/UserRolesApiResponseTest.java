package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;

/**
 * UserRolesApiResponse 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 생성 및 접근자</li>
 *   <li>from() 팩토리 메서드</li>
 *   <li>equals/hashCode</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("UserRolesApiResponse 테스트")
class UserRolesApiResponseTest {

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicBehaviorTest {

        @Test
        @DisplayName("모든 필드가 주어지면 Record가 정상 생성된다")
        void givenAllFields_whenCreate_thenRecordCreated() {
            // given
            UUID userId = UUID.randomUUID();
            Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
            Set<String> permissions = Set.of("user:read", "user:write");

            // when
            UserRolesApiResponse response = new UserRolesApiResponse(userId, roles, permissions);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.roles()).containsExactlyInAnyOrderElementsOf(roles);
            assertThat(response.permissions()).containsExactlyInAnyOrderElementsOf(permissions);
        }

        @Test
        @DisplayName("동일한 값을 가진 두 Record는 동등하다")
        void givenSameValues_whenCompare_thenEqual() {
            // given
            UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("user:read");

            UserRolesApiResponse response1 = new UserRolesApiResponse(userId, roles, permissions);
            UserRolesApiResponse response2 = new UserRolesApiResponse(userId, roles, permissions);

            // then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("from() 팩토리 메서드 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("UserRolesResponse로부터 정상적으로 변환된다")
        void givenUseCaseResponse_whenFrom_thenCorrectlyMapped() {
            // given
            UUID userId = UUID.randomUUID();
            Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
            Set<String> permissions = Set.of("user:read", "user:write", "admin:read");
            UserRolesResponse useCaseResponse = new UserRolesResponse(userId, roles, permissions);

            // when
            UserRolesApiResponse apiResponse = UserRolesApiResponse.from(useCaseResponse);

            // then
            assertThat(apiResponse.userId()).isEqualTo(userId);
            assertThat(apiResponse.roles()).containsExactlyInAnyOrderElementsOf(roles);
            assertThat(apiResponse.permissions()).containsExactlyInAnyOrderElementsOf(permissions);
        }

        @Test
        @DisplayName("빈 roles와 permissions도 정상적으로 변환된다")
        void givenEmptyRolesAndPermissions_whenFrom_thenCorrectlyMapped() {
            // given
            UUID userId = UUID.randomUUID();
            Set<String> emptyRoles = Set.of();
            Set<String> emptyPermissions = Set.of();
            UserRolesResponse useCaseResponse = new UserRolesResponse(userId, emptyRoles, emptyPermissions);

            // when
            UserRolesApiResponse apiResponse = UserRolesApiResponse.from(useCaseResponse);

            // then
            assertThat(apiResponse.userId()).isEqualTo(userId);
            assertThat(apiResponse.roles()).isEmpty();
            assertThat(apiResponse.permissions()).isEmpty();
        }
    }
}
