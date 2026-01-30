# Tenant 비즈니스 룰

> **도메인 성숙도**: 초기 단계 (Basic)
> **Last Updated**: 2025-01-27

## 1. 개요

Tenant는 Multi-Tenant 시스템의 최상위 격리 단위입니다.
현재는 기본적인 CRUD와 상태 관리만 구현되어 있으며,
시스템이 성숙해짐에 따라 비즈니스 룰이 점차 확장될 예정입니다.

```
┌─────────────────────────────────────────┐
│                 Tenant                  │
│         (최상위 격리 단위)              │
├─────────────────────────────────────────┤
│  Organization  Organization  ...        │
│      │              │                   │
│    User           User                  │
└─────────────────────────────────────────┘
```

---

## 2. 현재 구현된 비즈니스 룰

### 2.1 테넌트 생성

| 규칙 | 설명 | 상태 |
|------|------|------|
| 이름 필수 | TenantName은 null/empty 불가 | ✅ 구현됨 |
| 이름 유일성 | 테넌트 이름은 시스템 전체에서 unique | ✅ 구현됨 |
| 초기 상태 | 생성 시 ACTIVE 상태로 시작 | ✅ 구현됨 |
| ID 생성 | UUIDv7 사용 (시간순 정렬 가능) | ✅ 구현됨 |

### 2.2 상태 전이 규칙

```
        ┌──────────┐
        │  ACTIVE  │◄─────────┐
        └────┬─────┘          │
             │                │
             ▼                │
        ┌──────────┐          │
        │ INACTIVE │──────────┘
        └────┬─────┘
             │
             ▼
        ┌──────────┐
        │ DELETED  │ (Soft Delete, 복구 불가)
        └──────────┘
```

| 전이 | 허용 여부 | 설명 |
|------|----------|------|
| ACTIVE → INACTIVE | ✅ | 테넌트 일시 중지 |
| INACTIVE → ACTIVE | ✅ | 테넌트 재활성화 |
| ACTIVE → DELETED | ❌ | 직접 삭제 불가, INACTIVE 경유 필요 |
| INACTIVE → DELETED | ✅ | 소프트 삭제 |
| DELETED → * | ❌ | 삭제된 테넌트 복구 불가 |

### 2.3 테넌트 수정

| 필드 | 수정 가능 | 조건 |
|------|----------|------|
| name | ✅ | DELETED 상태가 아닐 때 |
| status | ✅ | 상태 전이 규칙 준수 |
| tenantId | ❌ | PK, 변경 불가 |

---

## 3. API 엔드포인트 설계

### 3.1 Command API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/api/v1/tenants` | 테넌트 생성 | Super Admin |
| PUT | `/api/v1/tenants/{id}` | 테넌트 수정 (name 등) | Super Admin |
| PATCH | `/api/v1/tenants/{id}/status` | 상태 변경 | Super Admin |

**참고**: 삭제는 별도 엔드포인트 없이 상태 변경(`status: DELETED`)으로 처리

### 3.2 Query API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/api/v1/tenants` | 테넌트 목록 조회 (복합 조건) | Super Admin |

**복합 조회 조건**:

```yaml
SearchTenantsQuery:
  name: String (LIKE 검색)
  status: TenantStatus (필터)
  createdFrom: LocalDateTime (범위)
  createdTo: LocalDateTime (범위)
  sortBy: TenantSortKey (NAME, CREATED_AT, STATUS)
  sortDirection: ASC | DESC
  page: int
  size: int
```

**참고**: 단건 조회는 현재 비즈니스 가치가 낮아 목록 조회로 대체

---

## 4. 미래 확장 계획 (Roadmap)

> 아래 기능들은 시스템 성숙도에 따라 점진적으로 구현될 예정입니다.

### 4.1 Phase 2: 리소스 제한 (Quota)

```java
// 예상 필드
private int maxOrganizations;       // 최대 조직 수
private int maxUsersPerOrganization; // 조직당 최대 사용자
private int maxTotalUsers;          // 테넌트 총 사용자 수
```

| 규칙 | 설명 |
|------|------|
| 조직 생성 제한 | maxOrganizations 초과 시 생성 불가 |
| 사용자 등록 제한 | maxTotalUsers 초과 시 등록 불가 |

### 4.2 Phase 3: 보안 정책 (Security Policy)

```java
// 예상 필드
private PasswordPolicy passwordPolicy;
private int sessionTimeoutMinutes;
private boolean mfaRequired;
private List<String> allowedIpRanges;
```

| 규칙 | 설명 |
|------|------|
| 비밀번호 정책 적용 | 테넌트별 비밀번호 복잡도 강제 |
| MFA 강제 | mfaRequired=true 시 2FA 필수 |
| IP 제한 | allowedIpRanges 외 접근 차단 |

### 4.3 Phase 4: 구독/플랜 (Subscription)

```java
// 예상 필드
private TenantPlan plan;              // FREE, PRO, ENTERPRISE
private Instant subscriptionExpiry;   // 구독 만료일
private Set<Feature> enabledFeatures; // 활성화된 기능
```

| 규칙 | 설명 |
|------|------|
| 플랜별 기능 제한 | FREE 플랜은 일부 기능 비활성화 |
| 구독 만료 처리 | 만료 시 INACTIVE로 자동 전환 |

### 4.4 Phase 5: Cascade 규칙

| 상황 | 동작 |
|------|------|
| 테넌트 INACTIVE | 하위 조직/사용자 로그인 차단 |
| 테넌트 DELETED | 하위 데이터 보관 정책 적용 |

---

## 5. 검증 규칙 (Validation)

### 5.1 TenantName 규칙

| 규칙 | 값 |
|------|-----|
| 최소 길이 | 2자 |
| 최대 길이 | 100자 |
| 허용 문자 | 한글, 영문, 숫자, 공백, `-`, `_` |
| 금지 | 특수문자, 앞뒤 공백 |

### 5.2 중복 검사

- 테넌트 이름은 시스템 전체에서 unique (대소문자 구분)
- 삭제된 테넌트의 이름은 재사용 가능 (soft delete이므로 주의 필요)

---

## 6. 도메인 이벤트 (Future)

> 현재 미구현, Event-Driven 아키텍처 도입 시 추가 예정

| 이벤트 | 트리거 | 후속 처리 |
|--------|--------|----------|
| TenantCreated | 테넌트 생성 완료 | 기본 조직 자동 생성? |
| TenantStatusChanged | 상태 변경 | 하위 엔티티 상태 동기화 |
| TenantDeleted | 소프트 삭제 | 데이터 보관/정리 스케줄링 |

---

## 7. 참고 사항

### 7.1 현재 제약사항

- 테넌트는 **Super Admin만** 관리 가능
- 테넌트 간 데이터 완전 격리 (Cross-tenant 조회 불가)
- 테넌트 이름 변경 시 하위 엔티티 영향 없음 (ID 기반 참조)

### 7.2 관련 문서

- [Data Model](../architecture/data-model.md)
- [RBAC Gateway Architecture](../architecture/rbac-gateway.md)
