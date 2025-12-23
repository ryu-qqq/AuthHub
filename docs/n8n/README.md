# n8n Permission Approval Workflow 설정 가이드

AuthHub 권한 검증 실패 시 Slack 승인 → 자동 권한 등록 → GitHub Actions 재실행 워크플로우입니다.

---

## 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        CI/CD Permission Validation Flow                      │
└─────────────────────────────────────────────────────────────────────────────┘

GitHub Actions                n8n                         AuthHub
┌──────────────┐         ┌──────────────┐            ┌──────────────┐
│ Permission   │         │  Webhook     │            │  /validate   │
│ Scanner      │────────▶│  Trigger     │───────────▶│    API       │
│ 실행         │ (실패시) │              │            │              │
└──────────────┘         └──────┬───────┘            └──────────────┘
                               │
                               ▼
                        ┌──────────────┐
                        │  Slack       │
                        │  승인 요청    │◀─────┐
                        └──────┬───────┘      │
                               │              │
        ┌──────────────────────┼──────────────┴───────────────────────┐
        │                      │                                       │
        ▼                      ▼                                       │
┌──────────────┐         ┌──────────────┐                             │
│   거부       │         │   승인       │                             │
│   처리       │         │   처리       │                             │
└──────────────┘         └──────┬───────┘                             │
                               │                                       │
                               ▼                                       │
                        ┌──────────────┐            ┌──────────────┐  │
                        │  AuthHub     │            │  GitHub      │  │
                        │  /usages API │───────────▶│  Rerun       │──┘
                        │  (권한 등록)  │            │  Workflow    │
                        └──────────────┘            └──────────────┘
```

---

## 1. 사전 준비

### 1.1 필요한 계정/서비스
- n8n 인스턴스 (Self-hosted 또는 Cloud)
- Slack Workspace + Bot 앱
- GitHub Personal Access Token
- AuthHub 서비스 토큰

### 1.2 n8n 버전
- 최소 버전: v1.0.0 이상
- 권장 버전: v1.20.0 이상 (Webhook 노드 v2 지원)

---

## 2. 환경 변수 설정

n8n 서버에 다음 환경 변수를 설정합니다.

### 2.1 Docker Compose 예시

```yaml
version: '3.8'
services:
  n8n:
    image: n8nio/n8n:latest
    environment:
      - N8N_HOST=n8n.your-domain.com
      - N8N_PORT=5678
      - N8N_PROTOCOL=https
      - WEBHOOK_URL=https://n8n.your-domain.com/

      # AuthHub 설정
      - AUTHHUB_URL=https://auth.your-domain.com
      - SERVICE_TOKEN=your-service-token-here

      # Slack 설정
      - SLACK_CHANNEL_ID=C01234567890

      # GitHub 설정
      - GITHUB_TOKEN=ghp_xxxxxxxxxxxx
      - GITHUB_OWNER=your-org
      - GITHUB_REPO=your-repo
    ports:
      - "5678:5678"
    volumes:
      - n8n_data:/home/node/.n8n
```

### 2.2 환경 변수 설명

| 변수명 | 설명 | 예시 |
|--------|------|------|
| `AUTHHUB_URL` | AuthHub 서버 URL | `https://auth.example.com` |
| `SERVICE_TOKEN` | AuthHub 서비스 인증 토큰 | `eyJhbGciOiJIUzI1NiIs...` |
| `SLACK_CHANNEL_ID` | 알림 받을 Slack 채널 ID | `C01234567890` |
| `GITHUB_TOKEN` | GitHub Personal Access Token | `ghp_xxxxx` |
| `GITHUB_OWNER` | GitHub 조직/사용자명 | `ryu-qqq` |
| `GITHUB_REPO` | 대상 레포지토리명 | `FileFlow` |

---

## 3. Credentials 설정

### 3.1 Slack API Credential

1. https://api.slack.com/apps 에서 앱 생성
2. OAuth & Permissions 메뉴에서 Bot Token Scopes 추가:
   - `chat:write` - 메시지 전송
   - `chat:write.public` - Public 채널에 전송
3. Bot User OAuth Token 복사
4. n8n에서 Credentials 생성:
   - Type: `Slack API`
   - Access Token: Bot User OAuth Token 붙여넣기

### 3.2 Slack Interactivity 설정

1. Slack App 설정 > Interactivity & Shortcuts
2. Interactivity: **On**
3. Request URL: `https://n8n.your-domain.com/webhook/slack-interaction`

### 3.3 GitHub Token 권한

GitHub Personal Access Token에 필요한 권한:
- `repo` - Full control of private repositories
- `workflow` - Update GitHub Action workflows

생성 방법:
1. GitHub > Settings > Developer settings > Personal access tokens > Tokens (classic)
2. Generate new token (classic)
3. 위 권한 체크 후 생성

---

## 4. 워크플로우 Import

### 4.1 Import 방법

1. n8n 대시보드 접속
2. Workflows > Add Workflow > Import from File
3. `permission-approval-workflow.json` 파일 선택
4. Import 완료

### 4.2 Import 후 설정

1. **Slack Credential 연결**:
   - `Slack - Approval Request` 노드 클릭
   - Credential 드롭다운에서 생성한 Slack API 선택

2. **Webhook URL 확인**:
   - `Webhook - CI/CD Trigger` 노드 클릭
   - Production URL 복사: `https://n8n.your-domain.com/webhook/permission-validation-failed`
   - `Webhook - Slack Interaction` 노드 클릭
   - Production URL 확인: `https://n8n.your-domain.com/webhook/slack-interaction`

3. **워크플로우 활성화**:
   - 우측 상단 토글을 Active로 변경

---

## 5. GitHub Actions 연동

### 5.1 GitHub Secrets 설정

