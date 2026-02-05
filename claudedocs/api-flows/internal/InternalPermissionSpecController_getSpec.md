# API Flow: InternalPermissionSpecController.getSpec

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | GET |
| Path | /api/v1/internal/endpoint-permissions/spec |
| Controller | InternalPermissionSpecController |
| Method | getSpec |
| UseCase | GetEndpointPermissionSpecUseCase |
| Service | GetEndpointPermissionSpecService |
| 목적 | Gateway가 URL 기반 권한 검사를 위해 전체 엔드포인트-권한 매핑 정보를 조회 |

---

## 호출 흐름 다이어그램

```
InternalPermissionSpecController.getSpec()
  |- GetEndpointPermissionSpecUseCase.getAll()                [Port-In]
  |   +-- GetEndpointPermissionSpecService.getAll()           [Service Implementation]
  |       |- PermissionEndpointReadManager.findAllActiveSpecs()
  |       |   +-- PermissionEndpointSpecQueryPort.findAllActiveSpecs() [Port-Out]
  |       |       +-- PermissionEndpointSpecQueryAdapter.findAllActiveSpecs()
  |       |           +-- PermissionEndpointQueryDslRepository.findAllActiveSpecs()
  |       |               +-- QueryDSL JOIN Query
  |       |                   - FROM permission_endpoints
  |       |                   - JOIN permissions ON permission_id
  |       |                   - WHERE deletedAt IS NULL (Both tables)
  |       |                   +-- -> List<EndpointPermissionSpecResult>
  |       |
  |       |- PermissionEndpointReadManager.findLatestUpdatedAt()
  |       |   +-- PermissionEndpointSpecQueryPort.findLatestUpdatedAt()
  |       |       +-- PermissionEndpointSpecQueryAdapter.findLatestUpdatedAt()
  |       |           +-- PermissionEndpointQueryDslRepository.findLatestUpdatedAt()
  |       |               +-- SELECT MAX(updatedAt) FROM permission_endpoints
  |       |                   WHERE deletedAt IS NULL
  |       |                   +-- -> Instant (latestUpdatedAt)
  |       |
  |       +-- PermissionEndpointAssembler.toSpecListResult(specs, latestUpdatedAt)
  |           +-- -> EndpointPermissionSpecListResult
  |
  +-- InternalPermissionSpecApiMapper.toApiResponse(result)
      +-- -> EndpointPermissionSpecListApiResponse

  +-- ApiResponse.ofSuccess(response)
      +-- -> ApiResponse<EndpointPermissionSpecListApiResponse>
```

---

## Layer별 상세

### 1. Adapter-In Layer

#### 1.1 Controller
**위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/controller/InternalPermissionSpecController.java`

**역할**: Gateway용 엔드포인트-권한 스펙 조회 API 제공

**메서드 시그니처**:
```java
@GetMapping(ENDPOINT_PERMISSIONS_SPEC)
public ApiResponse<EndpointPermissionSpecListApiResponse> getSpec()
```

**처리 흐름**:
1. `GetEndpointPermissionSpecUseCase.getAll()` 호출 → `EndpointPermissionSpecListResult` 반환
2. `InternalPermissionSpecApiMapper.toApiResponse(result)` 호출 → API Response DTO 변환
3. `ApiResponse.ofSuccess(response)` 래핑

#### 1.2 Response DTO

**EndpointPermissionSpecListApiResponse**
- **위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/EndpointPermissionSpecListApiResponse.java`
- **타입**: Record
- **필드**:
  - `version` (String): 스펙 버전 (ETag용, latestUpdatedAt의 timestamp)
  - `updatedAt` (Instant): 마지막 수정 시간
  - `endpoints` (List\<EndpointPermissionSpecApiResponse\>): 엔드포인트-권한 매핑 목록

