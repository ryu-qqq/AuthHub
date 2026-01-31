package com.ryuqq.authhub.domain.permission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Permission Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Permission Aggregate 테스트")
class PermissionTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("Permission 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("시스템 권한을 성공적으로 생성한다")
        void shouldCreateSystemPermissionSuccessfully() {
            // when
            Permission permission = Permission.createSystem("user", "read", "사용자 조회 권한", NOW);

            // then
            assertThat(permission.permissionKeyValue()).isEqualTo("user:read");
            assertThat(permission.resourceValue()).isEqualTo("user");
            assertThat(permission.actionValue()).isEqualTo("read");
            assertThat(permission.descriptionValue()).isEqualTo("사용자 조회 권한");
            assertThat(permission.isSystem()).isTrue();
            assertThat(permission.isCustom()).isFalse();
            assertThat(permission.isNew()).isTrue();
            assertThat(permission.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("커스텀 권한을 성공적으로 생성한다")
        void shouldCreateCustomPermissionSuccessfully() {
            // when
            Permission permission = Permission.createCustom("order", "manage", "주문 관리 권한", NOW);

            // then
            assertThat(permission.permissionKeyValue()).isEqualTo("order:manage");
            assertThat(permission.resourceValue()).isEqualTo("order");
            assertThat(permission.actionValue()).isEqualTo("manage");
            assertThat(permission.isSystem()).isFalse();
            assertThat(permission.isCustom()).isTrue();
        }

        @Test
        @DisplayName("통합 create 메서드로 권한을 생성한다")
        void shouldCreatePermissionViaUnifiedMethod() {
            // when
            Permission systemPermission =
                    Permission.create("user", "delete", "사용자 삭제 권한", true, NOW);
            Permission customPermission =
                    Permission.create("report", "view", "리포트 조회 권한", false, NOW);

            // then
            assertThat(systemPermission.isSystem()).isTrue();
            assertThat(customPermission.isCustom()).isTrue();
        }

        @Test
        @DisplayName("permissionKey가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenPermissionKeyIsNull() {
            // permissionKey는 resource + ":" + action으로 자동 생성되므로
            // resource나 action이 null이면 예외가 발생한다
            assertThatThrownBy(() -> Permission.createSystem(null, "read", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("resource가 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenResourceIsBlank() {
            // when & then
            assertThatThrownBy(() -> Permission.createSystem("", "read", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("resource");
        }

        @Test
        @DisplayName("action이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenActionIsBlank() {
            // when & then
            assertThatThrownBy(() -> Permission.createSystem("user", "", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("action");
        }

        @Test
        @DisplayName("permissionKey는 resource:action 형식이어야 한다")
        void permissionKeyShouldMatchResourceActionFormat() {
            // given
            Permission permission =
                    Permission.createCustom("organization", "create", "조직 생성 권한", NOW);

            // then
            assertThat(permission.permissionKeyValue()).isEqualTo("organization:create");
            assertThat(permission.permissionKeyValue())
                    .isEqualTo(permission.resourceValue() + ":" + permission.actionValue());
        }
    }

    @Nested
    @DisplayName("Permission 수정 테스트")
    class UpdateTests {

        @Test
        @DisplayName("커스텀 권한의 설명을 수정한다")
        void shouldUpdateCustomPermissionDescription() {
            // given
            Permission permission = PermissionFixture.createCustomPermission();
            PermissionUpdateData updateData = PermissionUpdateData.of("새로운 설명");

            // when
            permission.update(updateData, NOW);

            // then
            assertThat(permission.descriptionValue()).isEqualTo("새로운 설명");
            assertThat(permission.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("description이 null이면 변경하지 않는다")
        void shouldNotUpdateWhenDescriptionIsNull() {
            // given
            Permission permission = PermissionFixture.createCustomPermission();
            String originalDescription = permission.descriptionValue();
            PermissionUpdateData updateData = PermissionUpdateData.of(null);

            // when
            permission.update(updateData, NOW);

            // then
            assertThat(permission.descriptionValue()).isEqualTo(originalDescription);
        }

        @Test
        @DisplayName("시스템 권한은 수정할 수 없다")
        void shouldThrowExceptionWhenUpdatingSystemPermission() {
            // given
            Permission systemPermission = PermissionFixture.createSystemPermission();
            PermissionUpdateData updateData = PermissionUpdateData.of("새로운 설명");

            // when & then
            assertThatThrownBy(() -> systemPermission.update(updateData, NOW))
                    .isInstanceOf(SystemPermissionNotModifiableException.class);
        }

        @Test
        @DisplayName("resource와 action은 변경할 수 없다 (불변 필드)")
        void resourceAndActionShouldBeImmutable() {
            // given
            Permission permission = PermissionFixture.create();
            String originalResource = permission.resourceValue();
            String originalAction = permission.actionValue();
            String originalKey = permission.permissionKeyValue();

            // update 후에도 resource, action, key는 동일해야 함
            PermissionUpdateData updateData = PermissionUpdateData.of("변경된 설명");
            permission.update(updateData, NOW);

            // then
            assertThat(permission.resourceValue()).isEqualTo(originalResource);
            assertThat(permission.actionValue()).isEqualTo(originalAction);
            assertThat(permission.permissionKeyValue()).isEqualTo(originalKey);
        }
    }

    @Nested
    @DisplayName("Permission 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("커스텀 권한을 삭제(소프트 삭제)한다")
        void shouldDeleteCustomPermission() {
            // given
            Permission permission = PermissionFixture.createCustomPermission();

            // when
            permission.delete(NOW);

            // then
            assertThat(permission.isDeleted()).isTrue();
            assertThat(permission.isActive()).isFalse();
            assertThat(permission.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("시스템 권한은 삭제할 수 없다")
        void shouldThrowExceptionWhenDeletingSystemPermission() {
            // given
            Permission systemPermission = PermissionFixture.createSystemPermission();

            // when & then
            assertThatThrownBy(() -> systemPermission.delete(NOW))
                    .isInstanceOf(SystemPermissionNotDeletableException.class);
        }

        @Test
        @DisplayName("삭제된 권한을 복원한다")
        void shouldRestorePermission() {
            // given
            Permission permission = PermissionFixture.createDeleted();
            assertThat(permission.isDeleted()).isTrue();

            // when
            permission.restore(NOW);

            // then
            assertThat(permission.isDeleted()).isFalse();
            assertThat(permission.isActive()).isTrue();
            assertThat(permission.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Permission Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            Permission newPermission = PermissionFixture.createNewCustomPermission();
            Permission existingPermission = PermissionFixture.create();

            // then
            assertThat(newPermission.isNew()).isTrue();
            assertThat(existingPermission.isNew()).isFalse();
        }

        @Test
        @DisplayName("typeValue는 권한 유형 문자열을 반환한다")
        void typeValueShouldReturnTypeString() {
            // given
            Permission systemPermission = PermissionFixture.createSystemPermission();
            Permission customPermission = PermissionFixture.createCustomPermission();

            // then
            assertThat(systemPermission.typeValue()).isEqualTo("SYSTEM");
            assertThat(customPermission.typeValue()).isEqualTo("CUSTOM");
        }
    }

    @Nested
    @DisplayName("Permission equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 permissionId를 가진 Permission은 동등하다")
        void shouldBeEqualWhenSamePermissionId() {
            // given
            Permission permission1 = PermissionFixture.create();
            Permission permission2 = PermissionFixture.create();

            // then
            assertThat(permission1).isEqualTo(permission2);
            assertThat(permission1.hashCode()).isEqualTo(permission2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 permissionKey로 동등성을 판단한다")
        void shouldUsePermissionKeyWhenIdIsNull() {
            // given
            Permission newPermission1 = Permission.createCustom("user", "read", "설명1", NOW);
            Permission newPermission2 = Permission.createCustom("user", "read", "설명2", NOW);

            // then - 같은 permissionKey이므로 동등함
            assertThat(newPermission1).isEqualTo(newPermission2);
        }

        @Test
        @DisplayName("permissionKey가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentPermissionKey() {
            // given
            Permission permission1 = Permission.createCustom("user", "read", "설명", NOW);
            Permission permission2 = Permission.createCustom("user", "write", "설명", NOW);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }
    }
}
