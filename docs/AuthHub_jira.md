🎯 Epic 구조
Epic 1: AuthHub Core - 인증 시스템 구축
Epic 2: AuthHub Identity - 신원 및 조직 관리
Epic 3: B2C Commerce Integration - B2C 플랫폼 통합
Epic 4: B2B Distribution Integration - B2B 플랫폼 통합
Epic 5: Security & Operations - 보안 및 운영
```

---

## 📌 Epic 1: AuthHub Core - 인증 시스템 구축

### 🎯 Epic 설명
```
제목: [AUTH-EPIC-001] AuthHub Core - 인증 시스템 구축
설명: JWT 기반 인증/인가 시스템의 핵심 기능 구현. 로그인, 토큰 발급/갱신, JWKS 엔드포인트 구축
Story Points: 34
Priority: Critical
Sprint: Sprint 1-2
📋 Tasks
Task 1.1: 프로젝트 초기 설정
yaml제목: [AUTH-001] Spring Boot 3.2 + WebFlux 프로젝트 초기 설정
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  AuthHub 프로젝트 초기 환경 구성 및 의존성 설정

수락 기준:
  - Spring Boot 3.2.x + WebFlux 프로젝트 생성
  - Gradle 빌드 설정
  - 프로젝트 구조 설정 (domain, application, infrastructure, presentation)
  
기술 상세:
  dependencies:
    - spring-boot-starter-webflux
    - spring-boot-starter-data-r2dbc
    - r2dbc-mysql (io.asyncer:r2dbc-mysql)
    - spring-boot-starter-security
    - spring-boot-starter-validation
    - jjwt-api: 0.12.x
    - jjwt-impl: 0.12.x
    - jjwt-jackson: 0.12.x
    
  application.yml 설정:
    - R2DBC MySQL 연결 설정
    - JWT 설정 (secret, expiration)
    - Redis 연결 설정

체크리스트:
  [ ] 프로젝트 생성 및 Git 저장소 연동
  [ ] 의존성 추가 및 버전 관리
  [ ] 패키지 구조 생성
  [ ] 환경별 설정 파일 구성 (local, dev, prod)
  [ ] Docker-compose.yml 작성 (MySQL, Redis)
  [ ] README.md 작성
Task 1.2: 데이터베이스 스키마 생성
yaml제목: [AUTH-002] Core 데이터베이스 스키마 및 R2DBC 엔티티 구현
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  인증 시스템 핵심 테이블 생성 및 R2DBC 엔티티 매핑

수락 기준:
  - Flyway 마이그레이션 스크립트 작성
  - R2DBC 엔티티 클래스 구현
  - Repository 인터페이스 구현

SQL 스크립트:
  V1__create_users_table.sql:
    - users 테이블 생성
    - 인덱스 생성 (status, created_at)
    
  V2__create_user_credentials_table.sql:
    - user_credentials 테이블 생성
    - 유니크 제약 조건 (credential_type, identifier)
    
  V3__create_refresh_tokens_table.sql:
    - refresh_tokens 테이블 생성
    - 인덱스 생성 (user_id, token_family, expires_at)

엔티티 구현:
  @Table("users")
  class User {
    @Id Long id;
    String uid; // UUID
    String status; // Enum: ACTIVE, SUSPENDED, DELETED
    LocalDateTime lastLoginAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
  }
  
  @Table("user_credentials")
  class UserCredential {
    @Id Long id;
    Long userId;
    String credentialType; // Enum: EMAIL, PHONE, KAKAO
    String identifier;
    String credentialValue; // 암호화된 비밀번호
    Boolean isVerified;
    Boolean isPrimary;
  }

Repository:
  interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUid(String uid);
    Mono<User> findByIdAndStatus(Long id, String status);
  }
  
  interface UserCredentialRepository extends R2dbcRepository<UserCredential, Long> {
    Mono<UserCredential> findByCredentialTypeAndIdentifier(String type, String identifier);
    Flux<UserCredential> findByUserId(Long userId);
  }

체크리스트:
  [ ] Flyway 설정 및 마이그레이션 스크립트 작성
  [ ] 엔티티 클래스 구현 및 매핑 어노테이션
  [ ] Repository 인터페이스 정의
  [ ] 데이터베이스 연결 테스트
  [ ] 기본 CRUD 테스트 코드 작성
Task 1.3: JWT 토큰 서비스 구현
yaml제목: [AUTH-003] JWT 토큰 생성/검증 서비스 구현
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  JWT 액세스 토큰 및 리프레시 토큰 생성/검증 로직 구현

수락 기준:
  - JWT 토큰 생성 서비스 구현
  - JWT 토큰 검증 서비스 구현
  - 토큰 클레임 구조 정의
  - JWKS 엔드포인트 구현

구현 상세:
  JwtTokenService:
    - generateAccessToken(TokenClaims claims): String
    - generateRefreshToken(userId: String): String
    - validateToken(token: String): Mono<TokenClaims>
    - extractClaims(token: String): TokenClaims
    
  TokenClaims:
    - iss: "https://auth.company.com"
    - sub: user_uid
    - aud: ["b2c-commerce", "b2b-distribution"]
    - exp: 액세스 토큰 10분
    - iat: 발급 시간
    - jti: 토큰 고유 ID
    - platform: "B2C" | "B2B"
    - user_type: "CUSTOMER" | "SELLER" | "VENDOR" | "BUYER" | "ADMIN"
    - org_id: 조직 ID (optional)
    - org_type: 조직 타입 (optional)
    - base_role: 기본 역할 (optional)
    
  JwksController:
    GET /.well-known/jwks.json
    응답: {
      "keys": [{
        "kty": "RSA",
        "use": "sig",
        "kid": "key-1",
        "alg": "RS256",
        "n": "...",
        "e": "AQAB"
      }]
    }

설정:
  jwt:
    secret: ${JWT_SECRET}
    access-token-validity: 600 # 10분
    refresh-token-validity: 2592000 # 30일
    issuer: https://auth.company.com

체크리스트:
  [ ] JwtTokenService 구현
  [ ] TokenClaims DTO 정의
  [ ] RSA 키페어 생성 및 관리
  [ ] JWKS 엔드포인트 구현
  [ ] 토큰 만료 처리 로직
  [ ] 토큰 검증 예외 처리
  [ ] 단위 테스트 작성
Task 1.4: 로그인 API 구현
yaml제목: [AUTH-004] 로그인 API 엔드포인트 구현 (Email/Phone)
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  이메일/전화번호 + 비밀번호 로그인 API 구현

수락 기준:
  - POST /auth/login 엔드포인트 구현
  - 비밀번호 검증 로직 (Argon2id)
  - 플랫폼별 라우팅 로직
  - 로그인 이력 저장

API 스펙:
  POST /auth/login
  Request:
    {
      "credential_type": "EMAIL" | "PHONE",
      "identifier": "user@example.com" | "+821012345678",
      "password": "password123",
      "platform": "B2C" | "B2B"
    }
  
  Response (200):
    {
      "access_token": "eyJhbGciOiJSUzI1NiIs...",
      "refresh_token": "rt_550e8400-e29b-41d4...",
      "token_type": "Bearer",
      "expires_in": 600
    }
  
  Response (401):
    {
      "error": "INVALID_CREDENTIALS",
      "message": "이메일 또는 비밀번호가 올바르지 않습니다"
    }

