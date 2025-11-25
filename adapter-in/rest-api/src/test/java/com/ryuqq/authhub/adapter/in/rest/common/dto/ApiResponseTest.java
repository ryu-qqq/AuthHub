package com.ryuqq.authhub.adapter.in.rest.common.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ApiResponse 테스트")
class ApiResponseTest {

    @Nested
    @DisplayName("성공 응답 생성 테스트")
    class SuccessResponseTests {

        @Test
        @DisplayName("ofSuccess - 데이터가 있는 성공 응답 생성")
        void ofSuccess_withData_success() {
            // Given
            String testData = "test data";

            // When
            ApiResponse<String> response = ApiResponse.ofSuccess(testData);

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(testData);
            assertThat(response.error()).isNull();
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
            assertThat(response.requestId()).startsWith("req-");
        }

        @Test
        @DisplayName("ofSuccess - 데이터가 없는 성공 응답 생성")
        void ofSuccess_withoutData_success() {
            // When
            ApiResponse<Void> response = ApiResponse.ofSuccess();

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isNull();
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
            assertThat(response.requestId()).startsWith("req-");
        }

        @Test
        @DisplayName("ofSuccess - null 데이터로 성공 응답 생성")
        void ofSuccess_withNullData_success() {
            // Given
            String nullData = null;

            // When
            ApiResponse<String> response = ApiResponse.ofSuccess(nullData);

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isNull();
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
        }

        @Test
        @DisplayName("ofSuccess - 복잡한 객체 데이터로 성공 응답 생성")
        void ofSuccess_withComplexData_success() {
            // Given
            Map<String, Object> complexData =
                    Map.of("id", 123L, "name", "test", "items", List.of("item1", "item2"));

            // When
            ApiResponse<Map<String, Object>> response = ApiResponse.ofSuccess(complexData);

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo(complexData);
            assertThat(response.data().get("id")).isEqualTo(123L);
            assertThat(response.data().get("name")).isEqualTo("test");
            assertThat(response.error()).isNull();
        }
    }

    @Nested
    @DisplayName("실패 응답 생성 테스트")
    class FailureResponseTests {

        @Test
        @DisplayName("ofFailure - ErrorInfo로 실패 응답 생성")
        void ofFailure_withErrorInfo_success() {
            // Given
            ErrorInfo errorInfo = new ErrorInfo("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");

            // When
            ApiResponse<String> response = ApiResponse.ofFailure(errorInfo);

            // Then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isEqualTo(errorInfo);
            assertThat(response.error().errorCode()).isEqualTo("USER_NOT_FOUND");
            assertThat(response.error().message()).isEqualTo("사용자를 찾을 수 없습니다");
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
            assertThat(response.requestId()).startsWith("req-");
        }

        @Test
        @DisplayName("ofFailure - 에러 코드와 메시지로 실패 응답 생성")
        void ofFailure_withCodeAndMessage_success() {
            // Given
            String errorCode = "VALIDATION_ERROR";
            String errorMessage = "입력값이 올바르지 않습니다";

            // When
            ApiResponse<String> response = ApiResponse.ofFailure(errorCode, errorMessage);

            // Then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isNotNull();
            assertThat(response.error().errorCode()).isEqualTo(errorCode);
            assertThat(response.error().message()).isEqualTo(errorMessage);
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
        }

        @Test
        @DisplayName("ofFailure - null ErrorInfo로 실패 응답 생성")
        void ofFailure_withNullErrorInfo_success() {
            // Given
            ErrorInfo nullErrorInfo = null;

            // When
            ApiResponse<String> response = ApiResponse.ofFailure(nullErrorInfo);

            // Then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isNull();
            assertThat(response.timestamp()).isNotNull();
            assertThat(response.requestId()).isNotNull();
        }

        @Test
        @DisplayName("ofFailure - 빈 문자열 에러 코드로 실패 응답 생성 시 예외 발생")
        void ofFailure_withEmptyStrings_throwsException() {
            // Given
            String emptyCode = "";
            String emptyMessage = "";

            // When & Then
            assertThatThrownBy(() -> ApiResponse.ofFailure(emptyCode, emptyMessage))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("errorCode는 필수입니다");
        }
    }

    @Nested
    @DisplayName("다양한 타입 테스트")
    class GenericTypeTests {