Repository > Settings > Secrets and variables > Actions:

```
AUTHHUB_URL=https://auth.your-domain.com
SERVICE_TOKEN=your-service-token
N8N_WEBHOOK_URL=https://n8n.your-domain.com/webhook/permission-validation-failed
```

### 5.2 워크플로우 파일 예시

`.github/workflows/permission-validation.yml`:

```yaml
name: Permission Validation

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [main, develop]

jobs:
  validate-permissions:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run Permission Scanner
        run: |
          ./gradlew scanPermissions

      - name: Validate with AuthHub
        id: validate
        run: |
          RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
            "${{ secrets.AUTHHUB_URL }}/api/v1/auth/internal/permissions/validate" \
            -H "X-Service-Token: ${{ secrets.SERVICE_TOKEN }}" \
            -H "Content-Type: application/json" \
            -d @build/permissions/permissions.json)

          HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
          BODY=$(echo "$RESPONSE" | sed '$d')

          echo "response=$BODY" >> $GITHUB_OUTPUT
          echo "http_code=$HTTP_CODE" >> $GITHUB_OUTPUT

          # valid 필드 추출
          VALID=$(echo "$BODY" | jq -r '.data.valid')
          echo "valid=$VALID" >> $GITHUB_OUTPUT

      - name: Check Validation Result
        if: steps.validate.outputs.valid == 'false'
        run: |
          echo "::error::Permission validation failed. Missing permissions detected."

          # n8n 웹훅으로 알림 전송
          curl -X POST "${{ secrets.N8N_WEBHOOK_URL }}" \
            -H "Content-Type: application/json" \
            -d '{
              "serviceName": "${{ github.repository }}",
              "branch": "${{ github.ref_name }}",
              "commitSha": "${{ github.sha }}",
              "runId": "${{ github.run_id }}",
              "totalCount": '"$(echo '${{ steps.validate.outputs.response }}' | jq '.data.totalCount')"',
              "existingCount": '"$(echo '${{ steps.validate.outputs.response }}' | jq '.data.existingCount')"',
              "missingCount": '"$(echo '${{ steps.validate.outputs.response }}' | jq '.data.missingCount')"',
              "missing": '"$(echo '${{ steps.validate.outputs.response }}' | jq '.data.missing')"'
            }'

          exit 1

      - name: Validation Passed
        if: steps.validate.outputs.valid == 'true'
        run: |
          echo "::notice::All permissions validated successfully!"
```

---

## 6. 테스트 방법

### 6.1 로컬 테스트 (curl)

```bash
# 1. n8n 웹훅 직접 호출 테스트
curl -X POST https://n8n.your-domain.com/webhook/permission-validation-failed \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "test-service",
    "branch": "feature/test",
    "commitSha": "abc123",
    "runId": "12345678",
    "totalCount": 5,
    "existingCount": 3,
    "missingCount": 2,
    "missing": [
      {"key": "product:read", "locations": ["ProductController.java:45"]},
      {"key": "product:admin", "locations": ["AdminController.java:23"]}
    ]
  }'
```

### 6.2 예상 결과

1. Slack 채널에 승인 요청 메시지 도착
2. "승인" 버튼 클릭 시:
   - AuthHub에 권한 등록
   - GitHub Actions 워크플로우 재실행
   - Slack 메시지 "승인됨"으로 업데이트
3. "거부" 버튼 클릭 시:
   - Slack 메시지 "거부됨"으로 업데이트
   - 추가 작업 없음

### 6.3 디버깅

n8n Execution History에서 각 노드별 입/출력 확인 가능:
- Workflows > 워크플로우 선택 > Executions 탭

---

## 7. 문제 해결

### 7.1 Slack 메시지가 안 오는 경우

```
원인: Bot Token 권한 부족 또는 채널 미초대
해결:
1. Slack App의 Bot Token Scopes 확인
2. 채널에 봇 초대: /invite @your-bot-name
```

### 7.2 GitHub Rerun이 안 되는 경우

```
원인: GitHub Token 권한 부족
해결:
1. Token에 workflow 권한 확인
2. Repository가 private인 경우 repo 권한도 필요
```

### 7.3 AuthHub API 호출 실패

```
원인: Service Token 만료 또는 잘못된 URL
해결:
1. SERVICE_TOKEN 환경변수 값 확인
2. AUTHHUB_URL이 /api/v1/auth로 끝나지 않는지 확인
```

---

## 8. 보안 고려사항

### 8.1 Webhook 보안

프로덕션 환경에서는 Webhook에 인증 추가 권장:

```javascript
// Webhook 노드 설정에서 Header Auth 사용
// 또는 Custom Code로 시크릿 검증
const secret = $env.WEBHOOK_SECRET;
const signature = $input.first().headers['x-webhook-signature'];

if (!verifySignature(signature, secret)) {
  throw new Error('Invalid webhook signature');
}
```

### 8.2 환경 변수 암호화

민감한 정보는 n8n Credentials로 관리하거나 외부 시크릿 매니저 연동 권장.

---

## 9. 확장 가능성

### 9.1 멀티 레포지토리 지원

`GITHUB_OWNER`와 `GITHUB_REPO`를 webhook payload에서 동적으로 받도록 수정 가능.

### 9.2 승인 권한 제한

Slack User ID 기반으로 승인 가능자 제한:

```javascript
const allowedApprovers = ['U12345', 'U67890'];
const userId = payload.user.id;

if (!allowedApprovers.includes(userId)) {
  throw new Error('Unauthorized approver');
}
```

### 9.3 감사 로그

권한 승인/거부 이력을 별도 데이터베이스나 로깅 시스템에 저장 가능.

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0.0 | 2024-01-01 | 초기 버전 |
