package site.praytogether.praytogetherapi.modules.notification.domain.constant

object NotificationMessageFormat {
    const val PRAYER_COMPLETION = "%s님이 %s 기도제목으로 기도했습니다.\n기도로 함께 동참해 주세요!"
    
    fun formatPrayerCompletion(memberName: String, prayerTitle: String): String {
        return PRAYER_COMPLETION.format(memberName, prayerTitle)
    }
}