**EndpointPermissionSpecApiResponse**
- **위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/EndpointPermissionSpecApiResponse.java`
- **타입**: Record
- **필드**:
  - `serviceName` (String): 서비스 이름 (예: "product-service")
  - `pathPattern` (String): URL 패턴 (예: "/api/v1/users/{id}")
  - `httpMethod` (String): HTTP 메서드 (예: "GET", "POST")
  - `requiredPermissions` (List\<String\>): 필요 권한 목록 (예: ["user:read"])
  - `requiredRoles` (List\<String\>): 필요 역할 목록 (예: ["ADMIN"])
  - `isPublic` (boolean): 공개 엔드포인트 여부 (인증 불필요)
  - `description` (String): 엔드포인트 설명

#### 1.3 ApiMapper

**InternalPermissionSpecApiMapper**
- **위치**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/mapper/InternalPermissionSpecApiMapper.java`
- **타입**: @Component
- **역할**: Application Layer 결과를 API 응답으로 변환

**변환 메서드**:
```java
public EndpointPermissionSpecListApiResponse toApiResponse(EndpointPermissionSpecListResult result) {
    List<EndpointPermissionSpecApiResponse> endpoints =
        result.endpoints().stream().map(this::toApiResponse).toList();

    return new EndpointPermissionSpecListApiResponse(
        result.version(),
        result.updatedAt(),
        endpoints
    );
}

private EndpointPermissionSpecApiResponse toApiResponse(EndpointPermissionSpecResult result) {
    return new EndpointPermissionSpecApiResponse(
        result.serviceName(),
        result.pathPattern(),
        result.httpMethod(),
        result.requiredPermissions(),
        result.requiredRoles(),
        result.isPublic(),
        result.description()
    );
}
```

---

### 2. Application Layer

#### 2.1 UseCase (Port-In)

**GetEndpointPermissionSpecUseCase**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/port/in/query/GetEndpointPermissionSpecUseCase.java`
- **타입**: Interface (Port-In)
- **역할**: Gateway용 엔드포인트-권한 스펙 조회 UseCase 정의

**메서드 시그니처**:
```java
EndpointPermissionSpecListResult getAll();
```

**사용 시나리오**:
- Gateway 시작 시 전체 스펙 캐싱
- 스펙 변경 시 Gateway 갱신

#### 2.2 Service (Implementation)

**GetEndpointPermissionSpecService**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/service/query/GetEndpointPermissionSpecService.java`
- **타입**: @Service (UseCase 구현체)
- **의존성**:
  - `PermissionEndpointReadManager`: 조회 관리
  - `PermissionEndpointAssembler`: Domain → Result 변환

**구현 로직**:
```java
@Override
public EndpointPermissionSpecListResult getAll() {
    // 1. 모든 활성 엔드포인트-권한 스펙 조회
    List<EndpointPermissionSpecResult> specs = readManager.findAllActiveSpecs();

    // 2. 가장 최근 수정 시간 조회 (버전 관리용)
    Instant latestUpdatedAt = readManager.findLatestUpdatedAt();

    // 3. Assembler를 통해 Result DTO 생성
    return assembler.toSpecListResult(specs, latestUpdatedAt);
}
```

**Zero-Tolerance 규칙**:
- `@Service` 어노테이션
- `@Transactional` 금지 (Manager에서 처리)
- ReadManager → Assembler 흐름
- Port 직접 호출 금지
- Lombok 금지

#### 2.3 ReadManager

**PermissionEndpointReadManager**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/manager/PermissionEndpointReadManager.java`
- **타입**: @Component
- **역할**: 단일 Port 조회 관리
- **의존성**:
  - `PermissionEndpointQueryPort`: 일반 조회
  - `PermissionEndpointSpecQueryPort`: 스펙 조회 (조인 쿼리)

**조회 메서드**:
```java
@Transactional(readOnly = true)
public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
    return specQueryPort.findAllActiveSpecs();
}

@Transactional(readOnly = true)
public Instant findLatestUpdatedAt() {
    return specQueryPort.findLatestUpdatedAt();
}
```

**Zero-Tolerance 규칙**:
- `@Component` 어노테이션 (`@Service` 아님)
- `@Transactional(readOnly = true)` 메서드 단위
- `find*()` 메서드 네이밍
- QueryPort만 의존
- 비즈니스 로직 금지
- Lombok 금지
- Criteria 기반 조회

#### 2.4 Result DTO

**EndpointPermissionSpecListResult**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/dto/response/EndpointPermissionSpecListResult.java`
- **타입**: Record
- **필드**:
  - `version` (String): 스펙 버전 (ETag용)
  - `updatedAt` (Instant): 마지막 수정 시간 (ISO 8601)
  - `endpoints` (List\<EndpointPermissionSpecResult\>): 엔드포인트-권한 매핑 목록

