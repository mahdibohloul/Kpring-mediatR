# Kpring MediatR

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mahdibohloul/kpring-mediatr-starter)](https://search.maven.org/artifact/io.github.mahdibohloul/kpring-mediatr-starter)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-blue.svg)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

A powerful implementation of the [Mediator Pattern](https://en.wikipedia.org/wiki/Mediator_pattern) for the JVM, built
with Kotlin and native coroutine support for the Spring Framework.
Kpring MediatR is heavily inspired by the [MediatR](https://github.com/jbogard/MediatR) project for .NET by Jimmy Bogard.

## 🚀 Features

- **🔄 Asynchronous Operations**: Everything runs asynchronously using Kotlin coroutines
- **📨 Request/Response**: Send requests and receive typed responses
- **⚡ Commands**: Execute commands without expecting return values
- **📢 Notifications**: Publish events handled by multiple subscribers in parallel
- **🛡️ Exception Handling**: Centralized exception handling for notifications
- **🎯 Type Safety**: Full type safety with Kotlin generics
- **🔧 Spring Boot Auto-Configuration**: Zero-configuration setup with automatic bean discovery
- **⚙️ Custom Dispatchers**: Configure coroutine dispatchers per handler
- **🔄 Reactive Support**: Compatible with reactive programming patterns
- **🎛️ Custom Routing**: Advanced custom dispatchers and publishers for complex scenarios

## 📋 Requirements

- **Java**: 21+
- **Kotlin**: 1.9.23+
- **Spring Framework**: 6.2.10+
- **Spring Boot**: 3.5.5+

## 🛠️ Installation

### Maven

```xml

<dependency>
    <groupId>io.github.mahdibohloul</groupId>
    <artifactId>kpring-mediatr-starter</artifactId>
    <version>2.0.2</version>
</dependency>
```

### Gradle

```kotlin
implementation("io.github.mahdibohloul:kpring-mediatr-starter:2.0.2")
```

## ⚙️ Configuration

### Auto-Configuration (Recommended)

Kpring MediatR 2.0.0 includes Spring Boot auto-configuration, so no manual configuration is required!
The library will automatically:

- Discover and register all handlers (`@Component` annotated classes)
- Configure the mediator and factory beans
- Set up notification exception handling based on properties
- Register custom dispatchers and publishers

The auto-configuration is enabled via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
and uses `@ComponentScan` to discover all mediator components.

Simply add the dependency and start using the `Mediator`:

```kotlin
@Service
class OrderService(private val mediator: Mediator) {
  suspend fun createOrder(orderData: OrderData): Order {
    // Use mediator directly - no configuration needed!
    mediator.sendAsync(CreateOrderCommand(orderData))
    return mediator.sendAsync(GetOrderRequest(orderData.id))
  }
}
```

### Manual Configuration (Optional)

If you need custom configuration, you can still override the auto-configuration:

```kotlin
@Configuration
class CustomMediatorConfiguration {

  @Bean
  @Primary
  fun customMediator(
    componentFactory: ComponentFactory,
    notificationCoroutineScope: CoroutineScope
  ): Mediator {
    return DefaultMediator(componentFactory, notificationCoroutineScope)
  }
}
```

### Configuration Properties

Kpring MediatR provides several configuration options via application properties:

#### Exception Handling Configuration

```properties
# application.properties
# Enable/disable notification exception handling (default: true)
mediator.notification.activate-exception-handling=true
```

#### Graceful Shutdown Configuration

```properties
# application.properties
# Timeout for each notification handler during shutdown (default: 30s)
mediator.notification.graceful-shutdown-per-child=30s
# Additional time after cancellation during shutdown (default: 5s)
mediator.notification.graceful-best-effort-after-cancel=5s
```

#### Complete YAML Configuration

```yaml
# application.yml
mediator:
  notification:
    # Enable exception handling for notifications
    activate-exception-handling: true

    # Graceful shutdown configuration
    graceful-shutdown-per-child: 30s
    graceful-best-effort-after-cancel: 5s
```

#### Configuration Reference

| Property                                                  | Type       | Default | Description                                           |
|-----------------------------------------------------------|------------|---------|-------------------------------------------------------|
| `mediator.notification.activate-exception-handling`       | `Boolean`  | `true`  | Enable/disable notification exception handling        |
| `mediator.notification.graceful-shutdown-per-child`       | `Duration` | `30s`   | Timeout for each notification handler during shutdown |
| `mediator.notification.graceful-best-effort-after-cancel` | `Duration` | `5s`    | Additional time after cancellation during shutdown    |

## 📖 Usage Guide

### 1. Request/Response Pattern

Requests are used when you need to send a message and expect a typed response back.
Only one handler can be registered per request type.

#### Define Request

```kotlin
data class GetUserRequest(val userId: String) : Request<User?>
```

#### Implement Handler

```kotlin
@Component
class GetUserRequestHandler(
  private val userRepository: UserRepository
) : RequestHandler<GetUserRequest, User?> {

  override suspend fun handle(request: GetUserRequest): User? {
    return userRepository.findById(request.userId)
  }
}
```

#### Use in Service

```kotlin
@Service
class UserService(private val mediator: Mediator) {

  suspend fun getUser(userId: String): User? {
    return mediator.sendAsync(GetUserRequest(userId))
  }
}
```

### 2. Command Pattern

Commands are used for operations that perform actions without returning values.
Only one handler can be registered per command type.

#### Define Command

```kotlin
data class CreateUserCommand(
  val username: String,
  val email: String,
  val password: String
) : Command
```

#### Implement Handler

```kotlin
@Component
class CreateUserCommandHandler(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) : CommandHandler<CreateUserCommand> {

  override suspend fun handle(command: CreateUserCommand) {
    val user = User(
      username = command.username,
      email = command.email,
      password = passwordEncoder.encode(command.password)
    )
    userRepository.save(user)
  }
}
```

#### Use in Service

```kotlin
@Service
class UserService(private val mediator: Mediator) {

  suspend fun createUser(userData: UserData) {
    mediator.sendAsync(
      CreateUserCommand(
        username = userData.username,
        email = userData.email,
        password = userData.password
      )
    )
  }
}
```

### 3. Notification Pattern

Notifications are used for publishing events that can be handled by multiple subscribers.
Multiple handlers can be registered for the same notification type, and they will all be executed in parallel.

#### Define Notification

```kotlin
data class OrderCreatedNotification(
  val orderId: String,
  val customerId: String,
  val totalAmount: BigDecimal,
  val createdAt: LocalDateTime
) : Notification
```

#### Implement Handlers

```kotlin
@Component
class SendOrderConfirmationEmailHandler(
  private val emailService: EmailService
) : NotificationHandler<OrderCreatedNotification> {

  override suspend fun handle(notification: OrderCreatedNotification) {
    emailService.sendOrderConfirmation(
      customerId = notification.customerId,
      orderId = notification.orderId,
      amount = notification.totalAmount
    )
  }

  override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Component
class UpdateInventoryHandler(
  private val inventoryService: InventoryService
) : NotificationHandler<OrderCreatedNotification> {

  override suspend fun handle(notification: OrderCreatedNotification) {
    inventoryService.reserveItemsForOrder(notification.orderId)
  }

  override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

@Component
class LogOrderCreatedHandler(
  private val logger: Logger
) : NotificationHandler<OrderCreatedNotification> {

  override suspend fun handle(notification: OrderCreatedNotification) {
    logger.info("Order created: ${notification.orderId} for customer ${notification.customerId}")
  }
}
```

#### Use in Service

```kotlin
@Service
class OrderService(private val mediator: Mediator) {

  suspend fun createOrder(orderData: OrderData): Order {
    val order = orderRepository.save(Order(orderData))

    // Publish notification to all handlers
    mediator.publishAsync(
      OrderCreatedNotification(
        orderId = order.id,
        customerId = order.customerId,
        totalAmount = order.totalAmount,
        createdAt = order.createdAt
      )
    )

    return order
  }
}
```

### 4. Notification Exception Handling

When notification exception handling is enabled, you can create handlers to catch and process exceptions that occur in
notification handlers.

#### Enable Exception Handling

```properties
# application.properties
# Enable/disable notification exception handling (default: true)
mediator.notification.activate-exception-handling=true
```

#### Implement Exception Handler

```kotlin
@Component
class EmailServiceExceptionHandler(
  private val logger: Logger,
  private val alertingService: AlertingService
) : NotificationExceptionHandler<OrderCreatedNotification, EmailServiceException> {

  override suspend fun handle(
    notification: OrderCreatedNotification,
    exception: EmailServiceException
  ) {
    logger.error(
      "Failed to send order confirmation email for order ${notification.orderId}",
      exception
    )

    // Send alert to operations team
    alertingService.sendAlert(
      "Email Service Failure",
      "Failed to send order confirmation for order ${notification.orderId}: ${exception.message}"
    )

    // Optionally, queue for retry
    retryQueue.enqueue(notification)
  }

  override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```

## 🎛️ Custom Dispatchers and Publishers

Kpring MediatR supports custom dispatchers and publishers that allow you to implement custom routing logic and
processing before messages reach their handlers.
This is particularly useful for cross-cutting concerns like logging, 
monitoring, caching, or integration with external systems.

### Custom Command Dispatcher

```kotlin
@CustomCommandDispatcher(commandTypes = [SendPushNotificationCommand::class])
class PushNotificationCommandDispatcher(
  private val logger: Logger,
  private val pushNotificationService: PushNotificationService,
) : CommandDispatcher {

  override suspend fun sendAsync(command: Command) {
    command.toMono()
      .filter { command is SendPushNotificationCommand }
      .doOnSuccess {
        if (it == null) {
          logger.warn("Command ${command::class.simpleName} is not a SendPushNotificationCommand")
        }
      }
      .switchIfEmpty {
        Mono.error(DispatcherException.CommandDispatcherException.CommandNotSupportedForDispatchException(command))
      }
      .cast(SendPushNotificationCommand::class.java)
      .doOnNext {
        logger.info("Sending push notification with text ${it.pushRequest.text} and user id ${it.pushRequest.userId}")
      }
      .flatMap { pushNotificationService.push(it.pushRequest) }
      .doOnSuccess { logger.info("Sent push notification successfully") }
      .doOnError { logger.error("Error occurred while sending push notification with command $command", it) }
      .awaitSingleOrNull()
  }
}
```

### Custom Request Dispatcher

```kotlin
@CustomRequestDispatcher(requestTypes = [GetUserRequest::class])
class UserRequestDispatcher(
  private val logger: Logger,
  private val userService: UserService,
  private val cacheService: CacheService,
) : RequestDispatcher {

  override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(
    request: TRequest
  ): TResponse {
    return request.toMono()
      .filter { request is GetUserRequest }
      .cast(GetUserRequest::class.java)
      .flatMap { getUserRequest ->
        // Check cache first
        cacheService.getUser(getUserRequest.userId)
          .switchIfEmpty(
            // If not in cache, fetch from service
            userService.getUser(getUserRequest.userId)
              .doOnNext { user ->
                // Cache the result
                cacheService.cacheUser(user)
              }
          )
      }
      .doOnNext { logger.info("User request processed for ID: ${(request as GetUserRequest).userId}") }
      .doOnError { logger.error("Failed to process user request", it) }
      .awaitSingle() as TResponse
  }
}
```

### Custom Notification Publisher

#### Simple Publisher Example

```kotlin
@CustomNotificationPublisher(notificationTypes = [OrderCreatedNotification::class])
class OrderNotificationPublisher(
  private val logger: Logger,
  private val notificationService: NotificationService,
  private val metricsService: MetricsService,
) : Publisher {

  override suspend fun publishAsync(notification: Notification) {
    notification.toMono()
      .filter { notification is OrderCreatedNotification }
      .cast(OrderCreatedNotification::class.java)
      .doOnNext {
        logger.info("Publishing order notification for order: ${it.orderId}")
        metricsService.incrementOrderNotifications()
      }
      .flatMap { orderNotification ->
        notificationService.publishOrderNotification(orderNotification)
          .doOnSuccess { metricsService.incrementSuccessfulNotifications() }
          .doOnError { metricsService.incrementFailedNotifications() }
      }
      .doOnSuccess { logger.info("Order notification published successfully") }
      .doOnError { logger.error("Failed to publish order notification", it) }
      .awaitSingleOrNull()
  }
}
```

#### Advanced Kafka Publisher Example

```kotlin
@CustomNotificationPublisher(notificationTypes = [KafkaNotification::class, KafkaNotifications::class])
class KafkaPublisher(
  private val kafkaProducerService: KafkaProducerService,
  private val logger: Logger,
) : Publisher {

  override suspend fun publishAsync(notification: Notification) {
    notification.toMono()
      .filter { notification.canStreamOnKafka() }
      .doOnSuccess {
        if (it == null) {
          return@doOnSuccess logger.warn(
            "Notification ${notification::class.simpleName} is not a KafkaNotification or KafkaNotifications"
          )
        }
        return@doOnSuccess logger.info("Publishing Kafka notification with type ${it::class.simpleName}")
      }
      .switchIfEmpty {
        Mono.error(PublisherException.NotSupportedNotificationException(notification))
      }
      .flatMap {
        return@flatMap when (it) {
          is KafkaNotifications<*> -> kafkaProducerService.sends(
            topic = it.topic,
            keyGeneratorClass = it.keyGeneratorClass,
            messages = it.messages,
          )

          is KafkaNotification<*> -> kafkaProducerService.send(
            topic = it.topic,
            keyGeneratorClass = it.keyGeneratorClass,
            message = it.message,
          )

          else -> Mono.error(PublisherException.NotSupportedNotificationException(it))
        }
      }
      .addTraceIdToReactorContext()
      .awaitSingleOrNull()
  }

  private fun Notification.canStreamOnKafka(): Boolean = when (this) {
    is KafkaNotifications<*> -> true
    is KafkaNotification<*> -> true
    else -> false
  }
}
```

### Key Benefits of Custom Dispatchers/Publishers

- **Cross-cutting Concerns**: Implement logging, monitoring, caching, and security
- **External Integrations**: Route messages to external systems or services (Kafka, RabbitMQ, etc.)
- **Custom Logic**: Add business-specific processing before handlers
- **Performance Optimization**: Implement caching, batching, or async processing
- **Error Handling**: Centralized error handling and retry logic
- **Multiple Message Types**: Handle multiple notification/command/request types in a single dispatcher
- **Reactive Streams**: Full integration with reactive programming patterns
- **Tracing and Observability**: Add distributed tracing and context propagation
- **Type Safety**: Maintain type safety while handling multiple message types

## 🔄 Reactive Programming Support

Kpring MediatR is compatible with reactive programming patterns. Add the Kotlinx coroutines reactor dependency:

```kotlin
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
```

### Usage with Reactor

```kotlin
@Service
class ProductService(private val mediator: Mediator) {

  fun reserveProduct(): Mono<Boolean> {
    val request = GetUserRequest("username")
    return mono { mediator.sendAsync(request) }
      .map { user ->
        if (user?.username?.isEmpty() == true) {
          throw CustomerDoesntExistsException()
        }
        return@map user
      }
      .map { true }
  }
}
```

## 🎯 Coroutine Dispatchers

Each notification handler can specify its own coroutine dispatcher for optimal performance:

- **`Dispatchers.IO`**: For I/O operations (database, network, file operations)
- **`Dispatchers.Default`**: For CPU-intensive operations
- **`Dispatchers.Main`**: For UI operations (if applicable)
- **Custom Dispatchers**: For specialized thread pools

```kotlin
@Component
class DatabaseNotificationHandler : NotificationHandler<SomeNotification> {

  override suspend fun handle(notification: SomeNotification) {
    // Database operations
  }

  override fun getCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```

## 🚨 Exception Handling

### Request and Command Handlers

Exceptions thrown in request and command handlers are propagated to the caller:

```kotlin
@Component
class GetUserRequestHandler : RequestHandler<GetUserRequest, User?> {

  override suspend fun handle(request: GetUserRequest): User? {
    return userRepository.findById(request.userId)
      ?: throw UserNotFoundException("User not found: ${request.userId}")
  }
}
```

### Notification Handlers

Exceptions in notification handlers are isolated and don't affect other handlers.
If notification exception handling is enabled, exceptions are caught and passed to appropriate exception handlers.

## 📚 API Reference

### Core Interfaces

- **[Mediator](src/main/kotlin/io/github/mahdibohloul/mediator/Mediator.kt)**: Main interface combining dispatcher and
  publisher functionality
- **[Request](src/main/kotlin/io/github/mahdibohloul/mediator/request/Request.kt)**: Marker interface for requests that
  expect responses
- **[Command](src/main/kotlin/io/github/mahdibohloul/mediator/command/Command.kt)**: Marker interface for commands that
  perform actions
- **[Notification](src/main/kotlin/io/github/mahdibohloul/mediator/notification/Notification.kt)**: Marker interface for
  notifications

### Handler Interfaces

- **[RequestHandler](src/main/kotlin/io/github/mahdibohloul/mediator/request/RequestHandler.kt)**: Interface for
  handling requests
- **[CommandHandler](src/main/kotlin/io/github/mahdibohloul/mediator/command/CommandHandler.kt)**: Interface for
  handling commands
- **[NotificationHandler](src/main/kotlin/io/github/mahdibohloul/mediator/notification/NotificationHandler.kt)**:
  Interface for handling notifications
- **[NotificationExceptionHandler](src/main/kotlin/io/github/mahdibohloul/mediator/notification/NotificationExceptionHandler.kt)**:
  Interface for handling notification exceptions

### Dispatcher Interfaces

- **[RequestDispatcher](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/RequestDispatcher.kt)**: Interface
  for dispatching requests
- **[CommandDispatcher](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/CommandDispatcher.kt)**: Interface
  for dispatching commands
- **[Publisher](src/main/kotlin/io/github/mahdibohloul/mediator/publisher/Publisher.kt)**: Interface for publishing
  notifications

### Custom Dispatcher Annotations

- **[@CustomCommandDispatcher](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/annotations/CustomCommandDispatcher.kt)**: Annotation for custom command dispatchers

- **[@CustomRequestDispatcher](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/annotations/CustomRequestDispatcher.kt)**: Annotation for custom request dispatchers
- **[@CustomNotificationPublisher](src/main/kotlin/io/github/mahdibohloul/mediator/publisher/annotations/CustomNotificationPublisher.kt)**: Annotation for custom notification publishers

### Auto-Configuration Classes

- **[MediatorAutoConfiguration](src/main/kotlin/io/github/mahdibohloul/mediator/autoconfigure/MediatorAutoConfiguration.kt)**: Spring Boot auto-configuration for automatic setup

### Configuration Classes

- **[NotificationProperties](src/main/kotlin/io/github/mahdibohloul/mediator/notification/NotificationProperties.kt)**:
  Configuration properties for notification handling

### Factory Classes

- **[ComponentFactory](src/main/kotlin/io/github/mahdibohloul/mediator/factories/ComponentFactory.kt)**: Factory for
  managing and providing access to mediator handlers
- **[CommandDispatcherFactory](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/factories/CommandDispatcherFactory.kt)**: Factory for discovering and providing custom command dispatchers

- **[RequestDispatcherFactory](src/main/kotlin/io/github/mahdibohloul/mediator/dispatcher/factories/RequestDispatcherFactory.kt)**: Factory for discovering and providing custom request dispatchers

- **[NotificationPublisherFactory](src/main/kotlin/io/github/mahdibohloul/mediator/publisher/factories/NotificationPublisherFactory.kt)**: Factory for discovering and providing custom notification publishers

## 🏗️ Architecture

Kpring MediatR follows a clean architecture pattern:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Application   │───▶│     Mediator     │───▶│    Handlers     │
│     Layer       │    │   (Facade)       │    │   (Business)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │  ComponentFactory │
                       │  (Registration)   │
                       └──────────────────┘
```

## 🧪 Testing

### Unit Testing Handlers

```kotlin
class GetUserRequestHandlerTest {

  @Test
  fun `should return user when found`() = runTest {
    // Given
    val userRepository = mockk<UserRepository>()
    val handler = GetUserRequestHandler(userRepository)
    val request = GetUserRequest("123")
    val expectedUser = User("123", "john", "john@example.com")

    coEvery { userRepository.findById("123") } returns expectedUser

    // When
    val result = handler.handle(request)

    // Then
    assertEquals(expectedUser, result)
  }
}
```

### Integration Testing

```kotlin
@SpringBootTest
class MediatorIntegrationTest {

  @Autowired
  private lateinit var mediator: Mediator

  @Test
  fun `should handle request through mediator`() = runTest {
    // Given
    val request = GetUserRequest("123")

    // When
    val result = mediator.sendAsync(request)

    // Then
    assertNotNull(result)
  }
}
```

## 🤝 Contributing

We welcome contributions!
Please feel free to submit a Pull Request. 
For major changes, please open an issue first to discuss what you would like to change.

### Development Setup

1. Clone the repository
2. Run `./gradlew build` to build the project
3. Run `./gradlew test` to run tests
4. Run `./gradlew detekt` to check code quality

### Code Style

This project uses:

- **Ktlint** for code formatting
- **Detekt** for static analysis
- **Spotless** for code quality checks

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by [MediatR](https://github.com/jbogard/MediatR) by Jimmy Bogard
- Built with ❤️ using Kotlin and Spring Framework

## 📞 Support

If you have any questions or need help, please:

- Open an issue on GitHub
- Check the documentation
- Review the examples in the test directory

---

**Made with ❤️ for the Kotlin and Spring communities**
