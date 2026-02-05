# Application Layer – tenantservice 패키지 테스트 감사

> **상태**: ✅ **완료**  
> **보완 완료일**: 2025-02-05  
> **최종 업데이트**: 2025-02-05

**대상:** `application` 레이어 `com.ryuqq.authhub.application.tenantservice`  
**실행일:** 2025-02-05  
**산출물:** 권장 조치 및 우선순위

---

## 1. 요약

| 항목 | 상태 |
|------|------|
| 소스 패키지 | `application/.../tenantservice` (assembler, dto, factory, manager, port, service, validator) |
| 테스트 존재 | 모든 테스트 대상 클래스에 대응 테스트 클래스 있음 ✅ |
| testFixtures | `TenantServiceCommandFixtures`, `TenantServiceQueryFixtures` 존재 및 사용 ✅ |
| 메서드 누락 | 없음 ✅ |
| 엣지/경계 | 3건 보강 완료 (ReadManager empty Optional, QueryFactory null statuses, Service invalid status) ✅ |
| 컨벤션 | 위반 없음 ✅ |
| **MED 우선순위** | 0건 (1건 보완 완료) ✅ |
| **LOW 우선순위** | 0건 (2건 보완 완료) ✅ |

---

## 2. 소스 vs 테스트 매트릭스

| 소스 클래스 | 테스트 클래스 | 비고 |
|-------------|----------------|------|
| **assembler** | | |
| TenantServiceAssembler | TenantServiceAssemblerTest | ✅ toResult, toResultList(null/empty), toPageResult, 상태별 매핑 |
| **validator** | | |
| TenantServiceValidator | TenantServiceValidatorTest | ✅ validateExists, findExistingOrThrow, validateNotDuplicated (성공/실패) |
| **manager** | | |
| TenantServiceCommandManager | TenantServiceCommandManagerTest | ✅ persist 위임 및 반환값 |
| TenantServiceReadManager | TenantServiceReadManagerTest | ✅ findById, findByIdOptional, existsById, existsByTenantIdAndServiceId, findByTenantIdAndServiceId(존재·빈 Optional), findAllByCriteria, countByCriteria |
| **factory** | | |
| TenantServiceCommandFactory | TenantServiceCommandFactoryTest | ✅ create, createStatusChangeContext, TimeProvider 연동 |
| TenantServiceQueryFactory | TenantServiceQueryFactoryTest | ✅ toCriteria (기본/tenant/service/statuses/null statuses) |
| **service/command** | | |
| SubscribeTenantServiceService | SubscribeTenantServiceServiceTest | ✅ 성공/중복 예외/Factory·Manager 호출 순서 |
| UpdateTenantServiceStatusService | UpdateTenantServiceStatusServiceTest | ✅ 성공/NotFound 예외/잘못된 status IllegalArgumentException/Domain·Manager 호출 |
| **service/query** | | |
| SearchTenantServicesService | SearchTenantServicesServiceTest | ✅ 성공/빈 결과/Factory·Manager·Assembler 순서 |
| **port (in/out), dto** | — | 인터페이스/레코드로 단위 테스트 생략 (관례) |

---

## 3. 갭 유형별 분석

### 3.1 MISSING_TEST

- **없음.** assembler, validator, manager, factory, service 하위 모든 구현 클래스에 대응 테스트 존재.

### 3.2 MISSING_FIXTURES

- **없음.**  
  - `TenantServiceCommandFixtures`: subscribeCommand, updateStatusCommand 계열 및 기본값 접근자 사용.  
  - `TenantServiceQueryFixtures`: searchParams, searchParamsWithTenant/Service/Statuses, tenantServiceResult, pageResult, emptyPageResult 사용.  
  - Domain `TenantServiceFixture`는 Assembler/Validator/Manager/Service 테스트에서 적절히 사용.

### 3.3 MISSING_METHOD

- **없음.**  
  - TenantServiceReadManager의 7개 public 메서드는 모두 테스트됨.  
  - findByTenantIdAndServiceId는 “존재 시 Optional 반환”만 테스트되어 있고, “미존재 시 Optional.empty()”는 엣지 케이스로 분류(아래).

### 3.4 MISSING_EDGE_CASE (우선순위: MED 1건, LOW 2건) — ✅ 완료

- ✅ **TenantServiceReadManagerTest – findByTenantIdAndServiceId (LOW)**  
  - 보완: `shouldReturnEmptyOptional_WhenNotExists` 추가. Port가 `Optional.empty()` 반환 시 빈 Optional 검증.

- ✅ **TenantServiceQueryFactoryTest – toCriteria (LOW)**  
  - 보완: `shouldCreateCriteriaWithEmptyStatuses_WhenStatusesIsNull` 추가. `TenantServiceSearchParams.of(..., null)`로 toCriteria 호출 시 `result.statuses()` 빈 목록 검증.

