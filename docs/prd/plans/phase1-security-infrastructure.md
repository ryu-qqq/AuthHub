# Phase 1.1: 보안 인프라 상세 설계

## 1. 개요

### 1.1 현재 상태 분석

**기존 JwtAuthenticationFilter 문제점:**
- JWT 토큰을 직접 검증 → Gateway에서 이미 검증 완료 (중복!)
- X-User-Id, X-Tenant-Id, X-Roles 헤더 무시
- Gateway-AuthHub 아키텍처와 맞지 않음

**목표 상태 (Gateway 연동):**
```
Client → Gateway (JWT 검증) → AuthHub (헤더 신뢰)
                ↓
         X-User-Id: 123
         X-Tenant-Id: 456
         X-Roles: ["ROLE_TENANT_ADMIN"]
         X-Trace-Id: uuid
```

### 1.2 Gateway vs AuthHub 역할 분담

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           전체 인증/인가 플로우                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────┐         ┌──────────────┐         ┌──────────────┐            │
│  │  Client  │ ──────→ │   Gateway    │ ──────→ │   AuthHub    │            │
│  └──────────┘         └──────────────┘         └──────────────┘            │
│                              │                        │                     │
│                       ┌──────┴──────┐          ┌──────┴──────┐             │
│                       │  1차 인증    │          │  2차 인가    │             │
│                       │(Authentication)│       │(Authorization)│            │
│                       └─────────────┘          └─────────────┘             │
│                              │                        │                     │
│                       - JWT 검증                - 역할별 API 접근 제어       │
│                       - 토큰 유효성             - 테넌트 데이터 격리         │
│                       - 사용자 정보 추출        - 리소스 소유자 검증         │
│                       - 헤더로 전달             - 비즈니스 권한 검증         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

| 구분 | Gateway | AuthHub |
|------|---------|---------|
| **JWT 검증** | ✅ 수행 | ❌ 하지 않음 (Gateway 신뢰) |
| **역할** | 1차 인증 (Authentication) | 2차 인가 (Authorization) |
| **헤더** | X-User-Id, X-Tenant-Id, X-Roles 추가 | 헤더 파싱 → SecurityContext |
| **Public API** | JWT 검증 스킵, 헤더 없음 | Anonymous 처리 |

### 1.3 설계 목표

1. **Gateway 헤더 기반 인증**
   - Gateway에서 전달하는 X-* 헤더 신뢰
   - JWT 재검증 불필요 (Gateway가 이미 검증)
   - Public API는 헤더 없음 → Anonymous 처리

2. **멀티테넌트 격리**
   - SecurityContextHolder에서 tenantId 추출
   - **Persistence Layer**에서 tenantId 필터 적용

3. **역할 기반 접근 제어 (RBAC)**
   - **Controller**에서 @PreAuthorize로 역할 검증
   - 계층적 역할: SUPER_ADMIN > TENANT_ADMIN > ORG_ADMIN > USER

---

## 2. 상황별 플로우

### 2.1 로그인 요청 (Public API)

```
Client                    Gateway                     AuthHub
  │                          │                           │
  │ POST /api/v1/auth/login  │                           │
  │─────────────────────────→│                           │
  │                          │                           │
  │                          │ 🔓 Public 경로            │
  │                          │ JWT 검증 SKIP             │
  │                          │ 헤더 추가 없이 전달        │
  │                          │──────────────────────────→│
  │                          │                           │
  │                          │                           │ 로그인 처리
  │                          │                           │ JWT 발급
  │                          │                           │
  │                          │←──────────────────────────│
  │←─────────────────────────│                           │
  │  Access Token + Refresh  │                           │
```

**핵심:** X-User-Id 헤더 없음 → AuthHub는 "인증 안 된 요청"으로 판단 → OK (Public API니까)

### 2.2 Admin API 요청 (Authenticated)

