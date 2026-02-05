# API Flow: InternalEndpointSyncController.syncEndpoints

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | POST |
| Path | /api/v1/internal/endpoints/sync |
| Controller | InternalEndpointSyncController |
| Method | syncEndpoints |
| UseCase | SyncEndpointsUseCase |
| Service | SyncEndpointsService |
| Coordinator | EndpointSyncCoordinator |

---

## 호출 흐름 다이어그램

```
InternalEndpointSyncController.syncEndpoints(@Valid @RequestBody EndpointSyncApiRequest)
  |
  |- InternalEndpointSyncApiMapper.toCommand(request)
  |   +-- -> SyncEndpointsCommand(serviceName, serviceCode, List<EndpointSyncItem>)
  |
  |- SyncEndpointsUseCase.sync(command)                    [Port-In Interface]
  |   +-- SyncEndpointsService.sync(command)               [Implementation]
  |       |
  |       +-- EndpointSyncCoordinator.coordinate(command)  [@Transactional]
  |           |
  |           |-- 0. serviceCode → ServiceId 리졸브
  |           |   +-- ServiceReadManager.findByCodeOptional(ServiceCode)
  |           |       +-- ServiceQueryPort.findByCode()
  |           |
  |           |-- 1. Permission 동기화 (IN절 조회 + 필터링 + 저장)
  |           |   |- PermissionReadManager.findAllByPermissionKeys(List<String>)
  |           |   |   +-- PermissionQueryPort.findAllByPermissionKeys()
  |           |   |       +-- PermissionQueryAdapter.findAllByPermissionKeys()
  |           |   |           +-- PermissionQueryDslRepository.findAllByPermissionKeys()
  |           |   |               +-- QueryDSL: WHERE permission_key IN (...)
  |           |   |
  |           |   |- 누락된 permissionKey 필터링
  |           |   |
  |           |   |- EndpointSyncCommandFactory.createMissingPermissions()
  |           |   |   +-- Permission.createCustom(serviceId, resource, action, description, now)
  |           |   |
  |           |   +-- PermissionCommandManager.persistAllAndReturnKeyToIdMap()
  |           |       +-- PermissionCommandPort.persist() (반복)
  |           |           +-- PermissionCommandAdapter.persist()
  |           |               +-- PermissionJpaRepository.save(PermissionJpaEntity)
  |           |
  |           |-- 2. PermissionEndpoint 동기화 (IN절 조회 + 필터링 + 저장)
  |           |   |- PermissionEndpointReadManager.findAllByUrlPatterns(List<String>)
  |           |   |   +-- PermissionEndpointQueryPort.findAllByUrlPatterns()
  |           |   |       +-- PermissionEndpointQueryAdapter.findAllByUrlPatterns()
  |           |   |           +-- PermissionEndpointQueryDslRepository.findAllByUrlPatterns()
  |           |   |               +-- QueryDSL: WHERE url_pattern IN (...)
  |           |   |
  |           |   |- 서비스명 + URL + HTTP Method 조합으로 기존 엔드포인트 필터링
  |           |   |
  |           |   |- EndpointSyncCommandFactory.createMissingEndpoints()
  |           |   |   +-- PermissionEndpoint.create(permissionId, serviceName, urlPattern, httpMethod, ...)
  |           |   |
  |           |   +-- PermissionEndpointCommandManager.persistAll()
  |           |       +-- PermissionEndpointCommandPort.persist() (반복)
  |           |           +-- PermissionEndpointCommandAdapter.persist()
  |           |               +-- PermissionEndpointJpaRepository.save(PermissionEndpointJpaEntity)
  |           |
  |           +-- 3. 자동 Role-Permission 매핑 (SERVICE scope 기본 Role)
  |               |- RoleReadManager.findByTenantIdAndServiceIdAndName(null, serviceId, "ADMIN/EDITOR/VIEWER")
  |               |   +-- RoleQueryPort.findByTenantIdAndServiceIdAndName()
  |               |
  |               |- action 패턴에 따라 매핑 대상 Role 결정:
  |               |   - read/list/search/get → ADMIN, EDITOR, VIEWER
  |               |   - create/update/write/edit → ADMIN, EDITOR
  |               |   - delete/기타 → ADMIN only
  |               |
  |               |- RolePermission.create(RoleId, PermissionId, now) (반복)
  |               |
  |               |- 중복 체크 (이미 매핑된 것 제외)
  |               |   +-- RolePermissionReadManager.findGrantedPermissionIds(RoleId, List<PermissionId>)
  |               |
  |               +-- RolePermissionCommandManager.persistAll()
  |                   +-- RolePermissionCommandPort.persist() (반복)
  |
  +-- InternalEndpointSyncApiMapper.toApiResponse(result)
      +-- -> ApiResponse<EndpointSyncResultApiResponse>
```

