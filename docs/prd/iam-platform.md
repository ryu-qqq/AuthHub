# PRD: IAM Platform (AuthHub)

**작성일**: 2025-01-13
**작성자**: Sangwon Ryu
**상태**: Draft
**프로젝트**: AuthHub - 멀티테넌트 통합 인증·인가 플랫폼

---

## 📋 프로젝트 개요

### 비즈니스 목적

**문제 정의**:
현재 각 마이크로서비스(Commerce, Admin, FileFlow 등)가 자체적으로 회원가입/로그인/권한을 처리하고 있어:
- 중복된 인증 로직과 계정 데이터가 여러 곳에 흩어져 있음
- 전화번호 기반 계정과 소셜 로그인 계정이 통합되지 않아 중복 계정 발생
- 서비스가 늘어날수록 각 서비스마다 인증 로직을 넣어야 해서 관리 복잡성 및 보안 위험 증가
- 권한(Role/Permission)이 서비스마다 달라 일관된 접근제어가 어려움
- API Gateway를 통한 일관된 인증/인가/토큰 검증 구조가 현재 불가능한 상태

**해결 방안**:
멀티테넌트 IAM 플랫폼(AuthHub)을 구축해:
- 모든 사용자, 조직, 권한, 인증/인가를 중앙에서 관리
- 전체 서비스가 하나의 통일된 인증 체계를 사용
- Gateway와 연동하여 모든 마이크로서비스가 공동으로 사용

### 주요 사용자

| 사용자 타입 | 설명 | 특징 |
|------------|------|------|
| **PUBLIC 사용자** | 일반 고객 (쇼핑 서비스 이용자) | 전화번호/소셜 로그인, Organization 없음 |
| **INTERNAL 사용자** | 내부 직원, 셀러, 파트너 | Organization 소속 필수, 이메일+비밀번호 로그인 |
| **Organization Admin** | 셀러 브랜드 관리자, 내부 팀 관리자 | 조직 멤버 관리 및 Role 부여 권한 |
| **Super Admin** | 시스템 관리자 (전역 권한) | Tenant/Organization 생성, 시스템 정책 관리 |

### 성공 기준

| 메트릭 | 목표 | 설명 |
|--------|------|------|
| **토큰 발급/검증 응답 시간** | < 100ms (평균) | 인증 성능 |
| **동시 로그인 처리량** | 10,000 req/min 이상 | 동시성 처리 |
| **가용성** | 99.9% (초기), 99.99% (장기) | 서비스 안정성 |
| **소셜 로그인 계정 통합 성공률** | > 99% | 계정 통합 품질 |
| **Gateway 인증 실패율** | < 0.1% (정상 사용자 기준) | 인증 정확성 |
| **서비스별 인증 코드 제거율** | > 80% | 중앙화 효과 |

---

## 🏗️ Layer별 요구사항

### 1. Domain Layer

#### 1.1 Aggregate: User

**속성**:
```java
public class User {
    private UserId userId;              // Value Object (UUID)
    private TenantId tenantId;          // FK (Long)
    private OrganizationId organizationId; // FK (Long, Nullable for PUBLIC)
    private UserType userType;          // Enum: PUBLIC, INTERNAL
    private Credential credential;      // Value Object (전화번호/이메일+비밀번호)
    private UserStatus status;          // Enum: ACTIVE, INACTIVE, SUSPENDED, DELETED
    private UserProfile profile;        // Value Object (이름, 프로필 이미지 등)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**비즈니스 규칙**:

##### 1.1.1 사용자 생성 (User Registration)

**PUBLIC 사용자**:
- ✅ **전화번호 기반 가입**: 전화번호 인증은 프론트엔드에서 처리 (AuthHub는 인증된 전화번호만 받음)
- ✅ **전화번호 중복 방지**: DB Unique Constraint + Application 검증
  - `users` 테이블: `UNIQUE(tenant_id, phone_number)`
  - 중복 시 예외: `DuplicatePhoneNumberException`
- ✅ **소셜 로그인 (Kakao)**: 최초 가입 시 자동 계정 생성
  - Kakao에서 받은 전화번호가 이미 존재하면 계정 통합 프로세스 시작
- ✅ **Organization 소속**: PUBLIC 사용자는 Organization에 소속되지 않음 (`organizationId = null`)
- ✅ **Tenant 할당**: 모든 PUBLIC 사용자는 "Connectly Public Tenant"에 자동 할당

**INTERNAL 사용자**:
- ✅ **생성 권한**: Super Admin 또는 Organization Admin만 생성 가능
- ✅ **필수 정보**: 이메일, 전화번호, 이름 (비밀번호는 별도 설정)
- ✅ **Organization 필수**: INTERNAL 사용자는 반드시 Organization에 소속되어야 함
  - `organizationId != null` (제약 조건)
  - Organization 없이 생성 시 예외: `OrganizationRequiredException`
- ✅ **단일 Organization 소속**: INTERNAL 사용자는 **현재 단일 Organization 소속만 지원**
  - 한 사용자가 여러 Organization에 동시 소속 불가
  - 향후 Multi-Organization 지원 시 별도 설계 필요 (예: `user_organizations` 조인 테이블)
- ✅ **소셜 로그인 불가**: INTERNAL 사용자는 소셜 로그인 지원 안 함 (이메일+비밀번호만)

##### 1.1.2 계정 통합 (Account Merging)

**통합 전략**:
- ✅ **통합 기준**: 동일 전화번호
  - 전화번호 계정 A + 소셜 계정 B (같은 전화번호) → A 계정에 소셜 계정 연동
- ✅ **통합 방향**: 소셜 계정 → 전화번호 계정으로만 통합 가능
  - 전화번호 계정이 소셜 로그인 시도 → 소셜 계정 통합 유도 (UI에서 안내)
  - 소셜 계정이 전화번호로 로그인 시도 → **반려** (`SocialAccountCannotLoginWithPhoneException`)
- ✅ **통합 후 로그인**: 소셜 로그인만 가능 (전화번호 로그인 비활성화)
- ✅ **통합 실패**: 소셜 회원가입 실패 처리 (`AccountMergingFailedException`)

**통합 플로우**:
```
1. 소셜 로그인 시도 (Kakao)
2. AuthHub가 Kakao에서 전화번호 획득
3. 기존 전화번호 계정 존재 여부 확인
4-1. 존재 → 계정 통합 시작
   - SocialAccount 생성 (kakaoId, accessToken 등)
   - User.addSocialAccount(socialAccount)
   - Credential.disablePhoneLogin() (전화번호 로그인 비활성화)
4-2. 존재하지 않음 → 새 계정 생성
   - User 생성 (userType=PUBLIC, credential=SOCIAL)
   - SocialAccount 생성 및 연동
```

##### 1.1.3 사용자 상태 관리

**상태 전환**:
```
ACTIVE ──(관리자 비활성화)──> INACTIVE
  ↓
  (관리자 정지)
  ↓
SUSPENDED ──(관리자 활성화)──> ACTIVE
  ↓
  (사용자 탈퇴/관리자 삭제)
  ↓