구현 로직:
  AuthController:
    @PostMapping("/auth/login")
    Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request)
  
  AuthService:
    1. UserCredential 조회 (identifier + type)
    2. 비밀번호 검증 (Argon2id)
    3. User 상태 확인 (ACTIVE)
    4. PlatformIdentity 조회 (platform별 user_type 확인)
    5. Organization 정보 조회 (있는 경우)
    6. JWT 토큰 생성 (claims 포함)
    7. Refresh Token 저장 (Redis)
    8. 로그인 이력 저장 (login_history)
    
  PasswordEncoder:
    - Argon2id 사용
    - Salt 자동 생성
    - Memory: 64MB, Iterations: 3, Parallelism: 1

Rate Limiting:
  - 계정당 5회/분 제한
  - IP당 20회/분 제한

체크리스트:
  [ ] LoginRequest/Response DTO 정의
  [ ] AuthController 구현
  [ ] AuthService 비즈니스 로직 구현
  [ ] PasswordEncoder 설정 (Argon2id)
  [ ] 로그인 이력 저장 로직
  [ ] Rate Limiting 구현
  [ ] 통합 테스트 작성
Task 1.5: 토큰 갱신 API 구현
yaml제목: [AUTH-005] Refresh Token 갱신 API 구현
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  Refresh Token을 사용한 Access Token 갱신 API 구현

수락 기준:
  - POST /auth/refresh 엔드포인트 구현
  - Refresh Token Rotation 구현
  - Token Family 관리

API 스펙:
  POST /auth/refresh
  Request:
    {
      "refresh_token": "rt_550e8400-e29b-41d4..."
    }
  
  Response (200):
    {
      "access_token": "eyJhbGciOiJSUzI1NiIs...",
      "refresh_token": "rt_660e9511-f39c-52e5...", // 새로운 RT
      "token_type": "Bearer",
      "expires_in": 600
    }

구현 로직:
  RefreshTokenService:
    1. Redis에서 Refresh Token 조회
    2. Token Family 확인
    3. 기존 토큰 무효화
    4. 새로운 Access Token 생성
    5. 새로운 Refresh Token 생성 (Rotation)
    6. Token Family 업데이트
    
  Token Rotation 정책:
    - 사용된 RT는 즉시 무효화
    - 새로운 RT 발급 (같은 Family)
    - 만료된 RT 사용 시 전체 Family 무효화 (탈취 감지)

체크리스트:
  [ ] RefreshTokenService 구현
  [ ] Redis Repository 구현
  [ ] Token Family 관리 로직
  [ ] 토큰 무효화 처리
  [ ] 보안 예외 처리
  [ ] 테스트 케이스 작성
Task 1.6: 로그아웃 API 구현
yaml제목: [AUTH-006] 로그아웃 API 및 토큰 무효화 구현
타입: Task
담당: Backend Developer  
Story Points: 2

설명: |
  로그아웃 처리 및 토큰 블랙리스트 관리

수락 기준:
  - POST /auth/logout 엔드포인트 구현
  - Refresh Token 무효화
  - Access Token 블랙리스트 (선택적)

API 스펙:
  POST /auth/logout
  Headers:
    Authorization: Bearer {access_token}
  Request:
    {
      "refresh_token": "rt_550e8400-e29b-41d4..."
    }
  
  Response (200):
    {
      "message": "로그아웃 되었습니다"
    }

구현 로직:
  LogoutService:
    1. Access Token에서 jti, exp 추출
    2. Refresh Token Family 전체 삭제
    3. Access Token을 블랙리스트에 추가 (만료시간까지)
    4. 로그아웃 이력 저장

Redis 구조:
  # Blacklist (만료시간까지만 유지)
  blacklist:at:{jti} = true
  TTL = token의 남은 만료시간

체크리스트:
  [ ] LogoutController 구현
  [ ] LogoutService 구현
  [ ] Token 블랙리스트 관리
  [ ] Redis TTL 설정
  [ ] 테스트 케이스 작성
```

---

## 📌 Epic 2: AuthHub Identity - 신원 및 조직 관리

### 🎯 Epic 설명
```
제목: [AUTH-EPIC-002] AuthHub Identity - 신원 및 조직 관리
설명: 사용자 계정 생성, 플랫폼별 신원 관리, 조직 관리 기능 구현
Story Points: 21
Priority: High
Sprint: Sprint 2-3
📋 Tasks
Task 2.1: 사용자 등록 API
yaml제목: [AUTH-007] 사용자 등록 API 구현 (B2C/B2B)
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  플랫폼별 사용자 등록 API 구현

수락 기준:
  - POST /auth/register 엔드포인트
  - 플랫폼별 검증 로직
  - 중복 체크

API 스펙:
  POST /auth/register
  Request (B2C Customer):
    {
      "platform": "B2C",
      "user_type": "CUSTOMER",
      "credential_type": "PHONE",
      "identifier": "+821012345678",
      "password": "password123",
      "verification_code": "123456" // SMS 인증코드
    }
  
  Request (B2B):
    {
      "platform": "B2B",
      "user_type": "VENDOR",
      "credential_type": "EMAIL",
      "identifier": "user@company.com",
      "password": "password123",
      "org_code": "VENDOR_SAMSUNG", // 사전 등록된 조직 코드
      "invite_code": "INV_ABC123" // 초대 코드
    }

  Response (201):
    {
      "user_id": "usr_550e8400-e29b-41d4...",
      "platform": "B2C",
      "user_type": "CUSTOMER",
      "message": "회원가입이 완료되었습니다"
    }

비즈니스 규칙:
  B2C Customer:
    - 휴대폰 번호 중복 체크
    - SMS 인증 필수
    - 자동으로 B2C_DEFAULT 조직 할당
    
  B2B (Vendor/Buyer):
    - 이메일 도메인 검증
    - 초대 코드 필수
    - 조직 자동 연결

구현:
  RegistrationService:
    - validatePlatformRequirements()
    - checkDuplication()
    - createUser()
    - createCredential()
    - createPlatformIdentity()
    - assignDefaultOrganization()

체크리스트:
  [ ] RegistrationRequest DTO 정의
  [ ] 플랫폼별 Validator 구현
  [ ] 중복 체크 로직
  [ ] 트랜잭션 처리
  [ ] SMS 인증 연동 (모의)
  [ ] 테스트 케이스 작성
Task 2.2: 조직 관리 스키마 구현
yaml제목: [AUTH-008] 조직(Organization) 관리 스키마 및 API 구현
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  조직 및 사용자-조직 관계 테이블 구현

수락 기준:
  - organizations 테이블 생성
  - user_organizations 테이블 생성
  - 기본 조직 Seed 데이터

