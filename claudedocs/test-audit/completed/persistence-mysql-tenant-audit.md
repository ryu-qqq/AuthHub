# Persistence-Mysql Tenant 패키지 테스트 커버리지 감사 리포트

> **상태**: 🔍 **분석 완료**  
> **생성일**: 2026-02-05  
> **최종 업데이트**: 2026-02-05

**레이어**: Adapter-Out (Persistence-Mysql)  
**패키지**: `com.ryuqq.authhub.adapter.out.persistence.tenant`  
**분석 범위**: 전체 패키지

---

## 📊 요약

| 항목 | 수량 | 비율 | 상태 |
|------|------|------|------|
| **소스 클래스** | 7 | 100% | - |
| **테스트 파일** | 4 | 57% | ⚠️ (2개 누락) |
| **TestFixtures** | 1 | ✅ | ✅ |
| **커버리지 갭** | 2 | 29% | ⚠️ |

### 우선순위별 분류

- **HIGH**: 2개 ⚠️
- **MED**: 0개 ✅
- **LOW**: 0개 ✅

---

## 🔍 상세 분석

### ✅ 테스트 존재

#### 1. `TenantCommandAdapter` (Adapter)
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/adapter/TenantCommandAdapter.java`
- **테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/adapter/TenantCommandAdapterTest.java`
- **커버리지**: 양호
- **테스트된 메서드**:
  - ✅ `persist()` - Domain→Entity 변환, 저장, ID 반환, Mapper/Repository 위임 검증

#### 2. `TenantQueryAdapter` (Adapter)
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/adapter/TenantQueryAdapter.java`
- **테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/adapter/TenantQueryAdapterTest.java`
- **커버리지**: 양호
- **테스트된 메서드**:
  - ✅ `findById()` - Entity 조회 후 Domain 변환, 빈 Optional 반환, ID 추출
  - ✅ `existsById()` - 존재 여부 확인 (true/false)
  - ✅ `existsByName()` - 이름 존재 여부 확인
  - ✅ `existsByNameAndIdNot()` - 이름 중복 검증 (ID 제외)
  - ✅ `findAllByCriteria()` - 조건 검색, 빈 목록 반환
  - ✅ `countByCriteria()` - 조건 검색 개수

#### 3. `TenantJpaEntity` (JPA Entity)
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/entity/TenantJpaEntity.java`
- **테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/entity/TenantJpaEntityTest.java`
- **커버리지**: 양호
- **테스트된 메서드**:
  - ✅ `of()` - 정상 생성, deletedAt null/설정 시 상태 검증
  - ✅ `isActive()` / `isDeleted()` - SoftDeletableEntity 상속 검증
  - ✅ `getStatus()` - ACTIVE/INACTIVE 상태
  - ✅ `getCreatedAt()` / `getUpdatedAt()` - BaseAuditEntity 상속 검증

#### 4. `TenantJpaEntityMapper` (Mapper)
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/mapper/TenantJpaEntityMapper.java`
- **테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/mapper/TenantJpaEntityMapperTest.java`
- **커버리지**: 양호
- **테스트된 메서드**:
  - ✅ `toEntity()` - Domain→Entity 변환, 활성/삭제/비활성 상태 처리
  - ✅ `toDomain()` - Entity→Domain 변환, DeletionStatus 매핑
  - ✅ 양방향 변환 (Round Trip) - 데이터 보존 검증

---

### ⚠️ 테스트 누락 (HIGH 우선순위)

