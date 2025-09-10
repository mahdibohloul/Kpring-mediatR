package io.github.mahdibohloul.mediator.notification

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Interface for handling exceptions that occur during notification processing.
 *
 * NotificationExceptionHandler provides a mechanism to handle exceptions that are thrown
 * by [NotificationHandler] implementations. This allows for centralized exception handling
 * and recovery strategies for notification processing failures.
 *
 * ## Key Characteristics:
 * - **Exception Isolation**: Exceptions in notification handlers don't affect other handlers
 * - **Centralized Handling**: Provides a single place to handle specific exception types
 * - **Asynchronous**: Uses suspend functions for non-blocking operations
 * - **Custom Dispatchers**: Each handler can specify its own coroutine dispatcher
 * - **Spring Integration**: Should be annotated with @Component for auto-discovery
 *
 * ## Implementation Guidelines:
 * - Handle specific exception types for targeted error handling
 * - Consider logging, alerting, or retry mechanisms
 * - Use appropriate coroutine dispatchers for error handling operations
 * - Keep exception handlers focused and lightweight
 * - Consider dead letter queues for failed notifications
 *
 * ## Usage Example:
 * ```kotlin
 * @Component
 * class EmailServiceExceptionHandler(
 *     private val logger: Logger,
 *     private val alertingService: AlertingService
 * ) : NotificationExceptionHandler<OrderCreatedNotification, EmailServiceException> {
 *
 *     override suspend fun handle(notification: OrderCreatedNotification, exception: EmailServiceException) {
 *         logger.error(
 *             "Failed to send order confirmation email for order ${notification.orderId}",
 *             exception
 *         )
 *
 *         // Send alert to operations team
 *         alertingService.sendAlert(
 *             "Email Service Failure",
 *             "Failed to send order confirmation for order ${notification.orderId}: ${exception.message}"
 *         )
 *
 *         // Optionally, queue for retry
 *         retryQueue.enqueue(notification)
 *     }
 *
 *     override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
 * }
 * ```
 *
 * @param TNotification The type of notification this handler processes
 * @param TException The type of exception this handler catches
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see NotificationHandler for handling notifications
 * @see Notification for the notification interface
 * @see Mediator.publishAsync for publishing notifications
 */
interface NotificationExceptionHandler<
  in TNotification : Notification,
  in TException : Exception,
  > {

  /**
   * Handles the specified exception that occurred during notification processing.
   *
   * This method is called by the mediator when an exception of type [TException] is thrown
   * by a [NotificationHandler] while processing a notification of type [TNotification].
   *
   * @param notification The notification that was being processed when the exception occurred
   * @param exception The exception that was thrown during notification processing
   * @author Mahdi Bohloul
   */
  suspend fun handle(notification: TNotification, exception: TException)

  /**
   * Specifies the coroutine dispatcher to be used for handling the exception.
   *
   * This method allows each exception handler to specify which coroutine dispatcher should be used
   * for executing the exception handling logic. This is useful for optimizing performance
   * by using appropriate dispatchers for different types of error handling operations.
   *
   * ## Dispatcher Guidelines:
   * - **Dispatchers.IO**: For I/O operations (logging, alerting, database operations)
   * - **Dispatchers.Default**: For CPU-intensive error processing
   * - **Custom Dispatchers**: For specialized error handling thread pools
   *
   * @return The coroutine dispatcher to use for this exception handler
   * @default Dispatchers.Default
   * @author Mahdi Bohloul
   */
  fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
