package site.praytogether.praytogetherapi.common.constant

object CoreConstant {
    private const val RDBMS_CHAR_LEN_BYTE = 3

    object MemberConstant {
        const val EMAIL_MAX_LEN = 50 * RDBMS_CHAR_LEN_BYTE
        const val NAME_MAX_LEN = 20 * RDBMS_CHAR_LEN_BYTE
        const val PASSWORD_MAX_LEN = 20 * RDBMS_CHAR_LEN_BYTE
    }

    object RoomConstant {
        const val NAME_MAX_LEN = 50 * RDBMS_CHAR_LEN_BYTE
        const val DESCRIPTION_MAX_LEN = 255 * RDBMS_CHAR_LEN_BYTE
    }

    object MemberRoomConstant {
        const val ROLE_MAX_LEN = 10 * RDBMS_CHAR_LEN_BYTE
        const val DEFAULT_INFINITE_SCROLL_ORDER_BY = "time"
        const val DEFAULT_INFINITE_SCROLL_AFTER = "0"
        const val DEFAULT_INFINITE_SCROLL_DIR = "desc"
        const val ROOMS_INFINITE_SCROLL_SIZE = 10
    }

    object JwtConstant {
        const val ACCESS_TYPE = "access"
        const val REFRESH_TYPE = "refresh"
        const val HTTP_HEADER_AUTHORIZATION = "Authorization"
        const val HTTP_HEADER_AUTH_BEARER = "Bearer "
    }

    object PrayerTitleConstant {
        const val TITLE_MAX_LEN = 50
        const val TITLE_MIN_LEN = 1
        const val TITLE_ENTITY_MAX_LEN = TITLE_MAX_LEN * RDBMS_CHAR_LEN_BYTE
        const val DEFAULT_INFINITE_SCROLL_AFTER = "0"
        const val PRAYER_TITLES_INFINITE_SCROLL_SIZE = 7
    }

    object OtpConstant {
        const val OTP_TTL_MINUTE = 3
        const val REFRESH_TOKEN_TTL_DAYS = 7
    }
}