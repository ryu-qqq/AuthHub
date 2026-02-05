package com.ryuqq.authhub.adapter.out.persistence.role.fixture;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;

/**
 * RoleJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final String DEFAULT_NAME = "ADMIN";
    private static final String DEFAULT_DISPLAY_NAME = "관리자";
    private static final String DEFAULT_DESCRIPTION = "시스템 관리자 역할";

    private RoleJpaEntityFixture() {}

    /** 기본 RoleJpaEntity 생성 */
    public static RoleJpaEntity create() {
        return RoleJpaEntity.of(
                DEFAULT_ROLE_ID,
                null,
                null,
                DEFAULT_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 테넌트로 RoleJpaEntity 생성 */
    public static RoleJpaEntity createWithTenant(String tenantId) {
        return RoleJpaEntity.of(
                DEFAULT_ROLE_ID,
                tenantId,
                null,
                DEFAULT_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.TENANT,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 기본 테넌트로 RoleJpaEntity 생성 (파라미터 없음) */
    public static RoleJpaEntity createWithTenant() {
        return createWithTenant("default-tenant-id");
    }

    /** 삭제된 RoleJpaEntity 생성 */
    public static RoleJpaEntity createDeleted() {
        return RoleJpaEntity.of(
                DEFAULT_ROLE_ID,
                null,
                null,
                DEFAULT_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 시스템 역할 RoleJpaEntity 생성 */
    public static RoleJpaEntity createSystemRole() {
        return RoleJpaEntity.of(
                DEFAULT_ROLE_ID,
                null,
                null,
                "SYSTEM_ADMIN",
                "시스템 관리자",
                "시스템 관리자 역할",
                RoleType.SYSTEM,
                RoleScope.GLOBAL,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 이름으로 RoleJpaEntity 생성 */
    public static RoleJpaEntity createWithName(String name) {
        return RoleJpaEntity.of(
                DEFAULT_ROLE_ID,
                null,
                null,
                name,
                name + " 표시명",
                name + " 설명",
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 ID로 RoleJpaEntity 생성 */
    public static RoleJpaEntity createWithId(Long roleId) {
        return RoleJpaEntity.of(
                roleId,
                null,
                null,
                DEFAULT_NAME + roleId,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                RoleType.CUSTOM,
                RoleScope.GLOBAL,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 Role ID 반환 */
    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }
}
