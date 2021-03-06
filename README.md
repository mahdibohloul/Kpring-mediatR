# Kpring MediatR [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

In this project, an attempt has been made to implement
the [mediator pattern](https://en.wikipedia.org/wiki/Mediator_pattern#:~:text=In%20software%20engineering%2C%20the%20mediator,a%20set%20of%20objects%20interact.&text=Objects%20no%20longer%20communicate%20directly,communicating%20objects%2C%20thereby%20reducing%20coupling.)
on the JVM with simplicity using Kotlin with native coroutine support for the Spring Framework and is heavily inspired
by the [MediatR](https://github.com/jbogard/MediatR) project for .NET by Jimmy Bogard.

**Everything's going asynchronously with using Kotlin coroutine features.**

Kotlin MediatR can be used in both **reactive-programming** style and **Kotlin native coroutine**. is also intended to
help developers be more focused on their code and write cleaner and decoupled components

## Requirements

* Java 8 +
* Spring Framework 5 / Spring Boot 2*

## Setup and Configuration

> #### To get the Kpring-MediatR, you can use [Maven Central Repositories](https://search.maven.org/artifact/io.github.mahdibohloul/kpring-mediatr-starter). To use Maven Central, do the following:

##### Maven
```maven
<dependency>
  <groupId>io.github.mahdibohloul</groupId>
  <artifactId>kpring-mediatr-starter</artifactId>
  <version>1.1.0</version>
</dependency>
```
##### Gradle
```gradle
implementation("io.github.mahdibohloul:kpring-mediatr-starter:1.1.0")
```

To set up and config Kpring mediator in your project add the bean to your project as follows:

```kotlin
@Configuration
class MediatorConfiguration(
    @Autowired val applicationContext: ApplicationContext
) {

    @Bean
    fun factory(): Factory {
        return DefaultFactory(applicationContext)
    }

    @Bean
    fun mediator(factory: Factory): Mediator {
        return DefaultMediator(factory)
    }
}
```

## Features

**Kpring Mediator** offers four types of features:

#### Request:

You send a request to anyone who can handle the request, and the handler prepares the response you want.
***Note*** that there can only be one request handler per request.

#### Command:

Like [Request](#Request:), you send a command to anyone who can handle the command, except that you will not receive a response. Also, there can only be one command handler per command.

#### Notification:

You publish a notification and the relevant handlers receive the notification and do something about it. It is useful when something special is happening in your system, and you want to do separate tasks in parallel afterwards. For example, when a shipment is cancelled, you may want to send an email to the order owner and the driver and vendor of the shipment that is not related to your main process, to do this you can publish a notification.
Also, you can specify a coroutine dispatcher to run the notification handler on. it will become handy when you have many notification handlers, and you want to run some of them on another thread.

#### Notification Exception Handler:

Catch thrown exceptions in your notification handlers and handle them in a special way. For example, you may want to log the exception and email to the system administrator.
You can also specify a coroutine dispatcher to run the notification exception handler on.
To enable this feature, you need to add the following method to your factory configuration:
```kotlin
@Bean
fun factory(): Factory {
    return DefaultFactory(applicationContext).enableNotificationExceptionHandling()
}
```

## Usage

> ###### Do not forget to use the **component** annotation with your handler. it helps the mediator to register your handlers.

#### Request usage

```kotlin
class GetCustomerByUsernameRequest(val username: String?) : Request<Customer?>
```

```kotlin
@Component
class GetCustomerByUsernameRequestHandler(
    @Autowired val customerRepository: CustomerRepository
) : RequestHandler<GetCustomerByUsernameRequest, Customer?> {
    override suspend fun handle(request: GetCustomerByUsernameRequest): Customer? {
        return customerRepository.findFirstByUsername(request.username).awaitFirstOrNull()
    }
}
```

#### Command usage

```kotlin
class CancelOrderCommand(val order: Order) : Command
```

```kotlin
@Component
class CancelOrderCommandHandler : CommandHandler<CancelOrderCommand> {
    override suspend fun handle(command: CancelOrderCommand) {
        //Cancellation process
    }
}
```

#### Notification usage

```kotlin
class OrderCancelledNotification : Notification
```

```kotlin
@Component
class OrderCancelledNotificationHandler : NotificationHandler<OrderCancelledNotification> {
    override suspend fun handle(notification: OrderCancelledNotification) {
        //Do something
    }
    
    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        //Specify dispatcher here
    }
}

@Component
class SendEmailToVendorWhenOrderCancelledNotificationHandler : NotificationHandler<OrderCancelledNotification> {
    override suspend fun handle(notification: OrderCancelledNotification) {
        //Send email to vendor
    }
    
    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
```

#### Notification Exception Handler usage
```kotlin
@Component
class OrderCancellationSendingEmailExceptionHandler: NotificationExceptionHandler<OrderCancellationNotification, EmailServiceException> {
    override suspend fun handle(notification: OrderCancellationNotification, exception: EmailServiceException) {
        //Handle exception
    }
    
    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
```

### Use in reactive style

Add the [Kotlinx coroutines reactor](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-reactor/index.html) to your project.

```kotlin
@Service
class ProductService(
    @Autowired val mediator: Mediator
) {
    fun reserveProduct(): Mono<Boolean> {
        val request = GetCustomerByUsernameRequest("username")
        mono { mediator.sendAsync(request) }.map {
            if (it?.username?.isEmpty()!!)
                throw CustomerDoesntExistsException()
            return@map it
        } //DO SOMETHING
    }
}
```

## Exceptions

There are 5 different exceptions: **NoRequestHandlerException**, **DuplicateRequestHandlerRegistrationException**, **NoNotificationHandlersException**, **NoCommandHandlerException**, **DuplicateCommandHandlerRegistrationException**, all of each are inherited from **KpringMediatorException**.

Exceptions that occur in *request handler*s and *command handler*s were propagated in the parent and canceled the process, but if an exception occurs in one of the *notification handler*s, it is ignored if there is no *notification exception handler*s found and the other *notification handler*s continue to operate.

## Contribution
***If you can improve this project, do not hesitate to contribute with me. I'm waiting for your merge requests with open arms.***

