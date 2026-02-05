# Domain User 패키지 테스트 커버리지 감사 리포트

> **상태**: 🔶 **분석 완료** (보완 미진행)  
> **생성일**: 2026-02-05  
> **레이어**: Domain  
> **패키지**: `com.ryuqq.authhub.domain.user`

---

## 📊 요약

| 항목 | 수량 |
|------|------|
| 소스 클래스 | 17개 |
| 테스트 파일 | 1개 |
| Fixtures 파일 | 1개 ✅ |
| **총 갭** | **21개** |
| 🔴 HIGH | 9개 |
| 🟡 MEDIUM | 7개 |
| 🟢 LOW | 5개 |

**예상 보완 테스트 파일 수**: 14개 (신규) + 1개 (기존 보완)

---

## 📁 소스 클래스 목록

### Aggregate (2개)
| 클래스 | 테스트 | 상태 |
|--------|--------|------|
| User.java | ✅ UserTest.java | 보완 필요 |
| UserUpdateData.java | ❌ 없음 | MISSING_TEST |

### ID (1개)
| 클래스 | 테스트 | 상태 |
|--------|--------|------|
| UserId.java | ❌ 없음 | MISSING_TEST |

### VO (6개)
| 클래스 | 테스트 | 상태 |
|--------|--------|------|
| HashedPassword.java | ❌ 없음 | MISSING_TEST |
| Identifier.java | ❌ 없음 | MISSING_TEST |
| PhoneNumber.java | ❌ 없음 | MISSING_TEST |
| UserStatus.java | ❌ 없음 | MISSING_TEST |
| UserSearchField.java | ❌ 없음 | MISSING_TEST |
| UserSortKey.java | ❌ 없음 | MISSING_TEST |

### Query (1개)
| 클래스 | 테스트 | 상태 |
|--------|--------|------|
| UserSearchCriteria.java | ❌ 없음 | MISSING_TEST |

### Exception (6개)
| 클래스 | 테스트 | 상태 |
|--------|--------|------|
| UserErrorCode.java | ❌ 없음 | MISSING_TEST (LOW) |
| UserNotFoundException.java | ❌ 없음 | MISSING_TEST |
| DuplicateUserIdentifierException.java | ❌ 없음 | MISSING_TEST |
| DuplicateUserPhoneNumberException.java | ❌ 없음 | MISSING_TEST |
| InvalidPasswordException.java | ❌ 없음 | MISSING_TEST |
| UserNotActiveException.java | ❌ 없음 | MISSING_TEST |

---

## 🔍 상세 갭 분석

### 🔴 HIGH 우선순위

#### 1. MISSING_TEST: UserUpdateData
**클래스**: `UserUpdateData.java`  
**유형**: Record VO (UpdateData 패턴)  
**우선순위**: HIGH  
**이유**: User.update()에서 사용, Aggregate 수정 데이터 VO

**권장 조치**:
- `UserUpdateDataTest.java` 생성
- `of(PhoneNumber)` 팩토리 메서드 테스트
- `hasPhoneNumber()` 메서드 테스트 (null, 유효한 값)
- `hasAnyUpdate()` 메서드 테스트

---

#### 2. MISSING_TEST: UserId
**클래스**: `UserId.java`  
**유형**: ID VO (Record)  
**우선순위**: HIGH  
**이유**: Aggregate 식별자, 핵심 도메인 개념

**권장 조치**:
- `UserIdTest.java` 생성
- `forNew(String)` 팩토리 메서드 테스트
- `of(String)` 팩토리 메서드 테스트
- null/빈 값 검증 테스트 → IllegalArgumentException
- 동등성 테스트 (equals/hashCode)

---

#### 3. MISSING_TEST: HashedPassword
**클래스**: `HashedPassword.java`  
**유형**: VO (Record with validation)  
**우선순위**: HIGH  
**이유**: 비즈니스 규칙 포함 (null/blank 검증)

**권장 조치**:
- `HashedPasswordTest.java` 생성
- `of(String)` 팩토리 메서드 테스트
- null/빈 값 검증 테스트 → IllegalArgumentException
- 동등성 테스트

---

#### 4. MISSING_TEST: Identifier
**클래스**: `Identifier.java`  
**유형**: VO (Record with validation)  
**우선순위**: HIGH  
**이유**: 비즈니스 규칙 포함 (길이 4~100자, fromNullable)