SQL 스크립트:
  V4__create_organizations_table.sql:
    CREATE TABLE organizations (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      org_uid VARCHAR(36) NOT NULL UNIQUE,
      org_code VARCHAR(50) NOT NULL UNIQUE,
      org_name VARCHAR(255) NOT NULL,
      org_type VARCHAR(30) NOT NULL,
      platform_type VARCHAR(20) NOT NULL,
      status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
    );
    
  V5__create_user_organizations_table.sql:
    CREATE TABLE user_organizations (
      user_id BIGINT NOT NULL,
      org_id BIGINT NOT NULL,
      base_role VARCHAR(50) NOT NULL,
      is_primary BOOLEAN DEFAULT FALSE,
      joined_at TIMESTAMP NOT NULL,
      left_at TIMESTAMP,
      PRIMARY KEY (user_id, org_id)
    );

Seed 데이터:
  INSERT INTO organizations VALUES
    ('b2c_default', 'B2C_DEFAULT', 'B2C 기본 조직', 'B2C_DEFAULT', 'B2C', 'ACTIVE'),
    ('internal_b2c', 'INTERNAL_B2C', 'B2C 내부 관리', 'INTERNAL_B2C', 'B2C', 'ACTIVE'),
    ('internal_b2b', 'INTERNAL_B2B', 'B2B 내부 관리', 'INTERNAL_B2B', 'B2B', 'ACTIVE');

엔티티:
  @Table("organizations")
  class Organization {
    Long id;
    String orgUid;
    String orgCode;
    String orgName;
    String orgType;
    String platformType;
    String status;
  }

체크리스트:
  [ ] 마이그레이션 스크립트 작성
  [ ] 엔티티 클래스 구현
  [ ] Repository 구현
  [ ] Seed 데이터 스크립트
  [ ] 관계 매핑 테스트
Task 2.3: 플랫폼 신원 관리
yaml제목: [AUTH-009] Platform Identity 관리 구현
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  플랫폼별 사용자 신원 정보 관리

수락 기준:
  - platform_identities 테이블 생성
  - 플랫폼별 사용자 타입 관리
  - 메타데이터 저장

SQL 스크립트:
  V6__create_platform_identities_table.sql:
    CREATE TABLE platform_identities (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      user_id BIGINT NOT NULL,
      platform_type VARCHAR(20) NOT NULL,
      user_type VARCHAR(20) NOT NULL,
      platform_user_id VARCHAR(50),
      metadata JSON,
      created_at TIMESTAMP NOT NULL,
      UNIQUE KEY uk_user_platform (user_id, platform_type, user_type)
    );

서비스 구현:
  PlatformIdentityService:
    - createIdentity(userId, platform, userType)
    - findByUserAndPlatform(userId, platform)
    - updateMetadata(identityId, metadata)
    
  사용 예시:
    // B2C 고객 생성 시
    platformIdentityService.createIdentity(
      userId, "B2C", "CUSTOMER", 
      {"registered_from": "mobile_app"}
    );
    
    // B2B 벤더 생성 시
    platformIdentityService.createIdentity(
      userId, "B2B", "VENDOR",
      {"company_code": "VENDOR_SAMSUNG", "invite_code": "INV_123"}
    );

체크리스트:
  [ ] 테이블 생성 스크립트
  [ ] 엔티티 구현
  [ ] Service 레이어 구현
  [ ] JSON 메타데이터 처리
  [ ] 테스트 작성
Task 2.4: 사용자 프로필 조회 API
yaml제목: [AUTH-010] 사용자 프로필 및 권한 조회 API
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  현재 사용자 정보 및 권한 조회 API

수락 기준:
  - GET /auth/me 엔드포인트
  - 플랫폼별 정보 반환
  - 조직 정보 포함

API 스펙:
  GET /auth/me
  Headers:
    Authorization: Bearer {access_token}
    
  Response (B2C Customer):
    {
      "user_id": "usr_550e8400...",
      "platform": "B2C",
      "user_type": "CUSTOMER",
      "credentials": [
        {
          "type": "PHONE",
          "identifier": "+8210****5678",
          "is_verified": true,
          "is_primary": true
        }
      ],
      "organization": {
        "org_id": "b2c_default",
        "org_name": "B2C 기본 조직",
        "base_role": "USER"
      }
    }
    
  Response (B2C Seller):
    {
      "user_id": "usr_660e8400...",
      "platform": "B2C",
      "user_type": "SELLER",
      "credentials": [...],
      "organization": {
        "org_id": "org_seller_nike",
        "org_code": "SELLER_NIKE",
        "org_name": "나이키 코리아",
        "org_type": "B2C_SELLER",
        "base_role": "ADMIN"
      }
    }

구현:
  UserProfileService:
    - getUserProfile(userId)
    - getUserCredentials(userId)
    - getUserOrganizations(userId)
    - compilePlatformInfo()

체크리스트:
  [ ] ProfileController 구현
  [ ] UserProfileService 구현
  [ ] Response DTO 정의
  [ ] 민감정보 마스킹 처리
  [ ] 캐싱 전략 구현
  [ ] 테스트 작성
Task 2.5: 비밀번호 변경/재설정
yaml제목: [AUTH-011] 비밀번호 변경 및 재설정 API
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  비밀번호 변경 및 재설정 기능 구현

수락 기준:
  - POST /auth/password/change (로그인 상태)
  - POST /auth/password/reset-request (비로그인)
  - POST /auth/password/reset-confirm (비로그인)

API 스펙:
  # 비밀번호 변경 (로그인 필요)
  POST /auth/password/change
  Headers:
    Authorization: Bearer {access_token}
  Request:
    {
      "current_password": "old_password",
      "new_password": "new_password123"
    }
    
  # 비밀번호 재설정 요청
  POST /auth/password/reset-request
  Request:
    {
      "credential_type": "EMAIL",
      "identifier": "user@example.com"
    }
  Response:
    {
      "message": "비밀번호 재설정 링크가 이메일로 발송되었습니다",
      "reset_token": "rst_xxx" // 개발 환경에서만 반환
    }
    
  # 비밀번호 재설정 확인
  POST /auth/password/reset-confirm
  Request:
    {
      "reset_token": "rst_xxx",
      "new_password": "new_password123"
    }

구현:
  PasswordService:
    - changePassword(userId, currentPw, newPw)
    - requestPasswordReset(credentialType, identifier)
    - confirmPasswordReset(resetToken, newPassword)
    - generateResetToken()
    - validateResetToken()
    
  보안 규칙:
    - 재설정 토큰 유효기간: 1시간
    - 토큰 1회 사용 후 무효화
    - 비밀번호 정책 검증

체크리스트:
  [ ] PasswordController 구현
  [ ] PasswordService 구현  
  [ ] 재설정 토큰 관리 (Redis)
  [ ] 이메일 발송 서비스 연동
  [ ] 비밀번호 정책 검증
  [ ] 테스트 작성
Task 2.6: 초대 시스템 구현
yaml제목: [AUTH-012] B2B 조직 초대 시스템 구현
타입: Task
담당: Backend Developer
Story Points: 4

설명: |
  B2B 조직 구성원 초대 기능 구현

수락 기준:
  - POST /auth/invite 초대 생성
  - GET /auth/invite/verify 초대 확인
  - POST /auth/invite/accept 초대 수락