DELETED (소프트 삭제)
```

**상태별 제약**:
- ✅ **ACTIVE**: 정상 로그인 가능
- ✅ **INACTIVE**: 로그인 차단 (`UserInactiveException`), 토큰 발급 불가
- ✅ **SUSPENDED**: 로그인 차단 (`UserSuspendedException`), 기존 토큰 즉시 무효화
- ✅ **DELETED**: 소프트 삭제 (개인정보 익명화)
  - `deleted_at` 컬럼에 삭제 시각 기록
  - 전화번호/이메일은 `deleted_{userId}` 형태로 변환
  - Refresh Token 즉시 폐기

**Zero-Tolerance 규칙 준수**:
- ✅ **Law of Demeter**: Getter 체이닝 금지
  - `user.getPhoneNumber()` (O) - User Aggregate 내에서 직접 제공
  - `user.getCredential().getPhoneNumber()` (X) - Credential에 직접 접근 금지
- ✅ **Lombok 금지**: Pure Java 또는 Record 사용
- ✅ **Long FK 전략**: `private Long tenantId`, `private Long organizationId` (관계 어노테이션 금지)

---

#### 1.2 Aggregate: Organization

**속성**:
```java
public class Organization {
    private OrganizationId organizationId; // Value Object (Long)
    private TenantId tenantId;             // FK (Long)
    private OrganizationName name;         // Value Object
    private String description;
    private OrganizationStatus status;     // Enum: ACTIVE, INACTIVE, DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**비즈니스 규칙**:

##### 1.2.1 조직 생성

**생성 권한**:
- ✅ **Super Admin만 생성 가능**
  - Organization Admin은 조직 생성 불가 (멤버 추가만 가능)
- ✅ **필수 정보**:
  - 조직명 (OrganizationName): 2-100자, Tenant 내 Unique
  - 설명 (description): 선택 사항
  - Tenant ID: 반드시 지정 (1 Tenant : N Organization)

**조직 타입**:
- ❌ **조직 타입 없음** (초기 버전에서는 미지원)
  - 향후 확장 시 `SELLER`, `PARTNER`, `INTERNAL_TEAM` 등 추가 가능

**조직 계층 구조**:
- ❌ **계층 구조 없음** (Flat Structure)
  - 모든 Organization은 Tenant 아래에 동일한 레벨로 존재
  - `parent_organization_id` 컬럼 없음

##### 1.2.2 조직 멤버 관리

**멤버 추가**:
- ✅ **추가 권한**: Super Admin, Organization Admin
  - Organization Admin은 자신이 속한 Organization에만 멤버 추가 가능
- ✅ **한 사용자 = 한 조직**: 사용자는 여러 조직에 소속 불가
  - `users` 테이블: `organization_id` (Nullable, Unique per user)
  - 이미 다른 조직에 속한 사용자 추가 시 예외: `UserAlreadyBelongsToOrganizationException`

**조직 탈퇴**:
- ✅ **탈퇴 처리**: 상태 변경 또는 소프트 삭제
  - Option 1: `user.organizationId = null` (탈퇴 후 재가입 가능)
  - Option 2: `user.status = DELETED` (소프트 삭제)
  - **권장**: Option 1 (상태 변경) - 재가입 유연성

**Zero-Tolerance 규칙 준수**:
- ✅ **Law of Demeter**: `organization.getTenantName()` (O), `organization.getTenant().getName()` (X)
- ✅ **Long FK 전략**: `private Long tenantId`

---

#### 1.3 Aggregate: Tenant

**속성**:
```java
public class Tenant {
    private TenantId tenantId;       // Value Object (Long)
    private TenantName name;         // Value Object
    private TenantStatus status;     // Enum: ACTIVE, INACTIVE, DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**비즈니스 규칙**:

##### 1.3.1 Tenant 구조

**Tenant 개념**:
- ✅ **Tenant = 회사 단위** (예: "Connectly", "Partner Company A")
- ✅ **Tenant와 Organization 관계**: 1 Tenant : N Organization
  - 예: Connectly Tenant → "셀러센터", "관리자팀", "개발팀" Organization
- ✅ **PUBLIC 사용자 Tenant**: "Connectly Public Tenant" (시스템 기본 Tenant)
  - 모든 PUBLIC 사용자는 이 Tenant에 자동 할당
  - PUBLIC 사용자는 Organization 없음 (`organizationId = null`)

##### 1.3.2 Tenant 격리

**데이터 격리**:
- ✅ **Row-level 격리**: 단일 DB이지만 모든 테이블에 `tenant_id` 포함
  - `users`, `organizations`, `roles`, `permissions`, `refresh_tokens` 등
  - 모든 쿼리에 `WHERE tenant_id = ?` 자동 추가 (JPA Filter 또는 QueryDSL)
- ✅ **Tenant 간 데이터 완전 격리**:
  - Tenant A의 사용자는 Tenant B의 데이터 접근 불가
  - Gateway에서 Access Token의 `tenantId` claim으로 검증

**Tenant 이동**:
- ❌ **Tenant 간 이동 불가**: 사용자는 생성 시 Tenant에 고정
  - Tenant 변경 필요 시 새 계정 생성 필요

**Tenant 삭제**:
- ✅ **소프트 삭제**: `deleted_at` 컬럼에 삭제 시각 기록
  - Tenant 삭제 시 모든 하위 Organization, User도 소프트 삭제
  - Refresh Token 즉시 폐기

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: Tenant는 최상위 Aggregate이므로 FK 없음

---

#### 1.4 Aggregate: Role

**속성**:
```java
public class Role {
    private RoleId roleId;              // Value Object (Long)
    private OrganizationId organizationId; // FK (Long, Nullable for SUPER_ADMIN)
    private RoleName name;              // Value Object
    private String description;
    private RoleType roleType;          // Enum: GLOBAL, ORG_LEVEL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**비즈니스 규칙**:

##### 1.4.1 Role 생성

**생성 권한**:
- ✅ **Super Admin**: 전역 Role 생성 가능 (`SUPER_ADMIN` Role)
- ✅ **Organization Admin**: 자신이 속한 Organization의 Role만 생성 가능
  - `roleType = ORG_LEVEL`, `organizationId = {자신의 Organization}`

**Role 범위**:
- ✅ **Organization 단위 Role**: 대부분의 Role은 Organization 단위로 존재
  - 예: "셀러센터 관리자", "셀러센터 멤버"
  - `organizationId != null`
- ✅ **전역 Role (예외)**: `SUPER_ADMIN` Role만 전역
  - `organizationId = null`, `roleType = GLOBAL`
  - Super Admin만 생성/수정 가능

**시스템 기본 Role (⭐ 개선)**:

```java
// 시스템 기본 Role 정의
public enum SystemRole {
    SUPER_ADMIN(
        "전역 관리자",
        RoleType.GLOBAL,
        null,  // organizationId = null (전역)
        List.of("*:*")  // 모든 Permission 자동 허용
    ),

    ORG_ADMIN(
        "조직 관리자",
        RoleType.ORG_LEVEL,
        null,  // organizationId는 동적 할당
        List.of(
            "organization:*",  // 조직 관리 (생성, 수정, 삭제)
            "user:*",          // 사용자 관리 (생성, 조회, 수정, 삭제)
            "role:*",          // 역할 관리 (생성, 부여, 회수)
            "member:*"         // 멤버 관리 (초대, 제거)
        )
    ),

    MEMBER(
        "일반 멤버",
        RoleType.ORG_LEVEL,
        null,  // organizationId는 동적 할당
        List.of(
            "user:read"  // 본인 정보만 조회
        )
    );

    private final String description;
    private final RoleType roleType;
    private final Long organizationId;
    private final List<String> defaultPermissions;
}
```

**Super Admin 권한 정책**:

```java
// SuperAdminPolicy.java
public class SuperAdminPolicy {

    /**
     * Super Admin은 모든 Permission을 자동으로 보유
     * Gateway 권한 검증 시 Super Admin이면 즉시 허용
     */
    public boolean hasPermission(User user, String requiredPermission) {
        if (user.hasRole("SUPER_ADMIN")) {
            return true;  // 모든 Permission 허용
        }
        return false;
    }

    /**
     * Super Admin은 모든 Tenant/Organization에 접근 가능
     */
    public boolean canAccessTenant(User user, Long tenantId) {
        if (user.hasRole("SUPER_ADMIN")) {
            return true;  // 모든 Tenant 접근 허용
        }
        return user.getTenantId().equals(tenantId);
    }

    /**
     * Super Admin은 모든 Organization 관리 가능
     */
    public boolean canManageOrganization(User user, Long organizationId) {
        if (user.hasRole("SUPER_ADMIN")) {
            return true;  // 모든 Organization 관리 허용
        }
        return false;
    }
}
```

**Organization Admin 권한 정책**:

```java
// OrgAdminPolicy.java
public class OrgAdminPolicy {

    /**
     * Org Admin은 자신이 속한 Organization의 모든 리소스에 대한 manage permission 자동 허용
     */
    public boolean hasPermission(User user, String requiredPermission, Long resourceOrganizationId) {
        if (!user.hasRole("ORG_ADMIN")) {
            return false;
        }

        // Org Admin은 자신이 속한 Organization에만 권한 보유
        if (!user.getOrganizationId().equals(resourceOrganizationId)) {
            return false;
        }

        // organization:*, user:*, role:*, member:* 자동 허용
        String resource = requiredPermission.split(":")[0];
        return List.of("organization", "user", "role", "member").contains(resource);
    }

    /**
     * Org Admin은 자신이 속한 Organization의 멤버만 관리 가능
     */
    public boolean canManageUser(User admin, User targetUser) {
        if (!admin.hasRole("ORG_ADMIN")) {
            return false;
        }

        // 동일 Organization의 사용자만 관리 가능
        return admin.getOrganizationId().equals(targetUser.getOrganizationId());
    }

    /**
     * Org Admin은 자신이 속한 Organization의 Role만 생성/부여 가능
     */
    public boolean canManageRole(User admin, Role role) {
        if (!admin.hasRole("ORG_ADMIN")) {
            return false;
        }

        // 동일 Organization의 Role만 관리 가능
        return admin.getOrganizationId().equals(role.getOrganizationId());
    }
}
```

**Gateway 권한 검증 시 Policy 적용**:

```java
// Gateway: PermissionValidator.java
public boolean validatePermission(AccessToken token, String endpoint) {
    String requiredPermission = getRequiredPermission(endpoint);  // "order:read"

    // 1. Super Admin 체크 (즉시 허용)
    if (token.hasRole("SUPER_ADMIN")) {
        return true;  // Super Admin은 모든 Permission 허용
    }

    // 2. Org Admin 체크 (Organization Scoped)
    if (token.hasRole("ORG_ADMIN")) {
        // Org Admin은 organization:*, user:*, role:*, member:* 자동 허용
        String resource = requiredPermission.split(":")[0];
        if (List.of("organization", "user", "role", "member").contains(resource)) {
            // Organization Scope 검증 (요청된 리소스가 Org Admin의 Organization에 속하는지)
            Long resourceOrgId = extractOrganizationId(endpoint);  // URL에서 추출
            return token.getOrgIds().contains(resourceOrgId);
        }
    }

    // 3. 일반 Permission 체크
    return token.getPermissions().contains(requiredPermission);
}
```

**Org Admin 생성 프로세스**:

```java
// Option 1: 조직 생성 시 첫 번째 사용자를 Org Admin으로 자동 생성
@Transactional
public OrganizationResponse createOrganization(CreateOrganizationCommand command) {
    // 1. Organization 생성
    Organization org = Organization.create(
        command.organizationName(),
        command.description(),
        command.tenantId()
    );
    saveOrganization(org);

    // 2. 첫 번째 사용자 생성 (Org Admin)
    User adminUser = User.create(
        userType = UserType.INTERNAL,
        email = command.adminEmail(),
        name = command.adminName(),
        tenantId = command.tenantId(),
        organizationId = org.getOrganizationId()
    );
    saveUser(adminUser);

    // 3. ORG_ADMIN Role 자동 생성 및 부여
    Role orgAdminRole = Role.createSystemRole(
        SystemRole.ORG_ADMIN,
        org.getOrganizationId()
    );
    saveRole(orgAdminRole);

    adminUser.assignRole(orgAdminRole);
    saveUser(adminUser);

    return OrganizationResponse.from(org, adminUser);
}

// Option 2: 기존 사용자에게 Org Admin Role 부여
@Transactional
public UserResponse assignOrgAdminRole(Long userId, Long organizationId) {
    // 권한 확인: Super Admin만 Org Admin Role 부여 가능
    User currentUser = getCurrentUser();
    if (!currentUser.hasRole("SUPER_ADMIN")) {
        throw new UnauthorizedException("Super Admin만 Org Admin Role을 부여할 수 있습니다.");
    }

    User user = loadUser(userId);

    // ORG_ADMIN Role 조회 또는 생성
    Role orgAdminRole = loadOrCreateOrgAdminRole(organizationId);

    user.assignRole(orgAdminRole);
    saveUser(user);

    // Refresh Token 폐기 (즉시 재로그인 요구)
    deleteRefreshTokensByUserId(userId);

    return UserResponse.from(user);
}
```

##### 1.4.2 Role 할당

**할당 규칙**:
- ✅ **여러 Role 보유 가능**: 한 사용자가 여러 Role 보유 가능
  - 예: 사용자 A는 "셀러센터 관리자" + "개발팀 멤버" Role 보유
- ✅ **조직마다 다른 Role**: 사용자는 조직마다 다른 Role 보유 가능
  - 단, 현재는 **한 사용자 = 한 조직**이므로 실질적으로 한 조직의 Role만 보유

**Role 변경 시 처리**:
- ✅ **즉시 무효화**: Role 변경 시 기존 Refresh Token 즉시 폐기
  - 사용자는 재로그인해야 새 Role 반영된 Access Token 획득
- ✅ **Access Token**: 기존 Access Token은 만료 시까지 유효 (최대 30분)
  - 긴급 권한 회수 시 Access Token Blacklist 사용 (선택 사항)

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long organizationId`

---

#### 1.5 Aggregate: Permission

**속성**:
```java
public class Permission {
    private PermissionId permissionId;   // Value Object (Long)
    private ServiceName serviceName;     // Value Object (예: "commerce", "fileflow")
    private Resource resource;           // Value Object (예: "order", "user")
    private Action action;               // Value Object (예: "create", "read", "update", "delete")
    private String description;
    private String endpoint;             // API Endpoint (예: "GET /api/v1/orders/{orderId}")
    private LocalDateTime createdAt;
}
```

**비즈니스 규칙**:

##### 1.5.1 Permission 구조

**Permission 형식**:
- ✅ **형식**: `"resource:action"`
  - 예: `"order:create"`, `"user:read"`, `"product:update"`
- ✅ **서비스별 Permission**: 각 서비스(Commerce, FileFlow 등)에서 **Annotation 기반으로 자동 스캔**
  - `@RequiresPermission` Annotation으로 선언
  - Spring Boot Actuator Endpoint로 노출
  - AuthHub가 주기적으로 Pull 또는 서비스가 Push

**Permission 자동 스캔 (⭐ 개선)**:

**Step 1: 서비스에서 Annotation으로 선언**
```java
// Commerce Service
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping("/{orderId}")
    @RequiresPermission("order:read")  // ← 자동 스캔
    public OrderResponse getOrder(@PathVariable Long orderId) {
        // ...
    }

    @PostMapping
    @RequiresPermission("order:create")  // ← 자동 스캔
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        // ...
    }

    @DeleteMapping("/{orderId}")
    @RequiresPermission("order:delete")
    public void deleteOrder(@PathVariable Long orderId) {
        // ...
    }
}
```

**Step 2: Spring Boot Actuator Endpoint 노출**
```java
// PermissionScannerEndpoint.java
@Endpoint(id = "permissions")
@Component
public class PermissionScannerEndpoint {

