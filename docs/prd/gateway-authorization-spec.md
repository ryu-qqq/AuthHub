# Gateway Authorization Specification

> 작성일: 2025-12-22
> 상태: 스펙 정의 완료
> 대상 시스템: API Gateway (Spring Cloud Gateway)

---

## 1. 개요

### 1.1 목적

API Gateway에서 중앙집중식 권한 체크를 구현하여:
- 각 Backend 서비스의 권한 체크 로직 최소화
- 권한 규칙의 중앙 관리 및 일관성 보장
- 새로운 권한 추가 시 Gateway 설정만 변경

### 1.2 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              API Gateway                                │
├─────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐  │
│  │ JWT 검증    │ → │ 권한 체크   │ → │ 헤더 추가   │ → │ 라우팅      │  │
│  │ Filter      │   │ Filter      │   │ Filter      │   │             │  │
│  └─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
        ↓                   ↓                   ↓
   토큰 파싱          경로별 권한 규칙      X-User-* 헤더
   Claims 추출         적용 및 검증          Backend 전달
```

---

## 2. 인증 흐름

### 2.1 JWT 검증 (AuthenticationFilter)

```
┌─────────────────────────────────────────────────────────────────────────┐
│ Request 수신                                                            │
└─────────────────────────────────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────────────────────────────────┐
│ Authorization 헤더 확인                                                 │
│   - Bearer Token 존재? → JWT 검증                                       │
│   - 없음? → Public 경로인지 확인                                         │
└─────────────────────────────────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────────────────────────────────┐
│ JWT 검증                                                                │
│   - 서명 검증 (RS256 / HS256)                                           │
│   - 만료 시간 확인                                                       │
│   - Claims 추출:                                                        │
│     • sub (userId)                                                      │
│     • tenant_id                                                         │
│     • organization_id                                                   │
│     • roles                                                             │
│     • permissions                                                       │
└─────────────────────────────────────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────────────────────────────────────┐
│ SecurityContext 생성                                                    │
│   GatewaySecurityContext:                                               │
│     - userId: UUID                                                      │
│     - tenantId: UUID                                                    │
│     - organizationId: UUID                                              │
│     - roles: Set<String>                                                │
│     - permissions: Set<String>                                          │
│     - authenticated: boolean                                            │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 JWT Claims 구조

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "tenant_id": "tenant-uuid",
  "organization_id": "org-uuid",
  "roles": ["ROLE_TENANT_ADMIN", "ROLE_USER"],
  "permissions": ["user:read", "user:write", "product:read"],
  "permission_hash": "sha256-hash",
  "mfa_verified": true,
  "iat": 1703232000,
  "exp": 1703235600
}
```

---

## 3. 권한 체크 (AuthorizationFilter)

### 3.1 권한 규칙 구조

```yaml
# gateway-authorization-rules.yml

authorization:
  rules:
    # Public 엔드포인트 (인증 불필요)
    - path: "/api/v1/auth/**"
      methods: ["POST"]
      access: "permitAll"

    - path: "/api/v1/health"
      methods: ["GET"]
      access: "permitAll"

    - path: "/actuator/**"
      methods: ["GET"]
      access: "permitAll"

    # 인증만 필요 (권한 체크 없음)
    - path: "/api/v1/users/me"
      methods: ["GET", "PUT"]
      access: "authenticated"

    # 역할 기반 접근 제어
    - path: "/api/v1/admin/**"
      methods: ["*"]
      access: "hasRole"
      roles: ["ROLE_SUPER_ADMIN"]

    - path: "/api/v1/tenants/**"
      methods: ["POST", "PUT", "DELETE"]
      access: "hasRole"
      roles: ["ROLE_SUPER_ADMIN"]

    - path: "/api/v1/tenants/{tenantId}/**"
      methods: ["GET"]
      access: "hasAnyRole"
      roles: ["ROLE_SUPER_ADMIN", "ROLE_TENANT_ADMIN"]
      scopeCheck: "tenant"  # 본인 테넌트만

    # 권한 기반 접근 제어
    - path: "/api/v1/products/**"
      methods: ["GET"]
      access: "hasPermission"
      permissions: ["product:read"]

    - path: "/api/v1/products/**"
      methods: ["POST", "PUT"]
      access: "hasPermission"
      permissions: ["product:write"]

    - path: "/api/v1/products/**"
      methods: ["DELETE"]
      access: "hasPermission"
      permissions: ["product:delete"]

    # 복합 조건
    - path: "/api/v1/reports/export"
      methods: ["POST"]
      access: "hasAllPermissions"
      permissions: ["report:read", "report:export"]

    # 기본 규칙 (위 규칙에 매칭되지 않은 경우)
    - path: "/**"
      methods: ["*"]
      access: "authenticated"
