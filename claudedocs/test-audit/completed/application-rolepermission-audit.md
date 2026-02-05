# Application Layer – rolepermission 패키지 테스트 감사

> **상태**: ✅ **완료**
> **보완 완료일**: 2025-02-05
> **최종 업데이트**: 2025-02-05

**대상:** `application` 레이어 `com.ryuqq.authhub.application.rolepermission`
**실행일:** 2025-02-05
**산출물:** 권장 조치 및 우선순위

---

## 1. 요약

| 항목 | 상태 |
|------|------|
| 소스 패키지 | `application/.../rolepermission` (assembler, dto, factory, internal, manager, port, service, validator) |
| 테스트 존재 | **2개 클래스 테스트 누락** (RolePermissionQueryFactory, GrantRolePermissionCoordinator) |
| testFixtures | `RolePermissionCommandFixtures` 존재 ✅ / **RolePermissionQueryFixtures 없음** |
| 메서드 누락 | RolePermissionReadManager: `countBySearchCriteria`, `findGrantedPermissionIds`, `findAllByRoleIds` 미테스트 |
| 엣지/경계 | Coordinator permissionIds null·empty, QueryFactory roleIds/permissionIds null·empty 시나리오 미보강 |
| 컨벤션 | tenant·tenantservice에 있는 Query Fixtures 패턴이 rolepermission에는 없음 (PATTERN_VIOLATION) |
| **HIGH 우선순위** | 2건 (테스트 클래스 신규) |
| **MED 우선순위** | 2건 (Manager 메서드 보강, Query Fixtures) |
| **LOW 우선순위** | 2건 (엣지 케이스, 패턴 정합성) |

---

## 2. 소스 vs 테스트 매트릭스

| 소스 클래스 | 테스트 클래스 | 비고 |
|-------------|----------------|------|
| **assembler** | | |
| RolePermissionAssembler | RolePermissionAssemblerTest | ✅ toResult, toResultList, toPageResult |
| **validator** | | |
| RolePermissionValidator | RolePermissionValidatorTest | ✅ 검증 시나리오 |
| **manager** | | |
| RolePermissionCommandManager | RolePermissionCommandManagerTest | ✅ persistAll 위임 |
| RolePermissionReadManager | RolePermissionReadManagerTest | ⚠️ exists, findAllByRoleId, existsByPermissionId, findAllBySearchCriteria만 테스트. **countBySearchCriteria, findGrantedPermissionIds, findAllByRoleIds 누락** |
| **factory** | | |
| RolePermissionCommandFactory | RolePermissionCommandFactoryTest | ✅ create, createAll |
| RolePermissionQueryFactory | — | ❌ **테스트 없음** (toCriteria) |
| **internal** | | |
| GrantRolePermissionCoordinator | — | ❌ **테스트 없음** (coordinate, filterNotGranted) |
| **service/command** | | |
| GrantRolePermissionService | GrantRolePermissionServiceTest | ✅ Coordinator·Manager 순서, 빈 목록 시 persist 미호출 |
| RevokeRolePermissionService | RevokeRolePermissionServiceTest | ✅ |
| **service/query** | | |
| SearchRolePermissionsService | SearchRolePermissionsServiceTest | ✅ Factory→Manager→Assembler, SearchParams 하드코딩 사용 |
| **port (in/out), dto** | — | 인터페이스/레코드로 단위 테스트 생략 (관례) |

---

## 3. 갭 유형별 분석

### 3.1 MISSING_TEST (우선순위: HIGH 2건)

| 클래스 | 역할 | public 메서드 | 권장 |
|--------|------|----------------|------|
| RolePermissionQueryFactory | Factory | `toCriteria(RolePermissionSearchParams)` | RolePermissionQueryFactoryTest 신규. toCriteria 기본/roleId·roleIds·permissionId·permissionIds null·빈 목록 변환 검증 |
| GrantRolePermissionCoordinator | Internal | `coordinate(Long roleId, List<Long> permissionIds)` | GrantRolePermissionCoordinatorTest 신규. RoleValidator·PermissionValidator·ReadManager·CommandFactory mock. 성공·permissionIds null/empty·RoleNotFound·Permission 미존재·이미 부여된 권한 필터링 후 빈 목록 반환 |