    @ReadOperation
    public PermissionMetadata getPermissions() {
        // 모든 @RequiresPermission 어노테이션 스캔
        List<PermissionInfo> permissions = scanPermissions();

        return PermissionMetadata.builder()
            .serviceName("commerce")
            .version("1.0.0")
            .endpoints(permissions)
            .build();
    }

    private List<PermissionInfo> scanPermissions() {
        // Spring ApplicationContext에서 모든 Controller 스캔
        // @RequiresPermission 어노테이션이 있는 메서드 추출
        // Endpoint 정보 (Method, Path, Permission) 수집
    }
}

// GET /actuator/permissions 응답 예시
{
  "serviceName": "commerce",
  "version": "1.0.0",
  "endpoints": [
    {
      "method": "GET",
      "path": "/api/v1/orders/{orderId}",
      "permission": "order:read",
      "description": "주문 조회"
    },
    {
      "method": "POST",
      "path": "/api/v1/orders",
      "permission": "order:create",
      "description": "주문 생성"
    },
    {
      "method": "DELETE",
      "path": "/api/v1/orders/{orderId}",
      "permission": "order:delete",
      "description": "주문 삭제"
    }
  ]
}
```

**Step 3: AuthHub가 Permission 자동 수집**

**Option 1: AuthHub가 주기적으로 Pull**
```java
// AuthHub: PermissionSyncScheduler.java
@Scheduled(fixedRate = 300000)  // 5분마다
public void syncPermissions() {
    List<Service> services = loadAllServices();  // Commerce, FileFlow 등

    for (Service service : services) {
        // 각 서비스의 /actuator/permissions 호출
        String url = service.getBaseUrl() + "/actuator/permissions";
        PermissionMetadata metadata = restTemplate.getForObject(url, PermissionMetadata.class);

        // AuthHub에 Permission 등록/업데이트
        syncPermissionsToAuthHub(metadata);
    }
}
```

**Option 2: 서비스가 AuthHub로 Push (CI/CD 통합)**
```yaml
# .gitlab-ci.yml (서비스 배포 시)
deploy:
  script:
    - docker build -t commerce:latest .
    - docker push commerce:latest
    - kubectl apply -f k8s/deployment.yaml
    # ↓ Permission 자동 등록 (Webhook)
    - curl -X POST https://authhub.com/api/v1/permissions/sync \
           -H "Authorization: Bearer $SERVICE_API_KEY" \
           -d @permissions.json
```

**Permission Drift 방지**:
- ✅ **서비스 경로 변경 시 자동 반영**: Annotation 기반이므로 코드와 Permission이 항상 동기화
- ✅ **Permission 누락 방지**: ArchUnit 테스트로 모든 API가 `@RequiresPermission`을 가졌는지 검증
- ✅ **Gateway 자동 업데이트**: Gateway는 AuthHub API에서 Permission 맵핑 Pull

**ArchUnit 테스트로 Permission 누락 방지**:
```java
// PermissionArchTest.java
@ArchTest
public static final ArchRule all_public_api_methods_should_have_requires_permission =
    methods()
        .that().arePublic()
        .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
        .and().areAnnotatedWith(GetMapping.class)
            .or().areAnnotatedWith(PostMapping.class)
            .or().areAnnotatedWith(PutMapping.class)
            .or().areAnnotatedWith(DeleteMapping.class)
        .should().beAnnotatedWith(RequiresPermission.class)
        .because("모든 API 메서드는 @RequiresPermission을 가져야 합니다");
```

**Permission 등록 API**:
- ✅ **AuthHub 등록**: 서비스 시작 시 또는 CI/CD에서 AuthHub API 호출
  - API: `POST /api/v1/permissions/bulk-register`
  - Request: `{ serviceName, endpoints }`
- ✅ **중복 방지**: `UNIQUE(service_name, method, path)`
- ✅ **버전 관리**: Permission 변경 이력 관리 (`permission_history` 테이블)

##### 1.5.2 Permission 할당

**할당 방식**:
- ✅ **Role에만 할당**: Permission은 Role에만 매핑 (사용자 직접 부여 없음)
  - `role_permissions` 테이블: `(role_id, permission_id)` Many-to-Many
- ❌ **사용자 직접 부여 불가**: 사용자는 Role을 통해서만 Permission 획득

**Permission 변경 시 처리**:
- ✅ **5분 캐시**: Gateway는 Permission 정보를 5분 캐싱
  - Permission 변경 후 최대 5분 지연 발생 가능
  - 긴급 변경 시 Gateway 캐시 수동 초기화 (Admin API)

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: Permission Aggregate 자체는 FK 없음

---

#### 1.6 Aggregate: AuthToken

**속성**:
```java
public class AuthToken {
    private AccessToken accessToken;    // Value Object (JWT)
    private RefreshToken refreshToken;  // Value Object
    private UserId userId;              // FK (Long)
    private TenantId tenantId;          // FK (Long)
    private LocalDateTime expiresAt;    // Refresh Token 만료 시각
    private LocalDateTime createdAt;
}
```

**비즈니스 규칙**:

##### 1.6.1 Access Token

**발급**:
- ✅ **만료 시간**: 30분
- ✅ **서명 알고리즘**: RS256 (비대칭 키)
  - Private Key: AuthHub만 보유 (토큰 발급)
  - Public Key: Gateway 배포 (토큰 검증)
- ✅ **Payload**:
  ```json
  {
    "sub": "userId",
    "tenant_id": "tenantId",
    "user_type": "PUBLIC | INTERNAL",
    "org_ids": [organizationId],  // INTERNAL 사용자만
    "roles": ["ROLE_ORG_ADMIN", "ROLE_MEMBER"],
    "permissions": ["order:create", "user:read"],
    "iat": 1705135200,
    "exp": 1705137000
  }
  ```

**검증**:
- ✅ **Gateway 자체 검증**: Public Key로 서명 검증
  - AuthHub API 호출 없이 Gateway에서 독립적으로 검증
- ✅ **검증 실패 시**: HTTP 401 Unauthorized 반환
- ✅ **검증 결과 캐싱**: 1-3분 캐싱 (성능 최적화)

##### 1.6.2 Refresh Token

**발급**:
- ✅ **만료 시간**: 14일
- ✅ **저장 위치**: MySQL (`refresh_tokens` 테이블) + Redis 캐시
  - MySQL: 영구 저장 (Audit Log, 탈취 감지)
  - Redis: 빠른 검증 (TTL 14일)
- ✅ **Rotation 적용**: 토큰 재발급 시 기존 Refresh Token 무효화
  - 새 Refresh Token 발급 → 기존 Refresh Token 삭제
  - 재사용 감지 시 모든 Refresh Token 폐기 (탈취 의심)

**재발급**:
- ✅ **Gateway 자동 재발급**: Access Token 만료 시 Gateway가 자동으로 재발급
  - 클라이언트는 Refresh Token을 Authorization 헤더에 포함
  - Gateway는 AuthHub API 호출하여 새 Access Token 발급
  - 새 Access Token을 응답 헤더 (`X-New-Access-Token`)에 포함

**무효화**:
- ✅ **로그아웃 시**: Refresh Token 폐기 (MySQL + Redis에서 삭제)
- ✅ **Role 변경 시**: 해당 사용자의 모든 Refresh Token 폐기 → 즉시 재로그인 요구
- ✅ **사용자 상태 변경 시** (SUSPENDED, DELETED): 모든 Refresh Token 폐기

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long userId`, `private Long tenantId`

---

#### 1.7 Aggregate: SocialAccount

**속성**:
```java
public class SocialAccount {
    private SocialAccountId socialAccountId; // Value Object (Long)
    private UserId userId;                   // FK (Long)
    private SocialProvider provider;         // Enum: KAKAO
    private String providerUserId;           // Kakao User ID
    private String accessToken;              // Kakao Access Token (암호화 저장)
    private String refreshToken;             // Kakao Refresh Token (암호화 저장)
    private LocalDateTime tokenExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**비즈니스 규칙**:

##### 1.7.1 소셜 계정 연동

**지원 플랫폼**:
- ✅ **Kakao만 지원** (초기 버전)
  - 향후 확장: Naver, Google, Apple 등

**연동 프로세스**:
1. 사용자가 Kakao 로그인 버튼 클릭
2. Kakao OAuth 플로우 진행 → Authorization Code 획득
3. AuthHub가 Kakao API 호출하여 Access Token 획득
4. Kakao User Info API로 전화번호 획득
5. 기존 계정 존재 여부 확인:
   - 존재 → 계정 통합 (SocialAccount 연동)
   - 존재하지 않음 → 새 계정 생성 + SocialAccount 연동

**연동 해제**:
- ✅ **연동 해제 가능**: 사용자가 소셜 계정 연동 해제 가능
  - SocialAccount 삭제 (소프트 삭제)
  - 전화번호 로그인 다시 활성화 (Credential.enablePhoneLogin())

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long userId`

---

#### 1.8 Aggregate: AuditLog (⭐ 신규 추가)

**속성**:
```java
public class AuditLog {
    private AuditLogId auditLogId;       // Value Object (Long)
    private AuditEventType eventType;    // Enum (아래 참조)
    private UserId userId;               // FK (Long, Nullable for system events)
    private TenantId tenantId;           // FK (Long)
    private String ipAddress;            // 요청 IP
    private String userAgent;            // User-Agent
    private ResourceType resourceType;   // Enum: USER, ORGANIZATION, ROLE, TOKEN 등
    private String resourceId;           // 리소스 ID
    private ActionType action;           // Enum: CREATE, UPDATE, DELETE, LOGIN 등
    private String details;              // JSON (변경 전후 데이터)
    private Boolean success;             // 성공/실패
    private String errorMessage;         // 실패 시 에러 메시지
    private LocalDateTime createdAt;
}
```

**비즈니스 규칙**:

##### 1.8.1 Audit Event Type

```java
public enum AuditEventType {
    // 인증 이벤트
    LOGIN_SUCCESS("로그인 성공"),
    LOGIN_FAILURE("로그인 실패"),
    LOGOUT("로그아웃"),

    // 토큰 이벤트
    TOKEN_ISSUED("토큰 발급"),
    TOKEN_REFRESHED("토큰 재발급"),
    TOKEN_REVOKED("토큰 무효화"),

    // 사용자 관리
    USER_CREATED("사용자 생성"),
    USER_UPDATED("사용자 수정"),
    USER_DELETED("사용자 삭제"),
    USER_STATUS_CHANGED("사용자 상태 변경"),

    // 역할/권한
    ROLE_ASSIGNED("역할 부여"),
    ROLE_REVOKED("역할 회수"),
    PERMISSION_CHANGED("권한 변경"),

    // 조직 관리
    ORGANIZATION_CREATED("조직 생성"),
    ORGANIZATION_UPDATED("조직 수정"),
    ORGANIZATION_DELETED("조직 삭제"),
    ORGANIZATION_MEMBER_ADDED("조직 멤버 추가"),
    ORGANIZATION_MEMBER_REMOVED("조직 멤버 제거"),

    // 소셜 계정
    SOCIAL_ACCOUNT_LINKED("소셜 계정 연동"),
    SOCIAL_ACCOUNT_UNLINKED("소셜 계정 연동 해제"),

    // 보안 이벤트
    ACCOUNT_LOCKED("계정 잠금"),
    ACCOUNT_UNLOCKED("계정 잠금 해제"),
    PASSWORD_CHANGED("비밀번호 변경"),
    SUSPICIOUS_LOGIN_ATTEMPT("의심스러운 로그인 시도"),
    RATE_LIMIT_EXCEEDED("Rate Limit 초과"),

    // 시스템 이벤트
    PERMISSION_SYNCED("Permission 동기화"),
    TENANT_CREATED("Tenant 생성"),
    TENANT_DELETED("Tenant 삭제");

    private final String description;
}
```

