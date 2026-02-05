package com.ryuqq.authhub.application.service.port.out.query;

import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import java.util.List;
import java.util.Optional;

/**
 * ServiceQueryPort - Service Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById, findByCode)
 *   <li>저장/수정/삭제 메서드 금지 (CommandPort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ServiceQueryPort {

    /**
     * ID로 Service 단건 조회
     *
     * @param id Service ID (Value Object)
     * @return Service Domain (Optional)
     */
    Optional<Service> findById(ServiceId id);

    /**
     * ID로 Service 존재 여부 확인
     *
     * @param id Service ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(ServiceId id);

    /**
     * ServiceCode로 Service 존재 여부 확인
     *
     * @param serviceCode Service Code (Value Object)
     * @return 존재 여부
     */
    boolean existsByCode(ServiceCode serviceCode);

    /**
     * 조건에 맞는 Service 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return Service Domain 목록
     */
    List<Service> findAllByCriteria(ServiceSearchCriteria criteria);

    /**
     * 조건에 맞는 Service 개수 조회
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return 조건에 맞는 Service 총 개수
     */
    long countByCriteria(ServiceSearchCriteria criteria);

    /**
     * ServiceCode로 Service 단건 조회
     *
     * @param serviceCode Service Code (Value Object)
     * @return Service Domain (Optional)
     */
    Optional<Service> findByCode(ServiceCode serviceCode);

    /**
     * 모든 활성 Service 목록 조회
     *
     * @return 활성 상태의 Service Domain 목록
     */
    List<Service> findAllActive();
}
