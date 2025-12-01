# PRD: B2B AuthHub - 멀티 테넌트 인증/인가 시스템

**작성일**: 2025-01-13
**작성자**: System Architect
**상태**: Draft

---

## 📋 프로젝트 개요

### 비즈니스 목적
B2B 사업 시작 및 확장에 따라 기존 단일 회사의 어드민 인증/인가 시스템을 **멀티 테넌트 아키텍처**로 확장하고, 앞으로 생성될 모든 B2B 서비스의 중앙 인증/인가 허브로 기능합니다.

**핵심 구조**:
```
Tenant (1) → Organization (N) → User (M) → Roles/Permissions
```

### Scope / Out of Scope

#### ✅ In Scope (v1)
- **B2B 멀티 테넌트 인증/인가**: INTERNAL 사용자만
- **게이트웨이 연동**: JWT 검증, 권한 확인 API
- **RBAC**: Role-Based Access Control (역할 기반 권한 관리)
- **테넌트 격리**: Row-Level Security (같은 DB, TenantId 필터)
- **JWT**: Access Token (15분) + Refresh Token (7일)
- **Redis 캐싱**: User 상태, 권한, Tenant 설정

#### ❌ Out of Scope (v1)
- **B2C 고객 (PUBLIC 사용자)**: 세토프 일반 회원 제외 (커머스 쪽에서 처리)
- **소셜 로그인**: Google, Naver, Kakao 등
- **계정 연동**: 기존 계정과 소셜 계정 연동
- **MFA 실제 구현**: 테넌트 설정에 `mfaRequired` 필드만 제공 (구현은 나중에)
- **비밀번호 만료**: 비밀번호 주기적 변경 정책
- **계정 잠금**: 로그인 실패 횟수 제한
- **조직 계층 구조**: Flat 구조만 지원 (부모-자식 조직 없음)
- **사용자 조직/테넌트 이동**: 한 번 생성된 사용자는 소속 변경 불가

### 주요 사용자
- **게이트웨이**: AuthHub API를 호출하여 JWT 검증, 권한 확인
- **관리자**: API를 통한 테넌트/조직/사용자/권한 관리 (UI는 추후 제공)

### 성공 기준
- **JWT 검증 응답 시간**: < 50ms (Redis 캐시 Hit 시 < 10ms)
- **로그인 응답 시간**: < 200ms
- **동시 사용자**: 1,000명
- **테넌트 수**: 10개
- **가용성**: 99.9% (월 43분 이하 다운타임)

---

## 🎬 주요 시나리오 (Use Case)

### 시나리오 1: 테넌트 초기 설정
```
1. SYSTEM_ADMIN이 새 Tenant 생성 (POST /api/v1/tenants)
   → tenantId 발급

2. SYSTEM_ADMIN이 조직 생성 (POST /api/v1/organizations)
   → organizationId 발급

3. SYSTEM_ADMIN이 첫 번째 사용자 생성 (POST /api/v1/users)
   → userId 발급
   → TENANT_ADMIN 역할 부여 (POST /api/v1/users/{userId}/roles)

4. TENANT_ADMIN 로그인 → JWT 발급
   → 이제 해당 테넌트 관리 가능
```

### 시나리오 2: B2B 관리자 로그인 → Admin 콘솔 접근
```
1. B2B 관리자가 로그인 (POST /api/v1/auth/login)
   → Gateway → AuthHub
   → Response: {accessToken, refreshToken}

2. Gateway가 JWT를 쿠키/헤더에 저장
   → Admin 콘솔 페이지로 리다이렉트

3. Admin 콘솔에서 API 호출 시마다:
   Gateway → AuthHub (POST /api/v1/auth/validate)
   → JWT 검증 + 권한 확인
   → Gateway → Backend Service (with userId, permissions)

4. Access Token 만료 시:
   Gateway → AuthHub (POST /api/v1/auth/refresh with RefreshToken)
   → 새 Access Token + 새 Refresh Token 발급 (RTR)
```

### 시나리오 3: 역할 변경 → 권한 캐시 무효화
```
1. ORG_ADMIN이 사용자에게 새 역할 부여
   (POST /api/v1/users/{userId}/roles)
   → UserRole 생성 (DB)
   → Redis `user:permissions:{userId}` 삭제 (캐시 무효화)

2. 해당 사용자가 다음 API 호출 시:
   Gateway → AuthHub (POST /api/v1/auth/validate)
   → JWT는 여전히 유효 (만료 전)
   → Redis 캐시 미스 → DB에서 권한 재조회
   → 새 권한으로 캐시 재생성 (TTL: 10분)

3. 다음 15분 이내 (Access Token 만료 전):
   → Redis 캐시에서 새 권한 반환 (캐시 Hit)

4. Access Token 만료 후 재발급 시:
   → 새 JWT Claims에 새 권한 포함
```

### 시나리오 4: 조직 삭제 시도 (하위 사용자 존재)
```
1. TENANT_ADMIN이 조직 삭제 시도
   (DELETE /api/v1/organizations/{orgId})

2. AuthHub가 하위 사용자 존재 확인
   → SELECT COUNT(*) FROM users WHERE organization_id = ?

3. 하위 사용자가 1명 이상 존재하면:
   → 409 Conflict
   → Error Code: ORG_002 (Organization Has Users)
   → Message: "하위 사용자가 존재하여 삭제할 수 없습니다"

4. 조직 삭제 절차:
   a. 모든 하위 사용자를 다른 조직으로 이동 (현재 미지원)
   b. 또는 모든 하위 사용자를 먼저 삭제 (Soft Delete)
   c. 그 후 조직 삭제 가능 (Soft Delete)
```

---

## ⚙️ v1 제약사항 (Constraints)

이 섹션은 v1에서 **의도적으로 제한한 기능**들입니다. 나중에 확장 가능하도록 설계는 유지하되, 당장은 구현하지 않습니다.

### 사용자 (User)
- ✅ **한 User는 한 Tenant, 한 Organization만 소속**
- ✅ **조직/테넌트 간 이동 불가** (v2에서 고려)
- ✅ **userType: INTERNAL만 지원** (PUBLIC은 미사용, B2C 제외)

### 조직 (Organization)
- ✅ **계층 구조 없음** (Flat 구조만)
- ✅ **하위 사용자 있으면 삭제 금지** (비즈니스 규칙)