**팩토리 메서드**:
```java
public static EndpointPermissionSpecListResult empty() {
    return new EndpointPermissionSpecListResult("0", Instant.now(), List.of());
}

public static EndpointPermissionSpecListResult of(
    List<EndpointPermissionSpecResult> endpoints,
    Instant latestUpdatedAt
) {
    String version = String.valueOf(latestUpdatedAt != null ? latestUpdatedAt.toEpochMilli() : 0);
    return new EndpointPermissionSpecListResult(version, latestUpdatedAt, endpoints);
}
```

**EndpointPermissionSpecResult**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/dto/response/EndpointPermissionSpecResult.java`
- **타입**: Record
- **필드**:
  - `serviceName` (String): 서비스 이름
  - `pathPattern` (String): URL 패턴
  - `httpMethod` (String): HTTP 메서드
  - `requiredPermissions` (List\<String\>): 필요 권한 목록
  - `requiredRoles` (List\<String\>): 필요 역할 목록
  - `isPublic` (boolean): 공개 엔드포인트 여부
  - `description` (String): 엔드포인트 설명

**QueryDSL Projection용 생성자**:
```java
public EndpointPermissionSpecResult(
    String serviceName,
    String pathPattern,
    String httpMethod,
    String permissionKey,  // 단일 permissionKey
    boolean isPublic,
    String description
) {
    this(
        serviceName,
        pathPattern,
        httpMethod,
        List.of(permissionKey),  // List로 래핑
        List.of(),
        isPublic,
        description
    );
}
```

#### 2.5 Assembler

**PermissionEndpointAssembler**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/assembler/PermissionEndpointAssembler.java`
- **타입**: @Component
- **역할**: Domain → Result 변환

**변환 메서드**:
```java
public EndpointPermissionSpecListResult toSpecListResult(
    List<EndpointPermissionSpecResult> specs,
    Instant latestUpdatedAt
) {
    return EndpointPermissionSpecListResult.of(specs, latestUpdatedAt);
}
```

**Zero-Tolerance 규칙**:
- `@Component` 어노테이션
- Domain → Result 변환만 (toDomain 금지!)
- Port/Repository 의존 금지
- 비즈니스 로직 금지
- Getter 체이닝 금지 (Law of Demeter)
- Lombok 금지

---

### 3. Domain Layer

#### 3.1 Domain Port (Port-Out)

**PermissionEndpointSpecQueryPort**
- **위치**: `/application/src/main/java/com/ryuqq/authhub/application/permissionendpoint/port/out/query/PermissionEndpointSpecQueryPort.java`
- **타입**: Interface (Port-Out)
- **역할**: Gateway용 엔드포인트-권한 스펙 조회 포트

**메서드 정의**:
```java
List<EndpointPermissionSpecResult> findAllActiveSpecs();
Instant findLatestUpdatedAt();
```

**특이사항**:
- Application DTO 반환 (Domain 조합이 필요하므로 예외적으로 DTO 반환 허용)
- 조회 메서드만 제공

**Zero-Tolerance 규칙**:
- 조회 메서드만 제공
- Application DTO 반환 허용 (조인 쿼리 필요)

---

### 4. Adapter-Out Layer

#### 4.1 Adapter

**PermissionEndpointSpecQueryAdapter**
- **위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/adapter/PermissionEndpointSpecQueryAdapter.java`
- **타입**: @Component (Port 구현체)
- **의존성**:
  - `PermissionEndpointQueryDslRepository`: QueryDSL Repository

**구현 메서드**:
```java
@Override
public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
    return queryDslRepository.findAllActiveSpecs();
}

