package io.ing9990.api.vote

import io.ing9990.aop.AuthorizedUser
import io.ing9990.api.vote.dto.request.VoteRequest
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
@RequestMapping("api/v1/categories/{categoryId}/@{slug}/vote")
class VoteApi(
    private val voteService: VoteService,
) {
    val log = LoggerFactory.getLogger(VoteApi::class.java)

    @PostMapping
    fun voteFigure(
        @AuthorizedUser user: User,
        @PathVariable categoryId: String,
        @PathVariable slug: String,
        @Valid @RequestBody request: VoteRequest,
    ): ResponseEntity<Unit> {
        voteService.voteFigure(
            VoteData(
                user = user,
                categoryId = categoryId,
                slug = slug,
                sentiment = request.sentiment,
            ),
        )

        log.info("Votes saved: $request")

        return ResponseEntity.noContent().build()
    }
}
