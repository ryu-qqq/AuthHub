# API Flow: InternalTenantConfigController.getConfig

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | GET |
| Path | `/api/v1/internal/tenants/{tenantId}/config` |
| Controller | InternalTenantConfigController |
| Method | getConfig |
| UseCase | GetTenantConfigUseCase |
| Service | GetTenantConfigService |
| 목적 | Gateway가 테넌트 유효성 검증을 위해 설정 정보 조회 |

---

## 호출 흐름 다이어그램

```
InternalTenantConfigController.getConfig(tenantId: String)
  │
  ├─ GetTenantConfigUseCase.getByTenantId(tenantId)           [Port-In]
  │   │
  │   └─ GetTenantConfigService.getByTenantId(tenantId)        [Implementation]
  │       │
  │       ├─ TenantId.of(tenantId)                             [VO 변환]
  │       │
  │       ├─ TenantReadManager.findById(TenantId)              [Manager]
  │       │   │
  │       │   └─ TenantQueryPort.findById(TenantId)            [Port-Out]
  │       │       │
  │       │       └─ TenantQueryAdapter.findById(TenantId)     [Adapter]
  │       │           │
  │       │           ├─ TenantQueryDslRepository.findByTenantId(String)
  │       │           │   └─ QueryDSL: SELECT * FROM tenants WHERE tenant_id = ?
  │       │           │
  │       │           └─ TenantJpaEntityMapper.toDomain(TenantJpaEntity)
  │       │               └─ → Tenant Aggregate
  │       │
  │       └─ TenantAssembler.toConfigResult(Tenant)
  │           └─ → TenantConfigResult
  │
  └─ InternalTenantConfigApiMapper.toApiResponse(TenantConfigResult)
      └─ → TenantConfigApiResponse
          └─ ApiResponse.ofSuccess(response)
```

---

## Layer별 상세 분석

### 1. Adapter-In Layer

#### 1.1 Controller

**파일**: `InternalTenantConfigController.java`

```java
@RestController
@RequestMapping(TENANTS)  // "/api/v1/internal/tenants"
public class InternalTenantConfigController {

    private final GetTenantConfigUseCase getTenantConfigUseCase;
    private final InternalTenantConfigApiMapper mapper;

    @GetMapping(TENANT_CONFIG)  // "/{tenantId}/config"
    public ApiResponse<TenantConfigApiResponse> getConfig(
            @PathVariable String tenantId) {

        // 1. UseCase 호출
        TenantConfigResult result = getTenantConfigUseCase.getByTenantId(tenantId);

        // 2. Mapper를 통한 API 응답 변환
        TenantConfigApiResponse response = mapper.toApiResponse(result);

        // 3. 표준 API 응답 래핑
        return ApiResponse.ofSuccess(response);
    }
}
```

**역할**:
- HTTP 요청을 받아 UseCase로 위임
- Application 결과를 API 응답으로 변환
- 표준 API 응답 포맷으로 래핑

**주입 의존성**:
- `GetTenantConfigUseCase` (Port-In)
- `InternalTenantConfigApiMapper` (Mapper)

---

#### 1.2 Response DTO

**파일**: `TenantConfigApiResponse.java`

```java
@Schema(description = "테넌트 설정")
public record TenantConfigApiResponse(
    @Schema(description = "테넌트 ID", example = "01234567-89ab-cdef-0123-456789abcdef")
    String tenantId,

    @Schema(description = "테넌트 이름", example = "Acme Corp")
    String name,

    @Schema(description = "테넌트 상태", example = "ACTIVE")
    String status,

    @Schema(description = "활성 여부", example = "true")
    boolean active
) {}
```

**필드**:
| 필드 | 타입 | 설명 |
|------|------|------|
| tenantId | String | 테넌트 ID (UUIDv7) |
| name | String | 테넌트 이름 |
| status | String | 테넌트 상태 (ACTIVE, INACTIVE) |
| active | boolean | 활성 여부 (Gateway 빠른 검증용) |

**특징**:
- Java Record (불변 객체)
- Swagger 어노테이션으로 API 문서화
- Gateway에서 테넌트 유효성 빠른 검증을 위한 최소 필드

---

#### 1.3 ApiMapper

**파일**: `InternalTenantConfigApiMapper.java`

```java
@Component
public class InternalTenantConfigApiMapper {

    public TenantConfigApiResponse toApiResponse(TenantConfigResult result) {
        return new TenantConfigApiResponse(
            result.tenantId(),
            result.name(),
            result.status(),
            result.active()
        );
    }
}
```