#### 1. `TenantConditionBuilder` (Condition Builder) ⚠️
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/condition/TenantConditionBuilder.java`
- **테스트 파일**: ❌ **없음**
- **우선순위**: **HIGH** ⚠️
- **이유**:
  - QueryDSL 조건 생성 로직 담당 (10개 public 메서드)
  - Service 패키지에 `ServiceConditionBuilderTest` 존재 → **패턴 일치 필요 (PATTERN_VIOLATION)**
  - null-safe 처리 검증 필수

- **누락된 메서드** (10개):
  - ❌ `buildCondition(TenantSearchCriteria)` - 복합 조건 BooleanBuilder 생성
  - ❌ `buildOrderSpecifier(TenantSearchCriteria)` - 정렬 조건 (CREATED_AT, UPDATED_AT × ASC/DESC)
  - ❌ `searchByField(TenantSearchField, String)` - NAME 필드 검색, null/blank 처리
  - ❌ `nameEquals(String)` - 이름 일치 조건, null 처리
  - ❌ `tenantIdEquals(String)` - ID 일치 조건, null 처리
  - ❌ `tenantIdNotEquals(String)` - ID 불일치 조건, null 처리
  - ❌ `createdAtGoe(Instant)` - 생성일시 이상, null 처리
  - ❌ `createdAtLoe(Instant)` - 생성일시 이하, null 처리
  - ❌ `statusIn(TenantSearchCriteria)` - 상태 목록 포함 조건

- **권장 테스트 시나리오**:
  - `buildCondition()` - 검색어/상태/날짜 범위 포함, null 필터 제외
  - `buildOrderSpecifier()` - CREATED_AT/UPDATED_AT × ASC/DESC, sortKey 기본값 처리
  - `searchByField()` - NAME 필드 검색, null/searchWord blank 반환
  - 각 조건 메서드별 null 입력 시 null 반환 검증

- **참고 패턴**:
  - `ServiceConditionBuilderTest.java` - 동일 구조의 ConditionBuilder 테스트

---

#### 2. `TenantQueryDslRepository` (QueryDSL Repository) ⚠️
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/repository/TenantQueryDslRepository.java`
- **테스트 파일**: ❌ **없음**
- **우선순위**: **HIGH** ⚠️
- **이유**:
  - QueryDSL 쿼리 로직 담당 (6개 public 메서드)
  - Repository 계층 핵심 컴포넌트
  - ConditionBuilder 위임 및 쿼리 실행 흐름 검증 필요

- **누락된 메서드** (6개):
  - ❌ `findByTenantId(String)` - ID로 단건 조회
  - ❌ `existsByName(String)` - 이름 존재 여부 확인
  - ❌ `existsByTenantId(String)` - ID 존재 여부 확인
  - ❌ `existsByNameAndIdNot(String, String)` - 이름 중복 검증 (ID 제외)
  - ❌ `findAllByCriteria(TenantSearchCriteria)` - 조건 검색 (페이징/정렬)
  - ❌ `countByCriteria(TenantSearchCriteria)` - 조건 검색 개수

- **권장 테스트 시나리오**:
  - JPAQueryFactory Mock + TenantConditionBuilder Mock
  - `findByTenantId()` - Optional 반환 검증
  - `exists*()` - true/false 반환 검증
  - `findAllByCriteria()` - 조건/페이징/정렬 적용, 빈 목록
  - `countByCriteria()` - count 반환, 0 처리
  - null 반환 시 Optional.empty(), count 0 처리 검증

- **참고 패턴**:
  - `TenantQueryAdapterTest`에서 Repository를 Mock으로 사용
  - persistence-mysql 내 다른 QueryDslRepository 테스트 부재 (신규 작성 필요)

---

### ✅ 테스트 불필요 (LOW 우선순위)

#### 3. `TenantJpaRepository` (Spring Data JPA Interface) ✅
- **파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/repository/TenantJpaRepository.java`
- **테스트 파일**: ❌ **없음** (정상)
- **우선순위**: **LOW** ✅
- **이유**:
  - Spring Data JPA 인터페이스 (메서드 구현 없음)
  - 프레임워크가 자동 생성
  - `TenantCommandAdapter` 테스트에서 간접 검증됨
  - 별도 테스트 불필요

---

## 🎯 권장 조치

### 즉시 조치 (HIGH 우선순위)

1. **TenantConditionBuilderTest 생성**
   - **파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/condition/TenantConditionBuilderTest.java`
   - **패턴**: `ServiceConditionBuilderTest.java` 참고
   - **테스트 메서드**:
     - `buildCondition()` - 전체 조건, null 필터 제외
     - `buildOrderSpecifier()` - CREATED_AT/UPDATED_AT × ASC/DESC, 기본값
     - `searchByField()` - NAME 검색, null/blank 반환
     - `nameEquals()` / `tenantIdEquals()` / `tenantIdNotEquals()` - null 처리
     - `createdAtGoe()` / `createdAtLoe()` - null 처리
     - `statusIn()` - hasStatusFilter 기반

