#!/bin/bash

# POC: Context Analyzer - 키워드 매칭 및 Context Score 계산

analyze_context() {
    local user_input="$1"
    local context_score=0
    local matched_docs=""

    # Primary Keywords (강한 매칭 +30)
    if echo "$user_input" | grep -qiE "(aggregate|애그리게이트)"; then
        context_score=$((context_score + 30))
        matched_docs="domain-aggregate-boundaries,"
    fi

    if echo "$user_input" | grep -qiE "(getter|체이닝|chaining)"; then
        context_score=$((context_score + 30))
        matched_docs="${matched_docs}domain-law-of-demeter-getter-chaining,"
    fi

    if echo "$user_input" | grep -qiE "(controller|컨트롤러)"; then
        context_score=$((context_score + 30))
        matched_docs="${matched_docs}adapter-rest-controller-design,"
    fi

    # Secondary Keywords (약한 매칭 +15)
    if echo "$user_input" | grep -qiE "(domain|도메인)"; then
        context_score=$((context_score + 15))
    fi

    if echo "$user_input" | grep -qiE "(validation|검증)"; then
        context_score=$((context_score + 15))
    fi

    # File Path Analysis (+40)
    if [[ -n "$TARGET_FILE" ]]; then
        if echo "$TARGET_FILE" | grep -q "domain/model"; then
            context_score=$((context_score + 40))
            matched_docs="${matched_docs}domain-aggregate-boundaries,domain-law-of-demeter-getter-chaining,"
        elif echo "$TARGET_FILE" | grep -q "adapter/in/web"; then
            context_score=$((context_score + 40))
            matched_docs="${matched_docs}adapter-rest-controller-design,adapter-rest-dto-patterns,"
        fi
    fi

    # 결과 출력
    echo "Context Score: $context_score"
    echo "Matched Docs: $matched_docs"

    # 주입 전략 결정
    if [[ $context_score -gt 80 ]]; then
        echo "Strategy: SPECIFIC (High Confidence)"
        echo "Inject: $matched_docs"
    elif [[ $context_score -gt 50 ]]; then
        echo "Strategy: LAYER_LEVEL (Medium Confidence)"
        echo "Inject: domain-all or adapter-all"
    else
        echo "Strategy: ZERO_TOLERANCE (Low Confidence)"
        echo "Inject: common-lombok-prohibition,common-javadoc-required"
    fi
}

# 테스트 케이스
echo "=== Test 1: Order Aggregate 만들어줘 ==="
analyze_context "Order Aggregate 만들어줘"

echo ""
echo "=== Test 2: Controller에 validation 추가해줘 ==="
analyze_context "Controller에 validation 추가해줘"

echo ""
echo "=== Test 3: domain/model/Order.java 수정 ==="
TARGET_FILE="domain/model/Order.java"
analyze_context "Order 수정해줘"
