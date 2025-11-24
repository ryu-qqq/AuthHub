# AUT-000 TDD Plan

**Task**: Domain Skeleton + Infrastructure 설정
**Feature**: 공통 기반 (Foundation)
**Layer**: Multi-Layer (Domain, Application, Persistence, REST API, Integration)
**브랜치**: feature/AUT-000-foundation
**예상 소요 시간**: 270분 (18 사이클 × 15분)

---

## 📝 TDD 사이클 체크리스트

### 🔵 Domain Layer (6 사이클)

---

### 1️⃣ User Aggregate - UserId VO 설계 (Cycle 1) [COMPLETED] ✅

#### 🔴 Red: 테스트 작성
- [x] `UserIdTest.java` 파일 생성
- [x] `shouldCreateUserIdWithValidUUID()` 작성
- [x] `shouldThrowExceptionWhenNullUUID()` 작성
- [x] 테스트 실행 → 컴파일 에러 확인
- [x] 커밋: `test: UserId VO 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [x] `UserId.java` 생성 (Record, UUID 기반)
- [x] Null 검증 로직 추가
- [x] 테스트 실행 → 통과 확인
- [x] 커밋: `feat: UserId VO 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [x] VO ArchUnit 테스트 추가 및 통과
- [x] Lombok 미사용 확인
- [x] 불변성 보장 확인 (Record 특성)
- [x] 커밋: `struct: UserId VO 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [x] `UserIdFixture.java` 생성 (Object Mother 패턴)
- [x] `UserIdFixture.aUserId()` 메서드 작성
- [x] `UserIdTest` → Fixture 사용으로 리팩토링
- [x] 테스트 여전히 통과 확인
- [x] 커밋: `test: UserIdFixture 정리 (Tidy)`

---

### 2️⃣ User Aggregate - User Entity 설계 (Cycle 2) [COMPLETED] ✅

#### 🔴 Red: 테스트 작성
- [x] `UserTest.java` 파일 생성
- [x] `shouldCreateUserWithValidData()` 작성
- [x] `shouldValidateTenantId()` 작성
- [x] `shouldValidateUserStatus()` 작성
- [x] 테스트 실행 → 컴파일 에러 확인
- [x] 커밋: `test: User Aggregate 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [x] `User.java` 생성 (Plain Java, Lombok 금지)
- [x] `UserType` Enum 생성 (PUBLIC, INTERNAL)
- [x] `UserStatus` Enum 생성 (ACTIVE, INACTIVE, SUSPENDED, DELETED)
- [x] 생성자 + Getter 작성 (final 필드)
- [x] Long FK 전략 (tenantId, organizationId)
- [x] 테스트 실행 → 통과 확인
- [x] 커밋: `feat: User Aggregate 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [x] Law of Demeter 준수 확인
- [x] Tell Don't Ask 패턴 확인
- [x] Aggregate ArchUnit 테스트 추가 및 통과
- [x] 커밋: `struct: User Aggregate 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [x] `UserFixture.java` 생성 (Object Mother 패턴)
- [x] `UserFixture.aUser()` 메서드 작성
- [x] `UserTest` → Fixture 사용으로 리팩토링
- [x] 테스트 여전히 통과 확인
- [x] 커밋: `test: UserFixture 정리 (Tidy)`

---

### 3️⃣ Tenant Aggregate 설계 (Cycle 3)

#### 🔴 Red: 테스트 작성
- [ ] `TenantIdTest.java`, `TenantNameTest.java` 생성
- [ ] `TenantTest.java` 생성
- [ ] `shouldCreateTenantWithValidData()` 작성
- [ ] `shouldValidateTenantName()` 작성 (2-100자 검증)
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: Tenant Aggregate 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `TenantId.java` 생성 (Record, Long 기반)
- [ ] `TenantName.java` 생성 (Record, 2-100자 검증)
- [ ] `TenantStatus` Enum 생성 (ACTIVE, INACTIVE, DELETED)
- [ ] `Tenant.java` 생성 (Plain Java)
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Tenant Aggregate 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] VO 검증 로직 리팩토링
- [ ] Aggregate ArchUnit 테스트 통과
- [ ] 커밋: `struct: Tenant Aggregate 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `TenantFixture.java` 생성
- [ ] `TenantIdFixture.aTenantId()` 추가
- [ ] `TenantNameFixture.aTenantName()` 추가
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: TenantFixture 정리 (Tidy)`

---

### 4️⃣ Organization Aggregate 설계 (Cycle 4)

