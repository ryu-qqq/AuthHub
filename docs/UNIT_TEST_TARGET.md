# Unit Test Target List

> **문서 목적**: 단위 테스트가 필요한 클래스와 메서드를 우선순위별로 정리
> **생성일**: 2025-11-25
> **기준**: JaCoCo 커버리지 리포트 분석 결과

---

## 요약

| 레이어 | 전체 클래스 | 미테스트 클래스 | 커버리지 |
|--------|-------------|-----------------|----------|
| Domain | 26 | 0 (Aggregates) | ~100% |
| Application | 2 | 0 | 100% |
| REST API | 11 | 9 | ~18% |
| Persistence | 12 | 12 | 0% |

---

## 🔴 Priority 1: Critical (커버리지 0-30%)

### 1.1 Domain Layer - Aggregates

#### ✅ User Aggregate (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/user/aggregate/User.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `forNew(TenantId, OrganizationId, UserType, Clock)` | 신규 User 생성 팩토리 메서드 | ✅ 완료 |
| `of(UserId, TenantId, OrganizationId, UserType, UserStatus, Instant, Instant)` | 모든 필드 지정 팩토리 메서드 | ✅ 완료 |
| `reconstitute(UserId, TenantId, OrganizationId, UserType, UserStatus, Instant, Instant)` | DB에서 복원 팩토리 메서드 | ✅ 완료 |
| `activate(Clock)` | 사용자 활성화 | ✅ 완료 |
| `deactivate(Clock)` | 사용자 비활성화 | ✅ 완료 |
| `delete(Clock)` | 사용자 삭제 (소프트 삭제) | ✅ 완료 |
| `assignOrganization(OrganizationId, Clock)` | 조직 할당 | ✅ 완료 |
| `userIdValue()` | User ID UUID 값 반환 | ✅ 완료 |
| `tenantIdValue()` | Tenant ID Long 값 반환 | ✅ 완료 |
| `organizationIdValue()` | Organization ID Long 값 반환 | ✅ 완료 |
| `userTypeValue()` | UserType name 반환 | ✅ 완료 |
| `statusValue()` | UserStatus name 반환 | ✅ 완료 |
| `isNew()` | 새 User 여부 확인 | ✅ 완료 |
| `createdAt()` | 생성 시간 반환 | ✅ 완료 |
| `updatedAt()` | 수정 시간 반환 | ✅ 완료 |
| `isActive()` | 활성 상태 확인 | ✅ 완료 |
| `isDeleted()` | 삭제 상태 확인 | ✅ 완료 |
| `hasOrganization()` | Organization 소속 여부 확인 | ✅ 완료 |
| `getUserId()`, `getTenantId()`, `getOrganizationId()`, `getUserType()`, `getUserStatus()` | Legacy Getters | ✅ 완료 |
| `equals(Object)` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |
| `toString()` | 문자열 표현 | ✅ 완료 |

**테스트 파일**: 
- `domain/src/test/java/com/ryuqq/authhub/domain/user/UserTest.java` ✅ 완료
- `domain/src/test/java/com/ryuqq/authhub/domain/user/UserDebugTest.java` ✅ 완료 (통합 테스트)

