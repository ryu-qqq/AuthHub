# API Flow: InternalOnboardingController.onboarding

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | POST |
| Path | /api/v1/internal/onboarding |
| Controller | InternalOnboardingController |
| Method | onboarding |
| UseCase | OnboardingUseCase.execute |
| Service | OnboardingService |
| 특이사항 | X-Idempotency-Key 헤더 필수 (멱등키) |
| 트랜잭션 범위 | OnboardingFacade.persist (@Transactional) |

---

## 호출 흐름 다이어그램

```
[Adapter-In Layer]
InternalOnboardingController.onboarding(idempotencyKey, OnboardingApiRequest)
  ├─ InternalOnboardingApiMapper.toCommand(request, idempotencyKey)
  │   └─ → OnboardingCommand
  │
  ├─ OnboardingUseCase.execute(command)                        [Port-In]
  │   └─ OnboardingService.execute(command)                    [Impl]
  │       ├─ 1. OnboardingIdempotencyQueryManager.findByIdempotencyKey(key)
  │       │   └─ (Cache Hit → 즉시 OnboardingResult 반환)
  │       │
  │       ├─ 2. OnboardingValidator.validateNameNotDuplicated(TenantName)
  │       │   └─ TenantReadManager.existsByName()
  │       │       └─ DuplicateTenantNameException (중복 시)
  │       │
  │       ├─ 3. OnboardingFactory.create(command)
  │       │   ├─ TimeProvider.now() → Instant
  │       │   ├─ IdGeneratorPort.generate() → UUIDv7
  │       │   ├─ Tenant.create(TenantId, TenantName, now)
  │       │   ├─ Organization.create(OrganizationId, TenantId, OrganizationName, now)
  │       │   └─ → OnboardingBundle(Tenant, Organization)
  │       │
  │       ├─ 4. OnboardingFacade.persist(bundle)               [@Transactional]
  │       │   ├─ TenantCommandManager.persist(tenant)          [@Transactional]
  │       │   │   └─ TenantCommandPort.persist(tenant)         [Port-Out]
  │       │   │       └─ TenantCommandAdapter.persist(tenant)  [Adapter-Out]
  │       │   │           ├─ TenantJpaEntityMapper.toEntity(tenant)
  │       │   │           ├─ TenantJpaRepository.save(entity)
  │       │   │           └─ → tenantId (String)
  │       │   │
  │       │   ├─ OrganizationCommandManager.persist(organization) [@Transactional]
  │       │   │   └─ OrganizationCommandPort.persist(organization) [Port-Out]
  │       │   │       └─ OrganizationCommandAdapter.persist(organization) [Adapter-Out]
  │       │   │           ├─ OrganizationJpaEntityMapper.toEntity(organization)
  │       │   │           ├─ OrganizationJpaRepository.save(entity)
  │       │   │           └─ → organizationId (String)
  │       │   │
  │       │   └─ → OnboardingResult(tenantId, organizationId)
  │       │
  │       └─ 5. OnboardingIdempotencyCommandManager.save(key, result)
  │           └─ (Redis 캐시 저장, TTL: 24시간)
  │
  └─ InternalOnboardingApiMapper.toApiResponse(result)
      └─ → OnboardingResultApiResponse
          └─ ApiResponse.ofSuccess(response)
```

---

## 1. Adapter-In Layer

### 1.1 Controller

**파일**: `InternalOnboardingController.java`

**위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/controller/`

**메서드 시그니처**:
```java
@PostMapping
public ApiResponse<OnboardingResultApiResponse> onboarding(
    @RequestHeader(value = "X-Idempotency-Key") String idempotencyKey,
    @Valid @RequestBody OnboardingApiRequest request
)
```

**주요 역할**:
- HTTP 요청 수신 및 검증 (`@Valid`)
- 멱등키 헤더 추출 (`X-Idempotency-Key`)
- ApiMapper를 통한 DTO 변환
- UseCase 호출 및 응답 반환

**특이사항**:
- 멱등키 필수 (헤더 누락 시 400 Bad Request)
- 서비스 토큰 인증으로 보호 (내부 네트워크 전용)

### 1.2 Request DTO

**파일**: `OnboardingApiRequest.java`

**위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/command/`

**구조**:
```java
public record OnboardingApiRequest(
    @NotBlank(message = "테넌트 이름은 필수입니다")
    String tenantName,

    @NotBlank(message = "조직 이름은 필수입니다")
    String organizationName
)
```