#### 🔴 Red: 테스트 작성
- [ ] `OrganizationIdTest.java`, `OrganizationNameTest.java` 생성
- [ ] `OrganizationTest.java` 생성
- [ ] `shouldCreateOrganizationWithValidData()` 작성
- [ ] `shouldValidateOrganizationName()` 작성 (2-100자 검증)
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: Organization Aggregate 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `OrganizationId.java` 생성 (Record, Long 기반)
- [ ] `OrganizationName.java` 생성 (Record, 2-100자 검증)
- [ ] `OrganizationStatus` Enum 생성 (ACTIVE, INACTIVE, DELETED)
- [ ] `Organization.java` 생성 (Plain Java)
- [ ] Long FK 전략 (tenantId)
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Organization Aggregate 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] VO 검증 로직 리팩토링
- [ ] Aggregate ArchUnit 테스트 통과
- [ ] 커밋: `struct: Organization Aggregate 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `OrganizationFixture.java` 생성
- [ ] `OrganizationIdFixture`, `OrganizationNameFixture` 추가
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: OrganizationFixture 정리 (Tidy)`

---

### 5️⃣ Domain Exception 설계 (Cycle 5)

#### 🔴 Red: 테스트 작성
- [ ] `DomainExceptionTest.java` 생성
- [ ] `shouldCreateDomainException()` 작성
- [ ] `ErrorCode` Enum 테스트 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: DomainException 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `DomainException.java` 생성 (RuntimeException 상속)
- [ ] `ErrorCode` Enum 생성 (INVALID_INPUT, NOT_FOUND 등)
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: DomainException 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Exception ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: DomainException 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `DomainExceptionFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: DomainException Fixture 정리 (Tidy)`

---

### 6️⃣ ClockHolder 유틸리티 설계 (Cycle 6)

#### 🔴 Red: 테스트 작성
- [ ] `ClockHolderTest.java` 생성
- [ ] `shouldReturnCurrentTime()` 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: ClockHolder 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ClockHolder.java` 생성 (인터페이스)
- [ ] `SystemClockHolder.java` 구현체 생성
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: ClockHolder 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 테스트용 `FixedClockHolder` 추가
- [ ] 커밋: `struct: ClockHolder 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ClockHolderFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: ClockHolder Fixture 정리 (Tidy)`

---

### 🔵 Application Layer (1 사이클)

---

### 7️⃣ Port 인터페이스 패키지 구조 생성 (Cycle 7)

#### 🔴 Red: 테스트 작성
- [ ] `PortArchitectureTest.java` 생성 (ArchUnit)
- [ ] Port 인터페이스 패키지 구조 검증 테스트 작성
- [ ] Command/Query Port 네이밍 규칙 검증
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: Port 인터페이스 구조 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `port.in.command` 패키지 생성
- [ ] `port.in.query` 패키지 생성
- [ ] `port.out.command` 패키지 생성
- [ ] `port.out.query` 패키지 생성
- [ ] 각 패키지에 `package-info.java` 추가
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Port 인터페이스 패키지 구조 생성 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Port ArchUnit 테스트 강화
- [ ] 커밋: `struct: Port 구조 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (Port 인터페이스는 Fixture 불필요)
- [ ] 커밋: `test: Port 구조 테스트 정리 (Tidy)`

---

### 🔵 Persistence Layer (8 사이클)

---

### 8️⃣ BaseAuditEntity 설계 (Cycle 8)

#### 🔴 Red: 테스트 작성
- [ ] `BaseAuditEntityTest.java` 생성
- [ ] `shouldAutoPopulateAuditFields()` 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: BaseAuditEntity 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `BaseAuditEntity.java` 생성
- [ ] `@EntityListeners(AuditingEntityListener.class)` 추가
- [ ] `createdAt`, `updatedAt` 필드 추가
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: BaseAuditEntity 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] JPA Entity ArchUnit 테스트 추가 및 통과
- [ ] Lombok 미사용 확인
- [ ] 커밋: `struct: BaseAuditEntity 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (BaseEntity는 추상 클래스이므로 Fixture 불필요)
- [ ] 커밋: `test: BaseAuditEntity 테스트 정리 (Tidy)`

---

### 9️⃣ UserEntity 설계 (Cycle 9)

#### 🔴 Red: 테스트 작성
- [ ] `UserEntityTest.java` 생성
- [ ] `shouldMapToUser()` 작성 (Entity → Domain)
- [ ] `shouldCreateFromUser()` 작성 (Domain → Entity)
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: UserEntity 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `UserEntity.java` 생성
- [ ] Long FK 전략 (tenantId, organizationId)
- [ ] BaseAuditEntity 상속
- [ ] Lombok 금지 (Plain Java Getter/Setter)
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: UserEntity 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] JPA Entity ArchUnit 테스트 통과
- [ ] 커밋: `struct: UserEntity 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `UserEntityFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: UserEntityFixture 정리 (Tidy)`

