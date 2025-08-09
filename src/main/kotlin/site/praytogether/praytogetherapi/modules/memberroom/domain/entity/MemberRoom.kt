package site.praytogether.praytogetherapi.modules.memberroom.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import site.praytogether.praytogetherapi.common.constant.CoreConstant.MemberRoomConstant
import site.praytogether.praytogetherapi.common.entity.BaseEntity
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Entity
@Table(
    name = "member_room",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_room_member_id_room_id",
            columnNames = ["member_id", "room_id"]
        )
    ]
)
@SequenceGenerator(
    name = "MEMBER_ROOM_SEQ_GENERATOR",
    sequenceName = "MEMBER_ROOM_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class MemberRoom protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_ROOM_SEQ_GENERATOR")
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    lateinit var member: Member
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    lateinit var room: Room
        protected set

    @Column(nullable = false, length = MemberRoomConstant.ROLE_MAX_LEN)
    @Enumerated(EnumType.STRING)
    var role: RoomRole = RoomRole.MEMBER
        protected set

    @Column(name = "is_notification", nullable = false)
    var isNotification: Boolean = true
        protected set

    constructor(
        member: Member,
        room: Room,
        role: RoomRole,
        isNotification: Boolean = true
    ) : this() {
        this.member = member
        this.room = room
        this.role = role
        this.isNotification = isNotification
    }

    fun updateNotificationSetting(isNotification: Boolean) {
        this.isNotification = isNotification
    }

    fun changeRole(newRole: RoomRole) {
        this.role = newRole
    }

    companion object {
        fun create(
            member: Member,
            room: Room,
            role: RoomRole,
            isNotification: Boolean = true
        ): MemberRoom {
            return MemberRoom(member, room, role, isNotification)
        }
    }
}