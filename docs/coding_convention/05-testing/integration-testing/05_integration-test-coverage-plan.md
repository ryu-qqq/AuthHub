# Integration Test Coverage Plan

## 개요

이 문서는 AuthHub 프로젝트의 통합 테스트 커버리지 확장 계획을 정의합니다.

**작성일**: 2025-12-25
**현재 상태**: 45개 테스트 통과 / 44개 엔드포인트

---

## 현재 커버리지 현황

### 기존 테스트 파일

| 파일명 | 테스트 수 | 커버 엔드포인트 |
|--------|----------|----------------|
| TenantCrudIntegrationTest | 7 | POST, GET/{id}, PUT/name, PATCH/status |
| OrganizationCrudIntegrationTest | 6 | POST, GET/{id}, PUT, PATCH/status |
| UserCrudIntegrationTest | 7 | POST, GET/{id}, PATCH/status |
| RoleCrudIntegrationTest | 7 | POST, GET/{id}, PUT, POST/permissions |
| PermissionCrudIntegrationTest | 8 | POST, GET/{id}, PUT |
| AuthFlowIntegrationTest | 10 | POST/login, POST/refresh, POST/logout |

---

## Phase 1: UserRole 통합 테스트 (우선순위: 높음)

### 신규 파일: `UserRoleIntegrationTest.java`

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-001 | 사용자에게 역할 부여 - 성공 | POST | `/users/{userId}/roles` | 201 CREATED |
| TC-002 | 사용자에게 역할 부여 - 존재하지 않는 사용자 | POST | `/users/{userId}/roles` | 404 NOT_FOUND |
| TC-003 | 사용자에게 역할 부여 - 존재하지 않는 역할 | POST | `/users/{userId}/roles` | 404 NOT_FOUND |
| TC-004 | 사용자 역할 회수 - 성공 | PATCH | `/users/{userId}/roles/{roleId}/revoke` | 200 OK |
| TC-005 | 사용자 역할 회수 - 미부여된 역할 | PATCH | `/users/{userId}/roles/{roleId}/revoke` | 404 NOT_FOUND |

### Fixture 요구사항: `UserRoleIntegrationTestFixture.java`
```java
- assignRoleRequest(UUID roleId)
- assignRoleRequestWithInvalidRole()
```

---

## Phase 2: 소프트 삭제 테스트 (우선순위: 높음)

### TenantCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-008 | 테넌트 삭제 - 성공 | PATCH | `/tenants/{id}/delete` | 200 OK |
| TC-009 | 삭제된 테넌트 조회 - 404 | GET | `/tenants/{id}` | 404 NOT_FOUND |

### OrganizationCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-007 | 조직 삭제 - 성공 | PATCH | `/organizations/{id}/delete` | 200 OK |
| TC-008 | 삭제된 조직 조회 - 404 | GET | `/organizations/{id}` | 404 NOT_FOUND |

### UserCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-008 | 사용자 삭제 - 성공 | PATCH | `/users/{id}/delete` | 200 OK |
| TC-009 | 삭제된 사용자 조회 - 404 | GET | `/users/{id}` | 404 NOT_FOUND |

### RoleCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-008 | 역할 삭제 - 성공 | PATCH | `/roles/{id}/delete` | 200 OK |
| TC-009 | 삭제된 역할 조회 - 404 | GET | `/roles/{id}` | 404 NOT_FOUND |

### PermissionCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-009 | 권한 삭제 - 성공 | PATCH | `/permissions/{id}/delete` | 200 OK |
| TC-010 | 삭제된 권한 조회 - 404 | GET | `/permissions/{id}` | 404 NOT_FOUND |

---

## Phase 3: 목록 조회 테스트 (우선순위: 중간)

### TenantCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-010 | 테넌트 목록 조회 - 성공 | GET | `/tenants` | 200 OK + List |

### OrganizationCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-009 | 조직 목록 조회 - 성공 | GET | `/organizations` | 200 OK + List |
| TC-010 | 조직별 사용자 조회 - 성공 | GET | `/organizations/{id}/users` | 200 OK + List |

### UserCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-010 | 사용자 목록 조회 - 성공 | GET | `/users` | 200 OK + List |

### RoleCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-010 | 역할 목록 조회 - 성공 | GET | `/roles` | 200 OK + List |
| TC-011 | 역할별 사용자 조회 - 성공 | GET | `/roles/{id}/users` | 200 OK + List |

### PermissionCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-011 | 권한 목록 조회 - 성공 | GET | `/permissions` | 200 OK + List |
| TC-012 | 사용자별 권한 조회 - 성공 | GET | `/permissions/users/{userId}` | 200 OK + List |

---

## Phase 4: 사용자 추가 기능 테스트 (우선순위: 중간)

### UserCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-011 | 사용자 정보 수정 - 성공 | PUT | `/users/{id}` | 200 OK |
| TC-012 | 현재 로그인 사용자 조회 - 성공 | GET | `/users/me` | 200 OK |
| TC-013 | 비밀번호 변경 - 성공 | PATCH | `/users/{id}/password` | 200 OK |
| TC-014 | 비밀번호 변경 - 현재 비밀번호 불일치 | PATCH | `/users/{id}/password` | 400 BAD_REQUEST |

### Fixture 추가 요구사항: `UserIntegrationTestFixture.java`
```java
- updateUserRequest(String nickname, ...)
- changePasswordRequest(String currentPassword, String newPassword)
- changePasswordRequestWithWrongCurrent()
```

---

## Phase 5: 권한 관계 고급 시나리오 (우선순위: 중간)

### RoleCrudIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-012 | 역할에서 권한 회수 - 성공 | PATCH | `/roles/{roleId}/permissions/{permissionId}/revoke` | 200 OK |
| TC-013 | 역할에서 권한 회수 - 미부여된 권한 | PATCH | `/roles/{roleId}/permissions/{permissionId}/revoke` | 404 NOT_FOUND |

---

## Phase 6: 기타 엔드포인트 (우선순위: 낮음)

### AuthFlowIntegrationTest 추가

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-011 | JWKS 조회 - 성공 | GET | `/auth/jwks` | 200 OK |

### 신규 파일: `InternalPermissionIntegrationTest.java` (Optional)

| TC-ID | 테스트명 | HTTP Method | Endpoint | 예상 결과 |
|-------|---------|-------------|----------|----------|
| TC-001 | 권한 유효성 검증 - 성공 | POST | `/internal/validate` | 200 OK |
| TC-002 | 권한 사용 기록 - 성공 | POST | `/{permissionKey}/usages` | 201 CREATED |

---

## 구현 순서

```
1. UserRoleIntegrationTest.java (신규) ─────────────────────> +5 tests
2. 소프트 삭제 테스트 (5개 도메인) ────────────────────────> +10 tests
3. 목록 조회 테스트 (5개 도메인 + 관계 조회) ─────────────> +8 tests
4. 사용자 추가 기능 테스트 ─────────────────────────────────> +4 tests
5. 권한 관계 고급 시나리오 ─────────────────────────────────> +2 tests
6. JWKS + Internal API (Optional) ─────────────────────────> +3 tests
                                                    ────────────────
                                                    Total: +32 tests
```

---

## 예상 최종 결과

| 항목 | Before | After |
|------|--------|-------|
| 총 테스트 수 | 45 | 77+ |
| 엔드포인트 커버리지 | ~60% | ~95% |
| 누락 시나리오 | 20+ | 2-3 |

---

## 참고: Fixture 파일 위치

```
integration-test/src/test/java/com/ryuqq/authhub/integration/
├── auth/fixture/AuthIntegrationTestFixture.java
├── organization/fixture/OrganizationIntegrationTestFixture.java
├── permission/fixture/PermissionIntegrationTestFixture.java
├── role/fixture/RoleIntegrationTestFixture.java
├── tenant/fixture/TenantIntegrationTestFixture.java
├── user/fixture/UserIntegrationTestFixture.java
└── userrole/fixture/UserRoleIntegrationTestFixture.java (신규)
```
