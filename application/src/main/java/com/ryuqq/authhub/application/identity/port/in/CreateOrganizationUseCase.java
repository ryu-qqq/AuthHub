package com.ryuqq.authhub.application.identity.port.in;

/**
 * CreateOrganization UseCase - 조직 생성 유스케이스 Port Interface.
 *
 * <p>신규 조직을 생성하고 Organization Aggregate와 OrganizationMember 관계를 생성하는 Command UseCase입니다.
 * Command/Response는 UseCase 내부 Record로 정의되며, 외부 클래스로 분리하지 않습니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Command/Response는 UseCase 내부 Record로 정의</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드/Record에 @author, @since 포함</li>
 *   <li>✅ 구현체는 Service Layer에서 @Transactional 적용</li>
 *   <li>✅ @Transactional 내 외부 API 호출 금지</li>
 * </ul>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>조직명 중복 확인</li>
 *   <li>OrganizationType 검증 (Domain Layer에서 수행)</li>
 *   <li>Organization Aggregate 생성</li>
 *   <li>OrganizationMember 관계 생성 (OWNER 역할)</li>
 *   <li>생성된 organizationId 반환</li>
 * </ol>
 *
 * <p><strong>Race Condition 주의사항:</strong></p>
 * <ul>
 *   <li>중복 확인과 생성 사이에 동시 요청이 발생할 수 있음</li>
 *   <li>Database Unique Constraint로 최종 방어</li>
 *   <li>중복 에러 시 적절한 예외 변환 필요</li>
 * </ul>
 *
 * <p><strong>Transaction 경계:</strong></p>
 * <ul>
 *   <li>✅ Transaction은 짧게 유지 - 조직명 중복 확인 및 생성만 포함</li>
 *   <li>❌ 외부 API 호출 금지 (이메일 발송, S3 업로드 등)</li>
 *   <li>✅ MySQL/Redis는 내부 시스템으로 간주 (트랜잭션 내 호출 허용)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CreateOrganizationUseCase {

    /**
     * 신규 조직을 생성합니다.
     *
     * <p>Organization Aggregate를 생성하고, 요청 사용자를 OWNER 역할의 OrganizationMember로 등록합니다.
     * 모든 작업은 하나의 트랜잭션 내에서 수행됩니다.</p>
     *
     * @param command 조직 생성 요청 정보 (userId, organizationType, organizationName)
     * @return Response 생성된 조직 정보 (organizationId)
     * @throws DuplicateOrganizationNameException organizationName이 이미 존재하는 경우
     * @throws IllegalArgumentException 입력값이 유효하지 않은 경우 (OrganizationType 등)
     * @author AuthHub Team
     * @since 1.0.0
     */
    Response create(Command command);

    /**
     * 조직 생성 Command Record.
     *
     * <p>조직 생성에 필요한 모든 정보를 담고 있습니다.
     * Record의 Compact Constructor에서 입력값 검증을 수행합니다.</p>
     *
     * @param userId 조직 소유자 사용자 ID (UUID) - null 불가, 공백 불가
     * @param organizationType 조직 타입 (SELLER, COMPANY) - null 불가, 공백 불가
     * @param organizationName 조직명 - null 불가, 공백 불가, 중복 불가, 2~100자
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Command(
            String userId,
            String organizationType,
            String organizationName
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 형식이 잘못된 경우
         */
        public Command {
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("userId cannot be null or blank");
            }

            if (organizationType == null || organizationType.isBlank()) {
                throw new IllegalArgumentException("organizationType cannot be null or blank");
            }

            if (organizationType.length() > 50) {
                throw new IllegalArgumentException("organizationType cannot exceed 50 characters");
            }

            if (organizationName == null || organizationName.isBlank()) {
                throw new IllegalArgumentException("organizationName cannot be null or blank");
            }

            if (organizationName.length() < 2 || organizationName.length() > 100) {
                throw new IllegalArgumentException("organizationName must be between 2 and 100 characters");
            }
        }
    }

    /**
     * 조직 생성 Response Record.
     *
     * <p>조직 생성 성공 시 반환되는 정보입니다.
     * Organization의 ID를 포함합니다.</p>
     *
     * @param organizationId 생성된 Organization의 ID (UUID)
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Response(
            String organizationId
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException organizationId가 null이거나 공백인 경우
         */
        public Response {
            if (organizationId == null || organizationId.isBlank()) {
                throw new IllegalArgumentException("organizationId cannot be null or blank");
            }
        }
    }
}