**Validation**:
- `tenantName`: 필수 (NotBlank)
- `organizationName`: 필수 (NotBlank)

**예시**:
```json
{
  "tenantName": "my-tenant",
  "organizationName": "default-org"
}
```

### 1.3 Response DTO

**파일**: `OnboardingResultApiResponse.java`

**위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/`

**구조**:
```java
public record OnboardingResultApiResponse(
    String tenantId,      // UUIDv7
    String organizationId // UUIDv7
)
```

**예시**:
```json
{
  "tenantId": "01933abc-1234-7000-8000-000000000001",
  "organizationId": "01933abc-1234-7000-8000-000000000002"
}
```

### 1.4 ApiMapper

**파일**: `InternalOnboardingApiMapper.java`

**위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/mapper/`

**매핑 메서드**:

#### 1) Request → Command
```java
public OnboardingCommand toCommand(OnboardingApiRequest request, String idempotencyKey) {
    return new OnboardingCommand(
        request.tenantName(),
        request.organizationName(),
        idempotencyKey
    );
}
```

#### 2) Result → ApiResponse
```java
public OnboardingResultApiResponse toApiResponse(OnboardingResult result) {
    return new OnboardingResultApiResponse(
        result.tenantId(),
        result.organizationId()
    );
}
```

---

## 2. Application Layer

### 2.1 UseCase (Port-In)

**파일**: `OnboardingUseCase.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/port/in/command/`

**인터페이스 정의**:
```java
public interface OnboardingUseCase {
    OnboardingResult execute(OnboardingCommand command);
}
```

**Zero-Tolerance 규칙**:
- `{Action}{Bc}UseCase` 네이밍 준수
- `execute()` 메서드 시그니처 고정
- Command DTO 파라미터, Result DTO 반환

### 2.2 Service (Implementation)

**파일**: `OnboardingService.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/service/command/`

**클래스 정의**:
```java
@Service
public class OnboardingService implements OnboardingUseCase
```

**주입 의존성**:
- `OnboardingFacade`: 트랜잭션 조율
- `OnboardingFactory`: 도메인 객체 생성
- `OnboardingValidator`: 비즈니스 규칙 검증
- `OnboardingIdempotencyQueryManager`: 멱등키 조회
- `OnboardingIdempotencyCommandManager`: 멱등키 저장

**실행 흐름**:

```java
@Override
public OnboardingResult execute(OnboardingCommand command) {
    String idempotencyKey = command.idempotencyKey();

    // 1. 멱등키 캐시 조회 (Redis)
    Optional<OnboardingResult> cached =
        idempotencyQueryManager.findByIdempotencyKey(idempotencyKey);
    if (cached.isPresent()) {
        log.info("Idempotency cache hit for key: {}", idempotencyKey);
        return cached.get();
    }

    // 2. 테넌트 이름 중복 검증
    validator.validateNameNotDuplicated(TenantName.of(command.tenantName()));

    // 3. 도메인 번들 생성 (Factory)
    OnboardingBundle bundle = onboardingFactory.create(command);

    // 4. 트랜잭션 영속화 (Facade)
    OnboardingResult result = onboardingFacade.persist(bundle);

    // 5. 결과 캐싱 (Redis, 24시간 TTL)
    idempotencyCommandManager.save(idempotencyKey, result);

    return result;
}
```

**멱등성 보장 메커니즘**:
- **캐시 히트**: 동일 멱등키 → 저장된 응답 즉시 반환 (DB 접근 없음)
- **캐시 미스**: 온보딩 실행 → 결과 캐싱 (TTL: 24시간)
- **네트워크 재시도**: 동일 멱등키로 재요청 시 중복 생성 방지

### 2.3 Command DTO

**파일**: `OnboardingCommand.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/dto/command/`

**구조**:
```java
public record OnboardingCommand(
    String tenantName,
    String organizationName,
    String idempotencyKey
) {
    public OnboardingCommand {
        Objects.requireNonNull(tenantName, "tenantName must not be null");
        Objects.requireNonNull(organizationName, "organizationName must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
        if (idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
    }
}
```

**검증**:
- Compact Constructor에서 null/blank 검증
- jakarta.validation 금지 (순수 Java Record)

### 2.4 Result DTO

