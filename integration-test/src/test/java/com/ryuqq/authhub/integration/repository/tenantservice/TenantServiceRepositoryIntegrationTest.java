package com.ryuqq.authhub.integration.repository.tenantservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture.TenantServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceJpaRepository;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TenantService JPA Repository 통합 테스트.
 *
 * <p>Spring Data JPA의 기본 CRUD 및 JpaRepository 커스텀 메서드 동작을 검증합니다.
 *
 * <ul>
 *   <li>save - 신규/수정 저장
 *   <li>findById - ID 조회
 *   <li>existsByTenantIdAndServiceId - 복합 유니크 체크
 *   <li>findByTenantIdAndServiceId - 복합 조회
 *   <li>BaseAuditEntity 감사 필드 자동 설정 검증
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.TENANT_SERVICE)
@DisplayName("테넌트-서비스 JPA Repository 통합 테스트")
class TenantServiceRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private TenantServiceJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("신규 TenantService 저장 성공")
        void shouldSaveNewTenantService() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.create();

            // when
            TenantServiceJpaEntity saved = repository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTenantId()).isEqualTo(entity.getTenantId());
            assertThat(saved.getServiceId()).isEqualTo(entity.getServiceId());
            assertThat(saved.getStatus()).isEqualTo(TenantServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("BaseAuditEntity 감사 필드 저장 검증")
        void shouldPersistAuditFields() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.create();

            // when
            TenantServiceJpaEntity saved = repository.save(entity);
            flushAndClear();

            // then
            TenantServiceJpaEntity found = repository.findById(saved.getId()).orElseThrow();
            assertThat(found.getCreatedAt()).isNotNull();
            assertThat(found.getUpdatedAt()).isNotNull();
            assertThat(found.getCreatedAt()).isEqualTo(TenantServiceJpaEntityFixture.fixedTime());
            assertThat(found.getUpdatedAt()).isEqualTo(TenantServiceJpaEntityFixture.fixedTime());
        }

        @Test
        @DisplayName("기존 TenantService 수정 성공")
        void shouldUpdateExistingTenantService() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.create();
            TenantServiceJpaEntity saved = repository.save(entity);
            flushAndClear();

            TenantServiceJpaEntity toUpdate =
                    TenantServiceJpaEntity.of(
                            saved.getId(),
                            saved.getTenantId(),
                            saved.getServiceId(),
                            TenantServiceStatus.SUSPENDED,
                            saved.getSubscribedAt(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt());

            // when
            TenantServiceJpaEntity updated = repository.save(toUpdate);
            flushAndClear();

            // then
            assertThat(updated.getId()).isEqualTo(saved.getId());
            assertThat(updated.getStatus()).isEqualTo(TenantServiceStatus.SUSPENDED);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 성공")
        void shouldFindById() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.create();
            TenantServiceJpaEntity saved = repository.save(entity);
            flushAndClear();

            // when
            var found = repository.findById(saved.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(saved.getId());
            assertThat(found.get().getTenantId()).isEqualTo(entity.getTenantId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void shouldReturnEmptyForNonExistentId() {
            // when
            var found = repository.findById(999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById 테스트")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID - true")
        void shouldReturnTrueForExistingId() {
            // given
            TenantServiceJpaEntity entity = TenantServiceJpaEntityFixture.create();
            TenantServiceJpaEntity saved = repository.save(entity);
            flushAndClear();

            // when
            boolean exists = repository.existsById(saved.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID - false")
        void shouldReturnFalseForNonExistentId() {
            // when
            boolean exists = repository.existsById(999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("복합 유니크 제약 테스트")
    class UniqueConstraintTest {

        @Test
        @DisplayName("동일한 테넌트-서비스 조합 중복 저장 시 예외 발생")
        void shouldThrowExceptionForDuplicateCombo() {
            // given
            TenantServiceJpaEntity entity1 =
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            repository.save(entity1);
            flushAndClear();

            // when & then
            TenantServiceJpaEntity entity2 =
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-123456789abc", 1L);

            org.junit.jupiter.api.Assertions.assertThrows(
                    Exception.class,
                    () -> {
                        repository.save(entity2);
                        flushAndClear();
                    });
        }

        @Test
        @DisplayName("다른 조합은 중복 허용")
        void shouldAllowDifferentCombos() {
            // given
            TenantServiceJpaEntity entity1 =
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L);
            TenantServiceJpaEntity entity2 =
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000002", 1L);
            TenantServiceJpaEntity entity3 =
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 2L);

            // when
            repository.save(entity1);
            repository.save(entity2);
            repository.save(entity3);
            flushAndClear();

            // then
            assertThat(repository.count()).isEqualTo(3);
        }
    }
}