**역할**:
- Application Layer의 Result → API 응답 DTO 변환
- 1:1 필드 매핑 (단순 전달)

**변환 규칙**:
- `TenantConfigResult` → `TenantConfigApiResponse` (동일 구조)

---

### 2. Application Layer

#### 2.1 UseCase (Port-In)

**파일**: `GetTenantConfigUseCase.java`

```java
public interface GetTenantConfigUseCase {

    /**
     * 테넌트 ID로 설정 조회
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 설정
     * @throws TenantNotFoundException 존재하지 않는 테넌트인 경우
     */
    TenantConfigResult getByTenantId(String tenantId);
}
```

**역할**:
- Adapter-In과 Application의 경계 (Port-In)
- Gateway용 테넌트 설정 조회 계약 정의

**예외**:
- `TenantNotFoundException`: 존재하지 않는 테넌트

---

#### 2.2 Service (Implementation)

**파일**: `GetTenantConfigService.java`

```java
@Service
public class GetTenantConfigService implements GetTenantConfigUseCase {

    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    @Override
    public TenantConfigResult getByTenantId(String tenantId) {
        // 1. String → TenantId VO 변환
        TenantId id = TenantId.of(tenantId);

        // 2. Manager를 통한 Domain 조회
        Tenant tenant = readManager.findById(id);

        // 3. Domain → Result DTO 변환
        return assembler.toConfigResult(tenant);
    }
}
```

**역할**:
- UseCase 인터페이스 구현
- ReadManager 호출 및 Assembler를 통한 DTO 변환
- 트랜잭션 관리는 Manager에 위임

**주입 의존성**:
- `TenantReadManager` (Manager)
- `TenantAssembler` (Assembler)

**Zero-Tolerance 규칙 준수**:
- `@Service` 어노테이션 사용
- `@Transactional` 금지 (Manager에서 처리)
- Port 직접 호출 금지 (Manager 경유)
- Lombok 금지

---

#### 2.3 ReadManager

**파일**: `TenantReadManager.java`

```java
@Component
public class TenantReadManager {

    private final TenantQueryPort queryPort;

    @Transactional(readOnly = true)
    public Tenant findById(TenantId id) {
        return queryPort.findById(id)
            .orElseThrow(() -> new TenantNotFoundException(id));
    }
}
```

**역할**:
- QueryPort를 래핑하여 조회 작업 관리
- `@Transactional(readOnly=true)` 트랜잭션 경계 설정
- Port를 직접 노출하지 않고 Manager로 래핑 (C-005)

**트랜잭션**:
- `@Transactional(readOnly = true)` (메서드 단위)

---

#### 2.4 Assembler

**파일**: `TenantAssembler.java`

```java
@Component
public class TenantAssembler {

    public TenantConfigResult toConfigResult(Tenant tenant) {
        return new TenantConfigResult(
            tenant.tenantIdValue(),
            tenant.nameValue(),
            tenant.statusValue(),
            tenant.isActive()
        );
    }
}
```

**역할**:
- Domain Aggregate → Result DTO 변환
- Law of Demeter 준수 (Getter 체이닝 금지)

**변환 규칙**:
- `tenant.tenantIdValue()` → String
- `tenant.nameValue()` → String
- `tenant.statusValue()` → String
- `tenant.isActive()` → boolean

---

#### 2.5 Result DTO

**파일**: `TenantConfigResult.java`

```java
/**
 * RDTO-001: Response DTO는 Record로 정의
 * RDTO-008: Response DTO는 Domain 타입 의존 금지
 */
public record TenantConfigResult(
    String tenantId,
    String name,
    String status,
    boolean active
) {}
```

**역할**:
- Application Layer의 응답 DTO
- Domain 타입 의존 금지 (원시 타입 사용)

---

### 3. Domain Layer

#### 3.1 Domain Model

**파일**: `Tenant.java` (Aggregate Root)

```java
public final class Tenant {

    private final TenantId tenantId;
    private TenantName name;
    private TenantStatus status;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    // Factory Method - DB 재구성
    public static Tenant reconstitute(
        TenantId tenantId,
        TenantName name,
        TenantStatus status,
        DeletionStatus deletionStatus,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new Tenant(tenantId, name, status, deletionStatus, createdAt, updatedAt);
    }

    // Query Methods (Law of Demeter)
    public String tenantIdValue() {
        return tenantId.value();
    }

    public String nameValue() {
        return name.value();
    }

    public String statusValue() {
        return status.name();
    }

    public boolean isActive() {
        return status.isActive();
    }
}
```