- ✅ **UpdateTenantServiceStatusServiceTest – execute (MED)**  
  - 보완: `shouldThrowException_WhenStatusIsInvalid` 추가. 잘못된 status 문자열 시 `IllegalArgumentException` 및 validator/commandManager 미호출 검증.

### 3.5 MISSING_STATE_TRANSITION

- 해당 없음. tenantservice 애플리케이션 레이어는 상태 기계가 없고 오케스트레이션 위주.

### 3.6 PATTERN_VIOLATION

- **없음.**  
  - `@Tag("unit")`, `@ExtendWith(MockitoExtension.class)`, `@Nested`/`@DisplayName` 사용.  
  - BDDMockito(given/then) 및 Given-When-Then 구조 일관.  
  - testFixtures 사용 방식과 네이밍이 프로젝트 컨벤션과 일치.

---

## 4. 우선순위별 권장 조치

| 우선순위 | 유형 | 대상 | 조치 | 완료 |
|----------|------|------|------|------|
| **MED** | MISSING_EDGE_CASE | UpdateTenantServiceStatusServiceTest | 잘못된 status 문자열 시 `IllegalArgumentException` 테스트 1개 추가 | ✅ |
| **LOW** | MISSING_EDGE_CASE | TenantServiceReadManagerTest | `findByTenantIdAndServiceId` → Optional.empty() 반환 테스트 1개 추가 | ✅ |
| **LOW** | MISSING_EDGE_CASE | TenantServiceQueryFactoryTest | `toCriteria`에 null statuses 파라미터로 호출 시 빈 statuses 반환 검증 1개 추가 | ✅ |

---

## 5. 권장 조치 상세

### 5.1 UpdateTenantServiceStatusServiceTest 보강 (MED)

**파일:** `application/src/test/java/com/ryuqq/authhub/application/tenantservice/service/command/UpdateTenantServiceStatusServiceTest.java`

- **execute** Nested 하에:
  - DisplayName: "실패: 잘못된 status 문자열이면 IllegalArgumentException 발생"
  - `UpdateTenantServiceStatusCommand`에 존재하지 않는 status(예: `"INVALID"`)로 command 생성.
  - `sut.execute(command)` 호출 시 `assertThatThrownBy(...).isInstanceOf(IllegalArgumentException.class)`.
  - (선택) `then(commandManager).should(never()).persist(any())`로 영속화 미호출 검증.

### 5.2 TenantServiceReadManagerTest 보강 (LOW)

**파일:** `application/src/test/java/com/ryuqq/authhub/application/tenantservice/manager/TenantServiceReadManagerTest.java`

- **findByTenantIdAndServiceId** Nested 하에:
  - DisplayName: "존재하지 않으면 빈 Optional 반환"
  - `given(queryPort.findByTenantIdAndServiceId(tenantId, serviceId)).willReturn(Optional.empty())`
  - `Optional<TenantService> result = sut.findByTenantIdAndServiceId(tenantId, serviceId)`
  - `assertThat(result).isEmpty()`

### 5.3 TenantServiceQueryFactoryTest 보강 (LOW)

**파일:** `application/src/test/java/com/ryuqq/authhub/application/tenantservice/factory/TenantServiceQueryFactoryTest.java`

- **toCriteria** Nested 하에:
  - DisplayName: "statuses가 null인 경우 빈 목록으로 Criteria 생성"
  - `TenantServiceSearchParams`를 null statuses로 생성 (필요 시 `TenantServiceQueryFixtures`에 `searchParamsWithNullStatuses()` 추가 또는 기존 of 활용).
  - `TenantServiceSearchCriteria result = sut.toCriteria(params)`
  - `assertThat(result.statuses()).isEmpty()`

---

## 6. 참고

- Port(in/out), DTO(record)는 단위 테스트 생략이 프로젝트 관례임.
- 우선순위: 커버리지 갭(메서드/엣지) → 클래스 역할(Service/Manager HIGH) → 복잡도 기준으로 MED 1건, LOW 2건으로 정리함.

---

## 7. 완료 내역

| 완료일 | 조치 |
|--------|------|
| 2025-02-05 | MED: UpdateTenantServiceStatusServiceTest — `shouldThrowException_WhenStatusIsInvalid` 추가 |
| 2025-02-05 | LOW: TenantServiceReadManagerTest — `shouldReturnEmptyOptional_WhenNotExists` 추가 |
| 2025-02-05 | LOW: TenantServiceQueryFactoryTest — `shouldCreateCriteriaWithEmptyStatuses_WhenStatusesIsNull` 추가 |

**최종 상태:** MED 1건, LOW 2건 모두 보완 완료. 남은 우선순위 이슈 없음.