@Override
public Instant findLatestUpdatedAt() {
    return queryDslRepository.findLatestUpdatedAt();
}
```

**특이사항**:
- Application DTO 직접 반환 (조인 쿼리이므로 예외)

#### 4.2 Repository

**PermissionEndpointQueryDslRepository**
- **위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/repository/PermissionEndpointQueryDslRepository.java`
- **타입**: @Repository
- **의존성**:
  - `JPAQueryFactory`: QueryDSL Query 실행
  - `PermissionEndpointConditionBuilder`: 조건 빌더

**핵심 쿼리 메서드**:

##### findAllActiveSpecs()
```java
public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
    QPermissionJpaEntity permission = QPermissionJpaEntity.permissionJpaEntity;

    return queryFactory
        .select(
            Projections.constructor(
                EndpointPermissionSpecResult.class,
                permissionEndpoint.serviceName,
                permissionEndpoint.urlPattern,
                permissionEndpoint.httpMethod.stringValue(),
                permission.permissionKey,
                permissionEndpoint.isPublic,
                permissionEndpoint.description
            )
        )
        .from(permissionEndpoint)
        .join(permission)
            .on(permissionEndpoint.permissionId.eq(permission.permissionId))
        .where(
            permissionEndpoint.deletedAt.isNull(),
            permission.deletedAt.isNull()
        )
        .fetch();
}
```

**쿼리 분석**:
- **SELECT**:
  - `serviceName`, `urlPattern`, `httpMethod`, `permissionKey`, `isPublic`, `description`
- **FROM**: `permission_endpoints`
- **JOIN**: `permissions` ON `permission_endpoints.permission_id = permissions.permission_id`
- **WHERE**:
  - `permission_endpoints.deleted_at IS NULL`
  - `permissions.deleted_at IS NULL`
- **특징**: QueryDSL Projections.constructor로 DTO 직접 생성

##### findLatestUpdatedAt()
```java
public Instant findLatestUpdatedAt() {
    return queryFactory
        .select(permissionEndpoint.updatedAt.max())
        .from(permissionEndpoint)
        .where(permissionEndpoint.deletedAt.isNull())
        .fetchOne();
}
```

**쿼리 분석**:
- **SELECT**: `MAX(updated_at)`
- **FROM**: `permission_endpoints`
- **WHERE**: `deleted_at IS NULL`
- **반환**: Instant (null 가능)

#### 4.3 Entity

**PermissionEndpointJpaEntity**
- **위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permissionendpoint/entity/PermissionEndpointJpaEntity.java`
- **테이블**: `permission_endpoints`
- **PK**: `permissionEndpointId` (Long, Auto Increment)
- **FK**: `permissionId` (Long FK 전략, JPA 관계 어노테이션 금지)

**필드**:
- `permissionEndpointId` (Long): 엔드포인트 ID (PK)
- `permissionId` (Long): 권한 ID (FK)
- `serviceName` (String): 서비스 이름
- `urlPattern` (String): URL 패턴
- `httpMethod` (HttpMethod): HTTP 메서드
- `description` (String): 설명
- `isPublic` (boolean): 공개 엔드포인트 여부
- `createdAt`, `updatedAt`, `deletedAt` (SoftDeletableEntity 상속)

**인덱스**:
- `uk_permission_endpoints_service_url_method`: (service_name, url_pattern, http_method) - UNIQUE
- `idx_permission_endpoints_permission_id`: permission_id
- `idx_permission_endpoints_service_name`: service_name
- `idx_permission_endpoints_url_pattern`: url_pattern
- `idx_permission_endpoints_http_method`: http_method
- `idx_permission_endpoints_is_public`: is_public

**PermissionJpaEntity**
- **위치**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/permission/entity/PermissionJpaEntity.java`
- **테이블**: `permissions`
- **PK**: `permissionId` (Long, Auto Increment)
- **FK**: `serviceId` (Long FK, nullable)

**필드**:
- `permissionId` (Long): 권한 ID (PK)
- `serviceId` (Long): 서비스 ID FK (nullable)
- `permissionKey` (String): 권한 키 (resource:action 형식)
- `resource` (String): 리소스
- `action` (String): 행위
- `description` (String): 권한 설명
- `type` (PermissionType): 권한 유형 (SYSTEM, CUSTOM)
- `createdAt`, `updatedAt`, `deletedAt` (SoftDeletableEntity 상속)

