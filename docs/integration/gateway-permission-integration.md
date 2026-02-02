# Gateway Permission Integration Guide

Gateway PermissionFilter와 AuthHub PermissionSpec API 연동 가이드입니다.

---

## 1. 개요

### 1.1 목적

Gateway에서 API 요청에 대한 권한 검사를 수행하기 위해 AuthHub의 PermissionSpec을 활용합니다.

### 1.2 책임 분리

| 시스템 | 책임 |
|--------|------|
| **AuthHub** | PermissionSpec 관리, API 제공, 변경 시 Webhook 발송 |
| **Gateway** | PermissionSpec 캐싱, 요청별 권한 검사, JWT permissions 비교 |
| **각 서비스** | `@RequirePermission` 어노테이션으로 권한 선언, 시작 시 AuthHub에 동기화 |

### 1.3 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ARCHITECTURE                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────┐     POST /sync      ┌──────────────┐                      │
│  │  Service A   │ ──────────────────▶ │              │                      │
│  │  Service B   │  @RequirePermission │   AuthHub    │                      │
│  │  Service C   │                     │              │                      │
│  └──────────────┘                     └──────┬───────┘                      │
│                                              │                               │
│                                              │ Webhook (권한 변경 시)         │
│                                              ▼                               │
│                                       ┌──────────────┐                      │
│                         GET /spec     │              │                      │
│                      ◀──────────────  │   Gateway    │                      │
│                                       │              │                      │
│                                       └──────────────┘                      │
│                                              │                               │
│                                              │ PermissionFilter              │
│                                              ▼                               │
│                                       ┌──────────────┐                      │
│                                       │    Redis     │                      │
│                                       │   (Cache)    │                      │
│                                       └──────────────┘                      │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. AuthHub API Specification

### 2.1 PermissionSpec 조회 API

Gateway 시작 시 또는 캐시 갱신 시 호출합니다.

```
GET /api/v1/internal/endpoint-permissions/spec
```

#### Request Headers

| Header | Required | Description |
|--------|----------|-------------|
| `X-Service-Name` | Yes | 호출 서비스 식별자 (예: `gateway`) |
| `X-Service-Token` | Yes | 서비스 인증 토큰 |

#### Response

```json
{
  "success": true,
  "data": {
    "version": "1738494000000",
    "updatedAt": "2026-02-02T10:00:00Z",
    "endpoints": [
      {
        "serviceName": "product-service",
        "pathPattern": "/api/v1/products",
        "httpMethod": "POST",
        "requiredPermissions": ["product:create"],
        "requiredRoles": [],
        "isPublic": false,
        "description": "상품 생성"
      },
      {
        "serviceName": "product-service",
        "pathPattern": "/api/v1/products/{productId}",
        "httpMethod": "GET",
        "requiredPermissions": ["product:read"],
        "requiredRoles": [],
        "isPublic": false,
        "description": "상품 조회"
      },
      {
        "serviceName": "product-service",
        "pathPattern": "/api/v1/products/public/{productId}",
        "httpMethod": "GET",
        "requiredPermissions": [],
        "requiredRoles": [],
        "isPublic": true,
        "description": "공개 상품 조회"
      }
    ]
  },
  "timestamp": "2026-02-02T10:00:00Z"
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `version` | String | PermissionSpec 버전 (updatedAt의 epoch millis, ETag/캐시 키로 사용) |
| `updatedAt` | Instant | 마지막 업데이트 시간 (ISO 8601 형식) |
| `endpoints` | List | 엔드포인트별 권한 매핑 목록 |
| `endpoints[].serviceName` | String | 서비스 식별자 (영문 소문자, 숫자, 하이픈만 허용) |
| `endpoints[].httpMethod` | String | HTTP 메서드 (GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS) |
| `endpoints[].pathPattern` | String | 경로 패턴 (Path Variable 포함, `/`로 시작) |
| `endpoints[].requiredPermissions` | List<String> | 필요 권한 키 목록 (OR 조건, 현재 1:1 매핑으로 단일 요소) |
| `endpoints[].requiredRoles` | List<String> | 필요 역할 목록 (OR 조건, 현재 빈 배열) |
| `endpoints[].isPublic` | Boolean | 공개 엔드포인트 여부 (true면 인증 불필요) |
| `endpoints[].description` | String | 엔드포인트 설명 |

#### Error Responses

| Status | Error Code | Description |
|--------|------------|-------------|
| 401 | `UNAUTHORIZED` | 서비스 토큰 없음 |
| 403 | `INVALID_SERVICE_TOKEN` | 잘못된 서비스 토큰 |
| 500 | `INTERNAL_ERROR` | 서버 오류 |

---

### 2.2 PermissionSpec 버전 조회 API (Optional)

캐시 유효성 검사용 경량 API입니다.

```
GET /api/v1/internal/endpoint-permissions/spec/version
```

#### Response

```json
{
  "success": true,
  "data": {
    "version": 15,
    "updatedAt": "2026-02-02T10:00:00Z"
  }
}
```

---

### 2.3 권한 변경 Webhook (AuthHub → Gateway)

AuthHub에서 권한이 변경되면 Gateway에 알림을 보냅니다.

```
POST {gateway-webhook-url}/api/v1/webhooks/permissions/invalidate
```

#### Request Body

```json
{
  "version": 16,
  "previousVersion": 15,
  "changedAt": "2026-02-02T10:05:00Z",
  "changeType": "ENDPOINT_SYNC",
  "changedServices": ["product-service", "order-service"],
  "summary": {
    "created": 3,
    "updated": 2,
    "deleted": 0
  }
}
```

#### Change Types

| Type | Description |
|------|-------------|
| `ENDPOINT_SYNC` | 서비스 엔드포인트 동기화 |
| `PERMISSION_CREATED` | 새 권한 생성 |
| `PERMISSION_UPDATED` | 권한 수정 |
| `PERMISSION_DELETED` | 권한 삭제 |
| `ROLE_PERMISSION_CHANGED` | 역할-권한 매핑 변경 |

#### Expected Gateway Response

```json
{
  "success": true,
  "message": "Cache invalidated",
  "newVersion": 16
}
```

---

## 3. Gateway 구현 가이드

### 3.1 PermissionFilter 의사 코드

```java
public class PermissionFilter implements GlobalFilter {

