package com.ryuqq.authhub.application.user.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserAssembler 단위 테스트")
class UserAssemblerTest {

    private UserAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new UserAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            User user = UserFixture.create();

            // when
            UserResult result = sut.toResult(user);

            // then
            assertThat(result.userId()).isEqualTo(user.userIdValue());
            assertThat(result.organizationId()).isEqualTo(user.organizationIdValue());
            assertThat(result.identifier()).isEqualTo(user.identifierValue());
            assertThat(result.phoneNumber()).isEqualTo(user.phoneNumberValue());
            assertThat(result.status()).isEqualTo(user.statusValue());
            assertThat(result.createdAt()).isEqualTo(user.createdAt());
            assertThat(result.updatedAt()).isEqualTo(user.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllUsers_ToResultList() {
            // given
            User u1 = UserFixture.createWithIdentifier("user1@example.com");
            User u2 = UserFixture.createWithIdentifier("user2@example.com");
            List<User> users = List.of(u1, u2);

            // when
            List<UserResult> results = sut.toResultList(users);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).identifier()).isEqualTo("user1@example.com");
            assertThat(results.get(1).identifier()).isEqualTo("user2@example.com");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // when
            List<UserResult> results = sut.toResultList(Collections.emptyList());

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult 메서드")
    class ToPageResult {

        @Test
        @DisplayName("성공: Domain 목록과 페이징 정보가 PageResult로 올바르게 변환됨")
        void shouldCreatePageResult_WithCorrectPagination() {
            // given
            User user = UserFixture.create();
            List<User> users = List.of(user);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            UserPageResult result = sut.toPageResult(users, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