**테스트 픽스처**: 
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/user/fixture/UserFixture.java` ✅ 리팩토링 완료 (Builder 패턴 추가)

**완료 일자**: 2025-11-25

---

#### ✅ Tenant Aggregate (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/tenant/aggregate/Tenant.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `forNew(TenantName, Clock)` | 신규 Tenant 생성 팩토리 메서드 | ✅ 완료 |
| `of(TenantId, TenantName, TenantStatus, Instant, Instant)` | 모든 필드 지정 팩토리 메서드 | ✅ 완료 |
| `reconstitute(TenantId, TenantName, TenantStatus, Instant, Instant)` | DB에서 복원 팩토리 메서드 | ✅ 완료 |
| `activate(Clock)` | 테넌트 활성화 | ✅ 완료 |
| `deactivate(Clock)` | 테넌트 비활성화 | ✅ 완료 |
| `delete(Clock)` | 테넌트 삭제 (소프트 삭제) | ✅ 완료 |
| `tenantIdValue()` | Tenant ID Long 값 반환 | ✅ 완료 |
| `tenantNameValue()` | Tenant Name String 값 반환 | ✅ 완료 |
| `statusValue()` | TenantStatus name 반환 | ✅ 완료 |
| `isNew()` | 새 Tenant 여부 확인 | ✅ 완료 |
| `createdAt()` | 생성 시간 반환 | ✅ 완료 |
| `updatedAt()` | 수정 시간 반환 | ✅ 완료 |
| `isActive()` | 활성 상태 확인 | ✅ 완료 |
| `isDeleted()` | 삭제 상태 확인 | ✅ 완료 |
| `getTenantId()`, `getTenantName()`, `getTenantStatus()` | Legacy Getters | ✅ 완료 |
| `equals(Object)` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |
| `toString()` | 문자열 표현 | ✅ 완료 |

**테스트 파일**: 
- `domain/src/test/java/com/ryuqq/authhub/domain/tenant/TenantTest.java` ✅ 완료
- `domain/src/test/java/com/ryuqq/authhub/domain/tenant/TenantDebugTest.java` ✅ 완료 (디버그 테스트)

**테스트 픽스처**: 
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/tenant/fixture/TenantFixture.java` ✅ 리팩토링 완료 (Builder 패턴 추가)

**완료 일자**: 2025-11-25

---

### ✅ 1.2 Application Layer (100% 커버리지) - 완료

#### ✅ SliceResponse (100% 커버리지) - 완료
**파일**: `application/src/main/java/com/ryuqq/authhub/application/common/dto/response/SliceResponse.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(List, int, boolean, String)` | 전체 파라미터 팩토리 | ✅ 완료 |
| `of(List, int, boolean)` | 커서 없는 팩토리 | ✅ 완료 |
| `empty(int)` | 빈 슬라이스 팩토리 | ✅ 완료 |

**테스트 파일**: `application/src/test/java/com/ryuqq/authhub/application/common/dto/response/SliceResponseTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ PageResponse (100% 커버리지) - 완료
**파일**: `application/src/main/java/com/ryuqq/authhub/application/common/dto/response/PageResponse.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(List, int, int, long, int, boolean, boolean)` | 전체 파라미터 팩토리 | ✅ 완료 |
| `empty(int, int)` | 빈 페이지 팩토리 | ✅ 완료 |

**테스트 파일**: `application/src/test/java/com/ryuqq/authhub/application/common/dto/response/PageResponseTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

### ✅ 1.3 REST API Layer (100% 커버리지) - 완료

