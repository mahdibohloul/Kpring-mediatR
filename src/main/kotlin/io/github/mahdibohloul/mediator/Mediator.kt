package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.dispatcher.CommandDispatcher
import io.github.mahdibohloul.mediator.dispatcher.RequestDispatcher
import io.github.mahdibohloul.mediator.publisher.Publisher

/**
 * The main interface for the Mediator pattern implementation in Kpring MediatR.
 *
 * The Mediator interface combines the functionality of [RequestDispatcher], [CommandDispatcher], and [Publisher]
 * to provide a unified API for handling requests, commands, and notifications in a decoupled manner.
 *
 * This interface serves as the central communication hub that allows objects to interact
 * without direct dependencies, promoting loose coupling and better separation of concerns.
 *
 * ## Key Features:
 * - **Request/Response**: Send requests and receive typed responses
 * - **Commands**: Execute commands without expecting return values
 * - **Notifications**: Publish events that can be handled by multiple subscribers
 * - **Asynchronous**: All operations are suspend functions supporting Kotlin coroutines
 *
 * ## Usage Example:
 * ```kotlin
 * @Service
 * class OrderService(private val mediator: Mediator) {
 *     suspend fun processOrder(orderId: String): OrderResult {
 *         // Send a request and get a response
 *         val order = mediator.sendAsync(GetOrderRequest(orderId))
 *
 *         // Execute a command
 *         mediator.sendAsync(ProcessOrderCommand(order))
 *
 *         // Publish a notification
 *         mediator.publishAsync(OrderProcessedNotification(order))
 *
 *         return OrderResult.Success
 *     }
 * }
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see RequestDispatcher for request handling
 * @see CommandDispatcher for command handling
 * @see Publisher for notification publishing
 */
interface Mediator :
  RequestDispatcher,
  CommandDispatcher,
  Publisher
