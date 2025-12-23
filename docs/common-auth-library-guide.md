# Common Auth Library 사용 가이드

> 버전: v1.0.0
> 최종 수정: 2025-12-23
> 대상: FileFlow, CrawlingHub, MarketPlace 등 마이크로서비스

---

## 1. 개요

`common-auth` 라이브러리는 AuthHub Gateway에서 전달하는 인증 정보를 파싱하고, 권한 체크를 수행하는 공통 유틸리티입니다.

### 1.1 주요 기능

| 기능 | 설명 |
|------|------|
| **UserContext** | 현재 인증된 사용자 정보 저장 |
| **GatewayHeaderParser** | Gateway에서 전달한 X-User-* 헤더 파싱 |
| **AccessChecker** | 권한/역할 검사 유틸리티 |
| **ScopeValidator** | Tenant/Organization 범위 검증 |
| **SecurityHeaders** | 헤더 이름 상수 정의 |

---

## 2. 설치

### 2.1 Gradle 설정

**build.gradle (루트 또는 allprojects)**:
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

**build.gradle (모듈)**:
```groovy
dependencies {
    implementation 'com.github.ryu-qqq:AuthHub:v1.0.0'
}
```

---

## 3. 패키지 구조

```
com.ryuqq.auth.common
├── context/
│   ├── UserContext.java           # 사용자 정보 DTO
│   └── UserContextHolder.java     # ThreadLocal 기반 컨텍스트 저장소
├── access/
│   ├── AccessChecker.java         # 권한 검사 인터페이스
│   └── BaseAccessChecker.java     # 기본 구현체
├── util/
│   ├── PermissionMatcher.java     # 권한 매칭 유틸 (와일드카드 지원)
│   └── ScopeValidator.java        # Scope 검증 유틸
├── constant/
│   ├── Roles.java                 # 역할 상수 (ROLE_SUPER_ADMIN 등)
│   ├── Permissions.java           # 권한 상수 (user:read 등)
│   └── Scopes.java                # Scope 상수 (GLOBAL, TENANT 등)
├── exception/
│   ├── SecurityException.java     # 보안 예외 기본 클래스
│   ├── AuthenticationException.java  # 인증 예외
│   └── AuthorizationException.java   # 인가 예외
└── header/
    ├── GatewayHeaderParser.java   # 헤더 파싱 유틸
    └── SecurityHeaders.java       # 헤더 이름 상수
```

---

## 4. 사용법

### 4.1 Gateway 헤더 파싱 (Filter에서)

```java
import com.ryuqq.auth.common.header.GatewayHeaderParser;
import com.ryuqq.auth.common.context.UserContext;
import com.ryuqq.auth.common.context.UserContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class GatewayAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Gateway 헤더 파싱
            UserContext context = GatewayHeaderParser.parse(httpRequest);

            // ThreadLocal에 저장
            UserContextHolder.set(context);

            chain.doFilter(request, response);
        } finally {
            // 반드시 정리 (메모리 누수 방지)
            UserContextHolder.clear();
        }
    }
}
```

### 4.2 현재 사용자 정보 조회

```java
import com.ryuqq.auth.common.context.UserContext;
import com.ryuqq.auth.common.context.UserContextHolder;

@Service
public class FileService {

    public void uploadFile(FileUploadRequest request) {
        // 현재 사용자 정보 가져오기
        UserContext context = UserContextHolder.get();

        Long userId = context.getUserId();
        Long tenantId = context.getTenantId();
        Long organizationId = context.getOrganizationId();
        Set<String> roles = context.getRoles();
        Set<String> permissions = context.getPermissions();

        // 비즈니스 로직...
    }
}
```

### 4.3 권한 체크

```java
import com.ryuqq.auth.common.access.AccessChecker;
import com.ryuqq.auth.common.constant.Permissions;

@Service
public class ProductService {

    private final AccessChecker accessChecker;

    public void deleteProduct(Long productId) {
        // 권한 체크 - 없으면 AuthorizationException 발생
        accessChecker.checkPermission(Permissions.PRODUCT_DELETE);

        // 삭제 로직...
    }

    public void updateProduct(Long productId, ProductRequest request) {
        // 여러 권한 중 하나 이상 필요
        accessChecker.checkAnyPermission(
            Permissions.PRODUCT_WRITE,
            Permissions.PRODUCT_ADMIN
        );

        // 수정 로직...
    }
}
```

### 4.4 역할 체크

```java
import com.ryuqq.auth.common.access.AccessChecker;
import com.ryuqq.auth.common.constant.Roles;

@Service
public class AdminService {

    private final AccessChecker accessChecker;

    public void manageUsers() {
        // SUPER_ADMIN 또는 TENANT_ADMIN 필요
        accessChecker.checkAnyRole(
            Roles.SUPER_ADMIN,
            Roles.TENANT_ADMIN
        );

        // 관리 로직...
    }
}
```

### 4.5 Scope 검증 (테넌트/조직 범위)

```java
import com.ryuqq.auth.common.util.ScopeValidator;

@Service
public class TenantService {

    private final ScopeValidator scopeValidator;

    public TenantInfo getTenantInfo(Long targetTenantId) {
        // 현재 사용자가 해당 테넌트에 접근 가능한지 검증
        scopeValidator.validateTenantAccess(targetTenantId);

        // 조회 로직...
    }

    public OrganizationInfo getOrgInfo(Long targetOrgId) {
        // 현재 사용자가 해당 조직에 접근 가능한지 검증
        scopeValidator.validateOrganizationAccess(targetOrgId);

        // 조회 로직...
    }
}
```