##### 1.8.2 Audit Log 정책 (IAM 필수 요구사항)

**기본 원칙**:
IAM 시스템은 **감사 로그(Audit Log)**가 필수입니다. 모든 인증, 인가, 권한 변경 이벤트는 반드시 기록되어야 하며, 보안 감사(Security Audit) 및 규정 준수(Compliance)를 위해 보관되어야 합니다.

**필수 기록 이벤트**:

| 카테고리 | 이벤트 | Event Type | 기록 시점 | 보관 기간 |
|---------|--------|-----------|----------|----------|
| **인증** | 로그인 성공 | `LOGIN_SUCCESS` | 토큰 발급 직후 | 90일 (Hot), 1년 (Cold) |
| **인증** | 로그인 실패 | `LOGIN_FAILURE` | 인증 실패 시 | 90일 (Hot), 1년 (Cold) |
| **토큰** | Refresh Token 재발급 시도 | `TOKEN_REFRESHED` | 재발급 요청 시 | 90일 (Hot), 1년 (Cold) |
| **토큰** | Refresh Token Rotation 실패 (Reuse 감지) | `TOKEN_REVOKED` + `SUSPICIOUS_LOGIN_ATTEMPT` | Reuse 감지 시 | 1년 (Hot), 7년 (Archive) |
| **권한** | Role 변경 | `ROLE_ASSIGNED` / `ROLE_REVOKED` | Role 부여/회수 시 | 1년 (Hot), 7년 (Archive) |
| **권한** | Permission 변경 | `PERMISSION_CHANGED` | Permission 수정 시 | 1년 (Hot), 7년 (Archive) |
| **계정** | Status 변경 | `USER_STATUS_CHANGED` | INACTIVE → ACTIVE 등 상태 전환 시 | 1년 (Hot), 7년 (Archive) |
| **보안** | 계정 잠금 | `ACCOUNT_LOCKED` | 로그인 실패 5회 초과 시 | 1년 (Hot), 7년 (Archive) |
| **보안** | Rate Limit 초과 | `RATE_LIMIT_EXCEEDED` | Rate Limit 초과 시 | 90일 (Hot) |

**보관 정책**:
- **Hot Storage (90일)**: MySQL 메인 테이블, 실시간 조회 가능
- **Cold Storage (1년)**: Archive 테이블 또는 S3, 조회 속도 느림
- **Archive (7년)**: 규정 준수용, S3 Glacier 또는 별도 Archive DB

**보안 이벤트 우선순위**:
- 🔴 **Critical**: `SUSPICIOUS_LOGIN_ATTEMPT`, `REFRESH_TOKEN_REUSE_DETECTED` → 즉시 알림
- 🟡 **Warning**: `ACCOUNT_LOCKED`, `RATE_LIMIT_EXCEEDED` → 일일 리포트
- 🟢 **Info**: `LOGIN_SUCCESS`, `TOKEN_REFRESHED` → 정기 감사

**AOP 기반 자동 Audit Log 기록**:

```java
// @Auditable Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    AuditEventType eventType();
    String resourceType() default "";
    String action() default "";
}

// AuditLogAspect.java
@Aspect
@Component
public class AuditLogAspect {

    private final SaveAuditLogPort saveAuditLogPort;

    @Around("@annotation(auditable)")
    public Object logAuditEvent(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        // Audit Log 생성
        AuditLog auditLog = AuditLog.builder()
            .eventType(auditable.eventType())
            .userId(getCurrentUserId())
            .tenantId(getCurrentTenantId())
            .ipAddress(getClientIpAddress())
            .userAgent(getUserAgent())
            .resourceType(auditable.resourceType())
            .action(auditable.action())
            .createdAt(LocalDateTime.now())
            .build();

        try {
            // UseCase 실행
            Object result = joinPoint.proceed();

            // 성공 처리
            auditLog.setSuccess(true);
            auditLog.setDetails(toJson(result));  // 결과 데이터 JSON 저장

            return result;
        } catch (Exception e) {
            // 실패 처리
            auditLog.setSuccess(false);
            auditLog.setErrorMessage(e.getMessage());

            throw e;
        } finally {
            // Audit Log 저장 (비동기)
            saveAuditLogPort.save(auditLog);
        }
    }
}
```

**UseCase에 @Auditable 적용 (필수 이벤트)**:

```java
// 1. 로그인 성공
@Auditable(
    eventType = AuditEventType.LOGIN_SUCCESS,
    resourceType = "USER",
    action = "LOGIN"
)
public AuthTokenResponse loginWithPhone(LoginWithPhoneCommand command) {
    // AOP가 자동으로 IP, UserAgent, Timestamp 기록
    // details: {"phoneNumber": "010-1234-5678", "accessToken": "...", "refreshToken": "..."}
}

// 2. 로그인 실패 (Aspect에서 Exception 감지 시 자동 기록)
@Auditable(
    eventType = AuditEventType.LOGIN_FAILURE,
    resourceType = "USER",
    action = "LOGIN"
)
public AuthTokenResponse loginWithEmail(LoginWithEmailCommand command) {
    // 비밀번호 불일치 → InvalidPasswordException → success=false, errorMessage 기록
    // details: {"email": "user@example.com", "reason": "INVALID_PASSWORD"}
}

// 3. Refresh Token 재발급 시도
@Auditable(
    eventType = AuditEventType.TOKEN_REFRESHED,
    resourceType = "TOKEN",
    action = "REFRESH"
)
public AuthTokenResponse refreshAccessToken(RefreshAccessTokenCommand command) {
    // Refresh Token Rotation 수행
    // success=true: 재발급 성공, details: {"oldToken": "...", "newToken": "..."}
    // success=false: Reuse 감지, errorMessage: "REFRESH_TOKEN_REUSE_DETECTED"
}

// 4. Refresh Token Rotation 실패 (Reuse 감지)
// → AOP Aspect가 자동으로 SUSPICIOUS_LOGIN_ATTEMPT 추가 기록
@AfterThrowing(
    pointcut = "@annotation(auditable)",
    throwing = "ex"
)
public void logRefreshTokenReuse(JoinPoint joinPoint, RefreshTokenReuseDetectedException ex) {
    AuditLog securityLog = AuditLog.builder()
        .eventType(AuditEventType.SUSPICIOUS_LOGIN_ATTEMPT)
        .userId(ex.getUserId())
        .ipAddress(getClientIpAddress())
        .action(ActionType.SECURITY_ALERT)
        .success(false)
        .errorMessage("Refresh Token reuse detected. All tokens revoked.")
        .details(toJson(Map.of(
            "revokedTokenCount", ex.getRevokedTokenCount(),
            "suspiciousRefreshToken", ex.getRefreshToken()
        )))
        .build();

    saveAuditLogPort.save(securityLog);
}

// 5. Role 변경 (부여)
@Auditable(
    eventType = AuditEventType.ROLE_ASSIGNED,
    resourceType = "USER",
    action = "UPDATE"
)
public UserResponse assignRole(AssignRoleCommand command) {
    // details: {"userId": 123, "roleId": 456, "roleName": "ORG_ADMIN", "assignedBy": "SuperAdmin"}
}

// 6. Role 변경 (회수)
@Auditable(
    eventType = AuditEventType.ROLE_REVOKED,
    resourceType = "USER",
    action = "UPDATE"
)
public UserResponse revokeRole(RevokeRoleCommand command) {
    // details: {"userId": 123, "roleId": 456, "roleName": "ORG_ADMIN", "revokedBy": "SuperAdmin"}
}

// 7. Status 변경 (INACTIVE → ACTIVE)
@Auditable(
    eventType = AuditEventType.USER_STATUS_CHANGED,
    resourceType = "USER",
    action = "UPDATE"
)
public UserResponse changeUserStatus(ChangeUserStatusCommand command) {
    // details: {"userId": 123, "oldStatus": "INACTIVE", "newStatus": "ACTIVE", "changedBy": "OrgAdmin"}
}
```

##### 1.8.3 Audit Log 저장 전략

**비동기 저장 (성능 최적화)**:

```java
// SaveAuditLogPort.java (Application Layer)
public interface SaveAuditLogPort {
    void save(AuditLog auditLog);
}

// AuditLogPersistenceAdapter.java (Persistence Layer)
@Component
public class AuditLogPersistenceAdapter implements SaveAuditLogPort {

    @Async("auditLogExecutor")
    @Override
    public void save(AuditLog auditLog) {
        // MySQL에 저장
        AuditLogJpaEntity entity = AuditLogJpaMapper.toEntity(auditLog);
        auditLogJpaRepository.save(entity);

        // 선택: Elasticsearch에도 저장 (검색 최적화)
        if (elasticsearchEnabled) {
            auditLogElasticsearchRepository.save(entity);
        }
    }
}

// Async Executor 설정
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "auditLogExecutor")
    public Executor auditLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("audit-log-");
        executor.initialize();
        return executor;
    }
}
```

##### 1.8.4 Audit Log 조회

**Query UseCase**:

```java
// ListAuditLogsUseCase.java
public PageResponse<AuditLogResponse> listAuditLogs(ListAuditLogsQuery query) {
    // 필터링: userId, eventType, dateRange, success 등
    Page<AuditLog> auditLogs = loadAuditLogsPort.findByFilters(
        query.userId(),
        query.eventType(),
        query.startDate(),
        query.endDate(),
        query.success(),
        query.pageable()
    );

    return PageResponse.from(auditLogs, AuditLogResponse::from);
}

// GetAuditLogDetailUseCase.java
public AuditLogDetailResponse getAuditLogDetail(Long auditLogId) {
    AuditLog auditLog = loadAuditLogPort.findById(auditLogId)
        .orElseThrow(() -> new AuditLogNotFoundException(auditLogId));

    return AuditLogDetailResponse.from(auditLog);
}
```

##### 1.8.5 별도 Audit DB (선택 사항)

**대규모 환경 (10억+ 레코드)**:

```yaml
# Option 1: 별도 MySQL DB
audit_db:
  host: audit-mysql.internal
  database: authhub_audit
  partitioning: RANGE BY MONTH (created_at)
  retention: 2 years

# Option 2: Time-Series DB (TimescaleDB)
audit_db:
  type: timescaledb
  hypertable: audit_logs
  chunk_interval: 1 month
  retention: 2 years

# Option 3: NoSQL (Elasticsearch)
audit_db:
  type: elasticsearch
  index: authhub-audit-logs-{YYYY-MM}
  retention: 2 years
  replicas: 2
```

