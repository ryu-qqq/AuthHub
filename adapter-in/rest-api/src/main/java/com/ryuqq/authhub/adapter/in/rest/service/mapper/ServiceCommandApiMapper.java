package com.ryuqq.authhub.adapter.in.rest.service.mapper;

import com.ryuqq.authhub.adapter.in.rest.service.dto.request.CreateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.UpdateServiceApiRequest;
import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * ServiceCommandApiMapper - Service Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceCommandApiMapper {

    /**
     * CreateServiceApiRequest -> CreateServiceCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateServiceCommand toCommand(CreateServiceApiRequest request) {
        return new CreateServiceCommand(
                request.serviceCode(), request.name(), request.description());
    }

    /**
     * UpdateServiceApiRequest + PathVariable ID -> UpdateServiceCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param serviceId Service ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateServiceCommand toCommand(Long serviceId, UpdateServiceApiRequest request) {
        Objects.requireNonNull(serviceId, "serviceId must not be null");
        return new UpdateServiceCommand(
                serviceId, request.name(), request.description(), request.status());
    }
}
