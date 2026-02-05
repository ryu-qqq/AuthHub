# AuthHub API Endpoints

## 요약

| 분류 | 개수 |
|------|------|
| Query (조회) | 9개 |
| Command (명령) | 18개 |
| Internal (내부 API) | 5개 |
| **합계** | **32개** |

---

## Query Endpoints (조회)

| # | Method | Path | Controller | Method | UseCase |
|---|--------|------|------------|--------|---------|
| 1 | GET | /api/v1/auth/organizations | OrganizationQueryController | searchOrganizationsByOffset | SearchOrganizationsByOffsetUseCase |
| 2 | GET | /api/v1/auth/users | UserQueryController | search | SearchUsersUseCase |
| 3 | GET | /api/v1/auth/users/{userId} | UserQueryController | getById | GetUserUseCase |
| 4 | GET | /api/v1/auth/jwks | TokenQueryController | getPublicKeys | GetPublicKeyUseCase |
| 5 | GET | /api/v1/auth/me | TokenQueryController | getMyContext | GetMyContextUseCase |
| 6 | GET | /api/v1/auth/permission-endpoints | PermissionEndpointQueryController | search | SearchPermissionEndpointsUseCase |

---

### Q1. searchOrganizationsByOffset
- **Path**: `GET /api/v1/auth/organizations`
- **Controller**: `OrganizationQueryController`
- **Request**: `SearchOrganizationsOffsetApiRequest` (@ModelAttribute)
- **Response**: `ApiResponse<PageApiResponse<OrganizationApiResponse>>`
- **UseCase**: `SearchOrganizationsByOffsetUseCase`
- **Permission**: `@PreAuthorize("@access.superAdmin()")`

**설명**: 복합 조건에 맞는 조직 목록을 Offset 기반으로 조회합니다.

**요청 파라미터**:
- tenantIds: 테넌트 ID 목록 (선택)
- searchWord: 검색어 (선택)
- searchField: 검색 필드 (기본: NAME)
- statuses: 상태 필터 (선택)
- startDate: 조회 시작일 (선택)
- endDate: 조회 종료일 (선택)
- page: 페이지 번호 (기본: 0)
- size: 페이지 크기 (기본: 20)

---

### Q2. search (Users)
- **Path**: `GET /api/v1/auth/users`
- **Controller**: `UserQueryController`
- **Request**: Query Parameters
  - organizationId: 소속 조직 ID (선택)
  - searchWord: 검색어 (선택)
  - status: 상태 필터 (선택)
  - page: 페이지 번호 (기본: 0)
  - size: 페이지 크기 (기본: 20)
- **Response**: `ApiResponse<PageApiResponse<UserApiResponse>>`
- **UseCase**: `SearchUsersUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'read')")`

**설명**: 검색 조건에 따라 사용자 목록을 조회합니다.

---

### Q3. getById
- **Path**: `GET /api/v1/auth/users/{userId}`
- **Controller**: `UserQueryController`
- **Request**: Path Variable (userId)
- **Response**: `ApiResponse<UserApiResponse>`
- **UseCase**: `GetUserUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'read') or @access.self(#userId)")`

**설명**: ID로 사용자 정보를 조회합니다.

---

### Q4. getPublicKeys
- **Path**: `GET /api/v1/auth/jwks`
- **Controller**: `TokenQueryController`
- **Request**: 없음
- **Response**: `PublicKeysApiResponse`
- **UseCase**: `GetPublicKeyUseCase`
- **Permission**: 없음 (Public)

**설명**: Gateway에서 JWT 서명 검증용 공개키 목록을 조회합니다 (RFC 7517 JWKS 형식).

---

### Q5. getMyContext
- **Path**: `GET /api/v1/auth/me`
- **Controller**: `TokenQueryController`
- **Request**: 없음 (SecurityContext에서 userId 추출)
- **Response**: `ApiResponse<MyContextApiResponse>`
- **UseCase**: `GetMyContextUseCase`
- **Permission**: 인증 필요 (SecurityContext)