**데이터 보관 정책**:
- ✅ **Hot Storage** (최근 3개월): MySQL + Redis 캐시
- ✅ **Warm Storage** (3-12개월): MySQL (파티셔닝)
- ✅ **Cold Storage** (1-2년): S3 + Parquet 압축
- ✅ **삭제**: 2년 경과 시 자동 삭제 (Batch Job)

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long userId`, `private Long tenantId`
- ✅ **Lombok 금지**: Pure Java 또는 Record 사용

---

### 2. Application Layer

#### 2.1 Command UseCase

##### 2.1.1 RegisterUserUseCase (PUBLIC 사용자 가입)

**Input**:
```java
public record RegisterUserCommand(
    String phoneNumber,      // 전화번호 (국제 형식: +82-10-1234-5678)
    String password,         // 비밀번호 (해시 처리)
    String name,             // 사용자 이름
    String profileImageUrl   // 프로필 이미지 (선택)
) {}
```

**Output**:
```java
public record UserResponse(
    Long userId,
    String userType,         // "PUBLIC"
    String phoneNumber,
    String name,
    String status,           // "ACTIVE"
    LocalDateTime createdAt
) {}
```

**Transaction**: Yes (사용자 생성 + Credential 생성)

**비즈니스 로직**:
1. 전화번호 중복 확인 (CheckDuplicatePhoneNumberPort)
   - 중복 시 예외: `DuplicatePhoneNumberException`
2. 비밀번호 해시 처리 (BCrypt)
3. User Aggregate 생성:
   - `userType = PUBLIC`
   - `tenantId = "Connectly Public Tenant"`
   - `organizationId = null`
   - `status = ACTIVE`
4. Credential 생성 (Credential.ofPhone(phoneNumber, hashedPassword))
5. User 저장 (SaveUserPort)
6. **트랜잭션 커밋**

**Zero-Tolerance 규칙 준수**:
- ✅ **Transaction 경계**: 외부 API 호출 없음 (DB 저장만)

---

##### 2.1.2 LoginWithPhoneUseCase (전화번호 로그인)

**Input**:
```java
public record LoginWithPhoneCommand(
    String phoneNumber,
    String password
) {}
```

**Output**:
```java
public record AuthTokenResponse(
    String accessToken,
    String refreshToken,
    LocalDateTime expiresAt
) {}
```

**Transaction**: Yes (Refresh Token 저장)

**비즈니스 로직**:
1. 전화번호로 사용자 조회 (LoadUserByPhonePort)
   - 존재하지 않으면 예외: `UserNotFoundException`
2. 사용자 상태 확인:
   - INACTIVE → `UserInactiveException`
   - SUSPENDED → `UserSuspendedException`
   - DELETED → `UserNotFoundException`
3. 비밀번호 검증 (BCrypt.matches)
   - 실패 시 예외: `InvalidPasswordException`
4. Access Token 생성 (GenerateAccessTokenPort):
   - Payload: userId, tenantId, userType, roles, permissions
   - 만료: 30분
5. Refresh Token 생성 (GenerateRefreshTokenPort):
   - UUID 생성
   - 만료: 14일
6. Refresh Token 저장 (SaveRefreshTokenPort):
   - MySQL: `refresh_tokens` 테이블
   - Redis: TTL 14일
7. **트랜잭션 커밋**
8. AuthTokenResponse 반환

**Zero-Tolerance 규칙 준수**:
- ✅ **Transaction 경계**: 외부 API 호출 없음 (DB 저장만)

---

##### 2.1.3 LoginWithKakaoUseCase (Kakao 로그인)

**Input**:
```java
public record LoginWithKakaoCommand(
    String authorizationCode  // Kakao OAuth Authorization Code
) {}
```

**Output**:
```java
public record AuthTokenResponse(
    String accessToken,
    String refreshToken,
    LocalDateTime expiresAt
) {}
```

**Transaction**: Yes (사용자 생성/업데이트 + SocialAccount 저장 + Refresh Token 저장)

**비즈니스 로직**:
1. **트랜잭션 밖**: Kakao API 호출하여 Access Token 획득 (FetchKakaoAccessTokenPort)
   - ⚠️ **외부 API 호출은 트랜잭션 밖**
   - Timeout: 5초
   - 실패 시 예외: `KakaoAuthFailedException`
2. **트랜잭션 밖**: Kakao User Info API 호출 (FetchKakaoUserInfoPort)
   - 전화번호 획득
   - Timeout: 5초
3. **트랜잭션 시작**:
   - 전화번호로 기존 사용자 조회 (LoadUserByPhonePort)
   - 존재하면:
     - 계정 통합: SocialAccount 생성 및 연동
     - Credential.disablePhoneLogin() (전화번호 로그인 비활성화)
   - 존재하지 않으면:
     - 새 User 생성 (userType=PUBLIC, credential=SOCIAL)
     - SocialAccount 생성 및 연동
4. Access Token 생성
5. Refresh Token 생성 및 저장
6. **트랜잭션 커밋**

**Zero-Tolerance 규칙 준수**:
- ✅ **Transaction 경계 엄격 관리**: Kakao API 호출은 트랜잭션 밖

---

##### 2.1.4 RefreshAccessTokenUseCase (Access Token 재발급)

**Input**:
```java
public record RefreshAccessTokenCommand(
    String refreshToken
) {}
```

**Output**:
```java
public record AuthTokenResponse(
    String accessToken,
    String refreshToken,  // 새로운 Refresh Token (Rotation)
    LocalDateTime expiresAt
) {}
```

**Transaction**: Yes (Refresh Token Rotation)

**비즈니스 로직**:
1. Refresh Token 검증 (LoadRefreshTokenPort):
   - Redis 캐시에서 조회 (빠른 검증)
   - 없으면 MySQL에서 조회
   - 존재하지 않으면 예외: `InvalidRefreshTokenException`
2. Refresh Token 만료 확인:
   - 만료되었으면 예외: `RefreshTokenExpiredException`
3. 사용자 조회 (LoadUserPort):
   - 사용자 상태 확인 (ACTIVE만 허용)
4. 새 Access Token 생성
5. **Refresh Token Rotation**:
   - 새 Refresh Token 생성
   - 기존 Refresh Token 삭제 (MySQL + Redis)
   - 새 Refresh Token 저장
6. **트랜잭션 커밋**

**Refresh Token Reuse 감지**:
- ✅ **재사용 감지**: 이미 삭제된 Refresh Token으로 재발급 시도 시
  - 해당 사용자의 모든 Refresh Token 폐기
  - 예외: `RefreshTokenReuseDetectedException`
  - 사용자에게 재로그인 요구

**Zero-Tolerance 규칙 준수**:
- ✅ **Transaction 경계**: 외부 API 호출 없음 (DB만)

**Gateway Token Refresh 상세 Flow**:

AuthHub와 Gateway 간 토큰 재발급은 두 가지 옵션이 있습니다:

**Option 1: Gateway 자동 재발급 (권장)**

```java
// Gateway에서 Access Token 만료 시 자동 재발급
@Component
public class TokenRefreshFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
            .onErrorResume(JwtExpiredException.class, ex -> {
                // 1. Refresh Token 추출 (Cookie)
                String refreshToken = extractRefreshToken(exchange);
                if (refreshToken == null) {
                    return Mono.error(new UnauthorizedException("REFRESH_TOKEN_MISSING"));
                }

                // 2. AuthHub에 재발급 요청
                return webClient.post()
                    .uri("http://authhub/api/v1/auth/refresh")
                    .bodyValue(new RefreshTokenRequest(refreshToken))
                    .retrieve()
                    .bodyToMono(AuthTokenResponse.class)
                    .flatMap(response -> {
                        // 3. 새 Access Token을 Response Header에 담기
                        exchange.getResponse().getHeaders()
                            .add("X-New-Access-Token", response.accessToken());

                        // 4. 새 Refresh Token을 Cookie에 담기
                        exchange.getResponse().addCookie(
                            ResponseCookie.from("refreshToken", response.refreshToken())
                                .httpOnly(true)
                                .secure(true)
                                .maxAge(Duration.ofDays(14))
                                .build()
                        );

                        // 5. 원래 요청 재시도 (새 Access Token 사용)
                        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header("Authorization", "Bearer " + response.accessToken())
                            .build();

                        return chain.filter(exchange.mutate().request(newRequest).build());
                    })
                    .onErrorResume(error -> {
                        // 재발급 실패 시 에러 코드 반환
                        if (error instanceof RefreshTokenExpiredException) {
                            return Mono.error(new UnauthorizedException("REFRESH_TOKEN_EXPIRED"));
                        }
                        if (error instanceof RefreshTokenReuseDetectedException) {
                            return Mono.error(new UnauthorizedException("CONCURRENT_REFRESH_DETECTED"));
                        }
                        return Mono.error(new UnauthorizedException("TOKEN_REFRESH_FAILED"));
                    });
            });
    }
}
```

**Race Condition 방지 전략** (동시 재발급 요청):

```java
// AuthHub의 RefreshAccessTokenUseCase에 Redis 분산 락 추가
@Transactional
public AuthTokenResponse execute(RefreshAccessTokenCommand command) {
    String lockKey = "token:refresh:" + command.refreshToken();
    RLock lock = redissonClient.getLock(lockKey);

    try {
        // 1. 분산 락 획득 (최대 3초 대기, 5초 후 자동 해제)
        boolean acquired = lock.tryLock(3, 5, TimeUnit.SECONDS);
        if (!acquired) {
            throw new ConcurrentRefreshException("CONCURRENT_REFRESH_DETECTED");
        }

        // 2. Refresh Token 검증 및 재발급
        RefreshToken refreshToken = loadRefreshTokenPort.load(command.refreshToken())
            .orElseThrow(() -> new InvalidRefreshTokenException("REFRESH_TOKEN_NOT_FOUND"));

        if (refreshToken.isExpired()) {
            throw new RefreshTokenExpiredException("REFRESH_TOKEN_EXPIRED");
        }

        // 3. 새 Token 생성 및 Rotation
        User user = loadUserPort.load(refreshToken.getUserId());
        AccessToken newAccessToken = generateAccessToken(user);
        RefreshToken newRefreshToken = generateRefreshToken(user);

        // 4. 기존 Refresh Token 폐기 (MySQL + Redis)
        deleteRefreshTokenPort.delete(refreshToken);

        // 5. 새 Refresh Token 저장
        saveRefreshTokenPort.save(newRefreshToken);

        return new AuthTokenResponse(
            newAccessToken.getValue(),
            newRefreshToken.getValue(),
            newAccessToken.getExpiresAt()
        );

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new TokenRefreshException("LOCK_INTERRUPTED");
    } finally {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
```

**Option 2: Client 직접 재발급**

클라이언트가 401 응답 받은 후 직접 `/api/v1/auth/refresh` 호출

```javascript
// Frontend 예시
async function fetchWithTokenRefresh(url, options) {
    let response = await fetch(url, {
        ...options,
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            ...options.headers
        }
    });

    if (response.status === 401) {
        // Access Token 만료 → Refresh 시도
        const refreshResponse = await fetch('/api/v1/auth/refresh', {
            method: 'POST',
            body: JSON.stringify({ refreshToken }),
            credentials: 'include'  // Cookie 포함
        });

        if (refreshResponse.ok) {
            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = await refreshResponse.json();

            // 새 토큰 저장
            localStorage.setItem('accessToken', newAccessToken);
            localStorage.setItem('refreshToken', newRefreshToken);

            // 원래 요청 재시도
            response = await fetch(url, {
                ...options,
                headers: {
                    'Authorization': `Bearer ${newAccessToken}`,
                    ...options.headers
                }
            });
        } else {
            // Refresh Token도 만료 → 로그인 페이지로 리다이렉트
            window.location.href = '/login';
        }
    }

    return response;
}
```

**Gateway 재발급 Error Code**:

| Error Code | HTTP Status | 설명 | Client Action |
|-----------|-------------|------|---------------|
| `REFRESH_TOKEN_MISSING` | 401 | Refresh Token이 없음 | 재로그인 유도 |
| `REFRESH_TOKEN_EXPIRED` | 401 | Refresh Token 만료 | 재로그인 유도 |
| `REFRESH_TOKEN_NOT_FOUND` | 401 | Refresh Token이 DB에 없음 | 재로그인 유도 |
| `CONCURRENT_REFRESH_DETECTED` | 409 | 동시 재발급 감지 (Race Condition) | 3초 후 재시도 |
| `REFRESH_TOKEN_REUSE_DETECTED` | 401 | Refresh Token 재사용 감지 (보안 위협) | 모든 토큰 폐기 → 재로그인 |
| `TOKEN_REFRESH_FAILED` | 500 | AuthHub 재발급 실패 | 재로그인 유도 |

**권장 방식**: **Option 1 (Gateway 자동 재발급)**
- 클라이언트 복잡도 감소
- 일관된 재발급 로직 (모든 클라이언트 공통)
- Race Condition 방지 용이 (Gateway에서 중앙 관리)

---

##### 2.1.4-1 RateLimiter Component (Rate Limiting / Abuse Protection)

**목적**: Brute Force 공격, DDoS, Credential Stuffing 방지

**Redis 기반 Rate Limit 구현**:

```java
@Component
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Rate Limit 체크 및 증가
     * @return true if allowed, false if rate limit exceeded
     */
    public boolean tryAcquire(String key, int maxAttempts, Duration window) {
        String redisKey = "rate_limit:" + key;

        Long currentAttempts = redisTemplate.opsForValue().increment(redisKey);

        if (currentAttempts == 1) {
            // 첫 시도 → TTL 설정
            redisTemplate.expire(redisKey, window);
        }

        return currentAttempts <= maxAttempts;
    }

    /**
     * 남은 시도 횟수 조회
     */
    public int getRemainingAttempts(String key, int maxAttempts) {
        String redisKey = "rate_limit:" + key;
        Long currentAttempts = Optional.ofNullable(
            redisTemplate.opsForValue().get(redisKey)
        ).map(Long::parseLong).orElse(0L);

        return Math.max(0, maxAttempts - currentAttempts.intValue());
    }

    /**
     * Rate Limit 수동 리셋
     */
    public void reset(String key) {
        redisTemplate.delete("rate_limit:" + key);
    }
}
```

**Rate Limit / Abuse Protection 정책**:

IAM 시스템은 **Brute Force 공격**, **Credential Stuffing**, **DDoS**를 방지하기 위해 다음 정책을 강제합니다:

| 작업 | 제한 | Window | Key Pattern | 초과 시 조치 | HTTP Status |
|-----|------|--------|------------|-------------|-------------|
| **OTP 요청** | 3회 | 1시간 | `otp_send:{phoneNumber}` | 1시간 대기, AuditLog 기록 | 429 |
| **전화번호 로그인 실패** | 5회 | 5분 | `phone_login:{phoneNumber}` | 5분 대기 후 재시도 | 429 |
| **비밀번호 로그인 실패** | 5회 | 10분 | `password_login:{email}` | **30분 계정 잠금** (ACCOUNT_LOCKED), AuditLog 기록 | 403 |
| **Refresh Token 재발급** | 3회 | 1분 | `token_refresh:{userId}` | 1분 대기 후 재시도 | 429 |
| **Kakao 소셜 로그인 (IP)** | 10회 | 5분 | `kakao_login:{ipAddress}` | 5분 IP Throttling, AuditLog 기록 | 429 |

**정책 세부사항**:

1. **OTP 요청 횟수 제한** (Brute Force 방지):
   - 같은 전화번호로 1시간에 3회까지만 OTP 발송 가능
   - 초과 시 `OTP_SEND_RATE_LIMIT_EXCEEDED` 예외
   - AuditLog: `RATE_LIMIT_EXCEEDED` (eventType)

2. **로그인 실패 횟수 제한** (Credential Stuffing 방지):
   - 전화번호 로그인: 5분 내 5회 실패 시 5분 대기
   - 비밀번호 로그인: 10분 내 5회 실패 시 **계정 30분 잠금**
   - 잠금 해제: 30분 경과 후 자동 해제 또는 Super Admin 수동 해제
   - AuditLog: `LOGIN_FAILURE` (실패 시마다), `ACCOUNT_LOCKED` (잠금 시)

3. **Refresh Token 재발급 제한** (Token Abuse 방지):
   - 같은 사용자가 1분에 3회까지만 재발급 요청 가능
   - 초과 시 `TOKEN_REFRESH_RATE_LIMIT_EXCEEDED` 예외
   - AuditLog: `RATE_LIMIT_EXCEEDED`

4. **IP 기반 Throttling** (DDoS 방지):
   - Kakao 소셜 로그인: 같은 IP에서 5분에 10회까지만 허용
   - `X-Forwarded-For` 헤더에서 실제 Client IP 추출
   - 초과 시 `KAKAO_LOGIN_RATE_LIMIT_EXCEEDED` 예외
   - AuditLog: `RATE_LIMIT_EXCEEDED`

**UseCase에 Rate Limit 적용**:

```java
@Service
@Transactional
public class LoginWithPhoneService implements LoginWithPhoneUseCase {

