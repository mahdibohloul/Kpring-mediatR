package io.github.mahdibohloul.mediator.dispatcher.annotations

import io.github.mahdibohloul.mediator.command.Command
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Annotation used to mark a class as a custom command dispatcher in a Spring-based application.
 *
 * A component annotated with this annotation will be registered as a Spring-managed bean
 * and will serve as a dispatcher for specific types of commands. This allows for custom
 * routing logic and processing before commands reach their handlers.
 *
 * ## Key Features:
 * - **Custom Routing**: Implement custom logic for command processing
 * - **Reactive Support**: Full support for reactive streams and coroutines
 * - **Type Safety**: Strongly typed command handling
 * - **Spring Integration**: Automatically discovered and registered by Spring
 *
 * ## Usage Example:
 * ```kotlin
 * @CustomCommandDispatcher(commandTypes = [SendPushNotificationCommand::class])
 * class PushNotificationCommandDispatcher(
 *     private val logger: Logger,
 *     private val pushNotificationService: PushNotificationService,
 * ) : CommandDispatcher {
 *
 *     override suspend fun sendAsync(command: Command) {
 *         command.toMono()
 *             .filter { command is SendPushNotificationCommand }
 *             .cast(SendPushNotificationCommand::class.java)
 *             .doOnNext {
 *                 logger.info("Sending push notification: ${it.pushRequest.text}")
 *             }
 *             .flatMap { pushNotificationService.push(it.pushRequest) }
 *             .doOnSuccess { logger.info("Push notification sent successfully") }
 *             .doOnError { logger.error("Failed to send push notification", it) }
 *             .awaitSingleOrNull()
 *     }
 * }
 * ```
 *
 * @property value An optional value to customize the Spring bean name for the component
 * @property commandTypes Specifies the array of command types handled by the dispatcher
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see CommandDispatcher for the dispatcher interface
 * @see Command for the command interface
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CustomCommandDispatcher(
  @get:AliasFor(annotation = Component::class)
  val value: String = "",
  val commandTypes: Array<KClass<out Command>>,
)
