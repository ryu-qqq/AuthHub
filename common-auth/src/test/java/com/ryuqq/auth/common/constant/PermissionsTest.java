package com.ryuqq.auth.common.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Permissions")
class PermissionsTest {

    @Nested
    @DisplayName("isValidFormat")
    class IsValidFormat {

        @Test
        @DisplayName("2세그먼트 형식 (domain:action) 유효")
        void twoSegmentFormatIsValid() {
            assertThat(Permissions.isValidFormat("user:read")).isTrue();
            assertThat(Permissions.isValidFormat("product:write")).isTrue();
            assertThat(Permissions.isValidFormat("order:delete")).isTrue();
        }

        @Test
        @DisplayName("3세그먼트 형식 (service:domain:action) 유효")
        void threeSegmentFormatIsValid() {
            assertThat(Permissions.isValidFormat("authhub:user:read")).isTrue();
            assertThat(Permissions.isValidFormat("product-service:product:write")).isTrue();
            assertThat(Permissions.isValidFormat("order123:order:create")).isTrue();
        }

        @Test
        @DisplayName("하이픈 포함 세그먼트 유효")
        void segmentWithHyphenIsValid() {
            assertThat(Permissions.isValidFormat("product-catalog:read")).isTrue();
            assertThat(Permissions.isValidFormat("user-management:user:write")).isTrue();
        }

        @Test
        @DisplayName("숫자 포함 세그먼트 유효")
        void segmentWithDigitsIsValid() {
            assertThat(Permissions.isValidFormat("product2:read")).isTrue();
            assertThat(Permissions.isValidFormat("service1:domain2:action3")).isTrue();
        }

        @Test
        @DisplayName("와일드카드 (*:*) 유효")
        void wildcardIsValid() {
            assertThat(Permissions.isValidFormat("*:*")).isTrue();
        }

        @Test
        @DisplayName("대문자 포함 시 무효")
        void uppercaseIsInvalid() {
            assertThat(Permissions.isValidFormat("User:read")).isFalse();
            assertThat(Permissions.isValidFormat("user:Read")).isFalse();
            assertThat(Permissions.isValidFormat("USER:READ")).isFalse();
        }

        @Test
        @DisplayName("숫자로 시작하는 세그먼트 무효")
        void segmentStartingWithDigitIsInvalid() {
            assertThat(Permissions.isValidFormat("1user:read")).isFalse();
            assertThat(Permissions.isValidFormat("user:1read")).isFalse();
        }

        @Test
        @DisplayName("특수문자 포함 시 무효")
        void specialCharactersInvalid() {
            assertThat(Permissions.isValidFormat("user_name:read")).isFalse();
            assertThat(Permissions.isValidFormat("user.name:read")).isFalse();
            assertThat(Permissions.isValidFormat("user@name:read")).isFalse();
        }

        @Test
        @DisplayName("1세그먼트만 있으면 무효")
        void oneSegmentIsInvalid() {
            assertThat(Permissions.isValidFormat("user")).isFalse();
            assertThat(Permissions.isValidFormat("read")).isFalse();
        }

        @Test
        @DisplayName("4세그먼트 이상은 무효")
        void fourOrMoreSegmentsIsInvalid() {
            assertThat(Permissions.isValidFormat("a:b:c:d")).isFalse();
            assertThat(Permissions.isValidFormat("a:b:c:d:e")).isFalse();
        }

        @Test
        @DisplayName("null 또는 빈 문자열은 무효")
        void nullOrEmptyIsInvalid() {
            assertThat(Permissions.isValidFormat(null)).isFalse();
            assertThat(Permissions.isValidFormat("")).isFalse();
            assertThat(Permissions.isValidFormat("   ")).isFalse();
        }

        @Test
        @DisplayName("빈 세그먼트 포함 시 무효")
        void emptySegmentIsInvalid() {
            assertThat(Permissions.isValidFormat(":read")).isFalse();
            assertThat(Permissions.isValidFormat("user:")).isFalse();
            assertThat(Permissions.isValidFormat("::")).isFalse();
        }
    }

    @Nested
    @DisplayName("validateFormat")
    class ValidateFormat {

        @Test
        @DisplayName("유효한 형식은 빈 Optional 반환")
        void validFormatReturnsEmpty() {
            assertThat(Permissions.validateFormat("user:read")).isEmpty();
            assertThat(Permissions.validateFormat("authhub:user:read")).isEmpty();
            assertThat(Permissions.validateFormat("*:*")).isEmpty();
        }

        @Test
        @DisplayName("null은 오류 메시지 반환")
        void nullReturnsError() {
            Optional<String> result = Permissions.validateFormat(null);
            assertThat(result).isPresent();
            assertThat(result.get()).contains("null or blank");
        }

        @Test
        @DisplayName("잘못된 세그먼트 수는 오류 메시지 반환")
        void wrongSegmentCountReturnsError() {
            Optional<String> result = Permissions.validateFormat("only-one");
            assertThat(result).isPresent();
            assertThat(result.get()).contains("2 or 3 segments");
        }

        @Test
        @DisplayName("잘못된 문자는 상세 오류 메시지 반환")
        void invalidCharactersReturnsDetailedError() {
            Optional<String> result = Permissions.validateFormat("User:read");
            assertThat(result).isPresent();
            assertThat(result.get()).contains("User");
            assertThat(result.get()).contains("lowercase");
        }
    }

    @Nested
    @DisplayName("extractService")
    class ExtractService {

