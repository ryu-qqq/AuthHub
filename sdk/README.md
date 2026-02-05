# AuthHub SDK

AuthHub REST API와 통합하기 위한 공식 Java SDK입니다. 멀티 테넌트 IAM(Identity and Access Management) 시스템과의 연동을 제공합니다.

## 목차

- [개요](#개요)
- [요구사항](#요구사항)
- [설치](#설치)
- [빠른 시작](#빠른-시작)
- [설정](#설정)
- [클라이언트 구조](#클라이언트-구조)
- [AuthHubClient API](#authhubclient-api)
- [GatewayClient API](#gatewayclient-api)
- [인증 메커니즘](#인증-메커니즘)
- [엔드포인트 자동 동기화](#엔드포인트-자동-동기화)
- [Spring Boot Starter 기능](#spring-boot-starter-기능)
- [에러 처리](#에러-처리)
- [모듈 구조](#모듈-구조)

---

## 개요

AuthHub SDK는 두 개의 모듈로 구성됩니다:

| 모듈 | 설명 | 용도 |
|------|------|------|
| `authhub-sdk-core` | 순수 Java SDK 코어 | 모든 Java 프로젝트 |
| `authhub-sdk-spring-boot-starter` | Spring Boot 자동 설정 | Spring Boot 프로젝트 |

### 클라이언트 유형

| 클라이언트 | 인증 방식 | 용도 |
|------------|-----------|------|
| `AuthHubClient` | `Authorization: Bearer {token}` | 사용자 인증 API, 온보딩, 사용자 생성 |
| `GatewayClient` | `X-Service-Name` + `X-Service-Token` | Gateway 전용 Internal API |

---

## 요구사항

| 요구사항 | 버전 |
|----------|------|
| Java | 21+ |
| Spring Boot (선택) | 3.x |

---

## 설치

### Gradle (JitPack)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

#### Spring Boot 프로젝트 (권장)

```groovy
dependencies {
    implementation 'com.github.ryu-qqq.AuthHub:authhub-sdk-spring-boot-starter:{version}'
}
```

#### 순수 Java 프로젝트

```groovy
dependencies {
    implementation 'com.github.ryu-qqq.AuthHub:authhub-sdk-core:{version}'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ryu-qqq.AuthHub</groupId>
    <artifactId>authhub-sdk-spring-boot-starter</artifactId>
    <version>{version}</version>
</dependency>
```


---

## 빠른 시작

### Spring Boot

```yaml
# application.yml
authhub:
  base-url: https://auth.example.com
  service-token: ${AUTHHUB_SERVICE_TOKEN}
```

```java
@Service
public class AuthService {

    private final AuthApi authApi;

    public AuthService(AuthApi authApi) {
        this.authApi = authApi;
    }

    public LoginResponse login(String identifier, String password) {
        ApiResponse<LoginResponse> response = authApi.login(
            new LoginRequest(identifier, password)
        );
        return response.data();
    }
}
```

### 순수 Java

```java
AuthHubClient client = AuthHubClient.builder()
    .baseUrl("https://auth.example.com")
    .serviceToken("your-service-token")
    .build();

ApiResponse<LoginResponse> response = client.auth().login(
    new LoginRequest("user@example.com", "password")
);
```

---

## 설정

### Spring Boot 설정 옵션

```yaml
authhub:
  # [필수] AuthHub 서버 URL
  base-url: https://auth.example.com

  # [선택] 서비스 토큰 (M2M 통신용)
  # ThreadLocal 토큰이 없을 경우 fallback으로 사용
  service-token: ${AUTHHUB_SERVICE_TOKEN}

  # [선택] 서비스 코드 (엔드포인트 동기화 시 Role-Permission 자동 매핑용)
  service-code: SVC_STORE

  # [선택] 타임아웃 설정
  timeout:
    connect: 5s      # 연결 타임아웃 (기본: 5초)
    read: 30s        # 읽기 타임아웃 (기본: 30초)

  # [선택] 재시도 설정
  retry:
    enabled: true    # 재시도 활성화 (기본: true)
    max-attempts: 3  # 최대 재시도 횟수 (기본: 3)
    delay: 1s        # 재시도 대기 시간 (기본: 1초)
```

### 프로그래매틱 설정

```java
AuthHubClient client = AuthHubClient.builder()
    .baseUrl("https://auth.example.com")
    .serviceToken("your-service-token")
    .connectTimeout(Duration.ofSeconds(10))
    .readTimeout(Duration.ofSeconds(60))
    .build();
```

---

## 클라이언트 구조

SDK는 두 종류의 HTTP 클라이언트를 제공하며, 인증 방식과 용도가 다릅니다.

### AuthHubClient

사용자 인증이 필요한 API를 호출합니다. `Authorization: Bearer {token}` 헤더를 사용합니다.

```
AuthHubClient
├── auth()       → AuthApi        (로그인, 로그아웃, 토큰 갱신, 내 정보)
├── onboarding() → OnboardingApi  (테넌트 온보딩)
└── user()       → UserApi        (사용자 생성 + 역할 할당)
```

### GatewayClient

Gateway에서 Internal API를 호출합니다. `X-Service-Name` + `X-Service-Token` 헤더를 사용합니다.

```
GatewayClient
└── internal()   → InternalApi    (권한 스펙, JWKS, 테넌트 설정, 사용자 권한)
```

---

## AuthHubClient API

### AuthApi - 인증

```java
AuthApi auth = client.auth();

// 로그인
ApiResponse<LoginResponse> login = auth.login(
    new LoginRequest("user@example.com", "password")
);
// → LoginResponse(userId, accessToken, refreshToken, expiresIn, tokenType)

// 토큰 갱신
ApiResponse<TokenResponse> token = auth.refresh(
    new RefreshTokenRequest(refreshToken)
);
// → TokenResponse(accessToken, refreshToken, accessTokenExpiresIn, refreshTokenExpiresIn, tokenType)

// 로그아웃
auth.logout(new LogoutRequest(userId));

// 내 정보 조회 (테넌트, 조직, 역할, 권한 포함)
ApiResponse<MyContextResponse> me = auth.getMe();
// → MyContextResponse(userId, email, name, tenant, organization, roles, permissions)

// 사용자 정보 수정
ApiResponse<UserIdResponse> updated = auth.updateUser(userId,
    new UpdateUserRequest("010-1234-5678")
);

// 비밀번호 변경
auth.changePassword(userId,
    new ChangePasswordRequest("currentPassword", "newPassword")
);
```

### OnboardingApi - 테넌트 온보딩

테넌트 + 조직을 한 번에 생성합니다. 멱등키를 통해 중복 요청을 방지합니다.

```java
OnboardingApi onboarding = client.onboarding();

// 테넌트 온보딩 (X-Idempotency-Key 헤더 전송)
ApiResponse<TenantOnboardingResponse> result = onboarding.onboard(
    new TenantOnboardingRequest("My Company", "Main Organization"),
    UUID.randomUUID().toString()  // 멱등키
);
// → TenantOnboardingResponse(tenantId, organizationId)
// HTTP 201 Created 반환
```

### UserApi - 사용자 생성 + 역할 할당

Internal API로, 사용자 생성과 역할 할당을 한 번에 처리합니다.

```java
UserApi user = client.user();

// 사용자 생성 + 역할 할당
ApiResponse<CreateUserWithRolesResponse> result = user.createUserWithRoles(
    new CreateUserWithRolesRequest(
        "org-uuid",              // 소속 조직 ID (필수)
        "user@example.com",      // 로그인 식별자 (필수)
        "010-1234-5678",         // 전화번호 (선택)
        "password123",           // 비밀번호 (필수)
        "SVC_STORE",             // 서비스 코드 (선택, SERVICE scope Role 매핑용)
        List.of("ADMIN")         // 역할 이름 (선택)
    )
);
// → CreateUserWithRolesResponse(userId, assignedRoleCount)
// HTTP 201 Created 반환
```

**역할 매핑 로직:**
- `serviceCode` + `roleNames` 제공 → SERVICE scope Role 할당
- `roleNames`만 제공 → GLOBAL scope Role 할당
- `roleNames` 없음 → 사용자만 생성 (역할 할당 스킵)

---

## GatewayClient API

### InternalApi - Gateway 전용

Gateway에서 인증/인가 처리에 필요한 정보를 조회합니다.

```java
GatewayClient gateway = GatewayClient.builder()
    .baseUrl("https://authhub.example.com")
    .serviceName("gateway")
    .serviceToken("your-service-token")
    .build();

InternalApi internal = gateway.internal();

// 1. 엔드포인트-권한 스펙 조회 (Gateway 시작 시 캐싱 권장)
ApiResponse<EndpointPermissionSpecList> spec = internal.getPermissionSpec();
// → EndpointPermissionSpecList(version, updatedAt, endpoints[])
//   각 endpoint: EndpointPermissionSpec(serviceName, pathPattern, httpMethod,
//                requiredPermissions, requiredRoles, isPublic, description)

// 2. JWKS 공개키 조회 (JWT 서명 검증용, RFC 7517)
PublicKeys keys = internal.getJwks();
// → PublicKeys(keys[])
//   각 key: PublicKey(kid, kty, alg, use, n, e)

// 3. 테넌트 설정 조회 (테넌트 유효성 검증용)
ApiResponse<TenantConfig> config = internal.getTenantConfig("tenant-123");
// → TenantConfig(tenantId, name, status, active)

// 4. 사용자 권한 조회 (인가 검증용)
ApiResponse<UserPermissions> perms = internal.getUserPermissions("user-456");
// → UserPermissions(userId, roles, permissions)
```

### 타임아웃 설정

```java
GatewayClient gateway = GatewayClient.builder()
    .baseUrl("https://authhub.example.com")
    .serviceName("gateway")
    .serviceToken("your-service-token")
    .connectTimeout(Duration.ofSeconds(5))    // 기본: 5초
    .readTimeout(Duration.ofSeconds(30))      // 기본: 30초
    .build();
```

---

## 인증 메커니즘

### AuthHubClient 인증 (Bearer Token)

AuthHubClient는 `Authorization: Bearer {token}` 헤더를 사용합니다. 토큰은 `TokenResolver` 체인을 통해 결정됩니다.

#### 토큰 우선순위

```
1. ThreadLocal 토큰 (사용자 요청에서 자동 추출)
2. 서비스 토큰 (authhub.service-token 설정값)
3. AuthHubUnauthorizedException 발생
```

#### TokenResolver 구현체

| 구현체 | 용도 |
|--------|------|
| `ThreadLocalTokenResolver` | HTTP 요청의 Bearer 토큰을 ThreadLocal에 저장/조회 |
| `StaticTokenResolver` | 고정 서비스 토큰 (M2M 통신) |
| `ChainTokenResolver` | 여러 Resolver를 순차적으로 시도 |

Spring Boot AutoConfiguration에서는 `ChainTokenResolver.withFallback(serviceToken)`이 자동 등록되어, ThreadLocal 토큰을 먼저 시도하고 없으면 서비스 토큰을 사용합니다.

#### 커스텀 TokenResolver

```java
TokenResolver customResolver = () -> {
    String token = MySecurityContext.getCurrentToken();
    return Optional.ofNullable(token);
};

AuthHubClient client = AuthHubClient.builder()
    .baseUrl("https://auth.example.com")
    .tokenResolver(customResolver)
    .build();
```

### GatewayClient 인증 (Service Token)

GatewayClient는 **Bearer Token이 아닌** 커스텀 헤더를 사용합니다:

| 헤더 | 설명 |
|------|------|
| `X-Service-Name` | 서비스 이름 (예: `gateway`) |
| `X-Service-Token` | 서비스 토큰 |

Gateway 아키텍처에서 다운스트림 서비스는 `Authorization: Bearer` 헤더를 받지 않고, `X-USER-ID` 같은 커스텀 헤더를 받습니다. GatewayClient는 이 구조에 맞게 설계되었습니다.

---

## 엔드포인트 자동 동기화

애플리케이션 시작 시 `@RequirePermission` 어노테이션이 붙은 엔드포인트를 AuthHub에 자동 동기화합니다.

### 동작 흐름

```
앱 시작 → ApplicationRunner.run()
  → EndpointScanner: @RequirePermission 어노테이션 스캔
  → EndpointSyncRequest 생성 (serviceName, serviceCode, endpoints)
  → EndpointSyncClient.sync(): POST /api/v1/internal/endpoints/sync
  → AuthHub 서버: 없는 것만 새로 생성 (멱등성 보장)
```

### @RequirePermission 어노테이션

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping("/{id}")
    @RequirePermission(value = "product:read", description = "상품 조회")
    public ProductResponse getProduct(@PathVariable Long id) {
        // ...
    }

    @PostMapping
    @RequirePermission(value = "product:create", description = "상품 생성")
    public ProductResponse createProduct(@RequestBody CreateProductRequest request) {
        // ...
    }
}
```

### 설정 방법

#### 1. EndpointSyncClient 구현

```java
@Component
public class HttpEndpointSyncClient implements EndpointSyncClient {

    private final RestTemplate restTemplate;
    private final String authHubUrl;
    private final String serviceToken;

    public HttpEndpointSyncClient(
            RestTemplate restTemplate,
            @Value("${authhub.base-url}") String authHubUrl,
            @Value("${authhub.service-token}") String serviceToken) {
        this.restTemplate = restTemplate;
        this.authHubUrl = authHubUrl;
        this.serviceToken = serviceToken;
    }

    @Override
    public void sync(EndpointSyncRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Service-Name", request.serviceName());
        headers.set("X-Service-Token", serviceToken);

        restTemplate.postForEntity(
            authHubUrl + "/api/v1/internal/endpoints/sync",
            new HttpEntity<>(request, headers),
            Void.class
        );
    }
}
```

#### 2. EndpointSyncRunner 빈 등록

```java
@Configuration
public class EndpointSyncConfig {

    @Bean
    public EndpointSyncRunner endpointSyncRunner(
            RequestMappingHandlerMapping handlerMapping,
            EndpointSyncClient syncClient,
            @Value("${spring.application.name}") String serviceName,
            @Value("${authhub.service-code:}") String serviceCode) {
        return new EndpointSyncRunner(handlerMapping, syncClient, serviceName, serviceCode);
    }
}
```

### 서버 측 동기화 로직

AuthHub 서버는 다음과 같은 **Create-Missing-Only** 패턴으로 동기화합니다:

1. **Permission 동기화**: `permissionKey`로 IN절 조회 → 없는 것만 생성
2. **PermissionEndpoint 동기화**: `(serviceName, urlPattern, httpMethod)` 조합으로 중복 판단 → 없는 것만 생성
3. **자동 Role-Permission 매핑** (serviceCode 제공 시):

| permissionKey의 action | 매핑 대상 Role |
|---|---|
| `read`, `list`, `search`, `get` | ADMIN, EDITOR, VIEWER |
| `create`, `update`, `write`, `edit` | ADMIN, EDITOR |
| `delete`, 기타 | ADMIN only |

기존 데이터는 건드리지 않으므로 **멱등성이 보장**됩니다. 서비스가 여러 번 재시작해도 중복 데이터가 생기지 않습니다.

### 동기화 요청 형식

```json
{
  "serviceName": "product-service",
  "serviceCode": "SVC_PRODUCT",
  "endpoints": [
    {
      "httpMethod": "GET",
      "pathPattern": "/api/v1/products/{id}",
      "permissionKey": "product:read",
      "description": "상품 조회"
    },
    {
      "httpMethod": "POST",
      "pathPattern": "/api/v1/products",
      "permissionKey": "product:create",
      "description": "상품 생성"
    }
  ]
}
```

### 주의사항

- 동기화 실패 시에도 애플리케이션 시작은 계속 진행됩니다 (fail-safe)
- 오토스케일링으로 여러 인스턴스가 동시에 시작해도 멱등성이 보장됩니다
- `enabled` 파라미터로 동기화를 비활성화할 수 있습니다

---

## Spring Boot Starter 기능

`authhub-sdk-spring-boot-starter`는 코어 SDK 외에 다음 기능을 추가로 제공합니다.

### 자동 등록되는 빈

| 빈 | 조건 | 설명 |
|---|---|---|
| `TokenResolver` | `authhub.base-url` 설정 시 | ChainTokenResolver (ThreadLocal + 서비스 토큰) |
| `AuthHubClient` | `authhub.base-url` 설정 시 | 메인 클라이언트 |
| `AuthApi` | AuthHubClient 존재 시 | 인증 API |
| `OnboardingApi` | AuthHubClient 존재 시 | 온보딩 API |
| `UserApi` | AuthHubClient 존재 시 | 사용자 API |
| `AuthHubTokenContextFilter` | Servlet 환경 | `Authorization` 헤더 → ThreadLocal 자동 전파 |

### UserContext

Gateway에서 전달한 헤더 정보를 기반으로 현재 요청의 사용자 컨텍스트에 접근합니다.

```java
UserContext context = UserContextHolder.getContext();

// 사용자 정보
String userId = context.getUserId();
String tenantId = context.getTenantId();
String organizationId = context.getOrganizationId();
String email = context.getEmail();

// 역할/권한 확인
Set<String> roles = context.getRoles();
Set<String> permissions = context.getPermissions();
boolean isAdmin = context.hasRole("ADMIN");
boolean canRead = context.hasPermission("product:read");

// 서비스 계정 여부
boolean isService = context.isServiceAccount();

// 인증 여부
boolean authenticated = context.isAuthenticated();
```

### 보안 헤더 상수

`SecurityHeaders` 클래스에서 제공하는 헤더 상수입니다:

```java
SecurityHeaders.SERVICE_NAME                // "X-Service-Name"
SecurityHeaders.SERVICE_TOKEN               // "X-Service-Token"
SecurityHeaders.ORIGINAL_USER_ID            // "X-Original-User-Id"
SecurityHeaders.ORIGINAL_TENANT_ID          // "X-Original-Tenant-Id"
SecurityHeaders.ORIGINAL_ORGANIZATION_ID    // "X-Original-Organization-Id"
SecurityHeaders.CORRELATION_ID              // "X-Correlation-Id"
```

### 권한 상수

`Permissions` 클래스에서 제공하는 권한 키 상수입니다:

```java
// User
Permissions.USER_READ          // "user:read"
Permissions.USER_WRITE         // "user:write"
Permissions.USER_DELETE        // "user:delete"

// Role
Permissions.ROLE_READ          // "role:read"
Permissions.ROLE_WRITE         // "role:write"

// Permission
Permissions.PERMISSION_READ    // "permission:read"
Permissions.PERMISSION_WRITE   // "permission:write"

// 유틸리티
Permissions.of("product", "create")  // → "product:create"
Permissions.extractDomain("user:read")  // → "user"
Permissions.extractAction("user:read")  // → "read"
```

---

## 에러 처리

### 예외 계층

```
AuthHubException (base)
├── AuthHubBadRequestException     (400)
├── AuthHubUnauthorizedException   (401)
├── AuthHubForbiddenException      (403)
├── AuthHubNotFoundException       (404)
└── AuthHubServerException         (5xx)
```

### 예외 처리 예시

```java
try {
    ApiResponse<LoginResponse> response = client.auth().login(request);
} catch (AuthHubBadRequestException e) {
    // 400: 잘못된 요청 (유효성 검증 실패 등)
    log.error("Bad request: {} - {}", e.getErrorCode(), e.getMessage());
} catch (AuthHubUnauthorizedException e) {
    // 401: 인증 실패 (토큰 만료, 잘못된 토큰 등)
    log.error("Unauthorized: {}", e.getMessage());
} catch (AuthHubNotFoundException e) {
    // 404: 리소스 없음
    log.warn("Not found: {}", e.getMessage());
} catch (AuthHubServerException e) {
    // 5xx: 서버 오류
    log.error("Server error ({}): {}", e.getStatusCode(), e.getMessage());
}
```

### 예외 정보

```java
catch (AuthHubException e) {
    int statusCode = e.getStatusCode();    // HTTP 상태 코드
    String errorCode = e.getErrorCode();   // 에러 코드 (예: "USER_NOT_FOUND")
    String message = e.getMessage();       // 에러 메시지
}
```

---

## 모듈 구조

```
sdk/
├── authhub-sdk-core/                        # 순수 Java SDK
│   └── src/main/java/com/ryuqq/authhub/sdk/
│       ├── api/                              # API 인터페이스
│       │   ├── AuthApi.java                  #   로그인, 로그아웃, 토큰 갱신, 내 정보
│       │   ├── OnboardingApi.java            #   테넌트 온보딩
│       │   ├── UserApi.java                  #   사용자 생성 + 역할 할당
│       │   └── InternalApi.java              #   Gateway용 Internal API
│       ├── auth/                             # 토큰 리졸버
│       │   ├── TokenResolver.java            #   토큰 제공 인터페이스
│       │   ├── ThreadLocalTokenResolver.java #   요청별 사용자 토큰
│       │   ├── StaticTokenResolver.java      #   고정 서비스 토큰
│       │   └── ChainTokenResolver.java       #   순차적 토큰 시도
│       ├── client/                           # 클라이언트 구현
│       │   ├── AuthHubClient.java            #   메인 클라이언트 인터페이스
│       │   ├── AuthHubClientBuilder.java     #   빌더
│       │   ├── GatewayClient.java            #   Gateway 전용 클라이언트
│       │   ├── GatewayClientBuilder.java     #   빌더
│       │   └── internal/                     #   구현체 (패키지 프라이빗)
│       │       ├── HttpClientSupport.java    #     Bearer Token HTTP 클라이언트
│       │       └── ServiceTokenHttpClientSupport.java  # Service Token HTTP 클라이언트
│       ├── config/                           # 설정
│       │   ├── AuthHubConfig.java            #   AuthHubClient 설정
│       │   └── GatewayClientConfig.java      #   GatewayClient 설정
│       ├── exception/                        # 예외 계층
│       │   ├── AuthHubException.java
│       │   ├── AuthHubBadRequestException.java
│       │   ├── AuthHubUnauthorizedException.java
│       │   ├── AuthHubForbiddenException.java
│       │   ├── AuthHubNotFoundException.java
│       │   └── AuthHubServerException.java
│       └── model/                            # DTO 모델
│           ├── common/                       #   ApiResponse 등
│           ├── auth/                         #   LoginRequest, TokenResponse 등
│           ├── user/                         #   CreateUserWithRolesRequest/Response
│           ├── onboarding/                   #   TenantOnboardingRequest/Response
│           └── internal/                     #   EndpointPermissionSpec, TenantConfig 등
│
└── authhub-sdk-spring-boot-starter/         # Spring Boot 통합
    └── src/main/java/com/ryuqq/authhub/sdk/
        ├── autoconfigure/                    # 자동 설정
        │   ├── AuthHubAutoConfiguration.java #   빈 자동 등록
        │   ├── AuthHubProperties.java        #   application.yml 바인딩
        │   └── AuthHubTokenContextFilter.java#   Bearer 토큰 → ThreadLocal
        ├── annotation/                       # 어노테이션
        │   └── RequirePermission.java        #   엔드포인트 권한 선언
        ├── context/                          # 사용자 컨텍스트
        │   ├── SecurityContext.java          #   권한 검사 계약
        │   ├── UserContext.java              #   사용자 정보 (Builder 패턴)
        │   └── UserContextHolder.java        #   ThreadLocal 기반 관리
        ├── constant/                         # 상수
        │   ├── Permissions.java              #   권한 키 상수 + 유틸리티
        │   ├── Roles.java                    #   역할 상수
        │   └── Scopes.java                   #   스코프 상수
        ├── filter/                           # 인증 필터
        │   ├── GatewayAuthenticationFilter.java
        │   └── ServiceTokenAuthenticationFilter.java
        ├── header/                           # 헤더 처리
        │   ├── GatewayHeaderParser.java
        │   └── SecurityHeaders.java          #   헤더 키 상수
        ├── sync/                             # 엔드포인트 동기화
        │   ├── EndpointInfo.java             #   스캔된 엔드포인트 정보
        │   ├── EndpointScanner.java          #   @RequirePermission 스캐너
        │   ├── EndpointSyncClient.java       #   동기화 클라이언트 인터페이스
        │   ├── EndpointSyncRequest.java      #   동기화 요청 DTO
        │   └── EndpointSyncRunner.java       #   ApplicationRunner 구현
        └── util/                             # 유틸리티
            ├── PermissionMatcher.java
            └── ScopeValidator.java
```

## 버전 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| **v2.0.3** | 2026-02-03 | UserPermissions에 hash/generatedAt 필드 추가 (권한 변경 감지 지원) |
| v2.0.2 | 2026-02-03 | JWKS/Tenant Config/User Permissions API 추가, SDK 테스트 개선, 문서화 보강 |
| v2.0.1 | 2026-02-03 | GatewayClient 추가 (Internal API 지원), Permission Spec API, 성능 최적화 |
| v2.0.0 | 2026-01-20 | Spring Boot Starter 추가, 권한 체크 기능, 엔드포인트 자동 동기화 |
| v1.0.0 | 2025-01-15 | 최초 릴리즈 (AuthHubClient) |

---

## 라이선스

MIT License