**인덱스**:
- `uk_permission_service_key`: (service_id, permission_key) - UNIQUE

---

## Database Query 분석

### 쿼리 1: findAllActiveSpecs()

**생성되는 SQL** (예상):
```sql
SELECT
    pe.service_name,
    pe.url_pattern,
    pe.http_method,
    p.permission_key,
    pe.is_public,
    pe.description
FROM permission_endpoints pe
INNER JOIN permissions p
    ON pe.permission_id = p.permission_id
WHERE pe.deleted_at IS NULL
  AND p.deleted_at IS NULL
```

**테이블**:
- `permission_endpoints` (pe): 엔드포인트 정보
- `permissions` (p): 권한 정보

**JOIN 조건**:
- `pe.permission_id = p.permission_id`

**WHERE 조건**:
- `pe.deleted_at IS NULL` (삭제되지 않은 엔드포인트)
- `p.deleted_at IS NULL` (삭제되지 않은 권한)

**반환**:
- `List<EndpointPermissionSpecResult>` (각 엔드포인트-권한 매핑)

**인덱스 활용**:
- `idx_permission_endpoints_permission_id` (FK 조인)
- 삭제 필터링은 Full Table Scan (deletedAt에 인덱스 없음)

### 쿼리 2: findLatestUpdatedAt()

**생성되는 SQL** (예상):
```sql
SELECT MAX(pe.updated_at)
FROM permission_endpoints pe
WHERE pe.deleted_at IS NULL
```

**테이블**:
- `permission_endpoints` (pe)

**WHERE 조건**:
- `pe.deleted_at IS NULL`

**반환**:
- `Instant` (가장 최근 수정 시간, 없으면 null)

**인덱스 활용**:
- Full Table Scan (updated_at, deleted_at에 인덱스 없음)

---

## 트랜잭션 경계

### ReadManager 레벨
- `@Transactional(readOnly = true)` 메서드 단위
- 두 개의 독립적인 조회 트랜잭션:
  1. `findAllActiveSpecs()` → 전체 스펙 조회
  2. `findLatestUpdatedAt()` → 최근 수정 시간 조회

### Service 레벨
- `@Transactional` 금지
- Manager가 트랜잭션 관리

---

## 데이터 흐름 요약

```
[Database]
  permission_endpoints (LEFT)
    JOIN
  permissions (RIGHT)
  ↓ (QueryDSL Projection)
[Adapter-Out] EndpointPermissionSpecResult (Application DTO)
  ↓ (Port-Out 반환)
[Application] EndpointPermissionSpecResult (List)
  ↓ (Assembler)
[Application] EndpointPermissionSpecListResult
  ↓ (ApiMapper)
[Adapter-In] EndpointPermissionSpecListApiResponse
  ↓ (ApiResponse 래핑)
[HTTP Response] ApiResponse<EndpointPermissionSpecListApiResponse>
```

---

## 특이사항 및 설계 포인트

### 1. Application DTO 직접 반환
- **일반적인 Hexagonal 패턴**: Port-Out은 Domain 객체 반환
- **이 API의 예외**: Port-Out이 Application DTO (`EndpointPermissionSpecResult`) 직접 반환
- **이유**:
  - PermissionEndpoint + Permission 조인 쿼리
  - 두 Aggregate를 조합한 DTO가 Gateway 스펙 제공 목적에 적합
  - Domain 레이어에 Gateway 전용 Aggregate를 만드는 것은 과도한 복잡도
- **코드 주석**: "Domain 조합이 필요하므로 예외적으로 DTO 반환 허용"

### 2. 버전 관리 전략
- **version**: `latestUpdatedAt.toEpochMilli()` (타임스탬프)
- **용도**: Gateway ETag 캐싱
- **변경 감지**: updatedAt이 변경되면 새로운 버전 생성

