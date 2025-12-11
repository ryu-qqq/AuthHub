package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchUsersService - 사용자 목록 검색 Service
 *
 * <p>SearchUsersUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class SearchUsersService implements SearchUsersUseCase {

    private final UserReadManager readManager;
    private final UserAssembler assembler;

    public SearchUsersService(UserReadManager readManager, UserAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public List<UserResponse> execute(SearchUsersQuery query) {
        // 1. Manager: 검색
        List<User> users = readManager.search(query);

        // 2. Assembler: Response 목록 변환
        return assembler.toResponseList(users);
    }
}
