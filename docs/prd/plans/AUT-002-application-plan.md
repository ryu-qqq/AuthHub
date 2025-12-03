# AUT-002 Application Layer TDD Plan

**Task**: Application Layer 구현
**Layer**: Application Layer
**Branch**: `feature/AUT-002-application-layer`
**Jira Issue**: [AUT-75](https://ryuqqq.atlassian.net/browse/AUT-75)
**예상 소요 시간**: 약 6시간 (24개 UseCase × 15분)

---

## Zero-Tolerance Rules

- **Transaction 경계**: `@Transactional` 내 외부 API 호출 절대 금지
- **CQRS 분리**: Command/Query UseCase 명확히 분리
- **Assembler 사용**: Domain <-> DTO 변환은 Assembler에서만
- **Port 인터페이스**: 모든 외부 의존성은 Port로 추상화
- **Domain 객체 직접 반환 금지**: 항상 Response DTO로 변환

---

## TDD Cycles

### Phase 1: Port Interfaces (기반 구조)

---

### 1-1. User Command Ports (Cycle 1) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `UserPersistencePortTest.java` 생성 (Port 인터페이스 설계 검증)
- [x] Port 인터페이스 시그니처 정의 (persist(User) → UserId)
- [x] 커밋: `test: UserPersistencePort 인터페이스 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `port/out/command/UserPersistencePort.java` 생성
- [x] 커밋: `feat: UserPersistencePort 인터페이스 정의 (Green)`

#### Refactor: 구조 개선
- [x] ArchUnit 테스트 통과 (PersistencePortArchTest)
- [x] PasswordHasherPort 패키지 재구성 (common.port.out.security)
- [x] 커밋: `struct: PasswordHasherPort 패키지 재구성 (ArchUnit 준수)`

**Note**: 코딩 컨벤션에 따라 `SaveUserPort`/`DeleteUserPort` 대신 `UserPersistencePort` 단일 인터페이스로 구현 (persist() 메서드로 INSERT/UPDATE 통합)

---

### 1-2. User Query Ports (Cycle 2) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `UserQueryPortTest.java` 생성 (Port 인터페이스 설계 검증)
- [x] 커밋: `test: UserQueryPort 인터페이스 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `port/out/query/UserQueryPort.java` 생성
- [x] 커밋: `feat: UserQueryPort 인터페이스 정의 (Green)`

#### Refactor: 구조 개선
- [x] Optional 반환 타입 확인 (findById → Optional<User>)
- [x] ArchUnit QueryPortArchTest 통과 확인
- [x] 구조 개선 불필요 (이미 최적)

**Note**: 코딩 컨벤션에 따라 `LoadUserPort`/`CheckDuplicateEmailPort` 등 개별 포트 대신 `UserQueryPort` 단일 인터페이스로 구현 (findById, existsById 메서드로 통합). 중복 체크는 별도 UseCase에서 existsById 활용.

---

### 1-3. Tenant/Organization Ports (Cycle 3) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `TenantPersistencePortTest.java` 생성
- [x] `TenantQueryPortTest.java` 생성
- [x] `OrganizationPersistencePortTest.java` 생성
- [x] `OrganizationQueryPortTest.java` 생성
- [x] 커밋: `test: Tenant/Organization Port 인터페이스 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `tenant/port/out/command/TenantPersistencePort.java` 생성
- [x] `tenant/port/out/query/TenantQueryPort.java` 생성
- [x] `organization/port/out/command/OrganizationPersistencePort.java` 생성
- [x] `organization/port/out/query/OrganizationQueryPort.java` 생성
- [x] 커밋: `feat: Tenant/Organization Port 인터페이스 정의 (Green)`

#### Refactor: 구조 개선
- [x] ArchUnit PersistencePortArchTest, QueryPortArchTest 통과 확인
- [x] 구조 개선 불필요 (이미 최적)

**Note**: 코딩 컨벤션에 따라 `LoadTenantPort`/`SaveTenantPort` 등 개별 포트 대신 단일 인터페이스로 구현:
- Command: `TenantPersistencePort`, `OrganizationPersistencePort` (persist 단일 메서드)
- Query: `TenantQueryPort`, `OrganizationQueryPort` (findById, existsById 메서드)
- `OrganizationQueryPort`에 `existsByTenantId(TenantId)` 추가 (참조 무결성 검증용)

---

### 1-4. Auth/Role Ports (Cycle 4) ✅ COMPLETED

#### Red: 테스트 작성
- [x] Port 인터페이스 시그니처 테스트
- [x] 커밋: `test: Auth/Role Port 인터페이스 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `port/out/command/RefreshTokenPersistencePort.java` 생성
- [x] `port/out/command/UserRolePersistencePort.java` 생성
- [x] `port/out/query/RefreshTokenQueryPort.java` 생성
- [x] `port/out/query/UserRoleQueryPort.java` 생성
- [x] 커밋: `feat: Auth/Role Port 인터페이스 정의 (Green)`

#### Refactor: 구조 개선
- [x] ArchUnit QueryPort 규칙 수정 (Set 반환 타입 지원 추가)
- [x] 커밋: `struct: QueryPort ArchUnit 규칙에 Set 반환 타입 추가`

**Note**: Auth 도메인이 아직 존재하지 않아 String/Set<String> 타입 사용
- RefreshTokenPersistencePort: persist(UserId, String), deleteByUserId(UserId)
- RefreshTokenQueryPort: findByUserId(UserId) → Optional<String>, existsByUserId(UserId)
- UserRolePersistencePort: persist(UserId, Set<String>), deleteByUserId(UserId)
- UserRoleQueryPort: findByUserId(UserId) → Set<String>, existsByUserId(UserId)

---

### Phase 2: Command/Query DTOs

---

### 2-1. User Command DTOs (Cycle 5) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `CreateUserCommandTest.java` 생성
- [x] `UpdateUserCommandTest.java` 생성
- [x] `ChangePasswordCommandTest.java` 생성
- [x] Record 구조, 불변성, 프레임워크 독립성 검증 테스트
- [x] 커밋: `test: CreateUserCommand DTO 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `dto/command/CreateUserCommand.java` (Record)
- [x] `dto/command/UpdateUserCommand.java` (Record)
- [x] `dto/command/ChangePasswordCommand.java` (Record)
- [x] 커밋: `feat: User Command DTO 구현 (Green)`

#### Refactor: 구조 개선
- [x] DtoRecordArchTest 테스트 클래스 제외 설정
- [x] 커밋: `struct: DtoRecordArchTest에서 테스트 클래스 제외`
- [x] Record 불변성 확인 - 추가 구조 개선 불필요

**Note**: 순수 Java Record 사용, jakarta.validation 금지 규칙 준수

---

### 2-2. User Response DTOs (Cycle 6) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `UserResponseTest.java` 생성 (Record 구조, 필드, 보안, 불변성 검증)
- [x] `CreateUserResponseTest.java` 생성 (최소 응답 원칙 검증)
- [x] 커밋: `test: User Response DTO 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `dto/response/UserResponse.java` (Record) - 사용자 정보 응답
- [x] `dto/response/CreateUserResponse.java` (Record) - 생성 응답 (userId, createdAt)
- [x] 커밋: `feat: User Response DTO 구현 (Green)`

#### Refactor: 구조 개선
- [x] DtoRecordArchTest 비즈니스 메서드 규칙 개선 (Record accessor 허용)
- [x] 커밋: `struct: DtoRecordArchTest 비즈니스 메서드 규칙 개선`
- [x] 추가 구조 개선 불필요 (이미 최적)

**Note**: 민감 정보(password, credential) 제외, 최소 응답 원칙 준수

---

### 2-3. Auth DTOs (Cycle 7) ✅ COMPLETED

#### Red: 테스트 작성
- [x] `LoginCommandTest.java` 생성 (tenantId, identifier, password 검증)
- [x] `RefreshTokenCommandTest.java` 생성 (최소 필드 원칙 검증)
- [x] `LoginResponseTest.java` 생성 (토큰 정보 검증)
- [x] `TokenResponseTest.java` 생성 (갱신 응답 검증)
- [x] 커밋: `test: Auth DTO 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `dto/command/LoginCommand.java` (Record) - tenantId, identifier, password
- [x] `dto/command/RefreshTokenCommand.java` (Record) - refreshToken만
- [x] `dto/response/LoginResponse.java` (Record) - userId, tokens, expiresIn, tokenType
- [x] `dto/response/TokenResponse.java` (Record) - tokens, expiresIn, tokenType
- [x] 커밋: `feat: Auth DTO 구현 (Green)`

#### Refactor: 구조 개선
- [x] 추가 구조 개선 불필요 (이미 최적)

**Note**: TokenResponse는 LoginResponse보다 간소화 (userId 미포함)

---

### 2-4. Organization/Tenant DTOs (Cycle 8) ✅

#### Red: 테스트 작성
- [x] Command/Response DTO 테스트
- [x] 커밋: `test: Organization/Tenant DTO 설계 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `dto/command/CreateOrganizationCommand.java`
- [x] `dto/command/CreateTenantCommand.java`
- [x] `dto/response/OrganizationResponse.java`
- [x] `dto/response/TenantResponse.java`
- [x] 커밋: `feat: Organization/Tenant DTO 구현 (Green)`

#### Refactor: 구조 개선
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### Phase 3: Assemblers

---

### 3-1. UserAssembler (Cycle 9) ✅

#### Red: 테스트 작성
- [x] `UserAssemblerTest.java` 생성
- [x] `toResponse()` 변환 테스트
- [x] 커밋: `test: UserAssembler 변환 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `assembler/UserAssembler.java` 생성 (@Component)
- [x] Domain -> Response 변환 구현
- [x] 커밋: `feat: UserAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [x] VO value() 호출 방식 확인 - User에 Helper 메서드 이미 존재
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### 3-2. OrganizationAssembler (Cycle 10) ✅

#### Red: 테스트 작성
- [x] `OrganizationAssemblerTest.java` 생성
- [x] 커밋: `test: OrganizationAssembler 변환 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `assembler/OrganizationAssembler.java` 생성
- [x] 커밋: `feat: OrganizationAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### 3-3. TenantAssembler (Cycle 11) ✅

#### Red: 테스트 작성
- [x] `TenantAssemblerTest.java` 생성
- [x] 커밋: `test: TenantAssembler 변환 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `assembler/TenantAssembler.java` 생성
- [x] 커밋: `feat: TenantAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### Phase 4: User UseCases (7개)

---

### 4-1. CreateUserUseCase (Cycle 12) ✅

#### Red: 테스트 작성
- [x] `CreateUserServiceTest.java` 생성
- [x] Mock Port 준비 (TenantQueryPort, OrganizationQueryPort, PasswordHasherPort, UserPersistencePort)
- [x] `shouldCreateUserSuccessfully()` 작성
- [x] `shouldThrowWhenTenantNotFound()` 작성
- [x] `shouldThrowWhenOrganizationInactive()` 작성
- [x] 커밋: `test: CreateUserService 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `port/in/user/CreateUserUseCase.java` 인터페이스 생성
- [x] `service/user/CreateUserService.java` 구현
- [x] `@Transactional` 적용
- [x] Tenant/Organization 검증 로직
- [x] 비밀번호 해싱 (PasswordHasherPort)
- [x] 커밋: `feat: CreateUserService 구현 (Green)`

#### Refactor: 구조 개선
- [x] Transaction 경계 검증 (외부 API 호출 없음)
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### 4-2. UpdateUserUseCase (Cycle 13) ✅

#### Red: 테스트 작성
- [x] `UpdateUserServiceTest.java` 생성
- [x] `shouldUpdateUserSuccessfully()` 작성
- [x] `shouldThrowWhenUserNotFound()` 작성
- [x] `shouldThrowWhenUserDeleted()` 작성
- [x] `shouldOnlyUpdateProfileFields()` 작성
- [x] `shouldUpdatePartialFields()` 작성
- [x] null 검증 테스트 작성
- [x] 커밋: `test: UpdateUserService 테스트 추가 (Red)`

#### Green: 최소 구현
- [x] `port/in/user/UpdateUserUseCase.java` 인터페이스
- [x] `service/user/UpdateUserService.java` 구현
- [x] `User.updateProfile()` Domain 메서드 추가
- [x] `UserProfile.mergeWith()` 병합 메서드 추가
- [x] 커밋: `feat: UpdateUserService 구현 (Green)`

#### Refactor: 구조 개선
- [x] 추가 구조 개선 불필요 (이미 최적)

---

### 4-3. DeleteUserUseCase (Cycle 14)

#### Red: 테스트 작성
- [ ] `DeleteUserServiceTest.java` 생성
- [ ] Soft Delete 검증
- [ ] 커밋: `test: DeleteUserService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/user/DeleteUserUseCase.java`
- [ ] `service/user/DeleteUserService.java`
- [ ] 커밋: `feat: DeleteUserService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeleteUserService 구조 개선`

---

### 4-4. SuspendUserUseCase (Cycle 15)

#### Red: 테스트 작성
- [ ] `SuspendUserServiceTest.java` 생성
- [ ] 상태 변경 검증
- [ ] 커밋: `test: SuspendUserService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/user/SuspendUserUseCase.java`
- [ ] `service/user/SuspendUserService.java`
- [ ] 커밋: `feat: SuspendUserService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SuspendUserService 구조 개선`

---

### 4-5. ChangePasswordUseCase (Cycle 16)

#### Red: 테스트 작성
- [ ] `ChangePasswordServiceTest.java` 생성
- [ ] 현재 비밀번호 검증 테스트
- [ ] 커밋: `test: ChangePasswordService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/user/ChangePasswordUseCase.java`
- [ ] `service/user/ChangePasswordService.java`
- [ ] PasswordEncoder 활용
- [ ] 커밋: `feat: ChangePasswordService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: ChangePasswordService 구조 개선`

---

### 4-6. GetUserUseCase - Query (Cycle 17)

#### Red: 테스트 작성
- [ ] `GetUserServiceTest.java` 생성
- [ ] 커밋: `test: GetUserService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/user/GetUserUseCase.java`
- [ ] `service/user/GetUserService.java`
- [ ] `@Transactional(readOnly = true)` 적용
- [ ] UserAssembler 사용하여 Response 반환
- [ ] 커밋: `feat: GetUserService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetUserService 구조 개선`

---

### 4-7. SearchUsersUseCase - Query (Cycle 18)

#### Red: 테스트 작성
- [ ] `SearchUsersServiceTest.java` 생성
- [ ] 페이징 검증
- [ ] 커밋: `test: SearchUsersService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/user/SearchUsersUseCase.java`
- [ ] `service/user/SearchUsersService.java`
- [ ] `dto/query/SearchUsersQuery.java`
- [ ] 커밋: `feat: SearchUsersService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SearchUsersService 구조 개선`

---

### Phase 5: Organization UseCases (5개)

---

### 5-1. CreateOrganizationUseCase (Cycle 19)

#### Red: 테스트 작성
- [ ] `CreateOrganizationServiceTest.java` 생성
- [ ] Tenant 검증 테스트
- [ ] 커밋: `test: CreateOrganizationService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/organization/CreateOrganizationUseCase.java`
- [ ] `service/organization/CreateOrganizationService.java`
- [ ] 커밋: `feat: CreateOrganizationService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: CreateOrganizationService 구조 개선`

---

### 5-2. UpdateOrganizationUseCase (Cycle 20)

#### Red: 테스트 작성
- [ ] `UpdateOrganizationServiceTest.java` 생성
- [ ] 커밋: `test: UpdateOrganizationService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/organization/UpdateOrganizationUseCase.java`
- [ ] `service/organization/UpdateOrganizationService.java`
- [ ] 커밋: `feat: UpdateOrganizationService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: UpdateOrganizationService 구조 개선`

---

### 5-3. DeleteOrganizationUseCase (Cycle 21)

#### Red: 테스트 작성
- [ ] `DeleteOrganizationServiceTest.java` 생성
- [ ] 하위 사용자 존재 시 삭제 불가 테스트
- [ ] 커밋: `test: DeleteOrganizationService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/organization/DeleteOrganizationUseCase.java`
- [ ] `service/organization/DeleteOrganizationService.java`
- [ ] `port/out/query/CountUsersInOrganizationPort.java`
- [ ] 커밋: `feat: DeleteOrganizationService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeleteOrganizationService 구조 개선`

---

### 5-4. DeactivateOrganizationUseCase (Cycle 22)

#### Red: 테스트 작성
- [ ] `DeactivateOrganizationServiceTest.java` 생성
- [ ] 커밋: `test: DeactivateOrganizationService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/organization/DeactivateOrganizationUseCase.java`
- [ ] `service/organization/DeactivateOrganizationService.java`
- [ ] 커밋: `feat: DeactivateOrganizationService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeactivateOrganizationService 구조 개선`

---

### 5-5. GetOrganizationUseCase - Query (Cycle 23)

#### Red: 테스트 작성
- [ ] `GetOrganizationServiceTest.java` 생성
- [ ] 커밋: `test: GetOrganizationService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/organization/GetOrganizationUseCase.java`
- [ ] `service/organization/GetOrganizationService.java`
- [ ] `@Transactional(readOnly = true)`
- [ ] 커밋: `feat: GetOrganizationService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetOrganizationService 구조 개선`

---

### Phase 6: Tenant UseCases (3개)

---

### 6-1. CreateTenantUseCase (Cycle 24)

#### Red: 테스트 작성
- [ ] `CreateTenantServiceTest.java` 생성
- [ ] 커밋: `test: CreateTenantService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/tenant/CreateTenantUseCase.java`
- [ ] `service/tenant/CreateTenantService.java`
- [ ] 커밋: `feat: CreateTenantService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: CreateTenantService 구조 개선`

---

### 6-2. UpdateTenantUseCase (Cycle 25)

#### Red: 테스트 작성
- [ ] `UpdateTenantServiceTest.java` 생성
- [ ] 커밋: `test: UpdateTenantService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/tenant/UpdateTenantUseCase.java`
- [ ] `service/tenant/UpdateTenantService.java`
- [ ] 커밋: `feat: UpdateTenantService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: UpdateTenantService 구조 개선`

---

### 6-3. SuspendTenantUseCase (Cycle 26)

#### Red: 테스트 작성
- [ ] `SuspendTenantServiceTest.java` 생성
- [ ] 커밋: `test: SuspendTenantService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/tenant/SuspendTenantUseCase.java`
- [ ] `service/tenant/SuspendTenantService.java`
- [ ] 커밋: `feat: SuspendTenantService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SuspendTenantService 구조 개선`

---

### Phase 7: Auth UseCases (5개)

---

### 7-1. LoginUseCase (Cycle 27)

#### Red: 테스트 작성
- [ ] `LoginServiceTest.java` 생성
- [ ] Mock: LoadUserPort, LoadTenantPort, LoadOrganizationPort, PasswordEncoder
- [ ] `shouldLoginSuccessfully()` 작성
- [ ] `shouldThrowWhenInvalidPassword()` 작성
- [ ] `shouldThrowWhenUserSuspended()` 작성
- [ ] `shouldThrowWhenTenantInactive()` 작성
- [ ] 커밋: `test: LoginService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/auth/LoginUseCase.java`
- [ ] `service/auth/LoginService.java`
- [ ] 사용자/Tenant/Organization 검증
- [ ] JWT Token 생성 (JwtTokenProvider Port 필요)
- [ ] RefreshToken 저장
- [ ] 커밋: `feat: LoginService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] Transaction 경계 검증
- [ ] 커밋: `struct: LoginService 구조 개선`

