package io.github.mahdibohloul.mediator.publisher.factories

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.publisher.Publisher
import io.github.mahdibohloul.mediator.publisher.annotations.CustomNotificationPublisher
import kotlin.reflect.KClass
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component

@Component
class NotificationPublisherFactory(
  private val applicationContext: ApplicationContext,
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  private val publishers: Map<KClass<out Notification>, Publisher> by lazy {
    logger.info("Discovering notification publishers")
    applicationContext.getBeansWithAnnotation(CustomNotificationPublisher::class.java)
      .map { it.value as Publisher }.flatMap { publisher ->
        findNotificationType(publisher).map { notificationClass ->
          (notificationClass to publisher)
            .also {
              logger.info("Registered ${publisher::class.simpleName} for notification ${it.first.simpleName}")
            }
        }
      }.toMap()
      .also { logger.info("Discovered ${it.size} notification publishers") }
  }

  fun getNotificationPublisher(notification: Notification): Publisher? =
    publishers.filterKeys { it.isInstance(notification) }.values.singleOrNull()?.also {
      logger.info("Found publisher for notification ${notification::class.simpleName}")
    }

  private fun findNotificationType(publisher: Publisher): Array<KClass<out Notification>> {
    val annotation = AnnotationUtils.findAnnotation(publisher::class.java, CustomNotificationPublisher::class.java)
    requireNotNull(annotation) {
      "Publisher ${publisher::class.simpleName} is not annotated with CustomNotificationPublisher"
    }
    return annotation.notificationTypes
  }
}
