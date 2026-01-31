package com.ryuqq.authhub.integration.repository.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PermissionJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.PERMISSION)
class PermissionRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    @BeforeEach
    void setUp() {
        permissionJpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("권한을 저장할 수 있다")
        void shouldSavePermission() {
            // given
            Instant now = PermissionJpaEntityFixture.fixedTime();
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            null,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            now,
                            now,
                            null);

            // when
            PermissionJpaEntity saved = permissionJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getPermissionId()).isNotNull();

            Optional<PermissionJpaEntity> found =
                    permissionJpaRepository.findById(saved.getPermissionId());
            assertThat(found).isPresent();
            assertThat(found.get().getPermissionKey()).isEqualTo("user:read");
            assertThat(found.get().getResource()).isEqualTo("user");
            assertThat(found.get().getAction()).isEqualTo("read");
            assertThat(found.get().getType()).isEqualTo(PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("시스템 권한을 저장할 수 있다")
        void shouldSaveSystemPermission() {
            // given
            Instant now = PermissionJpaEntityFixture.fixedTime();
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            null,
                            "system:manage",
                            "system",
                            "manage",
                            "시스템 관리 권한",
                            PermissionType.SYSTEM,
                            now,
                            now,
                            null);

            // when
            PermissionJpaEntity saved = permissionJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<PermissionJpaEntity> found =
                    permissionJpaRepository.findById(saved.getPermissionId());
            assertThat(found).isPresent();
            assertThat(found.get().getPermissionKey()).isEqualTo("system:manage");
            assertThat(found.get().getType()).isEqualTo(PermissionType.SYSTEM);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 권한을 ID로 조회할 수 있다")
        void shouldFindExistingPermission() {
            // given
            Instant now = PermissionJpaEntityFixture.fixedTime();
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            null,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            now,
                            now,
                            null);
            PermissionJpaEntity saved = permissionJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<PermissionJpaEntity> found =
                    permissionJpaRepository.findById(saved.getPermissionId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getPermissionKey()).isEqualTo("user:read");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // when
            Optional<PermissionJpaEntity> found = permissionJpaRepository.findById(99999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("권한을 삭제할 수 있다")
        void shouldDeletePermission() {
            // given
            Instant now = PermissionJpaEntityFixture.fixedTime();
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            null,
                            "user:delete",
                            "user",
                            "delete",
                            "사용자 삭제 권한",
                            PermissionType.CUSTOM,
                            now,
                            now,
                            null);
            PermissionJpaEntity saved = permissionJpaRepository.save(entity);
            flushAndClear();

            // when
            permissionJpaRepository.deleteById(saved.getPermissionId());
            flushAndClear();

            // then
            Optional<PermissionJpaEntity> found =
                    permissionJpaRepository.findById(saved.getPermissionId());
            assertThat(found).isEmpty();
        }
    }
}
