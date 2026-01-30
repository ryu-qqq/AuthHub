package com.ryuqq.authhub.adapter.out.client.id;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UuidV7IdGeneratorClient 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>IdGeneratorPort 구현체의 generate() 동작 검증
 *   <li>UUIDv7 형식 및 고유성 검증
 *   <li>외부 라이브러리(uuid-creator) 실제 호출 검증 (Mock 없음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UuidV7IdGeneratorClient 단위 테스트")
class UuidV7IdGeneratorClientTest {

    /** UUIDv7 형식: 8-4-4-4-12 (버전 7은 세 번째 그룹의 첫 문자) */
    private static final Pattern UUID_V7_PATTERN =
            Pattern.compile(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private IdGeneratorPort sut;

    @BeforeEach
    void setUp() {
        sut = new UuidV7IdGeneratorClient();
    }

    @Nested
    @DisplayName("generate 메서드")
    class Generate {

        @Test
        @DisplayName("성공: null이 아닌 문자열 반환")
        void shouldReturnNonNullString() {
            // when
            String result = sut.generate();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("성공: 빈 문자열이 아닌 값 반환")
        void shouldReturnNonEmptyString() {
            // when
            String result = sut.generate();

            // then
            assertThat(result).isNotBlank();
        }

        @Test
        @DisplayName("성공: UUIDv7 형식 준수 (8-4-4-4-12, 버전 7)")
        void shouldReturnValidUuidV7Format() {
            // when
            String result = sut.generate();

            // then
            assertThat(result).matches(UUID_V7_PATTERN);
        }

        @Test
        @DisplayName("성공: 소문자 16진수 형식 반환")
        void shouldReturnLowercaseHexFormat() {
            // when
            String result = sut.generate();

            // then
            assertThat(result).isEqualTo(result.toLowerCase());
            assertThat(result).hasSize(36); // 32 hex + 4 hyphens
            assertThat(result.split("-")).hasSize(5); // 8-4-4-4-12
        }

        @RepeatedTest(10)
        @DisplayName("성공: 반복 호출 시 매번 고유한 ID 생성")
        void shouldGenerateUniqueIdOnEachCall() {
            // when
            String id1 = sut.generate();
            String id2 = sut.generate();

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("성공: 대량 생성 시 모든 ID가 고유함")
        void shouldGenerateUniqueIdsInBulk() {
            // given
            int count = 100;
            Set<String> generatedIds = new HashSet<>();

            // when
            for (int i = 0; i < count; i++) {
                generatedIds.add(sut.generate());
            }

            // then
            assertThat(generatedIds).hasSize(count);
        }

        @Test
        @DisplayName("성공: IdGeneratorPort 인터페이스 구현")
        void shouldImplementIdGeneratorPort() {
            // then
            assertThat(sut).isInstanceOf(IdGeneratorPort.class);
        }
    }
}
