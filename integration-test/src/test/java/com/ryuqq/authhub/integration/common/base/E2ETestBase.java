package com.ryuqq.authhub.integration.common.base;

import com.ryuqq.authhub.bootstrap.AuthHubWebApiApplication;
import com.ryuqq.authhub.integration.common.config.TestClockConfig;
import com.ryuqq.authhub.integration.common.config.TestQueryDslConfig;
import com.ryuqq.authhub.integration.common.config.TestSecurityConfig;
import com.ryuqq.authhub.integration.common.config.TestUseCaseConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * E2E API 통합 테스트 기반 클래스.
 *
 * <p>특징:
 *
 * <ul>
 *   <li>@SpringBootTest: 전체 애플리케이션 컨텍스트 로드
 *   <li>RestAssured: 실제 HTTP 요청/응답 테스트
 *   <li>H2 인메모리 DB (MySQL 호환 모드)
 *   <li>Gateway 헤더 기반 인증
 * </ul>
 *
 * <p>사용 예:
 *
 * <pre>{@code
 * @Tag(TestTags.E2E)
 * @Tag(TestTags.USER)
 * class UserE2ETest extends E2ETestBase {
 *
 *     @Autowired
 *     private UserJpaRepository userJpaRepository;
 *
 *     @BeforeEach
 *     void setUp() {
 *         userJpaRepository.deleteAll();
 *     }
 *
 *     @Test
 *     void shouldCreateUser() {
 *         // given
 *         Map<String, Object> request = Map.of(
 *             "email", "test@example.com",
 *             "name", "Test User"
 *         );
 *
 *         // when & then
 *         givenAuthenticated("admin-user-id", "tenant-1")
 *             .body(request)
 *             .when()
 *             .post("/api/v1/users")
 *             .then()
 *             .statusCode(201)
 *             .body("data.email", equalTo("test@example.com"));
 *     }
 * }
 * }</pre>
 */
@SpringBootTest(
        classes = AuthHubWebApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({
    TestClockConfig.class,
    TestSecurityConfig.class,
    TestQueryDslConfig.class,
    TestUseCaseConfig.class
})
public abstract class E2ETestBase {

    // ========================================
    // Gateway Header Constants
    // ========================================
    protected static final String HEADER_USER_ID = "X-User-Id";
    protected static final String HEADER_TENANT_ID = "X-Tenant-Id";
    protected static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    protected static final String HEADER_ROLES = "X-User-Roles";
    protected static final String HEADER_PERMISSIONS = "X-Permissions";
    protected static final String HEADER_TRACE_ID = "X-Trace-Id";

    // ========================================
    // Default Test Values
    // ========================================
    protected static final String DEFAULT_USER_ID = "test-user-id";
    protected static final String DEFAULT_TENANT_ID = "test-tenant-id";
    protected static final String DEFAULT_ORG_ID = "test-org-id";
    protected static final String DEFAULT_ROLE = "ROLE_USER";
    protected static final String DEFAULT_TRACE_ID = "test-trace-id";

    @LocalServerPort protected int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    // ========================================
    // Request Specification Builders
    // ========================================

    /**
     * JSON Content-Type 요청 명세.
     *
     * @return RequestSpecification
     */
    protected RequestSpecification givenJson() {
        return RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    /**
     * 인증된 요청 명세 (기본값 사용).
     *
     * @return 기본 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenAuthenticated() {
        return givenAuthenticated(DEFAULT_USER_ID, DEFAULT_TENANT_ID);
    }

    /**
     * 인증된 요청 명세 (사용자 ID, 테넌트 ID 지정).
     *
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @return 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenAuthenticated(String userId, String tenantId) {
        return givenJson()
                .header(HEADER_USER_ID, userId)
                .header(HEADER_TENANT_ID, tenantId)
                .header(HEADER_ROLES, DEFAULT_ROLE)
                .header(HEADER_TRACE_ID, DEFAULT_TRACE_ID);
    }

    /**
     * 인증된 요청 명세 (전체 정보 지정).
     *
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param roles 역할 (콤마 구분)
     * @param permissions 권한 (콤마 구분)
     * @return 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenAuthenticated(
            String userId,
            String tenantId,
            String organizationId,
            String roles,
            String permissions) {
        return givenJson()
                .header(HEADER_USER_ID, userId)
                .header(HEADER_TENANT_ID, tenantId)
                .header(HEADER_ORGANIZATION_ID, organizationId)
                .header(HEADER_ROLES, roles)
                .header(HEADER_PERMISSIONS, permissions)
                .header(HEADER_TRACE_ID, DEFAULT_TRACE_ID);
    }

    /**
     * 관리자 권한 요청 명세.
     *
     * @return 관리자 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenAdmin() {
        return givenJson()
                .header(HEADER_USER_ID, "admin-user-id")
                .header(HEADER_TENANT_ID, DEFAULT_TENANT_ID)
                .header(HEADER_ROLES, "ROLE_ADMIN")
                .header(HEADER_TRACE_ID, DEFAULT_TRACE_ID);
    }

    /**
     * 관리자 권한 요청 명세 (테넌트 지정).
     *
     * @param tenantId 테넌트 ID
     * @return 관리자 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenAdmin(String tenantId) {
        return givenJson()
                .header(HEADER_USER_ID, "admin-user-id")
                .header(HEADER_TENANT_ID, tenantId)
                .header(HEADER_ROLES, "ROLE_ADMIN")
                .header(HEADER_TRACE_ID, DEFAULT_TRACE_ID);
    }

    /**
     * 시스템 관리자 권한 요청 명세. 모든 테넌트에 접근 가능한 슈퍼 관리자.
     *
     * @return 시스템 관리자 인증 정보가 포함된 RequestSpecification
     */
    protected RequestSpecification givenSystemAdmin() {
        return givenJson()
                .header(HEADER_USER_ID, "system-admin-id")
                .header(HEADER_TENANT_ID, "system")
                .header(HEADER_ROLES, "ROLE_SYSTEM_ADMIN")
                .header(HEADER_TRACE_ID, DEFAULT_TRACE_ID);
    }
}
