package com.ryuqq.authhub.application.permission.port.out.query;

import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import java.util.List;
import java.util.Optional;

/**
 * PermissionQueryPort - 권한 조회 Port (Port-Out)
 *
 * <p>권한 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}QueryPort} 네이밍
 *   <li>Domain Aggregate/VO 파라미터/반환
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionQueryPort {

    /**
     * ID로 Permission 조회
     *
     * @param permissionId 권한 ID
     * @return Optional Permission
     */
    Optional<Permission> findById(PermissionId permissionId);

    /**
     * 권한 키로 Permission 조회
     *
     * @param key 권한 키 (resource:action)
     * @return Optional Permission
     */
    Optional<Permission> findByKey(PermissionKey key);

    /**
     * 권한 키 존재 여부 확인
     *
     * @param key 권한 키 (resource:action)
     * @return 존재 여부
     */
    boolean existsByKey(PermissionKey key);

    /**
     * 권한 검색
     *
     * @param query 검색 조건
     * @return Permission 목록
     */
    List<Permission> search(SearchPermissionsQuery query);

    /**
     * 권한 검색 총 개수
     *
     * @param query 검색 조건
     * @return 총 개수
     */
    long count(SearchPermissionsQuery query);

    /**
     * 여러 ID로 권한 목록 조회
     *
     * @param permissionIds 권한 ID Set
     * @return Permission 목록
     */
    List<Permission> findAllByIds(java.util.Set<PermissionId> permissionIds);
}
