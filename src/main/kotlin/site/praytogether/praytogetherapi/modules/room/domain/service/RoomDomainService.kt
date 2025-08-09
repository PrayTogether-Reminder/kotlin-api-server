package site.praytogether.praytogetherapi.modules.room.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.exception.roomNotFound
import site.praytogether.praytogetherapi.modules.room.domain.repository.RoomRepository

@Service
@Transactional(readOnly = true)
class RoomDomainService(
    private val roomRepository: RoomRepository
) {

    @Transactional
    fun createRoom(name: String, description: String, creatorId: Long): Room {
        val room = Room.create(name, description)
        return roomRepository.save(room)
    }

    fun getRoomById(roomId: Long): Room {
        return roomRepository.findById(roomId)
            ?: throw roomNotFound(roomId)
    }

    fun validateRoomExists(roomId: Long): Room {
        return getRoomById(roomId)
    }

    @Transactional
    fun updateRoom(roomId: Long, name: String?, description: String?): Room {
        val room = getRoomById(roomId)
        
        if (name != null && description != null) {
            room.updateInfo(name, description)
        }
        
        return roomRepository.save(room)
    }

    fun getAllRooms(): List<Room> {
        return roomRepository.findAll()
    }

    @Transactional
    fun deleteRoom(roomId: Long) {
        val room = getRoomById(roomId)
        roomRepository.delete(room)
    }

    @Transactional
    fun saveRoom(room: Room): Room {
        return roomRepository.save(room)
    }
}