**특징**:
- Lombok 금지, Plain Java
- Law of Demeter 준수 (Query Methods 제공)
- Tell, Don't Ask 패턴
- Long FK 전략 (JPA 관계 어노테이션 금지)

**Value Objects**:
- `TenantId`: String 기반 식별자 (UUIDv7)
- `TenantName`: 테넌트 이름
- `TenantStatus`: 테넌트 상태 (ACTIVE, INACTIVE)
- `DeletionStatus`: 삭제 상태

---

#### 3.2 Domain Port (Port-Out)

**파일**: `TenantQueryPort.java`

```java
public interface TenantQueryPort {

    /**
     * ID로 Tenant 단건 조회
     *
     * @param id Tenant ID (Value Object)
     * @return Tenant Domain (Optional)
     */
    Optional<Tenant> findById(TenantId id);

    // ... 기타 조회 메서드
}
```

**역할**:
- Application과 Adapter-Out의 경계 (Port-Out)
- Domain Aggregate 조회 계약 정의

**Zero-Tolerance 규칙**:
- 조회 메서드만 제공 (저장/수정/삭제 금지)
- Value Object 파라미터 (원시 타입 금지)
- Domain 반환 (DTO/Entity 반환 금지)
- Optional 반환 (null 방지)

---

### 4. Adapter-Out Layer

#### 4.1 Adapter

**파일**: `TenantQueryAdapter.java`

```java
@Component
public class TenantQueryAdapter implements TenantQueryPort {

    private final TenantQueryDslRepository repository;
    private final TenantJpaEntityMapper mapper;

    @Override
    public Optional<Tenant> findById(TenantId tenantId) {
        // 1. TenantId VO → String 추출
        String tenantIdValue = tenantId.value().toString();

        // 2. QueryDSL Repository로 조회
        return repository.findByTenantId(tenantIdValue)
            // 3. Entity → Domain 변환
            .map(mapper::toDomain);
    }
}
```

**역할**:
- TenantQueryPort 구현
- Repository 호출 및 Entity → Domain 변환
- `@Transactional` 금지 (Manager에서 관리)

**주입 의존성**:
- `TenantQueryDslRepository` (Repository)
- `TenantJpaEntityMapper` (Mapper)

**1:1 매핑 원칙**:
- Repository 1개 + Mapper 1개만 허용
- 다른 Repository 주입 금지

---

#### 4.2 Repository

**파일**: `TenantQueryDslRepository.java`

```java
@Repository
public class TenantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final TenantConditionBuilder conditionBuilder;

    public Optional<TenantJpaEntity> findByTenantId(String tenantId) {
        TenantJpaEntity result = queryFactory
            .selectFrom(tenantJpaEntity)
            .where(conditionBuilder.tenantIdEquals(tenantId))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
```

**역할**:
- QueryDSL 기반 조회 (Query 전용)
- Entity 반환 (Domain 변환은 Adapter에서)

**CQRS 패턴**:
- **Command**: `TenantJpaRepository` (JPA)
- **Query**: `TenantQueryDslRepository` (QueryDSL)

**규칙**:
- Join 금지 (N+1 해결은 Application Layer에서)
- 비즈니스 로직 금지
- ConditionBuilder를 사용하여 조건 생성

---

#### 4.3 Entity

**파일**: `TenantJpaEntity.java`

```java
@Entity
@Table(name = "tenants")
public class TenantJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TenantStatus status;

    // JPA 기본 생성자 (protected)
    protected TenantJpaEntity() {}

    // 스태틱 팩토리 메서드
    public static TenantJpaEntity of(
        String tenantId,
        String name,
        TenantStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
    ) {
        return new TenantJpaEntity(tenantId, name, status, createdAt, updatedAt, deletedAt);
    }
}
```

**특징**:
- UUIDv7 PK 전략 (String 타입)
- SoftDeletableEntity 상속 (createdAt, updatedAt, deletedAt)
- Lombok 금지 (Plain Java)
- Setter 제공 금지

**테이블 매핑**:
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| tenant_id | VARCHAR(36) | PK, NOT NULL | 테넌트 ID (UUIDv7) |
| name | VARCHAR(100) | NOT NULL, UNIQUE | 테넌트 이름 |
| status | VARCHAR(20) | NOT NULL | 테넌트 상태 (ACTIVE, INACTIVE) |
| created_at | TIMESTAMP | NOT NULL | 생성 일시 (UTC) |
| updated_at | TIMESTAMP | NOT NULL | 수정 일시 (UTC) |
| deleted_at | TIMESTAMP | NULLABLE | 삭제 일시 (UTC) |

