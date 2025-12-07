# Refactoring Plan Command

기존 프로젝트를 Claude Spring Standards 컨벤션에 맞게 리팩토링하는 계획을 수립합니다.

---

## 명령어

```
/refactor-plan [scope]
```

**scope 옵션:**
- `full` - 전체 프로젝트 분석 (기본값)
- `domain` - Domain Layer만
- `application` - Application Layer만
- `persistence` - Persistence Layer만
- `rest-api` - REST API Layer만

---

## 실행 프로세스

```
┌─────────────────────────────────────────────────────────────┐
│              Refactoring Plan Process                        │
├─────────────────────────────────────────────────────────────┤
│  1️⃣ 현재 상태 분석 (Current State Analysis)                 │
│     └─ Serena MCP로 코드베이스 스캔                          │
│                                                              │
│  2️⃣ 컨벤션 위반 탐지 (Convention Violation Detection)       │
│     └─ Zero-Tolerance 규칙 위반 식별                         │
│                                                              │
│  3️⃣ 영향도 분석 (Impact Analysis)                           │
│     └─ 변경 범위 및 의존성 파악                              │
│                                                              │
│  4️⃣ 리팩토링 계획 생성 (Refactoring Plan Generation)        │
│     └─ 우선순위 기반 단계별 계획                             │
│                                                              │
│  5️⃣ Serena Memory 저장                                      │
│     └─ refactor-plan-{timestamp} 저장                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 1️⃣ 현재 상태 분석

### 분석 대상

Serena MCP를 사용하여 다음 항목들을 스캔합니다:

```markdown
## 스캔 항목

### 프로젝트 구조
- [ ] 모듈 구조 (헥사고날 아키텍처 준수 여부)
- [ ] 패키지 구조 (레이어별 분리 여부)
- [ ] 의존성 방향 (안쪽 → 바깥쪽)

### Domain Layer
- [ ] Aggregate 패턴 사용 여부
- [ ] VO(Value Object) 사용 여부
- [ ] Domain Exception 구조
- [ ] Domain Event 사용 여부

### Application Layer
- [ ] Port-In (UseCase) 인터페이스 분리
- [ ] Port-Out 인터페이스 정의
- [ ] CQRS 패턴 (Command/Query 분리)
- [ ] DTO Record 사용 여부

### Persistence Layer
- [ ] Entity 구조 (Long FK 전략)
- [ ] Repository 패턴 (JPA + QueryDSL)
- [ ] Mapper 분리 여부
- [ ] Adapter 구현 여부

### REST API Layer
- [ ] Controller 구조
- [ ] Request/Response DTO 분리
- [ ] 에러 핸들링 구조
```

---

## 2️⃣ 컨벤션 위반 탐지

### Zero-Tolerance 위반 체크리스트

```markdown
## 🔴 Critical (즉시 수정 필요)

### Lombok 사용
- [ ] `@Data`, `@Getter`, `@Setter` 사용 여부
- [ ] `@Builder`, `@AllArgsConstructor` 사용 여부
- [ ] `@ToString`, `@EqualsAndHashCode` 사용 여부

검색 패턴:
- `import lombok.`
- `@Data`, `@Getter`, `@Setter`

### Law of Demeter 위반
- [ ] Getter 체이닝 (`a.getB().getC()`)
- [ ] 내부 객체 직접 노출

검색 패턴:
- `\.get[A-Z][a-zA-Z]*\(\)\.get`
- `\.get[A-Z][a-zA-Z]*\(\)\.[a-z]`

### Transaction 경계 위반
- [ ] `@Transactional` 내 외부 API 호출
- [ ] `@Transactional` 내 메시지 발행

검색 패턴:
- `@Transactional` 메서드 내 `RestTemplate`, `WebClient`, `FeignClient` 호출

### JPA 관계 어노테이션 사용
- [ ] `@OneToMany`, `@ManyToOne` 사용
- [ ] `@OneToOne`, `@ManyToMany` 사용

검색 패턴:
- `@OneToMany`, `@ManyToOne`, `@OneToOne`, `@ManyToMany`
```

```markdown
## 🟡 Important (빠른 수정 권장)

### CQRS 미분리
- [ ] Command/Query가 같은 클래스에 혼재
- [ ] UseCase 인터페이스 미분리

### DTO 미분리
- [ ] Request/Response 같은 DTO 사용
- [ ] Entity 직접 반환

### Assembler/Mapper 미사용
- [ ] 변환 로직이 Service에 직접 구현
- [ ] Domain ↔ DTO 변환 산재

### 테스트 구조
- [ ] MockMvc 사용 (TestRestTemplate 권장)
- [ ] 단위 테스트 부재
```

```markdown
## 🟢 Recommended (점진적 개선)

### 네이밍 컨벤션
- [ ] 클래스명 Suffix 규칙 미준수
- [ ] 메서드명 규칙 미준수

### 패키지 구조
- [ ] 기능별 패키지 미분리
- [ ] common 패키지 과다 사용

### 문서화
- [ ] JavaDoc 부재
- [ ] README 미작성
```

---

## 3️⃣ 영향도 분석

### 변경 영향도 매트릭스

```markdown
## 영향도 분석 결과