---

## Layer별 상세

### 1. Adapter-In Layer

#### 1.1 Controller
- **파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/controller/InternalEndpointSyncController.java`
- **메서드**: `syncEndpoints(@Valid @RequestBody EndpointSyncApiRequest request)`
- **어노테이션**:
  - `@RestController`
  - `@RequestMapping(ENDPOINTS)` → `/api/v1/internal/endpoints`
  - `@PostMapping(ENDPOINTS_SYNC)` → `/sync`
  - `@Operation(summary = "엔드포인트 동기화")`
- **의존성**:
  - `SyncEndpointsUseCase syncEndpointsUseCase`
  - `InternalEndpointSyncApiMapper mapper`

#### 1.2 Request DTO
- **파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/command/EndpointSyncApiRequest.java`
- **타입**: Record
- **필드**:
  ```java
  @NotBlank String serviceName         // 서비스 이름 (예: "marketplace")
  String serviceCode                   // 서비스 코드 (optional, 예: "SVC_MARKETPLACE")
  @NotEmpty @Valid List<EndpointInfoApiRequest> endpoints
  ```
- **중첩 Record**: `EndpointInfoApiRequest`
  ```java
  @NotBlank String httpMethod          // HTTP 메서드 (예: "POST")
  @NotBlank String pathPattern         // URL 패턴 (예: "/api/v1/products")
  @NotBlank String permissionKey       // 권한 키 (예: "product:create")
  String description                   // 설명 (optional)
  ```

#### 1.3 Response DTO
- **파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/EndpointSyncResultApiResponse.java`
- **타입**: Record
- **필드**:
  ```java
  String serviceName                   // 서비스 이름
  int totalEndpoints                   // 전체 엔드포인트 수
  int createdPermissions               // 생성된 권한 수
  int createdEndpoints                 // 생성된 엔드포인트 수
  int skippedEndpoints                 // 스킵된 엔드포인트 수 (이미 존재)
  int mappedRolePermissions            // 자동 매핑된 Role-Permission 수
  ```

#### 1.4 ApiMapper
- **파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/mapper/InternalEndpointSyncApiMapper.java`
- **매핑 로직**:
  - `toCommand(EndpointSyncApiRequest)` → `SyncEndpointsCommand`
    - `serviceName`, `serviceCode` 직접 매핑
    - `endpoints` → `List<EndpointSyncItem>` 변환 (stream + map)
  - `toApiResponse(SyncEndpointsResult)` → `EndpointSyncResultApiResponse`
    - 모든 필드 직접 매핑 (1:1 대응)

---

### 2. Application Layer

#### 2.1 UseCase (Port-In)
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/port/in/command/SyncEndpointsUseCase.java`
- **타입**: Interface
- **메서드**:
  ```java
  SyncEndpointsResult sync(SyncEndpointsCommand command);
  ```
- **책임**: 엔드포인트 동기화 UseCase 정의

#### 2.2 Service (Implementation)
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/service/command/SyncEndpointsService.java`
- **어노테이션**: `@Service`
- **의존성**: `EndpointSyncCoordinator coordinator`
- **구현**:
  ```java
  @Override
  public SyncEndpointsResult sync(SyncEndpointsCommand command) {
      return coordinator.coordinate(command);
  }
  ```
