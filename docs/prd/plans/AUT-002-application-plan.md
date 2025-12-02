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

### 1-3. Tenant/Organization Ports (Cycle 3)

#### Red: 테스트 작성
- [ ] Port 인터페이스 시그니처 테스트
- [ ] 커밋: `test: Tenant/Organization Port 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/out/query/LoadTenantPort.java` 생성
- [ ] `port/out/query/LoadOrganizationPort.java` 생성
- [ ] `port/out/command/SaveTenantPort.java` 생성
- [ ] `port/out/command/SaveOrganizationPort.java` 생성
- [ ] 커밋: `feat: Tenant/Organization Port 정의 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: Tenant/Organization Port 구조 개선`

---

### 1-4. Auth/Role Ports (Cycle 4)

#### Red: 테스트 작성
- [ ] Port 인터페이스 시그니처 테스트
- [ ] 커밋: `test: Auth/Role Port 테스트 (Red)`

#### Green: 최소 구현
- [ ] `port/out/command/SaveRefreshTokenPort.java` 생성
- [ ] `port/out/command/DeleteRefreshTokenPort.java` 생성
- [ ] `port/out/query/LoadRefreshTokenPort.java` 생성
- [ ] `port/out/query/LoadUserRolesPort.java` 생성
- [ ] `port/out/command/SaveUserRolePort.java` 생성
- [ ] 커밋: `feat: Auth/Role Port 정의 (Green)`

#### Refactor: 구조 개선
- [ ] ArchUnit 전체 Port 검증
- [ ] 커밋: `struct: Auth/Role Port 구조 개선`

---

### Phase 2: Command/Query DTOs

---

### 2-1. User Command DTOs (Cycle 5)

#### Red: 테스트 작성
- [ ] `CreateUserCommandTest.java` 생성
- [ ] null 검증, 필수값 검증 테스트
- [ ] 커밋: `test: User Command DTO 테스트 (Red)`

#### Green: 최소 구현
- [ ] `dto/command/CreateUserCommand.java` (Record)
- [ ] `dto/command/UpdateUserCommand.java` (Record)
- [ ] `dto/command/ChangePasswordCommand.java` (Record)
- [ ] 커밋: `feat: User Command DTO 구현 (Green)`

#### Refactor: 구조 개선
- [ ] Record 불변성 확인
- [ ] 커밋: `struct: User Command DTO 구조 개선`

---

### 2-2. User Response DTOs (Cycle 6)

#### Red: 테스트 작성
- [ ] `UserResponseTest.java` 생성
- [ ] 커밋: `test: User Response DTO 테스트 (Red)`

#### Green: 최소 구현
- [ ] `dto/response/UserResponse.java` (Record)
- [ ] `dto/response/CreateUserResponse.java` (Record)
- [ ] 커밋: `feat: User Response DTO 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: User Response DTO 구조 개선`

---

### 2-3. Auth DTOs (Cycle 7)

#### Red: 테스트 작성
- [ ] `LoginCommandTest.java` 생성
- [ ] `LoginResponseTest.java` 생성
- [ ] 커밋: `test: Auth DTO 테스트 (Red)`

#### Green: 최소 구현
- [ ] `dto/command/LoginCommand.java` (Record)
- [ ] `dto/command/RefreshTokenCommand.java` (Record)
- [ ] `dto/response/LoginResponse.java` (Record)
- [ ] `dto/response/TokenResponse.java` (Record)
- [ ] 커밋: `feat: Auth DTO 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: Auth DTO 구조 개선`

---

### 2-4. Organization/Tenant DTOs (Cycle 8)

#### Red: 테스트 작성
- [ ] Command/Response DTO 테스트
- [ ] 커밋: `test: Organization/Tenant DTO 테스트 (Red)`

