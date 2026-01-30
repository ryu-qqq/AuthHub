package com.ryuqq.authhub.application.token.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * MyContextCompositeAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("MyContextCompositeAssembler 단위 테스트")
class MyContextCompositeAssemblerTest {

    private MyContextCompositeAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new MyContextCompositeAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponse {

        @Test
        @DisplayName("성공: Composite의 모든 필드가 Response로 올바르게 매핑됨")
        void shouldMapAllFields_FromCompositeToResponse() {
            // given
            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId("user-123")
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-456")
                            .tenantName("Test Tenant")
                            .organizationId("org-789")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions =
                    new RolesAndPermissionsComposite(
                            Set.of("ADMIN", "USER"), Set.of("user:read", "user:write"));

            MyContextComposite composite = new MyContextComposite(userContext, rolesAndPermissions);

            // when
            MyContextResponse result = sut.toResponse(composite);

            // then
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.name()).isEqualTo("Test User");
            assertThat(result.tenantId()).isEqualTo("tenant-456");
            assertThat(result.tenantName()).isEqualTo("Test Tenant");
            assertThat(result.organizationId()).isEqualTo("org-789");
            assertThat(result.organizationName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("역할 목록이 RoleInfo 리스트로 올바르게 변환됨")
        void shouldMapRoles_ToRoleInfoList() {
            // given
            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId("user-123")
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-456")
                            .tenantName("Test Tenant")
                            .organizationId("org-789")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions =
                    new RolesAndPermissionsComposite(Set.of("ADMIN", "USER"), Set.of("user:read"));

            MyContextComposite composite = new MyContextComposite(userContext, rolesAndPermissions);

            // when
            MyContextResponse result = sut.toResponse(composite);

            // then
            assertThat(result.roles()).hasSize(2);
            assertThat(result.roles())
                    .extracting(MyContextResponse.RoleInfo::name)
                    .containsExactlyInAnyOrder("ADMIN", "USER");
        }

        @Test
        @DisplayName("권한 키 목록이 List로 올바르게 변환됨")
        void shouldMapPermissions_ToList() {
            // given
            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId("user-123")
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-456")
                            .tenantName("Test Tenant")
                            .organizationId("org-789")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions =
                    new RolesAndPermissionsComposite(
                            Set.of("ADMIN"), Set.of("user:read", "user:write", "role:read"));

            MyContextComposite composite = new MyContextComposite(userContext, rolesAndPermissions);

            // when
            MyContextResponse result = sut.toResponse(composite);

            // then
            assertThat(result.permissions()).hasSize(3);
            assertThat(result.permissions())
                    .containsExactlyInAnyOrder("user:read", "user:write", "role:read");
        }

        @Test
        @DisplayName("빈 역할/권한이 빈 리스트로 변환됨")
        void shouldMapEmptyRolesAndPermissions_ToEmptyLists() {
            // given
            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId("user-123")
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-456")
                            .tenantName("Test Tenant")
                            .organizationId("org-789")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions = RolesAndPermissionsComposite.empty();

            MyContextComposite composite = new MyContextComposite(userContext, rolesAndPermissions);

            // when
            MyContextResponse result = sut.toResponse(composite);

            // then
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
        }
    }
}