    private final PermissionSpecCache cache;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // 1. PermissionSpec에서 엔드포인트 권한 조회
        Optional<EndpointPermission> endpointPerm = cache.findPermission(path, method);

        // 2. 엔드포인트가 등록되지 않은 경우 (기본 정책에 따라 처리)
        if (endpointPerm.isEmpty()) {
            // Option A: 허용 (등록된 것만 체크)
            return chain.filter(exchange);
            // Option B: 거부 (화이트리스트 방식)
            // return unauthorized(exchange);
        }

        EndpointPermission ep = endpointPerm.get();

        // 3. Public 엔드포인트는 바로 통과
        if (ep.isPublic()) {
            return chain.filter(exchange);
        }

        // 4. JWT에서 사용자 권한 추출 (JwtAuthenticationFilter에서 설정)
        Set<String> userPermissions = exchange.getAttribute("permissions");
        Set<String> userRoles = exchange.getAttribute("roles");

        // 5. 권한 체크
        boolean hasPermission = hasAnyPermission(userPermissions, ep.requiredPermissions());
        boolean hasRole = hasAnyRole(userRoles, ep.requiredRoles());

        // 6. requiredPermissions와 requiredRoles 중 하나라도 만족하면 통과
        if (hasPermission || hasRole) {
            return chain.filter(exchange);
        }

        // 7. 권한 없음
        return forbidden(exchange, ep.requiredPermissions());
    }

    private boolean hasAnyPermission(Set<String> userPerms, List<String> required) {
        if (required.isEmpty()) return true;
        return required.stream().anyMatch(userPerms::contains);
    }
}
```

### 3.2 Path Pattern 매칭

Path Variable이 포함된 패턴 매칭 로직입니다.

```java
public class PathMatcher {

    /**
     * 요청 경로가 패턴과 매칭되는지 확인
     *
     * @param pattern "/api/v1/products/{productId}"
     * @param requestPath "/api/v1/products/123"
     * @return true if matches
     */
    public boolean matches(String pattern, String requestPath) {
        // Spring AntPathMatcher 또는 직접 구현
        String regex = pattern
            .replaceAll("\\{[^/]+\\}", "[^/]+")  // {variable} → [^/]+
            .replaceAll("\\*\\*", ".*");          // ** → .*

        return requestPath.matches("^" + regex + "$");
    }
}