**설명**: 현재 로그인한 사용자의 테넌트, 조직, 역할, 권한 정보를 조회합니다.

---

### Q6. search (PermissionEndpoints)
- **Path**: `GET /api/v1/auth/permission-endpoints`
- **Controller**: `PermissionEndpointQueryController`
- **Request**: `SearchPermissionEndpointsApiRequest` (@ParameterObject)
- **Response**: `ApiResponse<PageApiResponse<PermissionEndpointApiResponse>>`
- **UseCase**: `SearchPermissionEndpointsUseCase`

**설명**: 권한 엔드포인트 매핑 목록을 검색합니다.

---

## Command Endpoints (명령)

| # | Method | Path | Controller | Method | UseCase |
|---|--------|------|------------|--------|---------|
| 1 | POST | /api/v1/auth/organizations | OrganizationCommandController | create | CreateOrganizationUseCase |
| 2 | PUT | /api/v1/auth/organizations/{organizationId}/name | OrganizationCommandController | updateName | UpdateOrganizationNameUseCase |
| 3 | PATCH | /api/v1/auth/organizations/{organizationId}/status | OrganizationCommandController | updateStatus | UpdateOrganizationStatusUseCase |
| 4 | POST | /api/v1/auth/users | UserCommandController | create | CreateUserUseCase |
| 5 | PUT | /api/v1/auth/users/{userId} | UserCommandController | update | UpdateUserUseCase |
| 6 | PUT | /api/v1/auth/users/{userId}/password | UserCommandController | changePassword | ChangePasswordUseCase |
| 7 | POST | /api/v1/auth/login | TokenCommandController | login | LoginUseCase |
| 8 | POST | /api/v1/auth/refresh | TokenCommandController | refresh | RefreshTokenUseCase |
| 9 | POST | /api/v1/auth/logout | TokenCommandController | logout | LogoutUseCase |
| 10 | POST | /api/v1/auth/permission-endpoints | PermissionEndpointCommandController | create | CreatePermissionEndpointUseCase |
| 11 | PUT | /api/v1/auth/permission-endpoints/{permissionEndpointId} | PermissionEndpointCommandController | update | UpdatePermissionEndpointUseCase |
| 12 | DELETE | /api/v1/auth/permission-endpoints/{permissionEndpointId} | PermissionEndpointCommandController | delete | DeletePermissionEndpointUseCase |
| 13 | POST | /api/v1/auth/roles/{roleId}/permissions | RolePermissionCommandController | grantPermissions | GrantRolePermissionUseCase |
| 14 | DELETE | /api/v1/auth/roles/{roleId}/permissions | RolePermissionCommandController | revokePermissions | RevokeRolePermissionUseCase |
| 15 | POST | /api/v1/auth/users/{userId}/roles | UserRoleCommandController | assignRoles | AssignUserRoleUseCase |
| 16 | DELETE | /api/v1/auth/users/{userId}/roles | UserRoleCommandController | revokeRoles | RevokeUserRoleUseCase |

---

### C1. create (Organization)
- **Path**: `POST /api/v1/auth/organizations`
- **Controller**: `OrganizationCommandController`
- **Request**: `CreateOrganizationApiRequest` (@RequestBody)
- **Response**: `ApiResponse<OrganizationIdApiResponse>` (201 Created)
- **UseCase**: `CreateOrganizationUseCase`
- **Permission**: `@PreAuthorize("@access.superAdmin() or @access.hasPermission('organization:create')")`

**설명**: 새로운 조직을 생성합니다.

---

### C2. updateName
- **Path**: `PUT /api/v1/auth/organizations/{organizationId}/name`
- **Controller**: `OrganizationCommandController`
- **Request**: `UpdateOrganizationNameApiRequest` (@RequestBody) + organizationId (Path)
- **Response**: `ApiResponse<OrganizationIdApiResponse>` (200 OK)
- **UseCase**: `UpdateOrganizationNameUseCase`
- **Permission**: `@PreAuthorize("@access.organization(#organizationId, 'update')")`

**설명**: 기존 조직의 이름을 수정합니다.

---

