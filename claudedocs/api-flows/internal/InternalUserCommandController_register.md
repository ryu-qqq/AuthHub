# API Flow: InternalUserCommandController.register

## 기본 정보

| 항목 | 값 |
|------|-----|
| HTTP Method | POST |
| Path | /api/v1/internal/users/register |
| Controller | InternalUserCommandController |
| Method | register |
| UseCase | CreateUserWithRolesUseCase |
| Service | CreateUserWithRolesService |
| 설명 | 사용자 생성 + SERVICE scope Role 자동 할당을 하나의 트랜잭션으로 처리 |

---

## 호출 흐름 다이어그램

```
InternalUserCommandController.register(CreateUserWithRolesApiRequest)
  |
  ├─ InternalUserApiMapper.toCommand(request)
  |   └─> CreateUserWithRolesCommand
  |
  ├─ CreateUserWithRolesUseCase.execute(command)                    [Port-In]
  |   |
  |   └─ CreateUserWithRolesService.execute(command)                [Service Impl]
  |       |
  |       ├─ CreateUserWithRolesCommandFactory.create(command)
  |       |   ├─ IdGeneratorPort.generate()                        [Port-Out]
  |       |   ├─ PasswordEncoderClient.hash(rawPassword)           [Port-Out]
  |       |   ├─ User.create(userId, orgId, identifier, ...)       [Domain]
  |       |   └─> CreateUserWithRolesBundle(user, [], serviceCode, roleNames)
  |       |
  |       └─ CreateUserWithRolesCoordinator.coordinate(bundle)
  |           |
  |           ├─ UserValidator.validateIdentifierNotDuplicated()
  |           ├─ UserValidator.validatePhoneNumberNotDuplicated()
  |           |
  |           ├─ ServiceReadManager.findByCode(serviceCode)
  |           |   └─ ServiceQueryPort.findByCode()                  [Port-Out]
  |           |
  |           ├─ RoleReadManager.findByTenantIdAndServiceIdAndName()
  |           |   └─ RoleQueryPort.findByTenantIdAndServiceIdAndName() [Port-Out]
  |           |
  |           ├─ UserRoleCommandFactory.createAll(userId, roleIds)
  |           |   └─> List<UserRole>
  |           |
  |           └─ UserWithRolesCommandFacade.persistAll(bundle)       [@Transactional]
  |               |
  |               ├─ UserCommandManager.persist(user)
  |               |   └─ UserCommandPort.persist(user)              [Port-Out]
  |               |       └─ UserCommandAdapter.persist(user)       [Adapter-Out]
  |               |           ├─ UserJpaEntityMapper.toEntity(user)
  |               |           ├─ UserJpaRepository.save(entity)
  |               |           └─> userId (String)
  |               |
  |               └─ UserRoleCommandManager.persistAll(userRoles)
  |                   └─ UserRoleCommandPort.persistAll(userRoles)  [Port-Out]
  |                       └─ UserRoleCommandAdapter.persistAll()    [Adapter-Out]
  |                           ├─ UserRoleJpaEntityMapper.toEntity()
  |                           ├─ UserRoleJpaRepository.saveAll()
  |                           └─> List<UserRole>
  |
  ├─ InternalUserApiMapper.toApiResponse(result)
  |   └─> CreateUserWithRolesResultApiResponse
  |
  └─> ApiResponse<CreateUserWithRolesResultApiResponse>
```

---

## 1. Adapter-In Layer

### Controller

**파일**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/controller/InternalUserCommandController.java`

```java
@RestController
@RequestMapping(InternalApiEndpoints.USERS)
public class InternalUserCommandController {

    private final CreateUserWithRolesUseCase createUserWithRolesUseCase;
    private final InternalUserApiMapper mapper;