- **특징**: 단순 위임 (트랜잭션은 Coordinator에서 처리)

#### 2.3 Command DTO
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/dto/command/SyncEndpointsCommand.java`
- **타입**: Record
- **필드**:
  ```java
  String serviceName                   // 서비스 이름
  String serviceCode                   // 서비스 코드 (nullable)
  List<EndpointSyncItem> endpoints     // 엔드포인트 정보 목록
  ```
- **중첩 Record**: `EndpointSyncItem`
  ```java
  String httpMethod
  String pathPattern
  String permissionKey
  String description
  boolean isPublic                     // 기본값 false
  ```

#### 2.4 Result DTO
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/dto/response/SyncEndpointsResult.java`
- **타입**: Record
- **필드**:
  ```java
  String serviceName
  int totalEndpoints
  int createdPermissions
  int createdEndpoints
  int skippedEndpoints
  int mappedRolePermissions
  ```
- **Static Factory**:
  - `of(serviceName, totalEndpoints, createdPermissions, createdEndpoints, skippedEndpoints, mappedRolePermissions)`
  - `of(...)` - 하위 호환 (mappedRolePermissions=0)

#### 2.5 Coordinator
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/internal/EndpointSyncCoordinator.java`
- **어노테이션**: `@Component`, `@Transactional` (메서드 레벨)
- **의존성**:
  - `PermissionReadManager` / `PermissionCommandManager`
  - `PermissionEndpointReadManager` / `PermissionEndpointCommandManager`
  - `ServiceReadManager`
  - `RoleReadManager`
  - `RolePermissionReadManager` / `RolePermissionCommandManager`
  - `EndpointSyncCommandFactory`
  - `TimeProvider`

**처리 흐름**:

1. **serviceCode → ServiceId 리졸브**:
   ```java
   ServiceCode code = ServiceCode.fromNullable(serviceCode);
   Optional<Service> service = serviceReadManager.findByCodeOptional(code);
   ServiceId serviceId = ServiceId.of(service.get().serviceIdValue());
   ```

2. **Permission 동기화 (IN절 조회 패턴)**:
   ```java
   // 기존 Permission IN절 한방 조회
   List<Permission> existing = permissionReadManager.findAllByPermissionKeys(permissionKeys);

   // permissionKey → permissionId 매핑 생성
   Map<String, Long> permissionKeyToIdMap = ...;

   // 누락된 permissionKey 필터링
   List<String> missingKeys = permissionKeys.stream()
       .filter(key -> !existingKeys.contains(key)).toList();

   // Factory로 생성
   List<Permission> newPermissions = factory.createMissingPermissions(missingKeys, itemsByPermissionKey, serviceId);

   // 벌크 저장 + 매핑 반환
   Map<String, Long> newKeyToIdMap = permissionCommandManager.persistAllAndReturnKeyToIdMap(newPermissions);
   ```

3. **PermissionEndpoint 동기화 (IN절 조회 패턴)**:
   ```java
   // URL 패턴 목록 추출
   List<String> urlPatterns = items.stream().map(EndpointSyncItem::pathPattern).toList();

   // IN절로 기존 엔드포인트 조회 → 서비스명으로 필터링
   List<PermissionEndpoint> existing = permissionEndpointReadManager.findAllByUrlPatterns(urlPatterns)
       .stream().filter(endpoint -> serviceName.equals(endpoint.serviceNameValue())).toList();

   // (serviceName, urlPattern, httpMethod) 조합으로 중복 체크
   String key = serviceName + "|" + pathPattern + "|" + httpMethod;

   // 누락된 엔드포인트 생성
   List<PermissionEndpoint> newEndpoints = factory.createMissingEndpoints(serviceName, newEndpointItems, permissionKeyToIdMap);

   // 벌크 저장
   permissionEndpointCommandManager.persistAll(newEndpoints);
   ```

4. **자동 Role-Permission 매핑**:
   ```java
   // SERVICE scope 기본 Role 조회 (ADMIN, EDITOR, VIEWER)
   Optional<Role> adminRole = findServiceRole(serviceId, "ADMIN");
   Optional<Role> editorRole = findServiceRole(serviceId, "EDITOR");
   Optional<Role> viewerRole = findServiceRole(serviceId, "VIEWER");

   // permissionKey에서 action 추출 (예: "product:create" → "create")
   String action = permissionKey.split(":")[1];

   // action 패턴에 따라 매핑 대상 Role 결정
   switch (action.toLowerCase()) {
       case "read", "list", "search", "get" -> ADMIN, EDITOR, VIEWER
       case "create", "update", "write", "edit" -> ADMIN, EDITOR
       default -> ADMIN only
   }

   // RolePermission 생성
   RolePermission.create(RoleId, PermissionId, now);

   // 중복 체크 (이미 매핑된 것 제외)
   List<PermissionId> alreadyGranted = rolePermissionReadManager.findGrantedPermissionIds(roleId, permissionIds);

   // 벌크 저장
   rolePermissionCommandManager.persistAll(filteredMappings);
   ```

#### 2.6 Factory
- **파일**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/factory/EndpointSyncCommandFactory.java`
- **어노테이션**: `@Component`
- **의존성**: `TimeProvider`