---

### 🔟 TenantEntity & OrganizationEntity 설계 (Cycle 10)

#### 🔴 Red: 테스트 작성
- [ ] `TenantEntityTest.java`, `OrganizationEntityTest.java` 생성
- [ ] Entity ↔ Domain 매핑 테스트 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: TenantEntity, OrganizationEntity 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `TenantEntity.java` 생성 (Long FK, BaseAuditEntity 상속)
- [ ] `OrganizationEntity.java` 생성 (Long FK, BaseAuditEntity 상속)
- [ ] Lombok 금지
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: TenantEntity, OrganizationEntity 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] JPA Entity ArchUnit 테스트 통과
- [ ] 커밋: `struct: Entity 구조 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `TenantEntityFixture.java`, `OrganizationEntityFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: Entity Fixture 정리 (Tidy)`

---

### 1️⃣1️⃣ Flyway 마이그레이션 - V1__init_schema.sql (Cycle 11)

#### 🔴 Red: 테스트 작성
- [ ] `FlywayMigrationTest.java` 생성
- [ ] `shouldExecuteV1Migration()` 작성
- [ ] `shouldCreateTablesWithCorrectSchema()` 작성
- [ ] 테스트 실행 → 실패 확인 (SQL 없음)
- [ ] 커밋: `test: Flyway V1 마이그레이션 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `V1__init_schema.sql` 생성
- [ ] `tenants` 테이블 생성
- [ ] `organizations` 테이블 생성
- [ ] `users` 테이블 생성
- [ ] Index 및 Unique Constraint 추가
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Flyway V1 마이그레이션 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Flyway ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: Flyway V1 스키마 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (SQL 파일은 Fixture 불필요)
- [ ] 커밋: `test: Flyway V1 테스트 정리 (Tidy)`

---

### 1️⃣2️⃣ Flyway 마이그레이션 - V2__insert_default_data.sql (Cycle 12)

#### 🔴 Red: 테스트 작성
- [ ] `FlywayMigrationTest.java`에 테스트 추가
- [ ] `shouldInsertDefaultTenant()` 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: Flyway V2 기본 데이터 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `V2__insert_default_data.sql` 생성
- [ ] "Connectly Public Tenant" 삽입
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Flyway V2 기본 데이터 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 기본 데이터 검증 강화
- [ ] 커밋: `struct: Flyway V2 데이터 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (SQL 파일은 Fixture 불필요)
- [ ] 커밋: `test: Flyway V2 테스트 정리 (Tidy)`

---

### 1️⃣3️⃣ HikariCP 설정 (Cycle 13)

#### 🔴 Red: 테스트 작성
- [ ] `HikariCPConfigTest.java` 생성 (ArchUnit)
- [ ] HikariCP 설정 값 검증 테스트 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: HikariCP 설정 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `application.yml`에 HikariCP 설정 추가
  - maximumPoolSize: 10
  - minimumIdle: 5
  - connectionTimeout: 30000
  - idleTimeout: 600000
  - maxLifetime: 1800000
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: HikariCP 설정 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] HikariCP ArchUnit 테스트 통과
- [ ] 커밋: `struct: HikariCP 설정 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (설정 파일은 Fixture 불필요)
- [ ] 커밋: `test: HikariCP 테스트 정리 (Tidy)`

---

### 1️⃣4️⃣ Redis 설정 - RedisConfig (Cycle 14)

#### 🔴 Red: 테스트 작성
- [ ] `RedisConfigTest.java` 생성
- [ ] `shouldCreateRedisConnectionFactory()` 작성
- [ ] `shouldCreateRedisTemplate()` 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: RedisConfig 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `RedisConfig.java` 생성
- [ ] `RedisConnectionFactory` Bean 추가
- [ ] `RedisTemplate` Bean 추가
- [ ] `StringRedisTemplate` Bean 추가
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: RedisConfig 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Redis Config ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: RedisConfig 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (Config는 Fixture 불필요)
- [ ] 커밋: `test: RedisConfig 테스트 정리 (Tidy)`

---

### 1️⃣5️⃣ Redis application.yml 설정 (Cycle 15)

