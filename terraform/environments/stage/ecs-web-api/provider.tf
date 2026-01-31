# ========================================
# Terraform Provider Configuration - STAGE
# ========================================

terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "prod-connectly"
    key            = "authhub/stage/ecs-web-api/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "prod-connectly-tf-lock"
    encrypt        = true
    kms_key_id     = "arn:aws:kms:ap-northeast-2:646886795421:key/086b1677-614f-46ba-863e-23c215fb5010"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

# ========================================
# Common Variables
# ========================================
variable "project_name" {
  description = "Project name"
  type        = string
  default     = "authhub"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "stage"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"
}

variable "web_api_cpu" {
  description = "CPU units for web-api task"
  type        = number
  default     = 512
}

variable "web_api_memory" {
  description = "Memory for web-api task"
  type        = number
  default     = 1024
}

variable "web_api_desired_count" {
  description = "Desired count for web-api service"
  type        = number
  default     = 1
}

variable "image_tag" {
  description = "Docker image tag to deploy"
  type        = string
  default     = "web-api-3-3a325dc"

  validation {
    condition     = can(regex("^web-api-[0-9]+-[a-z0-9]+$", var.image_tag))
    error_message = "Image tag must follow format: web-api-{build-number}-{git-sha}"
  }
}

# ========================================
# Shared Resource References (SSM)
# ========================================
data "aws_ssm_parameter" "vpc_id" {
  name = "/shared/network/vpc-id"
}

data "aws_ssm_parameter" "private_subnets" {
  name = "/shared/network/private-subnets"
}

# ========================================
# JWT Configuration (Stage용 - 별도 관리 권장)
# ========================================
data "aws_secretsmanager_secret" "jwt" {
  name = "authhub/stage/jwt/secret"
}

data "aws_secretsmanager_secret" "jwt_rsa" {
  name = "authhub/stage/jwt/rsa-keys"
}

# ========================================
# RDS Configuration (Stage MySQL)
# ========================================
# Stage RDS: staging-shared-mysql.cfacertspqbw.ap-northeast-2.rds.amazonaws.com
data "aws_secretsmanager_secret" "rds" {
  name = "stage-shared-mysql-auth"
}

data "aws_secretsmanager_secret_version" "rds" {
  secret_id = data.aws_secretsmanager_secret.rds.id
}

# ========================================
# Monitoring Configuration (AMP - shared)
# ========================================
data "aws_ssm_parameter" "amp_workspace_arn" {
  name = "/shared/monitoring/amp-workspace-arn"
}

data "aws_ssm_parameter" "amp_remote_write_url" {
  name = "/shared/monitoring/amp-remote-write-url"
}

# ========================================
# Redis Configuration (Stage)
# ========================================
# Stage Redis: stage-shared-redis (TLS 비활성화, 비밀번호 없음)
data "aws_ssm_parameter" "redis_endpoint" {
  name = "/shared/stage/elasticache/redis-endpoint"
}

data "aws_ssm_parameter" "redis_port" {
  name = "/shared/stage/elasticache/redis-port"
}

# ========================================
# Service Token Configuration
# ========================================
data "aws_ssm_parameter" "service_token_secret" {
  name = "/${var.project_name}/stage/security/service-token-secret"  # stage 환경 고정 (SSM 경로)
}

# ========================================
# Locals
# ========================================
locals {
  vpc_id          = data.aws_ssm_parameter.vpc_id.value
  private_subnets = split(",", data.aws_ssm_parameter.private_subnets.value)

  # RDS Configuration (Stage MySQL)
  rds_credentials = jsondecode(data.aws_secretsmanager_secret_version.rds.secret_string)
  rds_host        = "staging-shared-mysql.cfacertspqbw.ap-northeast-2.rds.amazonaws.com"
  rds_port        = "3306"
  rds_dbname      = "auth"
  rds_username    = local.rds_credentials.username

  # Redis Configuration (Stage - stage-shared-redis SSM 참조)
  redis_host = data.aws_ssm_parameter.redis_endpoint.value
  redis_port = tonumber(data.aws_ssm_parameter.redis_port.value)

  # AMP Configuration
  amp_workspace_arn    = data.aws_ssm_parameter.amp_workspace_arn.value
  amp_remote_write_url = data.aws_ssm_parameter.amp_remote_write_url.value
}
