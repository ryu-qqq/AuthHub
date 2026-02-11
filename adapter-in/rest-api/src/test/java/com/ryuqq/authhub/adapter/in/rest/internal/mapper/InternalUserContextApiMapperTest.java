package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.UserContextApiResponse;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalUserContextApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InternalUserContextApiMapper 테스트")
class InternalUserContextApiMapperTest {

    @InjectMocks private InternalUserContextApiMapper mapper;

    @Test
    @DisplayName("MyContextResponse를 UserContextApiResponse로 올바르게 변환한다")
    void shouldMapMyContextResponseToUserContextApiResponse() {
        // given
        MyContextResponse response =
                new MyContextResponse(
                        "user-001",
                        "test@example.com",
                        "테스트 사용자",
                        "tenant-001",
                        "테스트 테넌트",
                        "org-001",
                        "테스트 조직",
                        "010-1234-5678",
                        List.of(
                                new MyContextResponse.RoleInfo("role-001", "ADMIN"),
                                new MyContextResponse.RoleInfo("role-002", "USER")),
                        List.of("user:read", "user:write"));

        // when
        UserContextApiResponse result = mapper.toApiResponse(response);

        // then
        assertThat(result.userId()).isEqualTo("user-001");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("테스트 사용자");
        assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
        assertThat(result.tenant().id()).isEqualTo("tenant-001");
        assertThat(result.tenant().name()).isEqualTo("테스트 테넌트");
        assertThat(result.organization().id()).isEqualTo("org-001");
        assertThat(result.organization().name()).isEqualTo("테스트 조직");
        assertThat(result.roles()).hasSize(2);
        assertThat(result.roles().get(0).id()).isEqualTo("role-001");
        assertThat(result.roles().get(0).name()).isEqualTo("ADMIN");
        assertThat(result.roles().get(1).id()).isEqualTo("role-002");
        assertThat(result.roles().get(1).name()).isEqualTo("USER");
        assertThat(result.permissions()).containsExactlyInAnyOrder("user:read", "user:write");
    }

    @Test
    @DisplayName("역할/권한이 비어있는 경우에도 올바르게 변환한다")
    void shouldMapEmptyRolesAndPermissions() {
        // given
        MyContextResponse response =
                new MyContextResponse(
                        "user-002",
                        "empty@example.com",
                        "빈 사용자",
                        "tenant-001",
                        "테스트 테넌트",
                        "org-001",
                        "테스트 조직",
                        null,
                        List.of(),
                        List.of());

        // when
        UserContextApiResponse result = mapper.toApiResponse(response);

        // then
        assertThat(result.userId()).isEqualTo("user-002");
        assertThat(result.phoneNumber()).isNull();
        assertThat(result.roles()).isEmpty();
        assertThat(result.permissions()).isEmpty();
    }
}