### 역할 (Role)
- ✅ **계층/상속 없음** (Flat RBAC)
- ✅ **역할 간 권한 상속 없음** (각 역할은 독립적)

### 토큰 (RefreshToken)
- ✅ **1 User 최대 5개 RefreshToken** (다중 기기 지원)
- ✅ **RTR (Refresh Token Rotation)**: 재발급 시 기존 토큰 무효화

### 삭제 정책
- ✅ **모든 엔티티는 Soft Delete** (deletedAt 설정)
- ✅ **Hard Delete 없음** (데이터 보존 원칙)

### 인증 방식
- ✅ **이메일/비밀번호 로그인만** (소셜 로그인 제외)
- ✅ **MFA 설정만 제공** (실제 구현 나중에)

### 게이트웨이 연동
- ✅ **Gateway가 /auth/validate 호출** (v1에서는 매번 호출)
- 🔮 **v2 최적화 방향**:
  - Gateway가 JWT를 직접 검증하는 로컬 검증 모드 지원
  - 토큰 클레임 설계와 키 관리 구조는 이를 고려하여 유지
  - Public Key 공유 (JWKS) 및 Redis 캐시 활용
  - **목표**: 네트워크 홉 제거 (Gateway → AuthHub 호출 없이 직접 검증)

---

## 🏗️ Layer별 요구사항

### 1. Domain Layer

#### 1.1 Aggregate: Tenant (테넌트)

**속성**:
- `id`: UUID v7 (PK)
- `name`: String (테넌트 이름, 예: "Company A")
- `domain`: String (선택, 예: "companyA.example.com")
- `status`: TenantStatus (ACTIVE, SUSPENDED, DELETED)
- `mfaRequired`: boolean (MFA 필수 여부)
- `maxUsers`: int (최대 사용자 수, 0 = 무제한)
- `accessTokenTtlSeconds`: int (Access Token 만료 시간, 기본 900초)
- `refreshTokenTtlDays`: int (Refresh Token 만료 시간, 기본 7일)
- `createdAt`: LocalDateTime
- `deletedAt`: LocalDateTime (Soft Delete)

**비즈니스 규칙**:
1. **테넌트 간 데이터 격리**: Row-Level (같은 DB, TenantId로 구분)
2. **테넌트 삭제**: Soft Delete (deletedAt 설정)
3. **상태 전환**:
   ```
   ACTIVE ⇄ SUSPENDED → DELETED
   ```
4. **설정 기본값**:
   - `mfaRequired`: false
   - `maxUsers`: 0 (무제한)
   - `accessTokenTtlSeconds`: 900 (15분)
   - `refreshTokenTtlDays`: 7

5. **SUSPENDED vs DELETED 구분**:
   - **SUSPENDED**: 로그인/토큰 발급 모두 차단, 데이터는 유지
     - 사용 사례: 테넌트 계약 만료, 결제 미납 등
     - 복구 가능: SUSPENDED → ACTIVE
   - **DELETED**: Soft Delete (deletedAt 설정), 실 서비스에서 거의 사용 X
     - 사용 사례: 내부 운영 도구에서만 사용
     - 복구 불가 (또는 복잡한 복구 절차 필요)

**Zero-Tolerance 규칙 준수**:
- ✅ Lombok 금지 (Plain Java 사용)
- ✅ Law of Demeter 준수

---

#### 1.2 Aggregate: Organization (조직)

**속성**:
- `id`: UUID (PK)
- `tenantId`: UUID (FK - Long FK 전략)
- `name`: String (조직 이름, 예: "Marketing Team")
- `description`: String (선택)
- `status`: OrganizationStatus (ACTIVE, INACTIVE, DELETED)
- `createdAt`: LocalDateTime
- `deletedAt`: LocalDateTime (Soft Delete)

**비즈니스 규칙**:
1. **조직 수 제한**: 무제한 (Tenant 10개 수준에서 문제 없음)
2. **조직 삭제 규칙**:
   - **하위 사용자 존재 시 삭제 금지** (비즈니스 예외 발생)
   - 삭제 절차:
     1. 모든 하위 사용자를 먼저 삭제 (Soft Delete) 또는
     2. 모든 하위 사용자를 다른 조직으로 이동 (v1 미지원)
     3. 그 후 조직 삭제 가능 (Soft Delete)

3. **INACTIVE vs DELETED 구분**:
   - **INACTIVE**: 비활성화 (로그인 차단), 데이터 유지, 복구 가능
     - 사용 사례: 조직 통폐합 준비, 임시 중지 등
     - 기본값: 하위 사용자 있을 때 INACTIVE 권장
   - **DELETED**: Soft Delete (deletedAt 설정), 하위 사용자 없을 때만 가능
     - 사용 사례: 조직 완전 폐쇄, 하위 사용자 모두 삭제 후
     - 복구 불가 (또는 복잡한 복구 절차 필요)

4. **계층 구조**: 없음 (Flat 구조)
5. **조직 간 사용자 이동**: 지원 안 함 (v1 당장 불필요)

**Zero-Tolerance 규칙 준수**:
- ✅ Long FK 전략 (`private UUID tenantId;` - JPA 관계 어노테이션 금지)
- ✅ Lombok 금지

---

#### 1.3 Aggregate: User (사용자)

**속성**:
- `id`: UUID (PK)
- `tenantId`: UUID (FK - Long FK 전략)
- `organizationId`: UUID (FK - Long FK 전략)
- `email`: String (Unique within Tenant)
- `username`: String (Unique within Tenant)
- `passwordHash`: String (BCrypt, strength 12)
- `phoneNumber`: String (선택)
- `status`: UserStatus (ACTIVE, INACTIVE, SUSPENDED, DELETED)
- `lastLoginAt`: LocalDateTime
- `createdAt`: LocalDateTime
- `deletedAt`: LocalDateTime (Soft Delete)

**비즈니스 규칙**:
1. **사용자 소속**:
   - 하나의 조직에만 소속
   - 하나의 테넌트에만 소속
   - 조직/테넌트 간 이동 불가 (당장 불필요)

2. **이메일 중복 체크**: **테넌트 내 금지** (다른 테넌트는 허용)
   - Unique Constraint: `(tenant_id, email)`

3. **사용자명 중복 체크**: **테넌트 내 금지**
   - Unique Constraint: `(tenant_id, username)`