API 스펙:
  # 초대 생성 (조직 관리자)
  POST /auth/invite
  Headers:
    Authorization: Bearer {access_token}
  Request:
    {
      "email": "newuser@company.com",
      "org_id": "org_vendor_samsung",
      "base_role": "MEMBER",
      "expires_in_days": 7
    }
  Response:
    {
      "invite_code": "INV_ABC123XYZ",
      "invite_url": "https://platform.com/invite/INV_ABC123XYZ",
      "expires_at": "2024-01-07T00:00:00Z"
    }

데이터베이스:
  CREATE TABLE invitations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invite_code VARCHAR(20) UNIQUE,
    org_id BIGINT NOT NULL,
    inviter_user_id BIGINT NOT NULL,
    invitee_email VARCHAR(255) NOT NULL,
    base_role VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    expires_at TIMESTAMP NOT NULL,
    accepted_at TIMESTAMP,
    accepted_user_id BIGINT
  );

구현:
  InvitationService:
    - createInvitation(inviterUserId, orgId, email, role)
    - verifyInvitation(inviteCode)
    - acceptInvitation(inviteCode, userId)
    - expireOldInvitations() // 스케줄러
    
  권한 체크:
    - OWNER, ADMIN만 초대 가능
    - 본인 조직만 초대 가능

체크리스트:
  [ ] Invitation 테이블 생성
  [ ] InvitationController 구현
  [ ] InvitationService 구현
  [ ] 이메일 발송 연동
  [ ] 만료 처리 스케줄러
  [ ] 권한 검증
  [ ] 테스트 작성
```

---

## 📌 Epic 3: B2C Commerce Integration

### 🎯 Epic 설명
```
제목: [AUTH-EPIC-003] B2C Commerce Platform Integration
설명: B2C 커머스 플랫폼과 AuthHub 연동. 고객, 셀러, 관리자 인증 통합
Story Points: 21
Priority: High
Sprint: Sprint 3-4
📋 Tasks
Task 3.1: 카카오 소셜 로그인
yaml제목: [AUTH-013] 카카오 OAuth 2.0 소셜 로그인 구현
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  B2C 고객용 카카오 소셜 로그인 구현

수락 기준:
  - GET /auth/oauth/kakao 인증 시작
  - GET /auth/oauth/kakao/callback 콜백 처리
  - 계정 연결 및 자동 가입

API 플로우:
  1. GET /auth/oauth/kakao?platform=B2C
     -> Redirect to Kakao OAuth
  
  2. GET /auth/oauth/kakao/callback?code=xxx&state=yyy
     -> Token 교환
     -> 사용자 정보 조회
     -> 계정 연결 또는 생성
     -> JWT 발급
     
  3. Redirect to Frontend with tokens

구현:
  KakaoOAuthService:
    - getAuthorizationUrl(state)
    - exchangeToken(code)
    - getKakaoUserInfo(accessToken)
    - linkOrCreateUser(kakaoUserInfo)
    
  설정:
    kakao:
      client-id: ${KAKAO_CLIENT_ID}
      client-secret: ${KAKAO_CLIENT_SECRET}
      redirect-uri: https://auth.company.com/auth/oauth/kakao/callback
      
  외부 연동 테이블:
    CREATE TABLE social_accounts (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      user_id BIGINT NOT NULL,
      provider VARCHAR(20) NOT NULL,
      provider_user_id VARCHAR(100) NOT NULL,
      provider_email VARCHAR(255),
      linked_at TIMESTAMP NOT NULL,
      UNIQUE KEY uk_provider_user (provider, provider_user_id)
    );

계정 연결 로직:
  1. provider_user_id로 기존 연결 확인
  2. 없으면 email로 기존 계정 확인
  3. 둘 다 없으면 신규 계정 생성
  4. platform_identity 생성 (B2C, CUSTOMER)

체크리스트:
  [ ] Kakao OAuth 설정
  [ ] OAuthController 구현
  [ ] KakaoOAuthService 구현
  [ ] 계정 연결 로직
  [ ] 에러 처리 (취소, 실패)
  [ ] 테스트 작성
Task 3.2: SMS 인증 시스템
yaml제목: [AUTH-014] SMS 인증 시스템 구현 (AWS SNS)
타입: Task
담당: Backend Developer
Story Points: 4

설명: |
  휴대폰 번호 인증을 위한 SMS 발송 시스템

수락 기준:
  - POST /auth/sms/send 인증번호 발송
  - POST /auth/sms/verify 인증번호 확인
  - Rate limiting 적용

API 스펙:
  # SMS 발송
  POST /auth/sms/send
  Request:
    {
      "phone_number": "+821012345678",
      "purpose": "REGISTRATION" | "LOGIN" | "PASSWORD_RESET"
    }
  Response:
    {
      "message": "인증번호가 발송되었습니다",
      "expires_in": 180,
      "session_id": "sms_session_xxx"
    }
    
  # SMS 검증
  POST /auth/sms/verify
  Request:
    {
      "session_id": "sms_session_xxx",
      "verification_code": "123456"
    }
  Response:
    {
      "verified": true,
      "token": "sms_verified_token_xxx" // 회원가입 시 사용
    }

구현:
  SmsService:
    - sendVerificationCode(phoneNumber, purpose)
    - verifyCode(sessionId, code)
    - generateRandomCode(6자리)
    
  AWS SNS 연동:
    @Service
    class AwsSnsService {
      private final AmazonSNS snsClient;
      
      void sendSms(String phoneNumber, String message) {
        PublishRequest request = new PublishRequest()
          .withPhoneNumber(phoneNumber)
          .withMessage(message);
        snsClient.publish(request);
      }
    }
    
  Redis 저장:
    sms:session:{session_id}:
      phone_number: "+821012345678"
      code: "123456"
      purpose: "REGISTRATION"
      attempts: 0
      TTL: 180초

Rate Limiting:
  - 동일 번호 1분에 1회
  - 동일 번호 하루 5회
  - 검증 시도 5회 제한

체크리스트:
  [ ] AWS SNS 설정
  [ ] SmsController 구현
  [ ] SmsService 구현
  [ ] Redis 세션 관리
  [ ] Rate Limiting 구현
  [ ] 테스트 환경 Mock
  [ ] 통합 테스트
Task 3.3: B2C 셀러 관리 API
yaml제목: [AUTH-015] B2C 셀러 조직 및 멤버 관리 API
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  B2C 플랫폼 셀러 조직 생성 및 직원 관리

수락 기준:
  - POST /auth/organizations 조직 생성
  - POST /auth/organizations/{id}/members 멤버 추가
  - PUT /auth/organizations/{id}/members/{userId} 권한 변경

API 스펙:
  # 셀러 조직 생성 (관리자용)
  POST /auth/organizations
  Headers:
    Authorization: Bearer {admin_token}
  Request:
    {
      "org_code": "SELLER_ADIDAS",
      "org_name": "아디다스 코리아",
      "org_type": "B2C_SELLER",
      "platform_type": "B2C"
    }
  Response:
    {
      "org_id": "org_770e8400...",
      "org_code": "SELLER_ADIDAS",
      "status": "PENDING"
    }
    
  # 셀러 직원 추가
  POST /auth/organizations/{org_id}/members
  Request:
    {
      "email": "employee@adidas.com",
      "base_role": "MANAGER"
    }
    
  # 권한 변경
  PUT /auth/organizations/{org_id}/members/{user_id}
  Request:
    {
      "base_role": "ADMIN"
    }