    private final RateLimiter rateLimiter;
    private final LoadUserByPhonePort loadUserByPhonePort;
    private final LockUserAccountPort lockUserAccountPort;

    @Override
    public AuthTokenResponse execute(LoginWithPhoneCommand command) {
        String rateLimitKey = "phone_login:" + command.phoneNumber();

        // 1. Rate Limit 체크
        if (!rateLimiter.tryAcquire(rateLimitKey, 5, Duration.ofMinutes(5))) {
            int remaining = rateLimiter.getRemainingAttempts(rateLimitKey, 5);
            throw new RateLimitExceededException(
                "PHONE_LOGIN_RATE_LIMIT_EXCEEDED",
                "Too many login attempts. Please try again in 5 minutes.",
                remaining
            );
        }

        // 2. 사용자 조회
        User user = loadUserByPhonePort.load(command.phoneNumber())
            .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        // 3. 비밀번호 검증
        if (!user.getCredential().verifyPassword(command.password())) {
            // 실패 카운트 증가
            throw new InvalidPasswordException("INVALID_PASSWORD");
        }

        // 4. 로그인 성공 → Rate Limit 리셋
        rateLimiter.reset(rateLimitKey);

        // 5. Token 생성
        return generateTokens(user);
    }
}
```

**비밀번호 로그인 실패 시 계정 잠금**:

```java
@Service
@Transactional
public class LoginWithEmailService implements LoginWithEmailUseCase {

    private final RateLimiter rateLimiter;
    private final LoadUserByEmailPort loadUserByEmailPort;
    private final LockUserAccountPort lockUserAccountPort;

    @Override
    public AuthTokenResponse execute(LoginWithEmailCommand command) {
        String rateLimitKey = "password_login:" + command.email();

        // 1. Rate Limit 체크 (5회 시도 허용, 10분 윈도우)
        if (!rateLimiter.tryAcquire(rateLimitKey, 5, Duration.ofMinutes(10))) {
            throw new RateLimitExceededException(
                "PASSWORD_LOGIN_RATE_LIMIT_EXCEEDED",
                "Too many login attempts. Account locked for 30 minutes."
            );
        }

        // 2. 사용자 조회
        User user = loadUserByEmailPort.load(command.email())
            .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        // 3. 계정 잠금 상태 확인
        if (user.isLocked()) {
            throw new AccountLockedException("ACCOUNT_LOCKED", user.getLockedUntil());
        }

        // 4. 비밀번호 검증
        if (!user.getCredential().verifyPassword(command.password())) {
            int remaining = rateLimiter.getRemainingAttempts(rateLimitKey, 5);

            // 5회 연속 실패 → 30분 계정 잠금
            if (remaining == 0) {
                LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(30);
                lockUserAccountPort.lock(user.getUserId(), lockedUntil);

                throw new AccountLockedException(
                    "ACCOUNT_LOCKED_DUE_TO_FAILED_ATTEMPTS",
                    "Account locked for 30 minutes due to 5 failed login attempts.",
                    lockedUntil
                );
            }

            throw new InvalidPasswordException(
                "INVALID_PASSWORD",
                "Remaining attempts: " + remaining
            );
        }

        // 6. 로그인 성공 → Rate Limit 리셋
        rateLimiter.reset(rateLimitKey);

        // 7. 계정 잠금 해제 (이전에 잠겼었다면)
        if (user.isLocked()) {
            lockUserAccountPort.unlock(user.getUserId());
        }

        // 8. Token 생성
        return generateTokens(user);
    }
}
```

**Refresh Token 재발급 Rate Limit**:

```java
@Service
@Transactional
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final RateLimiter rateLimiter;

    @Override
    public AuthTokenResponse execute(RefreshAccessTokenCommand command) {
        // 1. Refresh Token 검증하여 UserId 추출
        RefreshToken refreshToken = loadRefreshTokenPort.load(command.refreshToken())
            .orElseThrow(() -> new InvalidRefreshTokenException("REFRESH_TOKEN_NOT_FOUND"));

        String rateLimitKey = "token_refresh:" + refreshToken.getUserId();

        // 2. Rate Limit 체크 (3회 시도 허용, 1분 윈도우)
        if (!rateLimiter.tryAcquire(rateLimitKey, 3, Duration.ofMinutes(1))) {
            throw new RateLimitExceededException(
                "TOKEN_REFRESH_RATE_LIMIT_EXCEEDED",
                "Too many token refresh attempts. Please wait 1 minute."
            );
        }

        // 3. Refresh Token Rotation 수행
        // ... (기존 로직)
    }
}
```

**IP 기반 Rate Limit (Kakao 로그인)**:

```java
@Service
public class LoginWithKakaoService implements LoginWithKakaoUseCase {

    private final RateLimiter rateLimiter;
    private final HttpServletRequest request;

    @Override
    public AuthTokenResponse execute(LoginWithKakaoCommand command) {
        String clientIp = getClientIpAddress(request);
        String rateLimitKey = "kakao_login:" + clientIp;

        // 1. IP 기반 Rate Limit (10회 시도 허용, 5분 윈도우)
        if (!rateLimiter.tryAcquire(rateLimitKey, 10, Duration.ofMinutes(5))) {
            throw new RateLimitExceededException(
                "KAKAO_LOGIN_RATE_LIMIT_EXCEEDED",
                "Too many Kakao login attempts from this IP. Please try again in 5 minutes."
            );
        }

        // 2. Kakao 로그인 처리
        // ... (기존 로직)
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

**Rate Limit 예외 처리**:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)  // 429
            .header("Retry-After", "300")  // 5분 = 300초
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                Map.of("remaining_attempts", ex.getRemainingAttempts())
            ));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException ex) {
        long secondsUntilUnlock = ChronoUnit.SECONDS.between(LocalDateTime.now(), ex.getLockedUntil());

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)  // 403
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                Map.of(
                    "locked_until", ex.getLockedUntil(),
                    "retry_after_seconds", secondsUntilUnlock
                )
            ));
    }
}
```

**Rate Limit 모니터링 (AuditLog 연동)**:

```java
@Aspect
@Component
public class RateLimitAuditAspect {

