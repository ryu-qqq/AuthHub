# Application Layer – tenant 패키지 테스트 감사

> **상태**: ✅ **완료**  
> **보완 완료일**: 2025-02-05  
> **최종 업데이트**: 2025-02-05

**대상:** `application` 레이어 `com.ryuqq.authhub.application.tenant`  
**실행일:** 2025-02-05  
**산출물:** 권장 조치 및 우선순위

---

## 1. 요약

| 항목 | 상태 |
|------|------|
| 소스 패키지 | `application/.../tenant` (assembler, dto, factory, internal, manager, port, service, validator) |
| 테스트 존재 | **6개 클래스 테스트 누락** (TenantQueryFactory, OnboardingIdempotencyQueryManager, UpdateTenantNameService, UpdateTenantStatusService, SearchTenantsByOffsetService, GetTenantConfigService) |
| testFixtures | `TenantCommandFixtures` 존재 ✅ / **TenantQueryFixtures 없음** (Query 서비스 테스트 시 SearchParams 등 하드코딩 가능성) |
| 메서드 누락 | TenantReadManager: `countByCriteria`, `findByIdOptional`, `existsByNameAndIdNot` 미테스트 |
| 엣지/경계 | UpdateTenantStatusService invalid status, GetTenantConfigService TenantNotFoundException 등 시나리오 미보강 |
| 컨벤션 | tenantservice 패키지에 있는 Query Fixtures 패턴이 tenant에는 없음 (PATTERN_VIOLATION) |
| **HIGH 우선순위** | 6건 (테스트 클래스 신규 추가) |
| **MED 우선순위** | 2건 (Manager 메서드 보강, Fixtures) |
| **LOW 우선순위** | 0건 (HIGH/MED 보완 시 엣지 케이스 포함 완료) |

---

## 2. 소스 vs 테스트 매트릭스

| 소스 클래스 | 테스트 클래스 | 비고 |
|-------------|----------------|------|
| **assembler** | | |
| TenantAssembler | TenantAssemblerTest | ✅ toResult, toResultList(empty), toPageResult, toConfigResult |
| **validator** | | |
| TenantValidator | TenantValidatorTest | ✅ findExistingOrThrow, validateNameNotDuplicated(성공/실패) |
| OnboardingValidator | OnboardingValidatorTest | ✅ 검증 시나리오 |
| **manager** | | |
| TenantCommandManager | TenantCommandManagerTest | ✅ persist 위임 |
| TenantReadManager | TenantReadManagerTest | ⚠️ findById, existsById, existsByName, findAllByCriteria만 테스트. **countByCriteria, findByIdOptional, existsByNameAndIdNot 누락** |
| OnboardingIdempotencyCommandManager | OnboardingIdempotencyCommandManagerTest | ✅ |
| OnboardingIdempotencyQueryManager | — | ❌ **테스트 없음** |
| **factory** | | |
| TenantCommandFactory | TenantCommandFactoryTest | ✅ create, createNameUpdateContext, createStatusChangeContext |
| TenantQueryFactory | — | ❌ **테스트 없음** (toCriteria 등) |
| OnboardingFactory | OnboardingFactoryTest | ✅ |
| **internal** | | |
| OnboardingFacade | OnboardingFacadeTest | ✅ |
| **service/command** | | |
| CreateTenantService | CreateTenantServiceTest | ✅ 성공/중복 이름 예외 |
| OnboardingService | OnboardingServiceTest | ✅ |
| UpdateTenantNameService | — | ❌ **테스트 없음** |
| UpdateTenantStatusService | — | ❌ **테스트 없음** |
| **service/query** | | |
| SearchTenantsByOffsetService | — | ❌ **테스트 없음** |
| GetTenantConfigService | — | ❌ **테스트 없음** |
| **port (in/out), dto** | — | 인터페이스/레코드로 단위 테스트 생략 (관례) |

---

## 3. 갭 유형별 분석