4. **비밀번호 정책**:
   - 최소 8자
   - 영문 + 숫자 필수
   - 특수문자 선택
   - BCrypt strength 12

5. **로그인 ID**: email 또는 username (둘 다 지원)

6. **계정 잠금**: 우선 미지원 (나중에)

7. **비밀번호 만료**: 우선 미지원 (나중에)

8. **상태 전환**:
   ```
   ACTIVE ⇄ INACTIVE
   ACTIVE → SUSPENDED
   SUSPENDED → ACTIVE
   * → DELETED (Soft Delete)
   ```

**Zero-Tolerance 규칙 준수**:
- ✅ Long FK 전략
- ✅ Lombok 금지
- ✅ Law of Demeter (Getter 체이닝 금지)
  - `user.getOrganizationName()` (O)
  - `user.getOrganization().getName()` (X - 관계 어노테이션 없으므로 불가능)

---

#### 1.4 Aggregate: Role (역할)

**속성**:
- `id`: Long (PK, Auto Increment)
- `code`: String (Unique, 예: "ORG_ADMIN", "ORG_USER")
- `name`: String (예: "조직 관리자")
- `description`: String
- `scope`: RoleScope (SYSTEM, TENANT, ORGANIZATION)

**비즈니스 규칙**:
1. **역할 체계** (RBAC - Role-Based Access Control):
   ```
   SYSTEM_ADMIN (전체 시스템 관리)
   TENANT_ADMIN (테넌트 관리자)
   ORG_ADMIN (조직 관리자)
   ORG_MANAGER (조직 매니저 - 선택)
   ORG_USER (일반 사용자)
   ```

2. **역할 계층**: 없음 (Flat 구조, 권한 상속 없음)

3. **사용자 다중 역할**: 가능 (예: ORG_ADMIN + ORG_USER)

4. **역할-권한 매핑**: N:M (role_permissions 중간 테이블)

5. **역할 변경 시 캐시 갱신**:
   - Redis `user:permissions:{userId}` 삭제
   - TTL: 10분

6. **Role.scope와 UserRole.scope 예시**:

   **Role.scope** (역할 자체의 범위 - 역할이 어느 레벨에서 작동하는지):
   ```java
   // SYSTEM_ADMIN 역할 정의
   Role systemAdmin = new Role(
       code: "SYSTEM_ADMIN",
       scope: RoleScope.SYSTEM  // 전체 시스템 레벨
   );

   // TENANT_ADMIN 역할 정의
   Role tenantAdmin = new Role(
       code: "TENANT_ADMIN",
       scope: RoleScope.TENANT  // 테넌트 레벨
   );

   // ORG_ADMIN 역할 정의
   Role orgAdmin = new Role(
       code: "ORG_ADMIN",
       scope: RoleScope.ORGANIZATION  // 조직 레벨
   );
   ```

   **UserRole.scope** (사용자에게 부여된 역할의 적용 범위 - 구체적인 컨텍스트):
   ```java
   // 예시 1: SYSTEM_ADMIN (전체 시스템)
   UserRole(
       userId: user123,
       roleId: systemAdmin.id,
       scope: "SYSTEM"  // 전체 시스템에 대해
   );
   // → 결과: 모든 Tenant, 모든 Organization에 대해 권한 행사 가능

   // 예시 2: TENANT_ADMIN (특정 테넌트)
   UserRole(
       userId: user456,
       roleId: tenantAdmin.id,
       scope: "TENANT:tenant-uuid-123"  // 특정 테넌트에 대해
   );
   // → 결과: tenant-uuid-123 내 모든 Organization에 대해 권한 행사 가능

   // 예시 3: ORG_ADMIN (특정 조직)
   UserRole(
       userId: user789,
       roleId: orgAdmin.id,
       scope: "ORG:org-uuid-456"  // 특정 조직에 대해
   );
   // → 결과: org-uuid-456 조직 내에서만 권한 행사 가능

   // 예시 4: 다중 역할 (두 조직의 관리자)
   UserRole(
       userId: user789,
       roleId: orgAdmin.id,
       scope: "ORG:org-uuid-456"
   );
   UserRole(
       userId: user789,
       roleId: orgAdmin.id,
       scope: "ORG:org-uuid-789"  // 다른 조직에도 ORG_ADMIN
   );
   // → 결과: 두 조직 모두에서 관리자 권한
   ```

   **권한 체크 예시 코드**:
   ```java
   // 예시: 사용자가 특정 조직의 사용자를 생성할 수 있는지 확인
   public boolean canCreateUserInOrg(UUID userId, UUID targetOrgId) {
       List<UserRole> userRoles = userRoleRepository.findByUserId(userId);

       for (UserRole userRole : userRoles) {
           // SYSTEM_ADMIN: 모든 조직 가능
           if (userRole.getScope().equals("SYSTEM")) {
               return true;
           }

           // TENANT_ADMIN: 같은 테넌트 내 모든 조직 가능
           if (userRole.getScope().startsWith("TENANT:")) {
               UUID tenantId = extractTenantId(userRole.getScope());
               Organization targetOrg = orgRepository.findById(targetOrgId);
               if (targetOrg.getTenantId().equals(tenantId)) {
                   return true;
               }
           }

           // ORG_ADMIN: 해당 조직만 가능
           if (userRole.getScope().equals("ORG:" + targetOrgId)) {
               return true;
           }
       }

       return false;
   }
   ```

**초기 데이터** (Flyway Migration):
```sql
INSERT INTO roles (code, name, description, scope) VALUES
('SYSTEM_ADMIN', '시스템 관리자', '전체 시스템 관리 권한', 'SYSTEM'),
('TENANT_ADMIN', '테넌트 관리자', '테넌트 내 모든 권한', 'TENANT'),
('ORG_ADMIN', '조직 관리자', '조직 내 사용자 및 권한 관리', 'ORGANIZATION'),
('ORG_USER', '일반 사용자', '기본 읽기 권한', 'ORGANIZATION');
```

---

#### 1.5 Aggregate: Permission (권한)

**속성**:
- `id`: Long (PK, Auto Increment)
- `code`: String (Unique, 예: "order:read", "user:manage")
- `resource`: String (예: "order", "user", "organization")
- `action`: String (예: "read", "write", "delete", "manage")
- `description`: String

**비즈니스 규칙**:
1. **권한 형식**: `{resource}:{action}` (예: `order:read`, `user:manage`)