**권장 조치**:
- `IdentifierTest.java` 생성
- `of(String)` 팩토리 메서드 테스트
- null/빈 값 검증 테스트
- `fromNullable(String)` 테스트 (null, 빈 문자열 → null)
- 경계값 테스트 (3자 → 예외, 4자 → 정상, 100자 → 정상, 101자 → 예외)

---

#### 5. MISSING_TEST: PhoneNumber
**클래스**: `PhoneNumber.java`  
**유형**: VO (Record with validation)  
**우선순위**: HIGH  
**이유**: 복잡한 비즈니스 규칙 (패턴, 길이, digitsOnly, fromNullable)

**권장 조치**:
- `PhoneNumberTest.java` 생성
- `of(String)` 팩토리 메서드 테스트
- null/빈 값 검증 테스트
- 패턴 검증 (숫자+하이픈만 허용)
- 길이 검증 (9자리 이하 → 예외, 10자리 → 정상, 21자 이상 → 예외)
- `fromNullable(String)` 테스트
- `digitsOnly()` 메서드 테스트

---

#### 6. MISSING_TEST: UserSearchCriteria
**클래스**: `UserSearchCriteria.java`  
**유형**: Query Criteria (Record)  
**우선순위**: HIGH  
**이유**: 복잡한 쿼리 로직 (편의 메서드 다수)

**권장 조치**:
- `UserSearchCriteriaTest.java` 생성
- `of()` 팩토리 메서드 테스트
- `ofDefault()` 팩토리 메서드 테스트
- `hasOrganizationFilter()` 메서드 테스트
- `hasSearchWord()` 메서드 테스트
- `hasStatusFilter()` 메서드 테스트
- `hasDateRange()` 메서드 테스트
- `offset()`, `size()`, `pageNumber()` 메서드 테스트
- `startInstant()`, `endInstant()` 메서드 테스트
- `sortKey()`, `sortDirection()` 메서드 테스트
- `includeDeleted()` 메서드 테스트

---

#### 7. MISSING_METHOD: User.reconstitute()
**클래스**: `User.java`  
**메서드**: `reconstitute(UserId, OrganizationId, Identifier, PhoneNumber, HashedPassword, UserStatus, DeletionStatus, Instant, Instant)`  
**우선순위**: HIGH  
**이유**: DB 재구성 메서드, 핵심 Aggregate 메서드

**권장 조치**:
- `UserTest`에 `@Nested ReconstituteTests` 그룹 추가
- 정상 재구성 테스트 (모든 필드 유효)
- null phoneNumber 재구성 테스트
- null status → ACTIVE 기본값 검증
- null deletionStatus → active() 기본값 검증
- 다양한 UserStatus (ACTIVE, INACTIVE, SUSPENDED) 재구성 테스트

---

#### 8. MISSING_EDGE_CASE: User.create() null 입력
**클래스**: `User.java`  
**시나리오**: create()에 null 전달 시 예외/동작 검증  
**우선순위**: HIGH  
**이유**: 방어적 검증 누락 가능성

**권장 조치**:
- `UserTest` CreateTests에 null 입력 시나리오 추가
- userId null, organizationId null, identifier null, hashedPassword null, now null 각각 검증
- (참고: VO들은 자체적으로 null 검증하여 NPE/IllegalArgumentException 발생)

---

#### 9. MISSING_STATE_TRANSITION: User.equals() userId null 케이스
**클래스**: `User.java`  
**시나리오**: equals()에서 userId가 null일 때 identifier + organizationId로 동등성 판단  
**우선순위**: HIGH  
**이유**: equals() 로직에 분기 존재, 현재 API로는 null userId User 생성 불가하나 코드 존재

**권장 조치**:
- `UserTest` EqualsHashCodeTests에 리플렉션 또는 테스트 전용 생성 경로로 null userId 케이스 검증
- 또는 equals 로직 문서화 후 LOW로 조정 (실제 발생 불가 시나리오)

---

### 🟡 MEDIUM 우선순위

#### 10. MISSING_TEST: UserStatus
**클래스**: `UserStatus.java`  
**유형**: Enum with methods  
**우선순위**: MEDIUM  
**이유**: 비즈니스 로직 포함 (isActive, canLogin, isSuspended)

**권장 조치**:
- `UserStatusTest.java` 생성
- `isActive()` 테스트 (ACTIVE→true, INACTIVE/SUSPENDED→false)
- `canLogin()` 테스트 (ACTIVE→true, 나머지→false)
- `isSuspended()` 테스트 (SUSPENDED→true, 나머지→false)

---