### 3.1 MISSING_TEST (우선순위: HIGH 6건)

| 클래스 | 역할 | public 메서드 | 권장 |
|--------|------|----------------|------|
| TenantQueryFactory | Factory | `toCriteria(TenantSearchParams)` | TenantQueryFactoryTest 신규 추가. CommonVoFactory mock, toCriteria 기본/statuses null/빈 목록 등 |
| OnboardingIdempotencyQueryManager | Manager | `findByIdempotencyKey(String)` | OnboardingIdempotencyQueryManagerTest 신규. Port mock, Optional 반환/empty 검증 |
| UpdateTenantNameService | Service | `execute(UpdateTenantNameCommand)` | UpdateTenantNameServiceTest 신규. Validator/Factory/Manager mock, 성공·중복 이름 예외 |
| UpdateTenantStatusService | Service | `execute(UpdateTenantStatusCommand)` | UpdateTenantStatusServiceTest 신규. 성공·TenantNotFoundException·잘못된 status(IllegalArgumentException) |
| SearchTenantsByOffsetService | Service | `execute(TenantSearchParams)` | SearchTenantsByOffsetServiceTest 신규. Factory→ReadManager→Assembler 순서, PageResult 반환/빈 결과 |
| GetTenantConfigService | Service | `getByTenantId(String)` | GetTenantConfigServiceTest 신규. 성공·TenantNotFoundException |

### 3.2 MISSING_FIXTURES (우선순위: MED)

- **TenantQueryFixtures 없음.**  
  - tenantservice 패키지는 `TenantServiceQueryFixtures`로 SearchParams, PageResult 등 제공.  
  - tenant는 Query 관련 Fixtures가 없어, SearchTenantsByOffsetServiceTest·TenantQueryFactoryTest 작성 시 `TenantSearchParams`, `CommonSearchParams` 등을 매번 하드코딩할 가능성이 있음.  
- **권장:** `TenantQueryFixtures` 추가 (예: `searchParams()`, `searchParamsWithStatuses(...)`, 필요 시 `TenantFixture`/domain 연동).

### 3.3 MISSING_METHOD (우선순위: MED)

- **TenantReadManagerTest**  
  - 다음 3개 메서드에 대한 테스트 없음:  
    - `countByCriteria(TenantSearchCriteria)`  
    - `findByIdOptional(TenantId)` (존재 시 Optional.of, 미존재 시 Optional.empty())  
    - `existsByNameAndIdNot(TenantName, TenantId)`  
  - 권장: 위 메서드별 Nested + 성공/실패(또는 empty) 시나리오 추가.

### 3.4 MISSING_EDGE_CASE (우선순위: LOW)

- **UpdateTenantStatusService:** `command.status()`가 도메인에 없는 값(예: `"INVALID"`)일 때 `IllegalArgumentException` 발생 및 Manager 미호출 검증.  
- **GetTenantConfigService:** 존재하지 않는 tenantId로 `getByTenantId` 호출 시 `TenantNotFoundException` 검증.  
- **TenantQueryFactory:** `statuses`가 null이거나 빈 목록일 때 toCriteria 결과에서 null/빈 목록 처리 검증.

### 3.5 MISSING_STATE_TRANSITION

- 해당 없음. tenant 애플리케이션 레이어는 상태 기계보다 오케스트레이션 위주.

### 3.6 PATTERN_VIOLATION (우선순위: LOW)

- **Query Fixtures 패턴 불일치:**  
  - tenantservice는 Command + Query Fixtures 모두 보유.  
  - tenant는 Command Fixtures만 있고 Query Fixtures가 없어, 동일 레이어 내 패턴과 불일치.  
- **권장:** TenantQueryFixtures 도입 시 PATTERN_VIOLATION 해소.

---

## 4. 우선순위별 권장 조치