---

#### 4.4 EntityMapper

**파일**: `TenantJpaEntityMapper.java`

```java
@Component
public class TenantJpaEntityMapper {

    public Tenant toDomain(TenantJpaEntity entity) {
        return Tenant.reconstitute(
            TenantId.of(entity.getTenantId()),
            TenantName.of(entity.getName()),
            entity.getStatus(),
            DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public TenantJpaEntity toEntity(Tenant domain) {
        DeletionStatus deletionStatus = domain.getDeletionStatus();
        return TenantJpaEntity.of(
            domain.tenantIdValue().toString(),
            domain.nameValue(),
            domain.getStatus(),
            domain.createdAt(),
            domain.updatedAt(),
            deletionStatus.deletedAt()
        );
    }
}
```

**역할**:
- Entity ↔ Domain 양방향 변환
- Domain은 JPA 의존성 없음 (Hexagonal Architecture)

**변환 규칙**:
- `TenantId.of(String)` → VO 생성
- `TenantName.of(String)` → VO 생성
- `DeletionStatus.reconstitute()` → VO 재구성
- `Instant` → `Instant` (직접 전달, 변환 없음)

---

## Database Query 분석

### 대상 테이블

```sql
CREATE TABLE tenants (
    tenant_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    INDEX idx_name (name),
    INDEX idx_status (status)
);
```

### 실행 쿼리

```sql
SELECT
    tenant_id,
    name,
    status,
    created_at,
    updated_at,
    deleted_at
FROM tenants
WHERE tenant_id = ?
```

**파라미터**:
- `?` = tenantId (String, 예: "01234567-89ab-cdef-0123-456789abcdef")

**인덱스 사용**:
- PRIMARY KEY (tenant_id) 사용

**성능**:
- 단일 행 조회 (PK 조회)
- 인덱스 스캔 (매우 빠름)

---

## 예외 처리

### TenantNotFoundException

**발생 위치**: `TenantReadManager.findById()`

```java
@Transactional(readOnly = true)
public Tenant findById(TenantId id) {
    return queryPort.findById(id)
        .orElseThrow(() -> new TenantNotFoundException(id));
}
```

**예외 정보**:
- **타입**: `TenantNotFoundException` (Domain Exception)
- **메시지**: "테넌트를 찾을 수 없습니다: {tenantId}"
- **HTTP Status**: 404 NOT_FOUND (ErrorHandler에서 처리)

**발생 조건**:
- 존재하지 않는 tenantId로 조회 시

---

## 데이터 흐름 요약

### Request Flow (입력)

```
HTTP Request (tenantId: String)
  ↓
Controller: String
  ↓
UseCase/Service: String
  ↓
Domain VO: TenantId.of(String)
  ↓
Manager: TenantId
  ↓
Port: TenantId
  ↓
Adapter: String (TenantId.value())
  ↓
Repository: String
  ↓
Database: VARCHAR(36)
```

### Response Flow (출력)

```
Database: TenantJpaEntity
  ↓
EntityMapper.toDomain(): Tenant
  ↓
Adapter: Tenant
  ↓
Port: Optional<Tenant>
  ↓
Manager: Tenant (orElseThrow)
  ↓
Service: Tenant
  ↓
Assembler.toConfigResult(): TenantConfigResult
  ↓
Service: TenantConfigResult
  ↓
ApiMapper.toApiResponse(): TenantConfigApiResponse
  ↓
Controller: ApiResponse<TenantConfigApiResponse>
  ↓
HTTP Response: JSON
```

---

## Hexagonal Architecture 레이어 매핑