#### Green: 최소 구현
- [ ] `dto/command/CreateOrganizationCommand.java`
- [ ] `dto/command/CreateTenantCommand.java`
- [ ] `dto/response/OrganizationResponse.java`
- [ ] `dto/response/TenantResponse.java`
- [ ] 커밋: `feat: Organization/Tenant DTO 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: Organization/Tenant DTO 구조 개선`

---

### Phase 3: Assemblers

---

### 3-1. UserAssembler (Cycle 9)

#### Red: 테스트 작성
- [ ] `UserAssemblerTest.java` 생성
- [ ] `toResponse()` 변환 테스트
- [ ] 커밋: `test: UserAssembler 테스트 (Red)`

#### Green: 최소 구현
- [ ] `assembler/UserAssembler.java` 생성 (@Component)
- [ ] Domain -> Response 변환 구현
- [ ] 커밋: `feat: UserAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [ ] VO value() 호출 방식 확인
- [ ] 커밋: `struct: UserAssembler 구조 개선`

---

### 3-2. OrganizationAssembler (Cycle 10)

#### Red: 테스트 작성
- [ ] `OrganizationAssemblerTest.java` 생성
- [ ] 커밋: `test: OrganizationAssembler 테스트 (Red)`

#### Green: 최소 구현
- [ ] `assembler/OrganizationAssembler.java` 생성
- [ ] 커밋: `feat: OrganizationAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: OrganizationAssembler 구조 개선`

---

### 3-3. TenantAssembler (Cycle 11)

#### Red: 테스트 작성
- [ ] `TenantAssemblerTest.java` 생성
- [ ] 커밋: `test: TenantAssembler 테스트 (Red)`

#### Green: 최소 구현
- [ ] `assembler/TenantAssembler.java` 생성
- [ ] 커밋: `feat: TenantAssembler 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: TenantAssembler 구조 개선`

---

### Phase 4: User UseCases (7개)

---

### 4-1. CreateUserUseCase (Cycle 12)

#### Red: 테스트 작성
- [ ] `CreateUserUseCaseTest.java` 생성
- [ ] Mock Port 준비 (LoadTenantPort, LoadOrganizationPort, CheckDuplicate*, SaveUserPort)
- [ ] `shouldCreateUserSuccessfully()` 작성
- [ ] `shouldThrowWhenDuplicateUsername()` 작성
- [ ] `shouldThrowWhenOrganizationInactive()` 작성
- [ ] 커밋: `test: CreateUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/CreateUserUseCase.java` 인터페이스 생성
- [ ] `usecase/user/CreateUserUseCaseImpl.java` 구현
- [ ] `@Transactional` 적용
- [ ] Tenant/Organization 검증 로직
- [ ] 중복 체크 로직
- [ ] 커밋: `feat: CreateUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] Transaction 경계 검증 (외부 API 호출 없음)
- [ ] ArchUnit: UseCase는 adapter에 의존하지 않음
- [ ] 커밋: `struct: CreateUserUseCase 구조 개선`

---

### 4-2. UpdateUserUseCase (Cycle 13)

#### Red: 테스트 작성
- [ ] `UpdateUserUseCaseTest.java` 생성
- [ ] `shouldUpdateUserSuccessfully()` 작성
- [ ] `shouldThrowWhenUserNotFound()` 작성
- [ ] 커밋: `test: UpdateUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/UpdateUserUseCase.java` 인터페이스
- [ ] `usecase/user/UpdateUserUseCaseImpl.java` 구현
- [ ] 커밋: `feat: UpdateUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: UpdateUserUseCase 구조 개선`

---

### 4-3. DeleteUserUseCase (Cycle 14)

#### Red: 테스트 작성
- [ ] `DeleteUserUseCaseTest.java` 생성
- [ ] Soft Delete 검증
- [ ] 커밋: `test: DeleteUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/DeleteUserUseCase.java`
- [ ] `usecase/user/DeleteUserUseCaseImpl.java`
- [ ] 커밋: `feat: DeleteUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeleteUserUseCase 구조 개선`

---

### 4-4. SuspendUserUseCase (Cycle 15)

