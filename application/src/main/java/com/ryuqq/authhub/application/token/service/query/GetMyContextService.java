package com.ryuqq.authhub.application.token.service.query;

import com.ryuqq.authhub.application.token.assembler.MyContextCompositeAssembler;
import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.internal.MyContextReadFacade;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * GetMyContextService - 내 컨텍스트 조회 Service
 *
 * <p>GetMyContextUseCase 구현체입니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>MyContextReadFacade: Composite 데이터 조회
 *   <li>MyContextCompositeAssembler: Composite → Response 변환
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>UseCase 1:1 Service 구현
 *   <li>@Transactional 금지 (ReadFacade/Manager에서 처리)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetMyContextService implements GetMyContextUseCase {

    private final MyContextReadFacade myContextReadFacade;
    private final MyContextCompositeAssembler myContextCompositeAssembler;

    public GetMyContextService(
            MyContextReadFacade myContextReadFacade,
            MyContextCompositeAssembler myContextCompositeAssembler) {
        this.myContextReadFacade = myContextReadFacade;
        this.myContextCompositeAssembler = myContextCompositeAssembler;
    }

    @Override
    public MyContextResponse execute(String userId) {
        UserId userIdVo = UserId.of(userId);
        MyContextComposite composite = myContextReadFacade.findMyContext(userIdVo);
        return myContextCompositeAssembler.toResponse(composite);
    }
}
