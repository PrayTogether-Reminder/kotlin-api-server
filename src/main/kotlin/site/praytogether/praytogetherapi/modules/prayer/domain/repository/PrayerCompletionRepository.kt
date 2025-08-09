package site.praytogether.praytogetherapi.modules.prayer.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerCompletion

interface PrayerCompletionRepository : JpaRepository<PrayerCompletion, Long>