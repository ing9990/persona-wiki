package io.ing9990.domain.figure.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.querydsl.QFigure
import io.ing9990.domain.vote.querydsl.QVote
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * FigureCustomRepository 인터페이스 구현체
 * QueryDSL을 사용하여 복잡한 쿼리 실행
 */
@Repository
class FigureRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : FigureCustomRepository {
    companion object {
        private val log = LoggerFactory.getLogger(FigureRepositoryImpl::class.java)
    }

    override fun findByIdWithVotes(id: Long): Figure? {
        val figure = QFigure.figure
        val vote = QVote.vote

        try {
            return jpaQueryFactory
                .selectFrom(figure)
                .leftJoin(figure.votes, vote)
                .fetchJoin() // votes 컬렉션 함께 로드
                .leftJoin(figure.category)
                .fetchJoin() // category 함께 로드
                .where(figure.id.eq(id))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findByIdWithVotes 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    override fun findByIdWithDetails(id: Long): Figure? {
        val figure = QFigure.figure
        val vote = QVote.vote

        try {
            return jpaQueryFactory
                .selectFrom(figure)
                .leftJoin(figure.category)
                .fetchJoin()
                .leftJoin(figure.votes, vote)
                .fetchJoin()
                .where(figure.id.eq(id))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findByIdWithDetails 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    override fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        name: String,
    ): Figure? {
        val figure = QFigure.figure
        val vote = QVote.vote

        try {
            return jpaQueryFactory
                .selectFrom(figure)
                .leftJoin(figure.category)
                .fetchJoin()
                .leftJoin(figure.votes, vote)
                .fetchJoin()
                .where(
                    figure.category.id
                        .eq(categoryId)
                        .and(figure.name.eq(name)),
                ).fetchOne()
        } catch (e: Exception) {
            log.error("findByCategoryIdAndNameWithDetails 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }
}