2. **권한 부여 방식**:
   - **역할을 통한 부여** (주 방식)
   - 직접 부여 (선택, 당장 불필요)

3. **권한 범위**: 권한 자체에는 범위 없음, UserRole에 scope 저장

4. **권한 변경 시 캐시 갱신**:
   - Redis `user:permissions:{userId}` 삭제
   - TTL: 10분

**초기 데이터** (Flyway Migration):
```sql
INSERT INTO permissions (code, resource, action, description) VALUES
-- Tenant 권한
('tenant:manage', 'tenant', 'manage', '테넌트 관리'),

-- Organization 권한
('organization:read', 'organization', 'read', '조직 조회'),
('organization:write', 'organization', 'write', '조직 생성/수정'),
('organization:delete', 'organization', 'delete', '조직 삭제'),

-- User 권한
('user:read', 'user', 'read', '사용자 조회'),
('user:write', 'user', 'write', '사용자 생성/수정'),
('user:delete', 'user', 'delete', '사용자 삭제'),
('user:manage', 'user', 'manage', '사용자 전체 관리'),

-- Role 권한
('role:read', 'role', 'read', '역할 조회'),
('role:assign', 'role', 'assign', '역할 부여/회수');
```

**역할-권한 매핑** (초기 데이터):
```sql
-- SYSTEM_ADMIN: 모든 권한
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'SYSTEM_ADMIN';

-- TENANT_ADMIN: 테넌트 내 모든 권한 (tenant:manage 제외)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'TENANT_ADMIN' AND p.code != 'tenant:manage';

-- ORG_ADMIN: 조직/사용자/역할 관리
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'ORG_ADMIN'
AND p.code IN ('organization:read', 'user:manage', 'role:read', 'role:assign');

-- ORG_USER: 읽기만
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'ORG_USER'
AND p.action = 'read';
```

---

#### 1.6 Aggregate: RefreshToken (리프레시 토큰)

**속성**:
- `id`: UUID v7 (PK)
- `userId`: UUID (FK - Long FK 전략)
- `tokenHash`: String (SHA-256, Unique)
- `expiresAt`: LocalDateTime
- `isRevoked`: boolean
- `createdAt`: LocalDateTime

**비즈니스 규칙**:
1. **RefreshToken 만료 기간**: 7일

2. **Rotation 전략** (RTR - Refresh Token Rotation):
   - 재발급 시 기존 토큰 무효화 (`isRevoked = true`)
   - 새 토큰 생성 (UUID v7)

3. **사용자당 토큰 개수 제한**: 5개 (다중 기기 지원)
   - 6번째 토큰 발급 시 가장 오래된 토큰 삭제

4. **로그아웃 시**: 해당 RefreshToken만 무효화

5. **전체 로그아웃** (관리자 기능):
   - 사용자의 모든 RefreshToken 무효화

6. **저장소**: MySQL (영구 보관 필요)

7. **동시성 제어**: Pessimistic Lock (`SELECT FOR UPDATE`)
   - RefreshToken 재발급 시 동시 요청 방지

**Zero-Tolerance 규칙 준수**:
- ✅ Long FK 전략
- ✅ Lombok 금지

---

#### 1.7 Aggregate: AuditLog (감사 로그)

**속성**:
- `id`: Long (PK, Auto Increment)
- `tenantId`: UUID (FK)
- `userId`: UUID (FK)
- `action`: String (예: "LOGIN", "LOGOUT", "ROLE_GRANTED")
- `resource`: String (예: "User", "Organization")
- `detail`: String (JSON, 선택)
- `ipAddress`: String
- `userAgent`: String
- `createdAt`: LocalDateTime

**비즈니스 규칙**:
1. **기록 이벤트**:
   - 로그인/로그아웃
   - 권한 변경 (역할 부여/회수)
   - 조직/사용자 CRUD

2. **저장 방식**: 비동기 (Transaction 밖, Spring `@Async`)

3. **보관 기간**: 1년

4. **API**: 당장 불필요 (로그만 저장)

**Zero-Tolerance 규칙 준수**:
- ✅ Long FK 전략

---

### 2. Application Layer

#### 2.1 Command UseCase (18개)

##### 인증 관련 (5개)

**1. LoginUseCase**
- **Input**: `LoginCommand(loginId, password, tenantId)`
- **Output**: `LoginResponse(accessToken, refreshToken, userInfo)`
- **Transaction**: Yes (DB Write)
- **비즈니스 로직**:
  1. Tenant 조회 (tenantId)
     - 존재하지 않으면 → `TenantNotFoundException`
     - 상태가 ACTIVE가 아니면 → `TenantSuspendedException`
  2. User 조회 (loginId, tenantId)
     - loginId로 email 또는 username 검색
     - 존재하지 않으면 → `InvalidCredentialsException` (보안: 404 대신 401)
     - 상태가 ACTIVE가 아니면 → `UserInactiveException`
  3. 비밀번호 검증
     - BCrypt.matches(password, user.passwordHash)
     - 실패 시 → `InvalidCredentialsException`
  4. JWT 생성 (Access Token)
     - Claims: userId, email, tenantId, organizationId, roles, permissions
     - 만료 시간: tenant.accessTokenTtlSeconds (기본 900초)
     - 서명: RS256 (Private Key)
  5. RefreshToken 생성
     - UUID v7 생성
     - SHA-256 Hash 저장
     - 만료 시간: tenant.refreshTokenTtlDays (기본 7일)
     - 기존 토큰 개수 체크 (5개 초과 시 가장 오래된 것 삭제)
  6. AuditLog 기록 (비동기)
     - action: "LOGIN"
     - userId, ipAddress, userAgent
  7. User.lastLoginAt 업데이트
  8. **Transaction 커밋**
  9. Response 반환

**Transaction 경계**:
- ✅ Transaction 내: DB Write (RefreshToken 저장, User 업데이트)
- ✅ Transaction 밖: AuditLog (비동기)

---

