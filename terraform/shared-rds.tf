# ============================================================================
# Shared Infrastructure Reference: RDS MySQL
# ============================================================================
# References centrally managed RDS from Infrastructure repository
# Credentials accessed via Secrets Manager
# ============================================================================

# RDS Credentials (from Secrets Manager - AuthHub specific)
data "aws_secretsmanager_secret" "rds" {
  name = "authhub/rds/credentials"
}

data "aws_secretsmanager_secret_version" "rds" {
  secret_id = data.aws_secretsmanager_secret.rds.id
}

# ============================================================================
# Local Variables
# ============================================================================

locals {
  rds_credentials = jsondecode(data.aws_secretsmanager_secret_version.rds.secret_string)
  rds_username    = local.rds_credentials.username
  rds_password    = local.rds_credentials.password
  rds_dbname      = local.rds_credentials.dbname
  rds_host        = "prod-shared-mysql.cfacertspqbw.ap-northeast-2.rds.amazonaws.com"
  rds_port        = "3306"
  rds_endpoint    = "${local.rds_host}:${local.rds_port}"
}
