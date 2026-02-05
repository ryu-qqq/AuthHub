# E2E Test Scenarios: Internal API (전체)

## 기본 정보

| 항목 | 값 |
|------|-----|
| API Prefix | /api/v1/internal |
| 목적 | Gateway 및 외부 서비스와의 통합을 위한 내부 API |
| 보안 | 서비스 토큰 인증 (내부 네트워크 전용) |
| 엔드포인트 수 | 6개 (Query 3개, Command 3개) |

---

## 엔드포인트 목록

### Query 엔드포인트 (조회)

| 엔드포인트 | HTTP | 설명 | 우선순위 |
|-----------|------|------|---------|
| /endpoint-permissions/spec | GET | Gateway용 엔드포인트-권한 스펙 조회 | P0 |
| /tenants/{tenantId}/config | GET | Gateway용 테넌트 설정 조회 | P0 |
| /users/{userId}/permissions | GET | Gateway용 사용자 역할/권한 조회 | P0 |

### Command 엔드포인트 (생성/수정)

| 엔드포인트 | HTTP | 설명 | 우선순위 |
|-----------|------|------|---------|
| /onboarding | POST | 테넌트 + 조직 한방 생성 (멱등키) | P0 |
| /users/register | POST | 사용자 + 역할 한방 생성 | P0 |
| /endpoints/sync | POST | 엔드포인트 동기화 + 자동 Role-Permission 매핑 | P0 |

---

## 1. Query 시나리오: GET /endpoint-permissions/spec

### 1.1 기본 조회 (P0)

#### 시나리오 1-1: 정상 조회 - 데이터 존재 시
- **사전 데이터**: Permission 1건 + PermissionEndpoint 1건
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **헤더**: Authorization: Bearer {serviceToken}
- **예상 결과**:
  - HTTP 200 OK
  - `data.endpoints` 배열에 1건 이상 존재
  - `data.version` 존재 (타임스탬프)
  - `data.updatedAt` 존재 (ISO 8601)
- **DB 검증**: N/A (조회만)

#### 시나리오 1-2: 빈 결과 반환
- **사전 데이터**: 없음
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **예상 결과**:
  - HTTP 200 OK
  - `data.endpoints` 빈 배열 `[]`
  - `data.version` = "0"

#### 시나리오 1-3: 여러 엔드포인트 조회
- **사전 데이터**: Permission 2건 + PermissionEndpoint 2건 (각각 다른 serviceName)
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **예상 결과**:
  - HTTP 200 OK
  - `data.endpoints` 배열에 2건 존재
  - 각 엔드포인트의 `serviceName`, `pathPattern`, `httpMethod`, `requiredPermissions` 검증

### 1.2 필드 검증 (P1)

#### 시나리오 1-4: version과 updatedAt 필드 존재
- **사전 데이터**: Permission 1건 + PermissionEndpoint 1건
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **예상 결과**:
  - `data.version` not null (String)
  - `data.updatedAt` not null (Instant)

#### 시나리오 1-5: 공개 엔드포인트 isPublic=true
- **사전 데이터**: Permission 1건 + PermissionEndpoint 1건 (isPublic=true)
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **예상 결과**:
  - `data.endpoints[0].isPublic` = true

#### 시나리오 1-6: Path Variable 패턴 처리
- **사전 데이터**: PermissionEndpoint 1건 (urlPattern="/api/v1/users/{id}")
- **요청**: GET /api/v1/internal/endpoint-permissions/spec
- **예상 결과**:
  - `data.endpoints[0].pathPattern` = "/api/v1/users/{id}"

### 1.3 인증 검증 (P0)

#### 시나리오 1-7: 서비스 토큰 인증 성공
- **사전 데이터**: Permission 1건 + PermissionEndpoint 1건
- **요청**: GET /api/v1/internal/endpoint-permissions/spec (서비스 토큰)
- **예상 결과**: HTTP 200 OK

