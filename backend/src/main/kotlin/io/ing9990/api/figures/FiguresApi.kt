package io.ing9990.api.figures

import io.ing9990.api.figures.dto.request.CreateFigureRequest
import io.ing9990.api.figures.dto.request.VoteRequest
import io.ing9990.api.figures.dto.response.FigureResponse
import io.ing9990.api.figures.dto.response.ReputationResponse
import io.ing9990.api.figures.dto.response.VoteResponse
import io.ing9990.domain.figure.service.FigureService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 인물 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/figures")
class FiguresApi(
    private val figureService: FigureService,
) {
    /**
     * 인물에 대한 평가를 등록합니다.
     */
    @PostMapping("/{figureId}/vote")
    fun voteFigure(
        @PathVariable figureId: Long,
        @RequestBody request: VoteRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<VoteResponse> {
        val updated = figureService.voteFigure(figureId, request.sentiment)

        return ResponseEntity.ok(
            VoteResponse(
                success = true,
                message = "평가가 성공적으로 등록되었습니다.",
                likeCount = updated.reputation.likeCount,
                dislikeCount = updated.reputation.dislikeCount,
                neutralCount = updated.reputation.neutralCount,
            ),
        )
    }

    /**
     * 인물의 평판 정보를 조회합니다.
     */
    @GetMapping("/{figureId}/reputation")
    fun getReputation(
        @PathVariable figureId: Long,
    ): ResponseEntity<ReputationResponse> {
        val figure = figureService.findById(figureId)

        return ResponseEntity.ok(
            ReputationResponse(
                likeCount = figure.reputation.likeCount,
                dislikeCount = figure.reputation.dislikeCount,
                neutralCount = figure.reputation.neutralCount,
                likeRatio = figure.reputation.likeRatio(),
                dislikeRatio = figure.reputation.dislikeRatio(),
                neutralRatio = figure.reputation.neutralRatio(),
                total = figure.reputation.total(),
            ),
        )
    }

    // 인물 생성 API 추가
    @PostMapping
    fun createFigure(
        @RequestBody request: CreateFigureRequest,
    ): ResponseEntity<FigureResponse> {
        val figure =
            figureService.createFigure(
                name = request.name,
                categoryId = request.categoryId,
                imageUrl = request.imageUrl,
                bio = request.bio,
            )

        return ResponseEntity.status(HttpStatus.CREATED).body(FigureResponse.from(figure))
    }
}