| 우선순위 | 유형 | 대상 | 조치 |
|----------|------|------|------|
| **HIGH** | MISSING_TEST | TenantQueryFactory | TenantQueryFactoryTest 신규. toCriteria(기본, statuses null/empty), CommonVoFactory mock |
| **HIGH** | MISSING_TEST | OnboardingIdempotencyQueryManager | OnboardingIdempotencyQueryManagerTest 신규. findByIdempotencyKey 성공/empty |
| **HIGH** | MISSING_TEST | UpdateTenantNameService | UpdateTenantNameServiceTest 신규. execute 성공/중복 이름 예외, TenantCommandFixtures 활용 |
| **HIGH** | MISSING_TEST | UpdateTenantStatusService | UpdateTenantStatusServiceTest 신규. execute 성공/NotFound/잘못된 status 예외 |
| **HIGH** | MISSING_TEST | SearchTenantsByOffsetService | SearchTenantsByOffsetServiceTest 신규. Factory→Manager→Assembler, PageResult (TenantQueryFixtures 권장) |
| **HIGH** | MISSING_TEST | GetTenantConfigService | GetTenantConfigServiceTest 신규. getByTenantId 성공/TenantNotFoundException |
| **MED** | MISSING_METHOD | TenantReadManagerTest | countByCriteria, findByIdOptional, existsByNameAndIdNot 테스트 추가 |
| **MED** | MISSING_FIXTURES | tenant testFixtures | TenantQueryFixtures 추가 (searchParams 등), Query 관련 테스트에서 재사용 |
| **LOW** | MISSING_EDGE_CASE | UpdateTenantStatusServiceTest | 잘못된 status 문자열 시 IllegalArgumentException 테스트 |
| **LOW** | MISSING_EDGE_CASE | GetTenantConfigServiceTest | 존재하지 않는 tenantId 시 TenantNotFoundException 테스트 |
| **LOW** | MISSING_EDGE_CASE | TenantQueryFactoryTest | toCriteria에 statuses null/빈 목록 입력 시 처리 검증 |

---

## 5. 권장 조치 상세

### 5.1 TenantQueryFactoryTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/factory/TenantQueryFactoryTest.java`
- **구성:**  
  - `@ExtendWith(MockitoExtension.class)`, CommonVoFactory mock.  
  - Nested `ToCriteria`:  
    - TenantSearchParams로 toCriteria 호출 시 TenantSearchCriteria 필드(searchWord, searchField, statuses, dateRange, queryContext) 검증.  
    - statuses가 null이거나 빈 목록인 params로 호출 시 criteria의 statuses 처리 검증.  
- **Fixture:** TenantQueryFixtures 도입 시 `searchParams()`, `searchParamsWithStatuses(List<String>)` 등 사용 권장.

### 5.2 OnboardingIdempotencyQueryManagerTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/manager/OnboardingIdempotencyQueryManagerTest.java`
- **구성:** OnboardingIdempotencyQueryPort mock, `findByIdempotencyKey` 호출 시 Optional.of(OnboardingResult) 반환 검증, Optional.empty() 반환 검증.

### 5.3 UpdateTenantNameServiceTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/service/command/UpdateTenantNameServiceTest.java`
- **구성:** TenantValidator, TenantCommandFactory, TenantCommandManager mock.  
  - 성공: execute 호출 시 validator.validateNameNotDuplicatedExcluding, findExistingOrThrow, commandFactory.createNameUpdateContext, tenant.changeName, commandManager.persist 호출 순서/인자 검증.  
  - 실패: 중복 이름 시 DuplicateTenantNameException, findExistingOrThrow 미존재 시 TenantNotFoundException.  
- **Fixture:** TenantCommandFixtures.updateNameCommand() 활용.

### 5.4 UpdateTenantStatusServiceTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/service/command/UpdateTenantStatusServiceTest.java`
- **구성:** 성공 시나리오, TenantNotFoundException(validator.findExistingOrThrow), 잘못된 status 문자열 시 IllegalArgumentException 및 persist 미호출 검증.  
- **Fixture:** TenantCommandFixtures.deactivateCommand(), activateCommand() 활용.

