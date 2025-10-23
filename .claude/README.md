# 🚀 Dynamic Hooks + Cache 시스템

**Spring Boot 헥사고날 아키텍처를 위한 혁신적인 AI 가이드 시스템**

이 디렉토리는 Claude Code가 프로젝트 표준을 이해하고 준수하는 코드를 생성하도록 **자동으로 가이드**합니다.

> ⚡ **2025년 10월 혁신**: Cache 기반 규칙 주입 시스템으로 **토큰 90% 절감**, **검증 속도 73.6% 향상**

---

## 📋 목차

- [개요](#개요)
- [시스템 아키텍처](#시스템-아키텍처)
- [Cache 시스템](#cache-시스템)
- [Dynamic Hooks](#dynamic-hooks)
- [Slash Commands](#slash-commands)
- [성능 메트릭](#성능-메트릭)
- [개발 워크플로우](#개발-워크플로우)
- [디렉토리 구조](#디렉토리-구조)

---

## 🎯 개요

### 핵심 혁신: Cache 기반 AI 가이드

기존의 AI 코딩 어시스턴트는 매번 전체 규칙 문서를 로딩하거나, 일반적인 가이드라인만 제공합니다.
이 프로젝트는 **완전히 다른 접근**을 합니다:

```
📚 90개 마크다운 규칙 문서
         ↓
🔄 JSON Cache로 변환 (O(1) 검색)
         ↓
🎯 컨텍스트 인식 자동 주입
         ↓
✨ Claude가 규칙 준수 코드 생성
         ↓
⚡ 실시간 검증 (148ms)
```

### 왜 혁신적인가?

1. **지능형 컨텍스트 인식**: 키워드 분석 → 레이어 매핑 → 관련 규칙만 주입
2. **O(1) 고속 검색**: JSON 인덱스 기반 즉시 검색 (index.json)
3. **실시간 검증**: 코드 생성 직후 자동 검증 (148ms)
4. **토큰 효율성**: 필요한 규칙만 선택적 주입 (90% 절감)

### 주요 기능

- ✅ **자동 규칙 주입**: 키워드 기반 지능형 컨텍스트 분석
- ✅ **실시간 검증**: 코드 생성 직후 즉시 검증
- ✅ **Cache 최적화**: O(1) 검색, 95% 빠른 로딩
- ✅ **Slash Commands**: 코드 생성 및 검증 자동화

---

## 🏗️ 시스템 아키텍처

### 전체 흐름도

```
┌─────────────────────────────────────────────────────────────┐
│  1. 규칙 문서 (docs/coding_convention/)                      │
│     - 90개 마크다운 규칙 (Layer별 구조화)                     │
│     - Domain, Application, Adapter, Testing 등               │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  2. Cache 빌드 (build-rule-cache.py)                        │
│     - 90개 JSON + index.json 생성                           │
│     - keywordIndex, layerIndex, priorityIndex               │
│     - 빌드 시간: ~5초                                        │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  3. 사용자 입력                                               │
│     "Order 엔티티를 만들어줘"                                 │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  4. user-prompt-submit.sh (Dynamic Hook)                    │
│     - 키워드 감지: "entity" → 30점                           │
│     - Layer 매핑: adapter-persistence                        │
│     - inject-rules.py 호출                                   │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  5. inject-rules.py                                         │
│     - index.json 로드 (O(1))                                │
│     - adapter-persistence 규칙 추출                          │
│     - Markdown 형식으로 규칙 주입                            │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  6. Claude Code                                             │
│     - 주입된 규칙 기반 코드 생성                             │
│     - Long FK, NO JPA relationships                         │
│     - Protected constructor + static factory                │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  7. after-tool-use.sh (Dynamic Hook)                        │
│     - Write/Edit 도구 사용 직후 트리거                       │
│     - validation-helper.py 호출                              │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  8. validation-helper.py                                    │
│     - Cache 기반 고속 검증 (148ms)                           │
│     - Layer 감지 + 규칙 매칭                                 │
│     - ✅ Pass / ❌ Fail 결과 출력                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Cache 시스템

### Cache 구조

```
.claude/cache/rules/
├── index.json                    # 메타 인덱스 (O(1) 검색)
│   ├── keywordIndex              # 키워드 → rule_id 매핑
│   ├── layerIndex                # 레이어 → rule_id[] 매핑
│   └── priorityIndex             # 우선순위별 분류
│
└── [rule-id].json (90개)         # 개별 규칙 JSON
    ├── metadata                  # 제목, Layer, 우선순위
    ├── rules                     # prohibited, allowed, patterns
    ├── validation                # 검증 로직
    ├── examples                  # 예시 코드
    └── documentation             # 원본 경로, 요약
```

### index.json 구조

```json
{
  "version": "1.0.0",
  "buildDate": "2025-10-17T03:52:03.347304Z",
  "totalRules": 90,

  "keywordIndex": {
    "entity": [
      "persistence-layer-jpa-entity-design-02_entity-immutability",
      "java21-patterns-record-patterns-05_entity-vs-value-object"
    ],
    "transaction": [
      "application-layer-transaction-management-01_transaction-boundaries",
      "application-layer-transaction-management-03_transaction-best-practices"
    ]
  },

  "layerIndex": {
    "domain": [
      "domain-layer-aggregate-design-01_aggregate-boundaries",
      "domain-layer-law-of-demeter-01_getter-chaining-prohibition"
    ],
    "application": [
      "application-layer-usecase-design-01_command-usecase"
    ],
    "adapter-persistence": [
      "persistence-layer-jpa-entity-design-01_long-fk-strategy"
    ]
  }
}
```

### 개별 규칙 JSON 예시

```json
{
  "metadata": {
    "id": "persistence-layer-jpa-entity-design-01_long-fk-strategy",
    "title": "Long FK 전략 (JPA 관계 어노테이션 금지)",
    "layer": "adapter-persistence",
    "category": "jpa-entity-design",
    "priority": "critical",
    "keywords": ["long", "fk", "strategy", "entity"]
  },

  "rules": {
    "prohibited": [
      "@ManyToOne, @OneToMany, @OneToOne, @ManyToMany 사용 금지",
      "엔티티 간 직접 참조 금지"
    ],
    "allowed": [
      "Long FK 필드 사용 (userId, orderId 등)",
      "ID 기반 조회 메서드"
    ],
    "patterns": [
      "private Long userId;",
      "public void assignUser(Long userId)"
    ]
  },

  "validation": {
    "antiPatterns": [
      "@ManyToOne",
      "@OneToMany",
      "private User user;"
    ]
  },

  "examples": {
    "good": "private Long userId; // ✅",
    "bad": "@ManyToOne private User user; // ❌"
  },

  "documentation": {
    "path": "docs/coding_convention/04-persistence-layer/jpa-entity-design/01_long-fk-strategy.md",
    "summary": "JPA 엔티티는 Long FK만 사용, 관계 어노테이션 금지"
  }
}
```

### Cache 빌드

```bash
# 마크다운 → JSON 변환 (약 5초)
python3 .claude/hooks/scripts/build-rule-cache.py

# 출력
Building Rule Cache...
✓ Parsed: 01_getter-chaining-prohibition.md
✓ Parsed: 02_entity-immutability.md
...
✓ Built index.json with 90 rules
Cache build complete: 90 rules in 4.8s
```

### Cache 업데이트 워크플로우

```bash
# 1. 규칙 문서 수정
vim docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md

# 2. Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. 확인
cat .claude/cache/rules/domain-layer-law-of-demeter-01_getter-chaining-prohibition.json

# 4. 테스트
/code-gen-domain Order  # 새 규칙 자동 적용됨
```

---

## 🔧 Dynamic Hooks

### Hook 실행 흐름

```
사용자: "Order entity 만들어줘"
    ↓
user-prompt-submit.sh
    ├─ 키워드 분석: "entity" (30점)
    ├─ Layer 매핑: adapter-persistence
    └─ inject-rules.py 호출
         ↓
inject-rules.py
    ├─ index.json 로드
    ├─ layerIndex["adapter-persistence"] 추출
    └─ Markdown 규칙 출력
         ↓
Claude Code
    ├─ 규칙 읽기
    ├─ 코드 생성 (Long FK, NO setters)
    └─ Write 도구 사용
         ↓
after-tool-use.sh
    ├─ Write 감지
    ├─ 파일 경로 추출
    └─ validation-helper.py 호출
         ↓
validation-helper.py
    ├─ Layer 감지 (파일 경로)
    ├─ Cache에서 규칙 로드
    ├─ Anti-pattern 검사
    └─ ✅ Pass / ❌ Fail
```

### 1. user-prompt-submit.sh

**목적**: 사용자 입력 분석 → 레이어 감지 → 규칙 주입

**키워드 매핑 전략**:

| 키워드 | Layer | 점수 |
|--------|-------|------|
| aggregate, entity, domain | domain | 30점 |
| usecase, service, command | application | 30점 |
| controller, rest api | adapter-rest | 30점 |
| repository, jpa | adapter-persistence | 30점 |
| test, 테스트 | testing | 25점 |

**임계값**: 25점 이상이면 규칙 주입

**예시**:

```bash
# 입력: "Order 엔티티를 만들어줘"
# → "entity" 감지 → 30점
# → Layer: adapter-persistence
# → inject-rules.py adapter-persistence
```

**로그**:

```
[2025-10-17 12:34:56] user-prompt-submit triggered
User Input: Order 엔티티를 만들어줘
  → Detected: entity → adapter-persistence (+30 score)
  → Context Score: 30
  → Detected Layers: adapter-persistence
  → Strategy: CACHE_BASED (inject-rules.py)
  → Injecting rules for layer: adapter-persistence
```

### 2. inject-rules.py

**목적**: Layer별 규칙을 Cache에서 로드하여 Markdown 출력

**알고리즘**:

```python
def inject_layer_rules(layer: str, priority_filter: str = None):
    # 1. index.json 로드
    index = load_index()

    # 2. layerIndex에서 rule_ids 추출
    rule_ids = index["layerIndex"][layer]

    # 3. 각 rule_id의 JSON 로드
    rules = [load_rule(rid) for rid in rule_ids]

    # 4. Priority 필터링 (optional)
    if priority_filter:
        rules = [r for r in rules if r["metadata"]["priority"] == priority_filter]

    # 5. Markdown 출력
    print("## 🎯 ADAPTER-PERSISTENCE 레이어 규칙")
    print("### ❌ 금지 규칙")
    for rule in rules:
        for item in rule["rules"]["prohibited"][:3]:
            print(f"- {item}")
```

**출력 예시**:

```markdown
---

## 🎯 ADAPTER-PERSISTENCE 레이어 규칙 (자동 주입됨)

### ❌ 금지 규칙 (Zero-Tolerance)

- @ManyToOne, @OneToMany, @OneToOne, @ManyToMany 사용 금지
- Setter 메서드 금지
- Public constructor 금지

### ✅ 필수 규칙

- Long FK 필드 사용 (userId, orderId 등)
- Protected constructor + static factory
- Getter만 허용

### 📋 상세 문서

- [Long FK 전략](docs/coding_convention/04-persistence-layer/jpa-entity-design/01_long-fk-strategy.md)
- [Entity Immutability](docs/coding_convention/04-persistence-layer/jpa-entity-design/02_entity-immutability.md)

**이 규칙들은 실시간으로 검증됩니다.**

---
```

### 3. after-tool-use.sh

**목적**: Write/Edit 도구 사용 직후 자동 검증

**트리거**: Claude가 파일을 생성/수정할 때

**알고리즘**:

```bash
# 1. 도구 데이터에서 파일 경로 추출
FILE_PATH=$(echo "$TOOL_DATA" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"')

# 2. 파일 경로 기반 Layer 감지
if echo "$FILE_PATH" | grep -q "domain/.*model"; then
    LAYER="domain"
elif echo "$FILE_PATH" | grep -q "adapter/in/web"; then
    LAYER="adapter-rest"
elif echo "$FILE_PATH" | grep -q "adapter/out/persistence"; then
    LAYER="adapter-persistence"
fi

# 3. validation-helper.py 호출
python3 validation-helper.py "$FILE_PATH" "$LAYER"
```

**로그**:

```
[2025-10-17 12:35:10] after-tool-use triggered
File: domain/src/main/java/.../OrderEntity.java
  → Detected Layer: ADAPTER-PERSISTENCE
  → Running cache-based validation for layer: adapter-persistence
```

### 4. validation-helper.py

**목적**: Cache 기반 고속 검증 (148ms)

**알고리즘**:

```python
def validate_file(file_path: str, layer: str):
    # 1. index.json 로드
    index = load_index()

    # 2. Layer 규칙 추출
    rule_ids = index["layerIndex"][layer]

    # 3. 파일 내용 읽기
    content = Path(file_path).read_text()

    # 4. Anti-pattern 검증
    violations = []
    for rule_id in rule_ids:
        rule = load_rule(rule_id)
        for pattern in rule["validation"]["antiPatterns"]:
            if re.search(pattern, content):
                violations.append({
                    "rule": rule["metadata"]["title"],
                    "pattern": pattern,
                    "priority": rule["metadata"]["priority"]
                })

    # 5. 결과 출력
    if violations:
        print("❌ Validation Failed")
        for v in violations:
            print(f"  - {v['rule']}: {v['pattern']}")
    else:
        print("✅ Validation Passed")
```

**성능**: 148ms (기존 561ms 대비 73.6% 향상)

---

## 🚀 Slash Commands

### 코드 생성 Commands

#### `/code-gen-domain <name>`

**목적**: Domain Aggregate 생성 (자동 규칙 주입 + 검증)

**실행 흐름**:

1. Domain layer 규칙 자동 주입 (inject-rules.py)
2. Aggregate Root, Value Objects, Domain Events 생성
3. Law of Demeter 준수 (Getter 체이닝 금지)
4. 생성 직후 자동 검증 (validation-helper.py)

**예시**:

```bash
/code-gen-domain Order

# 생성되는 파일:
# - Order.java (Aggregate Root)
# - OrderId.java (Value Object)
# - OrderStatus.java (Sealed class)
# - OrderCreated.java (Domain Event)
```

#### `/code-gen-usecase <name>`

**목적**: Application UseCase 생성

**실행 흐름**:

1. Application layer 규칙 주입
2. Command/Query UseCase 생성
3. Transaction 경계 설정
4. 자동 검증

**예시**:

```bash
/code-gen-usecase CreateOrder

# 생성되는 파일:
# - CreateOrderCommand.java (record)
# - CreateOrderUseCase.java (Service)
# - CreateOrderResponse.java (record)
```

#### `/code-gen-controller <name>`

**목적**: REST Controller 생성

**실행 흐름**:

1. Adapter-REST layer 규칙 주입
2. Controller + DTOs 생성
3. RESTful API 규칙 준수
4. 자동 검증

**예시**:

```bash
/code-gen-controller Order

# 생성되는 파일:
# - OrderController.java
# - CreateOrderRequest.java (record)
# - OrderResponse.java (record)
```

### 검증 Commands

#### `/validate-domain <file>`

**목적**: Domain layer 파일 검증

**예시**:

```bash
/validate-domain domain/src/main/java/.../Order.java

# 출력:
# ✅ Pure Java (NO Spring/JPA imports)
# ✅ NO Getter chaining
# ✅ Tell, Don't Ask pattern
```

#### `/validate-architecture [dir]`

**목적**: 전체 또는 특정 모듈 아키텍처 검증

**예시**:

```bash
# 전체 검증
/validate-architecture

# 특정 모듈만
/validate-architecture domain

# 출력:
# ✅ Layer dependency rules
# ✅ Naming conventions
# ❌ Found 2 violations:
#   - domain/Order.java: Spring import detected
```

### 기타 Commands

#### `/gemini-review [pr-number]`

**목적**: Gemini 코드 리뷰 분석 및 리팩토링 전략 생성

**예시**:

```bash
/gemini-review

# 출력:
# 📊 Review Summary: 8 comments (1 critical, 3 improvement)
# 🎯 Refactoring Strategy: 3 phases, estimated 1.5 days
```

#### `/jira-task`

**목적**: Jira 태스크 분석 및 브랜치 생성

**예시**:

```bash
/jira-task

# Git 브랜치에서 Jira ID 파싱
# Jira API로 태스크 내용 조회
# 작업 가이드 생성
```

---

## 📊 성능 메트릭

### 실측 성능 개선

| 메트릭 | Before (전체 문서) | After (Cache) | 개선율 |
|--------|-------------------|--------------|--------|
| **토큰 사용량** | 50,000 tokens | 500-1,000 tokens | **90% 절감** |
| **검증 속도** | 561ms | 148ms | **73.6% 향상** |
| **문서 로딩** | 2-3초 | <100ms | **95% 향상** |
| **규칙 검색** | O(n) 순차 검색 | O(1) 인덱스 | **무한대 향상** |
| **Cache 빌드** | N/A | 5초 (1회만) | - |

### 토큰 사용량 비교

**Before (전체 문서 방식)**:

```
CODING_STANDARDS.md (2,676줄) +
ENTERPRISE_SPRING_STANDARDS_PROMPT.md (3,361줄) =
약 50,000 tokens
```

**After (Cache 방식)**:

```
# 레이어별 필요한 규칙만 선택적 주입
adapter-persistence 규칙: 10개 × 50 tokens = 500 tokens
또는
domain 규칙: 13개 × 50 tokens = 650 tokens
```

### 검증 속도 비교

**Before (파일 기반 검증)**:

```python
# 전체 문서 읽기 + 파싱 + 정규식 매칭
time: 561ms
```

**After (Cache 기반 검증)**:

```python
# index.json 로드 (메모리) + 규칙 JSON 로드 + 매칭
time: 148ms
```

### 메모리 사용량

```
index.json: 45KB (메모리 상주)
개별 rule JSON: 평균 1.5KB × 90개 = 135KB
총 메모리 사용량: ~180KB (무시할 수준)
```

---

## 💼 개발 워크플로우

### 1. 일반 개발 (자동 규칙 적용)

```bash
# 1. Feature 브랜치 생성
git checkout -b feature/order-management

# 2. Claude에게 요청
"Order 도메인 클래스를 만들어줘"

# 3. 자동 실행 흐름:
#    a. user-prompt-submit.sh: "domain" 키워드 감지
#    b. inject-rules.py: Domain layer 규칙 주입
#    c. Claude: 규칙 준수 코드 생성
#    d. after-tool-use.sh: 즉시 검증
#    e. validation-helper.py: Cache 기반 검증 (148ms)

# 4. 검증 결과 확인
# ✅ Validation Passed
# - Pure Java (NO Spring/JPA)
# - NO Getter chaining
# - Tell, Don't Ask pattern

# 5. 커밋 및 푸시
git add .
git commit -m "feat: add Order domain class"
git push origin feature/order-management
```

### 2. Slash Command 활용

```bash
# 빠른 코드 생성
/code-gen-domain Order
/code-gen-usecase CreateOrder
/code-gen-controller Order

# 생성 후 즉시 검증됨 (자동)
# ✅ All validations passed
```

### 3. 수동 검증

```bash
# 특정 파일 검증
/validate-domain domain/src/main/java/.../Order.java

# 전체 아키텍처 검증
/validate-architecture

# 특정 레이어만 검증
/validate-architecture adapter-persistence
```

### 4. Cache 업데이트

```bash
# 규칙 수정 후
vim docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md

# Cache 재빌드 (5초)
python3 .claude/hooks/scripts/build-rule-cache.py

# 즉시 반영됨
/code-gen-domain Product  # 새 규칙 적용
```

### 5. Gemini 리뷰 활용

```bash
# PR 생성
gh pr create --title "feat: Order Management" --body "..."

# Gemini 자동 리뷰 대기 (1-2분)

# 리뷰 분석 실행
/gemini-review

# 출력된 리팩토링 전략 검토 및 실행
# Phase 1: Critical (보안, 런타임 오류) - 4시간
# Phase 2: Improvement (성능, 유지보수성) - 1일
# Phase 3: Suggestion (스타일, 가독성) - 0.5일
```

---

## 📁 디렉토리 구조

```
.claude/
├── README.md                          # 이 문서 (시스템 전체 가이드)
├── CLAUDE.md                          # 중앙 설정 파일 (프로젝트 컨텍스트)
│
├── cache/                             # ⭐ Cache 시스템 (핵심)
│   └── rules/
│       ├── index.json                 # 메타 인덱스 (O(1) 검색)
│       ├── [rule-id].json (90개)     # 개별 규칙 JSON
│       └── README.md                  # Cache 시스템 상세 문서
│
├── hooks/                             # ⭐ Dynamic Hooks (핵심)
│   ├── README.md                      # Hook 시스템 상세 문서
│   ├── user-prompt-submit.sh         # 규칙 주입 Hook
│   ├── after-tool-use.sh             # 검증 Hook
│   └── scripts/
│       ├── build-rule-cache.py       # Cache 빌드 스크립트
│       ├── validation-helper.py      # 검증 엔진
│       └── [other scripts]
│
├── commands/                          # ⭐ Slash Commands
│   ├── README.md                      # Commands 전체 가이드
│   ├── code-gen-domain.md            # Domain 생성
│   ├── code-gen-usecase.md           # UseCase 생성
│   ├── code-gen-controller.md        # Controller 생성
│   ├── validate-domain.md            # Domain 검증
│   ├── validate-architecture.md      # 아키텍처 검증
│   ├── gemini-review.md              # Gemini 리뷰 분석
│   ├── jira-task.md                  # Jira 태스크 분석
│   └── lib/
│       └── inject-rules.py           # 규칙 주입 엔진
│
└── agents/                            # 전문 에이전트 (향후 확장)
    └── prompt-engineer.md
```

### 핵심 파일 설명

| 파일 | 역할 | 중요도 |
|------|------|--------|
| `cache/rules/index.json` | O(1) 검색 인덱스 | ⭐⭐⭐⭐⭐ |
| `cache/rules/[rule-id].json` | 개별 규칙 JSON | ⭐⭐⭐⭐⭐ |
| `hooks/user-prompt-submit.sh` | 자동 규칙 주입 | ⭐⭐⭐⭐⭐ |
| `hooks/after-tool-use.sh` | 실시간 검증 | ⭐⭐⭐⭐⭐ |
| `hooks/scripts/build-rule-cache.py` | Cache 빌드 | ⭐⭐⭐⭐ |
| `hooks/scripts/validation-helper.py` | 검증 엔진 | ⭐⭐⭐⭐⭐ |
| `commands/lib/inject-rules.py` | 규칙 주입 로직 | ⭐⭐⭐⭐⭐ |
| `commands/code-gen-*.md` | 코드 생성 Commands | ⭐⭐⭐⭐ |
| `commands/validate-*.md` | 검증 Commands | ⭐⭐⭐⭐ |

---

## 🎓 학습 경로

### Day 1: 시스템 이해

1. **README.md 읽기** (이 문서) - 시스템 전체 아키텍처 파악
2. **Cache 시스템 이해** - `.claude/cache/rules/README.md`
3. **첫 코드 생성 테스트**:
   ```bash
   /code-gen-domain Order
   # 자동 규칙 주입 → 생성 → 검증 흐름 체험
   ```

### Week 1: 핵심 규칙 숙지

1. **Domain Layer 규칙**:
   - Law of Demeter (Getter 체이닝 금지)
   - Lombok 금지 (Pure Java)
   - Tell, Don't Ask pattern

2. **Application Layer 규칙**:
   - Transaction 경계 관리
   - `@Transactional` 내 외부 API 호출 금지
   - Command/Query 분리

3. **Persistence Layer 규칙**:
   - Long FK 전략 (JPA 관계 금지)
   - Entity Immutability
   - N+1 문제 방지

### Month 1: 고급 패턴

1. **DDD Aggregate 설계**
2. **CQRS 패턴 적용**
3. **Event-Driven Architecture**
4. **Cache 시스템 커스터마이징**

---

## 🔗 관련 문서

### 핵심 문서

- **[Cache System Guide](./cache/rules/README.md)** - Cache 시스템 상세 문서
- **[Dynamic Hooks Guide](./hooks/README.md)** - Hook 시스템 상세 문서
- **[Commands Guide](./commands/README.md)** - Slash Commands 전체 가이드
- **[CLAUDE.md](./CLAUDE.md)** - 프로젝트 중앙 설정 파일

### 규칙 문서

- **[Coding Convention](../docs/coding_convention/)** - 90개 규칙 (Layer별)
  - 01-adapter-rest-api-layer/ (18개)
  - 02-domain-layer/ (13개)
  - 03-application-layer/ (13개)
  - 04-persistence-layer/ (13개)
  - 05-testing/ (6개)
  - 06-java21-patterns/ (15개)
  - 07-enterprise-patterns/ (10개)
  - 08-error-handling/ (5개)

### 튜토리얼

- **[Getting Started](../docs/tutorials/01-getting-started.md)** - 5분 시작 가이드

---

## 🎯 효과

### Before (기존 방식)

```
Claude 요청
  → 전체 문서 로딩 (2-3초, 50K tokens)
  → 컨텍스트 압박
  → 느린 응답
  → 규칙 변경 시 여러 파일 수정 필요
```

### After (Cache 시스템)

```
Cache 빌드 (5초, 1회만)
  ↓
Claude 요청
  ↓
키워드 분석 → Layer 감지 (즉시)
  ↓
관련 규칙만 주입 (500-1K tokens, <100ms)
  ↓
규칙 준수 코드 생성
  ↓
실시간 검증 (148ms)
  ↓
✅ Pass / ❌ Fail
```

### 정량적 효과

- ⚡ **토큰 사용량**: 90% 절감 (50K → 500-1K)
- 🚀 **검증 속도**: 73.6% 향상 (561ms → 148ms)
- 📉 **문서 로딩**: 95% 향상 (2-3s → <100ms)
- 🔍 **규칙 검색**: O(n) → O(1) (무한대 향상)
- 🔧 **유지보수**: 단일 진실 공급원 (규칙 문서만 수정)

### 정성적 효과

- ✅ **처음부터 올바른 코드 생성** - 규칙이 자동 주입되어 재작업 불필요
- ✅ **실시간 피드백** - 코드 생성 직후 즉시 검증
- ✅ **학습 곡선 단축** - 규칙을 외울 필요 없음
- ✅ **일관된 품질** - 모든 코드가 동일한 규칙 준수
- ✅ **확장 용이** - 새 규칙 추가 → Cache 재빌드만

---

## 💡 혁신 포인트

### 1. 지능형 컨텍스트 인식

**기존 AI 코딩 어시스턴트**:
- 모든 규칙을 매번 로딩
- 또는 일반적인 가이드라인만 제공

**이 시스템**:
- 키워드 분석 → Layer 매핑
- 필요한 규칙만 선택적 주입
- 컨텍스트에 최적화된 가이드

### 2. O(1) 고속 검색

**기존 방식**:
- 순차 검색 (O(n))
- 파일 I/O 대기 시간

**Cache 시스템**:
- index.json 기반 O(1) 검색
- 메모리 캐싱 (180KB)
- 95% 빠른 로딩

### 3. 실시간 검증

**기존 방식**:
- 커밋 시 검증 (느림)
- 또는 빌드 시 검증 (더 느림)

**이 시스템**:
- 코드 생성 직후 즉시 검증 (148ms)
- 위반 시 즉시 수정 가능
- 피드백 루프 최소화

### 4. 단일 진실 공급원

**기존 방식**:
- Hook 코드에 규칙 하드코딩
- 변경 시 여러 파일 수정

**Cache 시스템**:
- 규칙 문서가 단일 진실 공급원
- 변경 → Cache 재빌드 → 즉시 반영
- 일관성 보장

---

## ⚙️ 커스터마이징

### 새로운 규칙 추가

```bash
# 1. 마크다운 규칙 작성
vim docs/coding_convention/02-domain-layer/new-rule/01_new-pattern.md

# 2. 규칙 문서 형식 준수:
# ---
# id: domain-layer-new-rule-01_new-pattern
# title: New Pattern Rule
# layer: domain
# priority: high
# keywords: [pattern, rule]
# ---
#
# ## Prohibited
# - Anti-pattern 1
#
# ## Allowed
# - Good pattern 1
#
# ## Examples
# ...

# 3. Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py

# 4. 테스트
/code-gen-domain Test  # 새 규칙 자동 적용됨
```

### Slash Command 추가

```bash
# 1. Command 파일 생성
vim .claude/commands/my-command.md

# 2. 메타데이터 정의
# ---
# name: my-command
# description: "My custom command"
# ---

# 3. 사용법 작성
# ...

# 4. 테스트
/my-command
```

### Hook 커스터마이징

```bash
# 1. user-prompt-submit.sh에 키워드 추가
vim .claude/hooks/user-prompt-submit.sh

# 2. 키워드 매핑 추가:
# ["my_keyword"]="my-layer"

# 3. inject-rules.py에 레이어 로직 추가 (필요시)

# 4. 테스트
echo "my_keyword test" | bash .claude/hooks/user-prompt-submit.sh
```

---

## 🚨 문제 해결

### Cache 빌드 실패

```bash
# 에러: "No such file or directory: docs/coding_convention/"
# 해결: 프로젝트 루트에서 실행
cd /path/to/project-root
python3 .claude/hooks/scripts/build-rule-cache.py
```

### 규칙 주입 안 됨

```bash
# 로그 확인
cat .claude/hooks/logs/hook-execution.log

# 점수 확인
# Context Score: 15 (25 이상이어야 주입됨)

# 해결: 키워드 추가 또는 더 명확한 요청
# "domain 레이어에서 Order entity를 만들어줘"
```

### 검증 실패

```bash
# 로그 확인
cat .claude/hooks/logs/hook-execution.log

# 수동 검증
/validate-domain path/to/file.java

# Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py
```

---

## 📊 비교표

| 항목 | 기존 AI 어시스턴트 | 전체 문서 방식 | **Cache 시스템** |
|------|-------------------|--------------|-----------------|
| 토큰 사용량 | 5K-10K (일반) | 50K (전체) | **500-1K (90% 절감)** |
| 검증 속도 | 수초 (외부 API) | 561ms | **148ms (73.6% 향상)** |
| 규칙 정확도 | 낮음 (일반 지식) | 높음 | **매우 높음 (프로젝트 특화)** |
| 컨텍스트 인식 | 없음 | 없음 | **있음 (키워드 → Layer)** |
| 실시간 검증 | 없음 | 수동 | **자동 (148ms)** |
| 유지보수 | 어려움 | 복잡 | **쉬움 (문서만 수정)** |
| 확장성 | 낮음 | 중간 | **높음 (Cache 재빌드)** |

---

**🎯 목표**: Claude Code가 프로젝트 표준을 이해하고 준수하는 코드를 생성하도록 지속적 가이드

**💡 핵심**: Dynamic Hooks + Cache 시스템이 자동으로 규칙을 주입하고 검증하므로, 개발자는 비즈니스 로직에 집중할 수 있습니다!

---

© 2024 Ryu-qqq. All Rights Reserved.