```
Client                    Gateway                     AuthHub
  │                          │                           │
  │ GET /api/v1/tenants      │                           │
  │ Authorization: Bearer JWT│                           │
  │─────────────────────────→│                           │
  │                          │                           │
  │                          │ 🔐 JWT 검증               │
  │                          │ userId: 123 추출          │
  │                          │ tenantId: 456 추출        │
  │                          │ roles: [TENANT_ADMIN]     │
  │                          │                           │
  │                          │ X-User-Id: 123            │
  │                          │ X-Tenant-Id: 456          │
  │                          │ X-Roles: [TENANT_ADMIN]   │
  │                          │──────────────────────────→│
  │                          │                           │
  │                          │                           │ 🛡️ 인가 검증
  │                          │                           │ 1. 헤더에서 정보 추출
  │                          │                           │ 2. TENANT_ADMIN이 이 API 호출 가능?
  │                          │                           │ 3. tenantId=456 데이터만 조회
  │                          │                           │
  │                          │←──────────────────────────│
  │←─────────────────────────│                           │
  │  Tenant 목록 (456만)     │                           │
```

**핵심:** X-User-Id 헤더 있음 → AuthHub는 "인증된 요청"으로 판단 → 인가 검증 수행

---

## 3. AuthHub 내부 인가 흐름

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  AuthHub 내부 인가 흐름                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Request (from Gateway)                                                     │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────┐                                           │
│  │ GatewayAuthenticationFilter │  ← X-User-Id, X-Tenant-Id, X-Roles 파싱   │
│  │ (OncePerRequestFilter)      │  ← SecurityContext 설정                   │
│  └─────────────────────────────┘  ← Spring SecurityContextHolder 동기화    │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────┐                                           │
│  │ Spring Security             │  ← @PreAuthorize("hasRole('ADMIN')")      │
│  │ (Method Security)           │  ← 역할별 API 접근 제어                    │
│  └─────────────────────────────┘                                           │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────┐                                           │
│  │ Controller                  │  ← UseCase 호출                           │
│  └─────────────────────────────┘                                           │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────┐                                           │
│  │ Repository/Adapter          │  ← tenantId 필터 적용 (데이터 격리)        │
│  │ (Persistence Layer)         │                                           │
│  └─────────────────────────────┘                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 컴포넌트 설계

### 4.1 SecurityContext (새로 추가)

**패키지:** `adapter-in/rest-api/.../auth/component`

```java
/**
 * 요청별 보안 컨텍스트
 *
 * <p>Gateway에서 전달한 인증 정보를 담습니다.
 * 불변 객체로 설계되어 Thread-safe합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityContext {

    private final Long userId;
    private final Long tenantId;
    private final Set<String> roles;
    private final String traceId;

    // Private constructor + Builder 패턴

    // Getters (불변)
    public Long getUserId() { ... }
    public Long getTenantId() { ... }
    public Set<String> getRoles() { return Set.copyOf(roles); }
    public String getTraceId() { ... }

    // 역할 검증 헬퍼 메서드
    public boolean hasRole(String role) { ... }
    public boolean hasAnyRole(String... roles) { ... }
    public boolean isSuperAdmin() { ... }
    public boolean isTenantAdmin() { ... }
    public boolean isOrgAdmin() { ... }

    // 인증 여부 확인
    public boolean isAuthenticated() {
        return userId != null;
    }

    // 익명 컨텍스트 (Public 엔드포인트용)
    public static SecurityContext anonymous() { ... }

    // Builder
    public static Builder builder() { ... }
}
```

### 4.2 SecurityContextHolder (새로 추가)

**패키지:** `adapter-in/rest-api/.../auth/component`

```java
/**
 * SecurityContext를 ThreadLocal로 관리
 *
 * <p>요청 스레드 내에서 SecurityContext에 접근할 수 있게 합니다.
 * Filter에서 설정하고, 요청 완료 시 반드시 clear() 호출 필요.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    private SecurityContextHolder() {}

    public static SecurityContext getContext() {
        SecurityContext context = contextHolder.get();
        return context != null ? context : SecurityContext.anonymous();
    }

    public static void setContext(SecurityContext context) {
        Objects.requireNonNull(context, "SecurityContext must not be null");
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    // 편의 메서드
    public static Long getCurrentUserId() {
        return getContext().getUserId();
    }

    public static Long getCurrentTenantId() {
        return getContext().getTenantId();
    }

    public static boolean hasRole(String role) {
        return getContext().hasRole(role);
    }

    public static boolean isSuperAdmin() {
        return getContext().isSuperAdmin();
    }
}
```

