package com.ryuqq.authhub.application.identity.service.command;

import com.ryuqq.authhub.application.identity.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.application.identity.port.in.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateOrganizationNamePort;
import com.ryuqq.authhub.application.identity.port.out.SaveOrganizationPort;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.organization.Organization;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * CreateOrganization Service - CreateOrganizationUseCase 구현체.
 *
 * <p>신규 조직을 생성하고 Organization Aggregate를 생성하는 Command Service입니다.
 * 생성 메서드에 {@code @Transactional}을 적용하여 트랜잭션 경계를 명확히 정의합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>조직명 중복 확인</li>
 *   <li>OrganizationType 검증 및 변환</li>
 *   <li>Organization Aggregate 생성 (팩토리 메서드 사용)</li>
 *   <li>Organization 저장</li>
 *   <li>생성된 organizationId 반환</li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ @Transactional 내 외부 API 호출 금지</li>
 *   <li>✅ MySQL은 내부 시스템으로 간주 (트랜잭션 내 호출 허용)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Race Condition 주의 - 중복 확인 후 생성 사이 동시 요청 가능</li>
 *   <li>✅ Database Unique Constraint로 최종 방어</li>
 * </ul>
 *
 * <p><strong>트랜잭션 경계:</strong></p>
 * <ul>
 *   <li>조직명 중복 확인: CheckDuplicateOrganizationNamePort (MySQL)</li>
 *   <li>OrganizationType 검증: Domain Layer (메모리 계산)</li>
 *   <li>저장: SaveOrganizationPort (MySQL)</li>
 * </ul>
 *
 * <p><strong>OrganizationMember 관계 생성:</strong></p>
 * <p>현재 구현에서는 Organization 생성만 수행합니다.
 * OrganizationMember 관계 생성은 향후 Epic에서 별도로 구현될 예정입니다.
 * 그 이유는 다음과 같습니다:</p>
 * <ul>
 *   <li>OrganizationMember Entity가 아직 Domain Layer에 구현되지 않음</li>
 *   <li>BaseRole Enum 정의 필요 (OWNER, ADMIN, MANAGER, MEMBER, VIEWER)</li>
 *   <li>OrganizationMember Port Out 인터페이스 구현 필요</li>
 *   <li>현재 AUT-17 태스크의 Scope는 Organization 생성에 집중</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class CreateOrganizationService implements CreateOrganizationUseCase {

    // 중복 확인 의존성
    private final CheckDuplicateOrganizationNamePort checkDuplicateOrganizationNamePort;

    // 저장 Port 의존성
    private final SaveOrganizationPort saveOrganizationPort;

    /**
     * CreateOrganizationService 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param checkDuplicateOrganizationNamePort 조직명 중복 확인 Port
     * @param saveOrganizationPort Organization 저장 Port
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CreateOrganizationService(
            final CheckDuplicateOrganizationNamePort checkDuplicateOrganizationNamePort,
            final SaveOrganizationPort saveOrganizationPort
    ) {
        this.checkDuplicateOrganizationNamePort = checkDuplicateOrganizationNamePort;
        this.saveOrganizationPort = saveOrganizationPort;
    }

    /**
     * 신규 조직을 생성합니다.
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>✅ 조직명 중복 확인 (MySQL)</li>
     *   <li>✅ Organization Aggregate 생성 및 저장 (MySQL)</li>
     *   <li>❌ 외부 API 호출 없음 (S3, HTTP, SQS 등)</li>
     * </ul>
     *
     * @param command 조직 생성 요청 정보
     * @return Response 생성된 조직 정보 (organizationId)
     * @throws DuplicateOrganizationNameException organizationName이 이미 존재하는 경우
     * @throws IllegalArgumentException organizationType이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    @Transactional
    public Response create(final Command command) {
        // ✅ 1. 조직명 중복 확인
        validateNoDuplicates(command);

        // ✅ 2. UserId 변환
        final UserId ownerId = parseUserId(command.userId());

        // ✅ 3. OrganizationType 검증 및 변환
        final OrganizationType organizationType = OrganizationType.fromString(command.organizationType());

        // ✅ 4. OrganizationName 생성
        final OrganizationName organizationName = new OrganizationName(command.organizationName());

        // ✅ 5. Organization Aggregate 생성 (팩토리 메서드 사용)
        final Organization organization = createOrganization(ownerId, organizationType, organizationName);

        // ✅ 6. Organization 저장
        final Organization savedOrganization = saveOrganizationPort.save(organization);

        // ✅ 7. Response 생성 및 반환
        return new Response(savedOrganization.getId().asString());
    }

    /**
     * 조직명 중복 여부를 확인합니다.
     *
     * @param command 조직 생성 Command
     * @throws DuplicateOrganizationNameException 조직명이 이미 존재하는 경우
     */
    private void validateNoDuplicates(final Command command) {
        // 조직명 중복 확인
        if (checkDuplicateOrganizationNamePort.existsByName(command.organizationName())) {
            throw new DuplicateOrganizationNameException(
                    "Organization name already exists: organizationName=" + command.organizationName()
            );
        }
    }

    /**
     * UserId를 문자열에서 파싱합니다.
     *
     * @param userIdString UserId 문자열 (UUID)
     * @return UserId Value Object
     * @throws IllegalArgumentException userId가 유효하지 않은 UUID 형식인 경우
     */
    private UserId parseUserId(final String userIdString) {
        try {
            final UUID uuid = UUID.fromString(userIdString);
            return UserId.from(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid userId format: " + userIdString, e);
        }
    }

    /**
     * Organization Aggregate를 생성합니다.
     * 조직 타입에 따라 적절한 팩토리 메서드를 호출합니다.
     *
     * @param ownerId 소유자 식별자
     * @param organizationType 조직 타입
     * @param organizationName 조직명
     * @return 생성된 Organization Aggregate
     */
    private Organization createOrganization(
            final UserId ownerId,
            final OrganizationType organizationType,
            final OrganizationName organizationName
    ) {
        return switch (organizationType) {
            case SELLER -> Organization.createSeller(ownerId, organizationName);
            case COMPANY -> Organization.createCompany(ownerId, organizationName);
        };
    }
}
