package com.ryuqq.authhub.adapter.out.persistence.service.mapper;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceDescription;
import com.ryuqq.authhub.domain.service.vo.ServiceName;
import org.springframework.stereotype.Component;

/**
 * ServiceJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Service 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Service → ServiceJpaEntity (저장용)
 *   <li>ServiceJpaEntity → Service (조회용)
 * </ul>
 *
 * <p><strong>PK 전략:</strong>
 *
 * <ul>
 *   <li>serviceId: Long Auto Increment PK (신규 생성 시 null)
 *   <li>serviceCode: String UNIQUE 비즈니스 식별자
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Domain과 Infrastructure 기술 분리
 *   <li>Domain은 JPA 의존성 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Service 저장 (serviceId == null → Auto Increment)
     *   <li>기존 Service 수정 (serviceId != null → UPDATE)
     * </ul>
     *
     * @param domain Service 도메인
     * @return ServiceJpaEntity
     */
    public ServiceJpaEntity toEntity(Service domain) {
        return ServiceJpaEntity.of(
                domain.serviceIdValue(),
                domain.serviceCodeValue(),
                domain.nameValue(),
                domain.descriptionValue(),
                domain.getStatus(),
                domain.createdAt(),
                domain.updatedAt());
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * @param entity ServiceJpaEntity
     * @return Service 도메인
     */
    public Service toDomain(ServiceJpaEntity entity) {
        return Service.reconstitute(
                ServiceId.of(entity.getServiceId()),
                ServiceCode.of(entity.getServiceCode()),
                ServiceName.of(entity.getName()),
                ServiceDescription.of(entity.getDescription()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
