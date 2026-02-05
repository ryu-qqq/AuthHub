# API Flow: InternalUserPermissionController.getPermissions

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | GET |
| Path | `/api/v1/internal/users/{userId}/permissions` |
| Controller | InternalUserPermissionController |
| Method | getPermissions |
| UseCase | GetUserPermissionsUseCase |
| Service | GetUserPermissionsService |
| 목적 | Gateway가 사용자 인가 검증을 위해 역할/권한 정보 조회 |

---

## 호출 흐름 다이어그램

```
InternalUserPermissionController.getPermissions(String userId)
  │
  ├─ GetUserPermissionsUseCase.getByUserId(userId)                    [Port-In]
  │   │
  │   └─ GetUserPermissionsService.getByUserId(userId)                [Service]
  │       │
  │       ├─ UserRoleReadFacade.findRolesAndPermissionsByUserId(UserId)
  │       │   │
  │       │   ├─ 1) UserRoleReadManager.findAllByUserId(UserId)
  │       │   │   └─ UserRoleQueryPort.findAllByUserId(UserId)        [Port-Out]
  │       │   │       └─ UserRoleQueryAdapter.findAllByUserId(UserId)
  │       │   │           └─ UserRoleQueryDslRepository.findAllByUserId(String)
  │       │   │               └─ SELECT * FROM user_roles WHERE user_id = ?
  │       │   │
  │       │   ├─ 2) RoleReadManager.findAllByIds(List<RoleId>)
  │       │   │   └─ RoleQueryPort.findAllByIds(List<RoleId>)         [Port-Out]
  │       │   │       └─ RoleQueryAdapter.findAllByIds(List<RoleId>)
  │       │   │           └─ RoleQueryDslRepository.findAllByIds(List<Long>)
  │       │   │               └─ SELECT * FROM roles WHERE role_id IN (...)
  │       │   │
  │       │   ├─ 3) RolePermissionReadManager.findAllByRoleIds(List<RoleId>)
  │       │   │   └─ RolePermissionQueryPort.findAllByRoleIds(List<RoleId>) [Port-Out]
  │       │   │       └─ RolePermissionQueryAdapter.findAllByRoleIds(...)
  │       │   │           └─ RolePermissionQueryDslRepository.findAllByRoleIds(...)
  │       │   │               └─ SELECT * FROM role_permissions WHERE role_id IN (...)
  │       │   │
  │       │   └─ 4) PermissionReadManager.findAllByIds(List<PermissionId>)
  │       │       └─ PermissionQueryPort.findAllByIds(List<PermissionId>) [Port-Out]
  │       │           └─ PermissionQueryAdapter.findAllByIds(...)
  │       │               └─ PermissionQueryDslRepository.findAllByIds(...)
  │       │                   └─ SELECT * FROM permissions WHERE permission_id IN (...)
  │       │
  │       └─ new UserPermissionsResult(userId, roleNames, permissionKeys)
  │
  ├─ InternalUserPermissionApiMapper.toApiResponse(result)
  │   └─ new UserPermissionsApiResponse(userId, roles, permissions)
  │
  └─ ApiResponse.ofSuccess(response)
```

---

## Layer별 상세 분석

### 1. Adapter-In Layer

#### 1.1 Controller

**위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/controller/InternalUserPermissionController.java`

**역할**: Gateway용 Internal API 엔드포인트 제공 (서비스 토큰 인증 보호)

```java
@RestController
@RequestMapping("/api/v1/internal/users")
public class InternalUserPermissionController {

    private final GetUserPermissionsUseCase getUserPermissionsUseCase;
    private final InternalUserPermissionApiMapper mapper;

    @GetMapping("/{userId}/permissions")
    public ApiResponse<UserPermissionsApiResponse> getPermissions(@PathVariable String userId) {
        // 1. UseCase 호출
        UserPermissionsResult result = getUserPermissionsUseCase.getByUserId(userId);

        // 2. API 응답 변환
        UserPermissionsApiResponse response = mapper.toApiResponse(result);

        // 3. 공통 응답 래핑
        return ApiResponse.ofSuccess(response);
    }
}
```

**주요 특징**:
- 서비스 토큰 인증 필요 (내부 네트워크 전용)
- Path Variable로 userId(String) 수신
- 단순 위임 패턴 (비즈니스 로직 없음)

#### 1.2 Response DTO

**위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/UserPermissionsApiResponse.java`