### 4.3 GatewayAuthenticationFilter (새로 추가)

**패키지:** `adapter-in/rest-api/.../auth/filter`

```java
/**
 * Gateway 헤더 기반 인증 필터
 *
 * <p>Gateway에서 전달하는 X-* 헤더를 파싱하여 SecurityContext를 설정합니다.
 *
 * <p>처리 흐름:
 * <ol>
 *   <li>X-User-Id 헤더 확인 (있으면 인증된 요청)</li>
 *   <li>X-Tenant-Id, X-Roles 헤더 추출</li>
 *   <li>SecurityContext 설정</li>
 *   <li>Spring SecurityContextHolder에 동기화</li>
 * </ol>
 *
 * <p>X-User-Id 헤더가 없는 경우:
 * - Public API 요청으로 판단
 * - Anonymous SecurityContext 설정
 * - 인증이 필요한 API는 Spring Security에서 401 반환
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_ROLES = "X-Roles";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    private final ObjectMapper objectMapper;

    public GatewayAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            SecurityContext context = buildSecurityContext(request);
            SecurityContextHolder.setContext(context);

            if (context.isAuthenticated()) {
                synchronizeWithSpringSecurityContext(context);
            }

            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private SecurityContext buildSecurityContext(HttpServletRequest request) {
        String userIdHeader = request.getHeader(HEADER_USER_ID);

        // X-User-Id 없으면 Anonymous (Public API)
        if (!StringUtils.hasText(userIdHeader)) {
            return SecurityContext.anonymous();
        }

        Long userId = Long.parseLong(userIdHeader);
        Long tenantId = parseTenantId(request.getHeader(HEADER_TENANT_ID));
        Set<String> roles = parseRoles(request.getHeader(HEADER_ROLES));
        String traceId = request.getHeader(HEADER_TRACE_ID);

        return SecurityContext.builder()
                .userId(userId)
                .tenantId(tenantId)
                .roles(roles)
                .traceId(traceId)
                .build();
    }

    private Long parseTenantId(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        return Long.parseLong(header);
    }

    private Set<String> parseRoles(String rolesHeader) {
        if (!StringUtils.hasText(rolesHeader)) {
            return Set.of();
        }
        try {
            // JSON 배열 파싱: ["ROLE_ADMIN","ROLE_USER"]
            String[] roles = objectMapper.readValue(rolesHeader, String[].class);
            return Set.of(roles);
        } catch (JsonProcessingException e) {
            return Set.of();
        }
    }

    private void synchronizeWithSpringSecurityContext(SecurityContext context) {
        Collection<GrantedAuthority> authorities = context.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        context.getUserId(),
                        null,
                        authorities
                );

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }
}
```

### 4.4 Roles 상수 클래스 (새로 추가)

**패키지:** `adapter-in/rest-api/.../auth/component`

```java
/**
 * 역할 상수 정의
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Roles {

    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";
    public static final String USER = "ROLE_USER";

    private Roles() {}
}
```

### 4.5 TenantSecurityChecker (새로 추가)

**패키지:** `adapter-in/rest-api/.../auth/component`

