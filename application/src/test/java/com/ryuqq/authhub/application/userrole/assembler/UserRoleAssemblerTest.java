package com.ryuqq.authhub.application.userrole.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.userrole.dto.response.UserRolesResponse;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleAssembler 단위 테스트")
class UserRoleAssemblerTest {

    private UserRoleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleAssembler();
    }

    @Nested
    @DisplayName("assemble 메서드")
    class Assemble {

        @Test
        @DisplayName("성공: 역할 이름과 권한 키를 UserRolesResponse로 조립")
        void shouldAssemble_FromRoleNamesAndPermissionKeys() {
            // given
            Set<String> roleNames = Set.of("ADMIN", "USER");
            Set<String> permissionKeys = Set.of("user:read", "user:create");

            // when
            UserRolesResponse result = sut.assemble(roleNames, permissionKeys);

            // then
            assertThat(result.roles()).containsExactlyInAnyOrder("ADMIN", "USER");
            assertThat(result.permissions()).containsExactlyInAnyOrder("user:read", "user:create");
        }

        @Test
        @DisplayName("빈 Set으로 UserRolesResponse 생성 가능")
        void shouldAssemble_WithEmptySets() {
            // given
            Set<String> roleNames = Set.of();
            Set<String> permissionKeys = Set.of();

            // when
            UserRolesResponse result = sut.assemble(roleNames, permissionKeys);

            // then
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
        }
    }
}