```
┌─────────────────────────────────────────────────────────────┐
│ Adapter-In Layer (REST)                                      │
│  - InternalTenantConfigController                            │
│  - TenantConfigApiResponse (DTO)                             │
│  - InternalTenantConfigApiMapper                             │
└─────────────────────────────────────────────────────────────┘
                          ↓ Port-In
┌─────────────────────────────────────────────────────────────┐
│ Application Layer                                            │
│  - GetTenantConfigUseCase (Port-In)                          │
│  - GetTenantConfigService                                    │
│  - TenantReadManager                                         │
│  - TenantAssembler                                           │
│  - TenantConfigResult (DTO)                                  │
└─────────────────────────────────────────────────────────────┘
                          ↓ Port-Out
┌─────────────────────────────────────────────────────────────┐
│ Domain Layer                                                 │
│  - Tenant (Aggregate Root)                                   │
│  - TenantId, TenantName, TenantStatus (Value Objects)        │
│  - TenantQueryPort (Port-Out)                                │
└─────────────────────────────────────────────────────────────┘
                          ↓ Implementation
┌─────────────────────────────────────────────────────────────┐
│ Adapter-Out Layer (Persistence)                              │
│  - TenantQueryAdapter                                        │
│  - TenantQueryDslRepository                                  │
│  - TenantJpaRepository                                       │
│  - TenantJpaEntity                                           │
│  - TenantJpaEntityMapper                                     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ Database (MySQL)                                             │
│  - tenants 테이블                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 주요 설계 원칙

### 1. Hexagonal Architecture (Port & Adapter)
- **Port-In**: `GetTenantConfigUseCase` (Application ← Adapter-In)
- **Port-Out**: `TenantQueryPort` (Application → Adapter-Out)
- **Adapter-In**: `InternalTenantConfigController`
- **Adapter-Out**: `TenantQueryAdapter`

### 2. CQRS (Command Query Responsibility Segregation)
- **Query**: `TenantQueryDslRepository` (QueryDSL)
- **Command**: `TenantJpaRepository` (JPA)

### 3. DDD (Domain-Driven Design)
- **Aggregate Root**: `Tenant`
- **Value Objects**: `TenantId`, `TenantName`, `TenantStatus`, `DeletionStatus`
- **Factory Method**: `Tenant.reconstitute()`

### 4. Law of Demeter
- Aggregate는 Query Methods 제공 (`tenantIdValue()`, `nameValue()`)
- Getter 체이닝 금지

### 5. Long FK Strategy
- JPA 관계 어노테이션 금지
- String/Long FK만 사용

### 6. Zero-Tolerance Rules
- Lombok 금지 (Plain Java)
- `@Transactional`은 Manager에서만 (Service 금지)
- Port 직접 호출 금지 (Manager 경유)

---

## 보안 고려사항

### 1. Internal API 보호
- **서비스 토큰 인증** 필요 (구현 필요)
- **내부 네트워크 접근 제한** 필요

### 2. 데이터 노출 최소화
- Gateway 검증에 필요한 최소 필드만 제공
- 민감 정보 제외 (이메일, 연락처 등)

### 3. 예외 처리
- `TenantNotFoundException` 발생 시 404 응답
- 예외 메시지에 민감 정보 포함하지 않음

---

## 성능 최적화

### 1. 인덱스 활용
- PRIMARY KEY (tenant_id) 사용
- 단일 행 조회 (매우 빠름)

### 2. 트랜잭션 최적화
- `@Transactional(readOnly = true)` (읽기 전용)
- Dirty Checking 비활성화

### 3. 캐싱 고려사항
- Gateway에서 Redis 캐싱 권장
- TTL 설정 필요 (테넌트 상태 변경 반영)

---

## 테스트 시나리오

### 1. 정상 흐름 (200 OK)

**Request**:
```http
GET /api/v1/internal/tenants/01234567-89ab-cdef-0123-456789abcdef/config
```

**Response**:
```json
{
  "status": "SUCCESS",
  "data": {
    "tenantId": "01234567-89ab-cdef-0123-456789abcdef",
    "name": "Acme Corp",
    "status": "ACTIVE",
    "active": true
  },
  "error": null
}
```

### 2. 존재하지 않는 테넌트 (404 NOT_FOUND)

**Request**:
```http
GET /api/v1/internal/tenants/non-existent-id/config
```

**Response**:
```json
{
  "status": "ERROR",
  "data": null,
  "error": {
    "code": "TENANT_NOT_FOUND",
    "message": "테넌트를 찾을 수 없습니다: non-existent-id"
  }
}
```

### 3. 비활성 테넌트 조회 (200 OK, active=false)

**Request**:
```http
GET /api/v1/internal/tenants/inactive-tenant-id/config
```

**Response**:
```json
{
  "status": "SUCCESS",
  "data": {
    "tenantId": "inactive-tenant-id",
    "name": "Inactive Tenant",
    "status": "INACTIVE",
    "active": false
  },
  "error": null
}
```

---

## 관련 문서

- **API 문서**: Swagger UI (`/swagger-ui.html`)
- **도메인 모델**: `Tenant.java`
- **Port 정의**: `GetTenantConfigUseCase.java`, `TenantQueryPort.java`
- **데이터베이스 스키마**: `V1__init.sql`

---

## 작성 정보

- **작성일**: 2026-02-05
- **프로젝트**: AuthHub
- **모듈**: adapter-in/rest-api, application, domain, adapter-out/persistence-mysql
- **아키텍처**: Hexagonal + CQRS + DDD
