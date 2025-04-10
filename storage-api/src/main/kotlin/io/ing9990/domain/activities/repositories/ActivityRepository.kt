package io.ing9990.domain.activities.repositories

import io.ing9990.domain.activities.Activity
import io.ing9990.domain.activities.repository.querydsl.ActivityCustomRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityRepository :
    JpaRepository<Activity, Long>,
    ActivityCustomRepository {
    // 기본 CRUD 메서드는 JpaRepository에서 상속
}
