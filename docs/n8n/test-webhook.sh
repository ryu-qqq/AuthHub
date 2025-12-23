#!/bin/bash
# n8n Permission Approval Workflow 테스트 스크립트
# Usage: ./test-webhook.sh [N8N_WEBHOOK_URL]

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 기본값
N8N_WEBHOOK_URL="${1:-http://localhost:5678/webhook/permission-validation-failed}"

echo -e "${YELLOW}==================================${NC}"
echo -e "${YELLOW}  n8n Webhook 테스트 스크립트${NC}"
echo -e "${YELLOW}==================================${NC}"
echo ""
echo -e "Webhook URL: ${GREEN}${N8N_WEBHOOK_URL}${NC}"
echo ""

# 테스트 1: 누락 권한이 있는 경우
echo -e "${YELLOW}[테스트 1] 누락 권한이 있는 시나리오${NC}"
echo "-----------------------------------"

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "${N8N_WEBHOOK_URL}" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "branch": "feature/permission-test",
    "commitSha": "abc123def456",
    "runId": "9876543210",
    "totalCount": 5,
    "existingCount": 3,
    "missingCount": 2,
    "missing": [
      {"key": "product:read", "locations": ["ProductController.java:45"]},
      {"key": "product:admin", "locations": ["AdminController.java:23"]}
    ]
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
  echo -e "Status: ${GREEN}$HTTP_CODE OK${NC}"
  echo -e "Response: $BODY"
  echo -e "${GREEN}[PASS]${NC} Slack 채널에서 승인 요청 메시지를 확인하세요."
else
  echo -e "Status: ${RED}$HTTP_CODE${NC}"
  echo -e "Response: $BODY"
  echo -e "${RED}[FAIL]${NC} Webhook 호출 실패"
fi

echo ""

# 테스트 2: 누락 권한이 없는 경우
echo -e "${YELLOW}[테스트 2] 모든 권한이 존재하는 시나리오${NC}"
echo "-----------------------------------"

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "${N8N_WEBHOOK_URL}" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "branch": "feature/all-valid",
    "commitSha": "xyz789",
    "runId": "1234567890",
    "totalCount": 3,
    "existingCount": 3,
    "missingCount": 0,
    "missing": []
  }')

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
  echo -e "Status: ${GREEN}$HTTP_CODE OK${NC}"
  echo -e "Response: $BODY"
  echo -e "${GREEN}[PASS]${NC} 누락 권한 없음 - Slack 메시지 없음이 정상입니다."
else
  echo -e "Status: ${RED}$HTTP_CODE${NC}"
  echo -e "Response: $BODY"
  echo -e "${RED}[FAIL]${NC} Webhook 호출 실패"
fi

echo ""
echo -e "${YELLOW}==================================${NC}"
echo -e "${YELLOW}  테스트 완료${NC}"
echo -e "${YELLOW}==================================${NC}"
echo ""
echo "다음 단계:"
echo "1. Slack 채널에서 승인 요청 메시지 확인"
echo "2. '승인' 또는 '거부' 버튼 클릭"
echo "3. n8n Executions에서 워크플로우 실행 결과 확인"
