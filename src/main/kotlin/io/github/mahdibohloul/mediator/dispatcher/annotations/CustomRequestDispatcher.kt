package io.github.mahdibohloul.mediator.dispatcher.annotations

import io.github.mahdibohloul.mediator.request.Request
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * Annotation used to mark a class as a custom request dispatcher in a Spring-based application.
 *
 * A component annotated with this annotation will be registered as a Spring-managed bean
 * and will serve as a dispatcher for specific types of requests. This allows for custom
 * routing logic and processing before requests reach their handlers.
 *
 * ## Key Features:
 * - **Custom Routing**: Implement custom logic for request processing
 * - **Reactive Support**: Full support for reactive streams and coroutines
 * - **Type Safety**: Strongly typed request and response handling
 * - **Spring Integration**: Automatically discovered and registered by Spring
 *
 * ## Usage Example:
 * ```kotlin
 * @CustomRequestDispatcher(requestTypes = [GetUserRequest::class])
 * class UserRequestDispatcher(
 *     private val logger: Logger,
 *     private val userService: UserService,
 * ) : RequestDispatcher {
 *
 *     override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(
 *         request: TRequest
 *     ): TResponse {
 *         return request.toMono()
 *             .filter { request is GetUserRequest }
 *             .cast(GetUserRequest::class.java)
 *             .doOnNext {
 *                 logger.info("Processing user request for ID: ${it.userId}")
 *             }
 *             .flatMap { userService.getUser(it.userId) }
 *             .doOnSuccess { logger.info("User request processed successfully") }
 *             .doOnError { logger.error("Failed to process user request", it) }
 *             .awaitSingle() as TResponse
 *     }
 * }
 * ```
 *
 * @property value An optional value to customize the Spring bean name for the component
 * @property requestTypes Specifies the array of request types handled by the dispatcher
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see RequestDispatcher for the dispatcher interface
 * @see Request for the request interface
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CustomRequestDispatcher(
  @get:AliasFor(annotation = Component::class)
  val value: String = "",
  val requestTypes: Array<KClass<out Request<*>>>,
)
