package com.ryuqq.authhub.application.service.factory;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.aggregate.ServiceUpdateData;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceDescription;
import com.ryuqq.authhub.domain.service.vo.ServiceName;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ServiceCommandFactory - Service Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * <p>NOTE: Service 도메인은 Long Auto Increment PK를 사용하므로 IdGeneratorPort가 불필요합니다. ServiceCode는 사용자가 직접
 * 지정하는 비즈니스 식별자입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceCommandFactory {

    private final TimeProvider timeProvider;

    public ServiceCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateServiceCommand로부터 Service 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Service 도메인 객체
     */
    public Service create(CreateServiceCommand command) {
        Instant now = timeProvider.now();
        ServiceCode serviceCode = ServiceCode.of(command.serviceCode());
        ServiceName name = ServiceName.of(command.name());
        ServiceDescription description = ServiceDescription.of(command.description());
        return Service.create(serviceCode, name, description, now);
    }

    /**
     * UpdateServiceCommand로부터 UpdateContext 생성
     *
     * <p>Command → (ServiceId, ServiceUpdateData, Instant) 변환
     *
     * @param command 수정 Command
     * @return UpdateContext (ServiceId, ServiceUpdateData, Instant)
     */
    public UpdateContext<ServiceId, ServiceUpdateData> createUpdateContext(
            UpdateServiceCommand command) {
        ServiceId id = ServiceId.of(command.serviceId());
        ServiceUpdateData updateData =
                ServiceUpdateData.of(command.name(), command.description(), command.status());
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}
