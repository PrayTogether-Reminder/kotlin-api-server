package site.praytogether.praytogetherapi.modules.prayer.domain.exception

// Re-export common exceptions for backward compatibility
typealias PrayerNotFoundException = site.praytogether.praytogetherapi.common.exception.PrayerException

// Factory functions to create exceptions
fun prayerTitleNotFound(prayerTitleId: Long? = null): PrayerNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.PrayerException.prayerTitleNotFound(prayerTitleId)
}