**2. LogoutUseCase**
- **Input**: `LogoutCommand(refreshToken)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. RefreshToken 조회 (tokenHash)
     - 존재하지 않으면 → `InvalidRefreshTokenException`
  2. RefreshToken 무효화 (`isRevoked = true`)
  3. AuditLog 기록 (비동기)
     - action: "LOGOUT"
  4. **Transaction 커밋**

---

**3. RefreshAccessTokenUseCase**
- **Input**: `RefreshTokenCommand(refreshToken)`
- **Output**: `RefreshTokenResponse(accessToken, refreshToken)`
- **Transaction**: Yes (RTR)
- **비즈니스 로직**:
  1. RefreshToken 조회 (tokenHash) - **Pessimistic Lock**
     - 존재하지 않으면 → `InvalidRefreshTokenException`
     - isRevoked == true → `RevokedRefreshTokenException`
     - expiresAt < now → `ExpiredRefreshTokenException`
  2. User 조회 (refreshToken.userId)
     - 상태가 ACTIVE가 아니면 → `UserInactiveException`
  3. Tenant 조회 (user.tenantId)
     - 상태가 ACTIVE가 아니면 → `TenantSuspendedException`
  4. 기존 RefreshToken 무효화 (RTR)
     - `refreshToken.revoke()`
  5. 새 RefreshToken 생성
     - 새 UUID v7, 새 만료 시간
  6. 새 Access Token 생성
  7. **Transaction 커밋**
  8. Response 반환

**동시성 제어**:
- ✅ Pessimistic Lock (`SELECT FOR UPDATE`) - 동시 재발급 방지

---

**4. ValidateTokenUseCase** (게이트웨이용)
- **Input**: `ValidateTokenCommand(accessToken)`
- **Output**: `ValidateTokenResponse(valid, userId, permissions)`
- **Transaction**: ReadOnly
- **비즈니스 로직**:
  1. JWT 검증 (Public Key로 서명 검증)
     - 서명 실패 → `InvalidTokenException`
     - 만료 확인 → `ExpiredTokenException`
  2. Claims 추출
     - userId, email, tenantId, organizationId, permissions
  3. User 상태 확인 (캐시 우선)
     - Redis에서 userId로 조회 (TTL: 5분)
     - 캐시 미스 시 DB 조회
     - 상태가 ACTIVE가 아니면 → `UserInactiveException`
  4. Response 반환

**성능 최적화**:
- ✅ Redis 캐시 (User 상태, TTL: 5분)
- ✅ 목표: < 50ms (캐시 Hit 시 < 10ms)

---

**5. RevokeAllUserTokensUseCase** (관리자 기능)
- **Input**: `RevokeAllTokensCommand(userId)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. User 조회 (userId)
     - 존재하지 않으면 → `UserNotFoundException`
  2. 모든 RefreshToken 무효화
     - `UPDATE refresh_tokens SET is_revoked = true WHERE user_id = ?`
  3. Redis 캐시 삭제
     - `user:status:{userId}` 삭제
     - `user:permissions:{userId}` 삭제
  4. AuditLog 기록 (비동기)
     - action: "ALL_TOKENS_REVOKED"
  5. **Transaction 커밋**

---

##### 사용자 관리 (5개)

**6. CreateUserUseCase**
- **Input**: `CreateUserCommand(organizationId, email, username, password, phoneNumber)`
- **Output**: `UserResponse`
- **Transaction**: Yes
- **비즈니스 로직**:
  1. Organization 조회 (organizationId)
     - 존재하지 않으면 → `OrganizationNotFoundException`
  2. 이메일 중복 체크 (tenantId, email)
     - 중복이면 → `DuplicateEmailException`
  3. 사용자명 중복 체크 (tenantId, username)
     - 중복이면 → `DuplicateUsernameException`
  4. 비밀번호 해시 (BCrypt, strength 12)
  5. User 생성 (Domain Aggregate)
  6. User 저장
  7. AuditLog 기록 (비동기)
     - action: "USER_CREATED"
  8. **Transaction 커밋**

---

**7. UpdateUserUseCase**
- **Input**: `UpdateUserCommand(userId, email, username, phoneNumber)`
- **Output**: `UserResponse`
- **Transaction**: Yes

**8. DeleteUserUseCase** (Soft Delete)
- **Input**: `DeleteUserCommand(userId)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. User 조회 (userId)
  2. RefreshToken 모두 무효화
  3. User.deletedAt 설정 (Soft Delete)
  4. AuditLog 기록 (비동기)

**9. ActivateUserUseCase**
- **Input**: `ActivateUserCommand(userId)`
- **Output**: void
- **Transaction**: Yes

**10. SuspendUserUseCase**
- **Input**: `SuspendUserCommand(userId)`
- **Output**: void
- **Transaction**: Yes

---

##### 조직 관리 (4개)

**11. CreateOrganizationUseCase**
- **Input**: `CreateOrganizationCommand(tenantId, name, description)`
- **Output**: `OrganizationResponse`
- **Transaction**: Yes

**12. UpdateOrganizationUseCase**
- **Input**: `UpdateOrganizationCommand(orgId, name, description)`
- **Output**: `OrganizationResponse`
- **Transaction**: Yes

**13. DeleteOrganizationUseCase** (Soft Delete)
- **Input**: `DeleteOrganizationCommand(orgId)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. Organization 조회 (orgId)
  2. 하위 사용자 존재 체크
     - 존재하면 → `OrganizationHasUsersException` (삭제 금지)
  3. Organization.deletedAt 설정 (Soft Delete)

**14. AddUserToOrganizationUseCase**
- **Input**: `AddUserToOrgCommand(orgId, userId)`
- **Output**: void
- **Transaction**: Yes

---

##### 권한 관리 (4개)

