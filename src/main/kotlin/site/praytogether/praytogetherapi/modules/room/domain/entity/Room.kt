package site.praytogether.praytogetherapi.modules.room.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import site.praytogether.praytogetherapi.common.constant.CoreConstant.RoomConstant
import site.praytogether.praytogetherapi.common.entity.BaseEntity
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Entity
@Table(name = "room")
@SequenceGenerator(
    name = "ROOM_SEQ_GENERATOR",
    sequenceName = "ROOM_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class Room protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROOM_SEQ_GENERATOR")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = RoomConstant.NAME_MAX_LEN)
    var name: String = ""
        protected set

    @Column(nullable = false, length = RoomConstant.DESCRIPTION_MAX_LEN)
    var description: String = ""
        protected set

    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)  // N+1 문제 방지: IN 절로 100개씩 묶어서 조회
    protected val memberRooms: MutableList<MemberRoom> = mutableListOf()

    constructor(name: String, description: String) : this() {
        this.name = name
        this.description = description
    }

    fun updateInfo(name: String, description: String) {
        this.name = name
        this.description = description
    }

    fun addMember(member: Member, role: RoomRole = RoomRole.MEMBER): MemberRoom {
        // 중복 체크
        if (hasMember(member)) {
            throw IllegalArgumentException("Member ${member.id} is already in room ${this.id}")
        }
        
        val memberRoom = MemberRoom.create(member, this, role)
        memberRooms.add(memberRoom)  // Room -> MemberRoom 관계 설정
        // MemberRoom -> Room 관계는 MemberRoom.create에서 이미 설정됨
        return memberRoom
    }

    fun removeMember(member: Member): Boolean {
        return memberRooms.removeIf { it.member.id == member.id }
    }

    fun getMembers(): List<MemberRoom> = memberRooms.toList()

    fun hasMember(member: Member): Boolean {
        return memberRooms.any { it.member.id == member.id }
    }

    companion object {
        fun create(name: String, description: String): Room {
            return Room(name, description)
        }
    }
}