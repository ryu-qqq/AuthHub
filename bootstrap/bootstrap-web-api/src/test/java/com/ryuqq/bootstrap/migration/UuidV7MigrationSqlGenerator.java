package com.ryuqq.bootstrap.migration;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UUIDv7 마이그레이션 SQL 생성기
 *
 * <p>실행 방법:
 *
 * <pre>{@code
 * ./gradlew :bootstrap:bootstrap-web-api:test --tests "*.UuidV7MigrationSqlGenerator.generateMigrationSql"
 * }</pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Tag("migration")
@DisplayName("UUIDv7 마이그레이션 SQL 생성")
class UuidV7MigrationSqlGenerator {

    @Test
    @DisplayName("마이그레이션 SQL 생성")
    void generateMigrationSql() {
        System.out.println("-- ============================================================");
        System.out.println("-- UUIDv7 마이그레이션 SQL (자동 생성)");
        System.out.println("-- 생성 시간: " + LocalDateTime.now());
        System.out.println("-- ============================================================");
        System.out.println();
        System.out.println("SET FOREIGN_KEY_CHECKS = 0;");
        System.out.println();

        // 1. Tenant (1개)
        UUID tenantV7 = UuidCreator.getTimeOrderedEpoch();
        System.out.println("-- Tenant Migration");
        System.out.printf("SET @new_tenant_1 = UNHEX(REPLACE('%s', '-', ''));%n", tenantV7);
        System.out.println("UPDATE tenants SET tenant_id = @new_tenant_1 WHERE id = 1;");
        System.out.println();

        // 2. Organizations (2개) - tenant_id FK 업데이트 포함
        UUID org1V7 = UuidCreator.getTimeOrderedEpoch();
        UUID org2V7 = UuidCreator.getTimeOrderedEpoch();
        System.out.println("-- Organizations Migration");
        System.out.printf("SET @new_org_1 = UNHEX(REPLACE('%s', '-', ''));%n", org1V7);
        System.out.printf("SET @new_org_2 = UNHEX(REPLACE('%s', '-', ''));%n", org2V7);
        System.out.println(
                "UPDATE organizations SET organization_id = @new_org_1, tenant_id = @new_tenant_1"
                        + " WHERE id = 1;");
        System.out.println(
                "UPDATE organizations SET organization_id = @new_org_2, tenant_id = @new_tenant_1"
                        + " WHERE id = 2;");
        System.out.println();

        // 3. Roles (1개) - tenant_id FK 업데이트 포함
        UUID roleV7 = UuidCreator.getTimeOrderedEpoch();
        System.out.println("-- Roles Migration");
        System.out.printf("SET @new_role_1 = UNHEX(REPLACE('%s', '-', ''));%n", roleV7);
        System.out.println("-- GLOBAL role(tenant_id NULL)인 경우와 아닌 경우 분리");
        System.out.println(
                "UPDATE roles SET role_id = @new_role_1, tenant_id = @new_tenant_1 WHERE id = 1 AND"
                        + " tenant_id IS NOT NULL;");
        System.out.println(
                "UPDATE roles SET role_id = @new_role_1 WHERE id = 1 AND tenant_id IS NULL;");
        System.out.println();

        // 4. Users (2개) - tenant_id, organization_id FK 업데이트 포함
        UUID user1V7 = UuidCreator.getTimeOrderedEpoch();
        UUID user2V7 = UuidCreator.getTimeOrderedEpoch();
        System.out.println("-- Users Migration (organization_id는 실제 데이터에 맞게 조정)");
        System.out.printf("SET @new_user_1 = UNHEX(REPLACE('%s', '-', ''));%n", user1V7);
        System.out.printf("SET @new_user_2 = UNHEX(REPLACE('%s', '-', ''));%n", user2V7);
        System.out.println(
                "UPDATE users SET user_id = @new_user_1, tenant_id = @new_tenant_1, organization_id"
                        + " = @new_org_1 WHERE id = 1;");
        System.out.println(
                "UPDATE users SET user_id = @new_user_2, tenant_id = @new_tenant_1, organization_id"
                        + " = @new_org_1 WHERE id = 2;");
        System.out.println();

        // 5. UserRoles - user_id, role_id FK 업데이트
        System.out.println("-- User Roles Migration");
        System.out.println("UPDATE user_roles ur");
        System.out.println("  JOIN users u ON ur.user_id = u.user_id");
        System.out.println("  SET ur.user_id = @new_user_1, ur.role_id = @new_role_1");
        System.out.println("  WHERE u.id = 1;");
        System.out.println();

        System.out.println("SET FOREIGN_KEY_CHECKS = 1;");
        System.out.println();

        // 검증 쿼리
        System.out.println("-- ============================================================");
        System.out.println("-- 검증 쿼리 (UUIDv7 버전 확인: 13번째 문자가 '7')");
        System.out.println("-- ============================================================");
        System.out.println(
                "SELECT 'tenants' as tbl, HEX(tenant_id) as uuid_hex, SUBSTRING(HEX(tenant_id), 13,"
                        + " 1) = '7' as is_v7 FROM tenants;");
        System.out.println(
                "SELECT 'organizations' as tbl, HEX(organization_id) as uuid_hex,"
                    + " SUBSTRING(HEX(organization_id), 13, 1) = '7' as is_v7 FROM organizations;");
        System.out.println(
                "SELECT 'users' as tbl, HEX(user_id) as uuid_hex, SUBSTRING(HEX(user_id), 13, 1) ="
                        + " '7' as is_v7 FROM users;");
        System.out.println(
                "SELECT 'roles' as tbl, HEX(role_id) as uuid_hex, SUBSTRING(HEX(role_id), 13, 1) ="
                        + " '7' as is_v7 FROM roles;");
        System.out.println();

        // UUID 값 요약
        System.out.println("-- ============================================================");
        System.out.println("-- 생성된 UUIDv7 값 요약");
        System.out.println("-- ============================================================");
        System.out.println("-- Tenant:  " + tenantV7);
        System.out.println("-- Org 1:   " + org1V7);
        System.out.println("-- Org 2:   " + org2V7);
        System.out.println("-- Role:    " + roleV7);
        System.out.println("-- User 1:  " + user1V7);
        System.out.println("-- User 2:  " + user2V7);
    }
}
