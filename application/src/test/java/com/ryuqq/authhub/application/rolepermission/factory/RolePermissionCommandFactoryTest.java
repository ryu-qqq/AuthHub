package com.ryuqq.authhub.application.rolepermission.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionCommandFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 RoleId + PermissionIds → RolePermission 목록 생성 담당
 *   <li>TimeProvider를 통한 시간 주입 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionCommandFactory 단위 테스트")
class RolePermissionCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private RolePermissionCommandFactory sut;

    private static final Instant FIXED_TIME = RolePermissionFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new RolePermissionCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("createAll 메서드")
    class CreateAll {

        @Test
        @DisplayName("성공: RoleId와 PermissionIds로 RolePermission 목록 생성")
        void shouldCreateRolePermissions_FromRoleIdAndPermissionIds() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIds =
                    List.of(RolePermissionFixture.defaultPermissionId(), PermissionId.of(2L));

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            List<RolePermission> result = sut.createAll(roleId, permissionIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).roleIdValue()).isEqualTo(roleId.value());
            assertThat(result.get(0).permissionIdValue()).isEqualTo(permissionIds.get(0).value());
            assertThat(result.get(0).createdAt()).isEqualTo(FIXED_TIME);
            assertThat(result.get(1).roleIdValue()).isEqualTo(roleId.value());
            assertThat(result.get(1).permissionIdValue()).isEqualTo(permissionIds.get(1).value());
        }

        @Test
        @DisplayName("TimeProvider를 통해 생성 시간 설정")
        void shouldSetCreatedAt_ThroughTimeProvider() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIds = List.of(RolePermissionFixture.defaultPermissionId());
            Instant expectedTime = Instant.parse("2025-06-15T10:30:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            List<RolePermission> result = sut.createAll(roleId, permissionIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).createdAt()).isEqualTo(expectedTime);
        }
    }
}