**15. GrantRoleToUserUseCase**
- **Input**: `GrantRoleCommand(userId, roleCode, scope)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. User 조회 (userId)
  2. Role 조회 (roleCode)
  3. 중복 체크 (userId, roleId, scope)
     - 이미 부여된 역할이면 → `RoleAlreadyGrantedException`
  4. UserRole 생성 및 저장
  5. Redis 캐시 삭제 (`user:permissions:{userId}`)
  6. AuditLog 기록 (비동기)
     - action: "ROLE_GRANTED"
  7. **Transaction 커밋**

**16. RevokeRoleFromUserUseCase**
- **Input**: `RevokeRoleCommand(userId, roleId)`
- **Output**: void
- **Transaction**: Yes
- **비즈니스 로직**:
  1. UserRole 조회 및 삭제
  2. Redis 캐시 삭제 (`user:permissions:{userId}`)
  3. AuditLog 기록 (비동기)

**17. GrantPermissionToUserUseCase** (당장 불필요, 나중에)
**18. RevokePermissionFromUserUseCase** (당장 불필요, 나중에)

---

#### 2.2 Query UseCase (6개)

**19. GetUserUseCase**
- **Input**: `GetUserQuery(userId)`
- **Output**: `UserDetailResponse`
- **Transaction**: ReadOnly

**20. ListUsersInOrgUseCase**
- **Input**: `ListUsersQuery(orgId, page, size, status)`
- **Output**: `Page<UserSummaryResponse>`
- **Transaction**: ReadOnly
- **최적화**: QueryDSL DTO Projection (N+1 방지)

**21. GetOrganizationUseCase**
- **Input**: `GetOrganizationQuery(orgId)`
- **Output**: `OrganizationDetailResponse`
- **Transaction**: ReadOnly

**22. ListOrganizationsUseCase**
- **Input**: `ListOrganizationsQuery(tenantId, page, size)`
- **Output**: `Page<OrganizationSummaryResponse>`
- **Transaction**: ReadOnly

**23. GetUserPermissionsUseCase**
- **Input**: `GetUserPermissionsQuery(userId)`
- **Output**: `List<PermissionResponse>`
- **Transaction**: ReadOnly
- **캐시**: Redis (TTL: 10분)

**24. ListRolesUseCase**
- **Input**: `ListRolesQuery(scope)`
- **Output**: `List<RoleResponse>`
- **Transaction**: ReadOnly

---

#### 2.3 Zero-Tolerance 규칙 준수

- ✅ **Command/Query 분리** (CQRS)
- ✅ **Transaction 경계 엄격 관리**
  - Transaction 내: DB Write만
  - Transaction 밖: 외부 API 호출, AuditLog (비동기)
- ✅ **ReadOnly Transaction** (조회 UseCase)
- ✅ **Assembler 사용** (Domain ↔ DTO 변환)

---

### 3. Persistence Layer

#### 3.1 JPA Entity 목록

1. **TenantJpaEntity** (`tenants`)
2. **OrganizationJpaEntity** (`organizations`)
3. **UserJpaEntity** (`users`)
4. **RefreshTokenJpaEntity** (`refresh_tokens`)
5. **RoleJpaEntity** (`roles`)
6. **PermissionJpaEntity** (`permissions`)
7. **RolePermissionJpaEntity** (`role_permissions` - 중간 테이블)
8. **UserRoleJpaEntity** (`user_roles`)
9. **AuditLogJpaEntity** (`audit_logs`)

#### 3.2 인덱스 전략

**users 테이블**:
```sql
CREATE INDEX idx_user_tenant_id ON users(tenant_id);
CREATE INDEX idx_user_organization_id ON users(organization_id);
CREATE INDEX idx_user_status ON users(status);
CREATE UNIQUE INDEX uk_user_tenant_email ON users(tenant_id, email);
CREATE UNIQUE INDEX uk_user_tenant_username ON users(tenant_id, username);
```

**refresh_tokens 테이블**:
```sql
CREATE INDEX idx_refresh_token_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_expires_at ON refresh_tokens(expires_at);
CREATE UNIQUE INDEX uk_refresh_token_hash ON refresh_tokens(token_hash);
```

**user_roles 테이블**:
```sql
CREATE INDEX idx_user_role_user_id ON user_roles(user_id);
CREATE INDEX idx_user_role_role_id ON user_roles(role_id);
CREATE UNIQUE INDEX uk_user_role_scope ON user_roles(user_id, role_id, scope);
```

#### 3.3 Repository 전략

**Spring Data JPA**:
- 단순 CRUD: `UserJpaRepository extends JpaRepository`

**QueryDSL**:
- 복잡한 조회: `UserQueryDslRepository`
- DTO Projection (N+1 방지)
- 동적 쿼리 (필터링, 정렬)

**동시성 제어**:
- Pessimistic Lock: `RefreshTokenJpaRepository.findByTokenHashWithLock()`

#### 3.4 Zero-Tolerance 규칙 준수

- ✅ **Long FK 전략** (`private UUID tenantId;` - JPA 관계 어노테이션 금지)
- ✅ **QueryDSL DTO Projection** (N+1 방지)
- ✅ **Lombok 금지** (Plain Java)

---

### 4. REST API Layer

#### 4.1 API 엔드포인트 요약

##### 인증 API (5개)

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/v1/auth/login` | 로그인 | 201 Created |
| POST | `/api/v1/auth/logout` | 로그아웃 | 204 No Content |
| POST | `/api/v1/auth/refresh` | Token 재발급 | 200 OK |
| POST | `/api/v1/auth/validate` | Token 검증 (게이트웨이용) | 200 OK |
| GET | `/api/v1/auth/.well-known/jwks.json` | JWKS Public Key | 200 OK |

##### 사용자 관리 API (7개)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/users` | 사용자 생성 | ✅ (user:write) |
| GET | `/api/v1/users/{userId}` | 사용자 조회 | ✅ (user:read) |
| PUT | `/api/v1/users/{userId}` | 사용자 수정 | ✅ (user:write) |
| DELETE | `/api/v1/users/{userId}` | 사용자 삭제 | ✅ (user:delete) |
| POST | `/api/v1/users/{userId}/activate` | 활성화 | ✅ (user:manage) |
| POST | `/api/v1/users/{userId}/suspend` | 정지 | ✅ (user:manage) |
| GET | `/api/v1/users/{userId}/permissions` | 권한 조회 | ✅ (user:read) |

##### 조직 관리 API (7개)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/organizations` | 조직 생성 | ✅ (organization:write) |
| GET | `/api/v1/organizations` | 조직 목록 | ✅ (organization:read) |
| GET | `/api/v1/organizations/{orgId}` | 조직 조회 | ✅ (organization:read) |
| PUT | `/api/v1/organizations/{orgId}` | 조직 수정 | ✅ (organization:write) |
| DELETE | `/api/v1/organizations/{orgId}` | 조직 삭제 | ✅ (organization:delete) |
| GET | `/api/v1/organizations/{orgId}/users` | 조직 내 사용자 | ✅ (organization:read) |
| POST | `/api/v1/organizations/{orgId}/users` | 사용자 추가 | ✅ (organization:write) |

