# AuthHub Data Model - RBAC Gateway-Centric Architecture

> Epic: AUT-79 | Version: 2.0 | Last Updated: 2025-01-27

## Overview

이 문서는 Gateway-Centric RBAC 아키텍처를 위한 데이터 모델 변경사항을 정의합니다.

---

## 1. Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           RBAC Data Model v2.0                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│    Tenant    │       │   Organization   │       │     User     │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id (PK)      │◄──┐   │ id (PK)          │   ┌──►│ id (PK)      │
│ name         │   │   │ tenantId (FK)    │───┘   │ email        │
│ status       │   └───│ name             │       │ password     │
│ createdAt    │       │ status           │       │ status       │
│ updatedAt    │       │ createdAt        │       │ createdAt    │
└──────────────┘       │ updatedAt        │       │ updatedAt    │
                       └──────────────────┘       └──────┬───────┘
                                │                        │
                                │                        │
                       ┌────────▼────────┐      ┌────────▼────────┐
                       │ OrganizationUser│      │    UserRole     │
                       ├─────────────────┤      ├─────────────────┤
                       │ id (PK)         │      │ id (PK)         │
                       │ organizationId  │──────│ userId (FK)     │
                       │ userId (FK)     │      │ roleId (FK)     │
                       │ createdAt       │      │ organizationId  │
                       └─────────────────┘      │ createdAt       │
                                                └────────┬────────┘
                                                         │
┌──────────────────┐                            ┌────────▼────────┐
│   Permission     │                            │      Role       │
├──────────────────┤                            ├─────────────────┤
│ id (PK)          │◄───────────────────────────│ id (PK)         │
│ code             │       RolePermission       │ code            │
│ name             │                            │ name            │
│ description      │                            │ tenantId (FK)   │
│ createdAt        │                            │ isSystem        │
│ updatedAt        │                            │ createdAt       │
└────────┬─────────┘                            │ updatedAt       │
         │                                      └─────────────────┘
         │ 1:N
         │
┌────────▼─────────────┐
│ PermissionEndpoint   │  ◄── NEW ENTITY
├──────────────────────┤
│ id (PK)              │
│ permissionId (FK)    │
│ httpMethod           │
│ pathPattern          │
│ serviceId            │
│ description          │
│ createdAt            │
│ updatedAt            │
└──────────────────────┘
```

---

## 2. 주요 변경사항

### 2.1 User Entity - tenantId 제거

**Before (v1.0)**
```java
@Entity
public class User {
    private Long id;
    private Long tenantId;      // ❌ 제거 대상
    private String email;
    private String password;
    // ...
}
```

**After (v2.0)**
```java
@Entity
public class User {
    private Long id;
    // tenantId 제거됨 - Organization을 통해 조회
    private String email;
    private String password;
    // ...
}
```

**변경 이유:**
- User는 여러 Organization에 소속될 수 있음 (Multi-Organization)
- Tenant 정보는 `User → OrganizationUser → Organization → Tenant` 경로로 조회
- 데이터 정규화 및 중복 제거

**조회 방식 변경:**
```sql
-- Before: 직접 조회
SELECT * FROM users WHERE tenant_id = ?

-- After: Organization 경유 조회
SELECT u.* FROM users u
JOIN organization_users ou ON u.id = ou.user_id
JOIN organizations o ON ou.organization_id = o.id
WHERE o.tenant_id = ?
```

---

### 2.2 PermissionEndpoint Entity - 신규 추가

**새로운 Entity 구조:**
```java
@Entity
@Table(name = "permission_endpoints")
public class PermissionEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(nullable = false, length = 10)
    private String httpMethod;  // GET, POST, PUT, DELETE, PATCH

    @Column(nullable = false, length = 255)
    private String pathPattern;  // /api/v1/products/{id}

    @Column(nullable = false, length = 50)
    private String serviceId;  // product-service, order-service

    @Column(length = 500)
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**관계:**
- Permission : PermissionEndpoint = 1 : N
- 하나의 Permission이 여러 API Endpoint를 포함할 수 있음

**예시 데이터:**
```
Permission: "product:write"
├── POST   /api/v1/products         (product-service)
├── PUT    /api/v1/products/{id}    (product-service)
├── DELETE /api/v1/products/{id}    (product-service)
└── PATCH  /api/v1/products/{id}/status (product-service)
```

---

## 3. 전체 Entity 관계 요약

| Entity | 관계 | 설명 |
|--------|------|------|
| Tenant → Organization | 1:N | 테넌트는 여러 조직을 가짐 |
| Organization → User | N:M | OrganizationUser를 통한 다대다 |
| User → Role | N:M | UserRole을 통한 다대다 (조직별) |
| Role → Permission | N:M | RolePermission을 통한 다대다 |
| Permission → PermissionEndpoint | 1:N | **신규** - 권한과 API 매핑 |

---

## 4. Gateway 권한 검증 Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Request: POST /api/v1/products               │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│ 1. Gateway: Endpoint → Permission 매핑 조회                      │
│    SELECT p.code FROM permissions p                             │
│    JOIN permission_endpoints pe ON p.id = pe.permission_id      │
│    WHERE pe.http_method = 'POST'                                │
│      AND pe.path_pattern = '/api/v1/products'                   │
│    → Result: "product:write"                                    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. Gateway: JWT에서 permissionHash 추출                          │
│    → User의 권한 세트 해시값                                      │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. Gateway: Local Cache에서 권한 조회                            │
│    Cache Key: "permissions:{userId}:{permissionHash}"           │
│    → ["product:read", "product:write", "order:read"]            │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. 권한 검증: "product:write" ∈ UserPermissions?                 │
│    → ✅ ALLOW  or  ❌ 403 Forbidden                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 마이그레이션 계획

### Phase 1: 스키마 변경
```sql
-- 1. PermissionEndpoint 테이블 생성
CREATE TABLE permission_endpoints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_id BIGINT NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    path_pattern VARCHAR(255) NOT NULL,
    service_id VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    UNIQUE KEY uk_endpoint (http_method, path_pattern, service_id)
);

-- 2. User 테이블에서 tenant_id 컬럼 제거 (데이터 마이그레이션 후)
-- ALTER TABLE users DROP COLUMN tenant_id;
```

### Phase 2: 데이터 마이그레이션
1. 기존 User.tenantId 데이터를 OrganizationUser 관계로 변환
2. 기존 Permission 데이터에 PermissionEndpoint 매핑 추가

### Phase 3: 코드 변경
- Domain Layer: Entity 수정
- Application Layer: UseCase/Query 수정
- Adapter Layer: Repository/API 수정

---

## 6. Related Documents

- [RBAC Gateway Architecture](./rbac-gateway.md)
- [Event Flow (SQS)](./event-flow.md)
- [Epic AUT-79](https://your-jira-instance/browse/AUT-79)

---

## Appendix: DDL Scripts

전체 DDL 스크립트는 `src/main/resources/db/migration/` 디렉토리 참조.