| 변경 항목 | 영향 파일 수 | 의존 모듈 | 위험도 | 우선순위 |
|-----------|-------------|-----------|--------|----------|
| Lombok 제거 | 45개 | 전체 | 🔴 High | 1 |
| Long FK 전환 | 12개 | Persistence | 🔴 High | 2 |
| CQRS 분리 | 23개 | Application | 🟡 Medium | 3 |
| DTO 분리 | 34개 | 전체 | 🟡 Medium | 4 |
| Assembler 추가 | 15개 | Application | 🟢 Low | 5 |
```

### 의존성 그래프

```
변경 A (Lombok 제거)
    ↓ 영향
변경 B (DTO 분리) ─────→ 변경 C (Assembler 추가)
    ↓ 영향
변경 D (CQRS 분리)
```

---

## 4️⃣ 리팩토링 계획 생성

### Phase 구조

```markdown
## 📋 리팩토링 계획

### Phase 1: 기반 정비 (Foundation) - 예상 1-2주
**목표**: Zero-Tolerance 위반 해결

#### Step 1.1: Lombok 제거
- [ ] Domain Layer Lombok 제거
- [ ] Application Layer Lombok 제거
- [ ] Persistence Layer Lombok 제거
- [ ] REST API Layer Lombok 제거

**변경 파일**: {파일 목록}
**검증**: `./gradlew build` 성공

#### Step 1.2: Long FK 전략 전환
- [ ] JPA 관계 어노테이션 제거
- [ ] Long FK 필드로 변경
- [ ] 관련 Repository 쿼리 수정

**변경 파일**: {파일 목록}
**검증**: 통합 테스트 통과

---

### Phase 2: 아키텍처 정렬 (Architecture Alignment) - 예상 2-3주
**목표**: 헥사고날 아키텍처 준수

#### Step 2.1: Port 인터페이스 분리
- [ ] Port-In (UseCase) 인터페이스 생성
- [ ] Port-Out 인터페이스 생성
- [ ] Adapter 구현체 분리

#### Step 2.2: CQRS 분리
- [ ] Command UseCase 분리
- [ ] Query UseCase 분리
- [ ] Service 레이어 정리

---

### Phase 3: 코드 품질 개선 (Code Quality) - 예상 1-2주
**목표**: 컨벤션 완전 준수

#### Step 3.1: DTO 구조 정리
- [ ] Command DTO 생성 (Record)
- [ ] Query DTO 생성 (Record)
- [ ] Response DTO 분리

#### Step 3.2: Assembler/Mapper 추가
- [ ] Domain → Response Assembler
- [ ] Command → Domain Factory
- [ ] Entity ↔ Domain Mapper

---

### Phase 4: 테스트 강화 (Test Enhancement) - 예상 1주
**목표**: ArchUnit 테스트 통과

#### Step 4.1: ArchUnit 테스트 적용
- [ ] Domain Layer ArchUnit
- [ ] Application Layer ArchUnit
- [ ] Persistence Layer ArchUnit
- [ ] REST API Layer ArchUnit

#### Step 4.2: 통합 테스트 정비
- [ ] MockMvc → TestRestTemplate 전환
- [ ] Test Fixtures 정리
```

---

## 5️⃣ 산출물

### Serena Memory 저장 형식

```markdown
# Refactoring Plan: {프로젝트명}

## 메타 정보
- 생성일: {timestamp}
- 분석 범위: {scope}
- 총 위반 항목: {count}개

## 현재 상태 요약
### Critical 위반: {count}개
- Lombok 사용: {files}개 파일
- Law of Demeter: {files}개 파일
- Transaction 경계: {files}개 파일

### Important 위반: {count}개
- CQRS 미분리: {files}개 파일
- DTO 미분리: {files}개 파일

## 리팩토링 계획
### Phase 1: 기반 정비
{상세 계획}

### Phase 2: 아키텍처 정렬
{상세 계획}

### Phase 3: 코드 품질 개선
{상세 계획}

### Phase 4: 테스트 강화
{상세 계획}

## 진행 상황
- [ ] Phase 1 완료
- [ ] Phase 2 완료
- [ ] Phase 3 완료
- [ ] Phase 4 완료
```

---

## 실행 예시

```bash
# 전체 프로젝트 분석
/refactor-plan

# Domain Layer만 분석
/refactor-plan domain

# Application Layer만 분석
/refactor-plan application
```

---

## 연계 워크플로우

```
/refactor-plan
    ↓
리팩토링 계획 승인
    ↓
Phase별 실행
    ├─ /kb/domain/go (Domain 리팩토링)
    ├─ /kb/application/go (Application 리팩토링)
    ├─ /kb/persistence/go (Persistence 리팩토링)
    └─ /kb/rest-api/go (REST API 리팩토링)
    ↓
ArchUnit 테스트 실행
    ↓
완료
```

---

## 참조 문서

- **컨벤션 문서**: `docs/coding_convention/`
- **ArchUnit 테스트**: 각 모듈 `src/test/java/.../architecture/`
- **Zero-Tolerance 규칙**: `.claude/CLAUDE.md`