```java
@Schema(description = "사용자 권한 정보")
public record UserPermissionsApiResponse(
    @Schema(description = "사용자 ID", example = "01234567-89ab-cdef-0123-456789abcdef")
    String userId,

    @Schema(description = "역할 이름 목록", example = "[\"ADMIN\", \"USER\"]")
    Set<String> roles,

    @Schema(description = "권한 키 목록", example = "[\"user:read\", \"user:write\"]")
    Set<String> permissions
) {}
```

**필드 구조**:
- `userId`: 사용자 UUID (String)
- `roles`: 역할 이름 Set (예: "ADMIN", "USER")
- `permissions`: 권한 키 Set (예: "user:read", "user:write")

#### 1.3 ApiMapper

**위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/mapper/InternalUserPermissionApiMapper.java`

```java
@Component
public class InternalUserPermissionApiMapper {

    public UserPermissionsApiResponse toApiResponse(UserPermissionsResult result) {
        return new UserPermissionsApiResponse(
            result.userId(),
            result.roles(),
            result.permissions()
        );
    }
}
```

**변환 전략**: Application Result → API Response (1:1 필드 매핑)

---

### 2. Application Layer

#### 2.1 UseCase (Port-In)

**위치**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/port/in/query/GetUserPermissionsUseCase.java`

```java
public interface GetUserPermissionsUseCase {

    /**
     * 사용자 ID로 역할/권한 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 역할/권한 정보
     */
    UserPermissionsResult getByUserId(String userId);
}
```

**계약**:
- 입력: `String userId` (UUIDv7)
- 출력: `UserPermissionsResult` (roles + permissions)
- 예외: 존재하지 않는 사용자인 경우 빈 역할/권한 반환

#### 2.2 Service (Implementation)

**위치**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/service/query/GetUserPermissionsService.java`

```java
@Service
public class GetUserPermissionsService implements GetUserPermissionsUseCase {

    private final UserRoleReadFacade userRoleReadFacade;

    @Override
    public UserPermissionsResult getByUserId(String userId) {
        // 1. Facade를 통해 역할 + 권한 조회
        RolesAndPermissionsComposite composite =
            userRoleReadFacade.findRolesAndPermissionsByUserId(UserId.of(userId));

        // 2. Result DTO 생성
        return new UserPermissionsResult(
            userId,
            composite.roleNames(),
            composite.permissionKeys()
        );
    }
}
```

**책임**:
- String userId → UserId VO 변환
- Facade 조율 (단일 호출)
- Composite → Result 변환

#### 2.3 Result DTO

**위치**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/dto/response/UserPermissionsResult.java`

```java
public record UserPermissionsResult(
    String userId,
    Set<String> roles,
    Set<String> permissions
) {
    public static UserPermissionsResult empty(String userId) {
        return new UserPermissionsResult(userId, Set.of(), Set.of());
    }
}
```

**특징**:
- Record 타입 (불변)
- Domain 타입 의존 금지
- `empty()` 팩토리 메서드 제공

#### 2.4 Facade (조율 계층)

**위치**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/facade/UserRoleReadFacade.java`

```java
@Component
public class UserRoleReadFacade {

    private final UserRoleReadManager userRoleReadManager;
    private final RoleReadManager roleReadManager;
    private final RolePermissionReadManager rolePermissionReadManager;
    private final PermissionReadManager permissionReadManager;

    public RolesAndPermissionsComposite findRolesAndPermissionsByUserId(UserId userId) {
        // 1. 사용자의 역할 관계 조회
        List<UserRole> userRoles = userRoleReadManager.findAllByUserId(userId);

        if (userRoles.isEmpty()) {
            return RolesAndPermissionsComposite.empty();
        }

        // 2. roleId 목록 추출
        List<RoleId> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .toList();

        // 3. 역할 이름 조회 (IN절 - N+1 방지)
        Set<String> roleNames = findRoleNames(roleIds);

        // 4. 권한 키 조회 (IN절 - N+1 방지)
        Set<String> permissionKeys = findPermissionKeys(roleIds);

        return new RolesAndPermissionsComposite(roleNames, permissionKeys);
    }

    private Set<String> findRoleNames(List<RoleId> roleIds) {
        List<Role> roles = roleReadManager.findAllByIds(roleIds);
        return roles.stream()
            .filter(Role::isActive)
            .map(Role::nameValue)
            .collect(Collectors.toSet());
    }

