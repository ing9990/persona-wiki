package io.ing9990.domain.category

import io.ing9990.domain.figure.Figure
import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity(name = "category")
@Table(name = "category")
class Category(
    @Id @Column(name = "category_id") val id: String,
    @Column(name = "display_name", nullable = false)
    val displayName: String,
    // 표시 이름 (예: 대통령, 유튜버)
    @Column(name = "description", nullable = false) var description: String,
    @Column(name = "image_url", nullable = false) var imageUrl: String,
    @OneToMany(mappedBy = "category") val figures: MutableList<Figure> = mutableListOf(),
) : BaseEntity() {
    /**
     * 인물을 카테고리에 추가합니다.
     */
    fun addFigure(figure: Figure) {
        figures.add(figure)
        figure.category = this
    }

    /**
     * 인물을 카테고리에서 제거합니다.
     */
    fun removeFigure(figure: Figure) {
        figures.remove(figure)
    }
}
