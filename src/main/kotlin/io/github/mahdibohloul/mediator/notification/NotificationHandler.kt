package io.github.mahdibohloul.mediator.notification

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Interface for handling notifications that can be processed by multiple handlers.
 *
 * NotificationHandler is responsible for processing a specific type of [Notification]
 * and executing the associated business logic. Multiple handlers can be registered
 * for the same notification type, and they will all be executed in parallel when
 * the notification is published.
 *
 * ## Key Characteristics:
 * - **Multiple Handlers**: Multiple handlers can be registered for the same notification type
 * - **Asynchronous**: Uses suspend functions for non-blocking operations
 * - **Parallel Execution**: All handlers are executed concurrently using coroutines
 * - **Exception Isolation**: Exceptions in one handler don't affect others
 * - **Custom Dispatchers**: Each handler can specify its own coroutine dispatcher
 * - **Spring Integration**: Should be annotated with @Component for auto-discovery
 *
 * ## Implementation Guidelines:
 * - Keep handlers focused on a single responsibility
 * - Use dependency injection for required services
 * - Handle exceptions appropriately (consider using [NotificationExceptionHandler])
 * - Choose appropriate coroutine dispatchers for I/O vs CPU-bound operations
 * - Make handlers idempotent when possible
 *
 * ## Usage Example:
 * ```kotlin
 * @Component
 * class SendEmailNotificationHandler(
 *     private val emailService: EmailService
 * ) : NotificationHandler<OrderCreatedNotification> {
 *
 *     override suspend fun handle(notification: OrderCreatedNotification) {
 *         emailService.sendOrderConfirmation(
 *             customerId = notification.customerId,
 *             orderId = notification.orderId,
 *             amount = notification.totalAmount
 *         )
 *     }
 *
 *     override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
 * }
 *
 * @Component
 * class UpdateInventoryNotificationHandler(
 *     private val inventoryService: InventoryService
 * ) : NotificationHandler<OrderCreatedNotification> {
 *
 *     override suspend fun handle(notification: OrderCreatedNotification) {
 *         inventoryService.reserveItemsForOrder(notification.orderId)
 *     }
 *
 *     override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
 * }
 * ```
 *
 * @param TNotification The type of notification this handler processes
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Notification for the notification interface
 * @see NotificationExceptionHandler for handling exceptions in notification handlers
 * @see Mediator.publishAsync for publishing notifications
 */
interface NotificationHandler<in TNotification : Notification> {

  /**
   * Handles the specified notification and executes the associated business logic.
   *
   * This method is called by the mediator when a notification of type [TNotification] is published.
   * The implementation should process the notification and perform the necessary actions.
   *
   * @param notification The notification to handle
   * @throws Exception Exceptions are isolated per handler and don't affect other handlers
   * @author Mahdi Bohloul
   */
  suspend fun handle(notification: TNotification)

  /**
   * Specifies the coroutine dispatcher to be used for handling the notification.
   *
   * This method allows each handler to specify which coroutine dispatcher should be used
   * for executing the notification handling logic. This is useful for optimizing performance
   * by using appropriate dispatchers for different types of operations.
   *
   * ## Dispatcher Guidelines:
   * - **Dispatchers.IO**: For I/O operations (database, network, file operations)
   * - **Dispatchers.Default**: For CPU-intensive operations
   * - **Dispatchers.Main**: For UI operations (if applicable)
   * - **Custom Dispatchers**: For specialized thread pools
   *
   * @return The coroutine dispatcher to use for this handler
   * @default Dispatchers.Default
   * @author Mahdi Bohloul
   */
  fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
