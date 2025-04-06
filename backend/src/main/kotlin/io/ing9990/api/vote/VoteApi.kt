package io.ing9990.api.vote

import io.ing9990.aop.AuthorizedUser
import io.ing9990.api.vote.dto.request.VoteRequest
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.user.User
import io.ing9990.domain.vote.service.VoteService
import io.ing9990.domain.vote.service.dto.VoteData
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/{categoryId}/@{figureName}/vote")
class VoteApi(
    private val voteService: VoteService,
) {
    val log = LoggerFactory.getLogger(VoteApi::class.java)

    @PostMapping
    fun voteFigure(
        @AuthorizedUser user: User,
        @PathVariable categoryId: String,
        @PathVariable figureName: String,
        @Valid @RequestBody request: VoteRequest,
    ): ResponseEntity<Unit> {
        val updatedFigure: Figure =
            voteService.voteFigure(
                VoteData(
                    user = user,
                    categoryId = categoryId,
                    figureName = figureName,
                    sentiment = request.sentiment,
                ),
            )

        log.info("Votes saved: $updatedFigure")

        return ResponseEntity.noContent().build()
    }
}
