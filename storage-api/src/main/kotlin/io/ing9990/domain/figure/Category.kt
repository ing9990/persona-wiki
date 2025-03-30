package io.ing9990.domain.figure

import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity(name = "category")
@Table(name = "category")
class Category(
    @Id
    @Column(name = "category_id")
    val id: String,
    @Column(name = "display_name", nullable = false)
    val displayName: String,
    // 표시 이름 (예: 대통령, 유튜버)
    @Column(name = "description")
    var description: String? = null,
    @Column(name = "image_url")
    var imageUrl: String? = "https://media.istockphoto.com/id/1475222907/ko/벡터/벡터-문자-실루엣-여성과-남성의-인식-할-수없는-초상화-사람들의-그룹.jpg?s=612x612&w=0&k=20&c=SNJcvBG4DOAk892m968F9WDQFegUJ2Jw851ZH3joTx4=",
    @OneToMany(mappedBy = "category")
    val figures: MutableList<Figure> = mutableListOf(),
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