#### 시나리오 1-8: 일반 사용자 인증도 허용 (테스트 환경)
- **사전 데이터**: Permission 1건 + PermissionEndpoint 1건
- **요청**: GET /api/v1/internal/endpoint-permissions/spec (사용자 토큰)
- **예상 결과**: HTTP 200 OK (테스트 환경 permitAll)

---

## 2. Query 시나리오: GET /tenants/{tenantId}/config

### 2.1 정상 조회 (P0)

#### 시나리오 2-1: 존재하는 테넌트 조회
- **사전 데이터**: Tenant 1건 (ACTIVE)
- **요청**: GET /api/v1/internal/tenants/{tenantId}/config
- **예상 결과**:
  - HTTP 200 OK
  - `data.tenantId` = {tenantId}
  - `data.name` = "Acme Corp"
  - `data.status` = "ACTIVE"
  - `data.active` = true

#### 시나리오 2-2: 비활성 테넌트 조회
- **사전 데이터**: Tenant 1건 (INACTIVE)
- **요청**: GET /api/v1/internal/tenants/{tenantId}/config
- **예상 결과**:
  - HTTP 200 OK
  - `data.status` = "INACTIVE"
  - `data.active` = false

### 2.2 예외 처리 (P0)

#### 시나리오 2-3: 존재하지 않는 테넌트 조회 → 404
- **사전 데이터**: 없음
- **요청**: GET /api/v1/internal/tenants/non-existent-id/config
- **예상 결과**:
  - HTTP 404 NOT_FOUND
  - RFC 7807 ProblemDetail 형식
  - `status` = 404
  - `title` not null

---

## 3. Query 시나리오: GET /users/{userId}/permissions

### 3.1 정상 조회 (P0)

#### 시나리오 3-1: 사용자 역할/권한 조회
- **사전 데이터**:
  - User 1건
  - Role 2건 (ADMIN, VIEWER)
  - UserRole 2건 (User ↔ ADMIN, User ↔ VIEWER)
  - Permission 3건 (user:read, user:write, admin:manage)
  - RolePermission 3건 (ADMIN → 3개, VIEWER → 1개)
- **요청**: GET /api/v1/internal/users/{userId}/permissions
- **예상 결과**:
  - HTTP 200 OK
  - `data.userId` = {userId}
  - `data.roles` = ["ADMIN", "VIEWER"]
  - `data.permissions` 배열에 "user:read", "user:write", "admin:manage" 포함

#### 시나리오 3-2: 역할 없는 사용자 조회 → 빈 배열
- **사전 데이터**: User 1건 (UserRole 없음)
- **요청**: GET /api/v1/internal/users/{userId}/permissions
- **예상 결과**:
  - HTTP 200 OK
  - `data.roles` = []
  - `data.permissions` = []

### 3.2 N+1 방지 검증 (P1)

#### 시나리오 3-3: IN절 조회 확인 (로그 확인)
- **사전 데이터**: User 1건, Role 3건, Permission 5건
- **요청**: GET /api/v1/internal/users/{userId}/permissions
- **예상 결과**:
  - 쿼리 수 = 4개 (UserRole, Role, RolePermission, Permission)
  - IN절 사용 확인

---

## 4. Command 시나리오: POST /onboarding

### 4.1 생성 성공 (P0)

#### 시나리오 4-1: 정상 온보딩
- **요청**:
  ```json
  POST /api/v1/internal/onboarding
  X-Idempotency-Key: abc-123
  {
    "tenantName": "my-tenant",
    "organizationName": "default-org"
  }
  ```
- **예상 결과**:
  - HTTP 201 CREATED
  - `data.tenantId` not null (UUIDv7)
  - `data.organizationId` not null (UUIDv7)
- **DB 검증**:
  - `tenants` 테이블에 1건 INSERT
  - `organizations` 테이블에 1건 INSERT

#### 시나리오 4-2: 멱등키 재요청 → 캐시 히트
- **사전 데이터**: 이전 온보딩 완료 + Redis 캐시 존재
- **요청**:
  ```json
  POST /api/v1/internal/onboarding
  X-Idempotency-Key: abc-123 (동일)
  {
    "tenantName": "different-name",
    "organizationName": "different-org"
  }
  ```