##### 역할/권한 API (6개)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/roles` | 역할 목록 | ✅ (role:read) |
| GET | `/api/v1/roles/{roleId}` | 역할 조회 | ✅ (role:read) |
| GET | `/api/v1/roles/{roleId}/permissions` | 역할별 권한 | ✅ (role:read) |
| POST | `/api/v1/users/{userId}/roles` | 역할 부여 | ✅ (role:assign) |
| DELETE | `/api/v1/users/{userId}/roles/{roleId}` | 역할 회수 | ✅ (role:assign) |
| GET | `/api/v1/permissions` | 권한 목록 | ✅ (role:read) |

##### 테넌트 API (게이트웨이용, 2개)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/tenants/{tenantId}/config` | 테넌트 설정 | ❌ (Internal) |
| GET | `/api/v1/tenants/{tenantId}/permission-spec` | 권한 스펙 | ❌ (Internal) |

**총 27개 API 엔드포인트**

---

#### 4.2 인증/인가 전략

##### JWT 설정

**Access Token**:
- **만료 시간**: 15분 (900초)
- **서명 알고리즘**: RS256 (비대칭키)
- **Claims**:
  ```json
  {
    "sub": "userId (UUID)",
    "email": "user@example.com",
    "username": "john_doe",
    "tenantId": "tenant-uuid",
    "organizationId": "org-uuid",
    "permissions": ["user:read", "order:write"],
    "iat": 1705123456,
    "exp": 1705124356
  }
  ```

**Refresh Token**:
- **만료 시간**: 7일
- **저장 방식**: SHA-256 Hash (MySQL)
- **Rotation**: Yes (RTR - Refresh Token Rotation)

**키 관리**:
- Private Key: `/keys/private.pem` (서버 내부)
- Public Key: `/keys/public.pem` (게이트웨이 공유)
- JWKS Endpoint: `GET /api/v1/auth/.well-known/jwks.json`

---

##### Spring Security 설정

**JwtAuthenticationFilter**:
1. `Authorization: Bearer {token}` 헤더 추출
2. JWT 검증 (Public Key 서명 확인)
3. Claims 파싱 (userId, permissions)
4. Redis 캐시에서 User 상태 확인 (TTL: 5분)
   - 캐시 미스 시 DB 조회
5. 상태가 ACTIVE이면 `JwtAuthenticationToken` 생성
6. `SecurityContextHolder`에 설정

**권한 체크**:
```java
@PreAuthorize("hasAuthority('user:write')")
public UserResponse createUser(@RequestBody CreateUserRequest request) {
    // ...
}
```

---

##### Redis 캐시 전략

**캐시 항목**:
1. `user:status:{userId}` - User 상태 (TTL: 5분)
2. `user:permissions:{userId}` - User 권한 목록 (TTL: 10분)
3. `tenant:config:{tenantId}` - Tenant 설정 (TTL: 1시간)

**캐시 무효화**:
- 역할/권한 변경 시: `user:permissions:{userId}` 삭제
- User 상태 변경 시: `user:status:{userId}` 삭제
- Tenant 설정 변경 시: `tenant:config:{tenantId}` 삭제

---

#### 4.3 Error Handling

##### HTTP 상태 코드 전략

| Status Code | 사용 시나리오 |
|-------------|--------------|
| 200 OK | 조회/수정 성공 |
| 201 Created | 생성 성공 (로그인, 사용자 생성 등) |
| 204 No Content | 삭제 성공 (로그아웃 등) |
| 400 Bad Request | 유효성 검증 실패 |
| 401 Unauthorized | 인증 실패 (로그인 실패, Token 만료) |
| 403 Forbidden | 권한 없음 (테넌트 정지, 권한 부족) |
| 404 Not Found | 리소스 없음 |
| 409 Conflict | 비즈니스 규칙 위반 (중복 이메일, 조직에 사용자 존재 등) |
| 500 Internal Server Error | 서버 오류 |

##### Error Response 형식

**기본 Error Response**:
```json
{
  "errorCode": "AUTH_001",
  "message": "이메일 또는 비밀번호가 올바르지 않습니다",
  "timestamp": "2025-01-13T12:34:56Z",
  "path": "/api/v1/auth/login"
}
```

**Validation Error Response**:
```json
{
  "errorCode": "VALIDATION_001",
  "message": "입력값 검증 실패",
  "timestamp": "2025-01-13T12:34:56Z",
  "path": "/api/v1/users",
  "errors": [
    {
      "field": "email",
      "message": "올바른 이메일 형식이 아닙니다"
    },
    {
      "field": "password",
      "message": "비밀번호는 8자 이상이어야 합니다"
    }
  ]
}
```

##### Error Code 체계

**인증 오류 (AUTH_xxx)**:
- `AUTH_001`: 로그인 실패 (Invalid Credentials)
- `AUTH_002`: 토큰 만료 (Expired Token)
- `AUTH_003`: 유효하지 않은 토큰 (Invalid Token)
- `AUTH_004`: 리프레시 토큰 무효화됨 (Revoked Token)

**사용자 오류 (USER_xxx)**:
- `USER_001`: 사용자 없음 (User Not Found)
- `USER_002`: 사용자 비활성 (User Inactive)
- `USER_003`: 중복 이메일 (Duplicate Email)
- `USER_004`: 중복 사용자명 (Duplicate Username)

**테넌트 오류 (TENANT_xxx)**:
- `TENANT_001`: 테넌트 정지 (Tenant Suspended)
- `TENANT_002`: 테넌트 없음 (Tenant Not Found)

**조직 오류 (ORG_xxx)**:
- `ORG_001`: 조직 없음 (Organization Not Found)
- `ORG_002`: 조직에 사용자 존재 (Organization Has Users)

**권한 오류 (ROLE_xxx)**:
- `ROLE_001`: 역할 없음 (Role Not Found)
- `ROLE_002`: 이미 부여된 역할 (Role Already Granted)
- `ROLE_003`: 권한 부족 (Permission Denied)

**유효성 검증 오류 (VALIDATION_xxx)**:
- `VALIDATION_001`: 입력값 검증 실패 (Validation Failed)

---

#### 4.4 Zero-Tolerance 규칙 준수

- ✅ **RESTful 설계 원칙**
- ✅ **일관된 Error Response 형식**
- ✅ **HTTP 상태 코드 전략**
- ✅ **Bean Validation** (`@NotNull`, `@Email`, `@Pattern` 등)

---

## ⚠️ 제약사항

### 비기능 요구사항

#### 성능
- **JWT 검증 응답 시간**: < 50ms (P95)
  - Redis 캐시 Hit 시: < 10ms