    private Set<String> findPermissionKeys(List<RoleId> roleIds) {
        // 1. RolePermission 조회 (IN절)
        List<RolePermission> rolePermissions =
            rolePermissionReadManager.findAllByRoleIds(roleIds);

        if (rolePermissions.isEmpty()) {
            return Set.of();
        }

        // 2. permissionId 목록 추출
        List<PermissionId> permissionIds = rolePermissions.stream()
            .map(RolePermission::getPermissionId)
            .distinct()
            .toList();

        // 3. Permission 조회 (IN절)
        List<Permission> permissions = permissionReadManager.findAllByIds(permissionIds);

        return permissions.stream()
            .filter(Permission::isActive)
            .map(Permission::permissionKeyValue)
            .collect(Collectors.toSet());
    }
}
```

**조회 흐름**:
1. `UserRole` 목록 조회 → `roleId` 목록 추출
2. `Role` 조회 (IN절) → Active 필터링 → `roleName` 추출
3. `RolePermission` 조회 (IN절) → `permissionId` 목록 추출
4. `Permission` 조회 (IN절) → Active 필터링 → `permissionKey` 추출

**성능 최적화**:
- **IN절 사용**: N+1 문제 방지 (4번의 쿼리로 완료)
- **Active 필터링**: isActive() 도메인 로직 적용
- **중복 제거**: distinct() + Set 사용

---

### 3. Domain Layer

#### 3.1 Domain Port (Query Port)

**위치**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/port/out/query/UserRoleQueryPort.java`

```java
public interface UserRoleQueryPort {

    /**
     * 사용자의 역할 목록 조회
     */
    List<UserRole> findAllByUserId(UserId userId);

    // ... 기타 조회 메서드
}
```

**Port 설계 원칙**:
- Value Object 파라미터 (원시 타입 금지)
- Domain 반환 (DTO/Entity 반환 금지)
- 조회 메서드만 제공 (저장/삭제 금지)

**관련 Port**:
- `RoleQueryPort.findAllByIds(List<RoleId>)` - 역할 다건 조회
- `RolePermissionQueryPort.findAllByRoleIds(List<RoleId>)` - 역할-권한 관계 조회
- `PermissionQueryPort.findAllByIds(List<PermissionId>)` - 권한 다건 조회

#### 3.2 Domain Model

**UserRole Aggregate**

**위치**: `/domain/src/main/java/com/ryuqq/authhub/domain/userrole/aggregate/UserRole.java`

```java
public final class UserRole {

    private final UserRoleId userRoleId;  // PK (Long)
    private final UserId userId;          // FK (String - UUIDv7)
    private final RoleId roleId;          // FK (Long)
    private final Instant createdAt;

    // Factory Methods
    public static UserRole create(UserId userId, RoleId roleId, Instant now) {
        return new UserRole(null, userId, roleId, now);
    }

    public static UserRole reconstitute(
        UserRoleId userRoleId, UserId userId, RoleId roleId, Instant createdAt
    ) {
        return new UserRole(userRoleId, userId, roleId, createdAt);
    }

    // Getters
    public UserId getUserId() { return userId; }
    public RoleId getRoleId() { return roleId; }

    // Query Methods
    public Long userRoleIdValue() {
        return userRoleId != null ? userRoleId.value() : null;
    }
    public String userIdValue() { return userId.value(); }
    public Long roleIdValue() { return roleId.value(); }
    public boolean isNew() { return userRoleId == null; }
}
```

**특징**:
- **관계 테이블**: User ↔ Role 다대다 관계
- **Hard Delete**: Soft Delete 미적용 (관계 테이블)
- **Value Object 기반**: UserId(String), RoleId(Long)
- **Immutable**: final 필드, Setter 없음

---

### 4. Adapter-Out Layer

#### 4.1 Adapter (Port 구현체)

**위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/userrole/adapter/UserRoleQueryAdapter.java`

```java
@Component
public class UserRoleQueryAdapter implements UserRoleQueryPort {

    private final UserRoleQueryDslRepository repository;
    private final UserRoleJpaEntityMapper mapper;

    @Override
    public List<UserRole> findAllByUserId(UserId userId) {
        return repository.findAllByUserId(userId.value())
            .stream()
            .map(mapper::toDomain)
            .toList();
    }
}
```

**책임**:
- Value Object → 원시 타입 변환 (`userId.value()`)
- Entity → Domain 변환 (`mapper::toDomain`)
- Repository 위임 (단순 위임 + 변환)

**원칙**:
- @Transactional 금지 (Manager에서 관리)
- 비즈니스 로직 금지
- 1:1 매핑 (1 Repository + 1 Mapper)

#### 4.2 Repository (QueryDSL)

**위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/userrole/repository/UserRoleQueryDslRepository.java`

```java
@Repository
public class UserRoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final UserRoleConditionBuilder conditionBuilder;

    public List<UserRoleJpaEntity> findAllByUserId(String userId) {
        return queryFactory
            .selectFrom(userRoleJpaEntity)
            .where(userRoleJpaEntity.userId.eq(userId))
            .orderBy(userRoleJpaEntity.createdAt.desc())
            .fetch();
    }
}
```

