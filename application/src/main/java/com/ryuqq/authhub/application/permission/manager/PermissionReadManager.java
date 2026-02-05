package com.ryuqq.authhub.application.permission.manager;

import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionReadManager - 단일 Port 조회 관리
 *
 * <p>QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 *   <li>Criteria 기반 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionReadManager {

    private final PermissionQueryPort queryPort;

    public PermissionReadManager(PermissionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Permission 조회 (필수)
     *
     * @param id Permission ID
     * @return Permission Domain
     * @throws PermissionNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Permission findById(PermissionId id) {
        return queryPort.findById(id).orElseThrow(() -> new PermissionNotFoundException(id));
    }

    /**
     * ID로 Permission 존재 여부 확인
     *
     * @param id Permission ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(PermissionId id) {
        return queryPort.existsById(id);
    }

    /**
     * 서비스 내 permissionKey 존재 여부 확인
     *
     * <p>동일 서비스 내에서 permissionKey 중복을 확인합니다.
     *
     * @param serviceId 서비스 ID
     * @param permissionKey 권한 키
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByServiceIdAndPermissionKey(ServiceId serviceId, String permissionKey) {
        return queryPort.existsByServiceIdAndPermissionKey(serviceId, permissionKey);
    }

    /**
     * 서비스 내 permissionKey로 Permission 조회 (Optional)
     *
     * <p>존재하지 않는 경우 빈 Optional 반환합니다.
     *
     * @param serviceId 서비스 ID
     * @param permissionKey 권한 키 (예: "user:read")
     * @return Permission Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Permission> findByServiceIdAndPermissionKeyOptional(
            ServiceId serviceId, String permissionKey) {
        return queryPort.findByServiceIdAndPermissionKey(serviceId, permissionKey);
    }

    /**
     * 조건에 맞는 권한 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return Permission Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Permission> findAllBySearchCriteria(PermissionSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 권한 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return 조건에 맞는 Permission 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(PermissionSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }

    /**
     * ID 목록으로 Permission 다건 조회 (IN절 조회)
     *
     * @param ids Permission ID 목록
     * @return Permission Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Permission> findAllByIds(List<PermissionId> ids) {
        return queryPort.findAllByIds(ids);
    }

    /**
     * permissionKey 목록으로 Permission 다건 조회 (IN절)
     *
     * <p>벌크 동기화 시 기존 Permission을 한 번에 조회합니다.
     *
     * @param permissionKeys 권한 키 목록
     * @return Permission Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Permission> findAllByPermissionKeys(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return List.of();
        }
        return queryPort.findAllByPermissionKeys(permissionKeys);
    }
}