### 4.6 와일드카드 권한 매칭

```java
import com.ryuqq.auth.common.util.PermissionMatcher;

// 사용자 권한: ["product:*", "user:read"]

// product:read 권한 체크 → product:* 에 매칭되어 true
boolean canRead = PermissionMatcher.matches("product:read", userPermissions);

// user:write 권한 체크 → user:read만 있으므로 false
boolean canWrite = PermissionMatcher.matches("user:write", userPermissions);
```

---

## 5. 상수 클래스

### 5.1 Roles

```java
import com.ryuqq.auth.common.constant.Roles;

Roles.SUPER_ADMIN     // "ROLE_SUPER_ADMIN"
Roles.TENANT_ADMIN    // "ROLE_TENANT_ADMIN"
Roles.ORG_ADMIN       // "ROLE_ORG_ADMIN"
Roles.USER            // "ROLE_USER"
```

### 5.2 Permissions

```java
import com.ryuqq.auth.common.constant.Permissions;

// User 도메인
Permissions.USER_READ     // "user:read"
Permissions.USER_WRITE    // "user:write"
Permissions.USER_DELETE   // "user:delete"

// Product 도메인
Permissions.PRODUCT_READ  // "product:read"
Permissions.PRODUCT_WRITE // "product:write"

// 와일드카드
Permissions.ALL           // "*:*"
```

### 5.3 SecurityHeaders

```java
import com.ryuqq.auth.common.header.SecurityHeaders;

// Gateway가 전달하는 헤더
SecurityHeaders.USER_ID          // "X-User-Id"
SecurityHeaders.TENANT_ID        // "X-Tenant-Id"
SecurityHeaders.ORGANIZATION_ID  // "X-Organization-Id"
SecurityHeaders.ROLES            // "X-User-Roles"
SecurityHeaders.PERMISSIONS      // "X-User-Permissions"

// Service Token 헤더
SecurityHeaders.SERVICE_TOKEN    // "X-Service-Token"
SecurityHeaders.SERVICE_NAME     // "X-Service-Name"
```

---

## 6. Spring Boot 통합

### 6.1 필터 등록

```java
@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<GatewayAuthFilter> gatewayAuthFilter() {
        FilterRegistrationBean<GatewayAuthFilter> registration =
            new FilterRegistrationBean<>();
        registration.setFilter(new GatewayAuthFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
```

### 6.2 AccessChecker Bean 등록

```java
@Configuration
public class AuthConfig {

    @Bean
    public AccessChecker accessChecker() {
        return new BaseAccessChecker();
    }

    @Bean
    public ScopeValidator scopeValidator() {
        return new ScopeValidator();
    }
}
```

---

## 7. 예외 처리

### 7.1 예외 클래스

| 예외 | HTTP 상태 | 설명 |
|------|----------|------|
| `AuthenticationException` | 401 | 인증 실패 (토큰 없음/만료) |
| `AuthorizationException` | 403 | 인가 실패 (권한 부족) |
| `SecurityException` | 500 | 보안 관련 기타 오류 |

### 7.2 Global Exception Handler

```java
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(AuthorizationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("FORBIDDEN", ex.getMessage()));
    }
}
```

---

## 8. 테스트

### 8.1 단위 테스트에서 UserContext 설정

```java
@BeforeEach
void setUp() {
    UserContext context = UserContext.builder()
        .userId(1L)
        .tenantId(100L)
        .organizationId(200L)
        .roles(Set.of(Roles.USER))
        .permissions(Set.of(Permissions.PRODUCT_READ))
        .build();

    UserContextHolder.set(context);
}

@AfterEach
void tearDown() {
    UserContextHolder.clear();
}
```

### 8.2 통합 테스트에서 헤더 설정

```java
@Test
void shouldAccessWithValidPermission() {
    given()
        .header(SecurityHeaders.USER_ID, "1")
        .header(SecurityHeaders.TENANT_ID, "100")
        .header(SecurityHeaders.ROLES, "ROLE_USER")
        .header(SecurityHeaders.PERMISSIONS, "product:read")
    .when()
        .get("/api/v1/products")
    .then()
        .statusCode(200);
}
```

---

## 9. 마이그레이션 가이드

### 9.1 기존 프로젝트에 적용

1. **build.gradle에 의존성 추가**
2. **GatewayAuthFilter 구현 및 등록**
3. **기존 권한 체크 코드를 AccessChecker로 교체**
4. **SecurityHeaders 상수 사용으로 하드코딩 제거**

### 9.2 버전 업그레이드

```groovy
// v1.0.0 → v1.1.0 업그레이드
implementation 'com.github.ryu-qqq:AuthHub:v1.1.0'
```

---

## 10. 관련 문서

- [Gateway Authorization Spec](./prd/gateway-authorization-spec.md)
- [IAM System Improvement Plan](./prd/iam-system-improvement-plan.md)
- [Service Token Guide](./coding_convention/01-adapter-in-layer/rest-api/security/service-token-guide.md)

---

## 11. FAQ

### Q1: Gateway 없이도 사용할 수 있나요?
A: 네, 직접 UserContext를 생성하여 UserContextHolder에 설정할 수 있습니다.

### Q2: 권한 상수에 없는 커스텀 권한은 어떻게 하나요?
A: 문자열로 직접 사용하거나, 프로젝트 내에 상수 클래스를 확장하세요.

### Q3: 비동기 처리에서도 UserContext가 유지되나요?
A: 기본적으로 ThreadLocal이므로 스레드가 바뀌면 유지되지 않습니다.
   `@Async` 사용 시 별도 전파 로직이 필요합니다.
