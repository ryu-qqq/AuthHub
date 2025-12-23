package com.ryuqq.authhub.application.permission.dto.response;

import java.util.List;

/**
 * ValidatePermissionsResult - 권한 검증 결과 DTO
 *
 * <p>권한 검증 결과를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param valid 검증 성공 여부 (모든 권한이 DB에 존재하면 true)
 * @param serviceName 검증한 서비스명
 * @param totalCount 검증 요청한 권한 총 개수
 * @param existingCount DB에 존재하는 권한 개수
 * @param missingCount DB에 없는 권한 개수
 * @param existing DB에 존재하는 권한 키 목록
 * @param missing DB에 없는 권한 키 목록
 * @param message 검증 결과 메시지
 * @author development-team
 * @since 1.0.0
 */
public record ValidatePermissionsResult(
        boolean valid,
        String serviceName,
        int totalCount,
        int existingCount,
        int missingCount,
        List<String> existing,
        List<String> missing,
        String message) {

    /**
     * 모든 권한이 존재하는 경우의 성공 결과 생성
     *
     * @param serviceName 서비스명
     * @param existing 존재하는 권한 키 목록
     * @return 성공 결과
     */
    public static ValidatePermissionsResult allValid(String serviceName, List<String> existing) {
        return new ValidatePermissionsResult(
                true,
                serviceName,
                existing.size(),
                existing.size(),
                0,
                existing,
                List.of(),
                "All permissions are registered in AuthHub.");
    }

    /**
     * 누락된 권한이 있는 경우의 실패 결과 생성
     *
     * @param serviceName 서비스명
     * @param existing 존재하는 권한 키 목록
     * @param missing 누락된 권한 키 목록
     * @return 실패 결과
     */
    public static ValidatePermissionsResult withMissing(
            String serviceName, List<String> existing, List<String> missing) {
        int total = existing.size() + missing.size();
        String message =
                String.format(
                        "Missing %d permission(s). Please register them in AuthHub before"
                                + " deployment.",
                        missing.size());
        return new ValidatePermissionsResult(
                false,
                serviceName,
                total,
                existing.size(),
                missing.size(),
                existing,
                missing,
                message);
    }
}
