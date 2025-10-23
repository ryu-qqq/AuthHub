# Hook 로깅 시스템 가이드

Dynamic Hooks + Cache 시스템의 **실행 로그**를 확인하고 분석하는 방법입니다.

---

## 🎯 왜 로그가 필요한가?

Hook이 **실제로 작동하는지** 확인하기 위해서입니다:

1. **실행 여부 확인**: Hook이 트리거되었는지
2. **키워드 감지 검증**: 어떤 키워드가 어떤 Layer로 매핑되었는지
3. **Context Score**: 규칙 주입 여부 결정 근거
4. **검증 결과**: 생성된 코드가 규칙을 준수하는지

---

## 🚀 빠른 시작 (3분)

### 1. **테스트 입력**

Claude Code에서 다음과 같이 입력:

```
domain aggregate Order 만들어줘
```

### 2. **로그 확인**

터미널에서:

```bash
cd /Users/sangwon-ryu/claude-spring-standards

# 실시간 모니터링
./.claude/hooks/scripts/view-logs.sh -f
```

### 3. **예상 출력**

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Hook 실행 로그
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

실시간 모니터링 중... (Ctrl+C로 종료)

[2025-10-17 18:30:15] user-prompt-submit triggered
User Input: domain aggregate Order 만들어줘
  → Detected: aggregate → domain (+30 score)
  → Detected: domain context (+15 score)
  → Context Score: 45
  → Detected Layers: domain
  → Priority Filter:
  → Strategy: CACHE_BASED (inject-rules.py)
  → Injecting rules for layer: domain

[2025-10-17 18:30:22] after-tool-use triggered
File: domain/src/main/java/com/company/template/order/domain/model/Order.java
  → Detected Layer: DOMAIN
  → Running cache-based validation for layer: domain
  ✅ PASSED: No Lombok
  ✅ PASSED: Javadoc @author present
  ✅ PASSED: Pure Java (no Spring/JPA)
  → FINAL RESULT: ALL VALIDATIONS PASSED ✅
```

---

## 📊 로그 뷰어 사용법

### `view-logs.sh` 스크립트

**위치**: `.claude/hooks/scripts/view-logs.sh`

### 주요 기능

#### 1. **실시간 모니터링** (가장 유용!)

```bash
./view-logs.sh -f
```

- Hook이 실행될 때마다 즉시 로그 표시
- Ctrl+C로 종료
- **사용 사례**: Hook이 제대로 트리거되는지 실시간 확인

#### 2. **마지막 N줄 확인**

```bash
# 마지막 50줄 (기본)
./view-logs.sh

# 마지막 100줄
./view-logs.sh -n 100
```

- **사용 사례**: 최근 Hook 실행 내역 빠르게 확인

#### 3. **통계 정보**

```bash
./view-logs.sh -s
```

**출력 예시**:
```
📊 Hook 실행 통계
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

총 Hook 실행: 45 회
  - user-prompt-submit: 23 회
  - after-tool-use: 22 회

Layer 감지 통계:
  - DOMAIN: 15 회
  - APPLICATION: 5 회
  - ADAPTER-REST: 3 회

최근 Context Scores:
  - Context Score: 45
  - Context Score: 60
  - Context Score: 30

검증 결과:
  - ✅ Passed: 20
  - ❌ Failed: 2

로그 파일 정보:
  - 크기: 15.3 KB
  - 라인 수: 458
```

- **사용 사례**: 시스템이 얼마나 자주 사용되는지, 어떤 Layer가 많이 쓰이는지 분석

#### 4. **로그 삭제**

```bash
./view-logs.sh -c
```

- **사용 사례**: 테스트 후 로그 초기화

#### 5. **도움말**

```bash
./view-logs.sh -h
```

---

## 🔍 로그 분석 가이드

### Hook이 실행되지 않는 경우

**증상**: 로그 파일이 없거나 최근 로그가 없음

**원인 1: Context Score < 25**

로그에서 다음을 확인:
```
  → Context Score: 15
  → Strategy: SKIP (Low Context Score < 25)