    private final SaveAuditLogPort saveAuditLogPort;

    @AfterThrowing(
        pointcut = "execution(* com.company.authhub.application..*UseCase.execute(..))",
        throwing = "ex"
    )
    public void logRateLimitExceeded(JoinPoint joinPoint, RateLimitExceededException ex) {
        AuditLog auditLog = AuditLog.builder()
            .eventType(AuditEventType.RATE_LIMIT_EXCEEDED)
            .userId(extractUserIdFromException(ex))
            .ipAddress(getClientIpAddress())
            .action(ActionType.LOGIN_ATTEMPT)
            .success(false)
            .errorMessage(ex.getMessage())
            .details(toJson(Map.of(
                "error_code", ex.getErrorCode(),
                "remaining_attempts", ex.getRemainingAttempts()
            )))
            .build();

        saveAuditLogPort.save(auditLog);
    }
}
```

**Zero-Tolerance 규칙 준수**:
- ✅ **Redis 분산 락**: Refresh Token Rotation Race Condition 방지
- ✅ **Transaction 경계**: Rate Limit 체크는 트랜잭션 밖 (Redis 조회)

---

##### 2.1.5 CreateOrganizationUseCase (조직 생성)

**Input**:
```java
public record CreateOrganizationCommand(
    Long tenantId,
    String organizationName,
    String description
) {}
```

**Output**:
```java
public record OrganizationResponse(
    Long organizationId,
    String organizationName,
    String status,
    LocalDateTime createdAt
) {}
```

**Transaction**: Yes (조직 생성)

**비즈니스 로직**:
1. **권한 확인**: 요청자가 Super Admin인지 확인
   - Super Admin 아니면 예외: `UnauthorizedException`
2. 조직명 중복 확인 (CheckDuplicateOrganizationNamePort):
   - Tenant 내 중복 시 예외: `DuplicateOrganizationNameException`
3. Organization Aggregate 생성:
   - `status = ACTIVE`
4. Organization 저장 (SaveOrganizationPort)
5. **트랜잭션 커밋**

**Zero-Tolerance 규칙 준수**:
- ✅ **Command/Query 분리**: Command UseCase

---

##### 2.1.6 AssignRoleToUserUseCase (사용자에게 Role 부여)

**Input**:
```java
public record AssignRoleCommand(
    Long userId,
    Long roleId
) {}
```

**Output**:
```java
public record UserResponse(
    Long userId,
    List<String> roles
) {}
```

**Transaction**: Yes (Role 할당 + Refresh Token 폐기)

**비즈니스 로직**:
1. **권한 확인**: 요청자가 Super Admin 또는 Organization Admin인지 확인
   - Organization Admin인 경우, 자신이 속한 Organization의 Role만 부여 가능
2. 사용자 조회 (LoadUserPort)
3. Role 조회 (LoadRolePort)
4. Role 할당 (User.assignRole(role)):
   - 이미 보유한 Role이면 무시
   - 새 Role이면 추가
5. User 저장 (SaveUserPort)
6. **Refresh Token 폐기** (DeleteRefreshTokensByUserIdPort):
   - 사용자의 모든 Refresh Token 삭제 (MySQL + Redis)
   - 사용자는 재로그인해야 새 Role 반영된 Access Token 획득
7. **트랜잭션 커밋**

**Zero-Tolerance 규칙 준수**:
- ✅ **Transaction 경계**: 외부 API 호출 없음

---

#### 2.2 Query UseCase

##### 2.2.1 GetUserUseCase (사용자 조회)

**Input**:
```java
public record GetUserQuery(
    Long userId
) {}
```

**Output**:
```java
public record UserDetailResponse(
    Long userId,
    String userType,
    String phoneNumber,
    String email,
    String name,
    String profileImageUrl,
    Long tenantId,
    Long organizationId,
    List<String> roles,
    String status,
    LocalDateTime createdAt
) {}
```

**Transaction**: ReadOnly

**비즈니스 로직**:
1. 사용자 조회 (LoadUserPort)
   - 존재하지 않으면 예외: `UserNotFoundException`
2. **권한 확인**: 본인 또는 Super Admin/Org Admin만 조회 가능
3. Role 조회 (LoadRolesByUserIdPort)
4. UserDetailResponse 반환

**Zero-Tolerance 규칙 준수**:
- ✅ **Command/Query 분리**: Query UseCase (ReadOnly)

---

##### 2.2.2 ListUsersUseCase (사용자 목록 조회)

**Input**:
```java
public record ListUsersQuery(
    Long tenantId,
    Long organizationId,  // 선택 사항 (Organization 필터)
    String userType,      // 선택 사항 (PUBLIC/INTERNAL 필터)
    String status,        // 선택 사항 (ACTIVE/INACTIVE 필터)
    int page,
    int size
) {}
```

**Output**:
```java
public record PageResponse<UserSummaryResponse>(
    List<UserSummaryResponse> items,
    int page,
    int size,
    long totalElements,
    int totalPages
) {}

public record UserSummaryResponse(
    Long userId,
    String userType,
    String name,
    String email,
    Long organizationId,
    String status,
    LocalDateTime createdAt
) {}
```

**Transaction**: ReadOnly

**비즈니스 로직**:
1. **권한 확인**:
   - Super Admin: 모든 사용자 조회 가능
   - Organization Admin: 자신이 속한 Organization의 사용자만 조회 가능
2. 페이징 조회 (ListUsersPort):
   - QueryDSL로 동적 쿼리 (tenantId, organizationId, userType, status 필터)
   - Cursor-based Pagination (성능 최적화)
3. PageResponse 반환

**Zero-Tolerance 규칙 준수**:
- ✅ **Command/Query 분리**: Query UseCase (ReadOnly)

---

##### 2.2.3 ValidateAccessTokenUseCase (Access Token 검증)

**Input**:
```java
public record ValidateAccessTokenQuery(
    String accessToken
) {}
```

**Output**:
```java
public record TokenValidationResponse(
    boolean valid,
    Long userId,
    String userType,
    Long tenantId,
    List<Long> orgIds,
    List<String> roles,
    List<String> permissions
) {}
```

**Transaction**: None (Gateway에서 Public Key로 검증)

**비즈니스 로직**:
1. **Gateway 자체 검증**: Public Key로 서명 검증
   - 서명 검증 실패 시: `valid = false`
2. Access Token Payload 파싱:
   - userId, tenantId, userType, orgIds, roles, permissions 추출
3. 만료 확인:
   - 만료되었으면: `valid = false`
4. TokenValidationResponse 반환

**Gateway 캐싱**:
- ✅ **1-3분 캐싱**: Gateway는 검증 결과를 1-3분 캐싱
  - 동일한 Access Token에 대한 반복 검증 방지

**Zero-Tolerance 규칙 준수**:
- ✅ **Command/Query 분리**: Query UseCase (ReadOnly)

---

### 3. Persistence Layer

#### 3.1 JPA Entity

##### 3.1.1 UserJpaEntity

**테이블**: `users`

**필드**:
```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_phone_number", columnList = "phone_number"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status")
})
public class UserJpaEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;  // UUID

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;  // FK (Long FK 전략)

    @Column(name = "organization_id")
    private Long organizationId;  // FK (Nullable for PUBLIC)

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;  // PUBLIC, INTERNAL

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;  // 전화번호 (Nullable for INTERNAL)

    @Column(name = "email", unique = true)
    private String email;  // 이메일 (Nullable for PUBLIC)

    @Column(name = "password_hash")
    private String passwordHash;  // 비밀번호 해시 (BCrypt)

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;  // ACTIVE, INACTIVE, SUSPENDED, DELETED

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 소프트 삭제 시각
}
```

**Unique Constraint**:
- `UNIQUE(tenant_id, phone_number)` - Tenant 내 전화번호 중복 방지
- `UNIQUE(tenant_id, email)` - Tenant 내 이메일 중복 방지

**인덱스**:
- `idx_tenant_id` (tenant_id) - Tenant 필터링
- `idx_phone_number` (phone_number) - 전화번호 검색
- `idx_email` (email) - 이메일 검색
- `idx_status` (status) - 상태별 검색

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long tenantId`, `private Long organizationId`
- ✅ **Lombok 금지**: Plain JPA Entity (Getter/Setter 수동 생성)

---

##### 3.1.2 OrganizationJpaEntity

**테이블**: `organizations`

**필드**:
```java
@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class OrganizationJpaEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false, unique = true)
    private Long organizationId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationStatus status;  // ACTIVE, INACTIVE, DELETED

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
```

**Unique Constraint**:
- `UNIQUE(tenant_id, organization_name)` - Tenant 내 조직명 중복 방지

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long tenantId`

---

##### 3.1.3 RefreshTokenJpaEntity

**테이블**: `refresh_tokens`

**필드**:
```java
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_token_hash", columnList = "token_hash"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class RefreshTokenJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;  // UUID

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;  // SHA-256 해시

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
```

**인덱스**:
- `idx_user_id` (user_id) - 사용자별 토큰 조회
- `idx_token_hash` (token_hash) - 토큰 검증
- `idx_expires_at` (expires_at) - 만료된 토큰 정리 (Batch Job)

**Zero-Tolerance 규칙 준수**:
- ✅ **Long FK 전략**: `private Long userId`, `private Long tenantId`

---

#### 3.2 Repository

##### 3.2.1 UserJpaRepository

```java
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUserId(String userId);
    Optional<UserJpaEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