비즈니스 규칙:
  셀러 역할 계층:
    - OWNER: 대표 (모든 권한)
    - ADMIN: 관리자 (직원 관리 가능)
    - MANAGER: 매니저 (상품/주문 관리)
    - MEMBER: 일반 직원 (조회만)
    
  권한 규칙:
    - OWNER는 1명만
    - ADMIN 이상만 직원 추가/삭제
    - 자기보다 높은 권한 부여 불가

구현:
  OrganizationService:
    - createOrganization(dto)
    - addMember(orgId, memberDto)
    - updateMemberRole(orgId, userId, newRole)
    - removeMember(orgId, userId)
    
  권한 체크:
    @PreAuthorize("hasRole('ADMIN') or hasOrgRole(#orgId, 'ADMIN')")

체크리스트:
  [ ] OrganizationController 구현
  [ ] OrganizationService 구현
  [ ] 권한 검증 로직
  [ ] 역할 계층 검증
  [ ] 트랜잭션 처리
  [ ] 감사 로그
  [ ] 테스트 작성
Task 3.4: B2C 관리자 권한 시스템
yaml제목: [AUTH-016] B2C 내부 관리자 세분화 권한 시스템
타입: Task
담당: Backend Developer
Story Points: 4

설명: |
  B2C 내부 관리자의 레벨별 권한 관리

수락 기준:
  - 관리자 레벨 정의 (SUPER, SENIOR, JUNIOR)
  - 권한별 접근 제어
  - 정산 권한 특별 관리

관리자 권한 매트릭스:
  SUPER_ADMIN:
    - 모든 권한
    - 정산 처리 가능
    - 다른 관리자 권한 부여
    
  SENIOR_ADMIN:
    - 셀러 승인/관리
    - 정산 조회 (처리 불가)
    - 상품/주문 관리
    
  JUNIOR_ADMIN:
    - 고객 문의 대응
    - 상품/주문 조회
    - 제한적 수정 권한

데이터 구조:
  admin_permissions:
    - admin_level: SUPER | SENIOR | JUNIOR
    - permission_code: String
    - granted: Boolean
    
  Permission Codes:
    - SETTLEMENT_VIEW: 정산 조회
    - SETTLEMENT_PROCESS: 정산 처리
    - SELLER_APPROVE: 셀러 승인
    - SELLER_MANAGE: 셀러 관리
    - CUSTOMER_MANAGE: 고객 관리
    - PRODUCT_MANAGE: 상품 관리
    - ORDER_MANAGE: 주문 관리

API:
  # 관리자 권한 조회
  GET /auth/admin/permissions
  Response:
    {
      "user_id": "usr_xxx",
      "admin_level": "SENIOR",
      "permissions": [
        "SETTLEMENT_VIEW",
        "SELLER_APPROVE",
        "SELLER_MANAGE"
      ]
    }

구현:
  AdminPermissionService:
    - getAdminPermissions(userId)
    - hasPermission(userId, permissionCode)
    - grantPermission(userId, permissionCode)
    - revokePermission(userId, permissionCode)

체크리스트:
  [ ] Permission 테이블 설계
  [ ] AdminPermissionService 구현
  [ ] 권한 체크 Annotation
  [ ] 권한 매트릭스 설정
  [ ] 감사 로그
  [ ] 테스트 작성
Task 3.5: JWT 검증 라이브러리
yaml제목: [AUTH-017] B2C 서비스용 JWT 검증 라이브러리 개발
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  B2C 서비스에서 사용할 JWT 검증 공통 라이브러리

수락 기준:
  - Spring Security Filter 구현
  - 자동 설정 지원
  - 권한 체크 유틸리티

라이브러리 구조:
  auth-jwt-spring-boot-starter/
    - JwtAuthenticationFilter
    - JwtTokenValidator  
    - AuthPrincipal
    - @RequiresPlatform
    - @RequiresUserType
    - @RequiresOrgRole

사용 예시:
  # B2C Service에서 사용
  @RestController
  class ProductController {
    
    @GetMapping("/products")
    @RequiresPlatform("B2C")
    @RequiresUserType({"CUSTOMER", "SELLER"})
    public Flux<Product> getProducts(@AuthPrincipal AuthUser user) {
      // user.getUserId()
      // user.getPlatform()
      // user.getUserType()
      // user.getOrgId()
    }
    
    @PostMapping("/seller/products")
    @RequiresPlatform("B2C")
    @RequiresUserType("SELLER")
    @RequiresOrgRole({"OWNER", "ADMIN", "MANAGER"})
    public Mono<Product> createProduct(
      @AuthPrincipal AuthUser user,
      @RequestBody ProductDto dto
    ) {
      // 셀러 권한 확인 후 상품 생성
    }
  }

설정:
  auth:
    jwt:
      public-key-url: https://auth.company.com/.well-known/jwks.json
      issuer: https://auth.company.com
      audience: b2c-commerce
      cache-duration: 600

구현:
  JwtAuthenticationFilter:
    - JWT 토큰 추출
    - JWKS 기반 서명 검증
    - 클레임 파싱
    - SecurityContext 설정
    
  JwtTokenValidator:
    - 서명 검증
    - 만료 시간 체크
    - Issuer/Audience 검증
    - 블랙리스트 체크 (옵션)

체크리스트:
  [ ] 라이브러리 프로젝트 생성
  [ ] JwtAuthenticationFilter 구현
  [ ] 권한 체크 Annotations
  [ ] 자동 설정 클래스
  [ ] 사용 가이드 문서
  [ ] 샘플 프로젝트
  [ ] 테스트 작성
```

---

## 📌 Epic 4: B2B Distribution Integration

### 🎯 Epic 설명
```
제목: [AUTH-EPIC-004] B2B Distribution Platform Integration  
설명: B2B 유통 플랫폼과 AuthHub 연동. 판매사/구매사 인증 및 구독제 연동
Story Points: 18
Priority: High
Sprint: Sprint 4-5
📋 Tasks
Task 4.1: B2B 기업 등록 API
yaml제목: [AUTH-018] B2B 기업(Vendor/Buyer) 등록 및 승인 API
타입: Task
담당: Backend Developer
Story Points: 5

설명: |
  B2B 플랫폼 기업 등록 및 승인 프로세스

수락 기준:
  - POST /auth/b2b/companies 기업 등록 신청
  - PUT /auth/b2b/companies/{id}/approve 승인
  - 기업 검증 로직

API 스펙:
  # 기업 등록 신청
  POST /auth/b2b/companies
  Request:
    {
      "company_type": "VENDOR" | "BUYER",
      "company_name": "삼성전자",
      "business_number": "124-81-00998",
      "representative_email": "admin@samsung.com",
      "representative_name": "홍길동",
      "documents": {
        "business_license": "base64_encoded_file",
        "tax_certificate": "base64_encoded_file"
      }
    }
  Response:
    {
      "application_id": "app_xxx",
      "org_code": "VENDOR_SAMSUNG_PENDING",
      "status": "PENDING",
      "message": "입점 신청이 접수되었습니다"
    }
    
  # 기업 승인 (관리자)
  PUT /auth/b2b/companies/{org_id}/approve
  Headers:
    Authorization: Bearer {admin_token}
  Request:
    {
      "approved": true,
      "subscription_plan": "STANDARD",
      "credit_limit": 100000000,
      "notes": "서류 검토 완료"
    }