    @PostMapping(InternalApiEndpoints.USERS_REGISTER)
    public ApiResponse<CreateUserWithRolesResultApiResponse> register(
            @Valid @RequestBody CreateUserWithRolesApiRequest request) {
        CreateUserWithRolesCommand command = mapper.toCommand(request);
        CreateUserWithRolesResult result = createUserWithRolesUseCase.execute(command);
        return ApiResponse.ofSuccess(mapper.toApiResponse(result));
    }
}
```

**책임**:
- HTTP 요청 수신 및 검증 (`@Valid`)
- Mapper를 통한 API DTO → Application DTO 변환
- UseCase 실행
- Application DTO → API Response 변환
- 표준 API 응답 래핑 (`ApiResponse.ofSuccess`)

**어노테이션**:
- `@RestController`: Spring MVC REST 컨트롤러
- `@RequestMapping`: 베이스 경로 매핑
- `@PostMapping`: HTTP POST 메서드 매핑
- `@Valid`: Bean Validation 활성화
- `@RequestBody`: JSON 요청 바디 바인딩

---

### Request DTO

**파일**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/command/CreateUserWithRolesApiRequest.java`

```java
public record CreateUserWithRolesApiRequest(
        @NotBlank(message = "조직 ID는 필수입니다")
        String organizationId,

        @NotBlank(message = "식별자는 필수입니다")
        String identifier,

        String phoneNumber,  // 선택

        @NotBlank(message = "비밀번호는 필수입니다")
        String password,

        String serviceCode,  // 선택, 예: "SVC_STORE"

        List<String> roleNames  // 선택, 예: ["ADMIN"]
) {}
```

**필드 설명**:
- `organizationId`: 소속 조직 ID (필수, UUIDv7 문자열)
- `identifier`: 로그인 식별자 (필수, 이메일 또는 사용자명)
- `phoneNumber`: 전화번호 (선택)
- `password`: 평문 비밀번호 (필수, 해싱 전)
- `serviceCode`: 서비스 코드 (선택, SERVICE scope Role 할당 시 필요)
- `roleNames`: 역할 이름 목록 (선택, 예: ["ADMIN", "OPERATOR"])

**Validation**:
- `@NotBlank`: 필수 필드 검증
- Jakarta Validation 기반

---

### Response DTO

**파일**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/dto/response/CreateUserWithRolesResultApiResponse.java`

```java
public record CreateUserWithRolesResultApiResponse(
        String userId,           // 생성된 사용자 ID (UUIDv7)
        int assignedRoleCount    // 할당된 역할 수
) {}
```

**필드 설명**:
- `userId`: 생성된 사용자 ID (UUIDv7 문자열, 예: "01933abc-1234-7000-8000-000000000001")
- `assignedRoleCount`: 할당된 역할 수 (0 이상)

---

### ApiMapper

**파일**: `/adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/internal/mapper/InternalUserApiMapper.java`

```java
@Component
public class InternalUserApiMapper {

    public CreateUserWithRolesCommand toCommand(CreateUserWithRolesApiRequest request) {
        return new CreateUserWithRolesCommand(
                request.organizationId(),
                request.identifier(),
                request.phoneNumber(),
                request.password(),
                request.serviceCode(),
                request.roleNames());
    }

    public CreateUserWithRolesResultApiResponse toApiResponse(CreateUserWithRolesResult result) {
        return new CreateUserWithRolesResultApiResponse(
                result.userId(),
                result.assignedRoleCount());
    }
}
```

**책임**:
- API Request DTO → Application Command DTO 변환
- Application Result DTO → API Response DTO 변환
- 레이어 간 DTO 격리 (Adapter-In ↔ Application)

**규칙**:
- `@Component` 어노테이션 필수
- 단순 변환만 수행 (비즈니스 로직 금지)

---

## 2. Application Layer

### UseCase (Port-In)

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/port/in/command/CreateUserWithRolesUseCase.java`

```java
public interface CreateUserWithRolesUseCase {
    CreateUserWithRolesResult execute(CreateUserWithRolesCommand command);
}
```

**책임**:
- Application Layer의 진입점 정의
- Service 구현체와의 계약 (Port-In)

**규칙**:
- `{Action}{Bc}UseCase` 네이밍
- `execute()` 메서드 시그니처 고정
- Command DTO 파라미터, Result DTO 반환

---

### Service (Implementation)

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/service/command/CreateUserWithRolesService.java`

```java
@Service
public class CreateUserWithRolesService implements CreateUserWithRolesUseCase {

    private final CreateUserWithRolesCommandFactory createUserWithRolesCommandFactory;
    private final CreateUserWithRolesCoordinator coordinator;

