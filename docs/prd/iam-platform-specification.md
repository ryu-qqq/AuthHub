# AuthHub IAM Platform Specification

**Version**: 1.0.0-MVP
**Last Updated**: 2025-12-03
**Project**: Multi-Tenant Identity & Access Management Platform

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Domain Model Overview](#2-domain-model-overview)
3. [Tenant Domain](#3-tenant-domain)
4. [Organization Domain](#4-organization-domain)
5. [User Domain](#5-user-domain)
6. [Role & Permission Domain](#6-role--permission-domain)
7. [Auth Domain](#7-auth-domain)
8. [Cross-Cutting Concerns](#8-cross-cutting-concerns)
9. [Implementation Status](#9-implementation-status)
10. [Roadmap](#10-roadmap)

---

## 1. Executive Summary

### 1.1 Purpose

AuthHub는 **B2B SaaS 환경을 위한 멀티테넌트 IAM(Identity & Access Management) 플랫폼**입니다.

### 1.2 Core Principles

| Principle | Description |
|-----------|-------------|
| **Multi-Tenancy** | 완전한 테넌트 격리 (Row-level isolation) |
| **Hexagonal Architecture** | Ports & Adapters 패턴으로 인프라 독립성 보장 |
| **DDD** | Aggregate 중심 설계, 불변 객체, Long FK 전략 |
| **CQRS** | Command/Query 분리로 확장성 확보 |
| **Zero-Tolerance Rules** | Lombok 금지, Law of Demeter, 트랜잭션 경계 엄수 |

### 1.3 Target Users

| User Type | Description | Auth Method |
|-----------|-------------|-------------|
| **PUBLIC** | 일반 소비자 (B2C) | 전화번호/소셜 로그인 |
| **INTERNAL** | 기업 사용자 (B2B) | 이메일 + 비밀번호 |

---

## 2. Domain Model Overview

### 2.1 Aggregate Relationships

```
┌─────────────────────────────────────────────────────────────────────┐
│                            TENANT                                    │
│  (Company/Platform Instance)                                         │
│  ┌─────────────────────────────────────────────────────────────────┐│
│  │                                                                 ││
│  │   ┌───────────────┐         ┌───────────────┐                   ││
│  │   │ Organization  │ 1───* │    User        │                   ││
│  │   │ (Department)  │←──────│ (INTERNAL)     │                   ││
│  │   └───────────────┘         └───────────────┘                   ││
│  │          │                         │                             ││
│  │          │                         │ *                           ││
│  │          │                   ┌─────┴─────┐                       ││
│  │          │                   │  UserRole  │                      ││
│  │          │                   └─────┬─────┘                       ││
│  │          │                         │ *                           ││
│  │   ┌──────┴──────┐           ┌──────┴──────┐                      ││
│  │   │    Role     │ ←─────── │    Role      │                     ││
│  │   │  (Tenant    │           │  (with       │                     ││
│  │   │   Scoped)   │           │ Permissions) │                     ││
│  │   └─────────────┘           └──────────────┘                     ││
│  │                                                                  ││
│  └──────────────────────────────────────────────────────────────────┘│
│                                                                      │
│   PUBLIC Users (No Organization) ────────────────────────────────────│
│   → "Connectly Public Tenant"에 속함                                  │
└──────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────┐
                    │   SYSTEM ROLES      │
                    │  (Global, tenantId  │
                    │   = null)           │
                    │  - SUPER_ADMIN      │
                    └─────────────────────┘
```

### 2.2 Core Aggregates

| Aggregate | Identity | Invariants |
|-----------|----------|------------|
| **Tenant** | TenantId (Long) | 유일한 이름, 상태 전이 규칙 |
| **Organization** | OrganizationId (Long) | Tenant에 종속, 상태 전이 규칙 |
| **User** | UserId (UUID) | Tenant 필수, Credential 무결성 |
| **Role** | RoleId (Long) | 유일한 이름(Tenant 범위), Permission 집합 |
| **Permission** | PermissionId (Long) | `resource:action` 형식 |

---

## 3. Tenant Domain

### 3.1 Current MVP Implementation

```java
// Aggregate: Tenant
public final class Tenant {
    private final TenantId tenantId;
    private final TenantName tenantName;      // VO: 2-50자
    private final TenantStatus tenantStatus;  // ACTIVE, INACTIVE, DELETED
    private final Instant createdAt;
    private final Instant updatedAt;

    // Business Methods
    - forNew(TenantName, Clock) → Tenant
    - activate(Clock) → Tenant
    - deactivate(Clock) → Tenant
    - delete(Clock) → Tenant
    - updateName(TenantName, Clock) → Tenant
}
```

### 3.2 Current Application Layer

| UseCase | Status | Description |
|---------|--------|-------------|
| CreateTenantUseCase | ✅ MVP | 테넌트 생성 |
| GetTenantUseCase | ❌ 미구현 | 테넌트 조회 |
| UpdateTenantUseCase | ❌ 미구현 | 테넌트 수정 |
| DeactivateTenantUseCase | ❌ 미구현 | 테넌트 비활성화 |
| DeleteTenantUseCase | ❌ 미구현 | 테넌트 삭제 |

### 3.3 Missing Features (Future Enhancement)

| Feature | Priority | Description |
|---------|----------|-------------|
| **Subscription Plan** | HIGH | 플랜별 기능/사용량 제한 (FREE, BASIC, PRO, ENTERPRISE) |
| **Billing Info** | HIGH | 결제 정보, 청구 주기, 결제 수단 |
| **Usage Quota** | MEDIUM | API 호출 제한, 사용자 수 제한, 저장 용량 |
| **Custom Domain** | MEDIUM | 테넌트별 커스텀 도메인 (acme.authhub.io) |
| **Theme/Branding** | LOW | 로고, 색상, 이메일 템플릿 커스터마이징 |
| **Audit Settings** | MEDIUM | 감사 로그 보존 기간, 이벤트 설정 |
| **Security Policies** | HIGH | 비밀번호 정책, MFA 강제, IP 화이트리스트 |
| **SSO Configuration** | MEDIUM | SAML/OIDC IdP 연동 설정 |

### 3.4 Future Tenant Domain Model

```java
// Enhanced Tenant (Future)
public final class Tenant {
    // === Current ===
    private final TenantId tenantId;
    private final TenantName tenantName;
    private final TenantStatus tenantStatus;

    // === Future Enhancements ===
    private final SubscriptionPlan subscriptionPlan;  // FREE, BASIC, PRO, ENTERPRISE
    private final BillingInfo billingInfo;            // 결제 정보
    private final UsageQuota usageQuota;              // 사용량 제한
    private final SecurityPolicy securityPolicy;      // 보안 정책
    private final BrandingConfig brandingConfig;      // 브랜딩 설정
    private final List<CustomDomain> customDomains;   // 커스텀 도메인
    private final SsoConfiguration ssoConfig;         // SSO 설정
}

// Value Objects (Future)
public record SubscriptionPlan(
    PlanType type,           // FREE, BASIC, PRO, ENTERPRISE
    Instant startDate,
    Instant endDate,
    boolean autoRenew
) {}

public record UsageQuota(
    int maxUsers,
    int maxOrganizations,
    long maxApiCallsPerMonth,
    long maxStorageBytes
) {}

public record SecurityPolicy(
    PasswordPolicy passwordPolicy,
    boolean mfaRequired,
    int sessionTimeoutMinutes,
    Set<String> allowedIpRanges
) {}
```

---

## 4. Organization Domain

### 4.1 Current MVP Implementation

```java
// Aggregate: Organization
public final class Organization {
    private final OrganizationId organizationId;
    private final OrganizationName organizationName;  // VO: 2-100자
    private final TenantId tenantId;                  // Long FK
    private final OrganizationStatus organizationStatus;  // ACTIVE, INACTIVE, DELETED
    private final Instant createdAt;
    private final Instant updatedAt;

    // Business Methods
    - forNew(OrganizationName, TenantId, Clock) → Organization
    - activate(Clock) → Organization
    - deactivate(Clock) → Organization
    - delete(Clock) → Organization
    - updateName(OrganizationName, Clock) → Organization
}
```

### 4.2 Current Application Layer

| UseCase | Status | Description |
|---------|--------|-------------|
| CreateOrganizationUseCase | ✅ MVP | 조직 생성 |
| GetOrganizationUseCase | ❌ 미구현 | 조직 조회 |
| UpdateOrganizationUseCase | ❌ 미구현 | 조직 수정 |
| DeactivateOrganizationUseCase | ❌ 미구현 | 조직 비활성화 |
| DeleteOrganizationUseCase | ❌ 미구현 | 조직 삭제 (하위 User 검증) |

### 4.3 Missing Features (Future Enhancement)

| Feature | Priority | Description |
|---------|----------|-------------|
| **Hierarchy** | MEDIUM | 부모-자식 조직 계층 구조 |
| **Org Settings** | MEDIUM | 조직별 설정 (타임존, 언어, 알림) |
| **Member Limit** | LOW | 조직별 멤버 수 제한 |
| **Department Metadata** | LOW | 추가 메타데이터 (코스트센터, 위치 등) |
| **Default Role** | MEDIUM | 조직 가입 시 기본 역할 |
| **Invitation Flow** | HIGH | 조직 초대 및 가입 흐름 |
| **Org Admin Role** | HIGH | 조직 관리자 권한 체계 |

### 4.4 Future Organization Domain Model

```java
// Enhanced Organization (Future)
public final class Organization {
    // === Current ===
    private final OrganizationId organizationId;
    private final OrganizationName organizationName;
    private final TenantId tenantId;
    private final OrganizationStatus organizationStatus;

    // === Future Enhancements ===
    private final OrganizationId parentOrganizationId;  // null if root
    private final OrganizationSettings settings;
    private final RoleId defaultRoleId;                 // 신규 멤버 기본 역할
    private final int memberLimit;
    private final Map<String, String> metadata;         // 유연한 메타데이터
}

public record OrganizationSettings(
    String timezone,
    String locale,
    NotificationPreferences notifications
) {}

// 조직 계층 탐색 Domain Service
public interface OrganizationHierarchyService {
    List<Organization> getAncestors(OrganizationId orgId);
    List<Organization> getDescendants(OrganizationId orgId);
    boolean isAncestor(OrganizationId potentialAncestor, OrganizationId orgId);
}
```

---

## 5. User Domain

### 5.1 Current MVP Implementation

```java
// Aggregate: User
public final class User {
    private final UserId userId;                    // UUID
    private final TenantId tenantId;                // Long FK (필수)
    private final OrganizationId organizationId;   // Long FK (INTERNAL 필수, PUBLIC null)
    private final UserType userType;               // PUBLIC, INTERNAL
    private final UserStatus userStatus;           // ACTIVE, INACTIVE, SUSPENDED, DELETED
    private final Credential credential;           // Email + Password (VO)
    private final UserProfile profile;             // Name, Phone, Timezone, Locale (VO)
    private final Instant createdAt;
    private final Instant updatedAt;

    // Business Methods
    - forNew(...) → User
    - activate(Clock) → User
    - deactivate(Clock) → User
    - suspend(Clock) → User
    - delete(Clock) → User
    - assignOrganization(OrganizationId, Clock) → User
    - changePassword(Password, Clock) → User
    - updateProfile(UserProfile, Clock) → User
}

// Value Objects
public record Credential(Email email, Password password) {
    Credential withPassword(Password newPassword);
}

public record UserProfile(
    String name,
    PhoneNumber phoneNumber,
    String timezone,
    String locale
) {
    UserProfile mergeWith(UserProfile other);
}
```

### 5.2 Current Application Layer

| UseCase | Status | Description |
|---------|--------|-------------|
| CreateUserUseCase | ✅ MVP | 사용자 생성 |
| UpdateUserUseCase | ✅ MVP | 프로필 수정 |
| GetUserUseCase | ✅ MVP | 사용자 조회 |
| ChangePasswordUseCase | ✅ MVP | 비밀번호 변경 |
| ChangeUserStatusUseCase | ✅ MVP | 상태 변경 (activate/suspend/delete) |
| SearchUsersUseCase | ❌ 미구현 | 사용자 검색 (페이징) |
| DeleteUserUseCase | ❌ 미구현 | 사용자 삭제 (Soft Delete) |

### 5.3 Missing Features (Future Enhancement)

| Feature | Priority | Description |
|---------|----------|-------------|
| **Social Accounts** | HIGH | 소셜 로그인 연동 (Kakao, Google, Apple) |
| **Account Linking** | HIGH | 전화번호 기준 계정 통합 |
| **MFA** | HIGH | 2FA (TOTP, SMS, Email) |
| **Login History** | MEDIUM | 로그인 이력, 기기 정보 |
| **Password History** | MEDIUM | 비밀번호 재사용 방지 |
| **Avatar/Profile Image** | LOW | 프로필 이미지 |
| **User Preferences** | LOW | 개인 설정 (알림, UI 테마) |
| **API Keys** | MEDIUM | 서비스 계정용 API 키 |
| **Recovery Options** | HIGH | 비밀번호 재설정, 계정 복구 |

### 5.4 Future User Domain Model

```java
// Enhanced User (Future)
public final class User {
    // === Current ===
    private final UserId userId;
    private final TenantId tenantId;
    private final OrganizationId organizationId;
    private final UserType userType;
    private final UserStatus userStatus;
    private final Credential credential;
    private final UserProfile profile;

    // === Future Enhancements ===
    private final Set<SocialAccount> socialAccounts;      // 소셜 연동
    private final MfaSettings mfaSettings;                // MFA 설정
    private final RecoveryOptions recoveryOptions;        // 복구 옵션
    private final UserPreferences preferences;            // 개인 설정
    private final Instant lastLoginAt;                    // 마지막 로그인
    private final Instant passwordChangedAt;              // 비밀번호 변경일
    private final int failedLoginAttempts;                // 로그인 실패 횟수
    private final Instant lockedUntil;                    // 계정 잠금 해제 시간
}

// SocialAccount Value Object
public record SocialAccount(
    SocialProvider provider,  // KAKAO, GOOGLE, APPLE
    String providerId,
    String email,
    Instant linkedAt
) {}

// MFA Settings
public record MfaSettings(
    boolean enabled,
    MfaMethod preferredMethod,  // TOTP, SMS, EMAIL
    String totpSecret,
    PhoneNumber smsPhoneNumber,
    Email backupEmail
) {}

// Account Linking Rule
// - 전화번호 기준 통합
// - 소셜 → 전화번호 통합만 가능 (역방향 불가)
// - 통합 시 소셜 계정 정보 SocialAccount로 이관
```

---

## 6. Role & Permission Domain

### 6.1 Current MVP Implementation

```java
// Aggregate: Role
public final class Role {
    private final RoleId roleId;
    private final TenantId tenantId;              // null = Global/System Role
    private final RoleName name;                  // "ROLE_" prefix 필수
    private final String description;
    private final boolean isSystem;               // true = 수정/삭제 불가
    private final Set<PermissionCode> permissions;

    // Business Methods
    - addPermission(PermissionCode) → Role
    - removePermission(PermissionCode) → Role
    - hasPermission(PermissionCode) → boolean (와일드카드 지원)
}

// Entity: Permission
public final class Permission {
    private final PermissionId permissionId;
    private final PermissionCode code;            // "resource:action" 형식
    private final String description;
}

// Value Objects
public record PermissionCode(String value) {
    // Format: "resource:action"
    // Wildcard: "resource:*"
    boolean implies(PermissionCode other);  // 와일드카드 매칭
}

public record RoleName(String value) {
    // Must start with "ROLE_" (Spring Security 호환)
    String nameWithoutPrefix();
}

// Association: UserRole
public final class UserRole {
    private final Long id;
    private final UserId userId;
    private final RoleId roleId;
    private final Instant assignedAt;
}
```

### 6.2 Current Application Layer

| UseCase | Status | Description |
|---------|--------|-------------|
| GetUserRolesUseCase | ✅ MVP | 사용자 역할/권한 조회 (JWT용) |
| AssignRoleToUserUseCase | ❌ 미구현 | 역할 할당 |
| RevokeRoleFromUserUseCase | ❌ 미구현 | 역할 해제 |
| CreateRoleUseCase | ❌ 미구현 | 역할 생성 |
| UpdateRoleUseCase | ❌ 미구현 | 역할 수정 (권한 추가/제거) |
| DeleteRoleUseCase | ❌ 미구현 | 역할 삭제 |
| GetRolePermissionsUseCase | ❌ 미구현 | 역할별 권한 조회 |

### 6.3 Permission Code Convention

```
Format: {resource}:{action}

Resources (예시):
- user, organization, tenant, role, permission
- product, order, payment, report
- audit, settings, billing

Actions (표준):
- read, write, create, update, delete
- manage (= create + update + delete)
- * (와일드카드 = 모든 액션)

Examples:
- user:read      → 사용자 조회
- user:write     → 사용자 생성/수정
- user:*         → 사용자 전체 권한
- organization:manage → 조직 관리 권한
- *:*            → 슈퍼 관리자 (모든 권한)
```

### 6.4 Default System Roles

| Role | Scope | Permissions | Description |
|------|-------|-------------|-------------|
| ROLE_SUPER_ADMIN | Global | `*:*` | 플랫폼 전체 관리자 |
| ROLE_TENANT_ADMIN | Tenant | `tenant:*, organization:*, user:*, role:*` | 테넌트 관리자 |
| ROLE_ORG_ADMIN | Organization | `organization:read, user:manage, role:read` | 조직 관리자 |
| ROLE_USER | Organization | `user:read(self)` | 일반 사용자 |

### 6.5 Missing Features (Future Enhancement)

| Feature | Priority | Description |
|---------|----------|-------------|
| **Role Hierarchy** | MEDIUM | 역할 상속 (ADMIN은 USER 권한 포함) |
| **Scope/Context** | HIGH | 권한 범위 (self, organization, tenant) |
| **Resource-level Permissions** | HIGH | 특정 리소스 인스턴스에 대한 권한 |
| **Permission Groups** | MEDIUM | 권한 그룹 (권한 템플릿) |
| **Temporary Roles** | LOW | 시간 제한 역할 (만료 시간) |
| **Role Constraints** | MEDIUM | 역할 제약 (상호 배타적 역할) |
| **Delegation** | LOW | 권한 위임 |

### 6.6 Future Role & Permission Model

```java
// Enhanced Permission (Future)
public final class Permission {
    private final PermissionId permissionId;
    private final PermissionCode code;
    private final String description;
    private final PermissionScope scope;          // SELF, ORGANIZATION, TENANT, GLOBAL
    private final Set<String> conditions;         // 조건부 권한
}

public enum PermissionScope {
    SELF,           // 자신의 리소스만
    ORGANIZATION,   // 같은 조직 내
    TENANT,         // 같은 테넌트 내
    GLOBAL          // 전체
}

// Enhanced Role (Future)
public final class Role {
    private final RoleId roleId;
    private final TenantId tenantId;
    private final RoleName name;
    private final String description;
    private final boolean isSystem;
    private final Set<PermissionCode> permissions;

    // === Future Enhancements ===
    private final RoleId parentRoleId;            // 역할 상속
    private final Instant expiresAt;              // 임시 역할
    private final Set<RoleId> mutuallyExclusive; // 상호 배타 역할
    private final int priority;                   // 우선순위 (충돌 해결)
}

// Resource-level Permission (Future)
public record ResourcePermission(
    PermissionCode permission,
    String resourceType,                          // "organization", "project"
    String resourceId,                            // 특정 리소스 ID
    PermissionScope scope
) {}
```

---

## 7. Auth Domain

### 7.1 Current MVP Implementation

```java
// Exception Classes
public class InvalidCredentialsException extends AuthException {}
public class InvalidRefreshTokenException extends AuthException {}
```

### 7.2 Current Application Layer

| UseCase | Status | Description |
|---------|--------|-------------|
| LoginUseCase | ✅ MVP | 로그인 (JWT 발급) |
| RefreshTokenUseCase | ✅ MVP | 토큰 갱신 (RTR 적용) |
| LogoutUseCase | ✅ MVP | 로그아웃 (RefreshToken 무효화) |
| ValidateTokenUseCase | ❌ 미구현 | 토큰 검증 (Gateway용) |
| RevokeAllTokensUseCase | ❌ 미구현 | 모든 토큰 무효화 |

### 7.3 Token Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                        Access Token                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Algorithm: RS256                                         │  │
│  │  Expiry: 30 minutes                                       │  │
│  │                                                           │  │
│  │  Payload:                                                 │  │
│  │  {                                                        │  │
│  │    "sub": "userId (UUID)",                                │  │
│  │    "tenantId": 1,                                         │  │
│  │    "userType": "INTERNAL",                                │  │
│  │    "organizationIds": [1, 2],                             │  │
│  │    "roles": ["ROLE_ADMIN", "ROLE_USER"],                  │  │
│  │    "permissions": ["user:read", "user:write", "org:*"],   │  │
│  │    "iat": 1234567890,                                     │  │
│  │    "exp": 1234569690                                      │  │
│  │  }                                                        │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Refresh Token                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Storage: MySQL (persistent) + Redis (cache)              │  │
│  │  Expiry: 14 days                                          │  │
│  │  Strategy: Rotation (RTR - Refresh Token Rotation)        │  │
│  │                                                           │  │
│  │  - 갱신 시 기존 토큰 무효화 + 새 토큰 발급                   │  │
│  │  - 탈취 감지: 무효화된 토큰 재사용 시 전체 세션 종료          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      Gateway Flow                                │
│                                                                  │
│  Client ──→ Gateway ──→ Microservice                            │
│              │                                                   │
│              ├─ 1. Public Key로 JWT 검증 (자체 검증)              │
│              ├─ 2. Access Token claims에서 permissions 추출      │
│              ├─ 3. 엔드포인트 필요 권한과 매칭                    │
│              ├─ 4. 만료 시 Refresh Token으로 자동 갱신           │
│              └─ 5. X-User-Id, X-Tenant-Id 헤더 전달              │
└─────────────────────────────────────────────────────────────────┘
```

### 7.4 Missing Features (Future Enhancement)

| Feature | Priority | Description |
|---------|----------|-------------|
| **Social Login** | HIGH | Kakao, Google, Apple OAuth2 |
| **MFA** | HIGH | TOTP, SMS OTP, Email OTP |
| **Password Reset** | HIGH | 이메일/SMS 기반 비밀번호 재설정 |
| **Session Management** | MEDIUM | 활성 세션 목록, 원격 로그아웃 |
| **Login Audit** | MEDIUM | 로그인 이력, IP, 기기 정보 |
| **Brute Force Protection** | HIGH | 로그인 시도 제한, CAPTCHA |
| **Device Trust** | MEDIUM | 신뢰할 수 있는 기기 관리 |
| **Magic Link** | LOW | 이메일 기반 비밀번호 없는 로그인 |
| **WebAuthn/Passkey** | LOW | 생체 인증, 하드웨어 키 |

### 7.5 Future Auth Domain Model

```java
// AuthSession Aggregate (Future)
public final class AuthSession {
    private final SessionId sessionId;
    private final UserId userId;
    private final DeviceInfo deviceInfo;
    private final IpAddress ipAddress;
    private final RefreshToken refreshToken;
    private final Instant createdAt;
    private final Instant lastActivityAt;
    private final Instant expiresAt;
    private final SessionStatus status;
}

public record DeviceInfo(
    String deviceId,
    String userAgent,
    String os,
    String browser,
    boolean trusted
) {}

// Login Audit (Future)
public final class LoginAudit {
    private final LoginAuditId id;
    private final UserId userId;
    private final LoginResult result;    // SUCCESS, FAILED_PASSWORD, FAILED_MFA, BLOCKED
    private final IpAddress ipAddress;
    private final DeviceInfo deviceInfo;
    private final Instant attemptedAt;
    private final String failureReason;
}

// MFA Challenge (Future)
public final class MfaChallenge {
    private final MfaChallengeId id;
    private final UserId userId;
    private final MfaMethod method;       // TOTP, SMS, EMAIL
    private final String challengeCode;
    private final Instant expiresAt;
    private final int attempts;
    private final boolean verified;
}
```

---

## 8. Cross-Cutting Concerns

### 8.1 Audit Logging

```java
// Audit Event (Future)
public record AuditEvent(
    AuditEventId id,
    TenantId tenantId,
    UserId actorId,
    String action,                        // CREATE_USER, UPDATE_ROLE, LOGIN
    String resourceType,                  // USER, ORGANIZATION, ROLE
    String resourceId,
    Map<String, Object> previousState,
    Map<String, Object> newState,
    IpAddress ipAddress,
    String userAgent,
    TraceId traceId,
    Instant occurredAt
) {}
```

### 8.2 Event-Driven Architecture

```java
// Domain Events (Future)
public record UserCreatedEvent(UserId userId, TenantId tenantId, Instant occurredAt) {}
public record UserRoleChangedEvent(UserId userId, Set<RoleId> previousRoles, Set<RoleId> newRoles) {}
public record LoginSuccessEvent(UserId userId, DeviceInfo deviceInfo, IpAddress ipAddress) {}
public record SuspiciousActivityEvent(UserId userId, String activityType, String details) {}
```

### 8.3 Notification Integration

```java
// Notification Triggers (Future)
- 신규 가입 환영 이메일
- 비밀번호 변경 알림
- 새 기기 로그인 알림
- 비정상 로그인 시도 경고
- 역할 변경 알림
- 계정 잠금 알림
```

---

## 9. Implementation Status

### 9.1 Domain Layer

| Domain | Aggregate | VOs | Exceptions | Tests | Status |
|--------|-----------|-----|------------|-------|--------|
| Tenant | ✅ | TenantName, TenantStatus | ✅ 3개 | ✅ | **MVP** |
| Organization | ✅ | OrganizationName, OrganizationStatus | ✅ 3개 | ✅ | **MVP** |
| User | ✅ | Email, Password, Credential, UserProfile, UserType, UserStatus, PhoneNumber | ✅ 4개 | ✅ | **MVP** |
| Role | ✅ | RoleName, PermissionCode | ✅ 기본 | ✅ | **MVP** |
| Permission | ✅ | PermissionCode | - | ✅ | **MVP** |
| UserRole | ✅ | - | - | ✅ | **MVP** |
| Auth | ❌ 없음 | - | InvalidCredentials, InvalidRefreshToken | - | **Partial** |

### 9.2 Application Layer

| Domain | UseCases 구현 | UseCases 미구현 | Ports | Assemblers | Status |
|--------|---------------|-----------------|-------|------------|--------|
| Tenant | 1 | 4 | ✅ | ✅ | **20%** |
| Organization | 1 | 4 | ✅ | ✅ | **20%** |
| User | 5 | 2 | ✅ | ✅ | **70%** |
| Role | 1 | 6 | ✅ | ❌ | **14%** |
| Auth | 3 | 2 | ✅ | ❌ | **60%** |

### 9.3 Adapter Layer (Out of Scope)

Persistence 및 Infrastructure Adapter는 별도 Phase에서 구현 예정.

---

## 10. Roadmap

### Phase 1: MVP Completion (Current)

**목표**: Core IAM 기능 완성

| Task | Priority | Status |
|------|----------|--------|
| 모든 Tenant/Organization CRUD UseCase | HIGH | ❌ |
| User Search/Delete UseCase | MEDIUM | ❌ |
| Role CRUD UseCase | HIGH | ❌ |
| Role Assignment UseCase | HIGH | ❌ |
| ValidateToken, RevokeAllTokens UseCase | MEDIUM | ❌ |

### Phase 2: Security Enhancement

**목표**: 보안 강화

| Task | Priority | Estimated |
|------|----------|-----------|
| MFA (TOTP) | HIGH | 2 weeks |
| Password Policy | HIGH | 1 week |
| Login Audit | MEDIUM | 1 week |
| Brute Force Protection | HIGH | 1 week |
| Session Management | MEDIUM | 1 week |

### Phase 3: Social & SSO

**목표**: 소셜 로그인 및 SSO 지원

| Task | Priority | Estimated |
|------|----------|-----------|
| Kakao OAuth2 | HIGH | 2 weeks |
| Google OAuth2 | MEDIUM | 1 week |
| Apple OAuth2 | MEDIUM | 1 week |
| Account Linking | HIGH | 2 weeks |
| SAML Integration | LOW | 3 weeks |

### Phase 4: Enterprise Features

**목표**: 엔터프라이즈 기능

| Task | Priority | Estimated |
|------|----------|-----------|
| Subscription Plans | MEDIUM | 2 weeks |
| Usage Quota | MEDIUM | 1 week |
| Custom Domain | LOW | 2 weeks |
| Branding | LOW | 1 week |
| Advanced Audit | MEDIUM | 2 weeks |

### Phase 5: Advanced RBAC

**목표**: 고급 권한 관리

| Task | Priority | Estimated |
|------|----------|-----------|
| Role Hierarchy | MEDIUM | 2 weeks |
| Resource-level Permissions | HIGH | 3 weeks |
| Permission Scopes | HIGH | 2 weeks |
| Delegation | LOW | 2 weeks |

---

## Appendix

### A. Zero-Tolerance Rules

1. **Lombok 금지** - Plain Java 사용 (특히 Domain Layer)
2. **Law of Demeter** - `user.getCredential().getEmail().getValue()` ❌
3. **Tell, Don't Ask** - 상태 질의 대신 행위 위임
4. **Long FK 전략** - JPA 관계 어노테이션 금지, ID만 저장
5. **Transaction 경계** - `@Transactional` 내 외부 API 호출 금지
6. **불변 객체** - 상태 변경 시 새 객체 반환

### B. Naming Conventions

```
Domain Layer:
- Aggregate: {Name}.java (User.java)
- Value Object: {Name}.java (Email.java)
- Identifier: {Name}Id.java (UserId.java)
- Exception: {Name}Exception.java (UserNotFoundException.java)

Application Layer:
- UseCase: {Action}{Entity}UseCase.java (CreateUserUseCase.java)
- Service: {Action}{Entity}Service.java (CreateUserService.java)
- Port: {Entity}{Type}Port.java (UserPersistencePort.java)
- Command DTO: {Action}{Entity}Command.java (CreateUserCommand.java)
- Response DTO: {Entity}Response.java (UserResponse.java)
- Assembler: {Entity}Assembler.java (UserAssembler.java)
```

### C. References

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design (Eric Evans)](https://domainlanguage.com/ddd/)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [JWT RFC 7519](https://tools.ietf.org/html/rfc7519)
- [Spring Security RBAC](https://docs.spring.io/spring-security/)

---

**Document Version History**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0.0-MVP | 2025-12-03 | Claude | Initial MVP specification |
