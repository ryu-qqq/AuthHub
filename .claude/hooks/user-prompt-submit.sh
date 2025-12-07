#!/bin/bash

# =====================================================
# User Prompt Submit Hook (Minimal)
# Purpose: 커맨드 없이 작업 시 경고 + 가이드 주입
# =====================================================

USER_PROMPT="$1"

# ==================== 커맨드 감지 ====================

# 정식 워크플로우 커맨드 사용 중이면 통과
if echo "$USER_PROMPT" | grep -qE "^/(plan|impl|jira-task|jira-register|jira-status|kb-)"; then
    echo "$USER_PROMPT"
    exit 0
fi

# ==================== 위험 키워드 감지 ====================

# 구현/수정 관련 키워드 (커맨드 없이 직접 요청)
NEEDS_GUIDANCE=false
DETECTED_LAYER=""

# Domain Layer 키워드
if echo "$USER_PROMPT" | grep -qiE "(aggregate|domain|vo|value.?object|entity 만|domain.?event|domain.?exception)"; then
    NEEDS_GUIDANCE=true
    DETECTED_LAYER="domain"
fi

# Application Layer 키워드
if echo "$USER_PROMPT" | grep -qiE "(usecase|use.?case|service 만|facade|manager|assembler|factory)"; then
    NEEDS_GUIDANCE=true
    DETECTED_LAYER="application"
fi

# Persistence Layer 키워드
if echo "$USER_PROMPT" | grep -qiE "(repository|jpa.?entity|querydsl|adapter|mapper 만)"; then
    NEEDS_GUIDANCE=true
    DETECTED_LAYER="persistence"
fi

# REST API Layer 키워드
if echo "$USER_PROMPT" | grep -qiE "(controller|rest.?api|endpoint|request.?dto|response.?dto)"; then
    NEEDS_GUIDANCE=true
    DETECTED_LAYER="rest-api"
fi

# ==================== 가이드 주입 ====================

if [ "$NEEDS_GUIDANCE" = true ]; then
    cat << 'EOF'

⚠️ [WORKFLOW GUIDE]
커맨드 없이 직접 구현을 요청하셨습니다.

권장 워크플로우:
1. /plan "{기능}" - 먼저 계획 수립
2. /impl {layer} {feature} - 문서 기반 구현

또는 기존 코드 수정 시:
- /kb/{layer}/go - TDD 기반 수정

EOF

    # 감지된 레이어별 핵심 컨벤션 주입
    if [ "$DETECTED_LAYER" = "domain" ]; then
        cat << 'EOF'
[Domain Layer 핵심 규칙]
- Lombok 금지 (Pure Java)
- Law of Demeter (Getter 체이닝 금지)
- Tell Don't Ask (상태 묻지 말고 행동 요청)
- VO는 record 사용
- 참조: docs/coding_convention/02-domain-layer/

EOF
    elif [ "$DETECTED_LAYER" = "application" ]; then
        cat << 'EOF'
[Application Layer 핵심 규칙]
- @Transactional 내 외부 API 호출 금지
- CQRS 분리 (Command/Query UseCase)
- DTO는 record 사용
- Assembler로 변환
- 참조: docs/coding_convention/03-application-layer/

EOF
    elif [ "$DETECTED_LAYER" = "persistence" ]; then
        cat << 'EOF'
[Persistence Layer 핵심 규칙]
- Long FK 전략 (JPA 관계 어노테이션 금지)
- QueryDSL DTO Projection 필수
- Lombok 금지
- Mapper 분리
- 참조: docs/coding_convention/04-persistence-layer/

EOF
    elif [ "$DETECTED_LAYER" = "rest-api" ]; then
        cat << 'EOF'
[REST API Layer 핵심 규칙]
- RESTful 설계
- Request/Response DTO 분리
- @Valid 필수
- TestRestTemplate 사용 (MockMvc 금지)
- 참조: docs/coding_convention/01-adapter-in-layer/

EOF
    fi
fi

# 원본 프롬프트 출력
echo "$USER_PROMPT"
