package com.ryuqq.authhub.integration.repository.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TenantJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.TENANT)
class TenantRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private TenantJpaRepository tenantJpaRepository;

    @BeforeEach
    void setUp() {
        tenantJpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("활성 테넌트를 저장할 수 있다")
        void shouldSaveActiveTenant() {
            // given
            String tenantId = UUID.randomUUID().toString();
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            tenantId,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null);

            // when
            TenantJpaEntity saved = tenantJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getTenantId()).isEqualTo(tenantId);

            Optional<TenantJpaEntity> found = tenantJpaRepository.findById(tenantId);
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Tenant");
            assertThat(found.get().getStatus()).isEqualTo(TenantStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 테넌트를 저장할 수 있다")
        void shouldSaveInactiveTenant() {
            // given
            String tenantId = UUID.randomUUID().toString();
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            tenantId,
                            "Inactive Tenant",
                            TenantStatus.INACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null);

            // when
            tenantJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<TenantJpaEntity> found = tenantJpaRepository.findById(tenantId);
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(TenantStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 테넌트를 ID로 조회할 수 있다")
        void shouldFindExistingTenant() {
            // given
            TenantJpaEntity entity = TenantJpaEntityFixture.create();
            tenantJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<TenantJpaEntity> found = tenantJpaRepository.findById(entity.getTenantId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Tenant");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // given
            String nonExistentId = UUID.randomUUID().toString();

            // when
            Optional<TenantJpaEntity> found = tenantJpaRepository.findById(nonExistentId);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("테넌트를 삭제할 수 있다")
        void shouldDeleteTenant() {
            // given
            TenantJpaEntity entity = TenantJpaEntityFixture.create();
            tenantJpaRepository.save(entity);
            flushAndClear();

            // when
            tenantJpaRepository.deleteById(entity.getTenantId());
            flushAndClear();

            // then
            Optional<TenantJpaEntity> found = tenantJpaRepository.findById(entity.getTenantId());
            assertThat(found).isEmpty();
        }
    }
}