**파일**: `OnboardingResult.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/dto/response/`

**구조**:
```java
public record OnboardingResult(
    String tenantId,      // UUIDv7
    String organizationId // UUIDv7
)
```

### 2.5 Factory

**파일**: `OnboardingFactory.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/factory/`

**클래스 정의**:
```java
@Component
public class OnboardingFactory
```

**주입 의존성**:
- `TimeProvider`: 현재 시간 생성 (Instant)
- `IdGeneratorPort`: UUIDv7 생성

**생성 로직**:
```java
public OnboardingBundle create(OnboardingCommand command) {
    Instant now = timeProvider.now();

    // 1. Tenant 생성
    TenantId tenantId = TenantId.forNew(idGeneratorPort.generate());
    Tenant tenant = Tenant.create(
        tenantId,
        TenantName.of(command.tenantName()),
        now
    );

    // 2. Organization 생성
    OrganizationId organizationId = OrganizationId.forNew(idGeneratorPort.generate());
    Organization organization = Organization.create(
        organizationId,
        tenant.getTenantId(),
        OrganizationName.of(command.organizationName()),
        now
    );

    return new OnboardingBundle(tenant, organization);
}
```

**책임**:
- 시간 생성 (외부 주입)
- ID 생성 (IdGeneratorPort 위임)
- 도메인 Aggregate 생성

**Zero-Tolerance 규칙**:
- Service에서 도메인 객체 직접 생성 금지 (C-006)
- Factory에서만 시간/ID 생성 허용

### 2.6 Bundle DTO (내부 전용)

**파일**: `OnboardingBundle.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/dto/bundle/`

**구조**:
```java
public record OnboardingBundle(
    Tenant tenant,
    Organization organization
)
```

**사용 범위**: Application Layer 내부 전용

### 2.7 Facade (트랜잭션 조율)

**파일**: `OnboardingFacade.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/internal/`

**클래스 정의**:
```java
@Component
public class OnboardingFacade
```

**주입 의존성**:
- `TenantCommandManager`
- `OrganizationCommandManager`

**트랜잭션 처리**:
```java
@Transactional
public OnboardingResult persist(OnboardingBundle bundle) {
    // 1. Tenant 저장
    String tenantId = tenantCommandManager.persist(bundle.tenant());

    // 2. Organization 저장
    String organizationId = organizationCommandManager.persist(bundle.organization());

    return new OnboardingResult(tenantId, organizationId);
}
```

**트랜잭션 범위**:
- 메서드 단위 `@Transactional`
- Tenant → Organization 순차 저장
- 원자성 보장 (둘 다 성공 또는 둘 다 롤백)

### 2.8 Validator

**파일**: `OnboardingValidator.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/validator/`

**클래스 정의**:
```java
@Component
public class OnboardingValidator
```

**검증 메서드**:
```java
public void validateNameNotDuplicated(TenantName name) {
    if (readManager.existsByName(name)) {
        throw new DuplicateTenantNameException(name);
    }
}
```

**Zero-Tolerance 규칙**:
- void 반환 (VAL-004)
- 실패 시 DomainException 발생

### 2.9 Manager (Port 래핑)

#### TenantCommandManager
**파일**: `TenantCommandManager.java`

```java
@Component
public class TenantCommandManager {
    private final TenantCommandPort persistencePort;

    @Transactional
    public String persist(Tenant tenant) {
        return persistencePort.persist(tenant);
    }
}
```

#### OrganizationCommandManager
**파일**: `OrganizationCommandManager.java`

```java
@Component
public class OrganizationCommandManager {
    private final OrganizationCommandPort persistencePort;

    @Transactional
    public String persist(Organization organization) {
        return persistencePort.persist(organization);
    }
}
```

**책임**:
- CommandPort 래핑
- 메서드 단위 트랜잭션 경계 설정
- Port 직접 노출 방지 (C-005)

---

## 3. Domain Layer

### 3.1 Domain Aggregate

#### 3.1.1 Tenant

**파일**: `Tenant.java`

**위치**: `domain/src/main/java/com/ryuqq/authhub/domain/tenant/aggregate/`

**생성 메서드**:
```java
public static Tenant create(TenantId tenantId, TenantName name, Instant now) {
    return new Tenant(
        tenantId,
        name,
        TenantStatus.ACTIVE,
        DeletionStatus.active(),
        now,
        now
    );
}
```

