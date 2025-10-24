package com.ryuqq.authhub.application.identity.service.command;

import com.ryuqq.authhub.application.identity.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.application.identity.port.in.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateOrganizationNamePort;
import com.ryuqq.authhub.application.identity.port.out.SaveOrganizationPort;
import com.ryuqq.authhub.domain.identity.organization.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * CreateOrganizationService 단위 테스트.
 *
 * <p>CreateOrganizationService의 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 모든 외부 의존성(Port)은 Mockito로 Mocking하여 순수한 Service 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음 (빠른 실행)</li>
 *   <li>✅ 모든 Port는 Mock 객체 사용</li>
 *   <li>✅ Given-When-Then 패턴</li>
 *   <li>✅ BDD 스타일 (given, when, then)</li>
 * </ul>
 *
 * <p><strong>테스트 케이스:</strong></p>
 * <ul>
 *   <li>✅ 정상 케이스: SELLER 조직 생성 성공</li>
 *   <li>✅ 정상 케이스: COMPANY 조직 생성 성공</li>
 *   <li>✅ 예외 케이스: 조직명 중복</li>
 *   <li>✅ 예외 케이스: 잘못된 OrganizationType</li>
 *   <li>✅ 예외 케이스: 잘못된 UserId 형식</li>
 *   <li>✅ 예외 케이스: 잘못된 Command (Validation)</li>
 * </ul>
 *
 * <p><strong>테스트 태그:</strong></p>
 * <ul>
 *   <li>@Tag("unit") - 단위 테스트 (빠른 실행, Mock 사용)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrganizationService 단위 테스트")
@Tag("unit")
class CreateOrganizationServiceTest {

    @Mock
    private CheckDuplicateOrganizationNamePort checkDuplicateOrganizationNamePort;

    @Mock
    private SaveOrganizationPort saveOrganizationPort;

    @InjectMocks
    private CreateOrganizationService createOrganizationService;

    private CreateOrganizationUseCase.Command validSellerCommand;
    private CreateOrganizationUseCase.Command validCompanyCommand;

    @BeforeEach
    void setUp() {
        // Given: 유효한 SELLER 조직 생성 Command
        validSellerCommand = new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "SELLER",
                "Nike Store"
        );

