package io.github.mahdibohloul.mediator.publisher.annotations

import io.github.mahdibohloul.mediator.notification.Notification
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Annotation used to mark a class as a custom notification publisher in a Spring-based application.
 *
 * A component annotated with this annotation will be registered as a Spring-managed bean
 * and will serve as a publisher for specific types of notifications. This allows for custom
 * routing logic and processing before notifications reach their handlers.
 *
 * ## Key Features:
 * - **Custom Routing**: Implement custom logic for notification processing
 * - **Reactive Support**: Full support for reactive streams and coroutines
 * - **Type Safety**: Strongly typed notification handling
 * - **Spring Integration**: Automatically discovered and registered by Spring
 *
 * ## Usage Examples:
 *
 * ### Simple Publisher
 * ```kotlin
 * @CustomNotificationPublisher(notificationTypes = [OrderCreatedNotification::class])
 * class OrderNotificationPublisher(
 *     private val logger: Logger,
 *     private val notificationService: NotificationService,
 * ) : Publisher {
 *
 *     override suspend fun publishAsync(notification: Notification) {
 *         notification.toMono()
 *             .filter { notification is OrderCreatedNotification }
 *             .cast(OrderCreatedNotification::class.java)
 *             .doOnNext {
 *                 logger.info("Publishing order notification for order: ${it.orderId}")
 *             }
 *             .flatMap { notificationService.publishOrderNotification(it) }
 *             .doOnSuccess { logger.info("Order notification published successfully") }
 *             .doOnError { logger.error("Failed to publish order notification", it) }
 *             .awaitSingleOrNull()
 *     }
 * }
 * ```
 *
 * ### Advanced Kafka Publisher with Multiple Notification Types
 * ```kotlin
 * @CustomNotificationPublisher(notificationTypes = [KafkaNotification::class, KafkaNotifications::class])
 * class KafkaPublisher(
 *     private val kafkaProducerService: KafkaProducerService,
 *     private val logger: Logger,
 * ) : Publisher {
 *
 *     override suspend fun publishAsync(notification: Notification) {
 *         notification.toMono()
 *             .filter { notification.canStreamOnKafka() }
 *             .doOnSuccess {
 *                 if (it == null) {
 *                     return@doOnSuccess logger.warn(
 *                         "Notification ${notification::class.simpleName} is" +
 *                         "not a KafkaNotification or KafkaNotifications"
 *                     )
 *                 }
 *                 return@doOnSuccess logger.info("Publishing Kafka notification with type ${it::class.simpleName}")
 *             }
 *             .switchIfEmpty {
 *                 Mono.error(PublisherException.NotSupportedNotificationException(notification))
 *             }
 *             .flatMap {
 *                 return@flatMap when (it) {
 *                     is KafkaNotifications<*> -> kafkaProducerService.sends(
 *                         topic = it.topic,
 *                         keyGeneratorClass = it.keyGeneratorClass,
 *                         messages = it.messages,
 *                     )
 *
 *                     is KafkaNotification<*> -> kafkaProducerService.send(
 *                         topic = it.topic,
 *                         keyGeneratorClass = it.keyGeneratorClass,
 *                         message = it.message,
 *                     )
 *
 *                     else -> Mono.error(PublisherException.NotSupportedNotificationException(it))
 *                 }
 *             }
 *             .addTraceIdToReactorContext()
 *             .awaitSingleOrNull()
 *     }
 *
 *     private fun Notification.canStreamOnKafka(): Boolean = when (this) {
 *         is KafkaNotifications<*> -> true
 *         is KafkaNotification<*> -> true
 *         else -> false
 *     }
 * }
 * ```
 *
 * @property value An optional value to customize the Spring bean name for the component
 * @property notificationTypes Specifies the array of notification types handled by the publisher
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Publisher for the publisher interface
 * @see Notification for the notification interface
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