```

### 3.2 접근 제어 타입

| 타입 | 설명 | 예시 |
|------|------|------|
| `permitAll` | 인증 없이 접근 가능 | 로그인, 헬스체크 |
| `authenticated` | 인증만 필요 | 내 정보 조회 |
| `hasRole` | 특정 역할 필수 | 관리자 전용 |
| `hasAnyRole` | 역할 중 하나 이상 | 관리자 또는 매니저 |
| `hasPermission` | 특정 권한 필수 | product:write |
| `hasAnyPermission` | 권한 중 하나 이상 | product:read 또는 product:* |
| `hasAllPermissions` | 모든 권한 필수 | report:read AND report:export |

### 3.3 Scope 기반 추가 검증

```yaml
scopeCheck:
  tenant: "요청 대상 tenantId == 사용자 tenantId"
  organization: "요청 대상 orgId == 사용자 orgId"
  global: "GLOBAL scope 역할만 허용"
```

**Scope 검증 로직**:
```java
// ScopeValidator.java
public boolean validateScope(String scopeType,
                             GatewaySecurityContext context,
                             ServerHttpRequest request) {

    // SUPER_ADMIN은 모든 Scope 통과
    if (context.hasRole("ROLE_SUPER_ADMIN")) {
        return true;
    }

    switch (scopeType) {
        case "tenant":
            String targetTenantId = extractPathVariable(request, "tenantId");
            return context.getTenantId().equals(targetTenantId);

        case "organization":
            String targetOrgId = extractPathVariable(request, "orgId");
            return context.getOrganizationId().equals(targetOrgId);

        case "global":
            return context.hasRole("ROLE_SUPER_ADMIN");

        default:
            return true;
    }
}
```

---

## 4. 헤더 전달 (HeaderEnrichmentFilter)

### 4.1 Backend로 전달하는 헤더

| 헤더 | 설명 | 예시 |
|------|------|------|
| `X-User-Id` | 사용자 UUID | `550e8400-e29b-...` |
| `X-Tenant-Id` | 테넌트 UUID | `660e8400-e29b-...` |
| `X-Organization-Id` | 조직 UUID | `770e8400-e29b-...` |
| `X-User-Roles` | 역할 목록 (콤마 구분) | `ROLE_TENANT_ADMIN,ROLE_USER` |
| `X-User-Permissions` | 권한 목록 (콤마 구분) | `user:read,user:write` |
| `X-Trace-Id` | 분산 추적 ID | `trace-123-456` |
| `X-Request-Time` | 요청 시간 | `2025-12-22T10:00:00Z` |

### 4.2 헤더 추가 로직

```java
// HeaderEnrichmentFilter.java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    GatewaySecurityContext context = getSecurityContext(exchange);

    if (context.isAuthenticated()) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-User-Id", context.getUserId())
            .header("X-Tenant-Id", context.getTenantId())
            .header("X-Organization-Id", context.getOrganizationId())
            .header("X-User-Roles", String.join(",", context.getRoles()))
            .header("X-User-Permissions", String.join(",", context.getPermissions()))
            .header("X-Trace-Id", getOrCreateTraceId(exchange))
            .header("X-Request-Time", Instant.now().toString())
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    return chain.filter(exchange);
}
```

### 4.3 보안: 헤더 스푸핑 방지

```java
// SecurityHeaderFilter.java (가장 먼저 실행)
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 외부에서 들어온 X-User-* 헤더 제거 (스푸핑 방지)
    ServerHttpRequest sanitizedRequest = exchange.getRequest().mutate()
        .headers(headers -> {
            headers.remove("X-User-Id");
            headers.remove("X-Tenant-Id");
            headers.remove("X-Organization-Id");
            headers.remove("X-User-Roles");
            headers.remove("X-User-Permissions");
        })
        .build();

    return chain.filter(exchange.mutate().request(sanitizedRequest).build());
}
```

---

## 5. 에러 응답

### 5.1 HTTP 상태 코드

| 상태 코드 | 조건 | 응답 |
|----------|------|------|
| 401 Unauthorized | JWT 없음/만료/무효 | 인증 필요 |
| 403 Forbidden | 권한/역할 부족 | 접근 거부 |
| 403 Forbidden | Scope 검증 실패 | 접근 거부 |

### 5.2 에러 응답 형식 (RFC 7807)

```json
{
  "type": "https://api.example.com/errors/forbidden",
  "title": "Access Denied",
  "status": 403,
  "detail": "Required permission 'product:delete' not found",
  "instance": "/api/v1/products/123",
  "timestamp": "2025-12-22T10:00:00Z",
  "traceId": "trace-123-456"
}
```

### 5.3 에러 핸들링

```java
// GatewayExceptionHandler.java
@Bean
public ErrorWebExceptionHandler errorWebExceptionHandler() {
    return (exchange, ex) -> {
        ProblemDetail problemDetail;

        if (ex instanceof UnauthorizedException) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication required"
            );
        } else if (ex instanceof ForbiddenException) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
            );
        } else {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
            );
        }

        problemDetail.setProperty("traceId", getTraceId(exchange));
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return writeResponse(exchange, problemDetail);
    };
}
```

---

## 6. 권한 규칙 Hot Reload

### 6.1 설정 변경 감지

```yaml
# application.yml
gateway:
  authorization:
    rules-path: "classpath:gateway-authorization-rules.yml"
    reload-interval: 60s  # 60초마다 갱신 체크
    cache-ttl: 300s       # 5분 캐시