**필드**:
- `tenantId`: TenantId (VO, UUIDv7)
- `name`: TenantName (VO)
- `status`: TenantStatus (ACTIVE/INACTIVE)
- `deletionStatus`: DeletionStatus (soft delete)
- `createdAt`: Instant (불변)
- `updatedAt`: Instant (가변)

**비즈니스 메서드**:
- `changeName(TenantName, Instant)`
- `changeStatus(TenantStatus, Instant)`
- `delete(Instant)` - Soft Delete
- `restore(Instant)`

#### 3.1.2 Organization

**파일**: `Organization.java`

**위치**: `domain/src/main/java/com/ryuqq/authhub/domain/organization/aggregate/`

**생성 메서드**:
```java
public static Organization create(
    OrganizationId organizationId,
    TenantId tenantId,
    OrganizationName name,
    Instant now
) {
    return new Organization(
        organizationId,
        tenantId,
        name,
        OrganizationStatus.ACTIVE,
        DeletionStatus.active(),
        now,
        now
    );
}
```

**필드**:
- `organizationId`: OrganizationId (VO, UUIDv7)
- `tenantId`: TenantId (FK, String)
- `name`: OrganizationName (VO)
- `status`: OrganizationStatus (ACTIVE/INACTIVE)
- `deletionStatus`: DeletionStatus (soft delete)
- `createdAt`: Instant (불변)
- `updatedAt`: Instant (가변)

**Zero-Tolerance 규칙**:
- Lombok 금지 (Plain Java)
- Long FK 전략 (JPA 관계 어노테이션 금지)
- Law of Demeter 준수
- Tell, Don't Ask 패턴

### 3.2 Domain Port (Port-Out)

#### 3.2.1 TenantCommandPort

**파일**: `TenantCommandPort.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/tenant/port/out/command/`

**인터페이스 정의**:
```java
public interface TenantCommandPort {
    String persist(Tenant tenant);
}
```

**규칙**:
- `persist()` 메서드만 제공 (save/update/delete 분리 금지)
- Domain Aggregate 파라미터
- ID 반환 (String 원시 타입)
- 조회 메서드 금지 (QueryPort로 분리)

#### 3.2.2 OrganizationCommandPort

**파일**: `OrganizationCommandPort.java`

**위치**: `application/src/main/java/com/ryuqq/authhub/application/organization/port/out/command/`

**인터페이스 정의**:
```java
public interface OrganizationCommandPort {
    String persist(Organization organization);
}
```

---

## 4. Adapter-Out Layer

### 4.1 Adapter (Port 구현체)

#### 4.1.1 TenantCommandAdapter

**파일**: `TenantCommandAdapter.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/adapter/`

**클래스 정의**:
```java
@Component
public class TenantCommandAdapter implements TenantCommandPort
```

**주입 의존성**:
- `TenantJpaRepository`
- `TenantJpaEntityMapper`

**구현 로직**:
```java
@Override
public String persist(Tenant tenant) {
    // 1. Domain → Entity 변환 (Mapper)
    TenantJpaEntity entity = mapper.toEntity(tenant);

    // 2. Entity 저장 (JpaRepository)
    TenantJpaEntity savedEntity = repository.save(entity);

    // 3. ID 반환
    return savedEntity.getTenantId();
}
```

**Hibernate Dirty Checking**:
- 같은 ID 존재 → UPDATE
- 새로운 ID → INSERT
- Hibernate가 자동 판단

**규칙**:
- `@Transactional` 금지 (Manager/Facade에서 관리)
- 비즈니스 로직 금지 (단순 위임 + 변환)

#### 4.1.2 OrganizationCommandAdapter

**파일**: `OrganizationCommandAdapter.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/organization/adapter/`

**구현 로직**:
```java
@Override
public String persist(Organization organization) {
    OrganizationJpaEntity entity = mapper.toEntity(organization);
    OrganizationJpaEntity savedEntity = repository.save(entity);
    return savedEntity.getOrganizationId();
}
```

### 4.2 Repository (Spring Data JPA)

#### 4.2.1 TenantJpaRepository

**파일**: `TenantJpaRepository.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/repository/`

**인터페이스 정의**:
```java
public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, String>
```

**PK 전략**: UUIDv7 (String)

**CQRS 분리**:
- Command: `TenantJpaRepository` (JPA)
- Query: `TenantQueryDslRepository` (QueryDSL)

