package com.ryuqq.authhub.adapter.out.persistence.userrole.repository;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * UserRoleJpaRepository - 사용자-역할 관계 JPA Repository (Command 전용)
 *
 * <p>JPA 기반 CUD 작업을 담당합니다.
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserRoleJpaRepository (JPA)
 *   <li>Query: UserRoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>Hard Delete:</strong>
 *
 * <ul>
 *   <li>관계 테이블이므로 Hard Delete 적용
 *   <li>delete 메서드로 물리적 삭제 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, Long> {

    /**
     * 사용자-역할 관계 삭제 (Hard Delete)
     *
     * @param userId 사용자 ID (String)
     * @param roleId 역할 ID (Long)
     */
    @Modifying
    @Query("DELETE FROM UserRoleJpaEntity ur WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") String userId, @Param("roleId") Long roleId);

    /**
     * 사용자의 모든 역할 관계 삭제 (Hard Delete - User 삭제 시 Cascade)
     *
     * @param userId 사용자 ID (String)
     */
    @Modifying
    @Query("DELETE FROM UserRoleJpaEntity ur WHERE ur.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);

    /**
     * 사용자-역할 관계 다건 삭제 (Hard Delete)
     *
     * @param userId 사용자 ID (String)
     * @param roleIds 역할 ID 목록 (Long)
     */
    @Modifying
    @Query("DELETE FROM UserRoleJpaEntity ur WHERE ur.userId = :userId AND ur.roleId IN :roleIds")
    void deleteAllByUserIdAndRoleIdIn(
            @Param("userId") String userId, @Param("roleIds") List<Long> roleIds);
}
