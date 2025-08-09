package site.praytogether.praytogetherapi.modules.room.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room

interface RoomJpaRepository : JpaRepository<Room, Long> {
    fun findByNameContaining(name: String): List<Room>
}