#### 4.2.2 OrganizationJpaRepository

**파일**: `OrganizationJpaRepository.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/organization/repository/`

**인터페이스 정의**:
```java
public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, String>
```

### 4.3 Entity (JPA)

#### 4.3.1 TenantJpaEntity

**파일**: `TenantJpaEntity.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/entity/`

**테이블 매핑**:
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
}
```

**상속**: `SoftDeletableEntity`
- `createdAt`, `updatedAt` (BaseAuditEntity)
- `deletedAt` (SoftDeletableEntity)

**생성 메서드**:
```java
public static TenantJpaEntity of(
    String tenantId,
    String name,
    TenantStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
)
```

**규칙**:
- Lombok 금지
- Setter 제공 금지
- protected 기본 생성자 (JPA 스펙)

#### 4.3.2 OrganizationJpaEntity

**파일**: `OrganizationJpaEntity.java`

**위치**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/organization/entity/`

**테이블 매핑**:
```java
@Entity
@Table(
    name = "organizations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_organizations_tenant_name",
            columnNames = {"tenant_id", "name"}
        )
    },
    indexes = {
        @Index(name = "idx_organizations_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_organizations_status", columnList = "status")
    }
)
public class OrganizationJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(name = "organization_id", nullable = false, length = 36)
    private String organizationId;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatus status;
}
```

**제약조건**:
- UK: `(tenant_id, name)` - 테넌트 내 조직 이름 중복 방지
- Index: `tenant_id`, `status`

**FK 전략**: String FK (JPA 관계 어노테이션 금지)

---

## 5. Database Schema

### 5.1 tenants 테이블

```sql
CREATE TABLE tenants (
    tenant_id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) NULL
);
```

**PK**: `tenant_id` (UUIDv7)
**UK**: `name`

### 5.2 organizations 테이블

```sql
CREATE TABLE organizations (
    organization_id VARCHAR(36) NOT NULL PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    deleted_at TIMESTAMP(6) NULL,

    CONSTRAINT uk_organizations_tenant_name UNIQUE (tenant_id, name),
    INDEX idx_organizations_tenant_id (tenant_id),
    INDEX idx_organizations_status (status)
);
```

**PK**: `organization_id` (UUIDv7)
**UK**: `(tenant_id, name)`
**FK**: `tenant_id` → `tenants.tenant_id` (Application 레벨 관리)

---

## 6. 트랜잭션 경계

### 6.1 트랜잭션 범위

```
[트랜잭션 시작] OnboardingFacade.persist()
  ├─ [트랜잭션 참여] TenantCommandManager.persist()
  │   └─ TenantCommandAdapter.persist()
  │       └─ TenantJpaRepository.save() → INSERT
  │
  └─ [트랜잭션 참여] OrganizationCommandManager.persist()
      └─ OrganizationCommandAdapter.persist()
          └─ OrganizationJpaRepository.save() → INSERT
[트랜잭션 커밋]
```

### 6.2 트랜잭션 전파 (REQUIRED)

- **Facade**: `@Transactional` (트랜잭션 시작)
- **Manager**: `@Transactional` (기존 트랜잭션 참여)
- **Adapter**: `@Transactional` 없음 (Manager 트랜잭션 활용)

### 6.3 원자성 보장

- Tenant 저장 실패 → 전체 롤백
- Organization 저장 실패 → 전체 롤백 (Tenant도 롤백)
- 둘 다 성공해야 커밋

---

## 7. 멱등성 보장 메커니즘

### 7.1 멱등키 처리 흐름

```
1. 요청 수신 (X-Idempotency-Key: abc-123)
   ↓
2. OnboardingIdempotencyQueryManager.findByIdempotencyKey("abc-123")
   ├─ Cache Hit (Redis) → 저장된 OnboardingResult 반환 (즉시 종료)
   └─ Cache Miss → 3단계로 진행
   ↓
3. 온보딩 실행 (Validation → Factory → Facade → Persist)
   ↓
4. OnboardingIdempotencyCommandManager.save("abc-123", result)
   └─ Redis 캐시 저장 (TTL: 24시간)
   ↓
5. OnboardingResult 반환
```

### 7.2 Redis 캐시 구조

**Key**: `onboarding:idempotency:{idempotencyKey}`
**Value**: `OnboardingResult` (JSON)
**TTL**: 24시간 (86,400초)