---

### 7-2. RefreshTokenUseCase (Cycle 28)

#### Red: 테스트 작성
- [ ] `RefreshTokenServiceTest.java` 생성
- [ ] RTR (Refresh Token Rotation) 테스트
- [ ] 커밋: `test: RefreshTokenService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/auth/RefreshTokenUseCase.java`
- [ ] `service/auth/RefreshTokenService.java`
- [ ] 기존 RefreshToken 무효화 + 새 Token 발급
- [ ] 커밋: `feat: RefreshTokenService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RefreshTokenService 구조 개선`

---

### 7-3. LogoutUseCase (Cycle 29)

#### Red: 테스트 작성
- [ ] `LogoutServiceTest.java` 생성
- [ ] RefreshToken 무효화 테스트
- [ ] 커밋: `test: LogoutService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/auth/LogoutUseCase.java`
- [ ] `service/auth/LogoutService.java`
- [ ] 커밋: `feat: LogoutService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: LogoutService 구조 개선`

---

### 7-4. ValidateTokenUseCase (Cycle 30)

#### Red: 테스트 작성
- [ ] `ValidateTokenServiceTest.java` 생성
- [ ] Gateway용 JWT 검증 테스트
- [ ] 커밋: `test: ValidateTokenService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/auth/ValidateTokenUseCase.java`
- [ ] `service/auth/ValidateTokenService.java`
- [ ] 커밋: `feat: ValidateTokenService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: ValidateTokenService 구조 개선`

