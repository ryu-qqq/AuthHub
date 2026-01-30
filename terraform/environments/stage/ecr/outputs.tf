# ========================================
# ECR Outputs - STAGE
# ========================================

output "web_api_repository_url" {
  description = "ECR repository URL for web-api"
  value       = module.ecr_web_api.repository_url
}

output "web_api_repository_arn" {
  description = "ECR repository ARN for web-api"
  value       = module.ecr_web_api.repository_arn
}
