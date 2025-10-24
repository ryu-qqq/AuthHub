package com.ryuqq.authhub.adapter.out.persistence.auth.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.user.mapper.UserEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.auth.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.auth.user.LastLoginAt;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.auth.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * UserPersistenceAdapter 단위 테스트.
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다.
 * Database나 Spring Context 없이 빠르게 실행됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>Repository와 Mapper는 Mock으로 대체</li>
 *   <li>각 Port 메서드의 동작 검증</li>
 *   <li>Null 안전성 검증</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserPersistenceAdapter 단위 테스트")
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserPersistenceAdapter userPersistenceAdapter;

    private UserId testUserId;
    private User testUser;
    private UserJpaEntity testEntity;

    @BeforeEach
    void setUp() {
        testUserId = UserId.newId();
        Instant now = Instant.now();

        testUser = User.create(testUserId);

        testEntity = UserJpaEntity.create(
                testUserId.value().toString(),
                UserJpaEntity.UserStatusEnum.ACTIVE,
                null,
                now,
                now
        );
    }

    @Test
    @DisplayName("UserId로 User를 성공적으로 조회한다")
    void load_Success() {
        // given
        String uid = testUserId.value().toString();
        given(userJpaRepository.findByUid(uid)).willReturn(Optional.of(testEntity));
        given(userEntityMapper.toDomain(testEntity)).willReturn(testUser);

        // when
        Optional<User> result = userPersistenceAdapter.load(testUserId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);

        then(userJpaRepository).should(times(1)).findByUid(uid);
        then(userEntityMapper).should(times(1)).toDomain(testEntity);
    }

    @Test
    @DisplayName("존재하지 않는 UserId로 조회 시 Empty를 반환한다")
    void load_NotFound_ReturnsEmpty() {
        // given
        String uid = testUserId.value().toString();
        given(userJpaRepository.findByUid(uid)).willReturn(Optional.empty());

        // when
        Optional<User> result = userPersistenceAdapter.load(testUserId);

        // then
        assertThat(result).isEmpty();

        then(userJpaRepository).should(times(1)).findByUid(uid);
        then(userEntityMapper).should(times(0)).toDomain(any());
    }

    @Test
    @DisplayName("load() 호출 시 userId가 null이면 예외를 던진다")
    void load_WithNullUserId_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> userPersistenceAdapter.load(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserId cannot be null");

        then(userJpaRepository).should(times(0)).findByUid(anyString());
    }

    @Test
    @DisplayName("User를 성공적으로 저장한다")
    void save_Success() {
        // given
        given(userEntityMapper.toEntity(testUser)).willReturn(testEntity);
        given(userJpaRepository.save(testEntity)).willReturn(testEntity);
        given(userEntityMapper.toDomain(testEntity)).willReturn(testUser);

        // when
        User result = userPersistenceAdapter.save(testUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUser);

        then(userEntityMapper).should(times(1)).toEntity(testUser);
        then(userJpaRepository).should(times(1)).save(testEntity);
        then(userEntityMapper).should(times(1)).toDomain(testEntity);
    }

    @Test
    @DisplayName("save() 호출 시 user가 null이면 예외를 던진다")
    void save_WithNullUser_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> userPersistenceAdapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("User cannot be null");

        then(userEntityMapper).should(times(0)).toEntity(any());
        then(userJpaRepository).should(times(0)).save(any());
    }

    @Test
    @DisplayName("생성자에 null Repository를 주입하면 예외를 던진다")
    void constructor_WithNullRepository_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> new UserPersistenceAdapter(null, userEntityMapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("userJpaRepository cannot be null");
    }

    @Test
    @DisplayName("생성자에 null Mapper를 주입하면 예외를 던진다")
    void constructor_WithNullMapper_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> new UserPersistenceAdapter(userJpaRepository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("userEntityMapper cannot be null");
    }
}