비즈니스 프로세스:
  1. 기업 정보 입력 및 서류 제출
  2. 사업자 번호 검증 (외부 API)
  3. 관리자 심사
  4. 승인 시:
     - Organization 생성
     - 대표자 계정 생성
     - 초기 구독 플랜 설정
  5. 이메일 통보

구현:
  CompanyRegistrationService:
    - submitApplication(dto)
    - validateBusinessNumber(businessNumber)
    - approveCompany(orgId, approvalDto)
    - rejectCompany(orgId, reason)
    - createInitialAdmin(orgId, email)

체크리스트:
  [ ] 기업 신청 테이블 설계
  [ ] CompanyController 구현
  [ ] 사업자 번호 검증 API 연동
  [ ] 서류 저장 (S3)
  [ ] 승인 워크플로우
  [ ] 이메일 알림
  [ ] 테스트 작성
Task 4.2: B2B 구독제 관리
yaml제목: [AUTH-019] B2B 구독제(Subscription) 관리 시스템
타입: Task
담당: Backend Developer
Story Points: 4

설명: |
  B2B 기업 구독 플랜 관리 및 권한 제어

수락 기준:
  - 구독 플랜 정의 (BASIC, STANDARD, PREMIUM, ENTERPRISE)
  - 플랜별 기능 제한
  - 구독 상태 관리

구독 플랜 정의:
  BASIC:
    - 월 $99
    - 상품 100개
    - 월 주문 50건
    - API 1,000회/일
    
  STANDARD:
    - 월 $299
    - 상품 1,000개
    - 월 주문 500건
    - API 10,000회/일
    
  PREMIUM:
    - 월 $999
    - 상품 무제한
    - 월 주문 5,000건
    - API 100,000회/일
    
  ENTERPRISE:
    - 맞춤형 가격
    - 모든 기능 무제한
    - 전담 지원

데이터베이스:
  company_subscriptions:
    - company_id: BIGINT
    - plan_code: VARCHAR(30)
    - status: ACTIVE | EXPIRED | SUSPENDED
    - start_date: DATE
    - end_date: DATE
    - auto_renew: BOOLEAN
    - payment_method: VARCHAR(50)
    
  subscription_usage:
    - company_id: BIGINT
    - metric_type: VARCHAR(50)
    - current_value: INT
    - limit_value: INT
    - reset_date: DATE

API:
  # 구독 상태 조회
  GET /auth/b2b/companies/{company_id}/subscription
  Response:
    {
      "plan": "STANDARD",
      "status": "ACTIVE",
      "expires_at": "2024-12-31",
      "usage": {
        "products": { "current": 450, "limit": 1000 },
        "orders": { "current": 230, "limit": 500 },
        "api_calls": { "current": 5420, "limit": 10000 }
      }
    }

구현:
  SubscriptionService:
    - getSubscription(companyId)
    - checkLimit(companyId, metricType)
    - incrementUsage(companyId, metricType)
    - upgradeDowngrade(companyId, newPlan)
    - suspendForNonPayment(companyId)

JWT 클레임 추가:
  "subscription": {
    "plan": "STANDARD",
    "status": "ACTIVE"
  }

체크리스트:
  [ ] 구독 테이블 설계
  [ ] SubscriptionService 구현
  [ ] 사용량 추적 로직
  [ ] 제한 체크 미들웨어
  [ ] 자동 갱신 스케줄러
  [ ] 결제 연동 준비
  [ ] 테스트 작성
Task 4.3: B2B 기업 멤버 권한
yaml제목: [AUTH-020] B2B 기업 내 직원 역할 및 권한 관리
타입: Task
담당: Backend Developer
Story Points: 4

설명: |
  B2B 기업 직원별 세분화된 권한 관리

수락 기준:
  - 역할별 권한 정의
  - 구매 한도 관리
  - 승인 워크플로우

역할 정의:
  VENDOR (판매사):
    - OWNER: 모든 권한
    - ADMIN: 직원 관리, 가격 정책
    - SALES_MANAGER: 상품/재고 관리
    - SALES_STAFF: 주문 조회
    
  BUYER (구매사):
    - OWNER: 모든 권한
    - PURCHASING_MANAGER: 구매 승인, 한도 설정
    - PURCHASER: 구매 요청 (한도 내)
    - VIEWER: 조회만

권한 매트릭스:
  company_member_permissions:
    - member_id: BIGINT
    - permission_code: VARCHAR(50)
    - granted: BOOLEAN
    - granted_by: BIGINT
    - granted_at: TIMESTAMP
    
  purchase_limits:
    - member_id: BIGINT
    - daily_limit: DECIMAL(15,2)
    - monthly_limit: DECIMAL(15,2)
    - per_order_limit: DECIMAL(15,2)
    - requires_approval_above: DECIMAL(15,2)

API:
  # 직원 권한 설정
  PUT /auth/b2b/companies/{company_id}/members/{member_id}/permissions
  Request:
    {
      "role": "PURCHASER",
      "permissions": [
        "CREATE_ORDER",
        "VIEW_PRODUCTS"
      ],
      "purchase_limits": {
        "daily_limit": 1000000,
        "monthly_limit": 10000000,
        "requires_approval_above": 500000
      }
    }

구현:
  B2BMemberPermissionService:
    - setMemberRole(companyId, memberId, role)
    - setPurchaseLimit(memberId, limits)
    - checkPurchaseLimit(memberId, amount)
    - requiresApproval(memberId, amount)

체크리스트:
  [ ] 권한 테이블 설계
  [ ] 구매 한도 테이블
  [ ] PermissionService 구현
  [ ] 승인 워크플로우
  [ ] 권한 체크 로직
  [ ] 감사 로그
  [ ] 테스트 작성
Task 4.4: B2B JWT 검증 라이브러리
yaml제목: [AUTH-021] B2B 서비스용 JWT 검증 및 구독 체크 라이브러리
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  B2B 서비스용 JWT 검증 라이브러리 (구독제 체크 포함)

수락 기준:
  - JWT 검증 + 구독 상태 체크
  - 사용량 제한 체크
  - 구매 한도 검증

라이브러리 기능:
  @RestController
  class B2BOrderController {
    
    @PostMapping("/orders")
    @RequiresPlatform("B2B")
    @RequiresUserType("BUYER")
    @RequiresSubscription(minPlan = "STANDARD")
    @CheckUsageLimit("monthly_orders")
    public Mono<Order> createOrder(
      @AuthPrincipal B2BAuthUser user,
      @Valid @RequestBody OrderDto dto
    ) {
      // 구독 플랜 체크 완료
      // 월 주문 한도 체크 완료
      // user.getCompanyId()
      // user.getPurchaseLimit()
    }
    
    @PostMapping("/products")
    @RequiresPlatform("B2B")
    @RequiresUserType("VENDOR")
    @CheckUsageLimit("product_count")
    public Mono<Product> createProduct(...) {
      // 상품 등록 한도 체크
    }
  }