        @Test
        @DisplayName("Integer 타입 ApiResponse 생성")
        void integerType_success() {
            // Given
            Integer data = 42;

            // When
            ApiResponse<Integer> response = ApiResponse.ofSuccess(data);

            // Then
            assertThat(response.data()).isEqualTo(42);
            assertThat(response.success()).isTrue();
        }

        @Test
        @DisplayName("List 타입 ApiResponse 생성")
        void listType_success() {
            // Given
            List<String> data = List.of("item1", "item2", "item3");

            // When
            ApiResponse<List<String>> response = ApiResponse.ofSuccess(data);

            // Then
            assertThat(response.data()).hasSize(3);
            assertThat(response.data()).containsExactly("item1", "item2", "item3");
            assertThat(response.success()).isTrue();
        }

        @Test
        @DisplayName("커스텀 객체 타입 ApiResponse 생성")
        void customObjectType_success() {
            // Given
            record TestDto(String name, int value) {}
            TestDto data = new TestDto("test", 100);

            // When
            ApiResponse<TestDto> response = ApiResponse.ofSuccess(data);

            // Then
            assertThat(response.data().name()).isEqualTo("test");
            assertThat(response.data().value()).isEqualTo(100);
            assertThat(response.success()).isTrue();
        }

        @Test
        @DisplayName("Boolean 타입 ApiResponse 생성")
        void booleanType_success() {
            // Given
            Boolean data = true;

            // When
            ApiResponse<Boolean> response = ApiResponse.ofSuccess(data);

            // Then
            assertThat(response.data()).isTrue();
            assertThat(response.success()).isTrue();
        }
    }

    @Nested
    @DisplayName("Request ID 생성 테스트")
    class RequestIdTests {

        @Test
        @DisplayName("Request ID가 고유하게 생성됨")
        void requestId_isUnique() throws InterruptedException {
            // When
            ApiResponse<String> response1 = ApiResponse.ofSuccess("data1");
            Thread.sleep(1); // 1ms 대기하여 다른 timestamp 보장
            ApiResponse<String> response2 = ApiResponse.ofSuccess("data2");

            // Then
            assertThat(response1.requestId()).isNotEqualTo(response2.requestId());
            assertThat(response1.requestId()).startsWith("req-");
            assertThat(response2.requestId()).startsWith("req-");
        }

        @Test
        @DisplayName("성공과 실패 응답 모두 Request ID 생성")
        void requestId_generatedForBothSuccessAndFailure() throws InterruptedException {
            // When
            ApiResponse<String> successResponse = ApiResponse.ofSuccess("data");
            Thread.sleep(1); // 1ms 대기하여 다른 timestamp 보장
            ApiResponse<String> failureResponse = ApiResponse.ofFailure("ERROR", "message");

            // Then
            assertThat(successResponse.requestId()).isNotNull();
            assertThat(failureResponse.requestId()).isNotNull();
            assertThat(successResponse.requestId()).startsWith("req-");
            assertThat(failureResponse.requestId()).startsWith("req-");
            assertThat(successResponse.requestId()).isNotEqualTo(failureResponse.requestId());
        }
    }

    @Nested
    @DisplayName("Timestamp 테스트")
    class TimestampTests {

        @Test
        @DisplayName("Timestamp가 현재 시간 근처로 생성됨")
        void timestamp_isNearCurrentTime() {
            // Given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            // When
            ApiResponse<String> response = ApiResponse.ofSuccess("data");

            // Then
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);
            assertThat(response.timestamp()).isAfter(before);
            assertThat(response.timestamp()).isBefore(after);
        }