#### 11. MISSING_TEST: UserNotFoundException
**클래스**: `UserNotFoundException.java`  
**유형**: DomainException  
**우선순위**: MEDIUM  
**이유**: 핵심 비즈니스 예외, 여러 생성자 오버로드

**권장 조치**:
- `UserNotFoundExceptionTest.java` 생성
- 생성자 테스트 (String userId, UserId, Identifier)
- 에러 코드 USER_NOT_FOUND 검증
- 메시지 파라미터 검증

---

#### 12. MISSING_TEST: DuplicateUserIdentifierException
**클래스**: `DuplicateUserIdentifierException.java`  
**유형**: DomainException  
**우선순위**: MEDIUM  
**이유**: 사용자 생성 시 중복 검증 예외

**권장 조치**:
- `DuplicateUserIdentifierExceptionTest.java` 생성
- 생성자 테스트 (String, Identifier)
- 에러 코드 DUPLICATE_USER_IDENTIFIER 검증

---

#### 13. MISSING_TEST: DuplicateUserPhoneNumberException
**클래스**: `DuplicateUserPhoneNumberException.java`  
**유형**: DomainException  
**우선순위**: MEDIUM  
**이유**: 사용자 생성 시 전화번호 중복 검증 예외

**권장 조치**:
- `DuplicateUserPhoneNumberExceptionTest.java` 생성
- 생성자 테스트 (String, PhoneNumber)
- 에러 코드 DUPLICATE_USER_PHONE_NUMBER 검증

---

#### 14. MISSING_TEST: InvalidPasswordException
**클래스**: `InvalidPasswordException.java`  
**유형**: DomainException  
**우선순위**: MEDIUM  
**이유**: 로그인 시 비밀번호 검증 실패 예외

**권장 조치**:
- `InvalidPasswordExceptionTest.java` 생성
- 생성자 테스트 (무인자, String identifier)
- 에러 코드 INVALID_PASSWORD 검증

---

#### 15. MISSING_TEST: UserNotActiveException
**클래스**: `UserNotActiveException.java`  
**유형**: DomainException (resolveErrorCode 로직 포함)  
**우선순위**: MEDIUM  
**이유**: SUSPENDED→USER_SUSPENDED, INACTIVE→USER_NOT_ACTIVE 매핑 로직

**권장 조치**:
- `UserNotActiveExceptionTest.java` 생성
- 생성자 테스트 (String userId + UserStatus, UserId + UserStatus)
- SUSPENDED일 때 USER_SUSPENDED 에러 코드 검증
- INACTIVE일 때 USER_NOT_ACTIVE 에러 코드 검증
- ACTIVE일 때 USER_NOT_ACTIVE 검증 (default 분기)

---

#### 16. MISSING_EDGE_CASE: UserTest.isNew() 시나리오
**클래스**: `UserTest.java`  
**시나리오**: isNew()는 userId가 null일 때 true 반환. 현재 create/reconstitute는 항상 userId 전달하여 실제로는 false만 가능  
**우선순위**: MEDIUM  
**이유**: 테스트 주석에 "createNew는 이미 ID가 있으므로 isNew가 false"라고 명시되어 있으나, isNew() true 시나리오가 누락

**권장 조치**:
- 설계 상 userId null User가 생성 불가하다면, isNew() 메서드 제거 또는 문서화
- 또는 테스트에서 "isNew는 현재 User 생성 API상 항상 false"라고 명시적 테스트 추가

---

### 🟢 LOW 우선순위

#### 17. MISSING_TEST: UserSearchField
**클래스**: `UserSearchField.java`  
**유형**: Enum implementing SearchField  
**우선순위**: LOW  
**이유**: 단순 enum, fieldName() 구현만

**권장 조치**:
- `UserSearchFieldTest.java` 생성
- `fieldName()` 메서드 테스트 (IDENTIFIER→"identifier", PHONE_NUMBER→"phoneNumber")

---

#### 18. MISSING_TEST: UserSortKey
**클래스**: `UserSortKey.java`  
**유형**: Enum implementing SortKey  
**우선순위**: LOW  
**이유**: 단순 enum, fieldName() 구현만

**권장 조치**:
- `UserSortKeyTest.java` 생성
- `fieldName()` 메서드 테스트 (CREATED_AT, UPDATED_AT)

---

#### 19. MISSING_TEST: UserErrorCode
**클래스**: `UserErrorCode.java`  
**유형**: Enum implementing ErrorCode  
**우선순위**: LOW  
**이유**: ErrorCode 인터페이스 구현만, 비즈니스 로직 없음