추가 기능:
  B2BAuthContext:
    - getSubscriptionPlan()
    - getSubscriptionStatus()
    - getRemainingLimit(metricType)
    - getPurchaseLimit()
    - requiresApproval(amount)

설정:
  auth:
    b2b:
      check-subscription: true
      check-usage-limits: true
      cache-duration: 300

체크리스트:
  [ ] B2B 라이브러리 프로젝트
  [ ] 구독 체크 Annotation
  [ ] 사용량 체크 Annotation
  [ ] B2BAuthContext 구현
  [ ] 캐싱 전략
  [ ] 사용 가이드
  [ ] 테스트 작성
Task 4.5: B2B 관리자 대시보드 API
yaml제목: [AUTH-022] B2B 플랫폼 관리자 대시보드 API
타입: Task
담당: Backend Developer
Story Points: 2

설명: |
  B2B 플랫폼 관리자용 통계 및 관리 API

수락 기준:
  - 기업 현황 조회
  - 구독 현황 통계
  - 권한 기반 접근

API 스펙:
  # 기업 목록 조회
  GET /auth/b2b/admin/companies
  Query: ?type=VENDOR&status=ACTIVE&subscription=PREMIUM
  Response:
    {
      "companies": [{
        "org_id": "org_xxx",
        "company_name": "삼성전자",
        "type": "VENDOR",
        "subscription": "PREMIUM",
        "member_count": 45,
        "created_at": "2024-01-01"
      }],
      "total": 150,
      "page": 1
    }
    
  # 구독 통계
  GET /auth/b2b/admin/statistics/subscriptions
  Response:
    {
      "by_plan": {
        "BASIC": 45,
        "STANDARD": 78,
        "PREMIUM": 23,
        "ENTERPRISE": 4
      },
      "revenue": {
        "monthly": 125000,
        "annual": 1500000
      },
      "churn_rate": 2.5
    }

권한 체크:
  B2B_SUPER_ADMIN:
    - 모든 데이터 접근
    - 구독 플랜 변경
    
  B2B_SALES_MANAGER:
    - 담당 기업만 조회
    - 통계 조회

체크리스트:
  [ ] AdminController 구현
  [ ] 통계 쿼리 작성
  [ ] 권한 체크 로직
  [ ] 페이징 처리
  [ ] 캐싱 전략
  [ ] 테스트 작성
```

---

## 📌 Epic 5: Security & Operations

### 🎯 Epic 설명
```
제목: [AUTH-EPIC-005] Security & Operations
설명: 보안 강화, 모니터링, 로깅, 운영 도구 구축
Story Points: 16
Priority: Medium
Sprint: Sprint 5-6
📋 Tasks
Task 5.1: Rate Limiting 구현
yaml제목: [AUTH-023] API Rate Limiting 시스템 구현
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  DDoS 방어 및 API 사용량 제한

수락 기준:
  - IP 기반 Rate Limiting
  - 계정 기반 Rate Limiting
  - API Key 기반 Rate Limiting

Rate Limit 정책:
  로그인 API:
    - IP당: 20회/분
    - 계정당: 5회/분
    - 실패 시: 지수 백오프
    
  일반 API:
    - 인증 사용자: 1000회/분
    - IP당: 100회/분
    
  B2B API (구독 플랜별):
    - BASIC: 1,000회/일
    - STANDARD: 10,000회/일
    - PREMIUM: 100,000회/일

구현:
  @Component
  class RateLimitFilter {
    
    @Autowired
    RedisTemplate<String, String> redis;
    
    public Mono<Void> filter(ServerWebExchange exchange) {
      String key = getRateLimitKey(exchange);
      Long count = redis.increment(key);
      
      if (count == 1) {
        redis.expire(key, 60, TimeUnit.SECONDS);
      }
      
      if (count > getLimit(exchange)) {
        return sendRateLimitResponse(exchange);
      }
      
      return chain.filter(exchange);
    }
  }

응답 헤더:
  X-RateLimit-Limit: 1000
  X-RateLimit-Remaining: 950
  X-RateLimit-Reset: 1609459200

Redis 구조:
  rate_limit:ip:{ip}:{api}: count
  rate_limit:user:{user_id}:{api}: count
  rate_limit:account:{account}:login: count

체크리스트:
  [ ] RateLimitFilter 구현
  [ ] Redis 카운터 관리
  [ ] 설정 가능한 정책
  [ ] 응답 헤더 추가
  [ ] 지수 백오프
  [ ] 화이트리스트
  [ ] 테스트 작성
Task 5.2: 감사 로그 시스템
yaml제목: [AUTH-024] 감사 로그(Audit Log) 시스템 구현
타입: Task
담당: Backend Developer
Story Points: 3

설명: |
  모든 인증/인가 활동 로깅 및 추적

수락 기준:
  - 로그인/로그아웃 이력
  - 권한 변경 이력
  - 민감 작업 로깅

로그 대상:
  인증 이벤트:
    - 로그인 성공/실패
    - 로그아웃
    - 비밀번호 변경
    - 계정 잠금/해제
    
  권한 이벤트:
    - 역할 변경
    - 권한 부여/회수
    - 조직 가입/탈퇴
    
  관리 이벤트:
    - 사용자 생성/삭제
    - 조직 생성/수정
    - 구독 변경

데이터베이스:
  audit_logs:
    - id: BIGINT AUTO_INCREMENT
    - event_type: VARCHAR(50)
    - event_subtype: VARCHAR(50)
    - user_id: BIGINT
    - target_user_id: BIGINT
    - target_org_id: BIGINT
    - ip_address: VARCHAR(45)
    - user_agent: VARCHAR(500)
    - request_id: VARCHAR(36)
    - request_data: JSON
    - response_data: JSON
    - status: SUCCESS | FAILURE
    - error_message: TEXT
    - created_at: TIMESTAMP

구현:
  @Aspect
  @Component
  class AuditLogAspect {
    
    @Around("@annotation(Auditable)")
    public Object logAuditEvent(ProceedingJoinPoint pjp) {
      AuditLog log = createAuditLog(pjp);
      
      try {
        Object result = pjp.proceed();
        log.setStatus("SUCCESS");
        saveAuditLog(log);
        return result;
      } catch (Exception e) {
        log.setStatus("FAILURE");
        log.setErrorMessage(e.getMessage());
        saveAuditLog(log);
        throw e;
      }
    }
  }

사용:
  @PostMapping("/auth/login")
  @Auditable(event = "AUTH", subtype = "LOGIN")
  public Mono<TokenResponse> login(...) { }

체크리스트:
  [ ] AuditLog 테이블 설계
  [ ] AuditLogAspect 구현
  [ ] @Auditable 어노테이션
  [ ] 비동기 로그 저장
  [ ] 로그 조회 API
  [ ] 보관 정책 (90일)
  [ ] 테스트 작성
Task 5.3: 모니터링 대시보드
yaml제목: [AUTH-025] 모니터링 메트릭 및 대시보드 구성
타입: Task
담당: DevOps Engineer
Story Points: 3

설명: |
  시스템 모니터링 및 알림 설정

수락 기준:
  - Prometheus 메트릭 수집
  - Grafana 대시보드
  - CloudWatch 알림

