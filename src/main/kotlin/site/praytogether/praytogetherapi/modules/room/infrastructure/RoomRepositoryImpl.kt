package site.praytogether.praytogetherapi.modules.room.infrastructure

import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.repository.RoomRepository

@Repository
class RoomRepositoryImpl(
    private val roomJpaRepository: RoomJpaRepository
) : RoomRepository {

    override fun save(room: Room): Room {
        return roomJpaRepository.save(room)
    }

    override fun findById(id: Long): Room? {
        return roomJpaRepository.findById(id).orElse(null)
    }

    override fun findAll(): List<Room> {
        return roomJpaRepository.findAll()
    }

    override fun delete(room: Room) {
        roomJpaRepository.delete(room)
    }
    
    override fun deleteAll() {
        roomJpaRepository.deleteAll()
    }
}