### C3. updateStatus
- **Path**: `PATCH /api/v1/auth/organizations/{organizationId}/status`
- **Controller**: `OrganizationCommandController`
- **Request**: `UpdateOrganizationStatusApiRequest` (@RequestBody) + organizationId (Path)
- **Response**: `ApiResponse<OrganizationIdApiResponse>` (200 OK)
- **UseCase**: `UpdateOrganizationStatusUseCase`
- **Permission**: `@PreAuthorize("@access.organization(#organizationId, 'update')")`

**설명**: 기존 조직의 상태를 수정합니다 (ACTIVE, INACTIVE, DELETED).

---

### C4. create (User)
- **Path**: `POST /api/v1/auth/users`
- **Controller**: `UserCommandController`
- **Request**: `CreateUserApiRequest` (@RequestBody)
- **Response**: `ApiResponse<UserIdApiResponse>` (201 Created)
- **UseCase**: `CreateUserUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'create')")`

**설명**: 새로운 사용자를 생성합니다.

---

### C5. update (User)
- **Path**: `PUT /api/v1/auth/users/{userId}`
- **Controller**: `UserCommandController`
- **Request**: `UpdateUserApiRequest` (@RequestBody) + userId (Path)
- **Response**: `ApiResponse<UserIdApiResponse>` (200 OK)
- **UseCase**: `UpdateUserUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'update') or @access.self(#userId)")`

**설명**: 기존 사용자의 정보를 수정합니다.

---

### C6. changePassword
- **Path**: `PUT /api/v1/auth/users/{userId}/password`
- **Controller**: `UserCommandController`
- **Request**: `ChangePasswordApiRequest` (@RequestBody) + userId (Path)
- **Response**: `ApiResponse<Void>` (200 OK)
- **UseCase**: `ChangePasswordUseCase`
- **Permission**: `@PreAuthorize("@access.self(#userId)")`

**설명**: 사용자의 비밀번호를 변경합니다.

---

### C7. login
- **Path**: `POST /api/v1/auth/login`
- **Controller**: `TokenCommandController`
- **Request**: `LoginApiRequest` (@RequestBody)
- **Response**: `ApiResponse<LoginApiResponse>` (201 Created)
- **UseCase**: `LoginUseCase`
- **Permission**: 없음 (Public)

**설명**: 이메일과 비밀번호로 로그인하여 Access Token과 Refresh Token을 발급받습니다.

---

### C8. refresh
- **Path**: `POST /api/v1/auth/refresh`
- **Controller**: `TokenCommandController`
- **Request**: `RefreshTokenApiRequest` (@RequestBody)
- **Response**: `ApiResponse<TokenApiResponse>` (200 OK)
- **UseCase**: `RefreshTokenUseCase`
- **Permission**: 없음 (Public)

**설명**: Refresh Token으로 새로운 Access Token과 Refresh Token을 발급받습니다.

---

### C9. logout
- **Path**: `POST /api/v1/auth/logout`
- **Controller**: `TokenCommandController`
- **Request**: `LogoutApiRequest` (@RequestBody)
- **Response**: `ApiResponse<Void>` (200 OK)
- **UseCase**: `LogoutUseCase`
- **Permission**: 인증 필요

**설명**: 현재 세션의 Refresh Token을 무효화하여 로그아웃합니다.

---

### C10. create (PermissionEndpoint)
- **Path**: `POST /api/v1/auth/permission-endpoints`
- **Controller**: `PermissionEndpointCommandController`
- **Request**: `CreatePermissionEndpointApiRequest` (@RequestBody)
- **Response**: `ApiResponse<Long>` (201 Created)
- **UseCase**: `CreatePermissionEndpointUseCase`

**설명**: 새로운 권한 엔드포인트 매핑을 생성합니다.

---

### C11. update (PermissionEndpoint)
- **Path**: `PUT /api/v1/auth/permission-endpoints/{permissionEndpointId}`
- **Controller**: `PermissionEndpointCommandController`
- **Request**: `UpdatePermissionEndpointApiRequest` (@RequestBody) + permissionEndpointId (Path)
- **Response**: `ApiResponse<Void>` (200 OK)
- **UseCase**: `UpdatePermissionEndpointUseCase`