메트릭 수집:
  비즈니스 메트릭:
    - auth_login_total{platform, user_type, status}
    - auth_token_issued_total{token_type}
    - auth_active_sessions{platform}
    - auth_failed_attempts{reason}
    
  성능 메트릭:
    - auth_api_latency{endpoint, percentile}
    - auth_db_connections{status}
    - auth_redis_operations{operation}
    
  보안 메트릭:
    - auth_rate_limit_exceeded{type}
    - auth_invalid_tokens{reason}
    - auth_suspicious_activity{type}

Grafana 대시보드:
  1. Overview Dashboard
     - 로그인 추이
     - 활성 세션
     - 플랫폼별 분포
     
  2. Performance Dashboard
     - API 응답시간
     - 처리량 (TPS)
     - 에러율
     
  3. Security Dashboard
     - 실패 로그인
     - Rate Limit 현황
     - 이상 패턴 감지

CloudWatch 알람:
  - 로그인 실패율 > 20%
  - API 응답시간 > 1초
  - DB 연결 실패
  - Redis 연결 실패

구현:
  @Configuration
  class MetricsConfig {
    
    @Bean
    MeterRegistry meterRegistry() {
      return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
    
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
      return new TimedAspect(registry);
    }
  }

체크리스트:
  [ ] Micrometer 설정
  [ ] 커스텀 메트릭 정의
  [ ] Prometheus 연동
  [ ] Grafana 대시보드
  [ ] CloudWatch 설정
  [ ] 알람 규칙 정의
  [ ] 문서화
Task 5.4: 토큰 블랙리스트
yaml제목: [AUTH-026] JWT 토큰 블랙리스트 관리 시스템
타입: Task
담당: Backend Developer
Story Points: 2

설명: |
  로그아웃 및 강제 만료된 토큰 관리

수락 기준:
  - 토큰 블랙리스트 저장
  - 효율적인 조회
  - 자동 정리

구현:
  TokenBlacklistService:
    - addToBlacklist(jti, expiresAt)
    - isBlacklisted(jti)
    - cleanupExpired()
    
  Redis 구조:
    # Set for quick lookup
    blacklist:tokens = SET of JTIs
    
    # Hash for expiry tracking  
    blacklist:expiry = {
      "jti_xxx": 1234567890,
      "jti_yyy": 1234567891
    }
    
  스케줄러:
    @Scheduled(cron = "0 0 * * * *")
    void cleanupExpiredTokens() {
      long now = System.currentTimeMillis() / 1000;
      
      // 만료된 토큰 조회
      Set<String> expired = redis.zrangeByScore(
        "blacklist:expiry", 0, now
      );
      
      // 블랙리스트에서 제거
      expired.forEach(jti -> {
        redis.srem("blacklist:tokens", jti);
        redis.zrem("blacklist:expiry", jti);
      });
    }

체크리스트:
  [ ] BlacklistService 구현
  [ ] Redis 데이터 구조
  [ ] 정리 스케줄러
  [ ] 성능 최적화
  [ ] 테스트 작성
Task 5.5: 보안 헤더 설정
yaml제목: [AUTH-027] 보안 헤더 및 CORS 설정
타입: Task
담당: Backend Developer
Story Points: 2

설명: |
  보안 헤더 설정 및 CORS 정책 구현

수락 기준:
  - 보안 헤더 추가
  - CORS 정책 설정
  - CSP 헤더 구성

보안 헤더:
  X-Frame-Options: DENY
  X-Content-Type-Options: nosniff
  X-XSS-Protection: 1; mode=block
  Strict-Transport-Security: max-age=31536000
  Content-Security-Policy: default-src 'self'

CORS 설정:
  @Configuration
  class SecurityConfig {
    
    @Bean
    CorsWebFilter corsFilter() {
      CorsConfiguration config = new CorsConfiguration();
      
      config.setAllowedOrigins(Arrays.asList(
        "https://b2c.company.com",
        "https://b2b.company.com"
      ));
      
      config.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
      ));
      
      config.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type"
      ));
      
      config.setExposedHeaders(Arrays.asList(
        "X-RateLimit-Limit",
        "X-RateLimit-Remaining"
      ));
      
      config.setAllowCredentials(true);
      config.setMaxAge(3600L);
      
      UrlBasedCorsConfigurationSource source = 
        new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);
      
      return new CorsWebFilter(source);
    }
  }

체크리스트:
  [ ] SecurityConfig 구현
  [ ] 보안 헤더 필터
  [ ] CORS 설정
  [ ] CSP 정책 정의
  [ ] 환경별 설정
  [ ] 테스트 작성
Task 5.6: 헬스체크 및 상태
yaml제목: [AUTH-028] 헬스체크 및 시스템 상태 API
타입: Task
담당: Backend Developer
Story Points: 2

설명: |
  시스템 상태 확인 및 헬스체크 엔드포인트

수락 기준:
  - 헬스체크 엔드포인트
  - 의존성 체크
  - 상세 상태 정보

API:
  # 기본 헬스체크
  GET /health
  Response:
    {
      "status": "UP",
      "timestamp": "2024-01-01T00:00:00Z"
    }
    
  # 상세 헬스체크 (인증 필요)
  GET /health/details
  Response:
    {
      "status": "UP",
      "components": {
        "database": {
          "status": "UP",
          "details": {
            "connections": 45,
            "max_connections": 100
          }
        },
        "redis": {
          "status": "UP",
          "details": {
            "used_memory": "125MB",
            "connected_clients": 12
          }
        }
      },
      "metrics": {
        "uptime": 864000,
        "requests_per_second": 125,
        "active_sessions": 1523
      }
    }

구현:
  @RestController
  class HealthController {
    
    @GetMapping("/health")
    public Mono<Health> health() {
      return Mono.just(Health.up().build());
    }
    
    @GetMapping("/health/details")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<HealthDetails> detailedHealth() {
      return Mono.zip(
        checkDatabase(),
        checkRedis(),
        getMetrics()
      ).map(this::buildHealthDetails);
    }
  }

체크리스트:
  [ ] HealthController 구현
  [ ] 데이터베이스 체크
  [ ] Redis 체크
  [ ] 메트릭 수집
  [ ] 인증 설정
  [ ] 테스트 작성

📊 에픽 요약 및 우선순위
Epic제목Story Points우선순위SprintEPIC-001AuthHub Core34Critical1-2EPIC-002Identity Management21High2-3EPIC-003B2C Integration21High3-4EPIC-004B2B Integration18High4-5EPIC-005Security & Operations16Medium5-6Total1106 Sprints
🎯 Definition of Done
모든 태스크는 다음 조건을 만족해야 완료:

✅ 코드 리뷰 완료
✅ 단위 테스트 작성 (Coverage > 80%)
✅ 통합 테스트 통과
✅ API 문서 작성
✅ 보안 검토 완료
✅ 성능 테스트 통과
✅ 배포 스크립트 준비
✅ 모니터링 설정 완료

이렇게 상세한 Jira 티켓을 작성했습니다. 각 태스크는 개발자가 바로 작업할 수 있도록 구체적인 구현 방법, API 스펙, 데이터베이스 스키마, 체크리스트를 포함했습니다.