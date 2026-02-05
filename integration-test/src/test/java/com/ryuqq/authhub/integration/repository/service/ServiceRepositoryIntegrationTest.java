package com.ryuqq.authhub.integration.repository.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceJpaRepository;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
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
 * ServiceJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.SERVICE)
class ServiceRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private ServiceJpaRepository serviceJpaRepository;

    @BeforeEach
    void setUp() {
        serviceJpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("활성 서비스를 저장할 수 있다")
        void shouldSaveActiveService() {
            // given
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            null,
                            "SVC_STORE",
                            "자사몰",
                            "자사몰 서비스",
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime());

            // when
            ServiceJpaEntity saved = serviceJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getServiceId()).isNotNull();

            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(saved.getServiceId());
            assertThat(found).isPresent();
            assertThat(found.get().getServiceCode()).isEqualTo("SVC_STORE");
            assertThat(found.get().getName()).isEqualTo("자사몰");
            assertThat(found.get().getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 서비스를 저장할 수 있다")
        void shouldSaveInactiveService() {
            // given
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            null,
                            "SVC_INACTIVE",
                            "비활성 서비스",
                            "비활성 서비스 설명",
                            ServiceStatus.INACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime());

            // when
            serviceJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(entity.getServiceId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(ServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("설명 없는 서비스를 저장할 수 있다")
        void shouldSaveServiceWithoutDescription() {
            // given
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            null,
                            "SVC_NO_DESC",
                            "설명 없는 서비스",
                            null,
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime());

            // when
            serviceJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(entity.getServiceId());
            assertThat(found).isPresent();
            assertThat(found.get().getDescription()).isNull();
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 서비스를 ID로 조회할 수 있다")
        void shouldFindExistingService() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createNew();
            serviceJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(entity.getServiceId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getServiceCode()).isEqualTo("SVC_NEW");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // when
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("update 테스트")
    class UpdateTest {

        @Test
        @DisplayName("서비스 정보를 수정할 수 있다")
        void shouldUpdateService() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createNew();
            serviceJpaRepository.save(entity);
            flushAndClear();

            // when
            ServiceJpaEntity updated =
                    ServiceJpaEntity.of(
                            entity.getServiceId(),
                            entity.getServiceCode(),
                            "수정된 서비스명",
                            "수정된 설명",
                            ServiceStatus.INACTIVE,
                            entity.getCreatedAt(),
                            ServiceJpaEntityFixture.fixedTime());
            serviceJpaRepository.save(updated);
            flushAndClear();

            // then
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(entity.getServiceId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("수정된 서비스명");
            assertThat(found.get().getDescription()).isEqualTo("수정된 설명");
            assertThat(found.get().getStatus()).isEqualTo(ServiceStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("서비스를 삭제할 수 있다")
        void shouldDeleteService() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createNew();
            serviceJpaRepository.save(entity);
            flushAndClear();

            // when
            serviceJpaRepository.deleteById(entity.getServiceId());
            flushAndClear();

            // then
            Optional<ServiceJpaEntity> found = serviceJpaRepository.findById(entity.getServiceId());
            assertThat(found).isEmpty();
        }
    }
}