- **예상 결과**:
  - HTTP 201 CREATED
  - `data.tenantId` = 이전 생성한 ID (동일)
  - `data.organizationId` = 이전 생성한 ID (동일)
- **DB 검증**: INSERT 없음 (캐시 히트)

### 4.2 Validation 실패 (P0)

#### 시나리오 4-3: tenantName 누락 → 400
- **요청**:
  ```json
  POST /api/v1/internal/onboarding
  X-Idempotency-Key: abc-456
  {
    "organizationName": "default-org"
  }
  ```
- **예상 결과**:
  - HTTP 400 BAD_REQUEST
  - Bean Validation 에러 메시지

#### 시나리오 4-4: X-Idempotency-Key 헤더 누락 → 400
- **요청**:
  ```json
  POST /api/v1/internal/onboarding
  {
    "tenantName": "my-tenant",
    "organizationName": "default-org"
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST

### 4.3 중복 검증 (P0)

#### 시나리오 4-5: 중복 테넌트 이름 → 400
- **사전 데이터**: Tenant 1건 (name="duplicate-tenant")
- **요청**:
  ```json
  POST /api/v1/internal/onboarding
  X-Idempotency-Key: abc-789
  {
    "tenantName": "duplicate-tenant",
    "organizationName": "new-org"
  }
  ```
- **예상 결과**:
  - HTTP 400 BAD_REQUEST
  - `DuplicateTenantNameException`

### 4.4 트랜잭션 검증 (P1)

#### 시나리오 4-6: 트랜잭션 롤백 검증
- **시뮬레이션**: Organization 저장 실패 시 Tenant도 롤백
- **예상 결과**: 둘 다 DB에 INSERT 안 됨

---

## 5. Command 시나리오: POST /users/register

### 5.1 생성 성공 (P0)

#### 시나리오 5-1: 사용자만 생성 (역할 없음)
- **사전 데이터**: Organization 1건
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "admin@setof.com",
    "phoneNumber": "010-1234-5678",
    "password": "SecurePassword123!"
  }
  ```
- **예상 결과**:
  - HTTP 201 CREATED
  - `data.userId` not null (UUIDv7)
  - `data.assignedRoleCount` = 0
- **DB 검증**:
  - `users` 테이블에 1건 INSERT
  - `user_roles` 테이블에 INSERT 없음

#### 시나리오 5-2: 사용자 + SERVICE scope Role 할당
- **사전 데이터**:
  - Organization 1건
  - Service 1건 (code="SVC_STORE")
  - Role 2건 (ADMIN, EDITOR - SERVICE scope)
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "store-admin@setof.com",
    "password": "SecurePassword123!",
    "serviceCode": "SVC_STORE",
    "roleNames": ["ADMIN", "EDITOR"]
  }
  ```
- **예상 결과**:
  - HTTP 201 CREATED
  - `data.userId` not null
  - `data.assignedRoleCount` = 2
- **DB 검증**:
  - `users` 테이블에 1건 INSERT
  - `user_roles` 테이블에 2건 INSERT

#### 시나리오 5-3: 사용자 + GLOBAL scope Role 할당
- **사전 데이터**:
  - Organization 1건
  - Role 1건 (ADMIN - GLOBAL scope, tenantId=null, serviceId=null)
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "global-admin@setof.com",
    "password": "SecurePassword123!",
    "roleNames": ["ADMIN"]
  }
  ```
- **예상 결과**:
  - HTTP 201 CREATED
  - `data.assignedRoleCount` = 1

### 5.2 Validation 실패 (P0)

#### 시나리오 5-4: organizationId 누락 → 400
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "identifier": "noorg@setof.com",
    "password": "SecurePassword123!"
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST

#### 시나리오 5-5: password 누락 → 400
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "nopw@setof.com"
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST

### 5.3 중복 검증 (P0)