        @Test
        @DisplayName("성공과 실패 응답 모두 Timestamp 생성")
        void timestamp_generatedForBothSuccessAndFailure() {
            // When
            ApiResponse<String> successResponse = ApiResponse.ofSuccess("data");
            ApiResponse<String> failureResponse = ApiResponse.ofFailure("ERROR", "message");

            // Then
            assertThat(successResponse.timestamp()).isNotNull();
            assertThat(failureResponse.timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Record 메서드 테스트")
    class RecordMethodTests {

        @Test
        @DisplayName("equals - 동일한 값들로 생성된 ApiResponse는 같음")
        void equals_sameValues_returnsTrue() {
            // Given
            LocalDateTime fixedTime = LocalDateTime.of(2025, 11, 25, 10, 30, 0);
            String fixedRequestId = "req-123456";

            ApiResponse<String> response1 =
                    new ApiResponse<>(true, "data", null, fixedTime, fixedRequestId);
            ApiResponse<String> response2 =
                    new ApiResponse<>(true, "data", null, fixedTime, fixedRequestId);

            // When & Then
            assertThat(response1).isEqualTo(response2);
        }

        @Test
        @DisplayName("equals - 다른 값들로 생성된 ApiResponse는 다름")
        void equals_differentValues_returnsFalse() {
            // Given
            LocalDateTime time = LocalDateTime.of(2025, 11, 25, 10, 30, 0);

            ApiResponse<String> response1 = new ApiResponse<>(true, "data1", null, time, "req-1");
            ApiResponse<String> response2 = new ApiResponse<>(true, "data2", null, time, "req-2");

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("hashCode - 동일한 값들로 생성된 ApiResponse는 같은 해시코드")
        void hashCode_sameValues_sameHashCode() {
            // Given
            LocalDateTime fixedTime = LocalDateTime.of(2025, 11, 25, 10, 30, 0);
            String fixedRequestId = "req-123456";

            ApiResponse<String> response1 =
                    new ApiResponse<>(true, "data", null, fixedTime, fixedRequestId);
            ApiResponse<String> response2 =
                    new ApiResponse<>(true, "data", null, fixedTime, fixedRequestId);

            // When & Then
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("toString - 모든 필드 포함한 문자열 반환")
        void toString_containsAllFields() {
            // Given
            ErrorInfo error = new ErrorInfo("TEST_ERROR", "테스트 에러");
            ApiResponse<String> response =
                    new ApiResponse<>(
                            false,
                            null,
                            error,
                            LocalDateTime.of(2025, 11, 25, 10, 30, 0),
                            "req-123");

            // When
            String result = response.toString();

            // Then
            assertThat(result).contains("ApiResponse");
            assertThat(result).contains("success=false");
            assertThat(result).contains("data=null");
            assertThat(result).contains("error=");
            assertThat(result).contains("timestamp=");
            assertThat(result).contains("requestId=req-123");
        }
    }

    @Nested
    @DisplayName("실제 사용 시나리오 테스트")
    class UsageScenarioTests {

        @Test
        @DisplayName("사용자 조회 성공 시나리오")
        void userRetrievalSuccess_scenario() {
            // Given
            record UserDto(Long id, String name, String email) {}
            UserDto user = new UserDto(1L, "홍길동", "hong@example.com");

            // When
            ApiResponse<UserDto> response = ApiResponse.ofSuccess(user);

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data().id()).isEqualTo(1L);
            assertThat(response.data().name()).isEqualTo("홍길동");
            assertThat(response.data().email()).isEqualTo("hong@example.com");
            assertThat(response.error()).isNull();
        }

        @Test
        @DisplayName("사용자 조회 실패 시나리오")
        void userRetrievalFailure_scenario() {
            // When
            ApiResponse<Object> response =
                    ApiResponse.ofFailure("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");

            // Then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.error().errorCode()).isEqualTo("USER_NOT_FOUND");
            assertThat(response.error().message()).isEqualTo("사용자를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("데이터 생성 성공 시나리오 (데이터 없음)")
        void dataCreationSuccess_scenario() {
            // When
            ApiResponse<Void> response = ApiResponse.ofSuccess();

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isNull();
            assertThat(response.requestId()).isNotNull();
        }

        @Test
        @DisplayName("검증 실패 시나리오")
        void validationFailure_scenario() {
            // Given
            ErrorInfo validationError = new ErrorInfo("VALIDATION_ERROR", "이메일 형식이 올바르지 않습니다");

            // When
            ApiResponse<Object> response = ApiResponse.ofFailure(validationError);

            // Then
            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.error()).isEqualTo(validationError);
            assertThat(response.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("페이징 데이터 응답 시나리오")
        void pagingDataResponse_scenario() {
            // Given
            record PageData<T>(List<T> content, int page, int size, long total) {}
            List<String> items = List.of("item1", "item2", "item3");
            PageData<String> pageData = new PageData<>(items, 0, 3, 10L);

            // When
            ApiResponse<PageData<String>> response = ApiResponse.ofSuccess(pageData);

            // Then
            assertThat(response.success()).isTrue();
            assertThat(response.data().content()).hasSize(3);
            assertThat(response.data().page()).isZero();
            assertThat(response.data().size()).isEqualTo(3);
            assertThat(response.data().total()).isEqualTo(10L);
        }
    }
}
