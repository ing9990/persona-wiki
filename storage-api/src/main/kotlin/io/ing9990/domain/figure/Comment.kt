package io.ing9990.domain.figure

import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "comment")
class Comment(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "figure_id")
    val figure: Figure,
    @Column(name = "content", nullable = false)
    var content: String,
    @Column(name = "likes")
    var likes: Int = 0,
    @Column(name = "dislikes")
    var dislikes: Int = 0,
) : BaseEntity()
