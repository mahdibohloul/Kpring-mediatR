package io.github.mahdibohloul.mediator.notification

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Configuration properties for notification handling.
 */
@ConfigurationProperties(prefix = "mediator.notification")
@Suppress("detekt.MagicNumber")
data class NotificationProperties(
  val gracefulShutdownPerChild: Duration = Duration.ofSeconds(30),
  val gracefulBestEffortAfterCancel: Duration = Duration.ofSeconds(5),
)
