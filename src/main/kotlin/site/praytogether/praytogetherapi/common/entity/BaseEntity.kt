package site.praytogether.praytogetherapi.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseEntity {

    @CreatedDate
    @Column(
        name = "created_time",
        updatable = false,
        columnDefinition = "TIMESTAMP(3) WITH TIME ZONE"
    )
    var createdTime: Instant? = null
        protected set

    @CreatedBy
    @Column(name = "created_by")
    var createdBy: Long? = null
        protected set

    @LastModifiedDate
    @Column(name = "updated_time", columnDefinition = "TIMESTAMP(3) WITH TIME ZONE")
    var updatedTime: Instant? = null
        protected set

    @LastModifiedBy
    @Column(name = "updated_By")
    var updatedBy: Long? = null
        protected set
}