**메서드**:
1. `createMissingPermissions(List<String> missingPermissionKeys, Map<String, EndpointSyncItem> itemsByPermissionKey, ServiceId serviceId)`:
   ```java
   // permissionKey 파싱: "product:create" → ["product", "create"]
   String[] parts = permissionKey.split(":");
   String resource = parts[0];
   String action = parts[1];

   // Permission 생성
   return Permission.createCustom(serviceId, resource, action, description, now);
   ```

2. `createMissingEndpoints(String serviceName, List<EndpointSyncItem> newEndpointItems, Map<String, Long> permissionKeyToIdMap)`:
   ```java
   Long permissionId = permissionKeyToIdMap.get(item.permissionKey());
   return PermissionEndpoint.create(permissionId, serviceName, item.pathPattern(),
                                    HttpMethod.from(item.httpMethod()), item.description(), item.isPublic(), now);
   ```

---

### 3. Domain Layer

#### 3.1 Domain Model - Permission
- **파일**: `domain/src/main/java/com/ryuqq/authhub/domain/permission/aggregate/Permission.java`
- **타입**: Aggregate Root
- **필드**:
  ```java
  PermissionId permissionId            // Value Object ID
  ServiceId serviceId                  // Value Object FK (NOT NULL)
  PermissionKey permissionKey          // Value Object (resource:action)
  Resource resource                    // Value Object
  Action action                        // Value Object
  String description
  PermissionType type                  // SYSTEM | CUSTOM
  DeletionStatus deletionStatus        // Value Object
  Instant createdAt
  Instant updatedAt
  ```

**Factory Method**:
```java
public static Permission createCustom(
    ServiceId serviceId,           // 서비스 ID (NOT NULL)
    String resource,                // 리소스 (예: "product")
    String action,                  // 액션 (예: "create")
    String description,
    Instant now
) {
    return new Permission(
        null,                       // ID는 영속화 시 생성
        serviceId,
        PermissionKey.of(resource, action),
        Resource.of(resource),
        Action.of(action),
        description,
        PermissionType.CUSTOM,
        DeletionStatus.active(),
        now,
        now
    );
}
```

**비즈니스 규칙**:
- 동일 Service 내에서 permissionKey 유니크 (UNIQUE(service_id, permission_key))
- SYSTEM 타입 권한은 수정/삭제 불가
- CUSTOM 타입 권한은 수정/삭제 가능

