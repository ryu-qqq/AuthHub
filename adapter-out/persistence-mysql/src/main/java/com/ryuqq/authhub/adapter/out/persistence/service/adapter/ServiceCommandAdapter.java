package com.ryuqq.authhub.adapter.out.persistence.service.adapter;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.mapper.ServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceJpaRepository;
import com.ryuqq.authhub.application.service.port.out.command.ServiceCommandPort;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import org.springframework.stereotype.Component;

/**
 * ServiceCommandAdapter - 서비스 Command Adapter (CUD 전용)
 *
 * <p>ServiceCommandPort 구현체로서 서비스 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>ServiceJpaRepository (1개) + ServiceJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 서비스 저장 (생성/수정)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Hibernate Dirty Checking 활용 (존재 여부 확인 불필요)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceCommandAdapter implements ServiceCommandPort {

    private final ServiceJpaRepository repository;
    private final ServiceJpaEntityMapper mapper;

    public ServiceCommandAdapter(ServiceJpaRepository repository, ServiceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 서비스 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain → Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>저장된 ID 반환 (Long)
     * </ol>
     *
     * <p><strong>Hibernate Dirty Checking:</strong>
     *
     * <ul>
     *   <li>같은 ID의 Entity가 이미 존재하면 UPDATE
     *   <li>새로운 Entity면 INSERT (Auto Increment)
     *   <li>Hibernate 구현체가 자동으로 판단
     * </ul>
     *
     * @param service 저장할 서비스 도메인
     * @return 저장된 서비스 ID (Long)
     */
    @Override
    public Long persist(Service service) {
        ServiceJpaEntity entity = mapper.toEntity(service);
        ServiceJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getServiceId();
    }
}