### 3.2 MISSING_FIXTURES (우선순위: MED)

- **RolePermissionQueryFixtures 없음.**
  - SearchRolePermissionsServiceTest에서 `RolePermissionSearchParams.of(CommonSearchParams.of(...), 1L, null, null, null)` 등 하드코딩 사용.
  - tenant·tenantservice는 Query Fixtures로 SearchParams·PageResult 등을 제공.
- **권장:** `RolePermissionQueryFixtures` 추가 (예: `searchParams()`, `searchParamsWithRoleId(Long)`, `searchParamsWithRoleIds(List<Long>)`, `defaultCommonSearchParams()`).

### 3.3 MISSING_METHOD (우선순위: MED)

- **RolePermissionReadManagerTest**
  - 다음 3개 메서드에 대한 테스트 없음:
    - `countBySearchCriteria(RolePermissionSearchCriteria)`
    - `findGrantedPermissionIds(RoleId, List<PermissionId>)`
    - `findAllByRoleIds(List<RoleId>)`
  - 권장: 메서드별 Nested + 성공/빈 결과 시나리오 추가.

### 3.4 MISSING_EDGE_CASE (우선순위: LOW)

- **RolePermissionQueryFactory:** roleId·roleIds·permissionId·permissionIds가 null이거나 빈 목록일 때 toCriteria 결과에서 null/빈 목록 처리 검증.
- **GrantRolePermissionCoordinator:** coordinate(roleId, null), coordinate(roleId, List.of()) 호출 시 빈 목록 반환 검증.

### 3.5 MISSING_STATE_TRANSITION

- 해당 없음. rolepermission 애플리케이션 레이어는 상태 기계 없이 오케스트레이션 위주.

### 3.6 PATTERN_VIOLATION (우선순위: LOW)

- **Query Fixtures 패턴 불일치:** 동일 레이어의 tenant·tenantservice는 Command + Query Fixtures 보유. rolepermission은 Command만 있고 Query Fixtures 없음.

---

## 4. 우선순위별 권장 조치

| 우선순위 | 유형 | 대상 | 조치 |
|----------|------|------|------|
| **HIGH** | MISSING_TEST | RolePermissionQueryFactory | RolePermissionQueryFactoryTest 신규. toCriteria(기본, roleId/roleIds/permissionId/permissionIds null·빈 목록) |
| **HIGH** | MISSING_TEST | GrantRolePermissionCoordinator | GrantRolePermissionCoordinatorTest 신규. coordinate 성공·permissionIds null/empty·RoleNotFound·Permission 미존재·이미 부여 필터링 |
| **MED** | MISSING_METHOD | RolePermissionReadManagerTest | countBySearchCriteria, findGrantedPermissionIds, findAllByRoleIds 테스트 추가 |
| **MED** | MISSING_FIXTURES | rolepermission testFixtures | RolePermissionQueryFixtures 추가 (searchParams 등), Query 관련 테스트에서 재사용 |
| **LOW** | MISSING_EDGE_CASE | RolePermissionQueryFactoryTest | toCriteria에 roleIds/permissionIds null·빈 목록 입력 시 처리 검증 |
| **LOW** | MISSING_EDGE_CASE | GrantRolePermissionCoordinatorTest | coordinate(roleId, null), coordinate(roleId, empty list) → 빈 목록 반환 검증 |

---

## 5. 권장 조치 상세

### 5.1 RolePermissionQueryFactoryTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/rolepermission/factory/RolePermissionQueryFactoryTest.java`
- **구성:**
  - Nested `ToCriteria`:
    - RolePermissionSearchParams로 toCriteria 호출 시 RolePermissionSearchCriteria 필드(roleId, roleIds, permissionId, permissionIds, queryContext) 검증.
    - roleId/roleIds/permissionId/permissionIds가 null 또는 빈 목록인 params로 호출 시 criteria 내 해당 필드 null 검증.
