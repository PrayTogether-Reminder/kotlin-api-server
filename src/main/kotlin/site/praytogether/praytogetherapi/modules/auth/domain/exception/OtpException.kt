package site.praytogether.praytogetherapi.modules.auth.domain.exception

class OtpSendFailException(message: String) : RuntimeException(message)

class OtpTemplateLoadFailException(message: String) : RuntimeException(message)