    @Override
    public CreateUserWithRolesResult execute(CreateUserWithRolesCommand command) {
        CreateUserWithRolesBundle bundle = createUserWithRolesCommandFactory.create(command);
        return coordinator.coordinate(bundle);
    }
}
```

**처리 흐름**:
1. Factory로 Bundle 생성 (User + 빈 UserRole + serviceCode + roleNames)
2. Coordinator에 Bundle 전달 (검증 → 역할 해석 → 영속화)

**규칙**:
- `@Service` 어노테이션 필수
- UseCase(Port-In) 인터페이스 구현 필수
- `@Transactional` 금지 → Facade에서 처리
- 비즈니스 로직 금지 → Coordinator에 위임

---

### Command/Result DTO

**Command**: `/application/src/main/java/com/ryuqq/authhub/application/user/dto/command/CreateUserWithRolesCommand.java`

```java
public record CreateUserWithRolesCommand(
        String organizationId,
        String identifier,
        String phoneNumber,
        String rawPassword,    // 평문 비밀번호
        String serviceCode,    // 선택
        List<String> roleNames // 선택
) {}
```

**Result**: `/application/src/main/java/com/ryuqq/authhub/application/user/dto/response/CreateUserWithRolesResult.java`

```java
public record CreateUserWithRolesResult(
        String userId,
        int assignedRoleCount
) {
    public static CreateUserWithRolesResult of(String userId, int assignedRoleCount) {
        return new CreateUserWithRolesResult(userId, assignedRoleCount);
    }
}
```

**규칙**:
- 순수 Java Record (jakarta.validation 금지)
- Lombok 금지
- 비즈니스 로직 금지 (Domain 책임)

---

### Factory

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/factory/CreateUserWithRolesCommandFactory.java`

```java
@Component
public class CreateUserWithRolesCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;
    private final PasswordEncoderClient passwordEncoderClient;

    public CreateUserWithRolesBundle create(CreateUserWithRolesCommand command) {
        UserId userId = UserId.of(idGeneratorPort.generate());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Identifier identifier = Identifier.of(command.identifier());
        PhoneNumber phoneNumber = PhoneNumber.fromNullable(command.phoneNumber());
        HashedPassword hashedPassword =
            HashedPassword.of(passwordEncoderClient.hash(command.rawPassword()));

        User user = User.create(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                timeProvider.now());

        return CreateUserWithRolesBundle.of(user, command.serviceCode(), command.roleNames());
    }
}
```

**책임**:
- Command DTO → Domain 객체 변환
- 시간/ID 생성 (외부 Port 사용)
- 비밀번호 해싱 (PasswordEncoderClient)
- User Aggregate 생성
- Bundle 생성 (User + 빈 UserRole 목록)

**규칙**:
- Service에서 Domain 객체 직접 생성 금지 → Factory에 위임
- 시간/ID 생성은 Factory에서만 허용

---

### Coordinator

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/internal/CreateUserWithRolesCoordinator.java`

```java
@Component
public class CreateUserWithRolesCoordinator {

    private final UserValidator userValidator;
    private final ServiceReadManager serviceReadManager;
    private final RoleReadManager roleReadManager;
    private final UserRoleCommandFactory userRoleCommandFactory;
    private final UserWithRolesCommandFacade commandFacade;

    public CreateUserWithRolesResult coordinate(CreateUserWithRolesBundle bundle) {
        User user = bundle.user();

        // 1. 검증
        validateUser(user);

        // 2. UserRole resolve 후 Bundle에 채움
        CreateUserWithRolesBundle enriched = bundle.withUserRoles(resolveUserRoles(user, bundle));

        // 3. Facade 한방 영속화
        String userId = commandFacade.persistAll(enriched);

        return CreateUserWithRolesResult.of(userId, enriched.assignedRoleCount());
    }

    private void validateUser(User user) {
        userValidator.validateIdentifierNotDuplicated(
                user.getOrganizationId(), user.getIdentifier());
        userValidator.validatePhoneNumberNotDuplicated(
                user.getOrganizationId(), user.getPhoneNumber());
    }

