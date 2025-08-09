package site.praytogether.praytogetherapi.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.IOException

@Configuration
@org.springframework.context.annotation.Profile("!test & !h2")
class FirebaseConfig {

    @Value("\${firebase.sdk.path}")
    private lateinit var firebaseSdkPath: String

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        return try {
            val serviceAccount = FileInputStream(firebaseSdkPath)
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            val app = if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            } else {
                FirebaseApp.getInstance()
            }

            FirebaseMessaging.getInstance(app)
        } catch (e: IOException) {
            throw RuntimeException("Failed to initialize Firebase", e)
        }
    }
}