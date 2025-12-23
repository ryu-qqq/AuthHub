# AuthHub 연동 가이드

> 버전: v1.0.0
> 최종 수정: 2024-12-23
> 대상: FileFlow, CrawlingHub, MarketPlace 등 마이크로서비스

---

## 1. 개요

### 1.1 AuthHub란?

AuthHub는 중앙 집중식 인증/인가 시스템으로, 마이크로서비스 아키텍처에서 다음 기능을 제공합니다:

| 기능 | 설명 |
|------|------|
| **사용자 관리** | 사용자 CRUD, 상태 관리, 비밀번호 관리 |
| **역할 관리** | 역할 생성, 권한 할당, 사용자-역할 매핑 |
| **권한 관리** | 권한 등록, 검증, 사용 이력 추적 |
| **조직 관리** | 조직/테넌트 기반 멀티테넌시 지원 |
| **토큰 발급** | JWT Access/Refresh Token 발급 |

### 1.2 아키텍처

```
┌─────────────────────────────────────────────────────────────────────┐
│                          API Gateway                                 │
│  (JWT 검증 → X-User-Id, X-Roles 헤더 주입)                           │
└─────────────────┬───────────────────────────────────────────────────┘
                  │
    ┌─────────────┼─────────────┬─────────────────┐
    ▼             ▼             ▼                 ▼
┌────────┐  ┌────────┐   ┌────────────┐    ┌──────────┐
│FileFlow│  │Crawling│   │MarketPlace │    │  AuthHub │
│        │  │  Hub   │   │            │    │          │
└────────┘  └────────┘   └────────────┘    └──────────┘
    │             │             │
    └─────────────┴─────────────┘
          common-auth 라이브러리
```

---

## 2. 서비스 연동 방법

### 2.1 의존성 추가 (향후 구현 예정)

```gradle
// common-auth 라이브러리 의존성
implementation 'com.ryuqq:common-auth:1.0.0'
```

### 2.2 Gateway 헤더 기반 인증 (현재 방식)

Gateway에서 JWT 검증 후 다음 헤더를 주입합니다:

| 헤더 | 설명 | 예시 |
|------|------|------|
| `X-User-Id` | 사용자 UUID | `550e8400-e29b-41d4-a716-446655440000` |
| `X-Username` | 사용자명 | `admin@example.com` |
| `X-Roles` | 역할 목록 (콤마 구분) | `ADMIN,USER` |
| `X-Tenant-Id` | 테넌트 ID | `tenant-001` |
| `X-Organization-Id` | 조직 ID | `org-001` |

### 2.3 권한 체크 방법

#### Option A: @PreAuthorize 사용 (권장)

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @PreAuthorize("hasAuthority('product:read')")
    @GetMapping
    public List<Product> getProducts() {
        // ...
    }

    @PreAuthorize("hasAuthority('product:write')")
    @PostMapping
    public Product createProduct(@RequestBody CreateProductRequest request) {
        // ...
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable UUID id) {
        // ...
    }
}
```

#### Option B: 프로그래밍 방식

```java
@Service
public class ProductService {

    private final AuthorizationChecker authChecker;

    public Product getProduct(UUID id) {
        authChecker.requireAuthority("product:read");
        // ...
    }
}
```

---

## 3. CI/CD 권한 검증 연동

### 3.1 흐름도

```
┌─────────────────────────────────────────────────────────────────────┐
│                      CI/CD Pipeline (GitHub Actions)                 │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
             ┌──────────┐   ┌──────────┐   ┌──────────────┐
             │  Build   │   │Permission│   │   Deploy     │
             │  & Test  │   │ Validate │   │              │
             └──────────┘   └────┬─────┘   └──────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ POST /internal/        │
                    │ permissions/validate   │
                    └────────────┬───────────┘
                                 │
                  ┌──────────────┴──────────────┐
                  ▼                             ▼
           ┌──────────┐                  ┌──────────────┐
           │valid:true│                  │ valid:false  │
           │ (통과)   │                  │ (누락 발견)  │
           └──────────┘                  └──────┬───────┘
                                                │
                                                ▼
                                    ┌───────────────────┐
                                    │ n8n 워크플로우    │
                                    │ (Slack 알림/승인) │
                                    └─────────┬─────────┘
                                              │
                                              ▼
                                    ┌───────────────────┐
                                    │ POST /permissions │
                                    │ /{key}/usages     │
                                    └───────────────────┘