---

### 7-5. RevokeAllTokensUseCase (Cycle 31)

#### Red: 테스트 작성
- [ ] `RevokeAllTokensServiceTest.java` 생성
- [ ] 특정 사용자의 모든 Token 무효화 테스트
- [ ] 커밋: `test: RevokeAllTokensService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/auth/RevokeAllTokensUseCase.java`
- [ ] `service/auth/RevokeAllTokensService.java`
- [ ] 커밋: `feat: RevokeAllTokensService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RevokeAllTokensService 구조 개선`

---

### Phase 8: Role & Permission UseCases (4개)

---

### 8-1. AssignRoleToUserUseCase (Cycle 32)

#### Red: 테스트 작성
- [ ] `AssignRoleToUserServiceTest.java` 생성
- [ ] 역할 할당 테스트
- [ ] 커밋: `test: AssignRoleToUserService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/role/AssignRoleToUserUseCase.java`
- [ ] `service/role/AssignRoleToUserService.java`
- [ ] UserRole 생성 및 저장
- [ ] 커밋: `feat: AssignRoleToUserService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: AssignRoleToUserService 구조 개선`

---

### 8-2. RevokeRoleFromUserUseCase (Cycle 33)

#### Red: 테스트 작성
- [ ] `RevokeRoleFromUserServiceTest.java` 생성
- [ ] 커밋: `test: RevokeRoleFromUserService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/role/RevokeRoleFromUserUseCase.java`
- [ ] `service/role/RevokeRoleFromUserService.java`
- [ ] 커밋: `feat: RevokeRoleFromUserService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RevokeRoleFromUserService 구조 개선`