// 매칭 예시
// Pattern: /api/v1/products/{productId}
// Request: /api/v1/products/123 → ✅ Match
// Request: /api/v1/products/abc-def → ✅ Match
// Request: /api/v1/products → ❌ No Match
// Request: /api/v1/products/123/reviews → ❌ No Match
```

### 3.3 캐싱 전략

```java
@Component
public class PermissionSpecCache {

    private final RedisTemplate<String, PermissionSpec> redis;
    private final AuthHubClient authHubClient;

    private static final String CACHE_KEY = "gateway:permission:spec";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    // 인메모리 캐시 (Redis 장애 대비)
    private volatile PermissionSpec localCache;
    private volatile long localCacheVersion = -1;

    /**
     * PermissionSpec 조회 (캐시 우선)
     */
    public PermissionSpec getSpec() {
        // 1. Redis 캐시 확인
        PermissionSpec cached = redis.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            updateLocalCache(cached);
            return cached;
        }

        // 2. AuthHub API 호출
        PermissionSpec fresh = authHubClient.getPermissionSpec();

        // 3. 캐시 저장
        redis.opsForValue().set(CACHE_KEY, fresh, CACHE_TTL);
        updateLocalCache(fresh);

        return fresh;
    }

    /**
     * 캐시 무효화 (Webhook 수신 시)
     */
    public void invalidate(long newVersion) {
        if (newVersion > localCacheVersion) {
            redis.delete(CACHE_KEY);
            // 즉시 새로 로드하거나, 다음 요청 시 로드
            refreshAsync();
        }
    }

    /**
     * Redis 장애 시 로컬 캐시 사용
     */
    public PermissionSpec getSpecWithFallback() {
        try {
            return getSpec();
        } catch (RedisException e) {
            log.warn("Redis unavailable, using local cache");
            return localCache;
        }
    }
}
```

### 3.4 Gateway Redis 캐시 키 구조

```
# PermissionSpec 전체
gateway:permission:spec
Value: JSON (PermissionSpec)
TTL: 1 hour

# 빠른 조회용 인덱스 (Optional)
gateway:permission:endpoint:{method}:{pathHash}
Value: EndpointPermission JSON
TTL: 1 hour

# 버전 정보
gateway:permission:spec:version
Value: 15
TTL: 1 hour
```

---

## 4. SDK 사용법 (Gateway에서)

Gateway에서 AuthHub SDK를 사용하여 PermissionSpec을 조회할 수 있습니다.

### 4.1 의존성 추가

```groovy
dependencies {
    implementation 'com.github.ryu-qqq.AuthHub:authhub-sdk-spring-boot-starter:v2.0.0'
}
```

### 4.2 설정

```yaml
authhub:
  base-url: http://authhub:8080
  service-token: ${AUTHHUB_SERVICE_TOKEN}
  timeout:
    connect: 3s
    read: 10s
```

### 4.3 PermissionSpec API 호출 (SDK 확장 예정)

> **Note**: 현재 SDK에는 PermissionSpec API가 없습니다. 아래는 추가 예정 스펙입니다.

```java
// SDK에 추가될 API (예정)
@Service
public class PermissionSpecService {

    private final AuthHubClient authHub;

    public PermissionSpec getSpec() {
        return authHub.internal().getPermissionSpec();
    }

