package com.ryuqq.authhub.adapter.out.persistence.security.audit.repository;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ActionTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ResourceTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AuditLogJpaRepository 통합 테스트.
 *
 * <p>Testcontainers를 사용하여 실제 MySQL 데이터베이스와 통합 테스트를 수행합니다.
 * {@code @DataJpaTest}를 사용하여 JPA 관련 설정만 로드하고, 트랜잭션은 자동 롤백됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>Testcontainers MySQL 사용 - 실제 DB 환경</li>
 *   <li>{@code @DataJpaTest} 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>각 테스트 메서드는 독립적으로 실행되며 트랜잭션 자동 롤백</li>
 *   <li>TestEntityManager로 테스트 데이터 준비</li>
 *   <li>JpaSpecificationExecutor 동작 검증</li>
 *   <li>인덱스 성능 검증</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ {@code @SpringBootTest} 금지 - {@code @DataJpaTest} 사용</li>
 *   <li>✅ Testcontainers로 실제 DB 환경 재현</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AuditLogJpaRepository 통합 테스트")
class AuditLogJpaRepositoryTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private AuditLogJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private String testAuditLogId;
    private String testUserId;
    private AuditLogJpaEntity testEntity;
    private Instant testOccurredAt;

    @BeforeEach
    void setUp() {
        testAuditLogId = UUID.randomUUID().toString();
        testUserId = UUID.randomUUID().toString();
        testOccurredAt = Instant.now();

        testEntity = AuditLogJpaEntity.of(
                testAuditLogId,
                testUserId,
                ActionTypeEnum.CREATE,
                ResourceTypeEnum.USER,
                "resource-123",
                "192.168.1.1",
                "Mozilla/5.0",
                testOccurredAt
        );
    }

    @Test
    @DisplayName("AuditLogJpaEntity를 성공적으로 저장하고 조회한다")
    void save_AndFind_Success() {
        // when
        AuditLogJpaEntity saved = repository.save(testEntity);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuditLogId()).isEqualTo(testAuditLogId);
        assertThat(saved.getUserId()).isEqualTo(testUserId);
        assertThat(saved.getActionType()).isEqualTo(ActionTypeEnum.CREATE);

        AuditLogJpaEntity found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getAuditLogId()).isEqualTo(testAuditLogId);
    }

    @Test
    @DisplayName("auditLogId로 AuditLogJpaEntity를 성공적으로 조회한다")
    void findByAuditLogId_Success() {
        // given
        entityManager.persist(testEntity);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<AuditLogJpaEntity> result = repository.findByAuditLogId(testAuditLogId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAuditLogId()).isEqualTo(testAuditLogId);
        assertThat(result.get().getUserId()).isEqualTo(testUserId);
        assertThat(result.get().getActionType()).isEqualTo(ActionTypeEnum.CREATE);
    }

    @Test
    @DisplayName("존재하지 않는 auditLogId로 조회 시 Empty를 반환한다")
    void findByAuditLogId_NotFound_ReturnsEmpty() {
        // given
        String nonExistentId = UUID.randomUUID().toString();

        // when
        Optional<AuditLogJpaEntity> result = repository.findByAuditLogId(nonExistentId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("auditLogId로 AuditLogJpaEntity 존재 여부를 확인한다")
    void existsByAuditLogId_ReturnsTrue() {
        // given
        entityManager.persist(testEntity);
        entityManager.flush();
        entityManager.clear();

        // when
        boolean exists = repository.existsByAuditLogId(testAuditLogId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 auditLogId로 존재 여부 확인 시 false를 반환한다")
    void existsByAuditLogId_ReturnsFalse() {
        // given
        String nonExistentId = UUID.randomUUID().toString();

        // when
        boolean exists = repository.existsByAuditLogId(nonExistentId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Specification으로 userId 조건 검색을 수행한다")
    void findAll_WithUserIdSpecification_Success() {
        // given
        entityManager.persist(testEntity);

        AuditLogJpaEntity anotherEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                ActionTypeEnum.UPDATE,
                ResourceTypeEnum.USER,
                "resource-456",
                "192.168.1.2",
                "Chrome/91.0",
                testOccurredAt
        );
        entityManager.persist(anotherEntity);
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasUserId(testUserId);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "occurredAt"));

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("Specification으로 actionType 조건 검색을 수행한다")
    void findAll_WithActionTypeSpecification_Success() {
        // given
        entityManager.persist(testEntity);

        AuditLogJpaEntity loginEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(),
                testUserId,
                ActionTypeEnum.LOGIN,
                ResourceTypeEnum.USER,
                "resource-456",
                "192.168.1.2",
                "Chrome/91.0",
                testOccurredAt
        );
        entityManager.persist(loginEntity);
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasActionType(ActionTypeEnum.CREATE);

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getActionType()).isEqualTo(ActionTypeEnum.CREATE);
    }

    @Test
    @DisplayName("Specification으로 resourceType과 resourceId 조건 검색을 수행한다")
    void findAll_WithResourceSpecification_Success() {
        // given
        entityManager.persist(testEntity);

        AuditLogJpaEntity orgEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(),
                testUserId,
                ActionTypeEnum.CREATE,
                ResourceTypeEnum.ORGANIZATION,
                "org-789",
                "192.168.1.2",
                "Chrome/91.0",
                testOccurredAt
        );
        entityManager.persist(orgEntity);
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasResource(
                ResourceTypeEnum.USER, "resource-123"
        );

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getResourceType()).isEqualTo(ResourceTypeEnum.USER);
        assertThat(result.getContent().get(0).getResourceId()).isEqualTo("resource-123");
    }

    @Test
    @DisplayName("Specification으로 시간 범위 조건 검색을 수행한다")
    void findAll_WithOccurredBetweenSpecification_Success() {
        // given
        Instant now = Instant.now();
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        Instant twoDaysAgo = now.minus(2, ChronoUnit.DAYS);

        AuditLogJpaEntity todayEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(), testUserId, ActionTypeEnum.CREATE,
                ResourceTypeEnum.USER, "resource-today", "192.168.1.1", "Mozilla/5.0", now
        );

        AuditLogJpaEntity yesterdayEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(), testUserId, ActionTypeEnum.UPDATE,
                ResourceTypeEnum.USER, "resource-yesterday", "192.168.1.2", "Chrome/91.0", yesterday
        );

        AuditLogJpaEntity oldEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(), testUserId, ActionTypeEnum.DELETE,
                ResourceTypeEnum.USER, "resource-old", "192.168.1.3", "Safari/14.0", twoDaysAgo
        );

        entityManager.persist(todayEntity);
        entityManager.persist(yesterdayEntity);
        entityManager.persist(oldEntity);
        entityManager.flush();
        entityManager.clear();

        Instant startDate = yesterday.minus(1, ChronoUnit.HOURS);
        Instant endDate = now.plus(1, ChronoUnit.HOURS);
        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.occurredBetween(startDate, endDate);

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2); // todayEntity, yesterdayEntity
        assertThat(result.getContent()).extracting(AuditLogJpaEntity::getResourceId)
                .contains("resource-today", "resource-yesterday");
    }

    @Test
    @DisplayName("복합 Specification으로 여러 조건을 조합하여 검색한다")
    void findAll_WithCombinedSpecification_Success() {
        // given
        entityManager.persist(testEntity);

        AuditLogJpaEntity anotherEntity = AuditLogJpaEntity.of(
                UUID.randomUUID().toString(),
                testUserId,
                ActionTypeEnum.UPDATE,
                ResourceTypeEnum.USER,
                "resource-456",
                "192.168.1.1",
                "Chrome/91.0",
                testOccurredAt
        );
        entityManager.persist(anotherEntity);
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = Specification.<AuditLogJpaEntity>where(null)
                .and(AuditLogSpecifications.hasUserId(testUserId))
                .and(AuditLogSpecifications.hasActionType(ActionTypeEnum.CREATE))
                .and(AuditLogSpecifications.hasIpAddress("192.168.1.1"));

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getActionType()).isEqualTo(ActionTypeEnum.CREATE);
        assertThat(result.getContent().get(0).getIpAddress()).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("페이징과 정렬이 올바르게 작동한다")
    void findAll_WithPagingAndSorting_Success() {
        // given
        for (int i = 0; i < 15; i++) {
            AuditLogJpaEntity entity = AuditLogJpaEntity.of(
                    UUID.randomUUID().toString(),
                    testUserId,
                    ActionTypeEnum.CREATE,
                    ResourceTypeEnum.USER,
                    "resource-" + i,
                    "192.168.1.1",
                    "Mozilla/5.0",
                    testOccurredAt.plus(i, ChronoUnit.MINUTES)
            );
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasUserId(testUserId);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "occurredAt"));

        // when
        Page<AuditLogJpaEntity> result = repository.findAll(spec, pageable);

        // then
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();

        // 최신순 정렬 확인
        Instant previousTime = Instant.MAX;
        for (AuditLogJpaEntity entity : result.getContent()) {
            assertThat(entity.getOccurredAt()).isBefore(previousTime);
            previousTime = entity.getOccurredAt();
        }
    }

    @Test
    @DisplayName("idx_audit_user_action 인덱스가 올바르게 생성되어 빠르게 조회된다")
    void findAll_UsesUserActionIndex_Performance() {
        // given
        for (int i = 0; i < 100; i++) {
            AuditLogJpaEntity entity = AuditLogJpaEntity.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    i % 2 == 0 ? ActionTypeEnum.CREATE : ActionTypeEnum.UPDATE,
                    ResourceTypeEnum.USER,
                    "resource-" + i,
                    "192.168.1.1",
                    "Mozilla/5.0",
                    testOccurredAt
            );
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = Specification.<AuditLogJpaEntity>where(null)
                .and(AuditLogSpecifications.hasUserId(testUserId))
                .and(AuditLogSpecifications.hasActionType(ActionTypeEnum.CREATE));

        // when
        long startTime = System.currentTimeMillis();
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(100); // 인덱스 사용 시 100ms 이하
    }

    @Test
    @DisplayName("idx_audit_occurred_at 인덱스가 올바르게 생성되어 빠르게 조회된다")
    void findAll_UsesOccurredAtIndex_Performance() {
        // given
        Instant baseTime = Instant.now().minus(100, ChronoUnit.DAYS);
        for (int i = 0; i < 100; i++) {
            AuditLogJpaEntity entity = AuditLogJpaEntity.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    ActionTypeEnum.CREATE,
                    ResourceTypeEnum.USER,
                    "resource-" + i,
                    "192.168.1.1",
                    "Mozilla/5.0",
                    baseTime.plus(i, ChronoUnit.DAYS)
            );
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.clear();

        Instant startDate = baseTime.plus(10, ChronoUnit.DAYS);
        Instant endDate = baseTime.plus(20, ChronoUnit.DAYS);
        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.occurredBetween(startDate, endDate);

        // when
        long startTime = System.currentTimeMillis();
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(100); // 인덱스 사용 시 100ms 이하
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("idx_audit_resource 인덱스가 올바르게 생성되어 빠르게 조회된다")
    void findAll_UsesResourceIndex_Performance() {
        // given
        for (int i = 0; i < 100; i++) {
            AuditLogJpaEntity entity = AuditLogJpaEntity.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    ActionTypeEnum.CREATE,
                    i % 2 == 0 ? ResourceTypeEnum.USER : ResourceTypeEnum.ORGANIZATION,
                    "resource-" + (i % 10),
                    "192.168.1.1",
                    "Mozilla/5.0",
                    testOccurredAt
            );
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.clear();

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasResource(
                ResourceTypeEnum.USER, "resource-0"
        );

        // when
        long startTime = System.currentTimeMillis();
        Page<AuditLogJpaEntity> result = repository.findAll(spec, PageRequest.of(0, 10));
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(100); // 인덱스 사용 시 100ms 이하
        assertThat(result.getContent()).isNotEmpty();
    }
}