    private List<UserRole> resolveUserRoles(User user, CreateUserWithRolesBundle bundle) {
        if (bundle.roleNames() == null || bundle.roleNames().isEmpty()) {
            return List.of();
        }

        ServiceId serviceId = resolveServiceId(bundle.serviceCode());

        List<RoleId> roleIds = bundle.roleNames().stream()
                .map(name -> roleReadManager.findByTenantIdAndServiceIdAndName(
                        null, serviceId, RoleName.of(name)))
                .map(Role::getRoleId)
                .toList();

        return userRoleCommandFactory.createAll(user.getUserId(), roleIds);
    }

    private ServiceId resolveServiceId(String serviceCode) {
        ServiceCode code = ServiceCode.fromNullable(serviceCode);
        if (code == null) {
            return null;
        }
        Service service = serviceReadManager.findByCode(code);
        return service.getServiceId();
    }
}
```

**책임**:
1. **검증**: 식별자/전화번호 중복 검증 (UserValidator)
2. **서비스 리졸브**: serviceCode → serviceId 변환 (ServiceReadManager)
3. **역할 리졸브**: roleName → Role 조회 (RoleReadManager)
4. **UserRole 생성**: UserRoleCommandFactory로 도메인 객체 생성
5. **영속화 조율**: Facade 한방 영속화

**Role 조회 로직**:
- `serviceCode + roleNames` 제공 → SERVICE scope Role (tenantId=null, serviceId=resolved)
- `roleNames`만 제공 (serviceCode 없음) → GLOBAL scope Role (tenantId=null, serviceId=null)
- `roleNames` 없음 → 사용자만 생성 (역할 할당 스킵)

---

### Facade

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/internal/UserWithRolesCommandFacade.java`

```java
@Component
class UserWithRolesCommandFacade {

    private final UserCommandManager userCommandManager;
    private final UserRoleCommandManager userRoleCommandManager;

    @Transactional
    String persistAll(CreateUserWithRolesBundle bundle) {
        String userId = userCommandManager.persist(bundle.user());
        if (!bundle.userRoles().isEmpty()) {
            userRoleCommandManager.persistAll(bundle.userRoles());
        }
        return userId;
    }
}
```

**책임**:
- User + UserRole 한방 영속화
- 트랜잭션 경계 설정 (`@Transactional`)

**규칙**:
- `@Transactional`은 Facade에서만 사용
- Manager를 통한 Port 호출

---

### Bundle

**파일**: `/application/src/main/java/com/ryuqq/authhub/application/user/internal/CreateUserWithRolesBundle.java`

```java
public record CreateUserWithRolesBundle(
        User user,
        List<UserRole> userRoles,
        String serviceCode,
        List<String> roleNames
) {
    public static CreateUserWithRolesBundle of(
            User user, String serviceCode, List<String> roleNames) {
        return new CreateUserWithRolesBundle(user, List.of(), serviceCode, roleNames);
    }

    CreateUserWithRolesBundle withUserRoles(List<UserRole> userRoles) {
        return new CreateUserWithRolesBundle(
                this.user, userRoles, this.serviceCode, this.roleNames);
    }

    int assignedRoleCount() {
        return userRoles.size();
    }
}
```

**책임**:
- User + UserRole 영속화 번들
- 불변 컨테이너 (immutable)
- `withUserRoles()`로 UserRole 추가 (새 인스턴스 반환)

---

## 3. Domain Layer

### Domain Model

**User Aggregate**: `/domain/src/main/java/com/ryuqq/authhub/domain/user/aggregate/User.java`

```java
public final class User {

    private final UserId userId;
    private final OrganizationId organizationId;
    private final Identifier identifier;
    private PhoneNumber phoneNumber;
    private HashedPassword hashedPassword;
    private UserStatus status;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    public static User create(
            UserId userId,
            OrganizationId organizationId,
            Identifier identifier,
            PhoneNumber phoneNumber,
            HashedPassword hashedPassword,
            Instant now) {
        return new User(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                UserStatus.ACTIVE,
                DeletionStatus.active(),
                now,
                now);
    }
}
```

**UserRole Aggregate**: `/domain/src/main/java/com/ryuqq/authhub/domain/userrole/aggregate/UserRole.java`

