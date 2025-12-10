package com.ryuqq.authhub.application.role.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleAssembler 단위 테스트")
class RoleAssemblerTest {

    private RoleAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new RoleAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("Role을 RoleResponse로 변환한다")
        void shouldConvertToResponse() {
            // given
            Role role = RoleFixture.create();

            // when
            RoleResponse result = assembler.toResponse(role);

            // then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(role.roleIdValue());
            assertThat(result.tenantId()).isEqualTo(role.tenantIdValue());
            assertThat(result.name()).isEqualTo(role.nameValue());
            assertThat(result.description()).isEqualTo(role.descriptionValue());
            assertThat(result.scope()).isEqualTo(role.scopeValue());
            assertThat(result.type()).isEqualTo(role.typeValue());
            assertThat(result.createdAt()).isEqualTo(role.createdAt());
            assertThat(result.updatedAt()).isEqualTo(role.updatedAt());
        }

        @Test
        @DisplayName("시스템 GLOBAL 역할을 RoleResponse로 변환한다")
        void shouldConvertSystemGlobalRoleToResponse() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();

            // when
            RoleResponse result = assembler.toResponse(systemRole);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.scope()).isEqualTo("GLOBAL");
            assertThat(result.type()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("커스텀 TENANT 역할을 RoleResponse로 변환한다")
        void shouldConvertCustomTenantRoleToResponse() {
            // given
            Role tenantRole = RoleFixture.createCustomTenant();

            // when
            RoleResponse result = assembler.toResponse(tenantRole);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(RoleFixture.defaultTenantUUID());
            assertThat(result.scope()).isEqualTo("TENANT");
            assertThat(result.type()).isEqualTo("CUSTOM");
        }
    }
}