#### 🔴 Red: 테스트 작성
- [ ] `RedisConfigTest.java`에 테스트 추가
- [ ] Redis 연결 설정 값 검증 테스트 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: Redis application.yml 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `application.yml`에 Redis 설정 추가
  - host: localhost
  - port: 6379
  - timeout: 3000ms
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: Redis application.yml 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 환경별 설정 분리 (dev, test, prod)
- [ ] 커밋: `struct: Redis 설정 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] (설정 파일은 Fixture 불필요)
- [ ] 커밋: `test: Redis 설정 테스트 정리 (Tidy)`

---

### 🔵 REST API Layer (2 사이클)

---

### 1️⃣6️⃣ 공통 응답 DTO 설계 (Cycle 16)

#### 🔴 Red: 테스트 작성
- [ ] `ApiResponseTest.java` 생성
- [ ] `PageApiResponseTest.java` 생성
- [ ] `SliceApiResponseTest.java` 생성
- [ ] 응답 DTO 직렬화/역직렬화 테스트 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: 공통 응답 DTO 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ApiResponse<T>` Record 생성 (success, data, timestamp)
- [ ] `PageApiResponse<T>` Record 생성
- [ ] `SliceApiResponse<T>` Record 생성
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: 공통 응답 DTO 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Response DTO ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: 공통 응답 DTO 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ApiResponseFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: ApiResponse Fixture 정리 (Tidy)`

---

### 1️⃣7️⃣ 공통 에러 DTO 및 GlobalExceptionHandler 설계 (Cycle 17)

#### 🔴 Red: 테스트 작성
- [ ] `ErrorInfoTest.java` 생성
- [ ] `GlobalExceptionHandlerTest.java` 생성 (MockMvc 금지)
- [ ] 에러 응답 형식 검증 테스트 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: 에러 DTO 및 Handler 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ErrorInfo` Record 생성 (errorCode, message, details)
- [ ] `GlobalExceptionHandler` 생성
- [ ] `@RestControllerAdvice` 추가
- [ ] 기본 예외 핸들러 구현
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: 에러 DTO 및 Handler 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Error Handling ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: 에러 처리 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ErrorInfoFixture.java` 생성
- [ ] 테스트 → Fixture 사용
- [ ] 커밋: `test: ErrorInfo Fixture 정리 (Tidy)`

---

### 🔵 Integration Test (1 사이클)

---

### 1️⃣8️⃣ TestConfiguration 및 Flyway 통합 테스트 (Cycle 18)

#### 🔴 Red: 테스트 작성
- [ ] `FlywayIntegrationTest.java` 생성 (@SpringBootTest)
- [ ] `shouldExecuteFlywayMigrationsInOrder()` 작성
- [ ] `shouldCreateSchemaCorrectly()` 작성
- [ ] `shouldInsertDefaultData()` 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: Flyway 통합 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `TestConfiguration.java` 생성
- [ ] `ClockConfig` 추가 (고정된 시각)
- [ ] `EmbeddedRedis` 설정 추가 (테스트용)
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: TestConfiguration 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Integration Test ArchUnit 테스트 추가 및 통과
- [ ] 커밋: `struct: 통합 테스트 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] 통합 테스트용 Fixture 정리
- [ ] 커밋: `test: 통합 테스트 Fixture 정리 (Tidy)`

---

## ✅ 완료 조건

- [ ] 모든 TDD 사이클 완료 (18 사이클, 72개 체크박스 모두 ✅)
- [ ] Domain Layer 기본 구조 완료 (User, Tenant, Organization)
- [ ] Persistence Layer 기본 구조 완료 (Entity, Flyway)
- [ ] Infrastructure 설정 완료 (HikariCP, Redis)
- [ ] 공통 DTO 구조 완료
- [ ] 모든 테스트 통과 (Unit + ArchUnit + Flyway + Integration)
- [ ] Zero-Tolerance 규칙 준수 (Lombok 금지, Long FK 전략)
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 📊 메트릭

**예상 메트릭**:
- 총 사이클 수: 18
- 예상 소요 시간: 270분 (18 사이클 × 15분)
- Red 단계: 18개
- Green 단계: 18개
- Refactor 단계: 18개
- Tidy 단계: 18개
- 총 커밋 수: 72개 (test:, feat:, struct:, test: 각 18개)

**커밋 타입 비율 목표**:
- `test:` (Red + Tidy): 36개 (50%)
- `feat:` (Green): 18개 (25%)
- `struct:` (Refactor): 18개 (25%)

---

## 🔗 관련 문서

- **Task**: docs/prd/tasks/AUT-000.md
- **PRD**: docs/prd/iam-platform.md
- **Jira**: https://ryuqqq.atlassian.net/browse/AUT-53

---

## 🎯 핵심 원칙

1. **작은 단위**: 각 사이클은 5-15분 내 완료
2. **4단계 필수**: Red → Green → Refactor → Tidy 모두 수행
3. **TestFixture 필수**: Tidy 단계에서 Object Mother 패턴 적용
4. **Zero-Tolerance**: 각 Refactor 단계에서 ArchUnit 검증
5. **체크박스 추적**: `/kb/foundation/go` 명령이 Plan 파일을 읽고 진행 상황 추적