#### Red: 테스트 작성
- [ ] `SuspendUserUseCaseTest.java` 생성
- [ ] 상태 변경 검증
- [ ] 커밋: `test: SuspendUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/SuspendUserUseCase.java`
- [ ] `usecase/user/SuspendUserUseCaseImpl.java`
- [ ] 커밋: `feat: SuspendUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SuspendUserUseCase 구조 개선`

---

### 4-5. ChangePasswordUseCase (Cycle 16)

#### Red: 테스트 작성
- [ ] `ChangePasswordUseCaseTest.java` 생성
- [ ] 현재 비밀번호 검증 테스트
- [ ] 커밋: `test: ChangePasswordUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/ChangePasswordUseCase.java`
- [ ] `usecase/user/ChangePasswordUseCaseImpl.java`
- [ ] PasswordEncoder 활용
- [ ] 커밋: `feat: ChangePasswordUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: ChangePasswordUseCase 구조 개선`

---

### 4-6. GetUserUseCase - Query (Cycle 17)

#### Red: 테스트 작성
- [ ] `GetUserUseCaseTest.java` 생성
- [ ] 커밋: `test: GetUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/GetUserUseCase.java`
- [ ] `usecase/user/GetUserUseCaseImpl.java`
- [ ] `@Transactional(readOnly = true)` 적용
- [ ] UserAssembler 사용하여 Response 반환
- [ ] 커밋: `feat: GetUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetUserUseCase 구조 개선`

---

### 4-7. SearchUsersUseCase - Query (Cycle 18)

#### Red: 테스트 작성
- [ ] `SearchUsersUseCaseTest.java` 생성
- [ ] 페이징 검증
- [ ] 커밋: `test: SearchUsersUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/user/SearchUsersUseCase.java`
- [ ] `usecase/user/SearchUsersUseCaseImpl.java`
- [ ] `dto/query/SearchUsersQuery.java`
- [ ] 커밋: `feat: SearchUsersUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SearchUsersUseCase 구조 개선`

---

### Phase 5: Organization UseCases (5개)

---

### 5-1. CreateOrganizationUseCase (Cycle 19)

#### Red: 테스트 작성
- [ ] `CreateOrganizationUseCaseTest.java` 생성
- [ ] Tenant 검증 테스트
- [ ] 커밋: `test: CreateOrganizationUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/organization/CreateOrganizationUseCase.java`
- [ ] `usecase/organization/CreateOrganizationUseCaseImpl.java`
- [ ] 커밋: `feat: CreateOrganizationUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: CreateOrganizationUseCase 구조 개선`

---

### 5-2. UpdateOrganizationUseCase (Cycle 20)

#### Red: 테스트 작성
- [ ] `UpdateOrganizationUseCaseTest.java` 생성
- [ ] 커밋: `test: UpdateOrganizationUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/organization/UpdateOrganizationUseCase.java`
- [ ] `usecase/organization/UpdateOrganizationUseCaseImpl.java`
- [ ] 커밋: `feat: UpdateOrganizationUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: UpdateOrganizationUseCase 구조 개선`

---

### 5-3. DeleteOrganizationUseCase (Cycle 21)

#### Red: 테스트 작성
- [ ] `DeleteOrganizationUseCaseTest.java` 생성
- [ ] 하위 사용자 존재 시 삭제 불가 테스트
- [ ] 커밋: `test: DeleteOrganizationUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/organization/DeleteOrganizationUseCase.java`
- [ ] `usecase/organization/DeleteOrganizationUseCaseImpl.java`
- [ ] `port/out/query/CountUsersInOrganizationPort.java`
- [ ] 커밋: `feat: DeleteOrganizationUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeleteOrganizationUseCase 구조 개선`

---

### 5-4. DeactivateOrganizationUseCase (Cycle 22)