**쿼리**:
```sql
SELECT *
FROM user_roles
WHERE user_id = ?
ORDER BY created_at DESC
```

**CQRS 패턴**:
- **Command**: `UserRoleJpaRepository` (JPA - 저장/삭제)
- **Query**: `UserRoleQueryDslRepository` (QueryDSL - 조회)

#### 4.3 JPA Entity

**위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/userrole/entity/UserRoleJpaEntity.java`

```java
@Entity
@Table(
    name = "user_roles",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_roles_user_role",
        columnNames = {"user_id", "role_id"}
    ),
    indexes = {
        @Index(name = "idx_user_roles_user_id", columnList = "user_id"),
        @Index(name = "idx_user_roles_role_id", columnList = "role_id")
    }
)
public class UserRoleJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id", nullable = false)
    private Long userRoleId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;  // String FK 전략

    @Column(name = "role_id", nullable = false)
    private Long roleId;    // Long FK 전략

    // Getters only (No Setters)
    public Long getUserRoleId() { return userRoleId; }
    public String getUserId() { return userId; }
    public Long getRoleId() { return roleId; }
}
```

**테이블 매핑**:
- **테이블명**: `user_roles`
- **PK**: `user_role_id` (Long, Auto Increment)
- **FK**: `user_id` (String), `role_id` (Long)
- **유니크 제약**: `(user_id, role_id)` - 중복 할당 방지
- **인덱스**: `user_id`, `role_id` - 조회 성능 최적화

**FK 전략**:
- JPA 관계 어노테이션 금지 (@ManyToOne, @JoinColumn 없음)
- 원시 타입 필드로 FK 관리 (String userId, Long roleId)

---

## Database Query 분석

### 쿼리 실행 순서

#### 1. UserRole 조회
```sql
-- UserRoleQueryDslRepository.findAllByUserId(userId)
SELECT *
FROM user_roles
WHERE user_id = '01234567-89ab-cdef-0123-456789abcdef'
ORDER BY created_at DESC;
```

#### 2. Role 조회 (IN절)
```sql
-- RoleQueryDslRepository.findAllByIds(roleIds)
SELECT *
FROM roles
WHERE role_id IN (1, 2, 3)
  AND deleted = false;
```

#### 3. RolePermission 조회 (IN절)
```sql
-- RolePermissionQueryDslRepository.findAllByRoleIds(roleIds)
SELECT *
FROM role_permissions
WHERE role_id IN (1, 2, 3);
```

#### 4. Permission 조회 (IN절)
```sql
-- PermissionQueryDslRepository.findAllByIds(permissionIds)
SELECT *
FROM permissions
WHERE permission_id IN (10, 11, 12, 13)
  AND deleted = false;
```

### 성능 특성

**총 쿼리 수**: 4개 (N+1 문제 없음)

**인덱스 활용**:
- `user_roles.user_id` → 인덱스 사용
- `roles.role_id` → PK 인덱스
- `role_permissions.role_id` → 인덱스 사용
- `permissions.permission_id` → PK 인덱스

**예상 성능**:
- 사용자당 역할 2~5개: 매우 빠름
- 역할당 권한 5~20개: 빠름
- 총 실행 시간: < 50ms (일반적인 경우)

---

## 트랜잭션 경계

### Application Layer

```java
// UserRoleReadManager
@Transactional(readOnly = true)
public List<UserRole> findAllByUserId(UserId userId) {
    return queryPort.findAllByUserId(userId);
}

// RoleReadManager
@Transactional(readOnly = true)
public List<Role> findAllByIds(List<RoleId> ids) {
    return queryPort.findAllByIds(ids);
}

// ... 기타 Manager도 동일
```

**트랜잭션 전략**:
- **ReadOnly Transaction**: 모든 조회는 `@Transactional(readOnly = true)`
- **Manager 단위**: 각 Manager가 독립적인 트랜잭션 관리
- **Facade는 트랜잭션 없음**: Facade는 여러 Manager를 조율만 함

---

## 데이터 변환 흐름

### Entity → Domain → DTO → Response

```
[Adapter-Out]
UserRoleJpaEntity (userId: String, roleId: Long)
  ↓ UserRoleJpaEntityMapper.toDomain()

[Domain]
UserRole (userId: UserId, roleId: RoleId)
  ↓ Facade 조율

[Application]
RolesAndPermissionsComposite (roleNames: Set<String>, permissionKeys: Set<String>)
  ↓ Service 변환
