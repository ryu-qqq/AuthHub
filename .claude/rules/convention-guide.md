# Convention Guide

> ⚠️ **규칙은 하드코딩되지 않습니다. MCP를 통해 동적으로 조회하세요.**

## 규칙 조회 방법

### 1. 레이어 목록 확인
```python
# 먼저 사용 가능한 레이어 목록 조회
list_tech_stacks()
# 또는
get_architecture(architecture_id=1)
```

### 2. 전체 규칙 개요
```python
# 조회된 레이어 코드로 validation_context 호출
validation_context(layers=["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"])
```

### 3. 레이어별 상세 규칙
```python
get_layer_detail(layer_code="조회된_레이어_코드")
```

### 4. 특정 규칙 상세
```python
get_rule(rule_code="규칙_코드")
```

### 5. 클래스별 템플릿 + 규칙
```python
module_context(module_id=N, class_type="클래스_타입")
```

## Serena 캐싱

조회된 규칙은 Serena Memory에 캐싱하여 재사용:

```python
# 캐시 키: convention-{layer}-{class_type}
serena.write_memory("convention-domain-aggregate", rules)
serena.read_memory("convention-domain-aggregate")
```

## Zero-Tolerance 빠른 참조

> 상세 규칙은 `validation_context()` 또는 `get_rule()` 로 조회

MCP를 통해 최신 규칙을 동적으로 조회하세요.
하드코딩된 규칙은 DB 변경 시 outdated 될 수 있습니다.