#### 3.2 Domain Model - PermissionEndpoint
- **파일**: `domain/src/main/java/com/ryuqq/authhub/domain/permissionendpoint/aggregate/PermissionEndpoint.java`
- **타입**: Aggregate Root
- **필드**:
  ```java
  PermissionEndpointId permissionEndpointId   // Value Object ID
  PermissionId permissionId                   // Value Object FK (Long)
  ServiceName serviceName                     // Value Object
  UrlPattern urlPattern                       // Value Object
  HttpMethod httpMethod                       // Enum
  String description
  boolean isPublic                            // 공개 엔드포인트 여부
  DeletionStatus deletionStatus               // Value Object
  Instant createdAt
  Instant updatedAt
  ```

**Factory Method**:
```java
public static PermissionEndpoint create(
    PermissionId permissionId,      // 권한 ID
    ServiceName serviceName,        // 서비스 이름
    UrlPattern urlPattern,          // URL 패턴
    HttpMethod httpMethod,          // HTTP 메서드
    String description,
    boolean isPublic,
    Instant now
) {
    return new PermissionEndpoint(
        null,                       // ID는 영속화 시 생성
        permissionId,
        serviceName,
        urlPattern,
        httpMethod,
        description,
        isPublic,
        DeletionStatus.active(),
        now,
        now
    );
}
```

**비즈니스 규칙**:
- (serviceName, urlPattern, httpMethod) 조합 유니크
- Gateway에서 URL-Permission 매핑 정보로 사용
- 하나의 Permission에 여러 Endpoint 매핑 가능 (1:N)

#### 3.3 Domain Port (Port-Out)

**Permission Ports**:
- **Query Port**: `application/src/main/java/com/ryuqq/authhub/application/permission/port/out/query/PermissionQueryPort.java`
  ```java
  Optional<Permission> findById(PermissionId id);
  List<Permission> findAllByPermissionKeys(List<String> permissionKeys);  // IN절 조회
  List<Permission> findAllBySearchCriteria(PermissionSearchCriteria criteria);
  // ...
  ```

- **Command Port**: `application/src/main/java/com/ryuqq/authhub/application/permission/port/out/command/PermissionCommandPort.java`
  ```java
  Long persist(Permission permission);
  ```

**PermissionEndpoint Ports**:
- **Query Port**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/port/out/query/PermissionEndpointQueryPort.java`
  ```java
  Optional<PermissionEndpoint> findById(PermissionEndpointId id);
  List<PermissionEndpoint> findAllByUrlPatterns(List<String> urlPatterns);  // IN절 조회
  List<PermissionEndpoint> findAllBySearchCriteria(PermissionEndpointSearchCriteria criteria);
  // ...
  ```

- **Command Port**: `application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/port/out/command/PermissionEndpointCommandPort.java`
  ```java
  Long persist(PermissionEndpoint permissionEndpoint);
  ```

---

### 4. Adapter-Out Layer

#### 4.1 Adapter - Permission

**Query Adapter**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/adapter/PermissionQueryAdapter.java`
- **구현**: `PermissionQueryPort`
- **의존성**:
  - `PermissionQueryDslRepository`
  - `PermissionJpaEntityMapper`
- **주요 메서드**:
  ```java
  @Override
  public List<Permission> findAllByPermissionKeys(List<String> permissionKeys) {
      List<PermissionJpaEntity> entities = repository.findAllByPermissionKeys(permissionKeys);
      return entities.stream().map(mapper::toDomain).toList();
  }
  ```

**Command Adapter**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/adapter/PermissionCommandAdapter.java`
- **구현**: `PermissionCommandPort`
- **의존성**:
  - `PermissionJpaRepository`
  - `PermissionJpaEntityMapper`
- **주요 메서드**:
  ```java
  @Override
  public Long persist(Permission permission) {
      PermissionJpaEntity entity = mapper.toEntity(permission);
      PermissionJpaEntity savedEntity = repository.save(entity);
      return savedEntity.getPermissionId();
  }
  ```

#### 4.2 Adapter - PermissionEndpoint

**Query Adapter**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/adapter/PermissionEndpointQueryAdapter.java`
- **구현**: `PermissionEndpointQueryPort`
- **의존성**:
  - `PermissionEndpointJpaRepository`
  - `PermissionEndpointQueryDslRepository`
  - `PermissionEndpointJpaEntityMapper`