```

### 3.2 Permission Scanner 설정

각 서비스에 Permission Scanner를 추가하여 `@PreAuthorize` 어노테이션을 추출합니다.

#### Gradle 설정

```gradle
// build.gradle
task scanPermissions(type: JavaExec) {
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.ryuqq.common.auth.PermissionScanner'
    args = ['com.ryuqq.fileflow', 'build/permissions/permissions.json']
}

// 빌드 시 자동 실행
build.finalizedBy(scanPermissions)
```

#### 스캔 결과 (permissions.json)

```json
{
  "serviceName": "fileflow-service",
  "scannedAt": "2024-12-23T10:30:00Z",
  "permissions": [
    {
      "key": "file:upload",
      "locations": ["FileController.java:45", "FileController.java:67"]
    },
    {
      "key": "file:download",
      "locations": ["FileController.java:89"]
    },
    {
      "key": "file:delete",
      "locations": ["FileController.java:112"]
    }
  ]
}
```

### 3.3 GitHub Actions Workflow

```yaml
# .github/workflows/permission-validate.yml
name: Permission Validation

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  validate-permissions:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build and Scan Permissions
        run: ./gradlew build scanPermissions

      - name: Validate Permissions with AuthHub
        id: validate
        run: |
          RESPONSE=$(curl -s -X POST \
            "${{ secrets.AUTHHUB_URL }}/api/v1/auth/internal/permissions/validate" \
            -H "X-Service-Token: ${{ secrets.SERVICE_TOKEN }}" \
            -H "Content-Type: application/json" \
            -d @build/permissions/permissions.json)

          echo "response=$RESPONSE" >> $GITHUB_OUTPUT

          VALID=$(echo $RESPONSE | jq -r '.data.valid')
          if [ "$VALID" != "true" ]; then
            echo "::warning::Missing permissions detected"
            echo $RESPONSE | jq '.data.missing[]'
            exit 1
          fi

      - name: Trigger n8n Workflow (on failure)
        if: failure()
        run: |
          curl -X POST "${{ secrets.N8N_WEBHOOK_URL }}" \
            -H "Content-Type: application/json" \
            -d '{
              "service": "${{ github.repository }}",
              "branch": "${{ github.ref_name }}",
              "permissions": ${{ steps.validate.outputs.response }}
            }'
