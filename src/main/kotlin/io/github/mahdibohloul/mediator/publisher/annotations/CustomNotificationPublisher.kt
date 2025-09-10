package io.github.mahdibohloul.mediator.publisher.annotations

import io.github.mahdibohloul.mediator.notification.Notification
import kotlin.reflect.KClass
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

/**
 * This annotation will be used to annotate a component that will be used to publish notifications.
 * The publisher factory will be searching for this annotation in the Spring Context
 * And match the notification type with the annotated class.
 *
 * @see Notification
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CustomNotificationPublisher(
  @get:AliasFor(annotation = Component::class)
  val value: String = "",
  val notificationTypes: Array<KClass<out Notification>>,
)
