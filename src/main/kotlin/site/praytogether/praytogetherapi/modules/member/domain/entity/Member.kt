package site.praytogether.praytogetherapi.modules.member.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.constant.CoreConstant.MemberConstant
import site.praytogether.praytogetherapi.common.entity.BaseEntity

@Entity
@Table(name = "member")
@SequenceGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class Member protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = MemberConstant.EMAIL_MAX_LEN, unique = true)
    var email: String = ""
        protected set

    @Column(nullable = false, length = MemberConstant.NAME_MAX_LEN)
    var name: String = ""
        protected set

    @Column(nullable = false, length = MemberConstant.PASSWORD_MAX_LEN)
    var password: String = ""
        protected set

    constructor(name: String, email: String, password: String) : this() {
        this.name = name
        this.email = email
        this.password = password
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun updateName(newName: String) {
        this.name = newName
    }

    companion object {
        fun create(name: String, email: String, password: String): Member {
            return Member(name, email, password)
        }
    }
}