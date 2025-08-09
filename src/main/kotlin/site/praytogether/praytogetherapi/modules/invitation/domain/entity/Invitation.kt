package site.praytogether.praytogetherapi.modules.invitation.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.entity.BaseEntity
import site.praytogether.praytogetherapi.modules.invitation.domain.exception.InvalidInvitationStatusException
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import java.time.Instant

@Entity
@Table(name = "invitation")
@SequenceGenerator(
    name = "invitation_seq_generator",
    sequenceName = "invitation_seq",
    initialValue = 1,
    allocationSize = 50
)
class Invitation protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invitation_seq_generator")
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    lateinit var room: Room
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    lateinit var inviter: Member
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    lateinit var invitee: Member
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: InvitationStatus = InvitationStatus.PENDING
        protected set

    @Column(name = "response_time")
    var responseTime: Instant? = null
        protected set

    constructor(
        room: Room,
        inviter: Member,
        invitee: Member
    ) : this() {
        this.room = room
        this.inviter = inviter
        this.invitee = invitee
        this.status = InvitationStatus.PENDING
    }

    fun accept() {
        if (status != InvitationStatus.PENDING) {
            throw InvalidInvitationStatusException("Cannot accept invitation with status: $status")
        }
        this.status = InvitationStatus.ACCEPTED
        this.responseTime = Instant.now()
    }

    fun reject() {
        if (status != InvitationStatus.PENDING) {
            throw InvalidInvitationStatusException("Cannot reject invitation with status: $status")
        }
        this.status = InvitationStatus.REJECTED
        this.responseTime = Instant.now()
    }

    companion object {
        fun create(inviter: Member, invitee: Member, room: Room): Invitation {
            return Invitation(room, inviter, invitee)
        }
    }
}