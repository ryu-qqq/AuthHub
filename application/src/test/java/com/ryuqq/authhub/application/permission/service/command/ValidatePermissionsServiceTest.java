package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.application.permission.dto.command.ValidatePermissionsCommand;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidatePermissionsService 단위 테스트")
class ValidatePermissionsServiceTest {

    @Mock private PermissionReadManager readManager;

    private ValidatePermissionsService service;

    @BeforeEach
    void setUp() {
        service = new ValidatePermissionsService(readManager);
    }

    @Nested
    @DisplayName("execute 메서드는")
    class ExecuteMethod {

        @Test
        @DisplayName("모든 권한이 존재하면 valid=true를 반환한다")
        void shouldReturnValidTrueWhenAllPermissionsExist() {
            // given
            String serviceName = "product-service";
            List<ValidatePermissionsCommand.PermissionEntry> entries =
                    List.of(
                            new ValidatePermissionsCommand.PermissionEntry(
                                    "product:read", List.of("ProductController.java:45")),
                            new ValidatePermissionsCommand.PermissionEntry(
                                    "product:write", List.of("ProductController.java:67")));
            ValidatePermissionsCommand command =
                    new ValidatePermissionsCommand(serviceName, entries);

            List<Permission> existingPermissions =
                    List.of(createPermission("product:read"), createPermission("product:write"));

            when(readManager.findAllByKeys(anySet())).thenReturn(existingPermissions);

            // when
            ValidatePermissionsResult result = service.execute(command);

            // then
            assertThat(result.valid()).isTrue();
            assertThat(result.serviceName()).isEqualTo(serviceName);
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.existingCount()).isEqualTo(2);
            assertThat(result.missingCount()).isZero();
            assertThat(result.existing())
                    .containsExactlyInAnyOrder("product:read", "product:write");
            assertThat(result.missing()).isEmpty();
            assertThat(result.message()).contains("All permissions are registered");

            verify(readManager).findAllByKeys(anySet());
        }

        @Test
        @DisplayName("일부 권한이 누락되면 valid=false를 반환한다")
        void shouldReturnValidFalseWhenSomePermissionsMissing() {
            // given
            String serviceName = "product-service";
            List<ValidatePermissionsCommand.PermissionEntry> entries =
                    List.of(
                            new ValidatePermissionsCommand.PermissionEntry(
                                    "product:read", List.of()),
                            new ValidatePermissionsCommand.PermissionEntry(
                                    "product:write", List.of()),
                            new ValidatePermissionsCommand.PermissionEntry(
                                    "product:delete", List.of()));
            ValidatePermissionsCommand command =
                    new ValidatePermissionsCommand(serviceName, entries);

            List<Permission> existingPermissions = List.of(createPermission("product:read"));

            when(readManager.findAllByKeys(anySet())).thenReturn(existingPermissions);

            // when
            ValidatePermissionsResult result = service.execute(command);

            // then
            assertThat(result.valid()).isFalse();
            assertThat(result.serviceName()).isEqualTo(serviceName);
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.existingCount()).isEqualTo(1);
            assertThat(result.missingCount()).isEqualTo(2);
            assertThat(result.existing()).containsExactly("product:read");
            assertThat(result.missing())
                    .containsExactlyInAnyOrder("product:write", "product:delete");
            assertThat(result.message()).contains("Missing 2 permission(s)");
        }

        @Test
        @DisplayName("빈 권한 목록이면 valid=true를 반환한다")
        void shouldReturnValidTrueWhenEmptyPermissions() {
            // given
            String serviceName = "empty-service";
            ValidatePermissionsCommand command =
                    new ValidatePermissionsCommand(serviceName, List.of());

            // when
            ValidatePermissionsResult result = service.execute(command);

            // then
            assertThat(result.valid()).isTrue();
            assertThat(result.serviceName()).isEqualTo(serviceName);
            assertThat(result.totalCount()).isZero();
            assertThat(result.existingCount()).isZero();
            assertThat(result.missingCount()).isZero();
            assertThat(result.existing()).isEmpty();
            assertThat(result.missing()).isEmpty();
        }

        @Test
        @DisplayName("모든 권한이 누락되면 valid=false를 반환한다")
        void shouldReturnValidFalseWhenAllPermissionsMissing() {
            // given
            String serviceName = "new-service";
            List<ValidatePermissionsCommand.PermissionEntry> entries =
                    List.of(
                            new ValidatePermissionsCommand.PermissionEntry("new:read", List.of()),
                            new ValidatePermissionsCommand.PermissionEntry("new:write", List.of()));
            ValidatePermissionsCommand command =
                    new ValidatePermissionsCommand(serviceName, entries);

            when(readManager.findAllByKeys(anySet())).thenReturn(List.of());

            // when
            ValidatePermissionsResult result = service.execute(command);

            // then
            assertThat(result.valid()).isFalse();
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.existingCount()).isZero();
            assertThat(result.missingCount()).isEqualTo(2);
            assertThat(result.existing()).isEmpty();
            assertThat(result.missing()).containsExactlyInAnyOrder("new:read", "new:write");
        }
    }

    private Permission createPermission(String key) {
        return Permission.reconstitute(
                PermissionId.of(UUID.randomUUID()),
                PermissionKey.of(key),
                PermissionDescription.of("Test permission: " + key),
                PermissionType.SYSTEM,
                false,
                java.time.Instant.now(),
                java.time.Instant.now());
    }
}