```

### 3.4 API 상세

#### POST /api/v1/auth/internal/permissions/validate

**요청**
```json
{
  "serviceName": "fileflow-service",
  "permissions": [
    {
      "key": "file:upload",
      "locations": ["FileController.java:45"]
    },
    {
      "key": "file:download",
      "locations": ["FileController.java:89"]
    }
  ]
}
```

**응답 (성공)**
```json
{
  "success": true,
  "data": {
    "valid": true,
    "serviceName": "fileflow-service",
    "totalCount": 2,
    "existingCount": 2,
    "missingCount": 0,
    "existing": ["file:upload", "file:download"],
    "missing": [],
    "message": "All permissions are registered"
  }
}
```

**응답 (누락 발견)**
```json
{
  "success": true,
  "data": {
    "valid": false,
    "serviceName": "fileflow-service",
    "totalCount": 3,
    "existingCount": 2,
    "missingCount": 1,
    "existing": ["file:upload", "file:download"],
    "missing": ["file:admin"],
    "message": "1 permission(s) are not registered in AuthHub"
  }
}
```

#### POST /api/v1/auth/internal/permissions/{key}/usages

**요청**
```json
{
  "serviceName": "fileflow-service",
  "locations": ["FileController.java:45", "FileService.java:123"]
}
```

**응답**
```json
{
  "success": true,
  "data": {
    "usageId": "550e8400-e29b-41d4-a716-446655440000",
    "permissionKey": "file:upload",
    "serviceName": "fileflow-service",
    "locations": ["FileController.java:45", "FileService.java:123"],
    "lastScannedAt": "2024-12-23T10:30:00Z",
    "createdAt": "2024-12-23T10:30:00Z"
  }
}
```

---

## 4. n8n 연동 방안

### 4.1 워크플로우 개요

```
┌────────────────────────────────────────────────────────────────────┐
│                         n8n Workflow                                │
├────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐    ┌─────────────┐    ┌──────────────────────┐   │
│  │   Webhook   │───▶│  Slack 알림 │───▶│ 승인 대기 (Wait)     │   │
│  │ (CI/CD에서) │    │ (누락 권한) │    │                      │   │
│  └─────────────┘    └─────────────┘    └──────────┬───────────┘   │
│                                                    │               │
│                                          ┌────────┴────────┐       │
│                                          ▼                 ▼       │
│                                   ┌──────────┐      ┌──────────┐   │
│                                   │  승인    │      │  거부    │   │
│                                   └────┬─────┘      └────┬─────┘   │
│                                        │                  │        │
│                                        ▼                  ▼        │
│                            ┌───────────────────┐  ┌─────────────┐  │
│                            │ AuthHub 권한 등록 │  │ Slack 알림  │  │
│                            │ POST /usages      │  │ (거부됨)    │  │
│                            └───────────────────┘  └─────────────┘  │
│                                        │                           │
│                                        ▼                           │
│                            ┌───────────────────┐                   │
│                            │ GitHub API 호출   │                   │
│                            │ (PR 승인/재실행)  │                   │
│                            └───────────────────┘                   │
│                                                                     │
└────────────────────────────────────────────────────────────────────┘
```

### 4.2 Webhook Payload 구조

```json
{
  "event": "permission_validation_failed",
  "service": "ryu-qqq/FileFlow",
  "branch": "feature/new-upload-api",
  "pullRequest": 123,
  "triggeredBy": "developer@example.com",
  "validationResult": {
    "valid": false,
    "serviceName": "fileflow-service",
    "totalCount": 5,
    "existingCount": 4,
    "missingCount": 1,
    "existing": ["file:upload", "file:download", "file:list", "file:metadata"],
    "missing": ["file:admin"]
  },
  "timestamp": "2024-12-23T10:30:00Z"
}
```

### 4.3 Slack 메시지 템플릿

```json
{
  "blocks": [
    {
      "type": "header",
      "text": {
        "type": "plain_text",
        "text": "🔐 권한 승인 요청"
      }
    },
    {
      "type": "section",
      "fields": [
        {
          "type": "mrkdwn",
          "text": "*서비스:*\nFileFlow"
        },
        {
          "type": "mrkdwn",
          "text": "*브랜치:*\nfeature/new-upload-api"
        },
        {
          "type": "mrkdwn",
          "text": "*요청자:*\ndeveloper@example.com"
        },
        {
          "type": "mrkdwn",
          "text": "*누락 권한:*\n`file:admin`"
        }
      ]
    },
    {
      "type": "actions",
      "elements": [
        {
          "type": "button",
          "text": { "type": "plain_text", "text": "✅ 승인" },
          "style": "primary",
          "action_id": "approve_permission"
        },
        {
          "type": "button",
          "text": { "type": "plain_text", "text": "❌ 거부" },
          "style": "danger",
          "action_id": "reject_permission"
        },
        {
          "type": "button",
          "text": { "type": "plain_text", "text": "📋 상세 보기" },
          "url": "https://github.com/ryu-qqq/FileFlow/pull/123"
        }
      ]
    }
  ]
}
```

### 4.4 n8n 노드 구성

#### 1) Webhook 노드 (시작점)

```json
{
  "name": "Webhook - Permission Validation Failed",
  "type": "n8n-nodes-base.webhook",
  "parameters": {
    "httpMethod": "POST",
    "path": "permission-validation"
  }
}
```

#### 2) Slack 노드 (알림)

```json
{
  "name": "Slack - Send Approval Request",
  "type": "n8n-nodes-base.slack",
  "parameters": {
    "channel": "#permission-approvals",
    "blocksUi": "..."
  }
}
```

#### 3) Wait 노드 (승인 대기)

```json
{
  "name": "Wait for Approval",
  "type": "n8n-nodes-base.wait",
  "parameters": {
    "resume": "webhook",
    "options": {
      "webhookSuffix": "={{ $json.approvalId }}"
    }
  }
}
```

#### 4) HTTP Request 노드 (AuthHub 호출)

```json
{
  "name": "AuthHub - Register Permission",
  "type": "n8n-nodes-base.httpRequest",
  "parameters": {
    "method": "POST",
    "url": "={{ $env.AUTHHUB_URL }}/api/v1/auth/internal/permissions/{{ $json.missingPermission }}/usages",
    "authentication": "genericCredentialType",
    "genericAuthType": "httpHeaderAuth",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        { "name": "X-Service-Token", "value": "={{ $env.SERVICE_TOKEN }}" }
      ]
    },
    "sendBody": true,
    "bodyParameters": {
      "parameters": [
        { "name": "serviceName", "value": "={{ $json.serviceName }}" },
        { "name": "locations", "value": "={{ $json.locations }}" }
      ]
    }
  }
}
```

#### 5) GitHub 노드 (PR 재실행)

```json
{
  "name": "GitHub - Trigger Workflow Rerun",
  "type": "n8n-nodes-base.github",
  "parameters": {
    "operation": "rerunWorkflow",
    "owner": "={{ $json.owner }}",
    "repository": "={{ $json.repo }}",
    "workflowId": "={{ $json.workflowRunId }}"
  }
}
```

---

## 5. 권한 관리 Best Practices

### 5.1 권한 키 네이밍 규칙

```
{resource}:{action}