```

##### 3.2.2 UserQueryDslRepository

**메서드**:
```java
public interface UserQueryDslRepository {
    Page<UserJpaEntity> findByTenantIdAndFilters(
        Long tenantId,
        Long organizationId,
        UserType userType,
        UserStatus status,
        Pageable pageable
    );
}
```

**구현** (QueryDSL):
```java
public Page<UserJpaEntity> findByTenantIdAndFilters(...) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(user.tenantId.eq(tenantId));

    if (organizationId != null) {
        builder.and(user.organizationId.eq(organizationId));
    }
    if (userType != null) {
        builder.and(user.userType.eq(userType));
    }
    if (status != null) {
        builder.and(user.status.eq(status));
    }

    List<UserJpaEntity> results = queryFactory
        .selectFrom(user)
        .where(builder)
        .orderBy(user.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = queryFactory
        .selectFrom(user)
        .where(builder)
        .fetchCount();

    return new PageImpl<>(results, pageable, total);
}
```

**Zero-Tolerance 규칙 준수**:
- ✅ **QueryDSL 최적화**: N+1 방지

---

##### 3.2.3 RefreshTokenJpaRepository

```java
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {
    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
```

---

### 4. REST API Layer

#### 4.1 API 엔드포인트

| Method | Path | Description | Request DTO | Response DTO | Status Code |
|--------|------|-------------|-------------|--------------|-------------|
| POST | /api/v1/auth/register | 사용자 가입 (PUBLIC) | RegisterUserRequest | UserResponse | 201 Created |
| POST | /api/v1/auth/login/phone | 전화번호 로그인 | LoginWithPhoneRequest | AuthTokenResponse | 200 OK |
| POST | /api/v1/auth/login/kakao | Kakao 로그인 | LoginWithKakaoRequest | AuthTokenResponse | 200 OK |
| POST | /api/v1/auth/refresh | Access Token 재발급 | RefreshAccessTokenRequest | AuthTokenResponse | 200 OK |
| POST | /api/v1/auth/logout | 로그아웃 | - | - | 204 No Content |
| GET | /api/v1/users/{userId} | 사용자 조회 | - | UserDetailResponse | 200 OK |
| GET | /api/v1/users | 사용자 목록 조회 | ListUsersRequest | PageResponse<UserSummaryResponse> | 200 OK |
| POST | /api/v1/organizations | 조직 생성 | CreateOrganizationRequest | OrganizationResponse | 201 Created |
| POST | /api/v1/users/{userId}/roles | Role 부여 | AssignRoleRequest | UserResponse | 200 OK |
| GET | /api/v1/auth/validate | Access Token 검증 | - | TokenValidationResponse | 200 OK |

---

#### 4.2 Request/Response DTO

##### 4.2.1 RegisterUserRequest

```java
public record RegisterUserRequest(
    @NotBlank @Pattern(regexp = "^\\+82-\\d{2,3}-\\d{3,4}-\\d{4}$")
    String phoneNumber,

    @NotBlank @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    String password,

    @NotBlank @Size(min = 2, max = 50)
    String name,

    @URL
    String profileImageUrl
) {}
```

**Validation**:
- 전화번호: 국제 형식 (`+82-10-1234-5678`)
- 비밀번호: 최소 8자, 대소문자/숫자/특수문자 포함

---

##### 4.2.2 AuthTokenResponse

```java
public record AuthTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,       // "Bearer"
    int expiresIn,          // 1800 (30분)
    LocalDateTime expiresAt
) {}
```

---

##### 4.2.3 Error Response

**형식**:
```json
{
  "errorCode": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다.",
  "timestamp": "2025-01-13T12:34:56Z",
  "path": "/api/v1/users/123"
}
```

**HTTP 상태 코드 전략**:
- **400 Bad Request**: 유효성 검증 실패 (Validation Error)
- **401 Unauthorized**: 인증 실패 (토큰 없음, 토큰 만료, 토큰 무효)
- **403 Forbidden**: 권한 없음 (Role/Permission 부족)
- **404 Not Found**: 리소스 없음 (사용자, 조직 등)
- **409 Conflict**: 비즈니스 규칙 위반 (중복 전화번호, 계정 통합 실패 등)
- **500 Internal Server Error**: 서버 오류

---

#### 4.3 인증/인가

**인증 전략**:
- ✅ **JWT (Access Token + Refresh Token)**
  - Access Token: 30분, RS256
  - Refresh Token: 14일, Rotation 적용
- ✅ **Authorization 헤더**: `Authorization: Bearer {accessToken}`

**권한 검증**:
- ✅ **Gateway에서 수행**: Access Token의 `permissions` claim 활용
- ✅ **서비스별 권한 정책**: `api-permissions.yml`에 정의
  - 예: `GET /api/v1/users` → 필수 권한: `user:read`
- ✅ **권한 부족 시**: HTTP 403 Forbidden

**인증 필수 API**:
- ✅ **로그인 필수**: 사용자 조회, 조직 생성, Role 부여 등
- ✅ **공개 API**: 회원가입, 로그인, 토큰 재발급

---

### 5. Gateway 연동

#### 5.1 토큰 검증

**Gateway 역할**:
- ✅ **Public Key로 자체 검증**: AuthHub의 Public Key로 Access Token 서명 검증
  - AuthHub API 호출 없이 Gateway에서 독립적으로 검증
- ✅ **검증 실패 시**: HTTP 401 Unauthorized 반환
- ✅ **검증 결과 캐싱**: 1-3분 캐싱 (성능 최적화)

**검증 플로우**:
```
1. 클라이언트 요청 (Authorization: Bearer {accessToken})
2. Gateway: Access Token 추출
3. Gateway: Public Key로 서명 검증
   - 서명 검증 실패 → 401 Unauthorized
4. Gateway: 만료 확인
   - 만료되었으면 → 401 Unauthorized
5. Gateway: Payload 파싱 (userId, tenantId, roles, permissions)
6. Gateway: 권한 검증 (다음 섹션 참조)
7. Gateway: 요청 전달 (서비스로)
```

---

#### 5.2 권한 검증

**Gateway 역할**:
- ✅ **Permission 기반 검증**: Access Token의 `permissions` claim 사용
- ✅ **서비스별 권한 정책**: `api-permissions.yml` 파일을 Gateway에서 로드
  - 각 서비스가 시작 시 AuthHub에 등록한 Permission을 Gateway가 동기화
- ✅ **권한 부족 시**: HTTP 403 Forbidden 반환

**권한 검증 플로우**:
```
1. Gateway: 요청 경로 추출 (예: GET /api/v1/users)
2. Gateway: 권한 정책 조회 (api-permissions.yml)
   - GET /api/v1/users → 필수 권한: "user:read"
3. Gateway: Access Token의 permissions claim 확인
   - permissions: ["user:read", "order:create"]
4. Gateway: 필수 권한 포함 여부 확인
   - "user:read" 포함 → 통과
   - "user:read" 없음 → 403 Forbidden
5. Gateway: 요청 전달 (서비스로)
```

**권한 정책 예시** (`api-permissions.yml`):
```yaml
service: commerce
endpoints:
  - path: GET /api/v1/users
    required_permissions: ["user:read"]
  - path: POST /api/v1/orders
    required_permissions: ["order:create"]
  - path: DELETE /api/v1/orders/{orderId}
    required_permissions: ["order:delete"]
```

---

#### 5.3 Trace-ID

**Gateway 역할**:
- ✅ **Trace-ID 생성**: Gateway가 UUID 형식으로 생성
- ✅ **요청 헤더 삽입**: `X-Trace-Id: {uuid}`
- ✅ **로깅**: 모든 요청에 Trace-ID 포함하여 로깅

**Trace-ID 플로우**:
```
1. 클라이언트 요청
2. Gateway: Trace-ID 생성 (UUID.randomUUID())
3. Gateway: 요청 헤더에 추가 (X-Trace-Id: {uuid})
4. Gateway: 로깅 (Trace-ID 포함)
5. Gateway: 서비스로 요청 전달 (X-Trace-Id 헤더 포함)
6. 서비스: Trace-ID 추출 및 로깅
7. 서비스: 응답 헤더에 Trace-ID 포함
8. Gateway: 응답 반환 (X-Trace-Id 헤더 포함)
```

---

## ⚠️ 제약사항

### 비기능 요구사항

**성능**:
- 토큰 발급/검증 응답 시간: < 100ms (P95)
- 동시 로그인 처리량: 10,000 req/min 이상
- Gateway 토큰 검증: < 10ms (캐싱 활용)

**보안**:
- JWT RS256 서명 (비대칭 키)
- 비밀번호 BCrypt 해시 (Cost Factor 12)
- Refresh Token 해시 저장 (SHA-256)
- HTTPS 통신 필수 (TLS 1.2+)
- Refresh Token Rotation 적용

**확장성**:
- 멀티테넌트 지원 (Row-level 격리)
- Horizontal Scaling 가능 (Stateless)
- Redis 캐싱 (Refresh Token, 토큰 검증 결과)

**가용성**:
- 목표: 99.9% (초기), 99.99% (장기)
- Gateway 다중화 (ECS Auto Scaling)
- AuthHub 다중화 (ECS Auto Scaling)
- MySQL Replication (Master-Slave)

---

## 🧪 테스트 전략

### Unit Test

**Domain**:
- User Aggregate 비즈니스 로직 (registerUser, loginWithPhone, mergeAccount 등)
- UserStatus Enum 상태 전환 로직
- Value Object (UserId, Credential) 생성 및 검증

**Application**:
- RegisterUserUseCase (Mock PersistencePort)
- LoginWithPhoneUseCase (Mock PersistencePort)
- RefreshAccessTokenUseCase (Mock PersistencePort)

### Integration Test

**Persistence**:
- UserJpaRepository CRUD 테스트 (TestContainers MySQL)
- UserQueryDslRepository 복잡한 쿼리 테스트
- RefreshTokenJpaRepository 토큰 저장/삭제 테스트

**REST API**:
- AuthApiController (MockMvc)
- Validation 테스트 (400 Bad Request)
- 인증/인가 테스트 (401 Unauthorized, 403 Forbidden)

### E2E Test

- 회원가입 → 로그인 → 토큰 재발급 플로우
- Kakao 로그인 → 계정 통합 플로우
- Gateway 토큰 검증 → 권한 검증 플로우

---

## 🚀 개발 계획

### Phase 1: Domain Layer (예상: 5일)
- [ ] User Aggregate 구현
- [ ] Organization Aggregate 구현
- [ ] Tenant Aggregate 구현
- [ ] Role/Permission Aggregate 구현
- [ ] Value Object 구현 (UserId, Credential 등)
- [ ] Domain Unit Test (TestFixture 패턴)

### Phase 2: Application Layer (예상: 7일)
- [ ] RegisterUserUseCase 구현
- [ ] LoginWithPhoneUseCase 구현
- [ ] LoginWithKakaoUseCase 구현 (Kakao API 연동)
- [ ] RefreshAccessTokenUseCase 구현
- [ ] CreateOrganizationUseCase 구현
- [ ] AssignRoleToUserUseCase 구현
- [ ] Query UseCase 구현 (GetUser, ListUsers 등)
- [ ] Application Unit Test

### Phase 3: Persistence Layer (예상: 5일)
- [ ] JPA Entity 구현 (User, Organization, RefreshToken 등)
- [ ] JpaRepository 구현
- [ ] QueryDSL 쿼리 구현
- [ ] Integration Test (TestContainers)

### Phase 4: REST API Layer (예상: 4일)
- [ ] AuthApiController 구현
- [ ] UserApiController 구현
- [ ] OrganizationApiController 구현
- [ ] Request/Response DTO 구현
- [ ] Exception Handling 구현
- [ ] REST API Integration Test (MockMvc)

### Phase 5: Gateway 연동 (예상: 3일)
- [ ] Public Key 배포 (Gateway)
- [ ] Gateway 토큰 검증 로직 구현
- [ ] Gateway 권한 검증 로직 구현
- [ ] Trace-ID 생성 및 로깅
- [ ] E2E Test (Gateway ↔ AuthHub)

### Phase 6: Kakao OAuth 연동 (예상: 2일)
- [ ] Kakao OAuth 플로우 구현
- [ ] Kakao API 연동 (Access Token 획득, User Info 조회)
- [ ] 계정 통합 로직 구현
- [ ] Kakao 로그인 E2E Test

### Phase 7: Redis 캐싱 (예상: 1일)
- [ ] Refresh Token Redis 캐싱
- [ ] 토큰 검증 결과 캐싱 (Gateway)
- [ ] 캐시 무효화 전략 구현

---

## 📚 참고 문서

- [Domain Layer 규칙](../../docs/coding_convention/02-domain-layer/)
- [Application Layer 규칙](../../docs/coding_convention/03-application-layer/)
- [Persistence Layer 규칙](../../docs/coding_convention/04-persistence-layer/)
- [REST API Layer 규칙](../../docs/coding_convention/01-adapter-in-layer/rest-api/)
- [JWT RFC 7519](https://tools.ietf.org/html/rfc7519)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Kakao Login API](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)

---

**다음 단계**:
1. PRD 검토 및 수정
2. `/jira-from-prd docs/prd/iam-platform.md` - Jira 티켓 생성
3. Kent Beck TDD 사이클 시작 (`/kb/domain/go`)