        // Given: 유효한 COMPANY 조직 생성 Command
        validCompanyCommand = new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "COMPANY",
                "Samsung Electronics"
        );
    }

    @Test
    @DisplayName("✅ 정상: SELLER 조직 생성 성공")
    void create_WithValidSellerCommand_ShouldReturnOrganizationId() {
        // Given: 조직명 중복 없음 + Organization 저장 성공
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Nike Store")))
                .willReturn(false);

        given(saveOrganizationPort.save(any(Organization.class)))
                .willAnswer(invocation -> {
                    Organization org = invocation.getArgument(0);
                    return org; // 저장된 Organization 반환 (ID 포함)
                });

        // When: 조직 생성
        final CreateOrganizationUseCase.Response response = createOrganizationService.create(validSellerCommand);

        // Then: 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.organizationId()).isNotBlank();

        // Then: Port 호출 검증
        then(checkDuplicateOrganizationNamePort).should(times(1))
                .existsByName(eq("Nike Store"));
        then(saveOrganizationPort).should(times(1))
                .save(any(Organization.class));
    }

    @Test
    @DisplayName("✅ 정상: COMPANY 조직 생성 성공")
    void create_WithValidCompanyCommand_ShouldReturnOrganizationId() {
        // Given: 조직명 중복 없음 + Organization 저장 성공
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Samsung Electronics")))
                .willReturn(false);

        given(saveOrganizationPort.save(any(Organization.class)))
                .willAnswer(invocation -> {
                    Organization org = invocation.getArgument(0);
                    return org; // 저장된 Organization 반환 (ID 포함)
                });

        // When: 조직 생성
        final CreateOrganizationUseCase.Response response = createOrganizationService.create(validCompanyCommand);

        // Then: 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.organizationId()).isNotBlank();

        // Then: Port 호출 검증
        then(checkDuplicateOrganizationNamePort).should(times(1))
                .existsByName(eq("Samsung Electronics"));
        then(saveOrganizationPort).should(times(1))
                .save(any(Organization.class));
    }

    @Test
    @DisplayName("❌ 예외: 조직명 중복 시 DuplicateOrganizationNameException 발생")
    void create_WithDuplicateOrganizationName_ShouldThrowDuplicateOrganizationNameException() {
        // Given: 조직명 중복 존재
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Nike Store")))
                .willReturn(true);

        // When & Then: 조직명 중복 예외 발생
        assertThatThrownBy(() -> createOrganizationService.create(validSellerCommand))
                .isInstanceOf(DuplicateOrganizationNameException.class)
                .hasMessageContaining("Organization name already exists")
                .hasMessageContaining("Nike Store");

        // Then: 저장 Port는 호출되지 않음
        then(saveOrganizationPort).should(never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("❌ 예외: 잘못된 OrganizationType으로 IllegalArgumentException 발생")
    void create_WithInvalidOrganizationType_ShouldThrowIllegalArgumentException() {
        // Given: 잘못된 OrganizationType
        final CreateOrganizationUseCase.Command invalidCommand = new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "INVALID_TYPE",
                "Nike Store"
        );

        // Given: 조직명 중복 없음
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Nike Store")))
                .willReturn(false);

        // When & Then: OrganizationType 검증 실패
        assertThatThrownBy(() -> createOrganizationService.create(invalidCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid organization type")
                .hasMessageContaining("INVALID_TYPE");

        // Then: 저장 Port는 호출되지 않음
        then(saveOrganizationPort).should(never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("❌ 예외: 잘못된 UserId 형식으로 IllegalArgumentException 발생")
    void create_WithInvalidUserIdFormat_ShouldThrowIllegalArgumentException() {
        // Given: 잘못된 UserId 형식
        final CreateOrganizationUseCase.Command invalidCommand = new CreateOrganizationUseCase.Command(
                "invalid-uuid-format",
                "SELLER",
                "Nike Store"
        );

        // Given: 조직명 중복 없음
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Nike Store")))
                .willReturn(false);

        // When & Then: UserId 파싱 실패
        assertThatThrownBy(() -> createOrganizationService.create(invalidCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid UUID format")
                .hasMessageContaining("invalid-uuid-format");

        // Then: 저장 Port는 호출되지 않음
        then(saveOrganizationPort).should(never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("❌ 예외: null userId로 IllegalArgumentException 발생")
    void create_WithNullUserId_ShouldThrowIllegalArgumentException() {
        // When & Then: null userId로 Command 생성 시도
        assertThatThrownBy(() -> new CreateOrganizationUseCase.Command(
                null,
                "SELLER",
                "Nike Store"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId cannot be null or blank");
    }

    @Test
    @DisplayName("❌ 예외: blank organizationName으로 IllegalArgumentException 발생")
    void create_WithBlankOrganizationName_ShouldThrowIllegalArgumentException() {
        // When & Then: blank organizationName으로 Command 생성 시도
        assertThatThrownBy(() -> new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "SELLER",
                "   "
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("organizationName cannot be null or blank");
    }

    @Test
    @DisplayName("❌ 예외: 너무 짧은 organizationName으로 IllegalArgumentException 발생")
    void create_WithTooShortOrganizationName_ShouldThrowIllegalArgumentException() {
        // Given: 1자 조직명 Command
        final CreateOrganizationUseCase.Command invalidCommand = new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "SELLER",
                "A"
        );

        // Given: 조직명 중복 없음
        given(checkDuplicateOrganizationNamePort.existsByName(eq("A")))
                .willReturn(false);

        // When & Then: OrganizationName VO에서 길이 검증 실패 (Domain Layer 책임)
        assertThatThrownBy(() -> createOrganizationService.create(invalidCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Organization name length must be between")
                .hasMessageContaining("2 and 100 characters");

        // Then: 저장 Port는 호출되지 않음
        then(saveOrganizationPort).should(never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("❌ 예외: 너무 긴 organizationName으로 IllegalArgumentException 발생")
    void create_WithTooLongOrganizationName_ShouldThrowIllegalArgumentException() {
        // Given: 101자 조직명
        final String longName = "A".repeat(101);

        // Given: 101자 조직명 Command
        final CreateOrganizationUseCase.Command invalidCommand = new CreateOrganizationUseCase.Command(
                "123e4567-e89b-12d3-a456-426614174000",
                "SELLER",
                longName
        );

        // Given: 조직명 중복 없음
        given(checkDuplicateOrganizationNamePort.existsByName(eq(longName)))
                .willReturn(false);

        // When & Then: OrganizationName VO에서 길이 검증 실패 (Domain Layer 책임)
        assertThatThrownBy(() -> createOrganizationService.create(invalidCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Organization name length must be between")
                .hasMessageContaining("2 and 100 characters");

        // Then: 저장 Port는 호출되지 않음
        then(saveOrganizationPort).should(never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("✅ 정상: 조직명 중복 확인 후 저장 호출 순서 검증")
    void create_ShouldCallPortsInCorrectOrder() {
        // Given: 조직명 중복 없음 + Organization 저장 성공
        given(checkDuplicateOrganizationNamePort.existsByName(eq("Nike Store")))
                .willReturn(false);

        given(saveOrganizationPort.save(any(Organization.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When: 조직 생성
        createOrganizationService.create(validSellerCommand);

        // Then: Port 호출 순서 검증 (InOrder)
        final org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(
                checkDuplicateOrganizationNamePort,
                saveOrganizationPort
        );
        inOrder.verify(checkDuplicateOrganizationNamePort).existsByName(eq("Nike Store"));
        inOrder.verify(saveOrganizationPort).save(any(Organization.class));
    }
}
