package io.github.mahdibohloul.mediator.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.ComponentScan

/**
 * Auto-configuration class for Kpring MediatR.
 *
 * This class provides automatic configuration for the Kpring MediatR library in Spring Boot applications.
 * It automatically discovers and registers all mediator-related components without requiring manual configuration.
 *
 * ## What's Auto-Configured:
 * - **Mediator Bean**: Main mediator interface implementation
 * - **ComponentFactory**: Handler discovery and registration
 * - **Custom Dispatchers**: All custom command and request dispatchers
 * - **Custom Publishers**: All custom notification publishers
 * - **Notification Configuration**: Coroutine scope and exception handling setup
 *
 * ## Usage:
 * Simply add the Kpring MediatR dependency to your Spring Boot project and start using the `Mediator`:
 *
 * ```kotlin
 * @Service
 * class OrderService(private val mediator: Mediator) {
 *     suspend fun createOrder(orderData: OrderData) {
 *         // No configuration needed - everything is auto-configured!
 *         mediator.sendAsync(CreateOrderCommand(orderData))
 *     }
 * }
 * ```
 *
 * ## Configuration Properties:
 * - `mediator.notification.activate-exception-handling`: Enable/disable notification exception handling
 * - `mediator.notification.graceful-shutdown-per-child`: Timeout for each notification handler during shutdown
 * - `mediator.notification.graceful-best-effort-after-cancel`: Additional time after cancellation during shutdown
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Mediator for the main mediator interface
 * @see ComponentFactory for handler management
 */
@AutoConfiguration
@ComponentScan("io.github.mahdibohloul.mediator")
class MediatorAutoConfiguration