```java
/**
 * 테넌트 권한 검증 Bean
 *
 * <p>@PreAuthorize에서 SpEL로 호출하여 테넌트 권한을 검증합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * @PreAuthorize("@tenantSecurityChecker.canAccess(#tenantId)")
 * public Tenant getTenant(Long tenantId) { ... }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component("tenantSecurityChecker")
public class TenantSecurityChecker {

    /**
     * 현재 사용자가 해당 테넌트에 접근 가능한지 검증
     *
     * @param tenantId 접근 대상 테넌트 ID
     * @return 접근 가능 여부
     */
    public boolean canAccess(Long tenantId) {
        SecurityContext context = SecurityContextHolder.getContext();

        // SUPER_ADMIN은 모든 테넌트 접근 가능
        if (context.isSuperAdmin()) {
            return true;
        }

        // 자신의 테넌트만 접근 가능
        return Objects.equals(context.getTenantId(), tenantId);
    }

    /**
     * 현재 사용자가 자신의 테넌트 리소스만 조회하도록 tenantId 반환
     * SUPER_ADMIN은 null 반환 (전체 조회 허용)
     *
     * @return 필터링할 tenantId (SUPER_ADMIN이면 null)
     */
    public Long getFilterTenantId() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.isSuperAdmin()) {
            return null;  // 전체 조회
        }

        return context.getTenantId();
    }
}
```

---

## 5. Controller에서 @PreAuthorize 사용

```java
@RestController
@RequestMapping(ApiPaths.Tenants.BASE)
public class TenantQueryController {

    private final TenantQueryUseCase tenantQueryUseCase;

    /**
     * 테넌트 목록 조회
     * - SUPER_ADMIN: 전체 조회
     * - TENANT_ADMIN: 자기 테넌트만
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseEntity<List<TenantResponse>> getTenants() {
        // SUPER_ADMIN은 전체, TENANT_ADMIN은 자기 테넌트만
        return ResponseEntity.ok(tenantQueryUseCase.findAccessibleTenants());
    }

    /**
     * 테넌트 단건 조회
     * - 자기 테넌트 또는 SUPER_ADMIN만 조회 가능
     */
    @GetMapping("/{tenantId}")
    @PreAuthorize("@tenantSecurityChecker.canAccess(#tenantId)")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(tenantQueryUseCase.findById(tenantId));
    }
}
```

---

## 6. Persistence Layer에서 테넌트 필터링

```java
/**
 * Tenant Query Adapter
 *
 * <p>테넌트 데이터 격리는 이 레이어에서 처리합니다.
 */
@Component
public class TenantQueryAdapter implements TenantQueryPort {

    private final TenantRepository tenantRepository;

    /**
     * 접근 가능한 테넌트 목록 조회
     */
    @Override
    public List<Tenant> findAccessibleTenants() {
        Long currentTenantId = SecurityContextHolder.getCurrentTenantId();

        // SUPER_ADMIN이면 전체 조회
        if (SecurityContextHolder.isSuperAdmin()) {
            return tenantRepository.findAll();
        }

        // 그 외는 자기 테넌트만
        return tenantRepository.findById(currentTenantId)
                .map(List::of)
                .orElse(List.of());
    }
}
```

---

## 7. 필터 체인 순서

```
┌─────────────────────────────────────────────────────────────────┐
│  Filter Chain 실행 순서                                          │
└─────────────────────────────────────────────────────────────────┘

Order: HIGHEST_PRECEDENCE
  │
  ├─ MdcLoggingFilter (Request ID 설정, 로깅)
  │
  ├─ GatewayAuthenticationFilter (X-* 헤더 → SecurityContext)
  │     └─ X-User-Id 있음 → 인증된 요청
  │     └─ X-User-Id 없음 → Anonymous (Public API)
  │
  └─ Spring Security Filters
        └─ Authorization 검증
        └─ @PreAuthorize 처리
        └─ 인증 필요 API + Anonymous → 401 Unauthorized
```

**⚠️ 기존 JwtAuthenticationFilter:**
- **삭제** 또는 **비활성화**
- Gateway가 JWT 검증을 담당하므로 AuthHub에서 재검증 불필요
- JWT 발급 로직(JwtTokenProvider)은 로그인 API용으로 유지

---

## 8. 역할 계층 구조

```java
public final class Roles {
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";
    public static final String USER = "ROLE_USER";
}
```

