package com.ryuqq.authhub.application.permission.manager.query;

import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionReadManager - 조회 전용 트랜잭션 관리
 *
 * <p>QueryPort에 대한 읽기 전용 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 클래스 레벨
 *   <li>단일 QueryPort만 의존
 *   <li>비즈니스 로직 금지 (단순 조회 및 예외 변환만)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class PermissionReadManager {

    private final PermissionQueryPort queryPort;

    public PermissionReadManager(PermissionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Permission 조회 (없으면 예외)
     *
     * @param permissionId 권한 ID
     * @return Permission
     * @throws PermissionNotFoundException 권한이 없는 경우
     */
    public Permission getById(PermissionId permissionId) {
        return queryPort
                .findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException(permissionId));
    }

    /**
     * 권한 키로 Permission 조회 (없으면 예외)
     *
     * @param key 권한 키
     * @return Permission
     * @throws PermissionNotFoundException 권한이 없는 경우
     */
    public Permission getByKey(PermissionKey key) {
        return queryPort
                .findByKey(key)
                .orElseThrow(() -> new PermissionNotFoundException(key.value()));
    }

    /**
     * 권한 키 존재 여부 확인
     *
     * @param key 권한 키
     * @return 존재 여부
     */
    public boolean existsByKey(PermissionKey key) {
        return queryPort.existsByKey(key);
    }

    /**
     * 권한 검색
     *
     * @param query 검색 조건
     * @return Permission 목록
     */
    public List<Permission> search(SearchPermissionsQuery query) {
        return queryPort.search(query);
    }

    /**
     * 여러 ID로 권한 목록 조회
     *
     * @param permissionIds 권한 ID Set
     * @return Permission 목록
     */
    public List<Permission> findAllByIds(Set<PermissionId> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        return queryPort.findAllByIds(permissionIds);
    }
}
