# AuthHub Terraform 환경별 구성

## 디렉토리 구조

```
environments/
├── prod/                    # 프로덕션 환경
│   ├── ecr/                 # ECR Repository
│   ├── ecs-cluster/         # ECS Cluster
│   ├── ecs-web-api/         # ECS Service (web-api)
│   └── elasticache/         # Redis
│
└── stage/                   # 스테이징 환경
    ├── ecr/                 # ECR Repository
    ├── ecs-cluster/         # ECS Cluster
    ├── ecs-web-api/         # ECS Service (web-api)
    └── elasticache/         # Redis
```

## 환경별 차이점

| 항목 | Prod | Stage |
|------|------|-------|
| ECS CPU | 512 | 256 |
| ECS Memory | 1024 | 512 |
| Desired Count | 2 | 1 |
| AutoScaling Max | 10 | 3 |
| Log Retention | 30일 | 14일 |
| Snapshot Retention | 1일 | 0 (비활성화) |
| Capacity Provider | FARGATE 100% | FARGATE_SPOT 70% |

## 배포 순서

Stage 환경 최초 배포 시 다음 순서로 진행:

```bash
# 1. ECR Repository 생성
cd environments/stage/ecr
terraform init
terraform apply

# 2. ECS Cluster 생성
cd ../ecs-cluster
terraform init
terraform apply

# 3. ElastiCache (Redis) 생성
cd ../elasticache
terraform init
terraform apply

# 4. ECS Service 생성
cd ../ecs-web-api
terraform init
terraform apply
```

## 사전 요구사항 (Stage)

Stage 환경 배포 전 다음 AWS 리소스가 필요합니다:

### SSM Parameters
```bash
# RDS Endpoint (Stage RDS가 이미 존재한다면)
/shared/stage/rds/endpoint

# Service Token
/authhub/stage/security/service-token-secret
```

### Secrets Manager
```bash
# Stage RDS 인증 정보
stage-shared-mysql-auth
  {
    "username": "...",
    "password": "..."
  }

# Stage JWT Secret
authhub/stage/jwt/secret
  {
    "secret": "..."
  }

# Stage JWT RSA Keys
authhub/stage/jwt/rsa-keys
  {
    "private_key": "...",
    "public_key": "..."
  }
```

## Backend State 경로

| 환경 | 모듈 | State Key |
|------|------|-----------|
| prod | ecr | `authhub/ecr/terraform.tfstate` |
| prod | ecs-cluster | `authhub/ecs-cluster/terraform.tfstate` |
| prod | ecs-web-api | `authhub/ecs-web-api/terraform.tfstate` |
| prod | elasticache | `authhub/elasticache/terraform.tfstate` |
| stage | ecr | `authhub/stage/ecr/terraform.tfstate` |
| stage | ecs-cluster | `authhub/stage/ecs-cluster/terraform.tfstate` |
| stage | ecs-web-api | `authhub/stage/ecs-web-api/terraform.tfstate` |
| stage | elasticache | `authhub/stage/elasticache/terraform.tfstate` |

## 주의사항

1. **Prod Backend Key**: Prod 환경은 기존 state 호환성을 위해 레거시 경로 유지
2. **Shared Resources**: VPC, Service Discovery Namespace는 `/shared/` 경로의 SSM 파라미터 공유
3. **Stage RDS**: 기존 Stage RDS 엔드포인트를 SSM에 등록 필요
