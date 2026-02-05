package com.ryuqq.authhub.adapter.in.rest.rolepermission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.RevokeRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.fixture.RolePermissionApiFixture;
import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RolePermissionCommandApiMapper 단위 테스트")
class RolePermissionCommandApiMapperTest {

    private RolePermissionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RolePermissionCommandApiMapper();
    }

    @Nested
    @DisplayName("toGrantCommand(Long, GrantRolePermissionApiRequest) 메서드는")
    class ToGrantCommand {

        @Test
        @DisplayName("GrantRolePermissionApiRequest를 GrantRolePermissionCommand로 변환한다")
        void shouldConvertToGrantRolePermissionCommand() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest();

            // When
            GrantRolePermissionCommand result = mapper.toGrantCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds())
                    .isEqualTo(RolePermissionApiFixture.defaultPermissionIds());
        }

        @Test
        @DisplayName("단일 권한 ID로 변환한다")
        void shouldConvertSinglePermissionId() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantSinglePermissionRequest(1L);

            // When
            GrantRolePermissionCommand result = mapper.toGrantCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).containsExactly(1L);
        }

        @Test
        @DisplayName("다른 roleId도 정상 변환한다")
        void shouldConvertDifferentRoleId() {
            // Given
            Long roleId = 999L;
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest();

            // When
            GrantRolePermissionCommand result = mapper.toGrantCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(999L);
            assertThat(result.permissionIds())
                    .isEqualTo(RolePermissionApiFixture.defaultPermissionIds());
        }

        @Test
        @DisplayName("커스텀 권한 ID 목록으로 변환한다")
        void shouldConvertCustomPermissionIds() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> customPermissionIds = List.of(10L, 20L, 30L);
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest(customPermissionIds);

            // When
            GrantRolePermissionCommand result = mapper.toGrantCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).isEqualTo(customPermissionIds);
        }

        @Test
        @DisplayName("빈 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            GrantRolePermissionApiRequest request =
                    RolePermissionApiFixture.grantRolePermissionRequest(List.of());

            // When
            GrantRolePermissionCommand result = mapper.toGrantCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toRevokeCommand(Long, RevokeRolePermissionApiRequest) 메서드는")
    class ToRevokeCommand {

        @Test
        @DisplayName("RevokeRolePermissionApiRequest를 RevokeRolePermissionCommand로 변환한다")
        void shouldConvertToRevokeRolePermissionCommand() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest();

            // When
            RevokeRolePermissionCommand result = mapper.toRevokeCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds())
                    .isEqualTo(RolePermissionApiFixture.defaultPermissionIds());
        }

        @Test
        @DisplayName("단일 권한 ID로 변환한다")
        void shouldConvertSinglePermissionId() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeSinglePermissionRequest(1L);

            // When
            RevokeRolePermissionCommand result = mapper.toRevokeCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).containsExactly(1L);
        }

        @Test
        @DisplayName("다른 roleId도 정상 변환한다")
        void shouldConvertDifferentRoleId() {
            // Given
            Long roleId = 999L;
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest();

            // When
            RevokeRolePermissionCommand result = mapper.toRevokeCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(999L);
            assertThat(result.permissionIds())
                    .isEqualTo(RolePermissionApiFixture.defaultPermissionIds());
        }

        @Test
        @DisplayName("커스텀 권한 ID 목록으로 변환한다")
        void shouldConvertCustomPermissionIds() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            List<Long> customPermissionIds = List.of(10L, 20L, 30L);
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest(customPermissionIds);

            // When
            RevokeRolePermissionCommand result = mapper.toRevokeCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).isEqualTo(customPermissionIds);
        }

        @Test
        @DisplayName("빈 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            Long roleId = RolePermissionApiFixture.defaultRoleId();
            RevokeRolePermissionApiRequest request =
                    RolePermissionApiFixture.revokeRolePermissionRequest(List.of());

            // When
            RevokeRolePermissionCommand result = mapper.toRevokeCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.permissionIds()).isEmpty();
        }
    }
}
