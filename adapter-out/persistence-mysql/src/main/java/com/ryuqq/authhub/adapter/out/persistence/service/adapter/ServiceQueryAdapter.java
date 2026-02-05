package com.ryuqq.authhub.adapter.out.persistence.service.adapter;

import com.ryuqq.authhub.adapter.out.persistence.service.mapper.ServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceQueryDslRepository;
import com.ryuqq.authhub.application.service.port.out.query.ServiceQueryPort;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ServiceQueryAdapter - 서비스 Query Adapter (조회 전용)
 *
 * <p>ServiceQueryPort 구현체로서 서비스 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>ServiceQueryDslRepository (1개) + ServiceJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>existsById() - ID 존재 여부 확인
 *   <li>existsByCode() - 서비스 코드 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>findAllActive() - 활성 상태 전체 조회
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Domain 반환 (Mapper로 변환)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceQueryAdapter implements ServiceQueryPort {

    private final ServiceQueryDslRepository repository;
    private final ServiceJpaEntityMapper mapper;

    public ServiceQueryAdapter(
            ServiceQueryDslRepository repository, ServiceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 서비스 단건 조회
     *
     * @param serviceId 서비스 ID
     * @return Optional<Service>
     */
    @Override
    public Optional<Service> findById(ServiceId serviceId) {
        return repository.findByServiceId(serviceId.value()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param serviceId 서비스 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(ServiceId serviceId) {
        return repository.existsByServiceId(serviceId.value());
    }

    /**
     * 서비스 코드 존재 여부 확인
     *
     * @param serviceCode 서비스 코드
     * @return 존재 여부
     */
    @Override
    public boolean existsByCode(ServiceCode serviceCode) {
        return repository.existsByServiceCode(serviceCode.value());
    }

    /**
     * 서비스 코드로 단건 조회
     *
     * @param serviceCode 서비스 코드
     * @return Optional<Service>
     */
    @Override
    public Optional<Service> findByCode(ServiceCode serviceCode) {
        return repository.findByServiceCode(serviceCode.value()).map(mapper::toDomain);
    }

    /**
     * 조건에 맞는 서비스 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return Service Domain 목록
     */
    @Override
    public List<Service> findAllByCriteria(ServiceSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 서비스 개수 조회
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return 조건에 맞는 서비스 총 개수
     */
    @Override
    public long countByCriteria(ServiceSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    /**
     * 모든 활성 서비스 목록 조회
     *
     * @return 활성 상태의 Service Domain 목록
     */
    @Override
    public List<Service> findAllActive() {
        return repository.findAllActive().stream().map(mapper::toDomain).toList();
    }
}