**Command Adapter**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/adapter/PermissionEndpointCommandAdapter.java`
- **구현**: `PermissionEndpointCommandPort`
- **의존성**:
  - `PermissionEndpointJpaRepository`
  - `PermissionEndpointJpaEntityMapper`

#### 4.3 Repository - Permission

**JPA Repository**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/repository/PermissionJpaRepository.java`
- **타입**: `extends JpaRepository<PermissionJpaEntity, Long>`
- **책임**: Command (CUD) 작업
- **메서드**: Spring Data JPA 기본 메서드 (save, delete 등)

**QueryDSL Repository**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/repository/PermissionQueryDslRepository.java`
- **어노테이션**: `@Repository`
- **의존성**:
  - `JPAQueryFactory`
  - `PermissionConditionBuilder`
- **주요 쿼리**:
  ```java
  public List<PermissionJpaEntity> findAllByPermissionKeys(List<String> permissionKeys) {
      return queryFactory
          .selectFrom(permissionJpaEntity)
          .where(permissionJpaEntity.permissionKey.in(permissionKeys)
              .and(permissionJpaEntity.deletedAt.isNull()))
          .fetch();
  }
  ```
- **쿼리 특징**: IN절 한방 조회, Soft Delete 고려 (deletedAt IS NULL)

#### 4.4 Repository - PermissionEndpoint

**JPA Repository**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/repository/PermissionEndpointJpaRepository.java`
- **타입**: `extends JpaRepository<PermissionEndpointJpaEntity, Long>`
- **커스텀 메서드**:
  ```java
  boolean existsByUrlPatternAndHttpMethodAndDeletedAtIsNull(String urlPattern, HttpMethod httpMethod);
  Optional<PermissionEndpointJpaEntity> findByUrlPatternAndHttpMethodAndDeletedAtIsNull(String urlPattern, HttpMethod httpMethod);
  List<PermissionEndpointJpaEntity> findAllByPermissionIdAndDeletedAtIsNull(Long permissionId);
  ```

**QueryDSL Repository**:
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/repository/PermissionEndpointQueryDslRepository.java`
- **주요 쿼리**:
  ```java
  public List<PermissionEndpointJpaEntity> findAllByUrlPatterns(List<String> urlPatterns) {
      return queryFactory
          .selectFrom(permissionEndpointJpaEntity)
          .where(permissionEndpointJpaEntity.urlPattern.in(urlPatterns)
              .and(permissionEndpointJpaEntity.deletedAt.isNull()))
          .fetch();
  }
  ```

#### 4.5 Entity - Permission

**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/entity/PermissionJpaEntity.java`

**테이블 매핑**:
```java
@Entity
@Table(name = "permissions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_permission_service_key",
                         columnNames = {"service_id", "permission_key"})
    })
public class PermissionJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "permission_key", nullable = false, length = 100)
    private String permissionKey;

    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PermissionType type;

    // createdAt, updatedAt, deletedAt는 SoftDeletableEntity에서 상속
}
```

**인덱스**:
- PK: `permission_id`
- UK: `UNIQUE(service_id, permission_key)`

#### 4.6 Entity - PermissionEndpoint

**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/entity/PermissionEndpointJpaEntity.java`

**테이블 매핑**:
```java
@Entity
@Table(name = "permission_endpoints",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_permission_endpoints_service_url_method",
                         columnNames = {"service_name", "url_pattern", "http_method"})
    },
    indexes = {
        @Index(name = "idx_permission_endpoints_permission_id", columnList = "permission_id")
    })
