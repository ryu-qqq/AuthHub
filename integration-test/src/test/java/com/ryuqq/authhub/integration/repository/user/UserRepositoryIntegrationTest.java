package com.ryuqq.authhub.integration.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
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
 * UserJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.USER)
class UserRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;
    private OrganizationJpaEntity savedOrganization;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 정리
        userJpaRepository.deleteAll();
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        // 부모 엔티티 생성 (FK 관계)
        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationJpaRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));

        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("활성 사용자를 저장할 수 있다")
        void shouldSaveActiveUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "test@example.com",
                            "010-1234-5678",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);

            // when
            UserJpaEntity saved = userJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getUserId()).isEqualTo(userId);

            Optional<UserJpaEntity> found = userJpaRepository.findById(userId);
            assertThat(found).isPresent();
            assertThat(found.get().getIdentifier()).isEqualTo("test@example.com");
            assertThat(found.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("전화번호 없이 사용자를 저장할 수 있다")
        void shouldSaveUserWithoutPhoneNumber() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "nophone@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);

            // when
            userJpaRepository.save(entity);
            flushAndClear();

            // then
            Optional<UserJpaEntity> found = userJpaRepository.findById(userId);
            assertThat(found).isPresent();
            assertThat(found.get().getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 사용자를 ID로 조회할 수 있다")
        void shouldFindExistingUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "findme@example.com",
                            "010-1234-5678",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);
            userJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<UserJpaEntity> found = userJpaRepository.findById(userId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getIdentifier()).isEqualTo("findme@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // given
            String nonExistentId = UUID.randomUUID().toString();

            // when
            Optional<UserJpaEntity> found = userJpaRepository.findById(nonExistentId);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("사용자를 삭제할 수 있다")
        void shouldDeleteUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "deleteme@example.com",
                            "010-1234-5678",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);
            userJpaRepository.save(entity);
            flushAndClear();

            // when
            userJpaRepository.deleteById(userId);
            flushAndClear();

            // then
            Optional<UserJpaEntity> found = userJpaRepository.findById(userId);
            assertThat(found).isEmpty();
        }
    }
}