UserPermissionsResult (userId: String, roles: Set<String>, permissions: Set<String>)
  ↓ ApiMapper.toApiResponse()

[Adapter-In]
UserPermissionsApiResponse (userId: String, roles: Set<String>, permissions: Set<String>)
  ↓ ApiResponse.ofSuccess()
ApiResponse<UserPermissionsApiResponse>
```

---

## 예외 처리

### 정상 케이스
```json
{
  "success": true,
  "data": {
    "userId": "01234567-89ab-cdef-0123-456789abcdef",
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write", "admin:manage"]
  },
  "error": null
}
```

### 빈 역할/권한 케이스
```json
{
  "success": true,
  "data": {
    "userId": "01234567-89ab-cdef-0123-456789abcdef",
    "roles": [],
    "permissions": []
  },
  "error": null
}
```

**특징**:
- 존재하지 않는 사용자도 빈 Set 반환 (예외 발생 안 함)
- Gateway가 빈 권한으로 인가 실패 처리

---

## 아키텍처 패턴 적용

### Hexagonal Architecture

```
[Port-In] GetUserPermissionsUseCase
           ↓
[Service] GetUserPermissionsService
           ↓
[Facade] UserRoleReadFacade (조율 계층)
           ↓
[Manager] UserRoleReadManager, RoleReadManager, ...
           ↓
[Port-Out] UserRoleQueryPort, RoleQueryPort, ...
           ↓
[Adapter] UserRoleQueryAdapter, RoleQueryAdapter, ...
           ↓
[Repository] UserRoleQueryDslRepository, ...
           ↓
[Entity] UserRoleJpaEntity, RoleJpaEntity, ...
```

### CQRS 패턴

**Query Path** (현재 API):
- `UserRoleQueryPort` → `UserRoleQueryAdapter` → `UserRoleQueryDslRepository`

**Command Path** (참고):
- `UserRolePersistencePort` → `UserRolePersistenceAdapter` → `UserRoleJpaRepository`

### Facade 패턴

**UserRoleReadFacade 역할**:
- 4개의 ReadManager 조율
- N+1 방지를 위한 IN절 조회 최적화
- Active 필터링 + Set 변환

---

## 핵심 설계 원칙

### 1. Long FK 전략
- JPA 관계 어노테이션 금지
- 원시 타입 FK 필드 (String userId, Long roleId)
- Value Object로 Domain 레벨 타입 안전성 확보

### 2. N+1 문제 방지
- IN절 사용 (4개 쿼리로 완료)
- 한 번에 모든 ID 조회 → 다건 조회

### 3. Active 필터링
- Domain에서 `isActive()` 검증
- 비활성화된 역할/권한 제외

### 4. ReadManager 패턴
- @Component (not @Service)
- @Transactional(readOnly = true) 메서드 단위
- QueryPort만 의존
- 비즈니스 로직 금지

### 5. Facade 패턴
- 여러 Manager 조율
- 트랜잭션 없음 (Manager가 관리)
- IN절 최적화 로직 포함

---

## 성능 고려사항

### 강점
- **IN절 최적화**: N+1 문제 완전 제거
- **인덱스 활용**: 모든 FK 필드 인덱스 보유
- **ReadOnly Transaction**: DB 부하 최소화

### 개선 가능 영역
- **캐싱 전략**: Redis를 통한 역할/권한 캐싱 (userId → roles + permissions)
- **Batch Size 제한**: IN절 크기 제한 (1000개 이상 시 분할)
- **Projection 쿼리**: 필요한 필드만 조회 (SELECT role_name, permission_key)

---

## 보안 고려사항

### 인증
- 서비스 토큰 인증 필요
- 내부 네트워크 전용 (외부 접근 차단)

### 인가
- Gateway가 응답 데이터로 인가 검증
- 역할/권한 기반 접근 제어 (RBAC)

### 데이터 보호
- 민감 정보 없음 (userId, roleName, permissionKey만 반환)
- 비활성화된 역할/권한 자동 필터링

---

## 테스트 전략

### Unit Test 레이어
1. **Service**: Facade Mock → Result 검증
2. **Facade**: Manager Mock → Composite 검증
3. **Manager**: Port Mock → Domain 검증
4. **Adapter**: Repository Mock → Entity → Domain 변환 검증

### Integration Test
- Controller → UseCase → Facade → Manager → QueryPort → DB
- 실제 DB 연동 테스트 (TestContainers)
- N+1 문제 검증 (쿼리 카운트 확인)

### E2E Test
- Gateway → Internal API 호출
- 서비스 토큰 인증 검증
- 실제 역할/권한 기반 인가 시나리오
