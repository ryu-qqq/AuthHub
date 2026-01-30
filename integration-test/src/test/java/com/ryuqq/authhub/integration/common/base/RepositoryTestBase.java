package com.ryuqq.authhub.integration.common.base;

import com.ryuqq.authhub.integration.common.config.RepositoryTestConfig;
import com.ryuqq.authhub.integration.common.config.TestClockConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository 레이어 통합 테스트 기반 클래스.
 *
 * <p>특징:
 *
 * <ul>
 *   <li>@DataJpaTest: JPA 관련 빈만 로드 (가볍고 빠름)
 *   <li>H2 인메모리 DB 사용 (MySQL 호환 모드)
 *   <li>트랜잭션 자동 롤백
 *   <li>Fixed Clock 사용 (시간 의존 테스트 일관성)
 * </ul>
 *
 * <p>사용 예:
 *
 * <pre>{@code
 * @Tag(TestTags.REPOSITORY)
 * @Tag(TestTags.USER)
 * class UserRepositoryTest extends RepositoryTestBase {
 *
 *     @Autowired
 *     private UserJpaRepository userJpaRepository;
 *
 *     @Test
 *     void shouldSaveUser() {
 *         // given
 *         UserJpaEntity entity = UserJpaEntityFixture.create();
 *
 *         // when
 *         userJpaRepository.save(entity);
 *         flushAndClear();
 *
 *         // then
 *         assertThat(userJpaRepository.findById(entity.getId())).isPresent();
 *     }
 * }
 * }</pre>
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestClockConfig.class, RepositoryTestConfig.class})
public abstract class RepositoryTestBase {

    @PersistenceContext protected EntityManager entityManager;

    /** 영속성 컨텍스트를 flush하고 clear. DB에서 실제 조회하여 검증할 때 사용. */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 엔티티 영속화.
     *
     * @param entity 영속화할 엔티티
     * @param <T> 엔티티 타입
     * @return 영속화된 엔티티
     */
    protected <T> T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    /**
     * 엔티티 영속화 후 flush & clear.
     *
     * @param entity 영속화할 엔티티
     * @param <T> 엔티티 타입
     * @return 영속화된 엔티티
     */
    protected <T> T persistAndFlush(T entity) {
        entityManager.persist(entity);
        flushAndClear();
        return entity;
    }

    /**
     * ID로 엔티티 조회.
     *
     * @param entityClass 엔티티 클래스
     * @param id 엔티티 ID
     * @param <T> 엔티티 타입
     * @return 조회된 엔티티 (없으면 null)
     */
    protected <T> T find(Class<T> entityClass, Object id) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Native SQL 실행. 테스트 데이터 정리 등에 사용.
     *
     * @param sql 실행할 SQL
     */
    protected void executeNativeSql(String sql) {
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
