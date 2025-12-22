# IAM 시스템 개선 작업 계획

> 작성일: 2025-12-22
> 상태: 계획 수립 완료

---

## 1. 개요

### 1.1 배경

현재 AuthHub 기반 IAM 시스템에서 다음 문제점이 발견됨:

1. **RoleScope 미활용**: GLOBAL/TENANT/ORGANIZATION 범위가 정의되어 있지만 실제 권한 체크에 사용되지 않음
2. **서버간 통신 인증**: Service Token 방식에서 사용자 컨텍스트가 전달되지 않음
3. **권한 동기화 문제**: 새로운 커스텀 권한 생성 시 각 서버 코드 수정 필요
4. **Gateway 권한 체크 부재**: Gateway에서 경로별 권한 규칙이 중앙 관리되지 않음

### 1.2 목표

- 멀티테넌트 환경에서 Scope 기반 접근 제어 구현
- 서버간 통신 시 사용자 컨텍스트 유지 및 감사 추적 가능
- Gateway 레벨에서 권한 규칙 중앙 관리
- 공통 라이브러리를 통한 권한/역할 상수 공유

### 1.3 영향 범위

| 시스템 | 영향도 | 설명 |
|--------|--------|------|
| AuthHub | 높음 | RoleScope 접근 제어, 관계 조회 API |
| Gateway | 높음 | 권한 체크 강화, 라우팅 규칙 |
| fileflow | 중간 | Service Token 확장, 공통 라이브러리 |
| crawlinghub | 중간 | Service Token 확장, 공통 라이브러리 |

---

## 2. Phase별 작업 계획

### Phase 1: AuthHub 핵심 기능 (1-2주)

| 작업 | 우선순위 | 예상 일정 |
|------|----------|----------|
| RoleScope 기반 접근 제어 | P0 | 3-4일 |
| Relationship Query API | P1 | 2-3일 |

### Phase 2: 공통 인프라 (1-2주)

| 작업 | 우선순위 | 예상 일정 |
|------|----------|----------|
| common-auth-library | P0 | 3-4일 |
| Service Token 확장 | P1 | 2-3일 |

### Phase 3: Gateway 강화 (1-2주)

| 작업 | 우선순위 | 예상 일정 |
|------|----------|----------|
| Gateway 권한 체크 구현 | P0 | 4-5일 |
| 경로별 권한 규칙 설정 | P1 | 2-3일 |

---

## 3. 상세 작업 스펙

### 3.1 RoleScope 기반 접근 제어

**목표**: 역할 범위에 따른 데이터 접근 제어 구현

**현재 문제**:
```java
// SecurityContext.java - 현재 코드
public boolean isSuperAdmin() {
    return hasRole(Role.SUPER_ADMIN);  // 단순 역할 이름만 체크
}
// Scope 체크 없음!
```

**개선 방향**:
```java
// ScopeBasedAccessControl.java
public interface ScopeBasedAccessControl {

    /**
     * 특정 테넌트 데이터에 접근 가능한지 확인
     */
    boolean canAccessTenant(TenantId targetTenantId);

    /**
     * 특정 조직 데이터에 접근 가능한지 확인
     */
    boolean canAccessOrganization(OrganizationId targetOrgId);

    /**
     * 전역 데이터에 접근 가능한지 확인
     */
    boolean canAccessGlobal();
}
```

**구현 로직**:
```
canAccessTenant(targetTenantId):
    if (scope == GLOBAL) return true
    if (scope == TENANT) return this.tenantId == targetTenantId
    if (scope == ORGANIZATION) return this.tenantId == targetTenantId
    return false

canAccessOrganization(targetOrgId):
    if (scope == GLOBAL) return true
    if (scope == TENANT) return this.tenantId == targetTenantId
    if (scope == ORGANIZATION) return this.organizationId == targetOrgId
    return false
```

**생성 파일**:
- `application/auth/service/ScopeBasedAccessControl.java`
- `adapter-in/rest-api/auth/component/ScopeValidator.java`
- 테스트 파일

---

### 3.2 Relationship Query API

**목표**: 역할별/조직별 사용자 목록 조회 API

**엔드포인트**:

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/v1/admin/roles/{roleId}/users` | 역할별 사용자 목록 |
| GET | `/api/v1/admin/organizations/{orgId}/users` | 조직별 사용자 목록 |

**Request/Response**:
```
GET /api/v1/admin/roles/{roleId}/users?page=0&size=20