**권한 상속:**
```
SUPER_ADMIN: 모든 테넌트, 모든 조직 접근 가능
     ↓
TENANT_ADMIN: 자신의 테넌트 내 모든 조직 접근 가능
     ↓
ORG_ADMIN: 자신의 조직만 접근 가능
     ↓
USER: 자신의 데이터만 접근 가능
```

---

## 9. 파일 구조

```
adapter-in/rest-api/src/main/java/.../adapter/in/rest/
├── auth/
│   ├── component/
│   │   ├── SecurityContext.java           (NEW)
│   │   ├── SecurityContextHolder.java     (NEW)
│   │   ├── Roles.java                     (NEW)
│   │   ├── TenantSecurityChecker.java     (NEW)
│   │   └── JwtTokenProvider.java          (기존 유지 - 로그인용)
│   ├── filter/
│   │   ├── GatewayAuthenticationFilter.java  (NEW)
│   │   └── JwtAuthenticationFilter.java      (DELETE or DISABLE)
│   ├── config/
│   │   └── SecurityConfig.java               (MODIFY)
│   ├── handler/
│   │   └── AuthenticationErrorHandler.java   (기존 유지)
│   └── paths/
│       ├── ApiPaths.java                     (NEW)
│       └── SecurityPaths.java                (NEW)
└── ...
```

---

## 10. 테스트 케이스

### 10.1 SecurityContext 테스트

```java
@Tag("unit")
class SecurityContextTest {

    @Test
    void builder_shouldCreateImmutableContext() { ... }

    @Test
    void hasRole_shouldReturnTrueForMatchingRole() { ... }

    @Test
    void isAuthenticated_shouldReturnTrueWhenUserIdExists() { ... }

    @Test
    void anonymous_shouldHaveNullUserIdAndEmptyRoles() { ... }
}
```

### 10.2 GatewayAuthenticationFilter 테스트

```java
@Tag("unit")
@Tag("adapter-rest")
class GatewayAuthenticationFilterTest {

    @Test
    void doFilter_withGatewayHeaders_shouldSetSecurityContext() { ... }

    @Test
    void doFilter_withoutGatewayHeaders_shouldSetAnonymousContext() { ... }

    @Test
    void doFilter_shouldClearContextAfterRequest() { ... }

    @Test
    void doFilter_shouldSynchronizeWithSpringSecurityContext() { ... }
}
```

### 10.3 TenantSecurityChecker 테스트

```java
@Tag("unit")
class TenantSecurityCheckerTest {

    @Test
    void canAccess_superAdmin_shouldReturnTrueForAnyTenant() { ... }

    @Test
    void canAccess_tenantAdmin_shouldReturnTrueForOwnTenant() { ... }

    @Test
    void canAccess_tenantAdmin_shouldReturnFalseForOtherTenant() { ... }
}
```

---

## 11. 구현 순서

1. **SecurityContext + SecurityContextHolder** (핵심)
2. **Roles 상수 클래스**
3. **GatewayAuthenticationFilter**
4. **TenantSecurityChecker**
5. **JwtAuthenticationFilter 삭제/비활성화**
6. **SecurityConfig 수정** (필터 등록, @PreAuthorize 활성화)
7. **ApiPaths + SecurityPaths**
8. **단위 테스트**
9. **통합 테스트**

---

## 12. 승인 체크리스트

- [ ] SecurityContext 불변성 보장
- [ ] ThreadLocal 메모리 누수 방지 (finally clear)
- [ ] Spring SecurityContextHolder와 동기화
- [ ] X-User-Id 없으면 Anonymous 처리
- [ ] @PreAuthorize로 역할 검증
- [ ] Persistence Layer에서 테넌트 필터링
- [ ] 기존 JwtAuthenticationFilter 제거
- [ ] 테스트 커버리지 90% 이상

---

**작성자:** Claude (AI Assistant)
**검토자:** (사용자 검토 필요)
**작성일:** 2025-12-06
**버전:** 2.0.0 (재정립된 Gateway 연동 플로우 반영)