```

### 6.2 동적 갱신 방식

| 방식 | 설명 | 장단점 |
|------|------|--------|
| 파일 감시 | 설정 파일 변경 시 자동 리로드 | 단순, 배포 필요 |
| Config Server | Spring Cloud Config에서 로드 | 중앙 관리, 복잡 |
| AuthHub API | AuthHub에서 규칙 조회 | 실시간, 의존성 |

**권장**: Config Server 또는 파일 감시 (AuthHub 의존성 최소화)

---

## 7. 로깅 및 모니터링

### 7.1 접근 로그

```json
{
  "timestamp": "2025-12-22T10:00:00Z",
  "traceId": "trace-123-456",
  "method": "POST",
  "path": "/api/v1/products",
  "userId": "user-uuid",
  "tenantId": "tenant-uuid",
  "roles": ["ROLE_USER"],
  "permissions": ["product:write"],
  "requiredPermission": "product:write",
  "authorized": true,
  "responseTime": 15,
  "statusCode": 201
}
```

### 7.2 접근 거부 로그

```json
{
  "timestamp": "2025-12-22T10:00:00Z",
  "traceId": "trace-123-456",
  "method": "DELETE",
  "path": "/api/v1/products/123",
  "userId": "user-uuid",
  "tenantId": "tenant-uuid",
  "roles": ["ROLE_USER"],
  "permissions": ["product:read", "product:write"],
  "requiredPermission": "product:delete",
  "authorized": false,
  "reason": "Missing required permission: product:delete"
}
```

### 7.3 메트릭

| 메트릭 | 설명 |
|--------|------|
| `gateway.auth.requests.total` | 총 인증 요청 수 |
| `gateway.auth.success.total` | 인증 성공 수 |
| `gateway.auth.failure.total` | 인증 실패 수 |
| `gateway.authz.allowed.total` | 권한 체크 통과 수 |
| `gateway.authz.denied.total` | 권한 체크 거부 수 |
| `gateway.authz.latency` | 권한 체크 소요 시간 |

---

## 8. 서버간 통신 (Internal Traffic)

### 8.1 Service Token 인식

```java
// ServiceTokenFilter.java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String serviceToken = exchange.getRequest()
        .getHeaders()
        .getFirst("X-Service-Token");

    if (isValidServiceToken(serviceToken)) {
        // 서버간 통신 - 권한 체크 SKIP
        String serviceName = exchange.getRequest()
            .getHeaders()
            .getFirst("X-Service-Name");

        log.info("Internal service request from: {}", serviceName);

        // 원본 사용자 정보 헤더 유지
        return chain.filter(exchange);
    }

    // 일반 요청 - 다음 필터로
    return chain.filter(exchange);
}
```

### 8.2 신뢰할 수 있는 서비스 목록

```yaml
gateway:
  internal-services:
    allowed:
      - name: "crawlinghub"
        token-hash: "sha256:abc123..."
      - name: "fileflow"
        token-hash: "sha256:def456..."
      - name: "marketplace"
        token-hash: "sha256:ghi789..."
```

---

## 9. 필터 실행 순서

```java
@Configuration
public class GatewayFilterConfig {