        @Test
        @DisplayName("3세그먼트에서 서비스명 추출")
        void extractsServiceFromThreeSegments() {
            assertThat(Permissions.extractService("authhub:user:read")).isEqualTo("authhub");
            assertThat(Permissions.extractService("product-service:product:write"))
                    .isEqualTo("product-service");
        }

        @Test
        @DisplayName("2세그먼트에서는 null 반환")
        void returnsNullForTwoSegments() {
            assertThat(Permissions.extractService("user:read")).isNull();
            assertThat(Permissions.extractService("product:write")).isNull();
        }

        @Test
        @DisplayName("null 입력 시 null 반환")
        void returnsNullForNullInput() {
            assertThat(Permissions.extractService(null)).isNull();
        }

        @Test
        @DisplayName("콜론 없는 문자열은 null 반환")
        void returnsNullForNoColon() {
            assertThat(Permissions.extractService("userread")).isNull();
        }
    }

    @Nested
    @DisplayName("extractDomain")
    class ExtractDomain {

        @Test
        @DisplayName("2세그먼트에서 도메인 추출")
        void extractsDomainFromTwoSegments() {
            assertThat(Permissions.extractDomain("user:read")).isEqualTo("user");
            assertThat(Permissions.extractDomain("product:write")).isEqualTo("product");
        }

        @Test
        @DisplayName("3세그먼트에서 도메인 추출 (두 번째 세그먼트)")
        void extractsDomainFromThreeSegments() {
            assertThat(Permissions.extractDomain("authhub:user:read")).isEqualTo("user");
            assertThat(Permissions.extractDomain("service:product:write")).isEqualTo("product");
        }

        @Test
        @DisplayName("null 입력 시 null 반환")
        void returnsNullForNullInput() {
            assertThat(Permissions.extractDomain(null)).isNull();
        }
    }

    @Nested
    @DisplayName("extractAction")
    class ExtractAction {

        @Test
        @DisplayName("2세그먼트에서 액션 추출")
        void extractsActionFromTwoSegments() {
            assertThat(Permissions.extractAction("user:read")).isEqualTo("read");
            assertThat(Permissions.extractAction("product:write")).isEqualTo("write");
        }

        @Test
        @DisplayName("3세그먼트에서 액션 추출 (마지막 세그먼트)")
        void extractsActionFromThreeSegments() {
            assertThat(Permissions.extractAction("authhub:user:read")).isEqualTo("read");
            assertThat(Permissions.extractAction("service:product:delete")).isEqualTo("delete");
        }

        @Test
        @DisplayName("null 입력 시 null 반환")
        void returnsNullForNullInput() {
            assertThat(Permissions.extractAction(null)).isNull();
        }
    }

    @Nested
    @DisplayName("of (factory methods)")
    class OfFactoryMethods {

        @Test
        @DisplayName("2세그먼트 권한 생성")
        void createsTwoSegmentPermission() {
            assertThat(Permissions.of("user", "read")).isEqualTo("user:read");
            assertThat(Permissions.of("product", "write")).isEqualTo("product:write");
        }

        @Test
        @DisplayName("3세그먼트 권한 생성")
        void createsThreeSegmentPermission() {
            assertThat(Permissions.of("authhub", "user", "read")).isEqualTo("authhub:user:read");
            assertThat(Permissions.of("service", "product", "write"))
                    .isEqualTo("service:product:write");
        }

        @Test
        @DisplayName("잘못된 형식이면 예외 발생")
        void throwsExceptionForInvalidFormat() {
            assertThatThrownBy(() -> Permissions.of("User", "read"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> Permissions.of("1user", "read"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> Permissions.of("Service", "user", "read"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("predefined constants")
    class PredefinedConstants {

        @Test
        @DisplayName("모든 상수가 유효한 형식")
        void allConstantsAreValid() {
            assertThat(Permissions.isValidFormat(Permissions.USER_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.USER_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.USER_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ROLE_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ROLE_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ROLE_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PERMISSION_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PERMISSION_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PERMISSION_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ORGANIZATION_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ORGANIZATION_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ORGANIZATION_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.TENANT_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.TENANT_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.TENANT_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PRODUCT_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PRODUCT_WRITE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.PRODUCT_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.FILE_READ)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.FILE_UPLOAD)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.FILE_DELETE)).isTrue();
            assertThat(Permissions.isValidFormat(Permissions.ALL)).isTrue();
        }

        @Test
        @DisplayName("USER_ROLE_ASSIGN은 3세그먼트 형식")
        void userRoleAssignIsThreeSegment() {
            assertThat(Permissions.isValidFormat(Permissions.USER_ROLE_ASSIGN)).isTrue();
            assertThat(Permissions.extractService(Permissions.USER_ROLE_ASSIGN)).isEqualTo("user");
            assertThat(Permissions.extractDomain(Permissions.USER_ROLE_ASSIGN)).isEqualTo("role");
            assertThat(Permissions.extractAction(Permissions.USER_ROLE_ASSIGN)).isEqualTo("assign");
        }
    }

    @Nested
    @DisplayName("isValidFormatLegacy (deprecated)")
    class IsValidFormatLegacy {

        @Test
        @DisplayName("레거시 검증은 대소문자 허용")
        void legacyAllowsUppercase() {
            assertThat(Permissions.isValidFormatLegacy("User:Read")).isTrue();
            assertThat(Permissions.isValidFormatLegacy("USER:READ")).isTrue();
        }

        @Test
        @DisplayName("레거시 검증도 최소 2세그먼트 필요")
        void legacyRequiresTwoSegments() {
            assertThat(Permissions.isValidFormatLegacy("onlyone")).isFalse();
            assertThat(Permissions.isValidFormatLegacy("user:read")).isTrue();
        }
    }
}