```java
public final class UserRole {

    private final UserRoleId userRoleId;
    private final UserId userId;
    private final RoleId roleId;
    private final Instant createdAt;

    public static UserRole create(UserId userId, RoleId roleId, Instant now) {
        return new UserRole(null, userId, roleId, now);
    }
}
```

**특징**:
- Lombok 금지 - Plain Java 사용
- Long FK 전략 - JPA 관계 어노테이션 금지
- Value Object 사용 (UserId, OrganizationId, Identifier 등)
- 불변성 원칙 (final 필드)

---

### Domain Port (Port-Out)

**UserCommandPort**: `/application/src/main/java/com/ryuqq/authhub/application/user/port/out/command/UserCommandPort.java`

```java
public interface UserCommandPort {
    String persist(User user);
}
```

**UserRoleCommandPort**: `/application/src/main/java/com/ryuqq/authhub/application/userrole/port/out/command/UserRoleCommandPort.java`

```java
public interface UserRoleCommandPort {
    UserRole persist(UserRole userRole);
    List<UserRole> persistAll(List<UserRole> userRoles);
    void delete(UserId userId, RoleId roleId);
    void deleteAllByUserId(UserId userId);
    void deleteAll(UserId userId, List<RoleId> roleIds);
}
```

**규칙**:
- `persist()` 메서드만 제공 (save/update/delete 분리 금지)
- Domain Aggregate 파라미터 (Entity/DTO 금지)
- ID 반환 (String - UUIDv7, Domain 객체 반환 금지)
- 조회 메서드 금지 (QueryPort로 분리)

---

## 4. Adapter-Out Layer

### Adapter

**UserCommandAdapter**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/user/adapter/UserCommandAdapter.java`

```java
@Component
public class UserCommandAdapter implements UserCommandPort {

    private final UserJpaRepository repository;
    private final UserJpaEntityMapper mapper;

    @Override
    public String persist(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getUserId();
    }
}
```

**UserRoleCommandAdapter**: `/adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/userrole/adapter/UserRoleCommandAdapter.java`

```java
@Component
public class UserRoleCommandAdapter implements UserRoleCommandPort {

    private final UserRoleJpaRepository repository;
    private final UserRoleJpaEntityMapper mapper;