```

**해결**:
- 키워드를 명확하게 입력 (예: "domain", "aggregate", "controller")
- 여러 키워드 조합 (예: "domain aggregate Order")

**원인 2: Hook 파일 권한 문제**

```bash
# 권한 확인
ls -la .claude/hooks/*.sh

# 권한 부여
chmod +x .claude/hooks/*.sh
```

### Hook은 실행되지만 규칙이 주입되지 않는 경우

**로그 확인**:
```
  → Strategy: SKIP (Low Context Score < 25)
```

**해결**: 키워드 추가
- "domain" → "domain aggregate"
- "controller" → "rest api controller"

### 검증이 실패하는 경우

**로그 예시**:
```
  ❌ FAILED: Lombok annotation detected!
  → FINAL RESULT: VALIDATION FAILED ❌
```

**확인 방법**:
```bash
# 실패한 검증 찾기
grep "FAILED" .claude/hooks/logs/hook-execution.log
```

**대응**:
1. 로그에서 실패 원인 확인
2. 코드 수정
3. 재검증

---

## 🧪 Hook 테스트 시나리오

### 시나리오 1: Domain Layer 테스트

**입력**:
```
domain aggregate Order 생성
```

**예상 로그**:
```
  → Detected: aggregate → domain (+30 score)
  → Detected: domain context (+15 score)
  → Context Score: 45
  → Injecting rules for layer: domain
```

### 시나리오 2: Application Layer 테스트

**입력**:
```
usecase CreateOrder 만들어줘
```

**예상 로그**:
```
  → Detected: usecase → application (+30 score)
  → Context Score: 30
  → Injecting rules for layer: application
```

### 시나리오 3: REST Controller 테스트

**입력**:
```
rest api controller OrderController
```

**예상 로그**:
```
  → Detected: controller → adapter-rest (+30 score)
  → Detected: api context (+15 score)
  → Context Score: 45
  → Injecting rules for layer: adapter-rest
```

### 시나리오 4: Zero-Tolerance 키워드 테스트

**입력**:
```
domain aggregate Order, Lombok 금지
```

**예상 로그**:
```
  → Detected: aggregate → domain (+30 score)
  → Detected: Zero-Tolerance keyword → critical priority (+20 score)
  → Context Score: 65
  → Priority Filter: critical
```

---

## 📈 로그 활용 Best Practices

### 1. **개발 중 실시간 모니터링**

```bash
# 터미널을 2개 띄우기
# 터미널 1: Claude Code
# 터미널 2: 실시간 로그 모니터링
./view-logs.sh -f
```

### 2. **통계 정기 확인**

```bash
# 매주 통계 확인
./view-logs.sh -s > weekly-stats.txt
```

### 3. **검증 실패 패턴 분석**

```bash
# 실패한 검증만 추출
grep "FAILED" .claude/hooks/logs/hook-execution.log > validation-failures.txt
```

### 4. **Context Score 튜닝**

```bash
# Context Score 분포 확인
grep "Context Score:" .claude/hooks/logs/hook-execution.log | \
  awk '{print $NF}' | sort -n | uniq -c
```

**출력 예시**:
```
  2 15
  5 30
 10 45
  3 60
```

→ 대부분 45점 (키워드 1개 + secondary 키워드)

---

## 🔧 고급 로그 분석

### 1. **Layer별 실행 빈도**

```bash
grep "Detected Layer:" .claude/hooks/logs/hook-execution.log | \
  awk '{print $NF}' | sort | uniq -c | sort -rn
```

**출력 예시**:
```
  15 DOMAIN
   8 APPLICATION
   5 ADAPTER-REST
   2 ADAPTER-PERSISTENCE
```

### 2. **시간대별 Hook 실행**

```bash
grep "triggered" .claude/hooks/logs/hook-execution.log | \
  awk '{print $1, $2}' | cut -d: -f1 | sort | uniq -c
```

**출력 예시**:
```
  10 [2025-10-17 09
  15 [2025-10-17 10
   8 [2025-10-17 11
```

### 3. **검증 성공률**

```bash
PASSED=$(grep -c "PASSED" .claude/hooks/logs/hook-execution.log)
FAILED=$(grep -c "FAILED" .claude/hooks/logs/hook-execution.log)
TOTAL=$((PASSED + FAILED))
SUCCESS_RATE=$(awk "BEGIN {printf \"%.2f\", ($PASSED/$TOTAL)*100}")

echo "검증 성공률: $SUCCESS_RATE%"
```

### 4. **가장 많이 감지된 키워드**

```bash
grep "Detected:" .claude/hooks/logs/hook-execution.log | \
  awk '{print $3}' | sort | uniq -c | sort -rn | head -10
```

---

## 🐛 문제 해결 체크리스트

### Hook이 전혀 실행되지 않음

- [ ] Hook 파일 권한 확인: `ls -la .claude/hooks/*.sh`
- [ ] Hook 파일 실행 권한 부여: `chmod +x .claude/hooks/*.sh`
- [ ] 로그 디렉토리 존재 확인: `ls -la .claude/hooks/logs/`
- [ ] Claude Code 재시작

### Hook은 실행되지만 로그가 없음

- [ ] 로그 파일 경로 확인: `.claude/hooks/logs/hook-execution.log`
- [ ] 파일 쓰기 권한 확인: `ls -la .claude/hooks/logs/`
- [ ] stderr 확인: Hook 스크립트 직접 실행해보기

### 규칙이 주입되지 않음

- [ ] Context Score 확인: `grep "Context Score:" hook-execution.log`
- [ ] 임계값 확인: Score >= 25인지
- [ ] 키워드 매핑 확인: `.claude/hooks/user-prompt-submit.sh` 라인 33-78

### 검증이 실패함

- [ ] 실패 원인 확인: `grep "FAILED" hook-execution.log`
- [ ] Lombok 사용 여부
- [ ] Javadoc @author/@since 누락
- [ ] Domain에서 Spring/JPA 사용

---

## 📚 참고 자료

- [Hook 로그 README](./logs/README.md) - 로그 파일 상세 설명
- [DYNAMIC_HOOKS_GUIDE.md](../../docs/DYNAMIC_HOOKS_GUIDE.md) - 전체 시스템 가이드
- [user-prompt-submit.sh](./../hooks/user-prompt-submit.sh) - 규칙 주입 Hook
- [after-tool-use.sh](./../hooks/after-tool-use.sh) - 검증 Hook

---

## 💡 Quick Tips

1. **개발 시 항상 실시간 모니터링**:
   ```bash
   ./view-logs.sh -f
   ```

2. **Hook 작동 여부 빠르게 확인**:
   ```bash
   ./view-logs.sh -s
   ```

3. **실패 원인 빠르게 찾기**:
   ```bash
   grep "FAILED" .claude/hooks/logs/hook-execution.log | tail -5
   ```

4. **Context Score 패턴 분석**:
   ```bash
   grep "Context Score:" .claude/hooks/logs/hook-execution.log | \
     awk '{print $NF}' | sort -n | uniq -c
   ```

---

**마지막 업데이트**: 2025-10-17
**다음 단계**: Hook 로그를 확인하며 실제 시스템 작동을 검증하세요!