    @Bean
    public GlobalFilter securityHeaderFilter() {
        return new SecurityHeaderFilter();  // Order: -100 (가장 먼저)
    }

    @Bean
    public GlobalFilter serviceTokenFilter() {
        return new ServiceTokenFilter();    // Order: -50
    }

    @Bean
    public GlobalFilter authenticationFilter() {
        return new AuthenticationFilter();  // Order: 0
    }

    @Bean
    public GlobalFilter authorizationFilter() {
        return new AuthorizationFilter();   // Order: 10
    }

    @Bean
    public GlobalFilter headerEnrichmentFilter() {
        return new HeaderEnrichmentFilter(); // Order: 20
    }

    @Bean
    public GlobalFilter loggingFilter() {
        return new LoggingFilter();          // Order: 100 (가장 나중)
    }
}
```

**실행 순서**:
1. SecurityHeaderFilter - 스푸핑 방지 (X-User-* 헤더 제거)
2. ServiceTokenFilter - 서버간 통신 감지
3. AuthenticationFilter - JWT 검증
4. AuthorizationFilter - 권한 체크
5. HeaderEnrichmentFilter - Backend 전달 헤더 추가
6. LoggingFilter - 접근 로그 기록

---

## 10. 구현 체크리스트

### 10.1 필터 구현

- [ ] SecurityHeaderFilter (헤더 스푸핑 방지)
- [ ] ServiceTokenFilter (서버간 통신)
- [ ] AuthenticationFilter (JWT 검증)
- [ ] AuthorizationFilter (권한 체크)
- [ ] HeaderEnrichmentFilter (헤더 추가)
- [ ] LoggingFilter (접근 로그)

### 10.2 설정 및 규칙

- [ ] gateway-authorization-rules.yml 작성
- [ ] 서비스별 라우팅 규칙 정의
- [ ] 권한 규칙 Hot Reload 구현

### 10.3 에러 처리

- [ ] GatewayExceptionHandler 구현
- [ ] RFC 7807 형식 에러 응답

### 10.4 모니터링

- [ ] 접근 로그 포맷 정의
- [ ] Prometheus 메트릭 추가
- [ ] Grafana 대시보드 구성

### 10.5 테스트

- [ ] 필터 단위 테스트
- [ ] 권한 규칙 통합 테스트
- [ ] 성능 테스트 (부하)

---

## 11. 부록: 경로별 권한 규칙 예시

### 11.1 AuthHub

```yaml
# AuthHub 라우팅 규칙
- id: authhub-auth
  uri: lb://authhub
  predicates:
    - Path=/api/v1/auth/**
  metadata:
    access: "permitAll"

- id: authhub-users-admin
  uri: lb://authhub
  predicates:
    - Path=/api/v1/admin/users/**
  metadata:
    access: "hasAnyRole"
    roles: ["ROLE_SUPER_ADMIN", "ROLE_TENANT_ADMIN"]
    scopeCheck: "tenant"

- id: authhub-roles-admin
  uri: lb://authhub
  predicates:
    - Path=/api/v1/admin/roles/**
  metadata:
    access: "hasRole"
    roles: ["ROLE_SUPER_ADMIN"]
```

### 11.2 FileFlow

```yaml
# FileFlow 라우팅 규칙
- id: fileflow-upload
  uri: lb://fileflow
  predicates:
    - Path=/api/v1/file/sessions/**
    - Method=POST
  metadata:
    access: "hasPermission"
    permissions: ["file:write"]

- id: fileflow-download
  uri: lb://fileflow
  predicates:
    - Path=/api/v1/file/assets/*/download
    - Method=GET
  metadata:
    access: "hasPermission"
    permissions: ["file:download"]

- id: fileflow-delete
  uri: lb://fileflow
  predicates:
    - Path=/api/v1/file/assets/**
    - Method=DELETE
  metadata:
    access: "hasPermission"
    permissions: ["file:delete"]
```

### 11.3 CrawlingHub

```yaml
# CrawlingHub 라우팅 규칙
- id: crawlinghub-tasks
  uri: lb://crawlinghub
  predicates:
    - Path=/api/v1/crawl/tasks/**
  metadata:
    access: "hasPermission"
    permissions: ["task:read"]

- id: crawlinghub-schedules-write
  uri: lb://crawlinghub
  predicates:
    - Path=/api/v1/crawl/schedules/**
    - Method=POST,PUT,DELETE
  metadata:
    access: "hasPermission"
    permissions: ["scheduler:update"]
```