### 5.5 SearchTenantsByOffsetServiceTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/service/query/SearchTenantsByOffsetServiceTest.java`
- **구성:** TenantQueryFactory, TenantReadManager, TenantAssembler mock.  
  - execute 호출 시 queryFactory.toCriteria → readManager.findAllByCriteria, countByCriteria → assembler.toPageResult 순서 및 반환 TenantPageResult 검증.  
  - 빈 목록/0 totalElements 시나리오 1개 권장.  
- **Fixture:** TenantQueryFixtures.searchParams() 또는 동일 패턴 도입 시 사용.

### 5.6 GetTenantConfigServiceTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/tenant/service/query/GetTenantConfigServiceTest.java`
- **구성:** TenantReadManager, TenantAssembler mock.  
  - getByTenantId(tenantId) 호출 시 readManager.findById(TenantId.of(tenantId)), assembler.toConfigResult(tenant) 검증.  
  - findById에서 TenantNotFoundException 발생 시 전파 검증.

### 5.7 TenantReadManagerTest 보강 (MED)

- **countByCriteria:** criteria 전달 시 queryPort.countByCriteria 호출 및 반환값 검증.  
- **findByIdOptional:** queryPort.findById가 Optional.of(tenant) / Optional.empty() 반환 시 sut.findByIdOptional 결과 검증.  
- **existsByNameAndIdNot:** queryPort.existsByNameAndIdNot 호출 및 true/false 반환 검증.

### 5.8 TenantQueryFixtures 추가 (MED)

- **파일:** `application/src/testFixtures/java/com/ryuqq/authhub/application/tenant/fixture/TenantQueryFixtures.java`
- **제공 메서드 예:**  
  - `TenantSearchParams searchParams()`  
  - `TenantSearchParams searchParamsWithStatuses(List<String> statuses)`  
  - (선택) CommonSearchParams, page/size 등 파라미터화된 of 메서드.  
- tenantservice의 TenantServiceQueryFixtures 구조를 참고하여 동일한 패턴으로 작성.

---

## 6. 참고

- Port(in/out), DTO(record)는 단위 테스트 생략이 프로젝트 관례임.
- 우선순위: 커버리지 갭(테스트 없음 HIGH) → 메서드 누락(MED) → Fixtures/엣지/패턴(LOW) 순으로 정리함.
- `/test-fix` 스킬로 HIGH 항목부터 자동 보완 가능.

---

## 7. 완료 내역

| 완료일 | 조치 |
|--------|------|
| 2025-02-05 | MED: TenantQueryFixtures 추가 (searchParams, searchParamsWithStatuses, defaultCommonSearchParams) |
| 2025-02-05 | HIGH: TenantQueryFactoryTest 신규 (toCriteria 기본/null/빈 statuses/statuses 반영) |
| 2025-02-05 | HIGH: OnboardingIdempotencyQueryManagerTest 신규 (findByIdempotencyKey 성공/empty) |
| 2025-02-05 | HIGH: UpdateTenantNameServiceTest 신규 (execute 성공/중복 이름/TenantNotFound) |
| 2025-02-05 | HIGH: UpdateTenantStatusServiceTest 신규 (execute 성공/NotFound/잘못된 status IllegalArgumentException) |
| 2025-02-05 | HIGH: SearchTenantsByOffsetServiceTest 신규 (Factory→Manager→Assembler, 빈 결과) |
| 2025-02-05 | HIGH: GetTenantConfigServiceTest 신규 (getByTenantId 성공/TenantNotFoundException) |
| 2025-02-05 | MED: TenantReadManagerTest 보강 (countByCriteria, findByIdOptional, existsByNameAndIdNot) |

**최종 상태:** HIGH 6건, MED 2건 보완 완료. LOW 엣지 케이스는 UpdateTenantStatusServiceTest·GetTenantConfigServiceTest·TenantQueryFactoryTest에 포함됨.