- **Fixture:** RolePermissionQueryFixtures 도입 시 searchParams(), searchParamsWithRoleId(Long) 등 사용 권장.

### 5.2 GrantRolePermissionCoordinatorTest 신규 (HIGH)

- **파일:** `application/src/test/java/com/ryuqq/authhub/application/rolepermission/internal/GrantRolePermissionCoordinatorTest.java`
- **구성:** RoleValidator, PermissionValidator, RolePermissionReadManager, RolePermissionCommandFactory mock.
  - **성공:** coordinate(roleId, permissionIds) → roleValidator.findExistingOrThrow, permissionValidator.validateAllExist, readManager.findGrantedPermissionIds, commandFactory.createAll 호출 순서·인자 검증, 반환 목록 검증.
  - **permissionIds null/empty:** 빈 목록 반환, validator/readManager/createAll 미호출(또는 최소 호출) 검증.
  - **Role 없음:** roleValidator.findExistingOrThrow에서 예외 시 전파.
  - **Permission 미존재:** permissionValidator.validateAllExist에서 예외 시 전파.
  - **이미 모두 부여:** findGrantedPermissionIds가 전부 반환 → createAll 미호출, 빈 목록 반환.
- **Fixture:** RolePermissionCommandFixtures.grantCommand(), domain RolePermissionFixture 활용.

### 5.3 RolePermissionReadManagerTest 보강 (MED)

- **countBySearchCriteria:** criteria 전달 시 queryPort.countBySearchCriteria 호출 및 반환값 검증.
- **findGrantedPermissionIds:** roleId, permissionIds 전달 시 queryPort.findGrantedPermissionIds 호출 및 반환 목록 검증.
- **findAllByRoleIds:** roleIds 전달 시 queryPort.findAllByRoleIds 호출 및 반환 목록 검증.

### 5.4 RolePermissionQueryFixtures 추가 (MED)

- **파일:** `application/src/testFixtures/java/com/ryuqq/authhub/application/rolepermission/fixture/RolePermissionQueryFixtures.java`
- **제공 메서드 예:**
  - `RolePermissionSearchParams searchParams()`
  - `RolePermissionSearchParams searchParamsWithRoleId(Long roleId)`
  - `RolePermissionSearchParams searchParamsWithRoleIds(List<Long> roleIds)`
  - `CommonSearchParams defaultCommonSearchParams()`
- tenant·tenantservice의 Query Fixtures 구조 참고.

---

## 6. 참고

- Port(in/out), DTO(record)는 단위 테스트 생략이 프로젝트 관례임.
- 우선순위: 커버리지 갭(테스트 없음 HIGH) → 메서드 누락(MED) → Fixtures/엣지/패턴(LOW).
- `/test-fix` 스킬로 HIGH 항목부터 자동 보완 가능.

---

## 7. 완료 내역

| 완료일 | 조치 |
|--------|------|
| 2025-02-05 | MED: RolePermissionQueryFixtures 추가 (searchParams, searchParamsWithRoleId/RoleIds/PermissionId/PermissionIds, defaultCommonSearchParams) |
| 2025-02-05 | HIGH: RolePermissionQueryFactoryTest 신규 (toCriteria 기본/roleId/roleIds/permissionId/permissionIds, null·빈 목록 엣지) |
| 2025-02-05 | HIGH: GrantRolePermissionCoordinatorTest 신규 (coordinate 성공, permissionIds null/empty, RoleNotFound, PermissionNotFound, 이미 모두 부여) |
| 2025-02-05 | MED: RolePermissionReadManagerTest 보강 (countBySearchCriteria, findGrantedPermissionIds, findAllByRoleIds) |
| 2025-02-05 | SearchRolePermissionsServiceTest에서 RolePermissionQueryFixtures 사용으로 변경 |

**최종 상태:** HIGH 2건, MED 2건, LOW 엣지 케이스 보완 완료.