#### ✅ GlobalExceptionHandler (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/controller/GlobalExceptionHandler.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `handleValidationException()` | MethodArgumentNotValidException 처리 | ✅ 완료 |
| `handleBindException()` | BindException 처리 | ✅ 완료 |
| `handleConstraintViolation()` | ConstraintViolationException 처리 | ✅ 완료 |
| `handleIllegalArgumentException()` | IllegalArgumentException 처리 | ✅ 완료 |
| `handleHttpMessageNotReadable()` | JSON 파싱 오류 처리 | ✅ 완료 |
| `handleTypeMismatch()` | 타입 불일치 처리 | ✅ 완료 |
| `handleMissingParam()` | 필수 파라미터 누락 처리 | ✅ 완료 |
| `handleNoResource()` | 리소스 없음 처리 | ✅ 완료 |
| `handleMethodNotAllowed()` | 메서드 불허 처리 | ✅ 완료 |
| `handleIllegalState()` | 잘못된 상태 처리 | ✅ 완료 |
| `handleGlobal()` | 전역 예외 처리 | ✅ 완료 |
| `handleDomain()` | 도메인 예외 처리 | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/controller/GlobalExceptionHandlerTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ ApiResponse (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/dto/ApiResponse.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `ofSuccess(T)` | 성공 응답 (데이터 포함) | ✅ 완료 |
| `ofSuccess()` | 성공 응답 (데이터 없음) | ✅ 완료 |
| `ofFailure(ErrorInfo)` | 실패 응답 (ErrorInfo) | ✅ 완료 |
| `ofFailure(String, String)` | 실패 응답 (코드, 메시지) | ✅ 완료 |
| `generateRequestId()` | 요청 ID 생성 | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/dto/ApiResponseTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ SliceApiResponse (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/dto/SliceApiResponse.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `from(SliceResponse)` | SliceResponse → SliceApiResponse | ✅ 완료 |
| `from(SliceResponse, Function)` | 변환 함수 포함 팩토리 | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/dto/SliceApiResponseTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ PageApiResponse (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/dto/PageApiResponse.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `from(PageResponse)` | PageResponse → PageApiResponse | ✅ 완료 |
| `from(PageResponse, Function)` | 변환 함수 포함 팩토리 | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/dto/PageApiResponseTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ ErrorInfo (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/dto/ErrorInfo.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `constructor(String, String)` | 생성자 (null 검증 포함) | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/dto/ErrorInfoTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ ErrorMapperRegistry (100% 커버리지) - 완료
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/common/error/ErrorMapperRegistry.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `map(DomainException, Locale)` | 도메인 예외 매핑 | ✅ 완료 |
| `defaultMapping(DomainException)` | 기본 매핑 | ✅ 완료 |

**테스트 파일**: `adapter-in/rest-api/src/test/java/com/ryuqq/authhub/adapter/in/rest/common/error/ErrorMapperRegistryTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

### ✅ 1.4 Persistence Layer (100% 커버리지) - 완료

#### JPA Entities

##### ✅ UserJpaEntity (100% 커버리지) - 완료
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/user/entity/UserJpaEntity.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(...)` | 팩토리 메서드 | ✅ 완료 |
| `getId()` | ID 반환 | ✅ 완료 |
| `getEmail()` | 이메일 반환 | ✅ 완료 |
| `getPassword()` | 비밀번호 반환 | ✅ 완료 |
| `getUsername()` | 사용자명 반환 | ✅ 완료 |
| `getOrganizationId()` | 조직 ID 반환 | ✅ 완료 |
| `getTenantId()` | 테넌트 ID 반환 | ✅ 완료 |
| `getUserType()` | 사용자 유형 반환 | ✅ 완료 |
| `getStatus()` | 상태 반환 | ✅ 완료 |

**테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/user/entity/UserJpaEntityTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

##### ✅ TenantJpaEntity (100% 커버리지) - 완료
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/tenant/entity/TenantJpaEntity.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(...)` | 팩토리 메서드 | ✅ 완료 |
| `getId()` | ID 반환 | ✅ 완료 |
| `getName()` | 이름 반환 | ✅ 완료 |
| `getDescription()` | 설명 반환 | ✅ 완료 |
| `getOrganizationId()` | 조직 ID 반환 | ✅ 완료 |
| `getStatus()` | 상태 반환 | ✅ 완료 |

**테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/tenant/entity/TenantJpaEntityTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

##### ✅ OrganizationJpaEntity (100% 커버리지) - 완료
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/organization/entity/OrganizationJpaEntity.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(...)` | 팩토리 메서드 | ✅ 완료 |
| `getId()` | ID 반환 | ✅ 완료 |
| `getName()` | 이름 반환 | ✅ 완료 |
| `getDescription()` | 설명 반환 | ✅ 완료 |
| `getStatus()` | 상태 반환 | ✅ 완료 |

**테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/organization/entity/OrganizationJpaEntityTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### Base Entities

##### ✅ BaseAuditEntity (100% 커버리지) - 완료
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/common/entity/BaseAuditEntity.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `constructor(LocalDateTime, LocalDateTime)` | 생성자 | ✅ 완료 |
| `getCreatedAt()` | 생성일시 반환 | ✅ 완료 |
| `getUpdatedAt()` | 수정일시 반환 | ✅ 완료 |

**테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/common/entity/BaseAuditEntityTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

##### ✅ SoftDeletableEntity (100% 커버리지) - 완료
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/common/entity/SoftDeletableEntity.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `constructor(LocalDateTime, LocalDateTime)` | 생성자 (삭제일 없음) | ✅ 완료 |
| `constructor(LocalDateTime, LocalDateTime, LocalDateTime)` | 생성자 (삭제일 포함) | ✅ 완료 |
| `getDeletedAt()` | 삭제일시 반환 | ✅ 완료 |
| `isDeleted()` | 삭제 여부 확인 | ✅ 완료 |
| `isActive()` | 활성 여부 확인 | ✅ 완료 |

**테스트 파일**: `adapter-out/persistence-mysql/src/test/java/com/ryuqq/authhub/adapter/out/persistence/common/entity/SoftDeletableEntityTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

## 🟡 Priority 2: Important (커버리지 30-70%)

### 2.1 Domain Layer - Aggregates

#### ✅ Organization Aggregate (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/organization/aggregate/Organization.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `forNew(OrganizationName, Clock)` | 신규 Organization 생성 팩토리 메서드 | ✅ 완료 |
| `of(OrganizationId, OrganizationName, OrganizationStatus, Instant, Instant)` | 모든 필드 지정 팩토리 메서드 | ✅ 완료 |
| `reconstitute(OrganizationId, OrganizationName, OrganizationStatus, Instant, Instant)` | DB에서 복원 팩토리 메서드 | ✅ 완료 |
| `activate(Clock)` | 조직 활성화 | ✅ 완료 |
| `deactivate(Clock)` | 조직 비활성화 | ✅ 완료 |
| `delete(Clock)` | 조직 삭제 (소프트 삭제) | ✅ 완료 |
| `organizationIdValue()` | Organization ID Long 값 반환 | ✅ 완료 |
| `organizationNameValue()` | Organization Name String 값 반환 | ✅ 완료 |
| `statusValue()` | OrganizationStatus name 반환 | ✅ 완료 |
| `isNew()` | 새 Organization 여부 확인 | ✅ 완료 |
| `createdAt()` | 생성 시간 반환 | ✅ 완료 |
| `updatedAt()` | 수정 시간 반환 | ✅ 완료 |
| `isActive()` | 활성 상태 확인 | ✅ 완료 |
| `isDeleted()` | 삭제 상태 확인 | ✅ 완료 |
| `getOrganizationId()`, `getOrganizationName()`, `getOrganizationStatus()` | Legacy Getters | ✅ 완료 |
| `equals(Object)` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |
| `toString()` | 문자열 표현 | ✅ 완료 |

**테스트 파일**: 
- `domain/src/test/java/com/ryuqq/authhub/domain/organization/OrganizationTest.java` ✅ 완료

**테스트 픽스처**: 
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/organization/fixture/OrganizationFixture.java` ✅ 리팩토링 완료 (Builder 패턴 추가)

**완료 일자**: 2025-11-25

---

### ✅ 2.2 Domain Layer - Identifiers (100% 커버리지) - 완료

#### ✅ UserId (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/user/identifier/UserId.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(UUID)` | UUID로 UserId 생성 | ✅ 완료 |
| `forNew()` | 신규 ID 생성 | ✅ 완료 |
| `isNew()` | 신규 여부 확인 | ✅ 완료 |
| `equals()` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |

**테스트 파일**: `domain/src/test/java/com/ryuqq/authhub/domain/user/identifier/UserIdTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ TenantId (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/tenant/identifier/TenantId.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(Long)` | Long으로 TenantId 생성 | ✅ 완료 |
| `forNew()` | 신규 ID 생성 | ✅ 완료 |
| `isNew()` | 신규 여부 확인 | ✅ 완료 |
| `equals()` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |

**테스트 파일**: `domain/src/test/java/com/ryuqq/authhub/domain/tenant/identifier/TenantIdTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

#### ✅ OrganizationId (100% 커버리지) - 완료
**파일**: `domain/src/main/java/com/ryuqq/authhub/domain/organization/identifier/OrganizationId.java`

| 메서드 | 설명 | 테스트 상태 |
|--------|------|-------------|
| `of(Long)` | Long으로 OrganizationId 생성 | ✅ 완료 |
| `forNew()` | 신규 ID 생성 | ✅ 완료 |
| `isNew()` | 신규 여부 확인 | ✅ 완료 |
| `equals()` | 동등성 비교 | ✅ 완료 |
| `hashCode()` | 해시코드 | ✅ 완료 |

**테스트 파일**: `domain/src/test/java/com/ryuqq/authhub/domain/organization/identifier/OrganizationIdTest.java` ✅ 완료

**완료 일자**: 2025-11-25

---

## 🟢 Priority 3: Nice to Have (커버리지 70%+)

### 3.1 Config Classes

#### ApiEndpointProperties
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/config/properties/ApiEndpointProperties.java`

| 메서드 | 설명 | 테스트 필요 사유 |
|--------|------|------------------|
| Getter/Setter 메서드들 | 프로퍼티 접근 | 설정값 검증 |

---

#### ApiErrorProperties
**파일**: `adapter-in/rest-api/src/main/java/com/ryuqq/authhub/adapter/in/rest/config/properties/ApiErrorProperties.java`

| 메서드 | 설명 | 테스트 필요 사유 |
|--------|------|------------------|
| `buildTypeUri(String)` | URI 빌드 | URI 생성 로직 |
| Getter/Setter 메서드들 | 프로퍼티 접근 | 설정값 검증 |

---

### 3.2 Persistence Config

#### FlywayConfig
**파일**: `adapter-out/persistence-mysql/src/main/java/com/ryuqq/authhub/adapter/out/persistence/config/FlywayConfig.java`

| 메서드 | 설명 | 테스트 필요 사유 |
|--------|------|------------------|
| `cleanMigrateStrategy()` | Clean + Migrate 전략 | 마이그레이션 전략 |
| `validateMigrateStrategy()` | Validate + Migrate 전략 | 마이그레이션 전략 |

---

## ✅ 완료된 테스트 (100% 커버리지)

### Domain Layer - Exceptions
- `DomainException` - 모든 메서드 테스트 완료
- `OrganizationErrorCode` - 모든 메서드 테스트 완료
- `OrganizationNotFoundException` - 모든 메서드 테스트 완료
- `InvalidOrganizationStateException` - 모든 메서드 테스트 완료
- `TenantErrorCode` - 모든 메서드 테스트 완료
- `TenantNotFoundException` - 모든 메서드 테스트 완료
- `InvalidTenantStateException` - 모든 메서드 테스트 완료
- `UserErrorCode` - 모든 메서드 테스트 완료
- `UserNotFoundException` - 모든 메서드 테스트 완료
- `InvalidUserStateException` - 모든 메서드 테스트 완료

### Domain Layer - Value Objects
- `OrganizationName` - 모든 메서드 테스트 완료
- `OrganizationStatus` - 모든 메서드 테스트 완료
- `TenantName` - 모든 메서드 테스트 완료
- `TenantStatus` - 모든 메서드 테스트 완료
- `UserStatus` - 모든 메서드 테스트 완료
- `UserType` - 모든 메서드 테스트 완료

---

## 테스트 작성 가이드

### TDD 사이클 준수
```
1. test: 실패하는 테스트 작성 → 커밋
2. feat: 테스트 통과 최소 구현 → 커밋
3. struct: 리팩토링 (필요시) → 커밋
```

### 테스트 네이밍 규칙
```java
@Test
@DisplayName("[메서드명] 조건_예상결과")
void methodName_condition_expectedResult() {
    // Given
    // When
    // Then
}
```

### 우선순위별 작업 순서
1. **Priority 1 (Critical)**: 반드시 AUT-000 이슈 완료 전 작성
2. **Priority 2 (Important)**: 다음 스프린트에서 작성
3. **Priority 3 (Nice to Have)**: 시간 여유 시 작성

---

## 진행 상황 추적

| 카테고리 | 전체 | 완료 | 진행률 |
|----------|------|------|--------|
| ✅ User Aggregate | 21 | 21 | 100% |
| ✅ Tenant Aggregate | 18 | 18 | 100% |
| ✅ Organization Aggregate | 18 | 18 | 100% |
| ✅ Application DTOs | 7 | 7 | 100% |
| ✅ GlobalExceptionHandler | 12 | 12 | 100% |
| ✅ ApiResponse | 5 | 5 | 100% |
| ✅ Identifier Classes | 6 | 6 | 100% |
| ✅ REST API DTOs | 10 | 10 | 100% |
| ✅ Persistence Entities | 26 | 26 | 100% |
| **Total** | **123** | **123** | **100%** |

---

> **Note**: 이 문서는 JaCoCo 커버리지 분석 결과를 기반으로 자동 생성되었습니다.
> 테스트 작성 완료 시 해당 항목을 업데이트해 주세요.
