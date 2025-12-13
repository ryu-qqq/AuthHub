# ========================================
# ECS web-api Outputs
# ========================================

output "service_name" {
  description = "ECS service name"
  value       = module.ecs_service.service_name
}

output "service_arn" {
  description = "ECS service ARN"
  value       = module.ecs_service.service_id
}

output "task_definition_arn" {
  description = "Task definition ARN"
  value       = module.ecs_service.task_definition_arn
}

output "service_discovery_name" {
  description = "Cloud Map service discovery DNS name"
  value       = "authhub.connectly.local"
}

output "internal_endpoint" {
  description = "Internal endpoint for service-to-service communication"
  value       = "http://authhub.connectly.local:8080"
}

output "log_group_name" {
  description = "CloudWatch log group name"
  value       = module.web_api_logs.log_group_name
}

output "kms_key_arn" {
  description = "KMS key ARN for logs encryption"
  value       = aws_kms_key.logs.arn
}