#### 시나리오 5-6: 중복 identifier → 409
- **사전 데이터**: User 1건 (identifier="duplicate@setof.com")
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "duplicate@setof.com",
    "password": "SecurePassword123!"
  }
  ```
- **예상 결과**: HTTP 409 CONFLICT

#### 시나리오 5-7: 중복 phoneNumber → 409
- **사전 데이터**: User 1건 (phoneNumber="010-1234-5678")
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "newuser@setof.com",
    "phoneNumber": "010-1234-5678",
    "password": "SecurePassword123!"
  }
  ```
- **예상 결과**: HTTP 409 CONFLICT

### 5.4 존재하지 않는 리소스 (P0)

#### 시나리오 5-8: 존재하지 않는 organizationId → 404
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "non-existent-org-id",
    "identifier": "orphan@setof.com",
    "password": "SecurePassword123!"
  }
  ```
- **예상 결과**: HTTP 404 NOT_FOUND

#### 시나리오 5-9: 존재하지 않는 serviceCode → 404
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "noservice@setof.com",
    "password": "SecurePassword123!",
    "serviceCode": "NON_EXISTENT_SERVICE",
    "roleNames": ["ADMIN"]
  }
  ```
- **예상 결과**: HTTP 404 NOT_FOUND

#### 시나리오 5-10: 존재하지 않는 roleName → 404
- **사전 데이터**: Service 1건 (code="SVC_STORE")
- **요청**:
  ```json
  POST /api/v1/internal/users/register
  {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "identifier": "norole@setof.com",
    "password": "SecurePassword123!",
    "serviceCode": "SVC_STORE",
    "roleNames": ["NON_EXISTENT_ROLE"]
  }
  ```
- **예상 결과**: HTTP 404 NOT_FOUND

---

## 6. Command 시나리오: POST /endpoints/sync

### 6.1 동기화 성공 (P0)

#### 시나리오 6-1: 신규 Permission + Endpoint 생성
- **사전 데이터**: 없음
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "serviceCode": "SVC_MARKETPLACE",
    "endpoints": [
      {
        "httpMethod": "POST",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:create",
        "description": "상품 생성"
      }
    ]
  }
  ```
- **예상 결과**:
  - HTTP 200 OK
  - `data.serviceName` = "marketplace"
  - `data.totalEndpoints` = 1
  - `data.createdPermissions` = 1
  - `data.createdEndpoints` = 1
  - `data.skippedEndpoints` = 0
  - `data.mappedRolePermissions` >= 1 (자동 매핑)
- **DB 검증**:
  - `permissions` 테이블에 1건 INSERT
  - `permission_endpoints` 테이블에 1건 INSERT
  - `role_permissions` 테이블에 N건 INSERT (자동 매핑)

#### 시나리오 6-2: 기존 Permission 재사용 + 신규 Endpoint 생성
- **사전 데이터**: Permission 1건 (key="product:read")
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "endpoints": [
      {
        "httpMethod": "GET",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:read"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.createdPermissions` = 0 (재사용)
  - `data.createdEndpoints` = 1

