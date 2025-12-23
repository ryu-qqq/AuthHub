package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.RegisterPermissionUsageApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.ValidatePermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.PermissionUsageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.ValidatePermissionsApiResponse;
import com.ryuqq.authhub.application.permission.dto.command.RegisterPermissionUsageCommand;
import com.ryuqq.authhub.application.permission.dto.command.ValidatePermissionsCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InternalApiMapper - Internal API DTO 변환기
 *
 * <p>Internal REST API DTO와 Application DTO 간의 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 *   <li>단순 변환만 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalApiMapper {

    /**
     * ValidatePermissionsApiRequest → ValidatePermissionsCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ValidatePermissionsCommand toCommand(ValidatePermissionsApiRequest request) {
        List<ValidatePermissionsCommand.PermissionEntry> entries =
                request.permissions().stream()
                        .map(
                                entry ->
                                        new ValidatePermissionsCommand.PermissionEntry(
                                                entry.key(),
                                                entry.locations() != null
                                                        ? entry.locations()
                                                        : List.of()))
                        .toList();

        return new ValidatePermissionsCommand(request.serviceName(), entries);
    }

    /**
     * ValidatePermissionsResult → ValidatePermissionsApiResponse 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public ValidatePermissionsApiResponse toApiResponse(ValidatePermissionsResult result) {
        return new ValidatePermissionsApiResponse(
                result.valid(),
                result.serviceName(),
                result.totalCount(),
                result.existingCount(),
                result.missingCount(),
                result.existing(),
                result.missing(),
                result.message());
    }

    /**
     * RegisterPermissionUsageApiRequest → RegisterPermissionUsageCommand 변환
     *
     * @param permissionKey 권한 키 (Path Variable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterPermissionUsageCommand toCommand(
            String permissionKey, RegisterPermissionUsageApiRequest request) {
        return new RegisterPermissionUsageCommand(
                permissionKey,
                request.serviceName(),
                request.locations() != null ? request.locations() : List.of());
    }

    /**
     * PermissionUsageResponse → PermissionUsageApiResponse 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public PermissionUsageApiResponse toApiResponse(PermissionUsageResponse result) {
        return new PermissionUsageApiResponse(
                result.usageId(),
                result.permissionKey(),
                result.serviceName(),
                result.locations(),
                result.lastScannedAt(),
                result.createdAt());
    }
}
