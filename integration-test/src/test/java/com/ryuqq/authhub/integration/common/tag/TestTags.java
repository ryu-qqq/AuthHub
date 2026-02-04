package com.ryuqq.authhub.integration.common.tag;

/**
 * JUnit 5 테스트 태그 상수. Gradle 태스크에서 태그 기반 필터링에 사용.
 *
 * <pre>
 * ./gradlew :integration-test:repositoryTest  # @Tag("repository")
 * ./gradlew :integration-test:e2eTest         # @Tag("e2e")
 * ./gradlew :integration-test:fastTest        # e2e, slow 제외
 * </pre>
 */
public final class TestTags {

    private TestTags() {}

    /** Repository 레이어 통합 테스트. @DataJpaTest 기반, H2 인메모리 DB 사용. */
    public static final String REPOSITORY = "repository";

    /** E2E API 통합 테스트. @SpringBootTest 기반, 전체 컨텍스트 로드. */
    public static final String E2E = "e2e";

    /** 느린 테스트 (30초 이상 소요). fastTest에서 제외됨. */
    public static final String SLOW = "slow";

    // ========================================
    // Domain-specific Tags (선택적 사용)
    // ========================================

    /** User 도메인 관련 테스트. */
    public static final String USER = "user";

    /** Role 도메인 관련 테스트. */
    public static final String ROLE = "role";

    /** Permission 도메인 관련 테스트. */
    public static final String PERMISSION = "permission";

    /** Tenant 도메인 관련 테스트. */
    public static final String TENANT = "tenant";

    /** Organization 도메인 관련 테스트. */
    public static final String ORGANIZATION = "organization";

    /** Auth/Token 관련 테스트. */
    public static final String AUTH = "auth";

    /** Service 도메인 관련 테스트. */
    public static final String SERVICE = "service";

    /** TenantService 도메인 관련 테스트. */
    public static final String TENANT_SERVICE = "tenant_service";
}
