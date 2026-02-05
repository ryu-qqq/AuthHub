package com.ryuqq.authhub.application.permission.port.out.query;

import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.util.List;
import java.util.Optional;

/**
 * PermissionQueryPort - Permission Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById)
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionQueryPort {

    /**
     * ID로 Permission 단건 조회
     *
     * @param id Permission ID (Value Object)
     * @return Permission Domain (Optional)
     */
    Optional<Permission> findById(PermissionId id);

    /**
     * ID로 Permission 존재 여부 확인
     *
     * @param id Permission ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(PermissionId id);

    /**
     * 서비스 내 permissionKey 존재 여부 확인
     *
     * <p>동일 서비스 내에서 permissionKey 중복을 확인합니다.
     *
     * @param serviceId 서비스 ID
     * @param permissionKey 권한 키 (예: "user:read")
     * @return 존재 여부
     */
    boolean existsByServiceIdAndPermissionKey(ServiceId serviceId, String permissionKey);

    /**
     * 서비스 내 permissionKey로 Permission 단건 조회
     *
     * @param serviceId 서비스 ID
     * @param permissionKey 권한 키 (예: "user:read")
     * @return Permission Domain (Optional)
     */
    Optional<Permission> findByServiceIdAndPermissionKey(ServiceId serviceId, String permissionKey);

    /**
     * 조건에 맞는 권한 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return Permission Domain 목록
     */
    List<Permission> findAllBySearchCriteria(PermissionSearchCriteria criteria);

    /**
     * 조건에 맞는 권한 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return 조건에 맞는 Permission 총 개수
     */
    long countBySearchCriteria(PermissionSearchCriteria criteria);

    /**
     * ID 목록으로 Permission 다건 조회
     *
     * @param ids Permission ID 목록
     * @return Permission Domain 목록
     */
    List<Permission> findAllByIds(List<PermissionId> ids);

    /**
     * permissionKey 목록으로 Permission 다건 조회 (IN절)
     *
     * <p>벌크 동기화 시 기존 Permission을 한 번에 조회합니다.
     *
     * @param permissionKeys 권한 키 목록
     * @return Permission Domain 목록
     */
    List<Permission> findAllByPermissionKeys(List<String> permissionKeys);
}