**설명**: 기존 권한 엔드포인트 매핑을 수정합니다.

---

### C12. delete (PermissionEndpoint)
- **Path**: `DELETE /api/v1/auth/permission-endpoints/{permissionEndpointId}`
- **Controller**: `PermissionEndpointCommandController`
- **Request**: permissionEndpointId (Path)
- **Response**: `ApiResponse<Void>` (200 OK)
- **UseCase**: `DeletePermissionEndpointUseCase`

**설명**: 권한 엔드포인트 매핑을 삭제합니다.

---

### C13. grantPermissions
- **Path**: `POST /api/v1/auth/roles/{roleId}/permissions`
- **Controller**: `RolePermissionCommandController`
- **Request**: `GrantRolePermissionApiRequest` (@RequestBody) + roleId (Path)
- **Response**: `ApiResponse<Void>` (201 Created)
- **UseCase**: `GrantRolePermissionUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('role', 'update')")`

**설명**: 지정한 역할에 하나 이상의 권한을 부여합니다.

---

### C14. revokePermissions
- **Path**: `DELETE /api/v1/auth/roles/{roleId}/permissions`
- **Controller**: `RolePermissionCommandController`
- **Request**: `RevokeRolePermissionApiRequest` (@RequestBody) + roleId (Path)
- **Response**: `Void` (204 No Content)
- **UseCase**: `RevokeRolePermissionUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('role', 'update')")`

**설명**: 지정한 역할에서 하나 이상의 권한을 제거합니다.

---

### C15. assignRoles
- **Path**: `POST /api/v1/auth/users/{userId}/roles`
- **Controller**: `UserRoleCommandController`
- **Request**: `AssignUserRoleApiRequest` (@RequestBody) + userId (Path)
- **Response**: `ApiResponse<Void>` (201 Created)
- **UseCase**: `AssignUserRoleUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'update')")`

**설명**: 지정한 사용자에게 하나 이상의 역할을 할당합니다.

---

### C16. revokeRoles
- **Path**: `DELETE /api/v1/auth/users/{userId}/roles`
- **Controller**: `UserRoleCommandController`
- **Request**: `RevokeUserRoleApiRequest` (@RequestBody) + userId (Path)
- **Response**: `Void` (204 No Content)
- **UseCase**: `RevokeUserRoleUseCase`
- **Permission**: `@PreAuthorize("@access.hasPermission('user', 'update')")`

**설명**: 지정한 사용자로부터 하나 이상의 역할을 철회합니다.

---

## Internal Endpoints (내부 API)

| # | Method | Path | Controller | Method | UseCase |
|---|--------|------|------------|--------|---------|
| 1 | POST | /api/v1/internal/endpoints/sync | InternalEndpointSyncController | syncEndpoints | SyncEndpointsUseCase |
| 2 | POST | /api/v1/internal/onboarding | InternalOnboardingController | onboarding | OnboardingUseCase |
| 3 | POST | /api/v1/internal/users/register | InternalUserCommandController | register | CreateUserWithRolesUseCase |

---

### I1. syncEndpoints
- **Path**: `POST /api/v1/internal/endpoints/sync`
- **Controller**: `InternalEndpointSyncController`
- **Request**: `EndpointSyncApiRequest` (@RequestBody)
- **Response**: `ApiResponse<EndpointSyncResultApiResponse>` (200 OK)
- **UseCase**: `SyncEndpointsUseCase`
- **Permission**: 서비스 토큰 인증 필요

**설명**: 다른 서비스의 @RequirePermission 어노테이션이 붙은 엔드포인트를 AuthHub에 동기화합니다.

**보안 참고**:
- 서비스 토큰 인증으로 보호
- 내부 네트워크에서만 접근 가능

---

### I2. onboarding
- **Path**: `POST /api/v1/internal/onboarding`
- **Controller**: `InternalOnboardingController`
- **Request**:
  - Header: `X-Idempotency-Key` (필수, UUID 권장)
  - Body: `OnboardingApiRequest` (@RequestBody)
