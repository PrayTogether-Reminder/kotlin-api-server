package site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.entity.BaseEntity
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member

@Entity
@Table(name = "fcm_token")
@SequenceGenerator(
    name = "fcm_token_seq_generator",
    sequenceName = "fcm_token_seq",
    allocationSize = 100
)
class FcmToken protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fcm_token_seq_generator")
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member? = null
        protected set

    @Column(name = "token", nullable = false, length = 512)
    var token: String = ""
        protected set

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
        protected set

    constructor(
        member: Member,
        token: String,
        isActive: Boolean = true
    ) : this() {
        this.member = member
        this.token = token
        this.isActive = isActive
    }

    fun activate() {
        this.isActive = true
    }

    fun deactivate() {
        this.isActive = false
    }

    fun updateToken(newToken: String) {
        this.token = newToken
    }

    companion object {
        fun create(member: Member, token: String): FcmToken {
            return FcmToken(member, token, true)
        }
    }
}