public class PermissionEndpointJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_endpoint_id")
    private Long permissionEndpointId;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;

    @Column(name = "url_pattern", nullable = false, length = 255)
    private String urlPattern;

    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false, length = 10)
    private HttpMethod httpMethod;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    // createdAt, updatedAt, deletedAt는 SoftDeletableEntity에서 상속
}
```

**인덱스**:
- PK: `permission_endpoint_id`
- UK: `UNIQUE(service_name, url_pattern, http_method)`
- IDX: `idx_permission_endpoints_permission_id`

---

## Database Query 분석

### Permission 동기화 쿼리

**1. 기존 Permission IN절 조회**:
```sql
SELECT *
FROM permissions
WHERE permission_key IN ('product:create', 'product:read', ...)
  AND deleted_at IS NULL;
```

**2. 신규 Permission 저장** (누락된 것만):
```sql
INSERT INTO permissions
    (service_id, permission_key, resource, action, description, type, created_at, updated_at)
VALUES
    (?, 'product:create', 'product', 'create', 'Create product', 'CUSTOM', NOW(), NOW()),
    (?, 'product:read', 'product', 'read', 'Read product', 'CUSTOM', NOW(), NOW());
```

### PermissionEndpoint 동기화 쿼리

**1. 기존 PermissionEndpoint IN절 조회**:
```sql
SELECT *
FROM permission_endpoints
WHERE url_pattern IN ('/api/v1/products', '/api/v1/products/{id}', ...)
  AND deleted_at IS NULL;
```

**2. 신규 PermissionEndpoint 저장** (누락된 것만):
```sql
INSERT INTO permission_endpoints
    (permission_id, service_name, url_pattern, http_method, description, is_public, created_at, updated_at)
VALUES
    (?, 'marketplace', '/api/v1/products', 'POST', 'Create product endpoint', false, NOW(), NOW()),
    (?, 'marketplace', '/api/v1/products', 'GET', 'List products endpoint', false, NOW(), NOW());
```

### Role-Permission 자동 매핑 쿼리

**1. SERVICE scope 기본 Role 조회**:
```sql
SELECT *
FROM roles
WHERE tenant_id IS NULL
  AND service_id = ?
  AND role_name IN ('ADMIN', 'EDITOR', 'VIEWER')
  AND deleted_at IS NULL;
```

**2. 이미 부여된 Permission 조회** (중복 체크):
```sql
SELECT permission_id
FROM role_permissions
WHERE role_id = ?
  AND permission_id IN (?, ?, ...)
  AND deleted_at IS NULL;
```

**3. 신규 Role-Permission 매핑 저장**:
```sql
INSERT INTO role_permissions
    (role_id, permission_id, created_at)
VALUES
    (?, ?, NOW()),  -- ADMIN → product:create
    (?, ?, NOW()),  -- ADMIN → product:read
    (?, ?, NOW()),  -- EDITOR → product:create
    (?, ?, NOW());  -- EDITOR → product:read
```

---

## 성능 최적화 전략

### 1. IN절 한방 조회 패턴
- 개별 조회 대신 IN절로 벌크 조회
- N+1 문제 방지
- 데이터베이스 왕복 횟수 최소화

### 2. 벌크 저장
- 누락된 것만 필터링하여 저장
- 불필요한 UPDATE 방지
- 트랜잭션 일관성 보장 (Coordinator의 @Transactional)

### 3. 중복 체크 최적화
- (serviceName, urlPattern, httpMethod) 조합으로 Set 생성
- O(1) 조회 성능
- 메모리 내에서 필터링

### 4. Role-Permission 매핑 최적화
- RoleId별 그룹핑하여 조회 최소화
- 이미 매핑된 것 제외 (중복 방지)
- 벌크 저장으로 성능 향상

---

## 트랜잭션 범위

```
@Transactional (EndpointSyncCoordinator.coordinate)
├── Permission 동기화
│   ├── IN절 조회 (ReadManager - @Transactional(readOnly=true))
│   ├── 벌크 저장 (CommandManager - @Transactional)
├── PermissionEndpoint 동기화
│   ├── IN절 조회 (ReadManager - @Transactional(readOnly=true))
│   ├── 벌크 저장 (CommandManager - @Transactional)
└── Role-Permission 자동 매핑
    ├── Role 조회 (ReadManager - @Transactional(readOnly=true))
    ├── 중복 체크 조회 (ReadManager - @Transactional(readOnly=true))
    └── 벌크 저장 (CommandManager - @Transactional)