### 3. Soft Delete 필터링
- 두 테이블 모두 `deletedAt IS NULL` 조건
- 삭제된 엔드포인트나 권한은 스펙에서 제외

### 4. Long FK 전략
- `permissionId` (Long)로 FK 관리
- JPA 관계 어노테이션 (@ManyToOne, @OneToMany) 금지
- 명시적 JOIN 쿼리 사용

### 5. QueryDSL Projections.constructor
- Entity → Domain 변환 없이 직접 DTO 생성
- 성능 최적화 (불필요한 객체 생성 제거)
- Gateway 스펙 조회는 대량 데이터 가능성 → 효율적 처리 필요

### 6. Gateway 캐싱 패턴
- Gateway 시작 시 전체 스펙 로드
- `version`과 `updatedAt`으로 캐시 무효화 판단
- 스펙 변경 시 Gateway 갱신 트리거

### 7. 보안 고려사항
- **Internal API**: 서비스 토큰 인증 보호
- **네트워크 격리**: 외부 접근 차단 필요
- **민감 정보**: 권한 구조 전체 노출 → 내부 네트워크 전용

---

## 성능 고려사항

### 쿼리 최적화
- **JOIN 인덱스**: `idx_permission_endpoints_permission_id` 활용
- **Soft Delete 필터**: deletedAt 인덱스 없음 → Full Table Scan 가능성
- **개선 제안**: (deleted_at, permission_id) 복합 인덱스 추가 고려

### 대량 데이터 처리
- **Gateway 시작 시 부하**: 전체 스펙 로드
- **메모리 사용**: 결과 DTO 리스트 전체 메모리 로드
- **개선 제안**:
  - 스펙이 수만 건 이상이면 페이징 고려
  - Redis 캐싱 추가

### 캐시 무효화 최적화
- **version 비교**: Gateway가 현재 버전과 비교
- **If-None-Match**: HTTP ETag 헤더 활용 가능
- **개선 제안**:
  - HTTP 304 Not Modified 지원
  - version만 먼저 조회하는 경량 API 추가

---

## Zero-Tolerance 규칙 준수 체크

### Application Layer
- ✅ Service: `@Service` 어노테이션
- ✅ Service: `@Transactional` 미사용 (Manager에서 처리)
- ✅ ReadManager: `@Component` 어노테이션
- ✅ ReadManager: `@Transactional(readOnly = true)` 메서드 단위
- ✅ ReadManager: `find*()` 메서드 네이밍
- ✅ Assembler: `@Component` 어노테이션
- ✅ Assembler: Domain → Result 변환만
- ✅ 모든 클래스: Lombok 미사용

### Adapter-Out Layer
- ✅ Adapter: `@Component` 어노테이션 (Port 구현체)
- ✅ Repository: `@Repository` 어노테이션
- ✅ Entity: Lombok 미사용
- ✅ Entity: Long FK 전략
- ✅ Entity: JPA 관계 어노테이션 금지

### Adapter-In Layer
- ✅ Controller: `@RestController` 어노테이션
- ✅ ApiMapper: `@Component` 어노테이션
- ✅ Response DTO: Record 타입
- ✅ 모든 클래스: Lombok 미사용

---

## 테스트 시나리오

### Unit Test
1. **Service**:
   - `readManager.findAllActiveSpecs()` Mock → 정상 반환
   - `readManager.findLatestUpdatedAt()` Mock → 정상 반환
   - `assembler.toSpecListResult()` → 정상 변환

2. **ApiMapper**:
   - Application DTO → API Response DTO 변환 검증

3. **Repository**:
   - QueryDSL 쿼리 결과 검증
   - Soft Delete 필터링 검증

### Integration Test
1. **Controller**:
   - GET /api/v1/internal/endpoint-permissions/spec
   - 200 OK 응답
   - Response 구조 검증

2. **Database**:
   - 실제 DB에 테스트 데이터 INSERT
   - JOIN 쿼리 결과 검증
   - deletedAt 필터링 검증

### E2E Test
1. **Gateway 통합**:
   - Gateway가 API 호출
   - 스펙 캐싱 검증
   - version 기반 캐시 무효화 검증