- **Response**: `ApiResponse<OnboardingResultApiResponse>` (200 OK)
- **UseCase**: `OnboardingUseCase`
- **Permission**: 서비스 토큰 인증 필요

**설명**: 테넌트와 조직을 하나의 트랜잭션으로 생성합니다.

**멱등키 지원**:
- X-Idempotency-Key 헤더로 멱등키 전송
- 동일한 멱등키로 요청 시 캐시된 응답 반환 (24시간 유지)
- 네트워크 오류 등으로 인한 재시도 시 중복 생성 방지

**보안 참고**:
- 서비스 토큰 인증으로 보호
- 내부 네트워크에서만 접근 가능

---

### I3. register
- **Path**: `POST /api/v1/internal/users/register`
- **Controller**: `InternalUserCommandController`
- **Request**: `CreateUserWithRolesApiRequest` (@RequestBody)
- **Response**: `ApiResponse<CreateUserWithRolesResultApiResponse>` (200 OK)
- **UseCase**: `CreateUserWithRolesUseCase`
- **Permission**: 서비스 토큰 인증 필요

**설명**: 사용자를 생성하고 선택적으로 SERVICE scope Role을 할당합니다. 사용자 생성 + 역할 할당을 하나의 트랜잭션으로 처리합니다.

**보안 참고**:
- 서비스 토큰 인증으로 보호
- 내부 네트워크에서만 접근 가능

---

## 도메인별 엔드포인트 분류

### Organization (조직 관리)
- **Query**: 1개 (목록 조회)
- **Command**: 3개 (생성, 이름 수정, 상태 수정)

### User (사용자 관리)
- **Query**: 2개 (목록 조회, 단건 조회)
- **Command**: 3개 (생성, 수정, 비밀번호 변경)

### Token (인증/토큰 관리)
- **Query**: 2개 (공개키 조회, 내 정보 조회)
- **Command**: 3개 (로그인, 토큰 갱신, 로그아웃)

### Permission Endpoint (권한-엔드포인트 매핑)
- **Query**: 1개 (목록 검색)
- **Command**: 3개 (생성, 수정, 삭제)

### Role Permission (역할-권한 관계)
- **Query**: 0개
- **Command**: 2개 (권한 부여, 권한 제거)

### User Role (사용자-역할 관계)
- **Query**: 0개
- **Command**: 2개 (역할 할당, 역할 철회)

### Internal (내부 API)
- **Command**: 3개 (엔드포인트 동기화, 온보딩, 사용자 등록)

---

## HTTP Method별 분류

| Method | 개수 | 비율 |
|--------|------|------|
| GET | 6개 | 18.8% |
| POST | 14개 | 43.8% |
| PUT | 5개 | 15.6% |
| PATCH | 1개 | 3.1% |
| DELETE | 6개 | 18.8% |

---

## 아키텍처 특징

### Hexagonal Architecture
- **Controller → UseCase** 단일 의존성
- Thin Controller (비즈니스 로직 없음)
- Query/Command 명확히 분리

### Security
- `@PreAuthorize` 어노테이션 기반 권한 제어
- `@access.hasPermission()` 커스텀 권한 검사
- `@access.self()` 본인 확인
- `@access.superAdmin()` 슈퍼 관리자 확인
- Internal API는 서비스 토큰 인증

### API 설계 원칙
- RESTful 설계
- 일관된 응답 구조 (`ApiResponse<T>`)
- 페이징 지원 (`PageApiResponse<T>`)
- `@Valid` 검증 필수
- HTTP Status Code 명확히 구분 (200, 201, 204, 400, 401, 404, 409)

---

## 다음 단계

엔드포인트 분류 완료 후 API 플로우 분석:

```bash
# 특정 도메인의 API 플로우 분석
/api-flow organization --all
/api-flow user --all
/api-flow token --all
```

## 통합 테스트 파이프라인

```
/api-endpoints (완료) → /api-flow → /test-scenario → /test-e2e
```