#### Red: 테스트 작성
- [ ] `DeactivateOrganizationUseCaseTest.java` 생성
- [ ] 커밋: `test: DeactivateOrganizationUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/organization/DeactivateOrganizationUseCase.java`
- [ ] `usecase/organization/DeactivateOrganizationUseCaseImpl.java`
- [ ] 커밋: `feat: DeactivateOrganizationUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: DeactivateOrganizationUseCase 구조 개선`

---

### 5-5. GetOrganizationUseCase - Query (Cycle 23)

#### Red: 테스트 작성
- [ ] `GetOrganizationUseCaseTest.java` 생성
- [ ] 커밋: `test: GetOrganizationUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/organization/GetOrganizationUseCase.java`
- [ ] `usecase/organization/GetOrganizationUseCaseImpl.java`
- [ ] `@Transactional(readOnly = true)`
- [ ] 커밋: `feat: GetOrganizationUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetOrganizationUseCase 구조 개선`

---

### Phase 6: Tenant UseCases (3개)

---

### 6-1. CreateTenantUseCase (Cycle 24)

#### Red: 테스트 작성
- [ ] `CreateTenantUseCaseTest.java` 생성
- [ ] 커밋: `test: CreateTenantUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/tenant/CreateTenantUseCase.java`
- [ ] `usecase/tenant/CreateTenantUseCaseImpl.java`
- [ ] 커밋: `feat: CreateTenantUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: CreateTenantUseCase 구조 개선`

---

### 6-2. UpdateTenantUseCase (Cycle 25)

#### Red: 테스트 작성
- [ ] `UpdateTenantUseCaseTest.java` 생성
- [ ] 커밋: `test: UpdateTenantUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/tenant/UpdateTenantUseCase.java`
- [ ] `usecase/tenant/UpdateTenantUseCaseImpl.java`
- [ ] 커밋: `feat: UpdateTenantUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: UpdateTenantUseCase 구조 개선`

---

### 6-3. SuspendTenantUseCase (Cycle 26)

#### Red: 테스트 작성
- [ ] `SuspendTenantUseCaseTest.java` 생성
- [ ] 커밋: `test: SuspendTenantUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/tenant/SuspendTenantUseCase.java`
- [ ] `usecase/tenant/SuspendTenantUseCaseImpl.java`
- [ ] 커밋: `feat: SuspendTenantUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: SuspendTenantUseCase 구조 개선`

---

### Phase 7: Auth UseCases (5개)

---

### 7-1. LoginUseCase (Cycle 27)