2. **TenantQueryDslRepositoryTest 생성**
   - **파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/repository/TenantQueryDslRepositoryTest.java`
   - **패턴**: Mock JPAQueryFactory + TenantConditionBuilder
   - **테스트 메서드** (6개):
     - `findByTenantId()` - 조회 성공, Optional.empty
     - `existsByName()` - true/false
     - `existsByTenantId()` - true/false
     - `existsByNameAndIdNot()` - true/false
     - `findAllByCriteria()` - 목록 반환, 빈 목록
     - `countByCriteria()` - count 반환, 0 반환
   - **주의**: QueryDSL Mock 복잡도 고려, 통합 테스트 분리 검토

---

## 📋 테스트 패턴 체크리스트

### ✅ 준수 사항
- ✅ TestFixtures 존재 (`TenantJpaEntityFixture.java`)
- ✅ `@Tag("unit")` 사용
- ✅ `@DisplayName` 사용
- ✅ `@Nested` 클래스로 그룹화
- ✅ AssertJ 사용
- ✅ Given-When-Then 패턴
- ✅ Mockito 사용 (Adapter 테스트)
- ✅ Domain fixture 사용 (`TenantFixture`)

### ⚠️ 개선 필요
- ⚠️ **TenantConditionBuilderTest 없음** - Service 패키지와 패턴 불일치 (PATTERN_VIOLATION)
- ⚠️ **TenantQueryDslRepositoryTest 없음** - QueryDSL Repository 미검증

---

## 📈 커버리지 향상 계획

| 항목 | 현재 | 목표 | 향상 |
|------|------|------|------|
| **테스트 파일 수** | 4/6 | 6/6 | +50% |
| **Adapter 커버리지** | 100% | 100% | 유지 |
| **ConditionBuilder 커버리지** | 0% | 95%+ | +95%+ |
| **Repository 커버리지** | 0% | 90%+ | +90%+ |
| **Entity 커버리지** | 95%+ | 95%+ | 유지 |
| **Mapper 커버리지** | 95%+ | 95%+ | 유지 |

---

## 🔗 참고 자료

- **참고 테스트 패턴**:
  - `ServiceConditionBuilderTest.java` - ConditionBuilder 테스트 패턴
  - `TenantQueryAdapterTest.java` - Query Adapter 테스트 패턴
  - `TenantCommandAdapterTest.java` - Command Adapter 테스트 패턴

- **TestFixtures**: `TenantJpaEntityFixture.java` (존재)

- **Domain fixtures**: `TenantFixture` (domain 모듈)

---

## 📝 추가 고려사항

### TenantSearchCriteria Fixture

- Adapter 테스트에서 `createTestCriteria()` 헬퍼 사용
- `TenantSearchCriteria.ofSimple()` 호출로 criteria 생성
- 별도 `TenantSearchCriteriaFixture` 불필요 (Domain record, persistence 레이어에서 domain 의존)

### QueryDSL Repository 테스트 전략

1. **단위 테스트 (Mock 기반)**:
   - JPAQueryFactory, TenantConditionBuilder Mock
   - 쿼리 위임 및 반환값 검증

2. **통합 테스트 (선택)**:
   - Testcontainers + 실제 DB
   - QueryDSL 쿼리 실행 검증

---

**생성일**: 2026-02-05  
**다음 검토일**: 테스트 보완 완료 후