Response:
{
  "success": true,
  "data": {
    "content": [
      {
        "userId": "uuid",
        "email": "user@example.com",
        "name": "홍길동",
        "assignedAt": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

**권한**: `ROLE_SUPER_ADMIN` 또는 `ROLE_TENANT_ADMIN` (본인 테넌트만)

---

### 3.3 common-auth-library

**목표**: 권한/역할 상수 및 유틸리티 공유 라이브러리

**패키지 구조**:
```
com.ryuqq.auth.common
├── constant
│   ├── Roles.java          # 역할 상수
│   ├── Permissions.java    # 권한 상수
│   └── Scopes.java         # 범위 상수
├── context
│   ├── SecurityContext.java    # 보안 컨텍스트
│   └── UserContext.java        # 사용자 컨텍스트
├── util
│   ├── ScopeValidator.java     # 범위 검증 유틸
│   └── PermissionMatcher.java  # 권한 매칭 유틸
└── header
    └── SecurityHeaders.java    # 보안 헤더 상수
```

**Roles.java**:
```java
public final class Roles {
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";
    public static final String USER = "ROLE_USER";

    private Roles() {}
}
```

**Permissions.java**:
```java
public final class Permissions {
    // User 도메인
    public static final String USER_READ = "user:read";
    public static final String USER_WRITE = "user:write";
    public static final String USER_DELETE = "user:delete";

    // Product 도메인
    public static final String PRODUCT_READ = "product:read";
    public static final String PRODUCT_WRITE = "product:write";

    // 와일드카드
    public static final String ALL = "*:*";

    private Permissions() {}
}
```

**SecurityHeaders.java**:
```java
public final class SecurityHeaders {
    // Gateway 헤더
    public static final String USER_ID = "X-User-Id";
    public static final String TENANT_ID = "X-Tenant-Id";
    public static final String ORGANIZATION_ID = "X-Organization-Id";
    public static final String ROLES = "X-User-Roles";
    public static final String PERMISSIONS = "X-User-Permissions";

    // Service Token 헤더
    public static final String SERVICE_TOKEN = "X-Service-Token";
    public static final String SERVICE_NAME = "X-Service-Name";

    // 서버간 통신 확장 헤더
    public static final String ORIGINAL_USER_ID = "X-Original-User-Id";
    public static final String ORIGINAL_TENANT_ID = "X-Original-Tenant-Id";
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String REQUEST_SOURCE = "X-Request-Source";

    private SecurityHeaders() {}
}
```

**배포**:
- Maven Central 또는 사내 Nexus에 배포
- 각 서비스에서 의존성 추가

---

### 3.4 Service Token 확장

**목표**: 서버간 통신 시 사용자 컨텍스트 및 추적 정보 전달

**현재 방식**:
```
X-Service-Token: {secret}
X-Service-Name: crawlinghub
```

**확장 방식**:
```
X-Service-Token: {secret}
X-Service-Name: crawlinghub
X-Original-User-Id: user-123        # 원본 사용자 ID
X-Original-Tenant-Id: tenant-A      # 원본 테넌트 ID
X-Correlation-Id: trace-789         # 분산 추적 ID
X-Request-Source: crawlinghub       # 호출 서비스
```

**수신 서버 처리**:
```java
// ServiceTokenFilter.java
if (isValidServiceToken(token)) {
    SecurityContext context = SecurityContext.builder()
        .userId(headers.get("X-Original-User-Id"))
        .tenantId(headers.get("X-Original-Tenant-Id"))
        .serviceAccount(true)
        .requestSource(headers.get("X-Request-Source"))
        .correlationId(headers.get("X-Correlation-Id"))
        .build();

    SecurityContextHolder.setContext(context);
}
```

**감사 로그**:
```json
{
  "action": "FILE_UPLOAD",
  "originalUserId": "user-123",
  "originalTenantId": "tenant-A",
  "requestSource": "crawlinghub",
  "correlationId": "trace-789",
  "timestamp": "2025-12-22T10:00:00Z"
}
```

---

## 4. Gateway 권한 체크 스펙

> 이 섹션은 별도 문서로 분리: [gateway-authorization-spec.md](./gateway-authorization-spec.md)

---

## 5. 의존성 및 순서

```
┌─────────────────────────────────────────────────────────────────┐
│ Phase 1: AuthHub                                                │
│ ┌─────────────────┐     ┌─────────────────────────────────────┐ │
│ │ RoleScope 접근   │────▶│ common-auth-library에 Scope 유틸   │ │
│ │ 제어 구현        │     │ 포함                               │ │
│ └─────────────────┘     └─────────────────────────────────────┘ │
│                                                                 │
│ ┌─────────────────┐                                             │
│ │ Relationship    │ (독립적, 병렬 진행 가능)                     │
│ │ Query API       │                                             │
│ └─────────────────┘                                             │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ Phase 2: 공통 인프라                                             │
│ ┌─────────────────┐     ┌─────────────────────────────────────┐ │
│ │ common-auth-    │────▶│ Service Token 확장                  │ │
│ │ library 배포    │     │ (헤더 상수 사용)                     │ │
│ └─────────────────┘     └─────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ Phase 3: Gateway                                                │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Gateway 권한 체크 구현                                       │ │
│ │ (common-auth-library, Service Token 확장 의존)              │ │
│ └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. 리스크 및 고려사항

### 6.1 하위 호환성

- 기존 Service Token 방식 유지하면서 확장 헤더 추가
- 확장 헤더가 없어도 기존 동작 보장

### 6.2 성능

- Gateway에서 매 요청 AuthHub 질의 → JWT 자체 검증으로 최소화
- 권한 규칙 캐싱 고려

### 6.3 보안

- Service Token 노출 시 영향 범위 최소화
- 정기적인 Token Rotation 정책 필요

---

## 7. 관련 문서

- [Gateway Authorization Spec](./gateway-authorization-spec.md)
- [Service Token Guide](../coding_convention/01-adapter-in-layer/rest-api/security/service-token-guide.md)