#### Red: 테스트 작성
- [ ] `LoginUseCaseTest.java` 생성
- [ ] Mock: LoadUserPort, LoadTenantPort, LoadOrganizationPort, PasswordEncoder
- [ ] `shouldLoginSuccessfully()` 작성
- [ ] `shouldThrowWhenInvalidPassword()` 작성
- [ ] `shouldThrowWhenUserSuspended()` 작성
- [ ] `shouldThrowWhenTenantInactive()` 작성
- [ ] 커밋: `test: LoginUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/auth/LoginUseCase.java`
- [ ] `usecase/auth/LoginUseCaseImpl.java`
- [ ] 사용자/Tenant/Organization 검증
- [ ] JWT Token 생성 (JwtTokenProvider Port 필요)
- [ ] RefreshToken 저장
- [ ] 커밋: `feat: LoginUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] Transaction 경계 검증
- [ ] 커밋: `struct: LoginUseCase 구조 개선`

---

### 7-2. RefreshTokenUseCase (Cycle 28)

#### Red: 테스트 작성
- [ ] `RefreshTokenUseCaseTest.java` 생성
- [ ] RTR (Refresh Token Rotation) 테스트
- [ ] 커밋: `test: RefreshTokenUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/auth/RefreshTokenUseCase.java`
- [ ] `usecase/auth/RefreshTokenUseCaseImpl.java`
- [ ] 기존 RefreshToken 무효화 + 새 Token 발급
- [ ] 커밋: `feat: RefreshTokenUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RefreshTokenUseCase 구조 개선`

---

### 7-3. LogoutUseCase (Cycle 29)

#### Red: 테스트 작성
- [ ] `LogoutUseCaseTest.java` 생성
- [ ] RefreshToken 무효화 테스트
- [ ] 커밋: `test: LogoutUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/auth/LogoutUseCase.java`
- [ ] `usecase/auth/LogoutUseCaseImpl.java`
- [ ] 커밋: `feat: LogoutUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: LogoutUseCase 구조 개선`

---

### 7-4. ValidateTokenUseCase (Cycle 30)

#### Red: 테스트 작성
- [ ] `ValidateTokenUseCaseTest.java` 생성
- [ ] Gateway용 JWT 검증 테스트
- [ ] 커밋: `test: ValidateTokenUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/auth/ValidateTokenUseCase.java`
- [ ] `usecase/auth/ValidateTokenUseCaseImpl.java`
- [ ] 커밋: `feat: ValidateTokenUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: ValidateTokenUseCase 구조 개선`

---

### 7-5. RevokeAllTokensUseCase (Cycle 31)

#### Red: 테스트 작성
- [ ] `RevokeAllTokensUseCaseTest.java` 생성
- [ ] 특정 사용자의 모든 Token 무효화 테스트
- [ ] 커밋: `test: RevokeAllTokensUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/auth/RevokeAllTokensUseCase.java`
- [ ] `usecase/auth/RevokeAllTokensUseCaseImpl.java`
- [ ] 커밋: `feat: RevokeAllTokensUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RevokeAllTokensUseCase 구조 개선`

---

### Phase 8: Role & Permission UseCases (4개)

---

### 8-1. AssignRoleToUserUseCase (Cycle 32)

#### Red: 테스트 작성
- [ ] `AssignRoleToUserUseCaseTest.java` 생성
- [ ] 역할 할당 테스트
- [ ] 커밋: `test: AssignRoleToUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/role/AssignRoleToUserUseCase.java`
- [ ] `usecase/role/AssignRoleToUserUseCaseImpl.java`
- [ ] UserRole 생성 및 저장
- [ ] 커밋: `feat: AssignRoleToUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: AssignRoleToUserUseCase 구조 개선`

---

### 8-2. RevokeRoleFromUserUseCase (Cycle 33)

#### Red: 테스트 작성
- [ ] `RevokeRoleFromUserUseCaseTest.java` 생성
- [ ] 커밋: `test: RevokeRoleFromUserUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/role/RevokeRoleFromUserUseCase.java`
- [ ] `usecase/role/RevokeRoleFromUserUseCaseImpl.java`
- [ ] 커밋: `feat: RevokeRoleFromUserUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: RevokeRoleFromUserUseCase 구조 개선`

---

### 8-3. GetUserPermissionsUseCase - Query (Cycle 34)

#### Red: 테스트 작성
- [ ] `GetUserPermissionsUseCaseTest.java` 생성
- [ ] 커밋: `test: GetUserPermissionsUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/role/GetUserPermissionsUseCase.java`
- [ ] `usecase/role/GetUserPermissionsUseCaseImpl.java`
- [ ] `@Transactional(readOnly = true)`
- [ ] 커밋: `feat: GetUserPermissionsUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetUserPermissionsUseCase 구조 개선`

---

### 8-4. GetRolePermissionsUseCase - Query (Cycle 35)

#### Red: 테스트 작성
- [ ] `GetRolePermissionsUseCaseTest.java` 생성
- [ ] 커밋: `test: GetRolePermissionsUseCase 테스트 (Red)`

#### Green: 최소 구현
- [ ] `usecase/role/GetRolePermissionsUseCase.java`
- [ ] `usecase/role/GetRolePermissionsUseCaseImpl.java`
- [ ] 커밋: `feat: GetRolePermissionsUseCase 구현 (Green)`

#### Refactor: 구조 개선
- [ ] 커밋: `struct: GetRolePermissionsUseCase 구조 개선`

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
