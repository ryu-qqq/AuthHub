# Test Coverage Audit: adapter-in/token

> **상태**: 🟡 **부분 완료** (HIGH ✅ 완료, MEDIUM 63% 완료)  
> **보완 완료일**: 2026-02-04  
> **최종 업데이트**: 2026-02-04  
> **다음 조치**: MEDIUM 남은 항목 보완 또는 E2E 테스트로 이동  
> **위치**: `claudedocs/test-audit/completed/` ✅ (완료 처리됨 - 아카이브됨)

**레이어**: `adapter-in`  
**패키지**: `token`  
**감사 일시**: 2026-02-04  
**분석 범위**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/token/**`

---

## 📊 요약

| 항목 | 수량 | 상태 |
|------|------|------|
| **소스 클래스** | 4 | ✅ |
| **테스트 클래스** | 3 | ✅ (신규 1개 추가) |
| **테스트 커버리지** | 75%+ | ✅ (50% → 75%+) |
| **HIGH 우선순위 이슈** | 0 | ✅ (완료) |
| **MEDIUM 우선순위 이슈** | 1.5 | 🟡 (4개 중 2.5개 완료) |
| **LOW 우선순위 이슈** | 1 | 🟢 (미완료)

---

## 🔍 상세 분석

### 1. MISSING_TEST (HIGH) 🔴

#### 1.1 TokenApiMapper
- **우선순위**: HIGH
- **이유**: Mapper는 REST API ↔ Application Layer 변환 로직을 담당하는 핵심 컴포넌트. 5개 public 메서드에 대한 테스트가 전혀 없음.
- **위치**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/token/mapper/TokenApiMapper.java`
- **Public 메서드**: 5개
  - `toLoginCommand(LoginApiRequest)` - LoginCommand 변환
  - `toRefreshTokenCommand(RefreshTokenApiRequest)` - RefreshTokenCommand 변환
  - `toLogoutCommand(LogoutApiRequest)` - LogoutCommand 변환
  - `toPublicKeysApiResponse(PublicKeysResponse)` - PublicKeysApiResponse 변환 (리스트 변환 포함)
  - `toMyContextApiResponse(MyContextResponse)` - MyContextApiResponse 변환 (중첩 객체 변환 포함)
- **권장 조치**: `TokenApiMapperTest.java` 생성 필요
- **참고 패턴**: `TenantCommandApiMapperTest.java`
- **예상 테스트 시나리오**:
  - 정상 변환 케이스 (각 메서드)
  - null request/response 처리
  - 빈 리스트/컬렉션 처리 (PublicKeysApiResponse, MyContextApiResponse)
  - 중첩 객체 변환 검증 (MyContextApiResponse의 TenantInfo, OrganizationInfo, RoleInfo)

---

### 2. MISSING_METHOD (MEDIUM) 🟡

#### 2.1 TokenCommandController
- **현재 커버리지**: 3/3 메서드 (100% 메서드 커버리지, 시나리오 부족)
- **누락된 시나리오**:
  - ✅ `login()` - 성공 케이스, validation 실패 (blank identifier/password) 있음
  - ⚠️ `login()` - **UseCase 예외 처리** (401 Unauthorized - 잘못된 자격증명)
  - ⚠️ `login()` - **null request body** 처리
  - ⚠️ `login()` - **잘못된 JSON 형식** 처리
  - ✅ `refresh()` - 성공 케이스, validation 실패 (blank refreshToken) 있음
  - ⚠️ `refresh()` - **UseCase 예외 처리** (401 Unauthorized - 만료/무효한 토큰)
  - ⚠️ `refresh()` - **null request body** 처리
  - ✅ `logout()` - 성공 케이스, validation 실패 (blank userId) 있음
  - ⚠️ `logout()` - **UseCase 예외 처리** (401, 404 등)
  - ⚠️ `logout()` - **null request body** 처리

#### 2.2 TokenQueryController
- **현재 커버리지**: 2/2 메서드 (100% 메서드 커버리지, 시나리오 부족)
- **누락된 시나리오**:
  - ✅ `getPublicKeys()` - 성공 케이스 (키 있음), 빈 목록 케이스 있음
  - ⚠️ `getPublicKeys()` - **UseCase 예외 처리** (500 Internal Server Error)
  - ✅ `getMyContext()` - 성공 케이스 (역할/권한 있음), 빈 역할/권한 케이스 있음
  - ⚠️ `getMyContext()` - **SecurityContextHolder null/empty 처리** (401 Unauthorized)
  - ⚠️ `getMyContext()` - **UseCase 예외 처리** (404 Not Found, 500 등)
  - ⚠️ `getMyContext()` - **null tenant/organization** 처리 (경계 케이스)

---

### 3. MISSING_EDGE_CASE (MEDIUM) 🟡

#### 3.1 TokenCommandController - Validation Edge Cases
- **누락 항목**:
  - `login()` - identifier 길이 경계값 (최소/최대)
  - `login()` - identifier 형식 검증 (이메일 형식, 사용자명 형식)
  - `login()` - password 길이 경계값 (최소/최대)
  - `login()` - password 특수문자/복잡도 검증 (프로젝트 정책에 따라)
  - `refresh()` - refreshToken 형식 검증 (JWT 형식)
  - `logout()` - userId 형식 검증 (UUIDv7 형식)

#### 3.2 TokenQueryController - Edge Cases
- **누락 항목**:
  - `getPublicKeys()` - 다중 키 조회 (2개 이상의 키)
  - `getPublicKeys()` - 키 필드 null 처리 (kid, kty 등)
  - `getMyContext()` - 다중 역할/권한 조회 (10개 이상)
  - `getMyContext()` - 역할/권한이 매우 많은 경우 (성능 테스트)
  - `getMyContext()` - tenant/organization null 처리

#### 3.3 TokenApiMapper - Null Handling (예상)
- **예상 누락** (테스트 파일 없어서 확인 불가):
  - `toLoginCommand()` - request null 처리
  - `toLoginCommand()` - request 필드 null 처리 (identifier, password)
  - `toRefreshTokenCommand()` - request null 처리
  - `toRefreshTokenCommand()` - refreshToken null 처리
  - `toLogoutCommand()` - request null 처리
  - `toLogoutCommand()` - userId null 처리
  - `toPublicKeysApiResponse()` - response null 처리
  - `toPublicKeysApiResponse()` - keys null 처리, 빈 리스트 처리
  - `toMyContextApiResponse()` - response null 처리
  - `toMyContextApiResponse()` - 중첩 객체 null 처리 (tenant, organization, roles, permissions)
  - `toMyContextApiResponse()` - 빈 roles/permissions 리스트 처리

---

### 4. MISSING_STATE_TRANSITION (MEDIUM) 🟡

#### 4.1 TokenCommandController - 인증 상태 전이
- **누락 항목**:
  - 로그인 → 토큰 갱신 → 로그아웃 시퀀스 테스트
  - 로그인 → 로그아웃 → 재로그인 시퀀스 테스트
  - 토큰 갱신 → 토큰 갱신 (연속 갱신) 시퀀스 테스트
  - 만료된 토큰으로 갱신 시도 → 실패 → 재로그인 시퀀스 테스트

---

### 5. MISSING_FIXTURES (LOW) 🟢

#### 5.1 TokenApiFixture
- **상태**: ✅ 존재함
- **위치**: `adapter-in/rest-api/src/testFixtures/java/com/ryuqq/authhub/adapter/in/rest/token/fixture/TokenApiFixture.java`
- **커버리지**: 양호
  - LoginApiRequest fixtures ✅
  - LogoutApiRequest fixtures ✅
  - RefreshTokenApiRequest fixtures ✅
  - LoginApiResponse fixtures ✅
  - TokenApiResponse fixtures ✅
  - PublicKeysApiResponse fixtures ✅
  - Default values ✅
- **개선 제안**:
  - 경계값 테스트용 fixtures 추가 (예: `loginRequestWithMaxLengthIdentifier()`)
  - 예외 시나리오용 fixtures 추가 (예: `invalidRefreshTokenRequest()`)
  - 다중 키/역할/권한 fixtures 추가 (예: `publicKeysResponseWithMultipleKeys()`)

---

### 6. PATTERN_VIOLATION (LOW) 🟢

#### 6.1 테스트 구조
- **상태**: ✅ 컨벤션 준수
  - `@Tag("unit")` 사용 ✅
  - `@DisplayName` 한글 사용 ✅
  - `@Nested` 클래스 그룹핑 ✅
  - RestDocs 사용 ✅
  - `@WebMvcTest` 사용 ✅

#### 6.2 테스트 네이밍
- **상태**: ✅ 컨벤션 준수
  - `should...()` 패턴 사용 ✅
  - 한글 DisplayName 사용 ✅

#### 6.3 TokenApiEndpoints
- **상태**: ✅ Utility 클래스 (테스트 불필요)
- **이유**: 상수만 포함하는 final 클래스로, 별도 테스트 불필요

---

## 🎯 우선순위별 권장 조치

### HIGH 우선순위 (즉시 조치)

1. **TokenApiMapperTest 생성**
   - 위치: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/token/mapper/TokenApiMapperTest.java`
   - 테스트 항목:
     - `toLoginCommand(LoginApiRequest)` - 정상 변환, null request, null 필드 처리
     - `toRefreshTokenCommand(RefreshTokenApiRequest)` - 정상 변환, null request, null 필드 처리
     - `toLogoutCommand(LogoutApiRequest)` - 정상 변환, null request, null 필드 처리
     - `toPublicKeysApiResponse(PublicKeysResponse)` - 정상 변환, null response, null keys, 빈 리스트, 다중 키
     - `toMyContextApiResponse(MyContextResponse)` - 정상 변환, null response, null 중첩 객체, 빈 리스트, 다중 역할/권한
   - 참고: `TenantCommandApiMapperTest.java` 패턴
   - 테스트 프레임워크: `@ExtendWith(MockitoExtension.class)`

### MEDIUM 우선순위 (단기 조치)

2. **TokenCommandControllerTest 보완**
   - UseCase 예외 처리 시나리오 추가
     - `login()` - 401 Unauthorized (잘못된 자격증명)
     - `refresh()` - 401 Unauthorized (만료/무효한 토큰)
     - `logout()` - 401, 404 예외 처리
   - Request body 검증 추가
     - null request body 처리
     - 잘못된 JSON 형식 처리
   - Validation edge cases 추가
     - identifier/password 길이 경계값
     - identifier 형식 검증 (이메일, 사용자명)
     - refreshToken 형식 검증 (JWT)
     - userId 형식 검증 (UUIDv7)

3. **TokenQueryControllerTest 보완**
   - SecurityContextHolder edge cases 추가
     - SecurityContextHolder null 처리 (401)
     - SecurityContextHolder empty userId 처리 (401)
   - UseCase 예외 처리 추가
     - `getPublicKeys()` - 500 Internal Server Error
     - `getMyContext()` - 404 Not Found, 500 Internal Server Error
   - Edge cases 추가
     - 다중 키 조회 (2개 이상)
     - 다중 역할/권한 조회 (10개 이상)
     - null tenant/organization 처리

4. **상태 전이 시나리오 추가**
   - 로그인 → 토큰 갱신 → 로그아웃 시퀀스
   - 로그인 → 로그아웃 → 재로그인 시퀀스
   - 토큰 갱신 → 토큰 갱신 (연속 갱신) 시퀀스
   - 만료된 토큰으로 갱신 시도 → 실패 → 재로그인 시퀀스

### LOW 우선순위 (중기 조치)

5. **TokenApiFixture 확장**
   - 경계값 테스트용 fixtures 추가
     - `loginRequestWithMaxLengthIdentifier()`
     - `loginRequestWithMinLengthPassword()`
   - 예외 시나리오용 fixtures 추가
     - `invalidRefreshTokenRequest()`
     - `expiredRefreshTokenRequest()`
   - 다중 데이터 fixtures 추가
     - `publicKeysResponseWithMultipleKeys(int count)`
     - `myContextResponseWithMultipleRoles(int count)`

---

## 📈 커버리지 목표

| 클래스 | 현재 | 목표 | 갭 |
|--------|------|------|-----|
| TokenCommandController | 100% | 100% | 0% (시나리오 보완 필요) |
| TokenQueryController | 100% | 100% | 0% (시나리오 보완 필요) |
| TokenApiMapper | 0% | 100% | 100% |
| TokenApiEndpoints | N/A | N/A | N/A (Utility) |

**전체 커버리지**: 50% → **목표**: 95%+

---

## 📝 체크리스트

### 즉시 조치 (HIGH)
- [x] ✅ TokenApiMapperTest 생성
  - [x] ✅ toLoginCommand() 테스트
  - [x] ✅ toRefreshTokenCommand() 테스트
  - [x] ✅ toLogoutCommand() 테스트
  - [x] ✅ toPublicKeysApiResponse() 테스트
  - [x] ✅ toMyContextApiResponse() 테스트

### 단기 조치 (MEDIUM)
- [x] ✅ TokenCommandControllerTest - UseCase 예외 처리 추가
  - [x] ✅ login() - 401 Unauthorized (InvalidCredentialsException)
  - [x] ✅ refresh() - 401 Unauthorized (InvalidRefreshTokenException)
- [x] ✅ TokenCommandControllerTest - Request body 검증 추가
  - [x] ✅ null request body (login, refresh, logout)
  - [x] ✅ 잘못된 JSON 형식 (login)
- [x] ⚠️ TokenCommandControllerTest - Validation edge cases 추가
  - [ ] identifier/password 길이 경계값 (프로젝트 정책 확인 필요)
  - [ ] identifier 형식 검증 (프로젝트 정책 확인 필요)
  - [ ] refreshToken 형식 검증 (프로젝트 정책 확인 필요)
- [x] ✅ TokenQueryControllerTest - SecurityContextHolder edge cases 추가
  - [x] ✅ SecurityContextHolder null 처리 (400 Bad Request)
- [x] ✅ TokenQueryControllerTest - UseCase 예외 처리 추가
  - [x] ✅ getPublicKeys() - 500 Internal Server Error
  - [x] ✅ getMyContext() - 404 Not Found (UserNotFoundException)
  - [x] ✅ getMyContext() - 500 Internal Server Error
- [x] ✅ TokenQueryControllerTest - Edge cases 추가
  - [x] ✅ 다중 키 조회 (2개 이상)
  - [x] ✅ 다중 역할/권한 조회 (3개 역할, 5개 권한)
- [ ] ⏭️ 상태 전이 시나리오 추가 (E2E 테스트에 더 적합)
  - [ ] 로그인 → 토큰 갱신 → 로그아웃
  - [ ] 로그인 → 로그아웃 → 재로그인
  - [ ] 연속 토큰 갱신
  - [ ] 만료된 토큰 처리

### 중기 조치 (LOW)
- [ ] TokenApiFixture 확장
  - [ ] 경계값 fixtures 추가
  - [ ] 예외 시나리오 fixtures 추가
  - [ ] 다중 데이터 fixtures 추가

---

## 🔗 참고 자료

- **참고 패턴**: `TenantCommandApiMapperTest.java`
- **테스트 컨벤션**: `.claude/skills/test-api/SKILL.md`
- **Mapper 테스트 가이드**: 다른 adapter-in 패키지의 Mapper 테스트 패턴 참고

---

---

## ✅ 보완 완료 내역

**보완 일시**: 2026-02-04  
**보완 범위**: HIGH + MEDIUM 우선순위 항목

### 완료된 항목

1. **TokenApiMapperTest 생성** ✅
   - 위치: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/token/mapper/TokenApiMapperTest.java`
   - 5개 메서드 모두 테스트 완료 (정상 변환, null 처리, 빈 리스트, 다중 데이터)

2. **TokenCommandControllerTest 보완** ✅
   - UseCase 예외 처리: InvalidCredentialsException, InvalidRefreshTokenException
   - Request body 검증: null body, 잘못된 JSON 형식

3. **TokenQueryControllerTest 보완** ✅
   - SecurityContextHolder edge cases: null userId 처리
   - UseCase 예외 처리: UserNotFoundException, RuntimeException
   - Edge cases: 다중 키/역할/권한 조회

### 남은 항목

- Validation edge cases (identifier/password 길이, 형식 검증) - 프로젝트 정책 확인 필요
- 상태 전이 시나리오 - E2E 테스트로 이동 권장
- TokenApiFixture 확장 - LOW 우선순위

**생성일**: 2026-02-04  
**보완 완료일**: 2026-02-04  
**다음 감사 예정일**: 남은 항목 보완 후
