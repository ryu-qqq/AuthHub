# AuthHub Implementation Guide

> **헥사고날 아키텍처 멀티모듈 프로젝트 구현 가이드**
>
> 본 문서는 원본 Jira 문서(`AuthHub_jira.md`)를 우리 프로젝트의 헥사고날 아키텍처 및 멀티모듈 구조에 맞게 재구성한 구현 가이드입니다.

---

## 📋 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [아키텍처 원칙](#아키텍처-원칙)
3. [멀티모듈 구조](#멀티모듈-구조)
4. [Zero-Tolerance 규칙](#zero-tolerance-규칙)
5. [JIRA Epic 구조](#jira-epic-구조)
6. [구현 가이드](#구현-가이드)

---

## 🎯 프로젝트 개요

### AuthHub란?
JWT 기반 인증/인가 시스템으로, B2C/B2B 플랫폼을 위한 통합 인증 허브입니다.

### 핵심 기능
- **JWT 인증**: RS256 기반 액세스/리프레시 토큰
- **멀티 플랫폼**: B2C (고객/셀러), B2B (벤더/구매사)
- **소셜 로그인**: 카카오 OAuth 2.0
- **SMS 인증**: AWS SNS 기반 휴대폰 인증
- **조직 관리**: 계층적 조직 구조 및 권한 관리
- **구독제**: B2B 기업용 구독 플랜 관리
- **보안**: Rate Limiting, Audit Log, Token Blacklist

### 기술 스택
- **Java 21**: Record, Sealed Classes, Virtual Threads
- **Spring Boot 3.5.x**: WebFlux는 사용하지 않음 (일반 MVC)
- **MySQL**: 사용자/인증 정보
- **Redis**: Refresh Token, Blacklist, Rate Limit, SMS Session
- **JWT**: JJWT 0.12.x (RS256)
- **AWS SNS**: SMS 발송
- **Gradle**: 멀티모듈 빌드

---

## 🏛️ 아키텍처 원칙

### 1. 헥사고날 아키텍처 (Ports & Adapters)
```
┌─────────────────────────────────────────┐
│         Adapter Layer (In)              │
│  ┌───────────────────────────────────┐  │
│  │   REST API Controllers            │  │
│  │   (adapter-in/rest-api)           │  │
│  └───────────────┬───────────────────┘  │
│                  │ UseCase Interface     │
│                  ↓                       │
│  ┌───────────────────────────────────┐  │
│  │   Application Layer               │  │
│  │   - UseCase Implementation        │  │
│  │   - Transaction Boundaries        │  │
│  │   - Assembler (DTO ↔ Domain)      │  │
│  │   (application)                   │  │
│  └───────────────┬───────────────────┘  │
│                  │ Port (Out)            │
│                  ↓                       │
│  ┌───────────────────────────────────┐  │
│  │   Domain Layer                    │  │
│  │   - Aggregates, Entities          │  │
│  │   - Value Objects, Policies       │  │
│  │   - Domain Events                 │  │
│  │   (domain)                        │  │
│  └───────────────────────────────────┘  │
│                  ↑                       │
│                  │ Adapter implements    │
│  ┌───────────────┴───────────────────┐  │
│  │   Adapter Layer (Out)             │  │
│  │   - Persistence (JPA, Redis)      │  │
│  │   - External APIs (Kakao, AWS)    │  │
│  │   (adapter-out-persistence/       │  │
│  │    adapter-out-external)          │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### 2. DDD (Domain-Driven Design)
- **Aggregate**: 트랜잭션 경계 및 일관성 보장 단위
- **Value Object**: 불변 값 객체 (Java Record 권장)
- **Domain Event**: 상태 전이 결과 발행
- **Bounded Context**: 도메인별 명확한 경계

### 3. CQRS (Command Query Responsibility Segregation)
- **Command**: 상태 변경 (`@Transactional`)
- **Query**: 조회 (`@Transactional(readOnly=true)`)
- **Controller 분리**: `CommandController`, `QueryController`

---

## 📦 멀티모듈 구조

### 전체 구조
```
AuthHub/
├── domain/                          # 순수 도메인 모델
│   ├── shared/                      # 공통 VO, Event, Exception
│   └── [boundedContext]/            # auth, user, organization 등
│       └── [aggregateName]/
│           ├── [AggregateRoot].java
│           ├── vo/
│           ├── event/
│           ├── exception/
│           ├── policy/
│           └── factory/
│
├── application/                     # UseCase 구현, 트랜잭션 경계
│   └── [context]/                   # auth, user, organization 등
│       ├── port/
│       │   ├── in/                  # UseCase 인터페이스 (Command/Response 내부 Record)
│       │   └── out/                 # Repository, External API 인터페이스
│       ├── assembler/               # Command ↔ Domain 변환
│       └── service/
│           ├── command/             # @Transactional
│           └── query/               # @Transactional(readOnly=true)
│
├── adapter-in/rest-api/                 # REST API Adapter
│   ├── shared/                      # 공통 DTO, ExceptionHandler, Filter
│   └── [context]/
│       ├── controller/              # CommandController, QueryController
│       ├── dto/
│       │   ├── request/             # API Request DTO
│       │   └── response/            # API Response DTO (Api 접두사 필수)
│       └── mapper/                  # API DTO ↔ UseCase DTO
│
├── adapter-out/persistence-mysql/         # JPA/Redis Adapter
│   └── [context]/
│       ├── entity/                  # JPA Entity (Long FK 전략)
│       ├── repository/              # JPA Repository
│       └── adapter/                 # Port 구현체
│
├── adapter-out/external-client/            # 외부 API Adapter
│   ├── kakao/                       # Kakao OAuth
│   ├── aws/                         # AWS SNS
│   └── email/                       # Email 발송
│
└── bootstrap/                       # Application 시작점
    ├── AuthHubApplication.java
    ├── config/                      # 설정
    ├── actuator/                    # Monitoring
    └── scheduler/                   # Scheduled Tasks
```

### 패키지 네이밍
```
com.authhub
├── domain                           # domain/
├── application                      # application/
├── adapter.in.rest                  # adapter-in-rest/
├── adapter.out.persistence          # adapter-out-persistence/
├── adapter.out.external             # adapter-out-external/
└── bootstrap                        # bootstrap/
```

---

## 🚨 Zero-Tolerance 규칙

> **다음 규칙들은 예외 없이 반드시 준수해야 합니다.**

### 1. Lombok 금지 (Domain Layer 특히 엄격)
❌ **금지**:
```java
@Data
@Builder
@Getter
@Setter
public class User {
    private Long id;
    private String email;
}
```

✅ **허용**:
```java
public class User {
    private final Long id;
    private final String email;

    public User(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
```

**검증**: `validation-helper.py`가 자동 감지

### 2. Law of Demeter (Getter 체이닝 금지)
❌ **금지**:
```java
String zip = order.getCustomer().getAddress().getZipCode();
```

✅ **허용**:
```java
// Domain Layer에 캡슐화된 메서드 제공
String zip = order.getCustomerZipCode();

// Order Aggregate 내부
public String getCustomerZipCode() {
    return this.customer.getAddress().getZipCode();
}
```

**검증**: Anti-pattern 정규식 매칭

### 3. Long FK 전략 (JPA 관계 어노테이션 금지)
❌ **금지**:
```java
@Entity
public class OrderJpaEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;  // ❌
}
```

✅ **허용**:
```java
@Entity
public class OrderJpaEntity {
    @Column(name = "user_id")
    private Long userId;  // ✅ Long FK 전략
}
```

**검증**: JPA 관계 어노테이션 감지

### 4. Transaction 경계 (외부 API 호출 금지)
❌ **금지**:
```java
@Transactional
public void sendSmsAndSave(Command cmd) {
    // DB 저장
    saveSmsSession(cmd);

    // ❌ 외부 API 호출 (트랜잭션이 길어짐!)
    awsSnsPort.sendSms(cmd.phoneNumber(), cmd.code());
}
```

✅ **허용**:
```java
// Facade (트랜잭션 없음)
public void sendSms(Command cmd) {
    // 1. 트랜잭션 내에서 DB 저장
    sendSmsService.saveSession(cmd);

    // 2. 트랜잭션 밖에서 외부 API 호출
    awsSnsPort.sendSms(cmd.phoneNumber(), cmd.code());
}

@Service
@Transactional
class SendSmsService {
    public void saveSession(Command cmd) {
        // 짧은 트랜잭션 (DB 저장만)
    }
}
```

**검증**: Git pre-commit hook

### 5. Spring Proxy 제약사항
❌ **작동하지 않는 경우**:
```java
// ❌ Private 메서드
@Transactional
private void saveOrder(Order order) { ... }

// ❌ Final 클래스/메서드
@Transactional
public final void createOrder(Command cmd) { ... }

// ❌ Self-invocation
@Service
public class OrderService {
    @Transactional
    public void processOrder() {
        this.saveOrder(); // ❌ @Transactional 무시됨!
    }

    @Transactional
    void saveOrder() { ... }
}
```

✅ **올바른 방법**:
```java
// ✅ Public 메서드
@Transactional
public void saveOrder(Order order) { ... }

// ✅ 별도 빈으로 분리
@Service
public class OrderService {
    private final OrderPersistenceService persistenceService;

    public void processOrder() {
        persistenceService.saveOrder(); // ✅ 프록시 정상 작동
    }
}
```

### 6. Javadoc 필수
❌ **금지**:
```java
public class User {
    public void updateEmail(String email) { ... }
}
```

✅ **허용**:
```java
/**
 * User Aggregate Root
 *
 * @author development-team
 * @since 1.0.0
 */
public class User {
    /**
     * 이메일 주소를 업데이트합니다.
     *
     * @param email 새로운 이메일 주소
     * @throws InvalidEmailException 유효하지 않은 이메일 형식
     */
    public void updateEmail(String email) { ... }
}
```

**검증**: Checkstyle

---

## 📊 JIRA Epic 구조

### Epic 개요
| Epic | 제목 | Story Points | Priority | Sprint |
|------|------|--------------|----------|--------|
| [AUT-1](https://ryuqqq.atlassian.net/browse/AUT-1) | AuthHub Core - JWT 인증 시스템 | 34 | Critical | 1-2 |
| [AUT-2](https://ryuqqq.atlassian.net/browse/AUT-2) | Identity Management - 신원 및 조직 관리 | 21 | High | 2-3 |
| [AUT-3](https://ryuqqq.atlassian.net/browse/AUT-3) | B2C Integration - 소셜 로그인 및 SMS | 21 | High | 3-4 |
| [AUT-4](https://ryuqqq.atlassian.net/browse/AUT-4) | B2B Integration - 기업 관리 및 구독 | 18 | High | 4-5 |
| [AUT-5](https://ryuqqq.atlassian.net/browse/AUT-5) | Security & Operations - 보안 및 운영 | 16 | Medium | 5-6 |
| **Total** | | **110** | | **6 Sprints** |

---

## 🛠️ 구현 가이드

### Epic 1: AuthHub Core - JWT 인증 시스템

#### 1.1 Domain Layer 구현
**위치**: `domain/src/main/java/com/authhub/domain`

**주요 Aggregate**:
1. **User** (사용자)
   - Value Objects: `UserId`, `UserStatus`, `LastLoginAt`
   - Events: `UserCreatedEvent`, `UserLoggedInEvent`
   - 검증: Lombok 금지, Law of Demeter 준수

2. **UserCredential** (인증 정보)
   - Value Objects: `CredentialType`, `Identifier`, `CredentialValue`
   - Exception: `InvalidCredentialException`
   - 비밀번호: Argon2id 암호화

3. **Token** (토큰)
   - Value Objects: `AccessToken`, `RefreshToken`, `TokenClaims`
   - Exception: `ExpiredTokenException`, `InvalidTokenException`

**예시 코드**:
```java
package com.authhub.domain.auth.user;

/**
 * User Aggregate Root
 *
 * @author development-team
 * @since 1.0.0
 */
public class User {
    private final UserId id;
    private final UserStatus status;
    private LastLoginAt lastLoginAt;
    private final Instant createdAt;

    private User(UserId id, UserStatus status, LastLoginAt lastLoginAt, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    public static User create(UserId id) {
        return new User(
            id,
            UserStatus.ACTIVE,
            null,
            Instant.now()
        );
    }

    public void recordLogin() {
        this.lastLoginAt = LastLoginAt.now();
    }

    public boolean isActive() {
        return this.status.isActive();
    }

    // Getters (Law of Demeter 준수)
    public UserId getId() {
        return id;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LastLoginAt getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
```

#### 1.2 Application Layer 구현
**위치**: `application/src/main/java/com/authhub/application`

**주요 UseCase**:
1. **LoginUseCase** (로그인)
   - Command (내부 Record): `credentialType`, `identifier`, `password`, `platform`
   - Response (내부 Record): `accessToken`, `refreshToken`, `tokenType`, `expiresIn`

2. **RefreshTokenUseCase** (토큰 갱신)
3. **LogoutUseCase** (로그아웃)

**예시 코드**:
```java
package com.authhub.application.auth.port.in;

/**
 * Login UseCase Interface
 *
 * @author development-team
 * @since 1.0.0
 */
public interface LoginUseCase {

    Response login(Command command);

    /**
     * Login Command (내부 Record)
     */
    record Command(
        String credentialType,
        String identifier,
        String password,
        String platform
    ) {
        public Command {
            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("Identifier is required");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password is required");
            }
        }
    }

    /**
     * Login Response (내부 Record)
     */
    record Response(
        String accessToken,
        String refreshToken,
        String tokenType,
        int expiresIn
    ) {}
}
```

**Service 구현**:
```java
package com.authhub.application.auth.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Login Service (UseCase 구현)
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional
public class LoginService implements LoginUseCase {

    private final LoadCredentialByIdentifierPort loadCredentialPort;
    private final LoadUserPort loadUserPort;
    private final GenerateTokenPort generateTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final UserAssembler userAssembler;

    public LoginService(
        LoadCredentialByIdentifierPort loadCredentialPort,
        LoadUserPort loadUserPort,
        GenerateTokenPort generateTokenPort,
        SaveRefreshTokenPort saveRefreshTokenPort,
        UserAssembler userAssembler
    ) {
        this.loadCredentialPort = loadCredentialPort;
        this.loadUserPort = loadUserPort;
        this.generateTokenPort = generateTokenPort;
        this.saveRefreshTokenPort = saveRefreshTokenPort;
        this.userAssembler = userAssembler;
    }

    @Override
    public Response login(Command command) {
        // 1. Credential 조회
        UserCredential credential = loadCredentialPort
            .loadByIdentifier(command.credentialType(), command.identifier())
            .orElseThrow(() -> new InvalidCredentialException("Invalid credentials"));

        // 2. 비밀번호 검증
        if (!credential.verifyPassword(command.password())) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        // 3. User 조회 및 상태 확인
        User user = loadUserPort
            .load(credential.getUserId())
            .orElseThrow(() -> new UserNotFoundException(credential.getUserId()));

        if (!user.isActive()) {
            throw new InvalidUserStatusException("User is not active");
        }

        // 4. 로그인 기록
        user.recordLogin();

        // 5. Token 생성
        TokenClaims claims = TokenClaims.of(user, command.platform());
        AccessToken accessToken = generateTokenPort.generateAccessToken(claims);
        RefreshToken refreshToken = generateTokenPort.generateRefreshToken(user.getId());

        // 6. Refresh Token 저장 (Redis)
        saveRefreshTokenPort.save(refreshToken);

        return new Response(
            accessToken.getValue(),
            refreshToken.getValue(),
            "Bearer",
            600 // 10분
        );
    }
}
```

#### 1.3 Adapter-In-Rest Layer 구현
**위치**: `adapter-in-rest/src/main/java/com/authhub/adapter/in/rest`

**Controller**:
```java
package com.authhub.adapter.in.rest.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Auth Command Controller
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthCommandController {

    private final LoginUseCase loginUseCase;
    private final AuthApiMapper authApiMapper;

    public AuthCommandController(LoginUseCase loginUseCase, AuthApiMapper authApiMapper) {
        this.loginUseCase = loginUseCase;
        this.authApiMapper = authApiMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenApiResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Mapper: API Request → UseCase Command
        LoginUseCase.Command command = authApiMapper.toLoginCommand(request);

        // 2. UseCase 실행
        LoginUseCase.Response response = loginUseCase.login(command);

        // 3. Mapper: UseCase Response → API Response
        TokenApiResponse apiResponse = authApiMapper.toTokenApiResponse(response);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
```

**API Request DTO**:
```java
package com.authhub.adapter.in.rest.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Login API Request DTO
 *
 * @author development-team
 * @since 1.0.0
 */
public record LoginRequest(
    @NotBlank(message = "Credential type is required")
    String credentialType,

    @NotBlank(message = "Identifier is required")
    String identifier,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Platform is required")
    String platform
) {
    public LoginRequest {
        if (credentialType != null && !credentialType.matches("EMAIL|PHONE")) {
            throw new IllegalArgumentException("Invalid credential type");
        }
    }
}
```

**API Response DTO**:
```java
package com.authhub.adapter.in.rest.auth.dto.response;

/**
 * Token API Response DTO
 *
 * @author development-team
 * @since 1.0.0
 */
public record TokenApiResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    int expiresIn
) {}
```

**Mapper**:
```java
package com.authhub.adapter.in.rest.auth.mapper;

import org.springframework.stereotype.Component;

/**
 * Auth API Mapper (Adapter Layer)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class AuthApiMapper {

    /**
     * API Request → UseCase Command
     */
    public LoginUseCase.Command toLoginCommand(LoginRequest request) {
        return new LoginUseCase.Command(
            request.credentialType(),
            request.identifier(),
            request.password(),
            request.platform()
        );
    }

    /**
     * UseCase Response → API Response
     */
    public TokenApiResponse toTokenApiResponse(LoginUseCase.Response response) {
        return new TokenApiResponse(
            response.accessToken(),
            response.refreshToken(),
            response.tokenType(),
            response.expiresIn()
        );
    }
}
```

#### 1.4 Adapter-Out-Persistence Layer 구현
**위치**: `adapter-out-persistence/src/main/java/com/authhub/adapter/out/persistence`

**JPA Entity** (Long FK 전략):
```java
package com.authhub.adapter.out.persistence.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * User JPA Entity
 *
 * @author development-team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid", nullable = false, unique = true, length = 36)
    private String uid;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // ❌ JPA 관계 어노테이션 금지
    // @OneToMany(mappedBy = "user")
    // private List<UserCredentialJpaEntity> credentials;

    // ✅ Hibernate 전용 생성자 (protected)
    protected UserJpaEntity() {}

    // ✅ Public 생성자
    public UserJpaEntity(
        Long id,
        String uid,
        String status,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.uid = uid;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ✅ Getters (Lombok 금지)
    public Long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ✅ Setters (필요한 경우만)
    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

**Persistence Adapter** (Port 구현):
```java
package com.authhub.adapter.out.persistence.auth.adapter;

import org.springframework.stereotype.Component;

/**
 * User Persistence Adapter (Port 구현체)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public UserPersistenceAdapter(
        UserJpaRepository userJpaRepository,
        UserMapper userMapper
    ) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = userMapper.toEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> load(UserId userId) {
        return userJpaRepository.findById(userId.getValue())
            .map(userMapper::toDomain);
    }
}
```

---

### Epic 2-5 구현 가이드

나머지 Epic들도 동일한 레이어별 구조를 따릅니다:
1. **Domain Layer**: Aggregate, Value Object, Event, Policy, Exception
2. **Application Layer**: UseCase, Service, Assembler, Port
3. **Adapter-In-Rest**: Controller, DTO, Mapper
4. **Adapter-Out-Persistence**: JPA Entity, Repository, Adapter
5. **Adapter-Out-External**: 외부 API Adapter (Kakao, AWS SNS 등)

각 Epic의 상세 구조는 JIRA에서 확인하세요:
- [AUT-2: Identity Management](https://ryuqqq.atlassian.net/browse/AUT-2)
- [AUT-3: B2C Integration](https://ryuqqq.atlassian.net/browse/AUT-3)
- [AUT-4: B2B Integration](https://ryuqqq.atlassian.net/browse/AUT-4)
- [AUT-5: Security & Operations](https://ryuqqq.atlassian.net/browse/AUT-5)

---

## 📚 참고 문서

### 코딩 컨벤션
- [Domain Layer 가이드](/docs/coding_convention/02-domain-layer/)
- [Application Layer 가이드](/docs/coding_convention/03-application-layer/)
- [REST API Layer 가이드](/docs/coding_convention/01-adapter-rest-api-layer/)
- [Persistence Layer 가이드](/docs/coding_convention/04-persistence-layer/)

### 핵심 규칙
- [Law of Demeter](/docs/coding_convention/02-domain-layer/law-of-demeter/)
- [Transaction 경계](/docs/coding_convention/03-application-layer/transaction-management/)
- [Long FK 전략](/docs/coding_convention/04-persistence-layer/jpa-entity-design/01_long-fk-strategy.md)
- [Spring Proxy 제약사항](/docs/coding_convention/03-application-layer/transaction-management/02_spring-proxy-limitations.md)

### Dynamic Hooks 시스템
- [Dynamic Hooks 가이드](/docs/DYNAMIC_HOOKS_GUIDE.md)
- [Validation Helper](/.claude/hooks/scripts/validation-helper.py)

### 슬래시 커맨드
- `/code-gen-domain <name>`: Domain Aggregate 생성
- `/code-gen-usecase <name>`: Application UseCase 생성
- `/code-gen-controller <name>`: REST Controller 생성
- `/validate-domain <file>`: Domain layer 검증
- `/validate-architecture [dir]`: 아키텍처 검증

---

## ✅ Definition of Done

모든 태스크는 다음 조건을 만족해야 완료:

- ✅ 코드 리뷰 완료
- ✅ 단위 테스트 작성 (Coverage > 80%)
- ✅ 통합 테스트 통과
- ✅ API 문서 작성
- ✅ Zero-Tolerance 규칙 준수 검증
- ✅ ArchUnit 테스트 통과
- ✅ 성능 테스트 통과
- ✅ 배포 스크립트 준비
- ✅ 모니터링 설정 완료

---

**작성자**: Development Team
**최종 수정일**: 2025-01-23
**버전**: 1.0.0
**원본 문서**: [AuthHub_jira.md](/docs/AuthHub_jira.md)
**JIRA 프로젝트**: [AUT - AuthHub](https://ryuqqq.atlassian.net/jira/software/projects/AUT)
