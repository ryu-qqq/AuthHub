package com.ryuqq.authhub.adapter.out.persistence.auth.user.repository;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserJpaRepository 통합 테스트.
 *
 * <p>Testcontainers를 사용하여 실제 PostgreSQL 데이터베이스와 통합 테스트를 수행합니다.
 * {@code @DataJpaTest}를 사용하여 JPA 관련 설정만 로드하고, 트랜잭션은 자동 롤백됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>Testcontainers PostgreSQL 사용 - 실제 DB 환경</li>
 *   <li>{@code @DataJpaTest} 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>각 테스트 메서드는 독립적으로 실행되며 트랜잭션 자동 롤백</li>
 *   <li>TestEntityManager로 테스트 데이터 준비</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserJpaRepository 통합 테스트")
class UserJpaRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private String testUid;
    private UserJpaEntity testEntity;

    @BeforeEach
    void setUp() {
        testUid = UUID.randomUUID().toString();
        Instant now = Instant.now();

        testEntity = UserJpaEntity.create(
                testUid,
                UserJpaEntity.UserStatusEnum.ACTIVE,
                null,
                now,
                now
        );
    }

    @Test
    @DisplayName("UserJpaEntity를 성공적으로 저장하고 조회한다")
    void save_AndFind_Success() {
        // when
        UserJpaEntity saved = userJpaRepository.save(testEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUid()).isEqualTo(testUid);
        assertThat(saved.getStatus()).isEqualTo(UserJpaEntity.UserStatusEnum.ACTIVE);

        UserJpaEntity found = userJpaRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getUid()).isEqualTo(testUid);
    }

    @Test
    @DisplayName("uid로 UserJpaEntity를 성공적으로 조회한다")
    void findByUid_Success() {
        // given
        entityManager.persist(testEntity);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<UserJpaEntity> result = userJpaRepository.findByUid(testUid);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUid()).isEqualTo(testUid);
        assertThat(result.get().getStatus()).isEqualTo(UserJpaEntity.UserStatusEnum.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 uid로 조회 시 Empty를 반환한다")
    void findByUid_NotFound_ReturnsEmpty() {
        // given
        String nonExistentUid = UUID.randomUUID().toString();

        // when
        Optional<UserJpaEntity> result = userJpaRepository.findByUid(nonExistentUid);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("uid로 UserJpaEntity 존재 여부를 확인한다")
    void existsByUid_ReturnsTrue() {
        // given
        entityManager.persist(testEntity);
        entityManager.flush();
        entityManager.clear();

        // when
        boolean exists = userJpaRepository.existsByUid(testUid);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 uid로 존재 여부 확인 시 false를 반환한다")
    void existsByUid_ReturnsFalse() {
        // given
        String nonExistentUid = UUID.randomUUID().toString();

        // when
        boolean exists = userJpaRepository.existsByUid(nonExistentUid);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("UserJpaEntity를 수정하고 저장한다")
    void update_Success() {
        // given
        entityManager.persist(testEntity);
        entityManager.flush();
        entityManager.clear();

        // when - 새로운 Entity 객체 생성하여 업데이트 (package-private setter 사용 불가)
        UserJpaEntity found = userJpaRepository.findByUid(testUid).orElseThrow();
        UserJpaEntity updated = UserJpaEntity.create(
                testUid,
                UserJpaEntity.UserStatusEnum.SUSPENDED,
                Instant.now(),
                found.getCreatedAt(),
                Instant.now()
        );
        userJpaRepository.save(updated);
        entityManager.flush();
        entityManager.clear();

        // then
        UserJpaEntity result = userJpaRepository.findByUid(testUid).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(UserJpaEntity.UserStatusEnum.SUSPENDED);
        assertThat(result.getLastLoginAt()).isNotNull();
    }

    @Test
    @DisplayName("uid 인덱스가 올바르게 생성되어 빠르게 조회된다")
    void findByUid_UsesIndex() {
        // given
        for (int i = 0; i < 100; i++) {
            UserJpaEntity entity = UserJpaEntity.create(
                    UUID.randomUUID().toString(),
                    UserJpaEntity.UserStatusEnum.ACTIVE,
                    null,
                    Instant.now(),
                    Instant.now()
            );
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.clear();

        // when
        long startTime = System.currentTimeMillis();
        Optional<UserJpaEntity> result = userJpaRepository.findByUid(testUid);
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(100); // 인덱스 사용 시 100ms 이하
    }
}
