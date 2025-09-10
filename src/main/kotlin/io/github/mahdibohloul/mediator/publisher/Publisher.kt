package io.github.mahdibohloul.mediator.publisher

import io.github.mahdibohloul.mediator.notification.Notification

/**
 * Interface for publishing notifications through the mediator pattern.
 *
 * Publisher provides the functionality to publish notifications that can be handled
 * by multiple subscribers. This follows the Publisher-Subscriber pattern where
 * multiple handlers can react to the same notification independently and in parallel.
 *
 * ## Key Characteristics:
 * - **Multiple Handlers**: Multiple handlers can be registered for the same notification type
 * - **Parallel Processing**: All handlers are executed concurrently using coroutines
 * - **Exception Isolation**: Exceptions in one handler don't affect others
 * - **Fire and Forget**: Notifications are published asynchronously
 * - **Custom Dispatchers**: Each handler can specify its own coroutine dispatcher
 *
 * ## Usage Example:
 * ```kotlin
 * class OrderService(private val publisher: Publisher) {
 *     suspend fun createOrder(orderData: OrderData): Order {
 *         val order = orderRepository.save(Order(orderData))
 *
 *         // Publish notification to multiple handlers
 *         publisher.publishAsync(OrderCreatedNotification(
 *             orderId = order.id,
 *             customerId = order.customerId,
 *             totalAmount = order.totalAmount,
 *             createdAt = order.createdAt
 *         ))
 *
 *         return order
 *     }
 * }
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Notification for the notification interface
 * @see NotificationHandler for handling notifications
 * @see NotificationExceptionHandler for handling exceptions in notification handlers
 * @see Mediator for the main mediator interface
 */
interface Publisher {

  /**
   * Publishes a notification asynchronously to all registered handlers.
   *
   * This method publishes the specified notification to all registered handlers
   * of the notification type. All handlers are executed in parallel using coroutines,
   * and exceptions in individual handlers are isolated and don't affect other handlers.
   *
   * @param notification The notification to publish
   * @throws NoNotificationHandlersException if no handlers are registered for the notification type
   * @author Mahdi Bohloul
   */
  suspend fun publishAsync(notification: Notification)
}
