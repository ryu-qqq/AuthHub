package com.ryuqq.authhub.integration.repository.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.fixture.RoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RoleJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ROLE)
class RoleRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private RoleJpaRepository roleJpaRepository;

    @BeforeEach
    void setUp() {
        roleJpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("역할을 저장할 수 있다")
        void shouldSaveRole() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            null,
                            null,
                            null,
                            "ADMIN",
                            "관리자",
                            "시스템 관리자 역할",
                            RoleType.CUSTOM,
                            RoleScope.GLOBAL,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null);

            // when
            RoleJpaEntity saved = roleJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getRoleId()).isNotNull();

            Optional<RoleJpaEntity> found = roleJpaRepository.findById(saved.getRoleId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("ADMIN");
            assertThat(found.get().getDisplayName()).isEqualTo("관리자");
            assertThat(found.get().getType()).isEqualTo(RoleType.CUSTOM);
        }

        @Test
        @DisplayName("테넌트가 있는 역할을 저장할 수 있다")
        void shouldSaveRoleWithTenant() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            null,
                            tenantId,
                            null,
                            "TENANT_ADMIN",
                            "테넌트 관리자",
                            "테넌트 관리자 역할",
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null);

            // when
            roleJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<RoleJpaEntity> found =
                    roleJpaRepository.findAll().stream()
                            .filter(r -> "TENANT_ADMIN".equals(r.getName()))
                            .findFirst();
            assertThat(found).isPresent();
            assertThat(found.get().getTenantId()).isEqualTo(tenantId);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 역할을 ID로 조회할 수 있다")
        void shouldFindExistingRole() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            null,
                            null,
                            null,
                            "VIEWER",
                            "조회자",
                            "조회 전용 역할",
                            RoleType.CUSTOM,
                            RoleScope.GLOBAL,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null);
            RoleJpaEntity saved = roleJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<RoleJpaEntity> found = roleJpaRepository.findById(saved.getRoleId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("VIEWER");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // when
            Optional<RoleJpaEntity> found = roleJpaRepository.findById(99999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("역할을 삭제할 수 있다")
        void shouldDeleteRole() {
            // given
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            null,
                            null,
                            null,
                            "DELETABLE",
                            "삭제 가능 역할",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.GLOBAL,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null);
            RoleJpaEntity saved = roleJpaRepository.save(entity);
            flushAndClear();

            // when
            roleJpaRepository.deleteById(saved.getRoleId());
            flushAndClear();

            // then
            Optional<RoleJpaEntity> found = roleJpaRepository.findById(saved.getRoleId());
            assertThat(found).isEmpty();
        }
    }
}
