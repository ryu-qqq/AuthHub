package com.ryuqq.authhub.domain.permission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Permission Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
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
            Permission permission = Permission.createSystem(null, "user", "read", "사용자 조회 권한", NOW);

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
            Permission permission =
                    Permission.createCustom(null, "order", "manage", "주문 관리 권한", NOW);

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
                    Permission.create(null, "user", "delete", "사용자 삭제 권한", true, NOW);
            Permission customPermission =
                    Permission.create(null, "report", "view", "리포트 조회 권한", false, NOW);

            // then
            assertThat(systemPermission.isSystem()).isTrue();
            assertThat(customPermission.isCustom()).isTrue();
        }

        @Test
        @DisplayName("permissionKey가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenPermissionKeyIsNull() {
            // permissionKey는 resource + ":" + action으로 자동 생성되므로
            // resource나 action이 null이면 예외가 발생한다
            assertThatThrownBy(() -> Permission.createSystem(null, null, "read", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("resource가 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenResourceIsBlank() {
            // when & then
            assertThatThrownBy(() -> Permission.createSystem(null, "", "read", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("resource");
        }

        @Test
        @DisplayName("action이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenActionIsBlank() {
            // when & then
            assertThatThrownBy(() -> Permission.createSystem(null, "user", "", "설명", NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("action");
        }

        @Test
        @DisplayName("permissionKey는 resource:action 형식이어야 한다")
        void permissionKeyShouldMatchResourceActionFormat() {
            // given
            Permission permission =
                    Permission.createCustom(null, "organization", "create", "조직 생성 권한", NOW);

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

        @Test
        @DisplayName("serviceIdValue()는 serviceId가 있으면 값을 반환한다")
        void serviceIdValueShouldReturnValueWhenServiceIdExists() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            Permission permission =
                    Permission.reconstitute(
                            PermissionId.of(1L),
                            serviceId,
                            PermissionKey.of("user:read"),
                            Resource.of("user"),
                            Action.of("read"),
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);

            // when
            Long serviceIdValue = permission.serviceIdValue();

            // then
            assertThat(serviceIdValue).isEqualTo(1L);
        }

        @Test
        @DisplayName("serviceIdValue()는 serviceId가 null이면 null을 반환한다")
        void serviceIdValueShouldReturnNullWhenServiceIdIsNull() {
            // given
            Permission permission = PermissionFixture.create();

            // when
            Long serviceIdValue = permission.serviceIdValue();

            // then
            assertThat(serviceIdValue).isNull();
        }

        @Test
        @DisplayName("getter 메서드들이 올바른 값을 반환한다")
        void gettersShouldReturnCorrectValues() {
            // given
            Permission permission = PermissionFixture.create();

            // then
            assertThat(permission.getPermissionId()).isNotNull();
            assertThat(permission.getServiceId()).isNull();
            assertThat(permission.getPermissionKey()).isNotNull();
            assertThat(permission.getResource()).isNotNull();
            assertThat(permission.getAction()).isNotNull();
            assertThat(permission.getDescription()).isNotNull();
            assertThat(permission.getType()).isEqualTo(PermissionType.CUSTOM);
            assertThat(permission.getDeletionStatus()).isNotNull();
        }

        @Test
        @DisplayName("toString()은 권한 정보를 포함한다")
        void toStringShouldContainPermissionInfo() {
            // given
            Permission permission = PermissionFixture.create();

            // when
            String toString = permission.toString();

            // then
            assertThat(toString).contains("Permission");
            assertThat(toString).contains("permissionId");
            assertThat(toString).contains("permissionKey");
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
            Permission newPermission1 = Permission.createCustom(null, "user", "read", "설명1", NOW);
            Permission newPermission2 = Permission.createCustom(null, "user", "read", "설명2", NOW);

            // then - 같은 permissionKey이므로 동등함
            assertThat(newPermission1).isEqualTo(newPermission2);
        }

        @Test
        @DisplayName("permissionKey가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentPermissionKey() {
            // given
            Permission permission1 = Permission.createCustom(null, "user", "read", "설명", NOW);
            Permission permission2 = Permission.createCustom(null, "user", "write", "설명", NOW);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }

        @Test
        @DisplayName("ID가 없고 serviceId가 null인 경우 permissionKey로 동등성을 판단한다")
        void shouldUsePermissionKeyWhenIdIsNullAndServiceIdIsNull() {
            // given
            Permission permission1 = Permission.createCustom(null, "user", "read", "설명1", NOW);
            Permission permission2 = Permission.createCustom(null, "user", "read", "설명2", NOW);

            // then
            assertThat(permission1).isEqualTo(permission2);
        }

        @Test
        @DisplayName("ID가 없고 serviceId가 다른 경우 동등하지 않다")
        void shouldNotBeEqualWhenIdIsNullAndServiceIdIsDifferent() {
            // given
            ServiceId serviceId1 = ServiceId.of(1L);
            ServiceId serviceId2 = ServiceId.of(2L);
            Permission permission1 =
                    Permission.reconstitute(
                            null,
                            serviceId1,
                            PermissionKey.of("user:read"),
                            Resource.of("user"),
                            Action.of("read"),
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);
            Permission permission2 =
                    Permission.reconstitute(
                            null,
                            serviceId2,
                            PermissionKey.of("user:read"),
                            Resource.of("user"),
                            Action.of("read"),
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }

        @Test
        @DisplayName("ID가 없고 serviceId는 같고 permissionKey가 다른 경우 동등하지 않다")
        void shouldNotBeEqualWhenIdIsNullAndServiceIdIsSameButPermissionKeyIsDifferent() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            Permission permission1 =
                    Permission.reconstitute(
                            null,
                            serviceId,
                            PermissionKey.of("user:read"),
                            Resource.of("user"),
                            Action.of("read"),
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);
            Permission permission2 =
                    Permission.reconstitute(
                            null,
                            serviceId,
                            PermissionKey.of("user:write"),
                            Resource.of("user"),
                            Action.of("write"),
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }
    }

    @Nested
    @DisplayName("Permission 재구성 테스트")
    class ReconstituteTests {

        @Test
        @DisplayName("VO 타입 reconstitute()로 재구성한다")
        void shouldReconstituteViaVOTypes() {
            // given
            PermissionId permissionId = PermissionId.of(1L);
            ServiceId serviceId = ServiceId.of(1L);
            PermissionKey permissionKey = PermissionKey.of("user:read");
            Resource resource = Resource.of("user");
            Action action = Action.of("read");
            String description = "설명";
            PermissionType type = PermissionType.CUSTOM;
            DeletionStatus deletionStatus = DeletionStatus.active();

            // when
            Permission permission =
                    Permission.reconstitute(
                            permissionId,
                            serviceId,
                            permissionKey,
                            resource,
                            action,
                            description,
                            type,
                            deletionStatus,
                            NOW,
                            NOW);

            // then
            assertThat(permission.permissionIdValue()).isEqualTo(1L);
            assertThat(permission.serviceIdValue()).isEqualTo(1L);
            assertThat(permission.permissionKeyValue()).isEqualTo("user:read");
            assertThat(permission.resourceValue()).isEqualTo("user");
            assertThat(permission.actionValue()).isEqualTo("read");
            assertThat(permission.descriptionValue()).isEqualTo("설명");
        }

        @Test
        @DisplayName("String 타입 reconstitute()로 재구성한다")
        void shouldReconstituteViaStringTypes() {
            // when
            Permission permission =
                    Permission.reconstitute(
                            1L,
                            1L,
                            "user:read",
                            "user",
                            "read",
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);

            // then
            assertThat(permission.permissionIdValue()).isEqualTo(1L);
            assertThat(permission.serviceIdValue()).isEqualTo(1L);
            assertThat(permission.permissionKeyValue()).isEqualTo("user:read");
            assertThat(permission.resourceValue()).isEqualTo("user");
            assertThat(permission.actionValue()).isEqualTo("read");
        }

        @Test
        @DisplayName("null serviceId로 재구성할 수 있다")
        void shouldReconstituteWithNullServiceId() {
            // when
            Permission permission =
                    Permission.reconstitute(
                            1L,
                            null,
                            "user:read",
                            "user",
                            "read",
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.active(),
                            NOW,
                            NOW);

            // then
            assertThat(permission.serviceIdValue()).isNull();
        }

        @Test
        @DisplayName("삭제된 상태로 재구성할 수 있다")
        void shouldReconstituteWithDeletedStatus() {
            // when
            Permission permission =
                    Permission.reconstitute(
                            1L,
                            1L,
                            "user:read",
                            "user",
                            "read",
                            "설명",
                            PermissionType.CUSTOM,
                            DeletionStatus.deletedAt(NOW),
                            NOW,
                            NOW);

            // then
            assertThat(permission.isDeleted()).isTrue();
            assertThat(permission.isActive()).isFalse();
        }
    }
}
