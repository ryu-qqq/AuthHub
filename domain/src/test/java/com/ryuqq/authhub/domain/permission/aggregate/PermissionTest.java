package com.ryuqq.authhub.domain.permission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
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

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("시스템 권한 생성")
    class CreateSystemTest {

        @Test
        @DisplayName("Resource와 Action으로 시스템 권한을 생성한다")
        void shouldCreateSystemPermission() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Resource resource = Resource.of("user");
            Action action = Action.of("read");
            PermissionDescription description = PermissionDescription.of("사용자 조회 권한");

            // when
            Permission permission =
                    Permission.createSystem(
                            permissionId, resource, action, description, FIXED_CLOCK);

            // then
            assertThat(permission).isNotNull();
            assertThat(permission.permissionIdValue()).isNotNull();
            assertThat(permission.keyValue()).isEqualTo("user:read");
            assertThat(permission.resourceValue()).isEqualTo("user");
            assertThat(permission.actionValue()).isEqualTo("read");
            assertThat(permission.descriptionValue()).isEqualTo("사용자 조회 권한");
            assertThat(permission.isSystem()).isTrue();
            assertThat(permission.isCustom()).isFalse();
            assertThat(permission.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("PermissionKey로 시스템 권한을 생성한다")
        void shouldCreateSystemPermissionWithKey() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            PermissionKey key = PermissionKey.of("organization:manage");
            PermissionDescription description = PermissionDescription.of("조직 관리 권한");

            // when
            Permission permission =
                    Permission.createSystemWithKey(permissionId, key, description, FIXED_CLOCK);

            // then
            assertThat(permission).isNotNull();
            assertThat(permission.keyValue()).isEqualTo("organization:manage");
            assertThat(permission.isSystem()).isTrue();
        }

        @Test
        @DisplayName("설명 없이 시스템 권한을 생성하면 빈 설명이 된다")
        void shouldCreateSystemPermissionWithoutDescription() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Resource resource = Resource.of("tenant");
            Action action = Action.of("admin");

            // when
            Permission permission =
                    Permission.createSystem(permissionId, resource, action, null, FIXED_CLOCK);

            // then
            assertThat(permission.descriptionValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("커스텀 권한 생성")
    class CreateCustomTest {

        @Test
        @DisplayName("Resource와 Action으로 커스텀 권한을 생성한다")
        void shouldCreateCustomPermission() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Resource resource = Resource.of("report");
            Action action = Action.of("export");
            PermissionDescription description = PermissionDescription.of("리포트 내보내기 권한");

            // when
            Permission permission =
                    Permission.createCustom(
                            permissionId, resource, action, description, FIXED_CLOCK);

            // then
            assertThat(permission).isNotNull();
            assertThat(permission.permissionIdValue()).isNotNull();
            assertThat(permission.keyValue()).isEqualTo("report:export");
            assertThat(permission.isCustom()).isTrue();
            assertThat(permission.isSystem()).isFalse();
        }

        @Test
        @DisplayName("PermissionKey로 커스텀 권한을 생성한다")
        void shouldCreateCustomPermissionWithKey() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            PermissionKey key = PermissionKey.of("dashboard:view");
            PermissionDescription description = PermissionDescription.of("대시보드 조회 권한");

            // when
            Permission permission =
                    Permission.createCustomWithKey(permissionId, key, description, FIXED_CLOCK);

            // then
            assertThat(permission).isNotNull();
            assertThat(permission.keyValue()).isEqualTo("dashboard:view");
            assertThat(permission.isCustom()).isTrue();
        }
    }

    @Nested
    @DisplayName("권한 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("DB에서 권한을 재구성한다")
        void shouldReconstitutePermission() {
            // given
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());
            PermissionKey key = PermissionKey.of("user:delete");
            PermissionDescription description = PermissionDescription.of("사용자 삭제 권한");
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2025-01-02T00:00:00Z");

            // when
            Permission permission =
                    Permission.reconstitute(
                            permissionId,
                            key,
                            description,
                            PermissionType.SYSTEM,
                            false,
                            createdAt,
                            updatedAt);

            // then
            assertThat(permission.getPermissionId()).isEqualTo(permissionId);
            assertThat(permission.isNew()).isFalse();
            assertThat(permission.keyValue()).isEqualTo("user:delete");
            assertThat(permission.createdAt()).isEqualTo(createdAt);
            assertThat(permission.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("permissionId가 null이면 예외 발생")
        void shouldThrowExceptionWhenPermissionIdNull() {
            // given
            PermissionKey key = PermissionKey.of("user:read");
            Instant now = Instant.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    Permission.reconstitute(
                                            null,
                                            key,
                                            null,
                                            PermissionType.SYSTEM,
                                            false,
                                            now,
                                            now))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("non-null permissionId");
        }
    }

    @Nested
    @DisplayName("설명 변경")
    class ChangeDescriptionTest {

        @Test
        @DisplayName("커스텀 권한의 설명을 변경한다")
        void shouldChangeDescriptionForCustomPermission() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Permission permission =
                    Permission.createCustom(
                            permissionId,
                            Resource.of("report"),
                            Action.of("export"),
                            PermissionDescription.of("기존 설명"),
                            FIXED_CLOCK);
            PermissionDescription newDescription = PermissionDescription.of("새로운 설명");
            Clock laterClock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"));

            // when
            Permission updated = permission.changeDescription(newDescription, laterClock);

            // then
            assertThat(updated.descriptionValue()).isEqualTo("새로운 설명");
            assertThat(updated.updatedAt()).isAfter(permission.updatedAt());
        }

        @Test
        @DisplayName("시스템 권한의 설명 변경 시 예외 발생")
        void shouldThrowExceptionWhenChangingSystemPermissionDescription() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Permission permission =
                    Permission.createSystem(
                            permissionId,
                            Resource.of("user"),
                            Action.of("read"),
                            PermissionDescription.of("설명"),
                            FIXED_CLOCK);
            PermissionDescription newDescription = PermissionDescription.of("새로운 설명");

            // when & then
            assertThatThrownBy(() -> permission.changeDescription(newDescription, FIXED_CLOCK))
                    .isInstanceOf(SystemPermissionNotModifiableException.class);
        }
    }

    @Nested
    @DisplayName("권한 삭제")
    class DeleteTest {

        @Test
        @DisplayName("커스텀 권한을 삭제한다")
        void shouldDeleteCustomPermission() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Permission permission =
                    Permission.createCustom(
                            permissionId,
                            Resource.of("report"),
                            Action.of("export"),
                            null,
                            FIXED_CLOCK);
            Clock laterClock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneId.of("UTC"));

            // when
            Permission deleted = permission.delete(laterClock);

            // then
            assertThat(deleted.isDeleted()).isTrue();
            assertThat(deleted.updatedAt()).isAfter(permission.updatedAt());
        }

        @Test
        @DisplayName("시스템 권한 삭제 시 예외 발생")
        void shouldThrowExceptionWhenDeletingSystemPermission() {
            // given
            PermissionId permissionId = PermissionId.forNew(UUID.randomUUID());
            Permission permission =
                    Permission.createSystem(
                            permissionId,
                            Resource.of("user"),
                            Action.of("read"),
                            null,
                            FIXED_CLOCK);

            // when & then
            assertThatThrownBy(() -> permission.delete(FIXED_CLOCK))
                    .isInstanceOf(SystemPermissionNotDeletableException.class);
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드")
    class HelperMethodsTest {

        @Test
        @DisplayName("permissionIdValue - 생성된 권한은 UUID 반환")
        void shouldReturnUuidForCreatedPermission() {
            // given
            UUID uuid = UUID.randomUUID();
            PermissionId permissionId = PermissionId.forNew(uuid);
            Permission permission =
                    Permission.createCustom(
                            permissionId,
                            Resource.of("test"),
                            Action.of("action"),
                            null,
                            FIXED_CLOCK);

            // then
            assertThat(permission.permissionIdValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("permissionIdValue - 재구성된 권한은 UUID 반환")
        void shouldReturnUuidForReconstitutedPermission() {
            // given
            UUID uuid = UUID.randomUUID();
            Permission permission =
                    Permission.reconstitute(
                            PermissionId.of(uuid),
                            PermissionKey.of("test:action"),
                            null,
                            PermissionType.CUSTOM,
                            false,
                            Instant.now(),
                            Instant.now());

            // then
            assertThat(permission.permissionIdValue()).isEqualTo(uuid);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 ID를 가진 권한은 동일하다")
        void shouldBeEqualWhenSameId() {
            // given
            UUID uuid = UUID.randomUUID();
            Instant now = Instant.now();
            Permission permission1 =
                    Permission.reconstitute(
                            PermissionId.of(uuid),
                            PermissionKey.of("user:read"),
                            null,
                            PermissionType.SYSTEM,
                            false,
                            now,
                            now);
            Permission permission2 =
                    Permission.reconstitute(
                            PermissionId.of(uuid),
                            PermissionKey.of("user:read"),
                            null,
                            PermissionType.SYSTEM,
                            false,
                            now,
                            now);

            // then
            assertThat(permission1).isEqualTo(permission2);
            assertThat(permission1.hashCode()).isEqualTo(permission2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 권한은 다르다")
        void shouldNotBeEqualWhenDifferentId() {
            // given
            Instant now = Instant.now();
            Permission permission1 =
                    Permission.reconstitute(
                            PermissionId.of(UUID.randomUUID()),
                            PermissionKey.of("user:read"),
                            null,
                            PermissionType.SYSTEM,
                            false,
                            now,
                            now);
            Permission permission2 =
                    Permission.reconstitute(
                            PermissionId.of(UUID.randomUUID()),
                            PermissionKey.of("user:read"),
                            null,
                            PermissionType.SYSTEM,
                            false,
                            now,
                            now);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }

        @Test
        @DisplayName("다른 ID로 생성된 권한은 서로 다르다")
        void shouldNotBeEqualWhenDifferentIds() {
            // given
            Permission permission1 =
                    Permission.createCustom(
                            PermissionId.forNew(UUID.randomUUID()),
                            Resource.of("test"),
                            Action.of("action"),
                            null,
                            FIXED_CLOCK);
            Permission permission2 =
                    Permission.createCustom(
                            PermissionId.forNew(UUID.randomUUID()),
                            Resource.of("test"),
                            Action.of("action"),
                            null,
                            FIXED_CLOCK);

            // then
            assertThat(permission1).isNotEqualTo(permission2);
        }
    }
}