### 7.3 멱등성 시나리오

#### 시나리오 1: 정상 처리
```
Request 1 (key: abc-123) → Cache Miss → 온보딩 실행 → 캐시 저장
→ Response: {tenantId: "uuid1", organizationId: "uuid2"}
```

#### 시나리오 2: 네트워크 재시도
```
Request 2 (key: abc-123) → Cache Hit → 저장된 응답 반환
→ Response: {tenantId: "uuid1", organizationId: "uuid2"} (동일)
```

#### 시나리오 3: TTL 만료 후 재요청
```
Request 3 (key: abc-123, 25시간 후) → Cache Miss
→ Validation 단계에서 DuplicateTenantNameException 발생
→ 400 Bad Request
```

---

## 8. 예외 처리

### 8.1 비즈니스 예외

| 예외 | 발생 조건 | HTTP 상태 | 응답 메시지 |
|------|----------|----------|------------|
| `DuplicateTenantNameException` | 테넌트 이름 중복 | 400 | "이미 존재하는 테넌트 이름입니다" |
| `IllegalArgumentException` | idempotencyKey 누락/공백 | 400 | "멱등키는 필수이며 공백일 수 없습니다" |

### 8.2 시스템 예외

| 예외 | 발생 조건 | HTTP 상태 |
|------|----------|----------|
| `TransactionRollbackException` | DB 저장 실패 | 500 |
| `DataIntegrityViolationException` | UK 위반 (동시성) | 500 |

---

## 9. 성능 최적화

### 9.1 멱등키 캐싱

**효과**:
- Cache Hit 시 DB 접근 없음 (응답 시간 < 10ms)
- 네트워크 재시도 시 부하 방지

**Trade-off**:
- Redis 메모리 사용 (TTL 24시간)
- 캐시 동기화 이슈 없음 (불변 데이터)

### 9.2 UUIDv7 PK 전략

**장점**:
- B-tree 인덱스 성능 우수 (시간순 정렬 가능)
- 분산 환경에서 충돌 없는 고유 ID 생성
- AUTO_INCREMENT 대비 수평 확장 용이

**단점**:
- VARCHAR(36) 저장 (BIGINT 대비 약간 큰 용량)

### 9.3 트랜잭션 최적화

**전략**:
- Facade 단위로 트랜잭션 묶기 (원자성 보장)
- Adapter는 `@Transactional` 없음 (불필요한 프록시 제거)

---

## 10. 보안 고려사항

### 10.1 인증/인가

**인증**: 서비스 토큰 (내부 네트워크 전용)
**인가**: 없음 (Internal API로 제한)

### 10.2 멱등키 보안

**UUID 권장**: 예측 불가능한 랜덤 값
**TTL 제한**: 24시간 후 자동 만료

---

## 11. 모니터링 포인트

### 11.1 비즈니스 메트릭

- 온보딩 성공률 (%)
- 멱등키 Cache Hit Rate (%)
- 평균 응답 시간 (ms)

### 11.2 기술 메트릭

- Redis 캐시 조회 성공/실패
- DB 트랜잭션 커밋/롤백
- DuplicateTenantNameException 발생 빈도

---

## 12. 개선 가능 영역

### 12.1 동시성 제어

**현재**: Redis 캐시 + DB UK
**개선안**: 분산 락 (Redisson) 추가

```java
@Transactional
public OnboardingResult execute(OnboardingCommand command) {
    RLock lock = redissonClient.getLock("onboarding:" + command.tenantName());
    try {
        lock.lock(5, TimeUnit.SECONDS);
        // 온보딩 로직
    } finally {
        lock.unlock();
    }
}
```

### 12.2 비동기 처리

**현재**: 동기 처리
**개선안**: 이벤트 기반 비동기 처리

```java
// Facade
applicationEventPublisher.publishEvent(new TenantCreatedEvent(tenant));
applicationEventPublisher.publishEvent(new OrganizationCreatedEvent(organization));

// EventListener
@Async
@EventListener
public void handleTenantCreated(TenantCreatedEvent event) {
    // 후속 처리 (알림, 로깅 등)
}
```

---

## 참고 문서

- `/api/v1/internal/onboarding` OpenAPI 스펙
- Redis 멱등키 관리 가이드
- Hexagonal Architecture 레이어 규칙
- UUIDv7 생성 전략