예시:
- product:read
- product:write
- product:delete
- order:create
- order:cancel
- user:admin
```

### 5.2 역할 계층 구조

```
SUPER_ADMIN
    └── ADMIN
         ├── USER_MANAGER (사용자 관리)
         ├── PRODUCT_MANAGER (상품 관리)
         └── ORDER_MANAGER (주문 관리)
              └── USER (일반 사용자)
```

### 5.3 권한 등록 전략

| 시점 | 방법 | 용도 |
|------|------|------|
| 초기 설정 | Admin API | 기본 권한 일괄 등록 |
| 개발 중 | Permission Scanner | 코드 기반 권한 추출 |
| 배포 전 | CI/CD 검증 | 누락 권한 자동 감지 |
| 승인 후 | n8n → AuthHub API | 권한 자동 등록 |

---

## 6. 문제 해결 가이드

### 6.1 FAQ

**Q: 권한 검증 실패 시 배포가 차단되나요?**

A: 설정에 따라 다릅니다.
- `exit 1` 설정 시: 배포 차단
- 경고만 발생: 배포는 진행되지만 Slack 알림

**Q: n8n 승인 없이 권한을 추가할 수 있나요?**

A: Admin API를 통해 직접 등록 가능합니다:
```bash
curl -X POST https://authhub.example.com/api/v1/admin/permissions \
  -H "Authorization: Bearer {admin-token}" \
  -d '{"key": "new:permission", "name": "New Permission", ...}'
```

**Q: Service Token은 어떻게 발급받나요?**

A: AuthHub 관리자에게 요청하거나, Admin 권한으로 발급:
```bash
curl -X POST https://authhub.example.com/api/v1/admin/service-tokens \
  -H "Authorization: Bearer {admin-token}" \
  -d '{"serviceName": "fileflow-service"}'
```

### 6.2 트러블슈팅

| 증상 | 원인 | 해결 방법 |
|------|------|----------|
| 401 Unauthorized | Service Token 무효 | Token 재발급 |
| 403 Forbidden | ROLE_SERVICE 권한 없음 | Token에 SERVICE 역할 확인 |
| 400 Bad Request | 요청 형식 오류 | JSON 스키마 확인 |
| 500 Internal Server Error | AuthHub 서버 오류 | 로그 확인 |

---

## 7. 연락처

- **AuthHub 관리자**: devops@example.com
- **Slack 채널**: #authhub-support
- **문서**: https://docs.example.com/authhub

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| v1.0.0 | 2024-12-23 | 초안 작성 |
