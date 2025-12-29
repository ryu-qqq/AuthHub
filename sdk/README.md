# AuthHub SDK

AuthHub REST API와 통합하기 위한 공식 Java SDK입니다. 멀티 테넌트 IAM(Identity and Access Management) 시스템과의 쉬운 연동을 제공합니다.

## 목차

- [개요](#개요)
- [요구사항](#요구사항)
- [설치](#설치)
- [빠른 시작](#빠른-시작)
- [설정](#설정)
- [API 레퍼런스](#api-레퍼런스)
- [인증](#인증)
- [에러 처리](#에러-처리)
- [고급 사용법](#고급-사용법)
- [예제](#예제)

---

## 개요

AuthHub SDK는 두 개의 모듈로 구성됩니다:

| 모듈 | 설명 | 용도 |
|------|------|------|
| `authhub-sdk-core` | 순수 Java SDK 코어 | 모든 Java 프로젝트 |
| `authhub-sdk-spring-boot-starter` | Spring Boot 자동 설정 | Spring Boot 프로젝트 |

### 주요 기능

- **6개 도메인 API 지원**: Tenant, Organization, User, Role, Permission, Onboarding
- **멀티 테넌트 구조**: 테넌트 > 조직 > 사용자 계층 관리
- **RBAC 지원**: 역할 기반 접근 제어 (Role-Based Access Control)
- **타입 안전성**: Java Record 기반의 강타입 DTO
- **Spring Boot 통합**: 자동 설정, 토큰 컨텍스트 필터 자동 등록
- **유연한 인증**: 서비스 토큰 / 사용자 토큰 자동 전파

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
// settings.gradle 또는 build.gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

#### Spring Boot 프로젝트 (권장)

```groovy
dependencies {
    implementation 'com.github.ryu-qqq.AuthHub:authhub-sdk-spring-boot-starter:v1.0.0'
}
```

#### 순수 Java 프로젝트

```groovy
dependencies {
    implementation 'com.github.ryu-qqq.AuthHub:authhub-sdk-core:v1.0.0'
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
    <version>v1.0.0</version>
</dependency>
```

---

## 빠른 시작

### Spring Boot

```yaml
# application.yml
authhub:
  base-url: https://auth.example.com
  service-token: ${AUTHHUB_SERVICE_TOKEN}  # 환경변수 권장
```

```java
@Service
public class UserService {

    private final AuthHubClient authHub;

    public UserService(AuthHubClient authHub) {
        this.authHub = authHub;
    }

    public void createUser(String tenantId, String orgId, String email) {
        CreateUserRequest request = new CreateUserRequest(
            tenantId,
            orgId,
            email,
            "010-1234-5678",
            "initial-password"
        );

        ApiResponse<CreateUserResponse> response = authHub.users().create(request);
        System.out.println("Created user: " + response.data().userId());
    }
}
```

### 순수 Java

```java
AuthHubClient client = AuthHubClient.builder()
    .baseUrl("https://auth.example.com")
    .serviceToken("your-service-token")
    .build();

// Tenant 생성
CreateTenantRequest request = new CreateTenantRequest("My Company");
ApiResponse<CreateTenantResponse> response = client.tenants().create(request);

System.out.println("Tenant ID: " + response.data().tenantId());
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

## API 레퍼런스

### TenantApi - 테넌트 관리

테넌트는 AuthHub의 최상위 격리 단위입니다.

```java
TenantApi tenants = client.tenants();

// 테넌트 생성
ApiResponse<CreateTenantResponse> created = tenants.create(
    new CreateTenantRequest("My Company")
);

// 테넌트 조회
ApiResponse<TenantResponse> tenant = tenants.getById("tenant-uuid");

// 테넌트 검색
Map<String, Object> params = Map.of(
    "name", "Company",
    "status", "ACTIVE",
    "page", 0,
    "size", 20
);
ApiResponse<PageResponse<TenantResponse>> list = tenants.search(params);

// 테넌트 이름 수정
tenants.updateName("tenant-uuid", new UpdateTenantNameRequest("New Name"));

// 테넌트 상태 변경
tenants.updateStatus("tenant-uuid", new UpdateTenantStatusRequest("INACTIVE"));

// 테넌트 삭제 (비활성화)
tenants.delete("tenant-uuid");
```

### OrganizationApi - 조직 관리

조직은 테넌트 하위의 논리적 그룹입니다.

```java
OrganizationApi organizations = client.organizations();

// 조직 생성
ApiResponse<CreateOrganizationResponse> created = organizations.create(
    new CreateOrganizationRequest("tenant-uuid", "Engineering Team")
);

// 조직 조회
ApiResponse<OrganizationResponse> org = organizations.getById(1L);

// 조직 검색
Map<String, Object> params = Map.of(
    "tenantId", "tenant-uuid",
    "name", "Engineering",
    "status", "ACTIVE",
    "page", 0,
    "size", 20,
    "sortKey", "CREATED_DATE",
    "sortDirection", "DESC"
);
ApiResponse<PageResponse<OrganizationResponse>> list = organizations.search(params);

// 조직 정보 수정
organizations.update(1L, new UpdateOrganizationRequest("DevOps Team"));

// 조직 상태 변경
organizations.updateStatus(1L, new UpdateOrganizationStatusRequest("INACTIVE"));

// 조직 삭제
organizations.delete(1L);
```

### UserApi - 사용자 관리

```java
UserApi users = client.users();

// 사용자 생성
ApiResponse<CreateUserResponse> created = users.create(
    new CreateUserRequest(
        "tenant-uuid",      // 테넌트 ID
        "org-uuid",         // 조직 ID
        "user@example.com", // 식별자 (이메일)
        "010-1234-5678",    // 전화번호
        "password123"       // 비밀번호
    )
);

// 사용자 조회
ApiResponse<UserResponse> user = users.getById(1L);

// 사용자 검색
Map<String, Object> params = Map.of(
    "tenantId", "tenant-uuid",
    "organizationId", "org-uuid",
    "identifier", "user@",
    "status", "ACTIVE",
    "page", 0,
    "size", 20
);
ApiResponse<PageResponse<UserResponse>> list = users.search(params);

// 사용자 정보 수정 (identifier 변경)
users.update(1L, new UpdateUserRequest("new-identifier"));

// 사용자 상태 변경
users.updateStatus(1L, new UpdateUserStatusRequest("SUSPENDED"));

// 비밀번호 변경 (현재 비밀번호, 새 비밀번호)
users.updatePassword(1L, new UpdateUserPasswordRequest("currentPassword123", "newPassword123"));

// 역할 할당
users.assignRole(1L, new AssignUserRoleRequest(10L));

// 역할 해제
users.unassignRole(1L, 10L);

// 사용자 삭제
users.delete(1L);
```

### RoleApi - 역할 관리

```java
RoleApi roles = client.roles();

// 역할 생성
ApiResponse<CreateRoleResponse> created = roles.create(
    new CreateRoleRequest("org-uuid", "Admin", "관리자 역할")
);

// 역할 조회
ApiResponse<RoleResponse> role = roles.getById(1L);

// 역할 검색
Map<String, Object> params = Map.of(
    "tenantId", "tenant-uuid",
    "name", "Admin",
    "page", 0,
    "size", 20,
    "sortKey", "NAME",
    "sortDirection", "ASC"
);
ApiResponse<PageResponse<RoleResponse>> list = roles.search(params);

// 역할 정보 수정
roles.update(1L, new UpdateRoleRequest("Super Admin", "최고 관리자"));

// 권한 부여
roles.grantPermissions(1L, new GrantRolePermissionRequest(List.of(1L, 2L, 3L)));

// 권한 회수
roles.revokePermissions(1L, new GrantRolePermissionRequest(List.of(3L)));

// 역할 삭제
roles.delete(1L);
```

### PermissionApi - 권한 관리

```java
PermissionApi permissions = client.permissions();

// 권한 생성
ApiResponse<CreatePermissionResponse> created = permissions.create(
    new CreatePermissionRequest(
        "USER",           // 리소스
        "READ",           // 액션
        "사용자 조회 권한", // 설명
        false             // 시스템 권한 여부
    )
);

// 권한 조회
ApiResponse<PermissionResponse> permission = permissions.getById(1L);

// 권한 검색
Map<String, Object> params = Map.of(
    "tenantId", "tenant-uuid",
    "name", "USER",
    "page", 0,
    "size", 20
);
ApiResponse<PageResponse<PermissionResponse>> list = permissions.search(params);

// 권한 정보 수정
permissions.update(1L, new UpdatePermissionRequest("사용자 전체 조회 권한"));

// 권한 삭제
permissions.delete(1L);
```

### OnboardingApi - 테넌트 온보딩

테넌트 + 조직 + 기본 역할 + 관리자를 한 번에 생성합니다.

```java
OnboardingApi onboarding = client.onboarding();

// 테넌트 온보딩
ApiResponse<TenantOnboardingResponse> result = onboarding.onboard(
    new TenantOnboardingRequest(
        "New Company",           // 테넌트 이름
        "Main Organization",     // 조직 이름
        "admin@company.com",     // 관리자 이메일
        "010-0000-0000"          // 관리자 전화번호
    )
);

// 결과
String tenantId = result.data().tenantId();
String organizationId = result.data().organizationId();
String userId = result.data().userId();
String temporaryPassword = result.data().temporaryPassword();
```

---

## 인증

### 인증 방식

SDK는 두 가지 인증 방식을 지원합니다:

| 방식 | 용도 | 설정 |
|------|------|------|
| 서비스 토큰 | 백엔드 서비스 간 통신 (M2M) | `authhub.service-token` |
| 사용자 토큰 | 사용자 요청 전파 | 자동 (ThreadLocal) |

### 토큰 우선순위

```
1. ThreadLocal 토큰 (사용자 요청에서 자동 추출)
2. 서비스 토큰 (설정된 경우)
3. 인증 실패 예외
```

### Spring Boot에서의 토큰 전파

`AuthHubTokenContextFilter`가 자동으로 등록되어, 들어오는 요청의 `Authorization: Bearer {token}` 헤더를 자동으로 추출하여 AuthHub API 호출 시 전파합니다.

```
[Client] --Bearer token--> [Your API] --same token--> [AuthHub API]
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
    ApiResponse<UserResponse> response = client.users().getById(userId);
    // 성공 처리
} catch (AuthHubNotFoundException e) {
    // 사용자 없음
    log.warn("User not found: {}", e.getMessage());
} catch (AuthHubUnauthorizedException e) {
    // 인증 실패
    log.error("Authentication failed: {}", e.getMessage());
} catch (AuthHubForbiddenException e) {
    // 권한 부족
    log.error("Access denied: {}", e.getMessage());
} catch (AuthHubBadRequestException e) {
    // 잘못된 요청
    log.error("Bad request: {} - {}", e.getErrorCode(), e.getMessage());
} catch (AuthHubServerException e) {
    // 서버 에러
    log.error("Server error: {}", e.getMessage());
}
```

### 예외 정보

```java
catch (AuthHubException e) {
    String errorCode = e.getErrorCode();  // "USER_NOT_FOUND"
    String message = e.getMessage();       // "User not found with ID: 123"
    int statusCode = e.getStatusCode();    // 404
}
```

---

## 고급 사용법

### 페이징 처리

```java
int page = 0;
int pageSize = 20;
List<UserResponse> allUsers = new ArrayList<>();

while (true) {
    Map<String, Object> params = Map.of(
        "tenantId", tenantId,
        "page", page,
        "size", pageSize
    );

    ApiResponse<PageResponse<UserResponse>> response = client.users().search(params);
    PageResponse<UserResponse> pageData = response.data();

    allUsers.addAll(pageData.content());

    if (!pageData.hasNext()) {
        break;
    }
    page++;
}
```

### Admin API 사용

Admin API는 추가 집계 정보가 포함된 Summary 응답을 반환합니다:

```java
// Admin용 조직 검색 (요약 정보 포함)
ApiResponse<PageResponse<OrganizationSummaryResponse>> adminList =
    client.organizations().searchAdmin(params);

for (OrganizationSummaryResponse summary : adminList.data().content()) {
    System.out.println("Organization: " + summary.name());
    System.out.println("User count: " + summary.userCount());
    System.out.println("Role count: " + summary.roleCount());
}
```

### 커스텀 TokenResolver

Spring Boot 이외의 환경에서 커스텀 토큰 처리:

```java
TokenResolver customResolver = () -> {
    // 커스텀 토큰 획득 로직
    String token = SecurityContextHolder.getContext()
        .getAuthentication()
        .getCredentials()
        .toString();
    return Optional.ofNullable(token);
};

AuthHubClient client = AuthHubClient.builder()
    .baseUrl("https://auth.example.com")
    .tokenResolver(customResolver)
    .build();
```

---

## 예제

### 완전한 테넌트 온보딩 플로우

```java
@Service
@RequiredArgsConstructor
public class TenantSetupService {

    private final AuthHubClient authHub;

    public TenantSetupResult setupNewTenant(TenantSetupCommand command) {
        // 1. 테넌트 온보딩 (테넌트 + 조직 + 관리자 일괄 생성)
        TenantOnboardingResponse onboarding = authHub.onboarding()
            .onboard(new TenantOnboardingRequest(
                command.companyName(),
                command.organizationName(),
                command.adminEmail(),
                command.adminPhone()
            ))
            .data();

        // 2. 추가 권한 생성
        CreatePermissionResponse readPermission = authHub.permissions()
            .create(new CreatePermissionRequest("ORDER", "READ", "주문 조회", false))
            .data();

        CreatePermissionResponse writePermission = authHub.permissions()
            .create(new CreatePermissionRequest("ORDER", "WRITE", "주문 수정", false))
            .data();

        // 3. 역할 생성 및 권한 부여
        CreateRoleResponse role = authHub.roles()
            .create(new CreateRoleRequest(
                onboarding.organizationId(),
                "OrderManager",
                "주문 관리자"
            ))
            .data();

        authHub.roles().grantPermissions(
            role.roleId(),
            new GrantRolePermissionRequest(List.of(
                readPermission.permissionId(),
                writePermission.permissionId()
            ))
        );

        return new TenantSetupResult(
            onboarding.tenantId(),
            onboarding.organizationId(),
            onboarding.userId(),
            onboarding.temporaryPassword()
        );
    }
}
```

### 사용자 초대 플로우

```java
@Service
@RequiredArgsConstructor
public class UserInvitationService {

    private final AuthHubClient authHub;
    private final EmailService emailService;

    public void inviteUser(String tenantId, String orgId, String email, Long roleId) {
        // 1. 임시 비밀번호 생성
        String tempPassword = generateTempPassword();

        // 2. 사용자 생성
        CreateUserResponse user = authHub.users()
            .create(new CreateUserRequest(tenantId, orgId, email, null, tempPassword))
            .data();

        // 3. 역할 할당
        authHub.users().assignRole(user.userId(), new AssignUserRoleRequest(roleId));

        // 4. 초대 이메일 발송
        emailService.sendInvitation(email, tempPassword);
    }
}
```

---

## 모듈 구조

```
sdk/
├── authhub-sdk-core/                    # 코어 SDK
│   └── src/main/java/
│       └── com/ryuqq/authhub/sdk/
│           ├── api/                      # API 인터페이스
│           │   ├── TenantApi.java
│           │   ├── OrganizationApi.java
│           │   ├── UserApi.java
│           │   ├── RoleApi.java
│           │   ├── PermissionApi.java
│           │   └── OnboardingApi.java
│           ├── client/                   # 클라이언트 구현
│           │   ├── AuthHubClient.java
│           │   └── AuthHubClientBuilder.java
│           ├── config/                   # 설정
│           ├── exception/                # 예외
│           ├── model/                    # DTO 모델
│           │   ├── common/               # 공통 응답
│           │   ├── tenant/
│           │   ├── organization/
│           │   ├── user/
│           │   ├── role/
│           │   ├── permission/
│           │   └── onboarding/
│           └── auth/                     # 인증
│
└── authhub-sdk-spring-boot-starter/     # Spring Boot 통합
    └── src/main/java/
        └── com/ryuqq/authhub/sdk/autoconfigure/
            ├── AuthHubAutoConfiguration.java
            ├── AuthHubProperties.java
            └── AuthHubTokenContextFilter.java
```

---

## 라이선스

MIT License - 자유롭게 사용, 수정, 배포할 수 있습니다.

---

## 문의

- **GitHub Issues**: [https://github.com/ryu-qqq/AuthHub/issues](https://github.com/ryu-qqq/AuthHub/issues)
- **이메일**: support@authhub.example.com