    public PermissionSpecVersion getSpecVersion() {
        return authHub.internal().getPermissionSpecVersion();
    }
}
```

### 4.4 직접 HTTP 호출 (SDK 확장 전까지)

```java
@Component
public class AuthHubPermissionClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String serviceToken;

    public PermissionSpec getPermissionSpec() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Name", "gateway");
        headers.set("X-Service-Token", serviceToken);

        ResponseEntity<ApiResponse<PermissionSpec>> response = restTemplate.exchange(
            baseUrl + "/api/v1/internal/endpoint-permissions/spec",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );

        return response.getBody().data();
    }
}
```

---

## 5. 권한 검사 흐름

### 5.1 정상 흐름

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Client                                                                      │
│    │                                                                         │
│    │  POST /api/v1/products                                                  │
│    │  Authorization: Bearer {jwt}                                            │
│    ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Gateway                                                             │    │
│  │                                                                      │    │
│  │  1. JwtAuthenticationFilter                                          │    │
│  │     - JWT 검증                                                       │    │
│  │     - Claims 추출: permissions=["product:create", "product:read"]    │    │
│  │     - Exchange Attribute에 저장                                      │    │
│  │                                                                      │    │
│  │  2. PermissionFilter                                                 │    │
│  │     - 요청: POST /api/v1/products                                    │    │
│  │     - PermissionSpec 조회 → requiredPermissions=["product:create"]   │    │
│  │     - 비교: "product:create" ∈ userPermissions? ✅ YES               │    │
│  │     - 통과                                                           │    │
│  │                                                                      │    │
│  │  3. 헤더 주입                                                        │    │
│  │     - X-User-Id: user-uuid                                           │    │
│  │     - X-Tenant-Id: tenant-uuid                                       │    │
│  │     - X-User-Permissions: product:create,product:read                │    │
│  │                                                                      │    │
│  └──────────────────────────────┬──────────────────────────────────────┘    │
│                                 │                                            │
│                                 ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Product Service                                                     │    │
│  │                                                                      │    │
│  │  GatewayAuthenticationFilter → UserContext 생성                      │    │
│  │  Controller → 비즈니스 로직 처리                                     │    │
│  │                                                                      │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 권한 거부 흐름

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Request: DELETE /api/v1/products/123                                        │
│  JWT permissions: ["product:read"]                                           │
│                                                                              │
│  PermissionSpec: requiredPermissions=["product:delete"]                      │
│                                                                              │
│  비교: "product:delete" ∈ ["product:read"]? ❌ NO                            │
│                                                                              │
│  Response: 403 Forbidden                                                     │
│  {                                                                           │
│    "success": false,                                                         │
│    "error": {                                                                │
│      "code": "ACCESS_DENIED",                                                │
│      "message": "Required permission: product:delete"                        │
│    }                                                                         │
│  }                                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. 장애 대응

### 6.1 AuthHub 장애 시

```
Gateway 시작 시:
  - AuthHub 연결 실패 → 캐시된 PermissionSpec 사용 (있으면)
  - 캐시도 없으면 → 기본 정책 적용 (공개 경로만 허용)

런타임 시:
  - Redis 캐시 우선 사용
  - Redis + AuthHub 모두 실패 → 인메모리 캐시 사용
  - 모든 캐시 없음 → 503 Service Unavailable
```

### 6.2 Redis 장애 시

```
- 인메모리 로컬 캐시로 fallback
- AuthHub API 직접 호출 (Rate Limit 주의)
- 로그 경고 및 알림
```

### 6.3 권장 Circuit Breaker 설정

```yaml
resilience4j:
  circuitbreaker:
    instances:
      authHubClient:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 3
```

---

## 7. 구현 체크리스트

### 7.1 AuthHub 구현 (우리)

- [x] `GET /api/v1/internal/endpoint-permissions/spec` API 구현
- [ ] `GET /api/v1/internal/endpoint-permissions/spec/version` API 구현
- [x] PermissionEndpoint 엔티티/테이블 설계
  - serviceName (VARCHAR 100) - 서비스 식별자
  - urlPattern (VARCHAR 500) - URL 패턴
  - httpMethod (ENUM) - HTTP 메서드
  - description (VARCHAR 500) - 설명
  - isPublic (BOOLEAN) - 공개 엔드포인트 여부
  - permissionId (FK) - Permission 연결
- [x] Domain 모델 - ServiceName, UrlPattern VO 추가
- [ ] 서비스 엔드포인트 동기화 시 PermissionSpec 버전 증가
- [ ] Webhook 발송 로직 구현 (권한 변경 시)
- [ ] SDK에 InternalApi 추가 (Optional)

### 7.2 Gateway 구현 (Gateway 팀)

- [ ] PermissionFilter 구현 및 활성화
- [ ] PermissionSpecCache 구현 (Redis + 로컬)
- [ ] Webhook 수신 컨트롤러 구현
- [ ] Path Pattern 매칭 로직 구현
- [ ] Circuit Breaker 설정
- [ ] 장애 대응 fallback 로직

---

## 8. 타임라인

| Phase | 작업 | 담당 | 예상 기간 |
|-------|------|------|----------|
| 1 | PermissionEndpoint 엔티티 설계 | AuthHub | 1일 |
| 2 | PermissionSpec API 구현 | AuthHub | 2일 |
| 3 | Gateway PermissionFilter 구현 | Gateway | 3일 |
| 4 | 통합 테스트 | 공동 | 2일 |
| 5 | Webhook 구현 | 공동 | 1일 |

---

## 9. 문의

- **AuthHub 담당**: [AuthHub Team]
- **Gateway 담당**: [Gateway Team]
- **Slack Channel**: #auth-integration