- **로그인 응답 시간**: < 200ms (P95)
- **API 응답 시간**: < 500ms (P95)
- **TPS**: 1,000 requests/sec (Peak Time)

#### 보안
- **JWT 서명**: RS256 (비대칭키)
- **RefreshToken**: SHA-256 Hash 저장
- **비밀번호**: BCrypt (strength 12)
- **HTTPS 필수**: TLS 1.2+
- **CORS**: 게이트웨이 도메인만 허용
- **Rate Limiting**: 로그인 API (IP당 분당 10회)

#### 확장성
- **동시 사용자**: 1,000명
- **테넌트 수**: 10개
- **조직 수**: 테넌트당 무제한
- **사용자 수**: 조직당 무제한 (Tenant 설정에 따라 제한 가능)

#### 가용성
- **목표**: 99.9% (월 43분 이하 다운타임)
- **Health Check**: `/actuator/health`
- **모니터링**: Prometheus + Grafana

---

## 🧪 테스트 전략

### Unit Test

#### Domain
- Aggregate 비즈니스 로직 (Tenant, Organization, User, Role, Permission, RefreshToken)
- Value Object 검증 (Email, Password 등)
- Enum 상태 전환 로직

#### Application
- UseCase (Mock PersistencePort)
- Transaction 경계 검증
- 비즈니스 예외 처리

### Integration Test

#### Persistence
- JPA Repository CRUD (TestContainers MySQL)
- QueryDSL 복잡한 쿼리
- 동시성 제어 (Pessimistic Lock)
- Unique Constraint 검증

#### REST API
- Controller (TestRestTemplate - MockMvc 금지)
- Validation 테스트 (400 Bad Request)
- 인증/인가 테스트 (401, 403)
- Error Handling (GlobalExceptionHandler)

### E2E Test

- 로그인 → JWT 발급 → API 호출 → 로그아웃 플로우
- RefreshToken 재발급 플로우
- 역할 부여 → 권한 확인 플로우
- 동시 로그인 (동시성 테스트)

---

## 🚀 개발 계획

### Phase 1: Domain Layer (예상: 5일)
- [ ] Tenant Aggregate 구현
- [ ] Organization Aggregate 구현
- [ ] User Aggregate 구현
- [ ] Role, Permission Aggregate 구현
- [ ] RefreshToken Aggregate 구현
- [ ] AuditLog Aggregate 구현
- [ ] Domain Unit Test (TestFixture 패턴)

### Phase 2: Application Layer (예상: 7일)
- [ ] LoginUseCase 구현 (Mock PersistencePort)
- [ ] RefreshAccessTokenUseCase 구현
- [ ] ValidateTokenUseCase 구현
- [ ] CreateUserUseCase 구현
- [ ] GrantRoleToUserUseCase 구현
- [ ] 나머지 UseCase 구현 (19개)
- [ ] Application Unit Test

### Phase 3: Persistence Layer (예상: 5일)
- [ ] JPA Entity 구현 (9개)
- [ ] Repository 인터페이스 구현
- [ ] QueryDSL 쿼리 구현
- [ ] Flyway Migration Scripts 작성
- [ ] Integration Test (TestContainers MySQL)

### Phase 4: REST API Layer (예상: 6일)
- [ ] Spring Security 설정
- [ ] JwtAuthenticationFilter 구현
- [ ] JwtProvider 구현
- [ ] AuthController 구현
- [ ] UserController, OrganizationController, RoleController 구현
- [ ] GlobalExceptionHandler 구현
- [ ] REST API Integration Test (TestRestTemplate)

### Phase 5: Redis 캐시 (예상: 2일)
- [ ] Redis 설정
- [ ] CacheManager 구현
- [ ] 캐시 무효화 로직 구현
- [ ] 캐시 성능 테스트

### Phase 6: 초기 데이터 및 E2E Test (예상: 3일)
- [ ] Flyway Migration: 초기 Role/Permission 데이터
- [ ] E2E Test 작성
- [ ] 성능 테스트 (JMeter)
- [ ] 보안 테스트

**총 예상 기간**: 28일 (약 4주)

---

## 📚 참고 문서

- [Domain Layer 규칙](../coding_convention/02-domain-layer/)
- [Application Layer 규칙](../coding_convention/03-application-layer/)
- [Persistence Layer 규칙](../coding_convention/04-persistence-layer/)
- [REST API Layer 규칙](../coding_convention/01-adapter-in-layer/rest-api/)
- [Integration Testing 규칙](../coding_convention/05-testing/integration-testing/)

---

## 📝 게이트웨이 통합

### 게이트웨이 → AuthHub API 호출 플로우

**1. 사용자 로그인 요청**:
```
User → Gateway → AuthHub: POST /api/v1/auth/login
AuthHub → Gateway: {accessToken, refreshToken, userInfo}
Gateway → User: {accessToken, refreshToken}
```

**2. API 요청 (JWT 검증)**:
```
User → Gateway (with JWT) → AuthHub: POST /api/v1/auth/validate
AuthHub → Gateway: {valid: true, userId, permissions}
Gateway → Backend Service (with userId, permissions)
```

**3. Token 재발급**:
```
User → Gateway (with RefreshToken) → AuthHub: POST /api/v1/auth/refresh
AuthHub → Gateway: {newAccessToken, newRefreshToken}
Gateway → User: {newAccessToken, newRefreshToken}
```

**4. Tenant 설정 조회** (게이트웨이 시작 시):
```
Gateway → AuthHub: GET /api/v1/tenants/{tenantId}/config
AuthHub → Gateway: {mfaRequired, tokenTTL, ...}
```

**5. Permission Spec 조회** (게이트웨이 시작 시):
```
Gateway → AuthHub: GET /api/v1/tenants/{tenantId}/permission-spec
AuthHub → Gateway: [{code: "order:read", resource: "order", ...}, ...]
```

---

## 🎯 다음 단계

1. **PRD 검토 및 수정** (이해관계자 피드백)
2. **Jira 티켓 생성**: `/jira-from-prd docs/prd/b2b-auth-hub.md`
3. **TDD 계획 수립**: `/kentback-plan docs/prd/b2b-auth-hub.md` (선택)
4. **Phase 1 시작**: Domain Layer TDD 사이클

---

**작성 완료일**: 2025-01-13
**최종 수정일**: 2025-01-13