```

**트랜잭션 전파 (REQUIRED)**:
- Coordinator의 `@Transactional`이 최상위 트랜잭션 생성
- Manager의 `@Transactional`은 기존 트랜잭션 참여 (REQUIRED)
- 전체 동기화 작업이 하나의 트랜잭션으로 처리
- 중간에 실패하면 전체 롤백

---

## 에러 처리

### Validation 에러
- `@Valid` 검증 실패 시 `MethodArgumentNotValidException`
- Spring Boot가 자동으로 400 Bad Request 응답

### Domain 예외
- `IllegalArgumentException`: permissionKey 형식 오류 (Factory)
- `IllegalStateException`: Permission not found for key (Factory)

### 트랜잭션 롤백
- 모든 RuntimeException 발생 시 트랜잭션 롤백
- Database 제약 조건 위반 시 롤백

---

## 비즈니스 규칙

### Permission 생성 규칙
1. permissionKey 형식: `{resource}:{action}` (예: "product:create")
2. 동일 Service 내에서 permissionKey 유니크
3. 타입: CUSTOM (동기화 시 자동 생성된 권한)
4. serviceId 필수 (Service 기반 설계)

### PermissionEndpoint 생성 규칙
1. (serviceName, urlPattern, httpMethod) 조합 유니크
2. 하나의 Permission에 여러 Endpoint 매핑 가능
3. isPublic 기본값: false (인증 필요)

### Role-Permission 자동 매핑 규칙
1. serviceId가 있는 경우에만 실행
2. SERVICE scope 기본 Role 존재 시에만 실행 (ADMIN, EDITOR, VIEWER)
3. action 패턴에 따른 Role 매핑:
   - `read`, `list`, `search`, `get` → ADMIN, EDITOR, VIEWER
   - `create`, `update`, `write`, `edit` → ADMIN, EDITOR
   - `delete`, 기타 → ADMIN only
4. 이미 매핑된 것은 스킵 (중복 방지)

---

## 주요 특징

### 1. Hexagonal Architecture 준수
- Port-In (UseCase) / Port-Out (Domain Port) 명확한 분리
- Domain이 외부 의존성 없이 순수 비즈니스 로직 포함
- Adapter가 Infrastructure 관심사 처리

### 2. CQRS 패턴
- Query용 Repository (QueryDSL)와 Command용 Repository (JPA) 분리
- ReadManager와 CommandManager 분리

### 3. DDD 패턴
- Aggregate Root (Permission, PermissionEndpoint)
- Value Object (PermissionId, ServiceId, PermissionKey 등)
- Factory (EndpointSyncCommandFactory)
- Repository (Port-Out)

### 4. Long FK 전략
- JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등)
- Long 타입 FK로 직접 관리
- N+1 문제 사전 방지

### 5. Manager 레이어
- Port를 직접 노출하지 않고 Manager로 래핑
- 트랜잭션 일관성 보장 (@Transactional 메서드 단위)
- 비즈니스 로직과 영속화 로직 분리

### 6. Coordinator 패턴
- 복잡한 비즈니스 로직 조율
- 여러 Manager 조합
- 트랜잭션 경계 관리

---

## 참고사항

### 보안
- Internal API이므로 서비스 토큰 인증 필요
- 내부 네트워크에서만 접근 가능해야 함

### 하위 호환성
- `SyncEndpointsCommand`: serviceCode 없는 생성자 제공
- `SyncEndpointsResult`: mappedRolePermissions 없는 생성자 제공
- `EndpointSyncItem`: isPublic 기본값 false

### 확장 가능성
- 새로운 Permission 타입 추가 가능 (enum 확장)
- 새로운 HttpMethod 추가 가능 (enum 확장)
- Role-Permission 매핑 규칙 커스터마이징 가능 (Coordinator 로직 수정)
