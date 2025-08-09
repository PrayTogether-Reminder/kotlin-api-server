package site.praytogether.praytogetherapi.test_config

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class IntegrateTestConfig {

    @Bean
    @Primary
    fun mockFirebaseApp(): FirebaseApp {
        return Mockito.mock(FirebaseApp::class.java)
    }

    @Bean
    @Primary
    fun mockFirebaseMessaging(): FirebaseMessaging {
        return Mockito.mock(FirebaseMessaging::class.java)
    }
}