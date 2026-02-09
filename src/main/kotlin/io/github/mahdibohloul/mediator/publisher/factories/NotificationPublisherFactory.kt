package io.github.mahdibohloul.mediator.publisher.factories

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.publisher.Publisher
import io.github.mahdibohloul.mediator.publisher.annotations.CustomNotificationPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class NotificationPublisherFactory(
  private val applicationContext: ApplicationContext,
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  private val publishers: Map<KClass<out Notification>, Publisher> by lazy {
    logger.debug("Discovering notification publishers")
    applicationContext.getBeansWithAnnotation<CustomNotificationPublisher>()
      .map { it.value as Publisher }.flatMap { publisher ->
        findNotificationType(publisher).map { notificationClass ->
          (notificationClass to publisher)
            .also {
              logger.debug("Registered ${publisher::class.simpleName} for notification ${it.first.simpleName}")
            }
        }
      }.toMap()
      .also { logger.debug("Discovered ${it.size} notification publishers") }
  }

  fun getNotificationPublisher(notification: Notification): Publisher? = publishers
    .filterKeys { it.isInstance(notification) }
    .values
    .singleOrNull()
    ?.also { logger.debug("Found publisher for notification ${notification::class.simpleName}") }

  private fun findNotificationType(publisher: Publisher): Array<KClass<out Notification>> {
    val annotation = AnnotationUtils.findAnnotation(publisher::class.java, CustomNotificationPublisher::class.java)
    requireNotNull(annotation) {
      "Publisher ${publisher::class.simpleName} is not annotated with CustomNotificationPublisher"
    }
    return annotation.notificationTypes
  }
}