#### 시나리오 6-3: 중복 Endpoint 스킵
- **사전 데이터**:
  - Permission 1건 (key="product:create")
  - PermissionEndpoint 1건 (serviceName="marketplace", urlPattern="/api/v1/products", httpMethod="POST")
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "endpoints": [
      {
        "httpMethod": "POST",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:create"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.createdPermissions` = 0
  - `data.createdEndpoints` = 0
  - `data.skippedEndpoints` = 1

### 6.2 자동 Role-Permission 매핑 검증 (P0)

#### 시나리오 6-4: read action → ADMIN, EDITOR, VIEWER 모두 매핑
- **사전 데이터**:
  - Service 1건 (code="SVC_MARKETPLACE")
  - Role 3건 (ADMIN, EDITOR, VIEWER - SERVICE scope)
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "serviceCode": "SVC_MARKETPLACE",
    "endpoints": [
      {
        "httpMethod": "GET",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:read"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.mappedRolePermissions` = 3
- **DB 검증**:
  - `role_permissions` 테이블에 3건 INSERT (ADMIN, EDITOR, VIEWER → product:read)

#### 시나리오 6-5: create action → ADMIN, EDITOR만 매핑
- **사전 데이터**: Service + Role 3건
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "serviceCode": "SVC_MARKETPLACE",
    "endpoints": [
      {
        "httpMethod": "POST",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:create"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.mappedRolePermissions` = 2
- **DB 검증**: ADMIN, EDITOR만 매핑 (VIEWER 제외)

#### 시나리오 6-6: delete action → ADMIN만 매핑
- **사전 데이터**: Service + Role 3건
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "serviceCode": "SVC_MARKETPLACE",
    "endpoints": [
      {
        "httpMethod": "DELETE",
        "pathPattern": "/api/v1/products/{id}",
        "permissionKey": "product:delete"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.mappedRolePermissions` = 1
- **DB 검증**: ADMIN만 매핑

#### 시나리오 6-7: serviceCode 없으면 자동 매핑 스킵
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "endpoints": [
      {
        "httpMethod": "GET",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:read"
      }
    ]
  }
  ```
- **예상 결과**:
  - `data.mappedRolePermissions` = 0

### 6.3 Validation 실패 (P0)

#### 시나리오 6-8: serviceName 누락 → 400
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "endpoints": [
      {
        "httpMethod": "GET",
        "pathPattern": "/api/v1/products",
        "permissionKey": "product:read"
      }
    ]
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST

#### 시나리오 6-9: endpoints 빈 배열 → 400
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "endpoints": []
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST

#### 시나리오 6-10: permissionKey 형식 오류 → 400
- **요청**:
  ```json
  POST /api/v1/internal/endpoints/sync
  {
    "serviceName": "marketplace",
    "endpoints": [
      {
        "httpMethod": "GET",
        "pathPattern": "/api/v1/products",
        "permissionKey": "invalid-key-without-colon"
      }
    ]
  }
  ```
- **예상 결과**: HTTP 400 BAD_REQUEST (permissionKey 형식: `resource:action`)

### 6.4 트랜잭션 검증 (P1)

#### 시나리오 6-11: 트랜잭션 롤백 검증
- **시뮬레이션**: PermissionEndpoint 저장 실패 시 Permission도 롤백
- **예상 결과**: 둘 다 DB에 INSERT 안 됨

---

## 전체 플로우 시나리오 (P0)

### 플로우 1: 온보딩 → 사용자 등록 → 권한 조회

1. **온보딩 (POST /onboarding)**:
   - Tenant + Organization 생성
   - 201 CREATED 응답
   - tenantId, organizationId 캡처

2. **사용자 등록 (POST /users/register)**:
   - organizationId 사용
   - 사용자 + 역할 할당
   - 201 CREATED 응답
   - userId 캡처

3. **권한 조회 (GET /users/{userId}/permissions)**:
   - userId로 역할/권한 조회
   - 200 OK 응답
   - roles, permissions 검증

### 플로우 2: 엔드포인트 동기화 → 권한 스펙 조회

1. **엔드포인트 동기화 (POST /endpoints/sync)**:
   - Permission + Endpoint 생성
   - 자동 Role-Permission 매핑
   - 200 OK 응답

2. **권한 스펙 조회 (GET /endpoint-permissions/spec)**:
   - 동기화된 엔드포인트 스펙 조회
   - 200 OK 응답
   - endpoints 배열에 새로운 엔드포인트 포함 확인

---

## Fixture 설계

### 필요 Repository

| Repository | 용도 |
|-----------|------|
| TenantJpaRepository | Tenant 생성/삭제 |
| OrganizationJpaRepository | Organization 생성/삭제 |
| UserJpaRepository | User 생성/삭제 |
| UserRoleJpaRepository | UserRole 생성/삭제 |
| RoleJpaRepository | Role 생성/삭제 |
| RolePermissionJpaRepository | RolePermission 생성/삭제 |
| PermissionJpaRepository | Permission 생성/삭제 |
| PermissionEndpointJpaRepository | PermissionEndpoint 생성/삭제 |
| ServiceJpaRepository | Service 생성/삭제 |

### TestFixtures 존재 여부

| Fixture | 위치 | 존재 |
|---------|------|------|
| TenantJpaEntityFixture | adapter-out/persistence-mysql/src/testFixtures/.../tenant/ | ✅ |
| OrganizationJpaEntityFixture | adapter-out/persistence-mysql/src/testFixtures/.../organization/ | ✅ |
| ServiceJpaEntityFixture | adapter-out/persistence-mysql/src/testFixtures/.../service/ | ? |
| RoleJpaEntityFixture | adapter-out/persistence-mysql/src/testFixtures/.../role/ | ? |
| PermissionJpaEntityFixture | adapter-out/persistence-mysql/src/testFixtures/.../permission/ | ? |

### setUp() 패턴

```java
@BeforeEach
void setUp() {
    // 1. 자식 엔티티부터 삭제 (FK 순서)
    permissionEndpointJpaRepository.deleteAll();
    rolePermissionJpaRepository.deleteAll();
    userRoleJpaRepository.deleteAll();
    permissionJpaRepository.deleteAll();
    roleJpaRepository.deleteAll();
    userJpaRepository.deleteAll();
    organizationJpaRepository.deleteAll();
    tenantJpaRepository.deleteAll();
    serviceJpaRepository.deleteAll();

    // 2. Redis 캐시 초기화 (멱등키)
    // redisTemplate.delete("onboarding:idempotency:*");

    // 3. 부모 엔티티 생성 (공통 사전 데이터)
    savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
    savedOrganization = organizationJpaRepository.save(
        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId())
    );
    savedService = serviceJpaRepository.save(
        ServiceJpaEntityFixture.create("SVC_MARKETPLACE", "marketplace")
    );
}
```

---

## 총 시나리오 수

| 분류 | 시나리오 수 | 우선순위 |
|------|-----------|---------|
| Query - /endpoint-permissions/spec | 8개 | P0: 6, P1: 2 |
| Query - /tenants/{tenantId}/config | 3개 | P0: 3 |
| Query - /users/{userId}/permissions | 3개 | P0: 2, P1: 1 |
| Command - /onboarding | 6개 | P0: 5, P1: 1 |
| Command - /users/register | 10개 | P0: 10 |
| Command - /endpoints/sync | 11개 | P0: 10, P1: 1 |
| 전체 플로우 | 2개 | P0: 2 |
| **합계** | **43개** | **P0: 38, P1: 5** |

---

## 다음 단계

시나리오 설계 완료 후:
```bash
/test-e2e internal --all
```

각 엔드포인트별 개별 실행:
```bash
/test-e2e internal:onboarding
/test-e2e internal:user-register
/test-e2e internal:endpoint-sync
/test-e2e internal:permission-spec
/test-e2e internal:tenant-config
/test-e2e internal:user-permissions
```

---

## 주요 참고사항

### 1. 멱등키 패턴
- **헤더**: `X-Idempotency-Key: {uuid}`
- **캐시**: Redis (TTL: 24시간)
- **적용 엔드포인트**: POST /onboarding

### 2. 자동 Role-Permission 매핑 규칙
- `read`, `list`, `search`, `get` → ADMIN, EDITOR, VIEWER
- `create`, `update`, `write`, `edit` → ADMIN, EDITOR
- `delete`, 기타 → ADMIN only
- **조건**: serviceCode 제공 시에만 실행

### 3. 트랜잭션 경계
- Facade 레벨에서 `@Transactional`
- 실패 시 전체 롤백 (원자성 보장)

### 4. Long FK 전략
- JPA 관계 어노테이션 금지
- 원시 타입 FK 필드 (String userId, Long roleId)
- N+1 문제 사전 방지 (IN절 사용)

### 5. 기존 E2E 테스트 패턴 참조
- InternalPermissionSpecE2ETest.java
- UserE2ETest.java
- TestTags 사용: `@Tag(TestTags.E2E)`, `@Tag(TestTags.INTERNAL)`
- E2ETestBase 상속
- givenServiceToken() / givenAuthenticated() 사용
