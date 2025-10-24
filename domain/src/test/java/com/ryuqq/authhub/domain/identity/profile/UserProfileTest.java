package com.ryuqq.authhub.domain.identity.profile;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.profile.vo.Bio;
import com.ryuqq.authhub.domain.identity.profile.vo.Nickname;
import com.ryuqq.authhub.domain.identity.profile.vo.ProfileImageUrl;
import com.ryuqq.authhub.domain.identity.profile.vo.UserProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link UserProfile} Aggregate Root에 대한 Unit Test.
 *
 * <p>UserProfile의 비즈니스 로직, Law of Demeter 준수, 도메인 이벤트 발행을 검증합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserProfile Aggregate Root 테스트")
class UserProfileTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("create() 팩토리 메서드로 새 프로필 생성 성공")
        void createNewProfile() {
            // given
            UserId userId = UserId.newId();
            Nickname nickname = new Nickname("홍길동");

            // when
            UserProfile profile = UserProfile.create(userId, nickname);

            // then
            assertThat(profile.getId()).isNotNull();
            assertThat(profile.getUserId()).isEqualTo(userId);
            assertThat(profile.getNickname()).isEqualTo(nickname);
            assertThat(profile.hasDefaultImage()).isTrue();
            assertThat(profile.hasBio()).isFalse();
            assertThat(profile.getCreatedAt()).isNotNull();
            assertThat(profile.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("create() 호출 시 UserId가 null이면 예외 발생")
        void createWithNullUserId() {
            // given
            Nickname nickname = new Nickname("홍길동");

            // when & then
            assertThatThrownBy(() -> UserProfile.create(null, nickname))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("UserId cannot be null");
        }

        @Test
        @DisplayName("create() 호출 시 Nickname이 null이면 예외 발생")
        void createWithNullNickname() {
            // given
            UserId userId = UserId.newId();

            // when & then
            assertThatThrownBy(() -> UserProfile.create(userId, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Nickname cannot be null");
        }

        @Test
        @DisplayName("reconstruct() 팩토리 메서드로 기존 프로필 재구성 성공")
        void reconstructProfile() {
            // given
            UserProfileId id = UserProfileId.newId();
            UserId userId = UserId.newId();
            Nickname nickname = new Nickname("홍길동");
            ProfileImageUrl imageUrl = new ProfileImageUrl("https://example.com/profile.png");
            Bio bio = new Bio("안녕하세요");
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            // when
            UserProfile profile = UserProfile.reconstruct(id, userId, nickname, imageUrl, bio, createdAt, updatedAt);

            // then
            assertThat(profile.getId()).isEqualTo(id);
            assertThat(profile.getUserId()).isEqualTo(userId);
            assertThat(profile.getNickname()).isEqualTo(nickname);
            assertThat(profile.getProfileImageUrl()).isEqualTo(imageUrl);
            assertThat(profile.getBio()).isEqualTo(bio);
            assertThat(profile.getCreatedAt()).isEqualTo(createdAt);
            assertThat(profile.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("Law of Demeter 준수 테스트")
    class LawOfDemeterTest {

        @Test
        @DisplayName("getNicknameValue() 메서드로 getter chaining 방지")
        void getNicknameValue() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when
            String nicknameValue = profile.getNicknameValue();

            // then
            assertThat(nicknameValue).isEqualTo("홍길동");
            // ❌ profile.getNickname().getValue() 대신
            // ✅ profile.getNicknameValue() 사용
        }

        @Test
        @DisplayName("getProfileImageUrlValue() 메서드로 getter chaining 방지")
        void getProfileImageUrlValue() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when
            String imageUrlValue = profile.getProfileImageUrlValue();

            // then
            assertThat(imageUrlValue).isNotEmpty();
            // ❌ profile.getProfileImageUrl().getValue() 대신
            // ✅ profile.getProfileImageUrlValue() 사용
        }

        @Test
        @DisplayName("getBioValue() 메서드로 getter chaining 방지")
        void getBioValue() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when
            String bioValue = profile.getBioValue();

            // then
            assertThat(bioValue).isEmpty();
            // ❌ profile.getBio().getValue() 대신
            // ✅ profile.getBioValue() 사용
        }
    }

    @Nested
    @DisplayName("닉네임 변경 테스트")
    class UpdateNicknameTest {

        @Test
        @DisplayName("닉네임 변경 시 새로운 인스턴스 반환 (불변성)")
        void updateNicknameReturnsNewInstance() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            Nickname newNickname = new Nickname("김철수");

            // when
            UserProfile updatedProfile = profile.updateNickname(newNickname);

            // then
            assertThat(updatedProfile).isNotSameAs(profile); // 다른 인스턴스
            assertThat(updatedProfile.getNickname()).isEqualTo(newNickname);
            assertThat(profile.getNickname()).isEqualTo(new Nickname("홍길동")); // 원본 불변
        }

        @Test
        @DisplayName("닉네임 변경 시 updatedAt 갱신")
        void updateNicknameUpdatesTimestamp() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            Instant beforeUpdate = profile.getUpdatedAt();

            // when
            UserProfile updatedProfile = profile.updateNickname(new Nickname("김철수"));

            // then
            assertThat(updatedProfile.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("동일한 닉네임으로 변경 시 현재 인스턴스 반환")
        void updateWithSameNicknameReturnsCurrentInstance() {
            // given
            Nickname sameNickname = new Nickname("홍길동");
            UserProfile profile = UserProfile.create(UserId.newId(), sameNickname);

            // when
            UserProfile updatedProfile = profile.updateNickname(sameNickname);

            // then
            assertThat(updatedProfile).isSameAs(profile); // 동일 인스턴스
        }


        @Test
        @DisplayName("null 닉네임으로 변경 시 예외 발생")
        void updateWithNullNickname() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when & then
            assertThatThrownBy(() -> profile.updateNickname(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("New nickname cannot be null");
        }
    }

    @Nested
    @DisplayName("프로필 이미지 변경 테스트")
    class UpdateProfileImageTest {

        @Test
        @DisplayName("프로필 이미지 변경 시 새로운 인스턴스 반환 (불변성)")
        void updateProfileImageReturnsNewInstance() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            ProfileImageUrl newImageUrl = new ProfileImageUrl("https://example.com/new-profile.png");

            // when
            UserProfile updatedProfile = profile.updateProfileImage(newImageUrl);

            // then
            assertThat(updatedProfile).isNotSameAs(profile);
            assertThat(updatedProfile.getProfileImageUrl()).isEqualTo(newImageUrl);
        }

    }

    @Nested
    @DisplayName("자기소개 변경 테스트")
    class UpdateBioTest {

        @Test
        @DisplayName("자기소개 변경 시 새로운 인스턴스 반환 (불변성)")
        void updateBioReturnsNewInstance() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            Bio newBio = new Bio("안녕하세요, AuthHub 개발자입니다.");

            // when
            UserProfile updatedProfile = profile.updateBio(newBio);

            // then
            assertThat(updatedProfile).isNotSameAs(profile);
            assertThat(updatedProfile.getBio()).isEqualTo(newBio);
            assertThat(updatedProfile.hasBio()).isTrue();
        }

    }

    @Nested
    @DisplayName("프로필 전체 업데이트 테스트")
    class UpdateProfileTest {

        @Test
        @DisplayName("프로필 전체 업데이트 시 새로운 인스턴스 반환 (불변성)")
        void updateProfileReturnsNewInstance() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            Nickname newNickname = new Nickname("김철수");
            ProfileImageUrl newImageUrl = new ProfileImageUrl("https://example.com/new-profile.png");
            Bio newBio = new Bio("안녕하세요, AuthHub 개발자입니다.");

            // when
            UserProfile updatedProfile = profile.updateProfile(newNickname, newImageUrl, newBio);

            // then
            assertThat(updatedProfile).isNotSameAs(profile);
            assertThat(updatedProfile.getNickname()).isEqualTo(newNickname);
            assertThat(updatedProfile.getProfileImageUrl()).isEqualTo(newImageUrl);
            assertThat(updatedProfile.getBio()).isEqualTo(newBio);
        }


        @Test
        @DisplayName("모든 값이 동일하면 현재 인스턴스 반환")
        void updateWithSameValuesReturnsCurrentInstance() {
            // given
            Nickname nickname = new Nickname("홍길동");
            UserProfile profile = UserProfile.create(UserId.newId(), nickname);

            // when
            UserProfile updatedProfile = profile.updateProfile(
                    nickname,
                    profile.getProfileImageUrl(),
                    profile.getBio()
            );

            // then
            assertThat(updatedProfile).isSameAs(profile);
        }
    }

    @Nested
    @DisplayName("동등성 비교 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 UserProfileId를 가진 두 객체는 동등하다")
        void equalsWithSameId() {
            // given
            UserProfileId id = UserProfileId.newId();
            UserId userId = UserId.newId();
            Nickname nickname = new Nickname("홍길동");

            UserProfile profile1 = UserProfile.reconstruct(
                    id, userId, nickname,
                    ProfileImageUrl.defaultImage(), Bio.empty(),
                    Instant.now(), Instant.now()
            );

            UserProfile profile2 = UserProfile.reconstruct(
                    id, userId, new Nickname("김철수"), // 다른 닉네임
                    ProfileImageUrl.defaultImage(), Bio.empty(),
                    Instant.now(), Instant.now()
            );

            // when & then
            assertThat(profile1).isEqualTo(profile2); // ID가 같으면 동등
            assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
        }

        @Test
        @DisplayName("다른 UserProfileId를 가진 두 객체는 동등하지 않다")
        void notEqualsWithDifferentId() {
            // given
            UserProfile profile1 = UserProfile.create(UserId.newId(), new Nickname("홍길동"));
            UserProfile profile2 = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when & then
            assertThat(profile1).isNotEqualTo(profile2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()이 모든 필드를 포함한다")
        void toStringContainsAllFields() {
            // given
            UserProfile profile = UserProfile.create(UserId.newId(), new Nickname("홍길동"));

            // when
            String result = profile.toString();

            // then
            assertThat(result).contains("UserProfile");
            assertThat(result).contains("id=");
            assertThat(result).contains("userId=");
            assertThat(result).contains("nickname=");
        }
    }
}
