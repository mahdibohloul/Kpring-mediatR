# Kpring MediatR [![GradleBuild](https://github.com/mahdibohloul/spring-reactive-mediatR/actions/workflows/build.yml/badge.svg)](https://github.com/mahdibohloul/spring-reactive-mediatR/actions/workflows/build.yml) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

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

## Getting Started

## Setup and Configuration

## Usage

### Use in reactive style

```
<dependency>
     <groupId>org.jetbrains.kotlinx</groupId>
     <artifactId>kotlinx-coroutines-reactor</artifactId>
</dependency>
```

**or**

```
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
```
**Implementation:**
```kotlin
class GetCustomerByUsernameRequest(val username: String?) : Request<Customer?>

@Component
class GetCustomerByUsernameRequestHandler(
    @Autowired val customerRepository: CustomerRepository
): RequestHandler<GetCustomerByUsernameRequest, Customer?> {
    override suspend fun handle(request: GetCustomerByUsernameRequest): Customer? {
        return customerRepository.findFirstByUsername(request.username).awaitFirstOrNull()
    }
}
```

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
