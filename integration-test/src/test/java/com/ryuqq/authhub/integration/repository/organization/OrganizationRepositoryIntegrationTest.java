package com.ryuqq.authhub.integration.repository.organization;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
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
 * OrganizationJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ORGANIZATION)
class OrganizationRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;

    @BeforeEach
    void setUp() {
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("활성 조직을 저장할 수 있다")
        void shouldSaveActiveOrganization() {
            // given
            String organizationId = UUID.randomUUID().toString();
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            organizationId,
                            savedTenant.getTenantId(),
                            "Test Organization",
                            OrganizationStatus.ACTIVE,
                            OrganizationJpaEntityFixture.fixedTime(),
                            OrganizationJpaEntityFixture.fixedTime(),
                            null);

            // when
            OrganizationJpaEntity saved = organizationJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getOrganizationId()).isEqualTo(organizationId);

            Optional<OrganizationJpaEntity> found =
                    organizationJpaRepository.findById(organizationId);
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Organization");
            assertThat(found.get().getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 조직을 저장할 수 있다")
        void shouldSaveInactiveOrganization() {
            // given
            String organizationId = UUID.randomUUID().toString();
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            organizationId,
                            savedTenant.getTenantId(),
                            "Inactive Organization",
                            OrganizationStatus.INACTIVE,
                            OrganizationJpaEntityFixture.fixedTime(),
                            OrganizationJpaEntityFixture.fixedTime(),
                            null);

            // when
            organizationJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<OrganizationJpaEntity> found =
                    organizationJpaRepository.findById(organizationId);
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 조직을 ID로 조회할 수 있다")
        void shouldFindExistingOrganization() {
            // given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId());
            organizationJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<OrganizationJpaEntity> found =
                    organizationJpaRepository.findById(entity.getOrganizationId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // given
            String nonExistentId = UUID.randomUUID().toString();

            // when
            Optional<OrganizationJpaEntity> found =
                    organizationJpaRepository.findById(nonExistentId);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("조직을 삭제할 수 있다")
        void shouldDeleteOrganization() {
            // given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId());
            organizationJpaRepository.save(entity);
            flushAndClear();

            // when
            organizationJpaRepository.deleteById(entity.getOrganizationId());
            flushAndClear();

            // then
            Optional<OrganizationJpaEntity> found =
                    organizationJpaRepository.findById(entity.getOrganizationId());
            assertThat(found).isEmpty();
        }
    }
}
