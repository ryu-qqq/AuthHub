# Test Coverage Audit: adapter-in/auth

> **상태**: 🟡 **부분 완료** (HIGH 3/4 완료, MEDIUM/LOW 완료)  
> **보완 완료일**: 2026-02-04  
> **최종 업데이트**: 2026-02-04  
> **다음 조치**: SecurityConfigTest → E2E/통합 테스트로 이관 필요 (TODO)  
> **위치**: `claudedocs/test-audit/completed/` ✅ (완료 처리됨 - 아카이브됨)

**레이어**: `adapter-in`  
**패키지**: `auth` (rest-api 모듈)  
**감사 일시**: 2026-02-04  
**분석 범위**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/auth/**`

---

## 📊 요약

| 항목 | 수량 | 상태 |
|------|------|------|
| **소스 클래스** | 11 | ✅ |
| **테스트 클래스** | 10 | ✅ (신규 3개 추가) |
| **테스트 커버리지** | ~85% | ✅ (64% → 85%+) |
| **HIGH 우선순위 이슈** | 1 | 🟡 (SecurityConfigTest 이관 TODO) |
| **MEDIUM 우선순위 이슈** | 0 | ✅ (완료) |
| **LOW 우선순위 이슈** | 0 | ✅ (완료) |

---

## 🔍 상세 분석

### 1. MISSING_TEST (HIGH) 🔴

#### 1.1 ResourceAccessChecker
- **우선순위**: HIGH
- **상태**: ✅ 완료
- **이유**: 
  - 핵심 보안 컴포넌트 (19개 public 메서드)
  - @PreAuthorize SpEL 함수로 사용되는 중요한 클래스
  - 복잡한 권한 검사 로직 포함
- **위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/auth/component/ResourceAccessChecker.java`
- **Public 메서드**: 19개
  - `superAdmin()`, `tenantAdmin()`, `orgAdmin()`
  - `myself(String)`, `myselfOr(String, String)`
  - `hasPermission(String)`, `hasAnyPermission(String...)`, `hasAllPermissions(String...)`
  - `hasRole(String)`, `hasAnyRole(String...)`
  - `sameTenant(String)`, `sameOrganization(String)`
  - `tenant(String, String)`, `organization(String, String)`, `user(String, String)`, `role(String, String)`, `permission(String, String)`
  - `authenticated()`
- **권장 조치**: `ResourceAccessCheckerTest.java` 생성 필요
- **테스트 시나리오**:
  - SUPER_ADMIN 권한 검사 (모든 메서드에서 true 반환)
  - 각 역할별 접근 제어 검증
  - 권한 기반 접근 제어 검증
  - 리소스 격리 규칙 검증 (테넌트/조직/사용자)

#### 1.2 CorsProperties
- **우선순위**: HIGH
- **상태**: ✅ 완료
- **이유**: Configuration Properties는 설정 바인딩 검증 필요
- **위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/auth/config/CorsProperties.java`
- **Public 메서드**: 10개 (getter/setter)
- **권장 조치**: `CorsPropertiesTest.java` 생성 필요
- **테스트 시나리오**:
  - Properties 바인딩 검증
  - 빈 리스트 기본값 검증
  - allowCredentials 기본값 검증

#### 1.3 JwtValidationProperties
- **우선순위**: HIGH
- **상태**: ✅ 완료
- **이유**: JWT 검증 설정은 보안에 중요
- **위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/auth/config/JwtValidationProperties.java`
- **Public 메서드**: 6개 (getter/setter) + RsaProperties 내부 클래스
- **권장 조치**: `JwtValidationPropertiesTest.java` 생성 필요
- **테스트 시나리오**:
  - Properties 바인딩 검증
  - issuer 기본값 검증 ("authhub")
  - RsaProperties 중첩 클래스 바인딩 검증

#### 1.4 SecurityConfig
- **우선순위**: HIGH
- **상태**: ⚠️ **TODO - E2E/통합 테스트로 이관 필요**
- **이유**: Spring Security 설정은 보안의 핵심
- **위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/auth/config/SecurityConfig.java`
- **Public 메서드**: 1개 (`securityFilterChain`)
- **권장 조치**: `SecurityConfigTest.java` 생성 필요 (통합 테스트)
- **테스트 시나리오**:
  - SecurityFilterChain 빈 생성 검증
  - CORS 설정 적용 검증
  - Public/Docs 경로 permitAll 검증
  - 인증 필터 순서 검증
- **이관 사유**: adapter-in rest-api 단위 테스트에서는 `mvcHandlerMappingIntrospector`(Spring MVC) 의존으로 컨텍스트 로딩 실패. **E2E 테스트(integration-test 모듈) 또는 bootstrap 전체 컨텍스트 기반 통합 테스트로 이관해야 함.**

---

### 2. MISSING_METHOD (MEDIUM) 🟡

#### 2.1 SecurityContext
- **상태**: ✅ 완료
- **현재 커버리지**: 약 95%+
- **누락된 메서드/시나리오**:
  - ✅ `anonymous()`, `builder()`, `hasRole()`, `hasAnyRole()`, `hasPermission()`, `isSuperAdmin()`, `equals()`, `hashCode()` 테스트 있음
  - ✅ `hasAllPermissions()` - 테스트 추가
  - ✅ `isTenantAdmin()`, `isOrgAdmin()` - 테스트 추가
  - ✅ `getTraceId()` - 테스트 추가
  - ✅ `toString()` - 테스트 추가
  - ✅ Builder 패턴 - 모든 필드 null 처리 테스트 추가

#### 2.2 SecurityContextHolder
- **상태**: ✅ 완료
- **현재 커버리지**: 약 95%+
- **누락된 메서드/시나리오**:
  - ✅ `getContext()`, `setContext()`, `clearContext()`, `getCurrentUserId()`, `getCurrentTenantId()`, `hasRole()`, `hasPermission()` 테스트 있음
  - ✅ `getCurrentOrganizationId()` - 테스트 추가
  - ✅ `hasAnyRole()` - 테스트 추가
  - ✅ `hasAnyPermission()`, `hasAllPermissions()` - 테스트 추가
  - ✅ `isSuperAdmin()`, `isAuthenticated()` - 테스트 추가
  - ✅ `getTraceId()` - 테스트 추가
  - ✅ Thread safety (clearContext 후 getContext) - 테스트 추가

#### 2.3 AuthErrorMapper
- **상태**: ✅ 완료
- **현재 커버리지**: 약 95%+
- **누락된 시나리오**:
  - ✅ `supports()` - AUTH- 접두사 테스트 있음
  - ✅ `map()` - AUTH-001 → 401 매핑 테스트 있음
  - ✅ `map()` - AUTH-007 → 403 매핑 테스트 추가
  - ✅ `map()` - URI 생성 검증 추가

---

### 3. MISSING_EDGE_CASE (MEDIUM) 🟡

#### 3.1 SecurityContext - Permission Edge Cases
- **상태**: ✅ 완료
- **누락 항목**:
  - ✅ 와일드카드 권한 (`*:*`, `user:*`) - 테스트 있음
  - ✅ 잘못된 권한 형식 (예: `user`, `user:read:extra`) - 테스트 추가
  - ✅ 빈 권한 Set 처리 - 테스트 추가

#### 3.2 SecurityContextHolder - Thread Safety
- **상태**: ✅ 완료
- **누락 항목**:
  - ✅ clearContext() 후 getContext() 동작 검증 - 테스트 추가

#### 3.3 ResourceAccessChecker - Edge Cases
- **상태**: ✅ 완료
- **테스트 항목**: null 파라미터, 빈 문자열, SUPER_ADMIN 검증, 권한 조합 시나리오 포함

---

### 4. MISSING_FIXTURES (LOW) 🟢

#### 4.1 Auth Test Fixtures
- **상태**: ✅ 완료
- **위치**: `adapter-in/rest-api/src/testFixtures/java/com/ryuqq/authhub/adapter/in/rest/auth/fixture/SecurityContextFixture.java`
- **커버리지**: 
  - ErrorMapper 테스트용 fixtures 있음 ✅
  - SecurityContextFixture 생성 ✅ (superAdminContext, tenantAdminContext, orgAdminContext, memberContext, contextWithPermissions, contextWithRoles, anonymousContext)
  - ResourceAccessCheckerFixture - 선택 사항, 미생성 (SecurityContextFixture로 대체 가능)

---

### 5. PATTERN_VIOLATION (LOW) 🟢

#### 5.1 테스트 구조
- **상태**: ✅ 컨벤션 준수
  - `@Tag("unit")` 사용 ✅
  - `@DisplayName` 한글 사용 ✅
  - `@Nested` 클래스 그룹핑 ✅

#### 5.2 테스트 네이밍
- **상태**: ✅ 컨벤션 준수
  - `should...()` 패턴 사용 ✅
  - 한글 DisplayName 사용 ✅

---

## 🎯 우선순위별 권장 조치

### HIGH 우선순위 (즉시 조치)

1. ✅ **ResourceAccessCheckerTest 생성** - 완료
2. ✅ **CorsPropertiesTest 생성** - 완료
3. ✅ **JwtValidationPropertiesTest 생성** - 완료
4. ⚠️ **SecurityConfigTest** - **TODO: E2E/통합 테스트로 이관 필요**
   - adapter-in 단위 테스트에서는 Spring MVC `mvcHandlerMappingIntrospector` 의존으로 실패
   - **이관 대상**: `integration-test` 모듈 또는 `bootstrap-web-api` 전체 컨텍스트 기반 통합 테스트

### MEDIUM 우선순위 (단기 조치)

5. ✅ **SecurityContextTest 보완** - 완료
6. ✅ **SecurityContextHolderTest 보완** - 완료
7. ✅ **AuthErrorMapperTest 보완** - 완료

### LOW 우선순위 (중기 조치)

8. ✅ **SecurityContextFixture 생성** - 완료
9. **ResourceAccessCheckerFixture 생성** (선택) - 미생성 (선택 사항)

---

## 📈 커버리지 목표

| 클래스 | 현재 | 목표 | 갭 |
|--------|------|------|-----|
| ResourceAccessChecker | 95%+ | 95%+ | ✅ |
| SecurityContext | 95%+ | 95%+ | ✅ |
| SecurityContextHolder | 95%+ | 95%+ | ✅ |
| SecurityConfig | 0% | 80%+ | ⚠️ TODO 이관 |
| CorsProperties | 90%+ | 90%+ | ✅ |
| JwtValidationProperties | 90%+ | 90%+ | ✅ |
| AuthErrorMapper | 95%+ | 95%+ | ✅ |
| GatewayAuthenticationFilter | 100% | 100% | ✅ |
| GatewayHeaderExtractor | 100% | 100% | ✅ |
| SecurityExceptionHandler | 100% | 100% | ✅ |
| SecurityPaths | 100% | 100% | ✅ |

**전체 커버리지**: ~85% (64% → 85%+) ✅

---

## 📝 체크리스트

### 즉시 조치 (HIGH)
- [x] ResourceAccessCheckerTest 생성 (19개 메서드 테스트)
- [x] CorsPropertiesTest 생성
- [x] JwtValidationPropertiesTest 생성
- [ ] **SecurityConfigTest** - **TODO: E2E/통합 테스트로 이관 필요**

### 단기 조치 (MEDIUM)
- [x] SecurityContextTest - 누락된 메서드 테스트 추가
- [x] SecurityContextHolderTest - 누락된 메서드 테스트 추가
- [x] AuthErrorMapperTest - 누락된 시나리오 테스트 추가

### 중기 조치 (LOW)
- [x] SecurityContextFixture 생성
- [ ] ResourceAccessCheckerFixture 생성 (선택)

---

## ✅ 완료 처리 내역

**보완 완료일**: 2026-02-04

### 완료된 항목
- ResourceAccessCheckerTest 생성
- CorsPropertiesTest 생성
- JwtValidationPropertiesTest 생성
- SecurityContextTest 보완 (hasAllPermissions, isTenantAdmin, isOrgAdmin, getTraceId, toString, Builder null)
- SecurityContextHolderTest 보완 (getCurrentOrganizationId, hasAnyRole, hasAnyPermission, hasAllPermissions, isSuperAdmin, isAuthenticated, getTraceId, Thread safety)
- AuthErrorMapperTest 보완 (AUTH-007 → 403, URI 생성 검증)
- SecurityContextFixture 생성
- AccessForbiddenException 도메인 추가 (AUTH-007)
- ErrorMapperApiFixture에 accessForbiddenException() 추가

### TODO - 이관 필요
- **SecurityConfigTest**: adapter-in 단위 테스트 불가 (Spring MVC 의존). **E2E/통합 테스트로 이관 필요** → `integration-test` 모듈 또는 bootstrap 전체 컨텍스트 기반 통합 테스트에서 SecurityFilterChain, CORS, Public/Docs 경로 검증

---

## 🔗 참고 자료

- **참고 패턴**: 다른 패키지의 Component 테스트 (예: `SecurityContextTest.java`)
- **테스트 컨벤션**: `.claude/agents/api-tester.md`
- **보안 테스트 가이드**: `.claude/agents/test-auditor.md` (Adapter-In 레이어 섹션)

---

## ⚠️ 특별 고려사항

### ResourceAccessChecker 테스트 복잡도
- **19개 public 메서드**: 각 메서드마다 여러 시나리오 필요
- **권한 조합**: SUPER_ADMIN, TENANT_ADMIN, ORG_ADMIN, MEMBER 조합 테스트
- **리소스 격리**: 테넌트/조직/사용자 격리 규칙 검증
- **권장 접근**: 
  1. 역할별 그룹핑 (`@Nested` 클래스)
  2. 각 메서드별 성공/실패 시나리오
  3. SUPER_ADMIN 우선순위 검증

### SecurityConfig 통합 테스트 (TODO - 이관 필요)
- Spring Security 설정은 통합 테스트가 적합
- adapter-in 단위 테스트에서는 `mvcHandlerMappingIntrospector` 의존으로 불가
- **이관 권장**: `integration-test` 모듈의 E2ETestBase 또는 bootstrap 전체 컨텍스트 기반 테스트에서 검증

---

**생성일**: 2026-02-04  
**보완 완료일**: 2026-02-04  
**최종 업데이트**: 2026-02-04  
**다음 조치**: SecurityConfigTest → E2E/통합 테스트로 이관