    @Override
    public List<UserRole> persistAll(List<UserRole> userRoles) {
        List<UserRoleJpaEntity> entities = userRoles.stream()
                .map(mapper::toEntity)
                .toList();
        List<UserRoleJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
```

**책임**:
- Domain → JPA Entity 변환
- JPA Repository 호출
- Entity → Domain 변환 (조회 시)

**규칙**:
- `@Transactional` 금지 (Manager/Facade에서 관리)
- 비즈니스 로직 금지 (단순 위임 + 변환만)
- 1:1 매핑 원칙 (Repository 1개 + Mapper 1개)

---

### Repository

**UserJpaRepository**: Spring Data JPA 인터페이스

```java
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    // Spring Data JPA가 자동 구현
}
```

**UserRoleJpaRepository**: Spring Data JPA 인터페이스

```java
public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, Long> {
    void deleteByUserIdAndRoleId(String userId, Long roleId);
    void deleteAllByUserId(String userId);
    void deleteAllByUserIdAndRoleIdIn(String userId, List<Long> roleIds);
}
```

---

### Entity

**UserJpaEntity**: JPA 엔티티 (테이블: `user`)

```java
@Entity
@Table(name = "user")
public class UserJpaEntity {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;  // UUIDv7

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // ... 기타 필드
}
```

**UserRoleJpaEntity**: JPA 엔티티 (테이블: `user_role`)

```java
@Entity
@Table(name = "user_role",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
public class UserRoleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
```

**규칙**:
- Long FK 전략 (JPA 관계 어노테이션 금지)
- `@Column(nullable = false)` 명시
- Unique 제약조건 (user_id + role_id)

---

## Database Query 분석

### User 저장

**테이블**: `user`

**INSERT 쿼리** (신규 사용자):
```sql
INSERT INTO user (
    user_id,
    organization_id,
    identifier,
    phone_number,
    password,
    status,
    deletion_status,
    created_at,
    updated_at
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
```

**파라미터 예시**:
- user_id: "01933abc-1234-7000-8000-000000000001" (UUIDv7)
- organization_id: "550e8400-e29b-41d4-a716-446655440000"
- identifier: "admin@setof.com"
- phone_number: "010-1234-5678" (nullable)
- password: "$2a$10$..." (BCrypt hash)
- status: "ACTIVE"
- deletion_status: "ACTIVE"
- created_at: 2026-02-05T12:00:00Z
- updated_at: 2026-02-05T12:00:00Z

---

### UserRole 저장

**테이블**: `user_role`

**INSERT 쿼리** (역할 할당):
```sql
INSERT INTO user_role (
    user_id,
    role_id,
    created_at
) VALUES (?, ?, ?)
```

**Batch INSERT** (다건):
```sql
INSERT INTO user_role (user_id, role_id, created_at) VALUES
    ('01933abc-1234-7000-8000-000000000001', 1, '2026-02-05T12:00:00Z'),
    ('01933abc-1234-7000-8000-000000000001', 2, '2026-02-05T12:00:00Z')
```

**UNIQUE 제약조건**:
```sql
UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
```

---

### 트랜잭션 경계

```
@Transactional (UserWithRolesCommandFacade.persistAll)
  |
  ├─ INSERT INTO user (...)                   -- User 저장
  |
  └─ INSERT INTO user_role (...) VALUES (...) -- UserRole 배치 저장
     └─ Commit / Rollback
```

**특징**:
- 하나의 트랜잭션으로 User + UserRole 저장
- 실패 시 전체 롤백 (User도 생성되지 않음)
- Hibernate Dirty Checking 활용 (존재 여부 확인 불필요)

---

## 핵심 원칙 및 규칙 준수

### Hexagonal Architecture

| 레이어 | 책임 | 격리 원칙 |
|--------|------|----------|
| Adapter-In | HTTP 요청 처리, API DTO 변환 | Application 레이어와 DTO로 통신 |
| Application | 비즈니스 플로우 조율, 트랜잭션 관리 | Domain Port로 영속화 위임 |
| Domain | 비즈니스 규칙, Aggregate 생성 | 순수 Java, 외부 의존성 없음 |
| Adapter-Out | 영속화 구현, Entity 변환 | Domain Aggregate를 JPA Entity로 변환 |

### CQRS 분리

- **Command**: `CreateUserWithRolesUseCase` (CUD 전용)
- **Query**: 별도 QueryPort (조회 전용, 이 플로우에서는 Role 조회만 사용)

### Zero-Tolerance 규칙 준수

- ✅ Lombok 금지 (Plain Java Record 사용)
- ✅ Long FK 전략 (JPA 관계 어노테이션 없음)
- ✅ Service에서 Domain 직접 생성 금지 (Factory 사용)
- ✅ `@Transactional`은 Facade/Manager에서만 사용
- ✅ Value Object 사용 (UserId, OrganizationId, Identifier 등)

---

## 요약

이 엔드포인트는 **사용자 생성 + 역할 할당을 하나의 트랜잭션으로 처리**하는 내부 API입니다.

**주요 특징**:
1. **Bundle 패턴**: User + UserRole을 묶어서 한 번에 영속화
2. **Coordinator 패턴**: 검증 → 리졸브 → 영속화 조율
3. **Facade 트랜잭션**: 여러 Manager 호출을 하나의 트랜잭션으로 묶음
4. **Role Scope 리졸브**: serviceCode가 있으면 SERVICE scope, 없으면 GLOBAL scope Role 조회
5. **Hexagonal 격리**: 각 레이어가 DTO로 격리되어 독립적으로 변경 가능

**트랜잭션 안전성**:
- User 저장 실패 시 전체 롤백
- UserRole 저장 실패 시 전체 롤백
- 중복 검증 실패 시 트랜잭션 시작 전 Exception 발생

**확장성**:
- 새로운 역할 추가 시 `roleNames`에 추가만 하면 됨
- 다른 Service의 Role 할당 시 `serviceCode` 변경만 하면 됨
- 검증 로직 추가 시 `UserValidator`에만 추가