**권장 조치**:
- `UserErrorCodeTest.java` 생성
- 각 에러 코드의 `getCode()`, `getHttpStatus()`, `getMessage()` 테스트

---

#### 20. PATTERN_VIOLATION: UserTest MockitoExtension
**클래스**: `UserTest.java`  
**이유**: Domain Aggregate 테스트는 Mock 없이 순수 단위 테스트  
**우선순위**: LOW  
**영향**: 작음 (MockitoExtension은 무해하지만 불필요)

**권장 조치**:
- `@ExtendWith(MockitoExtension.class)` 제거 (사용하지 않음)

---

#### 21. MISSING_METHOD: User getter/query 메서드 명시적 테스트
**클래스**: `User.java`  
**메서드**: getUserId, getOrganizationId, getIdentifier, getPhoneNumber, getHashedPassword, getStatus, getDeletionStatus, createdAt, updatedAt  
**우선순위**: LOW  
**이유**: 간접적으로 검증되나 명시적 테스트 없음

**권장 조치**:
- `UserTest` QueryMethodTests에 getter 반환값 검증 추가 (선택)

---

## 📋 권장 조치 요약표

| 우선순위 | 갭 유형 | 대상 클래스/메서드 | 조치 | 상태 |
|---------|---------|-------------------|------|------|
| 🔴 HIGH | MISSING_TEST | UserUpdateData | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_TEST | UserId | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_TEST | HashedPassword | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_TEST | Identifier | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_TEST | PhoneNumber | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_TEST | UserSearchCriteria | 테스트 파일 생성 | ⏳ |
| 🔴 HIGH | MISSING_METHOD | User.reconstitute() | 기존 테스트에 추가 | ⏳ |
| 🔴 HIGH | MISSING_EDGE_CASE | User.create() null 입력 | 기존 테스트에 추가 | ⏳ |
| 🔴 HIGH | MISSING_STATE_TRANSITION | User.equals() null userId | 문서화 또는 테스트 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | UserStatus | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | UserNotFoundException | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | DuplicateUserIdentifierException | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | DuplicateUserPhoneNumberException | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | InvalidPasswordException | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_TEST | UserNotActiveException | 테스트 파일 생성 | ⏳ |
| 🟡 MEDIUM | MISSING_EDGE_CASE | UserTest.isNew() | 문서화 또는 테스트 | ⏳ |
| 🟢 LOW | MISSING_TEST | UserSearchField | 테스트 파일 생성 | ⏳ |
| 🟢 LOW | MISSING_TEST | UserSortKey | 테스트 파일 생성 | ⏳ |
| 🟢 LOW | MISSING_TEST | UserErrorCode | 테스트 파일 생성 | ⏳ |
| 🟢 LOW | PATTERN_VIOLATION | UserTest MockitoExtension | 제거 | ⏳ |
| 🟢 LOW | MISSING_METHOD | User getters | 기존 테스트에 추가 (선택) | ⏳ |

---

## 🎯 다음 단계

1. **HIGH 우선순위 처리** (9개)
   - VO 테스트 파일 생성 (UserUpdateData, UserId, HashedPassword, Identifier, PhoneNumber)
   - Query Criteria 테스트 파일 생성 (UserSearchCriteria)
   - UserTest 보완 (reconstitute, create null 입력, equals 엣지케이스)

2. **MEDIUM 우선순위 처리** (7개)
   - Enum 테스트 파일 생성 (UserStatus)
   - Exception 테스트 파일 생성 (5개)
   - UserTest.isNew() 시나리오 문서화

3. **LOW 우선순위 처리** (5개)
   - Enum 테스트 파일 생성 (UserSearchField, UserSortKey, UserErrorCode)
   - UserTest MockitoExtension 제거
   - Getter 테스트 추가 (선택)

---

## 📝 참고사항

### Fixtures 현황
- ✅ `UserFixture.java` 존재 - create, createWithIdentifier, createWithStatus, createInactive, createSuspended, createDeleted, createNew, createWithoutPhone 등 풍부한 시나리오 제공

### 테스트 패턴
- Domain 레이어 테스트는 `@Tag("unit")` 사용
- `@DisplayName` 한글 사용
- `@Nested` 그룹으로 테스트 구조화
- UserFixture 활용

### Exception 테스트
- Exception 클래스는 DomainException 상속 검증
- 생성자별 에러 코드 및 메시지 파라미터 검증
- UserNotActiveException은 resolveErrorCode 로직 검증 필요

---

**리포트 생성일**: 2026-02-05
