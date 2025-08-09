package site.praytogether.praytogetherapi.modules.room.domain.repository

import site.praytogether.praytogetherapi.modules.room.domain.entity.Room

interface RoomRepository {
    fun save(room: Room): Room
    fun findById(id: Long): Room?
    fun findAll(): List<Room>
    fun delete(room: Room)
    fun deleteAll()
}