---

### 8-3. GetUserPermissionsUseCase - Query (Cycle 34)

#### Red: 테스트 작성
- [ ] `GetUserPermissionsServiceTest.java` 생성
- [ ] 커밋: `test: GetUserPermissionsService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/role/GetUserPermissionsUseCase.java`
- [ ] `service/role/GetUserPermissionsService.java`
- [ ] `@Transactional(readOnly = true)`
- [ ] 커밋: `feat: GetUserPermissionsService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetUserPermissionsService 구조 개선`

---

### 8-4. GetRolePermissionsUseCase - Query (Cycle 35)

#### Red: 테스트 작성
- [ ] `GetRolePermissionsServiceTest.java` 생성
- [ ] 커밋: `test: GetRolePermissionsService 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/in/role/GetRolePermissionsUseCase.java`
- [ ] `service/role/GetRolePermissionsService.java`
- [ ] 커밋: `feat: GetRolePermissionsService 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetRolePermissionsService 구조 개선`

---

### Phase 9: ArchUnit Tests & Final Validation

---

### 9-1. Application ArchUnit Tests (Cycle 36)

#### Red: 테스트 작성
- [ ] `ApplicationArchTest.java` 생성
- [ ] UseCase는 adapter에 의존하지 않음
- [ ] Command UseCase는 @Transactional
- [ ] Query UseCase는 @Transactional(readOnly = true)
- [ ] Port는 interface여야 함
- [ ] 커밋: `test: Application ArchUnit 테스트 (Red)`

#### Green: 최소 구현
- [ ] 모든 ArchUnit 규칙 통과하도록 수정
- [ ] 커밋: `feat: Application ArchUnit 규칙 적용 (Green)`

#### Refactor: 구조 개선
- [ ] 전체 코드 리뷰
- [ ] 커밋: `struct: Application Layer 최종 구조 개선`

---

## Completion Criteria

- [ ] 35개 TDD 사이클 모두 완료
- [ ] 24개 UseCase 모두 구현 (18 Command + 6 Query)
- [ ] Port 인터페이스 모두 정의
- [ ] Assembler 모두 구현
- [ ] 모든 테스트 통과 (Unit Test 90% 이상)
- [ ] ArchUnit 검증 통과
- [ ] Zero-Tolerance Rules 준수

---

## Related Documents

- Task: docs/prd/tasks/AUT-002.md
- PRD: docs/prd/b2b-auth-hub.md
- Application Guide: docs/coding_convention/03-application-layer/
