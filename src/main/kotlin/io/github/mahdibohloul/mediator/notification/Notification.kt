package io.github.mahdibohloul.mediator.notification

/**
 * Marker interface for notifications that can be published to multiple handlers.
 *
 * The Notification interface represents an event or message that is published
 * through the mediator and can be handled by multiple subscribers. This follows
 * the Publisher-Subscriber pattern where multiple handlers can react to the same
 * notification independently.
 *
 * ## Key Characteristics:
 * - **Multiple Handlers**: Multiple handlers can be registered for the same notification type
 * - **Fire and Forget**: Notifications are published asynchronously
 * - **Parallel Processing**: All handlers are executed in parallel using coroutines
 * - **Exception Isolation**: Exceptions in one handler don't affect others
 * - **Custom Dispatchers**: Each handler can specify its own coroutine dispatcher
 *
 * ## Usage Example:
 * ```kotlin
 * data class OrderCreatedNotification(
 *     val orderId: String,
 *     val customerId: String,
 *     val totalAmount: BigDecimal,
 *     val createdAt: LocalDateTime
 * ) : Notification
 *
 * @Component
 * class SendOrderConfirmationEmailHandler : NotificationHandler<OrderCreatedNotification> {
 *     override suspend fun handle(notification: OrderCreatedNotification) {
 *         emailService.sendOrderConfirmation(notification.customerId, notification.orderId)
 *     }
 *
 *     override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
 * }
 *
 * @Component
 * class UpdateInventoryHandler : NotificationHandler<OrderCreatedNotification> {
 *     override suspend fun handle(notification: OrderCreatedNotification) {
 *         inventoryService.reserveItems(notification.orderId)
 *     }
 * }
 *
 * // Usage in service
 * mediator.publishAsync(OrderCreatedNotification("123", "456", BigDecimal("99.99"), LocalDateTime.now()))
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see NotificationHandler for handling notifications
 * @see NotificationExceptionHandler for handling exceptions in notification handlers
 * @see Mediator.publishAsync for publishing notifications
 */
interface Notification
