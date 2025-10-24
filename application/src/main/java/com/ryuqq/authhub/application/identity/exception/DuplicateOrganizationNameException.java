package com.ryuqq.authhub.application.identity.exception;

/**
 * 조직명 중복 예외.
 *
 * <p>동일한 조직명이 이미 존재하는 경우 발생하는 Application Layer 예외입니다.
 * HTTP 409 Conflict 응답과 매핑되며, 클라이언트에게 조직명 변경을 요구합니다.</p>
 *
 * <p><strong>발생 시점:</strong></p>
 * <ul>
 *   <li>CreateOrganizationService에서 조직명 중복 확인 시</li>
 *   <li>CheckDuplicateOrganizationNamePort.existsByName() 결과가 true인 경우</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ RuntimeException 상속 - 언체크 예외</li>
 * </ul>
 *
 * <p><strong>Exception 처리 흐름:</strong></p>
 * <pre>
 * 1. CreateOrganizationService에서 DuplicateOrganizationNameException 발생
 * 2. GlobalExceptionHandler (@RestControllerAdvice)가 예외 포착
 * 3. HTTP 409 Conflict 응답 생성:
 *    {
 *      "code": "DUPLICATE_ORGANIZATION_NAME",
 *      "message": "Organization name already exists: [조직명]",
 *      "timestamp": "2024-10-25T00:00:00Z"
 *    }
 * 4. 클라이언트가 조직명을 변경하여 재시도
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class DuplicateOrganizationNameException extends RuntimeException {

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public DuplicateOrganizationNameException(final String message) {
        super(message);
    }

    /**
     * 원인 예외를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    public DuplicateOrganizationNameException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
