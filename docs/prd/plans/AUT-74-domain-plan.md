# AUT-74: Domain Layer TDD Plan

**Issue**: [AUT-74](https://ryuqqq.atlassian.net/browse/AUT-74)
**Epic**: [AUT-73](https://ryuqqq.atlassian.net/browse/AUT-73)
**Branch**: `feature/AUT-001-user-registration`
**Created**: 2025-12-02

---

## 현재 상태

테스트 파일들이 이미 작성되어 있으나 실제 구현 코드가 없어 컴파일 에러 발생 (RED 상태)

---

## TDD Cycles

### Cycle 1: User Aggregate Core ✅ COMPLETED
- [x] 🔴 RED: UserTest.java (이미 작성됨 - 컴파일 에러)
- [x] 🟢 GREEN: User Aggregate 구현 (2025-12-02)
- [x] ♻️ REFACTOR: 구조 개선 완료

**구현 필요 항목**:
- `domain/src/main/java/com/ryuqq/authhub/domain/user/aggregate/User.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/identifier/UserId.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/UserType.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/UserStatus.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/Credential.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/CredentialType.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/UserProfile.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/Email.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/Password.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/vo/PhoneNumber.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/exception/InvalidUserStateException.java`
- `domain/src/main/java/com/ryuqq/authhub/domain/user/exception/UserErrorCode.java`
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/user/fixture/UserFixture.java`
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/user/vo/fixture/CredentialFixture.java`
- `domain/src/testFixtures/java/com/ryuqq/authhub/domain/user/vo/fixture/UserProfileFixture.java`

**의존성**:
- `TenantId` (tenant 패키지)
- `OrganizationId` (organization 패키지)

### Cycle 2: Tenant Aggregate Core ✅ COMPLETED
- [x] 🔴 RED: TenantTest.java (이미 작성됨)
- [x] 🟢 GREEN: Tenant Aggregate 구현 (2025-12-02)
- [x] ♻️ REFACTOR: 구조 개선 완료

### Cycle 3: Organization Aggregate Core ✅ COMPLETED
- [x] 🔴 RED: OrganizationTest.java (이미 작성됨)
- [x] 🟢 GREEN: Organization Aggregate 구현 (2025-12-02)
- [x] ♻️ REFACTOR: 구조 개선 완료

---

## Zero-Tolerance Checklist

- [ ] Lombok 금지
- [ ] Law of Demeter 준수
- [ ] Tell Don't Ask 패턴 적용
- [ ] Long FK 전략 사용 (UUID 필드)
- [ ] VO 불변성 보장

---

## Progress Log

| Date | Cycle | Phase | Commit | Notes |
|------|-------|-------|--------|-------|
| 2025-12-02 | 1 | RED | - | 기존 테스트 파일 존재 (컴파일 에러) |
| 2025-12-02 | 1 | GREEN | 9661545 | feat: User 도메인 모델 구현 (AUT-74) |
| 2025-12-02 | 2 | GREEN | 3d374af | feat: Tenant 도메인 모델 구현 |
| 2025-12-02 | 3 | GREEN | - | feat: Organization 도메인 모델 구현 |
